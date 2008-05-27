package org.nightlabs.eclipse.ui.fckeditor.server;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FCKEditorCSSProvider extends BundleFileProvider {

	/**
	 * Create a new FCKEditorCSSProvider instance.
	 */
	public FCKEditorCSSProvider(IFCKEditor editor) {
		super(editor);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getPath()
	 */
	@Override
	public String getPath() {
		return "/style.css"; //$NON-NLS-1$
	}
}
