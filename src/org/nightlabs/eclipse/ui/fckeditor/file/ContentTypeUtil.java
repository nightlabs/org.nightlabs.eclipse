package org.nightlabs.eclipse.ui.fckeditor.file;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class ContentTypeUtil
{
	public static String getFileExtension(IFCKEditorContentFile file)
	{
		if("application/pdf".equals(file.getContentType()))
			return ".pdf";
		else if("image/jpeg".equals(file.getContentType()))
			return ".jpg";
		else if("image/gif".equals(file.getContentType()))
			return ".gif";
		else if("image/png".equals(file.getContentType()))
			return ".png";
		else if("text/html".equals(file.getContentType()))
			return ".html";
		return null;
	}

}
