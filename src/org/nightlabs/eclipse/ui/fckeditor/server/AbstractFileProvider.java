package org.nightlabs.eclipse.ui.fckeditor.server;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;
import org.nightlabs.eclipse.ui.fckeditor.file.ContentTypeUtil;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class AbstractFileProvider implements FileProvider {

//	private static Map<String, String> contentTypes = new HashMap<String, String>();
//	static {
//		contentTypes.put("htm", "text/html");
//		contentTypes.put("html", "text/html");
//		contentTypes.put("txt", "text/plain");
//		contentTypes.put("asc", "text/plain");
//		contentTypes.put("xml", "text/xml");
//		contentTypes.put("css", "text/css");
//		contentTypes.put("js", "text/javascript");
//		contentTypes.put("gif", "image/gif");
//		contentTypes.put("jpg", "image/jpeg");
//		contentTypes.put("jpeg", "image/jpeg");
//		contentTypes.put("png", "png");
//	}
//	"htm		text/html "+
//	"html		text/html "+
//	"txt		text/plain "+
//	"asc		text/plain "+
//	"xml		text/xml "+
//	"css		text/css "+
//	"js		text/javascript "+
//	"gif		image/gif "+
//	"jpg		image/jpeg "+
//	"jpeg		image/jpeg "+
//	"png		image/png "+
//	"mp3		audio/mpeg "+
//	"m3u		audio/mpeg-url " +
//	"pdf		application/pdf "+
//	"doc		application/msword "+
//	"ogg		application/x-ogg "+
//	"zip		application/octet-stream "+
//	"exe		application/octet-stream "+
//	"class		application/octet-stream " );

	private IFCKEditor editor;

	public AbstractFileProvider(IFCKEditor editor)
	{
		this.editor = editor;
	}

	protected IFCKEditor getEditor()
	{
		return editor;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType(String subUri) {
		return ContentTypeUtil.getContentType(subUri);
//		int idx = subUri.lastIndexOf('.');
//		String ext = subUri.substring(idx+1).toLowerCase();
//		String contentType = contentTypes.get(ext);
//		return contentType == null ? "application/octet-stream" : contentType;
	}

}
