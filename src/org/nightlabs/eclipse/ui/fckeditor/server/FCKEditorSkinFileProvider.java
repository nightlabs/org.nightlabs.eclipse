package org.nightlabs.eclipse.ui.fckeditor.server;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorSkinFileProvider extends BundleFileProvider
{
	public FCKEditorSkinFileProvider(IFCKEditor editor) {
		super(editor);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getPath()
	 */
	@Override
	public String getPath() {
		return "/fckeditor-skin/";
	}
}
