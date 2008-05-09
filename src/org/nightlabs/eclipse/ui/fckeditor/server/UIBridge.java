package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;
import org.nightlabs.eclipse.ui.fckeditor.file.ContentTypeUtil;
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
						files, getEditor().getImageProvider());
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
	
	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getFileContents(java.lang.String, java.util.Properties)
	 */
	@Override
	public InputStream getFileContents(String subUri, Properties parms) {
		if("/uibridge/setdirty.xml".equals(subUri)) {
			getEditor().setDirty(true);
			String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<dirty>true</dirty>\n";
			return getStreamForString(contents);
		}
		else if("/uibridge/insertimage.xml".equals(subUri)) {
			final Shell shell = getEditor().getSite().getShell();
			SelectFileRunnable selectFileRunnable = new SelectFileRunnable(getImageFiles(getEditor().getEditorInput().getEditorContent().getFiles()));
			shell.getDisplay().syncExec(selectFileRunnable);
			String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<image>"+selectFileRunnable.imageUrl+"</image>\n";
			return getStreamForString(contents);
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
