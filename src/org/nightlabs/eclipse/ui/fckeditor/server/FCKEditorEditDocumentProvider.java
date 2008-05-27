package org.nightlabs.eclipse.ui.fckeditor.server;

import java.util.Map;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;
import org.nightlabs.eclipse.ui.fckeditor.resource.Messages;

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
		return Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorEditDocumentProvider.loadingText"); //$NON-NLS-1$
	}
	
	private static String escapeContents(String contents)
	{
		if(contents == null)
			return ""; //$NON-NLS-1$
		return contents
				.replace("\\", "\\\\") //$NON-NLS-1$ //$NON-NLS-2$
				.replace("'", "\\'") //$NON-NLS-1$ //$NON-NLS-2$
				.replace("\n", "\\n") //$NON-NLS-1$ //$NON-NLS-2$
				.replace("\r", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	protected Map<String, String> getReplacements()
	{
		Map<String, String> replacements = super.getReplacements();
		replacements.put("loadingPaneText", getLoadingPaneText()); //$NON-NLS-1$
		replacements.put("escapedHtml", escapeContents(getEditor().getEditorInput().getEditorContent().getHtml())); //$NON-NLS-1$
		return replacements;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getPath()
	 */
	@Override
	public String getPath() 
	{
		return "/edit.html"; //$NON-NLS-1$
	}
}
