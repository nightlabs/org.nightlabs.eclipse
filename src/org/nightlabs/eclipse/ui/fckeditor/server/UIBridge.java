/* ************************************************************************
 * org.nightlabs.eclipse.ui.fckeditor - Eclipse RCP FCKeditor Integration *
 * Copyright (C) 2008 NightLabs - http://NightLabs.org                    *
 *                                                                        *
 * This library is free software; you can redistribute it and/or          *
 * modify it under the terms of the GNU Lesser General Public             *
 * License as published by the Free Software Foundation; either           *
 * version 2.1 of the License, or (at your option) any later version.     *
 *                                                                        *
 * This library is distributed in the hope that it will be useful,        *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of         *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU      *
 * Lesser General Public License for more details.                        *
 *                                                                        *
 * You should have received a copy of the GNU Lesser General Public       *
 * License along with this library; if not, write to the                  *
 *     Free Software Foundation, Inc.,                                    *
 *     51 Franklin St, Fifth Floor,                                       *
 *     Boston, MA  02110-1301  USA                                        *
 *                                                                        *
 * Or get it online:                                                      *
 *     http://www.gnu.org/copyleft/lesser.html                            *
 **************************************************************************/
package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContent;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;
import org.nightlabs.eclipse.ui.fckeditor.file.ContentTypeUtil;
import org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard;
import org.nightlabs.eclipse.ui.fckeditor.file.SelectFileDialog;
import org.nightlabs.eclipse.ui.fckeditor.resource.Messages;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class UIBridge extends AbstractFileProvider {

	public UIBridge(IFCKEditor editor) {
		super(editor);
	}

	private static InputStream getStreamForString(String contents)
	{
		try {
			return new ByteArrayInputStream(contents.getBytes("UTF-8")); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			// should never happen
			return null;
		}
	}

	private static List<IFCKEditorContentFile> getFlashFiles(List<IFCKEditorContentFile> files)
	{
		List<IFCKEditorContentFile> flashFiles = new LinkedList<IFCKEditorContentFile>(files);
		for (Iterator<IFCKEditorContentFile> iterator = flashFiles.iterator(); iterator.hasNext();) {
			IFCKEditorContentFile editorContentFile = iterator.next();
			if(!"application/x-shockwave-flash".equals(editorContentFile.getContentType().toLowerCase())) //$NON-NLS-1$
				iterator.remove();
		}
		return flashFiles;
	}

	private static List<IFCKEditorContentFile> getImageFiles(List<IFCKEditorContentFile> files)
	{
		List<IFCKEditorContentFile> imageFiles = new LinkedList<IFCKEditorContentFile>(files);
		for (Iterator<IFCKEditorContentFile> iterator = imageFiles.iterator(); iterator.hasNext();) {
			IFCKEditorContentFile editorContentFile = iterator.next();
			if(!editorContentFile.isImageFile())
				iterator.remove();
		}
		return imageFiles;
	}

	private class FileHelper
	{
		String getFileSingular() { return Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.UIBridge.file"); } //$NON-NLS-1$
		String getFilePlural() { return Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.UIBridge.files"); } //$NON-NLS-1$
		List<IFCKEditorContentFile> getFilteredFiles() { return getEditor().getEditorInput().getEditorContent().getFiles(); }
	}

	private class SelectFileRunnable implements Runnable
	{
		FileHelper fileHelper;
		String fileUrl;

		public SelectFileRunnable(FileHelper fileHelper)
		{
			this.fileHelper = fileHelper;
		}

		@Override
		public void run()
		{
			getEditor().setEnabled(false);
			try {
				SelectFileDialog dlg = new SelectFileDialog(
						getEditor().getSite().getShell(),
						fileHelper.getFilteredFiles(), getEditor().getImageProvider()) {

					private Link topLabel;

					private void updateTopLabel()
					{
						if(topLabel != null)
							topLabel.setText(String.format(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.UIBridge.topLabelText"), //$NON-NLS-1$
									fileHelper.getFilteredFiles().size(), fileHelper.getFilePlural(), fileHelper.getFileSingular()));
					}

					@Override
					protected Control createTopArea(Composite parent)
					{
						topLabel = new Link(parent, SWT.NONE);
						topLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
						updateTopLabel();
//						final FormText formText = new FormText(parent, SWT.NONE);
//						formText.setText(String.format(
//								"<form><p>This document contains %d %s. <a href=\"addfile\">Click here to add a new %s</a>.</p></form>",
//								fileHelper.getFilteredFiles().size(), fileHelper.getFilePlural(), fileHelper.getFileSingular()), true, false);
						topLabel.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
								FileDialog fileDialog = new FileDialog(getShell());
								fileDialog.setText(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.UIBridge.openFileDialogTitle")); //$NON-NLS-1$
								String filepath = fileDialog.open();
								if(filepath == null)
									return;

								String mimeType = ContentTypeUtil.getContentType(filepath);
								IContentFileWizard contentFileWizard;
								try {
									contentFileWizard = Activator.getDefault().getContentFileWizard(mimeType);
								} catch (CoreException e1) {
									String msg = String.format(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.UIBridge.wizardErrorText"), mimeType); //$NON-NLS-1$
									Activator.err(msg, e1);
									MessageDialog.openError(getShell(), Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.UIBridge.errorTitle"), msg); //$NON-NLS-1$
									return;
								}
								contentFileWizard.setSourceFile(new File(filepath), mimeType);
								int result = new WizardDialog(getShell(), contentFileWizard).open();
								if(result == IDialogConstants.OK_ID) {
									IFCKEditorContent editorContent = getEditor().getEditorInput().getEditorContent();
									IFCKEditorContentFile file = editorContent.getFileFactory().createContentFile();
									try {
										file.setData(contentFileWizard.getData());
									} catch (IOException e1) {
										String msg = String.format(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.UIBridge.loadingContentsErrorText"), e1.toString()); //$NON-NLS-1$
										Activator.err(msg, e1);
										MessageDialog.openError(getShell(), Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.UIBridge.errorTitle"), msg); //$NON-NLS-1$
										return;
									}
									file.setContentType(contentFileWizard.getMimeType());
									file.setName(contentFileWizard.getName());
									file.setDescription(contentFileWizard.getDescription());
									editorContent.addFile(file);
									setFiles(fileHelper.getFilteredFiles());
								}
							}
						});
						return topLabel;
					}

					/* (non-Javadoc)
					 * @see org.nightlabs.eclipse.ui.fckeditor.file.FileListDialog#setFiles(java.util.List)
					 */
					@Override
					public void setFiles(List<IFCKEditorContentFile> files)
					{
						updateTopLabel();
						super.setFiles(files);
					}
				};
				int result = dlg.open();
				if(result == IDialogConstants.OK_ID)
					try {
						IFCKEditorContentFile file = dlg.getSelectedFile();
						String extension = ContentTypeUtil.getFileExtension(file);
						if(extension == null)
							extension = ".bin"; //$NON-NLS-1$
						String filename = file.getFileId()+extension;
						fileUrl = getEditor().getBaseUrl()+"/uibridge/files/"+URLEncoder.encode(filename, "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
					} catch (UnsupportedEncodingException e) {
						// should never happen
						e.printStackTrace();
					}
				else
					fileUrl = ""; //$NON-NLS-1$
			} finally {
				getEditor().setEnabled(true);
			}
		}
	}

	private class ImageFileHelper extends FileHelper
	{
		@Override
		public String getFilePlural() { return Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.UIBridge.images"); } //$NON-NLS-1$
		@Override
		public String getFileSingular() { return Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.UIBridge.image"); } //$NON-NLS-1$
		@Override
		public List<IFCKEditorContentFile> getFilteredFiles()
		{
			return getImageFiles(super.getFilteredFiles());
		}
	}

	private class FlashFileHelper extends FileHelper
	{
		@Override
		public String getFilePlural() { return Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.UIBridge.flashFiles"); } //$NON-NLS-1$
		@Override
		public String getFileSingular() { return Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.UIBridge.flashFile"); } //$NON-NLS-1$
		@Override
		public List<IFCKEditorContentFile> getFilteredFiles()
		{
			return getFlashFiles(super.getFilteredFiles());
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getFileContents(java.lang.String, java.util.Properties)
	 */
	@Override
	public InputStream getFileContents(String subUri, Properties parms) {
		final Shell shell = getEditor().getSite().getShell();
		if("/uibridge/setdirty.xml".equals(subUri)) { //$NON-NLS-1$
			getEditor().setDirty(true);
			String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<dirty>true</dirty>\n"; //$NON-NLS-1$
			return getStreamForString(contents);
		}
		else if("/uibridge/insertimage.xml".equals(subUri)) { //$NON-NLS-1$
			SelectFileRunnable selectFileRunnable = new SelectFileRunnable(new ImageFileHelper());
			shell.getDisplay().syncExec(selectFileRunnable);
			return getStreamForString("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<file>"+selectFileRunnable.fileUrl+"</file>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if("/uibridge/insertlink.xml".equals(subUri)) { //$NON-NLS-1$
			SelectFileRunnable selectFileRunnable = new SelectFileRunnable(new FileHelper());
			shell.getDisplay().syncExec(selectFileRunnable);
			return getStreamForString("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<file>"+selectFileRunnable.fileUrl+"</file>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if("/uibridge/insertflash.xml".equals(subUri)) { //$NON-NLS-1$
			SelectFileRunnable selectFileRunnable = new SelectFileRunnable(new FlashFileHelper());
			shell.getDisplay().syncExec(selectFileRunnable);
			return getStreamForString("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<file>"+selectFileRunnable.fileUrl+"</file>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if(subUri.startsWith("/uibridge/files/")) { //$NON-NLS-1$
			String filename = subUri.substring("/uibridge/files/".length()); //$NON-NLS-1$
			int idx = filename.indexOf('.');
			if(idx == -1)
				throw new RuntimeException("Invalid file: "+filename); //$NON-NLS-1$
			long fileId = Long.parseLong(filename.substring(0, idx));
			IFCKEditorContentFile file = getEditor().getEditorInput().getEditorContent().getFile(fileId);
			if(file == null)
				return null;
			return new ByteArrayInputStream(file.getData());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getPath()
	 */
	@Override
	public String getPath() {
		return "/uibridge/"; //$NON-NLS-1$
	}
}
