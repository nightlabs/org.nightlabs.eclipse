package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class MarkDirtyProvider extends AbstractFileProvider {

	public MarkDirtyProvider(IFCKEditor editor) {
		super(editor);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getFileContents(java.lang.String, java.util.Properties)
	 */
	@Override
	public InputStream getFileContents(String subUri, Properties parms) {
		getEditor().markDirty(true);
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<dirty>true</dirty>\n";
		try {
			return new ByteArrayInputStream(contents.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// should never happen
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getPath()
	 */
	@Override
	public String getPath() {
		return "/markdirty.xml";
	}

}
