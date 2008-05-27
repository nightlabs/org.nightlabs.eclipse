package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

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
		return "/fckeditor-skin/"; //$NON-NLS-1$
	}
	
	@Override
	public InputStream getFileContents(String subUri, Properties parms)
			throws IOException
	{
		InputStream in = super.getFileContents(subUri, parms);
		if(subUri.equals("/fckeditor-skin/fck_editor.css")) { //$NON-NLS-1$
			// adapt colors
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder contents = new StringBuilder();
			while(true) {
				String line = reader.readLine();
				if(line == null)
					break;
				contents.append(line
						.replace("#abcde0", getEditor().getWidgetBackgroundColor()) //$NON-NLS-1$
						.replace("#abcde1", getEditor().getWidgetSelectedColor()) //$NON-NLS-1$
						.replace("#abcde2", getEditor().getWidgetHoverColor()) //$NON-NLS-1$
						);
			}
			in = new ByteArrayInputStream(contents.toString().getBytes("UTF-8")); //$NON-NLS-1$
		}
		return in;
	}
}
