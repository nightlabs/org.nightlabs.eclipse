package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.InputStream;
import java.util.Properties;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorSaveDocumentProvider extends FCKEditorEditDocumentProvider {
	/**
	 * Create a new SaveDocumentFileProvider instance.
	 */
	public FCKEditorSaveDocumentProvider(IFCKEditor editor) 
	{
		super(editor);
	}

	@Override
	public String getPath() {
		return "/save.html";
	}
	
	@Override
	protected String getLoadingPaneText() {
		return "Saving...";
	}
	
	@Override
	public InputStream getFileContents(String filename, Properties parms) {
		String contents = parms.getProperty(getFCKEditorId());
		if(contents == null)
			throw new RuntimeException("Error saving contents. Content parameter not found.");
		getEditor().getEditorInput().getEditorContent().setHtml(contents);
		getEditor().markDirty(false);
		return super.getFileContents(filename, parms);
	}
}
