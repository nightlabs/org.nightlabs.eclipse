package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorContentFile;
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

	private class FileHelper
	{
		String getFileSingular() { return "file"; }
		String getFilePlural() { return "files"; }
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
					@Override
					protected Composite createTopArea(Composite parent)
					{
						final FormText formText = new FormText(parent, SWT.NONE);
						formText.setText(String.format(
								"<form><p>This document contains %d %s. <a href=\"addfile\">Click here to add a new %s</a>.</p></form>",
								fileHelper.getFilteredFiles().size(), fileHelper.getFilePlural(), fileHelper.getFileSingular()), true, false);
						formText.addHyperlinkListener(new HyperlinkAdapter() {
							@Override
							public void linkActivated(HyperlinkEvent e)
							{
								System.out.println("LINK!");
								NewImageDialog newImageDialog = new NewImageDialog(getShell());
								int result = newImageDialog.open();
								if(result == IDialogConstants.OK_ID) {
									String filename = newImageDialog.getFilename();
									String contentType = ContentTypeUtil.getContentType(filename);
									ByteArrayOutputStream out = new ByteArrayOutputStream();
									ImageLoader imageLoader = new ImageLoader();
									imageLoader.data = new ImageData[] { newImageDialog.getImage().getImageData() };
									// TODO: where to get an "anonymous" instance from?
									IFCKEditorContentFile file = new FCKEditorContentFile();
									if(ContentTypeUtil.IMAGE_JPEG.equals(contentType)) {
										imageLoader.save(out, SWT.IMAGE_JPEG);
										file.setContentType(ContentTypeUtil.IMAGE_JPEG);
									} else {
										imageLoader.save(out, SWT.IMAGE_PNG);
										file.setContentType(ContentTypeUtil.IMAGE_PNG);
									}
									file.setData(out.toByteArray());
									file.setName(filename);
									getEditor().getEditorInput().getEditorContent().addFile(file);
									setFiles(fileHelper.getFilteredFiles());
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
						fileUrl = getEditor().getBaseUrl()+"/uibridge/files/"+URLEncoder.encode(filename, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// should never happen
						e.printStackTrace();
					}
				else
					fileUrl = "";
			} finally {
				getEditor().setEnabled(true);
			}
		}
	}

	private class ImageFileHelper extends FileHelper
	{
		@Override
		public String getFilePlural() { return "images"; }
		@Override
		public String getFileSingular() { return "image"; }
		@Override
		public List<IFCKEditorContentFile> getFilteredFiles()
		{
			return getImageFiles(super.getFilteredFiles());
		}
	}

	private class FlashFileHelper extends FileHelper
	{
		@Override
		public String getFilePlural() { return "Flash file"; }
		@Override
		public String getFileSingular() { return "Flash files"; }
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
		if("/uibridge/setdirty.xml".equals(subUri)) {
			getEditor().setDirty(true);
			String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<dirty>true</dirty>\n";
			return getStreamForString(contents);
		}
		else if("/uibridge/insertimage.xml".equals(subUri)) {
			SelectFileRunnable selectFileRunnable = new SelectFileRunnable(new ImageFileHelper());
			shell.getDisplay().syncExec(selectFileRunnable);
			return getStreamForString("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<file>"+selectFileRunnable.fileUrl+"</file>\n");
		}
		else if("/uibridge/insertlink.xml".equals(subUri)) {
			SelectFileRunnable selectFileRunnable = new SelectFileRunnable(new FileHelper());
			shell.getDisplay().syncExec(selectFileRunnable);
			return getStreamForString("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<file>"+selectFileRunnable.fileUrl+"</file>\n");
		}
		else if("/uibridge/insertflash.xml".equals(subUri)) {
			SelectFileRunnable selectFileRunnable = new SelectFileRunnable(new FlashFileHelper());
			shell.getDisplay().syncExec(selectFileRunnable);
			return getStreamForString("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<file>"+selectFileRunnable.fileUrl+"</file>\n");
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
