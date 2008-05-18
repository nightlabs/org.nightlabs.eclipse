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
		return "/fckeditor-skin/";
	}
	
	@Override
	public InputStream getFileContents(String subUri, Properties parms)
			throws IOException
	{
		InputStream in = super.getFileContents(subUri, parms);
		if(subUri.equals("/fckeditor-skin/fck_editor.css")) {
			// adapt colors
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder contents = new StringBuilder();
			while(true) {
				String line = reader.readLine();
				if(line == null)
					break;
				contents.append(line
						.replace("#abcde0", getEditor().getWidgetBackgroundColor())
						.replace("#abcde1", getEditor().getTitleBackgroundColor())
						.replace("#abcde2", getEditor().getTitleBackgroundGradientColor())
						);
			}
			in = new ByteArrayInputStream(contents.toString().getBytes("UTF-8"));
		}
		return in;
	}
}
