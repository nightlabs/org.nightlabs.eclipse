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
package org.nightlabs.eclipse.ui.fckeditor.file;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;
import org.nightlabs.eclipse.ui.fckeditor.resource.Messages;

public class FileListDialog extends TitleAreaDialog
	{
		final List<IFCKEditorContentFile> files;
//		private Point initialSize;
		private IImageProvider imageProvider;
		private ScrolledComposite sc;
		private FileList fileList;
		private Label topLabel;

		public FileListDialog(Shell parentShell, List<IFCKEditorContentFile> files, IImageProvider imageProvider)
		{
			super(parentShell);
			this.files = files;
			this.imageProvider = imageProvider;
			setShellStyle(getShellStyle() | SWT.RESIZE);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
		 */
		@Override
		protected void configureShell(Shell newShell)
		{
			super.configureShell(newShell);
			newShell.setText(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.FileListDialog.dialogTitle")); //$NON-NLS-1$
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
		 */
		@Override
		protected IDialogSettings getDialogBoundsSettings()
		{
			IDialogSettings pluginDialogSettings = Activator.getDefault().getDialogSettings();
			String sectionName = getClass().getName()+".bounds"; //$NON-NLS-1$
			IDialogSettings section = pluginDialogSettings.getSection(sectionName);
			if(section == null)
				section = pluginDialogSettings.addNewSection(sectionName);
			return section;
		}

//		@Override
//		protected Point getInitialSize()
//		{
//			if(initialSize == null)
//				return super.getInitialSize();
//			return initialSize;
//		}

		@Override
		protected Control createDialogArea(Composite parent)
		{
			setTitle(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.FileListDialog.dialogMessageTitle")); //$NON-NLS-1$
			setMessage(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.FileListDialog.dialogMessageMessage")); //$NON-NLS-1$

			// create a composite with standard margins and spacing
			final Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			applyDialogFont(composite);

			BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run()
				{
					createTopArea(composite);
					createFileListArea(composite);
				}
			});

			return composite;

//			final Composite c = (Composite)super.createDialogArea(parent);
//			GridLayout gd = ((GridLayout)c.getLayout());
//			gd.horizontalSpacing
//			createFileListArea(c);
//			return c;
		}

		private void updateTopLabel()
		{
			if(topLabel != null)
				topLabel.setText(String.format(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.FileListDialog.topLabelText"), files.size())); //$NON-NLS-1$
		}

		protected Control createTopArea(final Composite parent)
		{
			topLabel = new Label(parent, SWT.NONE);
			topLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			updateTopLabel();
			return topLabel;
//			final FormText formText = new FormText(parent, SWT.NONE);
//			formText.setText(String.format("<form><p>This document contains %d files</p></form>", files.size()), true, false);

//			formText.addHyperlinkListener(new HyperlinkAdapter() {
//				@Override
//				public void linkActivated(HyperlinkEvent e)
//				{
//					System.out.println("link: "+e);
//					FileDialog fileDialog = new FileDialog(formText.getShell());
//					String filepath = fileDialog.open();
//					if(filepath != null)
//				}
//			});
//			return formText;
		}

		protected Composite createFileListArea(final Composite parent)
		{
			sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			sc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
			sc.setExpandVertical(true);
			sc.setExpandHorizontal(true);
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			sc.setLayoutData(gridData);
			fileList = createFileList(sc);
			sc.setContent(fileList);
			Point fileListSize = fileList.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			sc.setMinSize(fileListSize);
			//initialSize = new Point(fileListSize.x + 64, 450);
			return sc;
		}

		protected FileList createFileList(Composite parent)
		{
			return new FileList(parent, SWT.NONE, files, imageProvider);
		}

		/**
		 * Get the files.
		 * @return the files
		 */
		protected List<IFCKEditorContentFile> getFiles()
		{
			return files;
		}

		/**
		 * Get the imageProvider.
		 * @return the imageProvider
		 */
		public IImageProvider getImageProvider()
		{
			return imageProvider;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.TrayDialog#close()
		 */
		@Override
		public boolean close()
		{
			imageProvider.stopThumbnailing();
			return super.close();
		}

		public void setFiles(List<IFCKEditorContentFile> files)
		{
			fileList.setFiles(files);
			updateTopLabel();
			Point fileListSize = fileList.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			sc.setMinSize(fileListSize);
			sc.layout(true, true);
		}
	}