package org.nightlabs.eclipse.ui.fckeditor.server;

import java.util.Map;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorEditDocumentProvider extends BundleTemplateFileProvider 
{
	public FCKEditorEditDocumentProvider(IFCKEditor editor) {
		super(editor);
	}

	protected String getLoadingPaneText()
	{
		return "Loading...";
	}
	
	private static String escapeContents(String contents)
	{
		if(contents == null)
			return "";
		return contents
				.replace("\\", "\\\\")
				.replace("'", "\\'")
				.replace("\n", "\\n")
				.replace("\r", "");
	}
	
	@Override
	protected Map<String, String> getReplacements()
	{
		Map<String, String> replacements = super.getReplacements();
		replacements.put("loadingPaneText", getLoadingPaneText());
		replacements.put("escapedHtml", escapeContents(getEditor().getEditorInput().getEditorContent().getHtml()));
		return replacements;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getPath()
	 */
	@Override
	public String getPath() 
	{
		return "/edit.html";
	}
}
