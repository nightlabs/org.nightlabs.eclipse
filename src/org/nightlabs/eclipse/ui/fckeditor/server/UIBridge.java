package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;
import org.nightlabs.eclipse.ui.fckeditor.file.ContentTypeUtil;
import org.nightlabs.eclipse.ui.fckeditor.file.NewImageDialog;
import org.nightlabs.eclipse.ui.fckeditor.file.SelectFileDialog;

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
			return new ByteArrayInputStream(contents.getBytes("UTF-8"));
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
			if(!"application/x-shockwave-flash".equals(editorContentFile.getContentType().toLowerCase()))
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

	private class SelectFileRunnable implements Runnable
	{
		List<IFCKEditorContentFile> files;
		String imageUrl;

		public SelectFileRunnable(List<IFCKEditorContentFile> files)
		{
			this.files = files;
		}

		@Override
		public void run()
		{
			getEditor().setEnabled(false);
			try {
				SelectFileDialog dlg = new SelectFileDialog(
						getEditor().getSite().getShell(),
						files, getEditor().getImageProvider()) {
					@Override
					protected Composite createTopArea(Composite parent)
					{
						final FormText formText = new FormText(parent, SWT.NONE);
						formText.setText(String.format("<form><p>This document contains %d %s. <a href=\"addfile\">Click here to add a new %s</a>.</p></form>", files.size(), "files", "file"), true, false);
						formText.addHyperlinkListener(new HyperlinkAdapter() {
							@Override
							public void linkActivated(HyperlinkEvent e)
							{
								System.out.println("LINK!");
								NewImageDialog newImageDialog = new NewImageDialog(getShell());
								int result = newImageDialog.open();
								if(result == IDialogConstants.OK_ID) {
									// TODO
									System.out.println("Ok...");
								}
							}
						});
						return formText;
					}
				};
				int result = dlg.open();
				if(result == IDialogConstants.OK_ID)
					try {
						IFCKEditorContentFile file = dlg.getSelectedFile();
						String extension = ContentTypeUtil.getFileExtension(file);
						if(extension == null)
							extension = ".bin";
						String filename = file.getFileId()+extension;
						imageUrl = getEditor().getBaseUrl()+"/uibridge/files/"+URLEncoder.encode(filename, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// should never happen
						e.printStackTrace();
					}
				else
					imageUrl = "";
			} finally {
				getEditor().setEnabled(true);
			}
		}
	}

	private String selectFile(final Shell shell, final List<IFCKEditorContentFile> files)
	{
		SelectFileRunnable selectFileRunnable = new SelectFileRunnable(files);
		shell.getDisplay().syncExec(selectFileRunnable);
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<file>"+selectFileRunnable.imageUrl+"</file>\n";
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getFileContents(java.lang.String, java.util.Properties)
	 */
	@Override
	public InputStream getFileContents(String subUri, Properties parms) {
		final Shell shell = getEditor().getSite().getShell();
		final List<IFCKEditorContentFile> allFiles = getEditor().getEditorInput().getEditorContent().getFiles();
		if("/uibridge/setdirty.xml".equals(subUri)) {
			getEditor().setDirty(true);
			String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<dirty>true</dirty>\n";
			return getStreamForString(contents);
		}
		else if("/uibridge/insertimage.xml".equals(subUri)) {
			return getStreamForString(selectFile(shell, getImageFiles(allFiles)));
//			final Shell shell = getEditor().getSite().getShell();
//			SelectFileRunnable selectFileRunnable = new SelectFileRunnable(getImageFiles(getEditor().getEditorInput().getEditorContent().getFiles()));
//			shell.getDisplay().syncExec(selectFileRunnable);
//			String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<file>"+selectFileRunnable.imageUrl+"</file>\n";
//			return getStreamForString(contents);
		}
		else if("/uibridge/insertlink.xml".equals(subUri)) {
			return getStreamForString(selectFile(shell, allFiles));
//			final Shell shell = getEditor().getSite().getShell();
//			SelectFileRunnable selectFileRunnable = new SelectFileRunnable(getEditor().getEditorInput().getEditorContent().getFiles());
//			shell.getDisplay().syncExec(selectFileRunnable);
//			String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<file>"+selectFileRunnable.imageUrl+"</file>\n";
//			return getStreamForString(contents);
		}
		else if("/uibridge/insertflash.xml".equals(subUri)) {
			return getStreamForString(selectFile(shell, getFlashFiles(allFiles)));
//			final Shell shell = getEditor().getSite().getShell();
//			SelectFileRunnable selectFileRunnable = new SelectFileRunnable(getFlashFiles(getEditor().getEditorInput().getEditorContent().getFiles()));
//			shell.getDisplay().syncExec(selectFileRunnable);
//			String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<file>"+selectFileRunnable.imageUrl+"</file>\n";
//			return getStreamForString(contents);
		}
		else if(subUri.startsWith("/uibridge/files/")) {
			String filename = subUri.substring("/uibridge/files/".length());
			int idx = filename.indexOf('.');
			if(idx == -1)
				throw new RuntimeException("Invalid file: "+filename);
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
		return "/uibridge/";
	}
}
