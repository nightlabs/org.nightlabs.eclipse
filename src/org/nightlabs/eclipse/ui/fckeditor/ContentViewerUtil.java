package org.nightlabs.eclipse.ui.fckeditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nightlabs.eclipse.ui.fckeditor.util.IOUtil;
import org.nightlabs.htmlcontent.IFCKEditorContent;
import org.nightlabs.htmlcontent.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ContentViewerUtil
{
	private static final String PERMALINK_PREFIX = "fckeditorfile://";

	public static String createStaticContent(IFCKEditorContent content) throws IOException
	{
		File cssTmp = File.createTempFile("jfire-css", ".css");
		cssTmp.deleteOnExit();
		IOUtil.writeTextFile(cssTmp, "body, td, a, p, .h { font-family:arial,sans-serif; }", "UTF-8");

		File htmlTmp = File.createTempFile("jfire-html", ".html");
		htmlTmp.deleteOnExit();
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
		sb.append("<head>\n");
		sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" charset=\"UTF-8\" href=\""+cssTmp.toURI().toString()+"\" />\n");
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append(rewriteToLocalLinks(content));
		sb.append("</body>\n");
		sb.append("</html>\n");
		IOUtil.writeTextFile(htmlTmp, sb.toString(), "UTF-8");

		return htmlTmp.toURI().toString();
	}

	public static String rewriteToLocalLinks(IFCKEditorContent content) throws IOException
	{
		String html = content.getHtml();
		if(html == null)
			return null;

		String rewritten = html;

		Pattern p = Pattern.compile(Pattern.quote(PERMALINK_PREFIX)+"(\\d+)(\\.[a-zA-Z0-9]+)");

		Matcher m1 = p.matcher(html);
		while(m1.find()) {
			String permalink = m1.group();
			Long fileId = Long.parseLong(m1.group(1));
			String fileExtension = m1.group(2);
			IFCKEditorContentFile file = content.getFile(fileId);
			if(file == null)
				throw new IllegalStateException("File id not found: "+fileId);
			File imageTmp = File.createTempFile("jfire-image", fileExtension);
			imageTmp.deleteOnExit();
			FileOutputStream out = new FileOutputStream(imageTmp);
			try {
				out.write(file.getData());
			} finally {
				out.close();
			}
			rewritten = rewritten.replace(permalink, imageTmp.toURI().toString());
		}

		return rewritten;
	}
}
