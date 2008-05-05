package org.nightlabs.eclipse.ui.fckeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class NLFinder extends AbstractFileProvider
{
	/**
	 * Create a new NLFinder instance.
	 */
	public NLFinder(IFCKEditor editor)
	{
		super(editor);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getFileContents(java.lang.String, java.util.Properties)
	 */
	@Override
	public InputStream getFileContents(String subUri, Properties parms)	throws IOException
	{
		String fileBaseUrl = getEditor().getBaseUrl()+"/nlfinder/files/";
		System.out.println("FINDER: "+subUri);
		System.out.println("FINDER params: "+parms);
		String content = "Nothing here for "+subUri;
		if("/nlfinder/nlfinder.html".equals(subUri)) {
			content = 
			"<html>\n"+
			"<head>\n"+
			"<title>My File Browser</title>\n"+
			"<script type=\"text/javascript\">\n"+
			"\n"+
			"function SelectFile( fileUrl )\n"+
			"{\n"+
			"// window.opener.SetUrl( url, width, height, alt);\n"+
			"window.opener.SetUrl( fileUrl ) ;\n"+
			"window.close() ;\n"+
			"}\n"+
			"</script>\n"+
			"</head>\n"+
			"<body>\n"+
			"<a href=\"javascript:SelectFile('"+fileBaseUrl+"vladstudio_skiing_1600x1200.jpg');\">File 1</a><br />\n"+
			"<a href=\"javascript:SelectFile('"+fileBaseUrl+"File2.jpg');\">File 2</a>\n"+
			"</body>\n"+
			"</html>\n"+				
			"";
		} else if(subUri.startsWith("/nlfinder/files/")) {
			URL resource = Activator.getDefault().getBundle().getResource("icons/vladstudio_skiing_1600x1200.jpg");
			if(resource != null)
				return resource.openStream();
		}
		return new ByteArrayInputStream(content.getBytes());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getPath()
	 */
	@Override
	public String getPath()
	{
		return "/nlfinder/";
	}
}
