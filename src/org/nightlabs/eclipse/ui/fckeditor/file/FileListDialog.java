// $Id$
package org.nightlabs.eclipse.ui.fckeditor.file;

import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

public class FileListDialog extends TitleAreaDialog
	{
		final List<IFCKEditorContentFile> files;		
		private Point initialSize;
		private IImageProvider imageProvider;
		
		public FileListDialog(Shell parentShell, List<IFCKEditorContentFile> files, IImageProvider imageProvider)
		{
			super(parentShell);
			this.files = files;
			this.imageProvider = imageProvider;
			setShellStyle(getShellStyle() | SWT.RESIZE);
			parentShell.setText("Document Files");
		}
		
		@Override
		protected Point getInitialSize()
		{
			if(initialSize == null)
				return super.getInitialSize();
			return initialSize;
		}
		
		@Override
		protected Control createDialogArea(Composite parent)
		{
			final Composite c = (Composite)super.createDialogArea(parent);
			BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run()
				{
					setTitle("Document Files");
					setMessage("Browse the document's files");
					
					ScrolledComposite sc = new ScrolledComposite(c, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
					sc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
					sc.setExpandVertical(true);
					sc.setExpandHorizontal(true);
					GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
					sc.setLayoutData(gridData);
					FileList fileList = createFileList(sc);
					sc.setContent(fileList);
					Point fileListSize = fileList.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					sc.setMinSize(fileListSize);
					initialSize = new Point(fileListSize.x + 64, 450);
				}
			});
			
			return c;
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
	}