package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface FileProvider 
{
	public String getPath();
	public InputStream getFileContents(String subUri, Properties parms) throws IOException;
	public String getContentType(String subUri);
}
