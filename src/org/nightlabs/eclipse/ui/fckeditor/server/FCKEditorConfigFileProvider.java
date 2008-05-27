package org.nightlabs.eclipse.ui.fckeditor.server;

import java.util.Locale;
import java.util.Map;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorConfigFileProvider extends BundleTemplateFileProvider {

	public FCKEditorConfigFileProvider(IFCKEditor editor) {
		super(editor);
	}

	@Override
	protected Map<String, String> getReplacements()
	{
		Map<String, String> replacements = super.getReplacements();
		replacements.put("language", Locale.getDefault().getLanguage()); //$NON-NLS-1$
		return replacements;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getPath()
	 */
	@Override
	public String getPath() {
		return "/editorconfig.js"; //$NON-NLS-1$
	}
}
