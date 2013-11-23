package org.nightlabs.eclipse.ui.fckeditor.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class LinkRewriter 
{
	private static final String LOCALLINK_MIDFIX = "/uibridge/files/";
	private static final String PERMALINK_PREFIX = "fckeditorfile://";

	public static String rewriteToPermaLinks(String html, String editorBaseUrl)
	{
		if(html == null)
			return null;
		if(editorBaseUrl == null)
			throw new NullPointerException("editorBaseUrl");
		Pattern p = Pattern.compile(Pattern.quote(editorBaseUrl+LOCALLINK_MIDFIX)+"(\\d+)");
		Matcher matcher = p.matcher(html);
		html = matcher.replaceAll(Matcher.quoteReplacement(PERMALINK_PREFIX)+"$1");
		return html;
	}

	public static String rewriteToLocalLinks(String html, String editorBaseUrl)
	{
		if(html == null)
			return null;
		if(editorBaseUrl == null)
			throw new NullPointerException("editorBaseUrl");
		Pattern p = Pattern.compile(Pattern.quote(PERMALINK_PREFIX)+"(\\d+)");
		Matcher matcher = p.matcher(html);
		html = matcher.replaceAll(Matcher.quoteReplacement(editorBaseUrl+LOCALLINK_MIDFIX)+"$1");
		return html;
	}
}
