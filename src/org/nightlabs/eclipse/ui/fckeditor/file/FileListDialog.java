// $Id$
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormText;
import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

public class FileListDialog extends TitleAreaDialog
	{
		final List<IFCKEditorContentFile> files;
//		private Point initialSize;
		private IImageProvider imageProvider;
		private FileList fileList;

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
			newShell.setText("Document Files");
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
		 */
		@Override
		protected IDialogSettings getDialogBoundsSettings()
		{
			IDialogSettings pluginDialogSettings = Activator.getDefault().getDialogSettings();
			String sectionName = getClass().getName()+".bounds";
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
			setTitle("Document Files");
			setMessage("Browse the document's files");

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

		protected Composite createTopArea(final Composite parent)
		{
			final FormText formText = new FormText(parent, SWT.NONE);
			formText.setText(String.format("<form><p>This document contains %d files</p></form>", files.size()), true, false);
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
			return formText;
		}

		protected Composite createFileListArea(final Composite parent)
		{
			ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
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
		}
	}