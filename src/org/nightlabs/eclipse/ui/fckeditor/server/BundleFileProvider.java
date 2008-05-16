package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class BundleFileProvider extends AbstractFileProvider {

	/**
	 * Create a new BundleFileProvider instance.
	 */
	public BundleFileProvider(IFCKEditor editor) {
		super(editor);
	}

	protected String getBundleFilename(String subUri)
	{
		return "/serverfiles" + subUri;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getFileContents(java.lang.String)
	 */
	@Override
	public InputStream getFileContents(String subUri, Properties parms) throws IOException {
		String bundleFilename = getBundleFilename(subUri);
		URL resource = Activator.getDefault().getBundle().getResource(bundleFilename);
		if(resource == null) {
			Activator.err("Bundle resource not found: "+bundleFilename);
			return null;
		}
		return resource.openStream();
	}
}
