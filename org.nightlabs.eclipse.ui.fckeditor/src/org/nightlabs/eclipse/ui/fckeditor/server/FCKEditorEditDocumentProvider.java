/* ************************************************************************
 * org.nightlabs.eclipse.ui.fckeditor - Eclipse RCP FCKeditor Integration *
 * Copyright (C) 2008 NightLabs - http://NightLabs.org                    *
 *                                                                        *
 * This library is free software; you can redistribute it and/or          *
 * modify it under the terms of the GNU Lesser General Public             *
 * License as published by the Free Software Foundation; either           *
 * version 2.1 of the License, or (at your option) any later version.     *
 *                                                                        *
 * This library is distributed in the hope that it will be useful,        *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of         *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU      *
 * Lesser General Public License for more details.                        *
 *                                                                        *
 * You should have received a copy of the GNU Lesser General Public       *
 * License along with this library; if not, write to the                  *
 *     Free Software Foundation, Inc.,                                    *
 *     51 Franklin St, Fifth Floor,                                       *
 *     Boston, MA  02110-1301  USA                                        *
 *                                                                        *
 * Or get it online:                                                      *
 *     http://www.gnu.org/copyleft/lesser.html                            *
 **************************************************************************/
package org.nightlabs.eclipse.ui.fckeditor.server;

import java.util.Map;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;
import org.nightlabs.eclipse.ui.fckeditor.resource.Messages;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
 */
public class FCKEditorEditDocumentProvider extends BundleTemplateFileProvider
{
	public FCKEditorEditDocumentProvider(IFCKEditor editor) {
		super(editor);
	}

	protected String getLoadingPaneText()
	{
		return Messages.getString("org.nightlabs.eclipse.ui.fckeditor.server.FCKEditorEditDocumentProvider.loadingText"); //$NON-NLS-1$
	}

	private static String escapeContents(String contents)
	{
		if(contents == null)
			return ""; //$NON-NLS-1$
		return saveConvert(contents);
//		return contents
//		.replace("\\", "\\\\") //$NON-NLS-1$ //$NON-NLS-2$
//		.replace("'", "\\'") //$NON-NLS-1$ //$NON-NLS-2$
//		.replace("\n", "\\n") //$NON-NLS-1$ //$NON-NLS-2$
//		.replace("\r", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected Map<String, String> getReplacements()
	{
		Map<String, String> replacements = super.getReplacements();
		replacements.put("loadingPaneText", getLoadingPaneText()); //$NON-NLS-1$
		String content = getEditor().getEditorInput().getEditorContent().getHtml();
		content = LinkRewriter.rewriteToLocalLinks(content, getEditor().getBaseUrl());
		replacements.put("escapedHtml", escapeContents(content)); //$NON-NLS-1$
		return replacements;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.server.FileProvider#getPath()
	 */
	@Override
	public String getPath()
	{
		return "/edit.html"; //$NON-NLS-1$
	}




	// TAKEN FROM java.util.Properties
	/*
	 * Converts unicodes to encoded &#92;uxxxx and escapes
	 * special characters with a preceding slash
	 */
	private static String saveConvert(String theString) {

		boolean escapeUnicode = true;

		int len = theString.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuilder outBuffer = new StringBuilder(bufLen);

		for(int x=0; x<len; x++) {
			char aChar = theString.charAt(x);
			// Handle common case first, selecting largest block that
			// avoids the specials below
			if ((aChar > 62) && (aChar < 127)) {
				if (aChar == '\\') {
					outBuffer.append('\\'); outBuffer.append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch(aChar) {
			case '\t':outBuffer.append('\\'); outBuffer.append('t');
				break;
			case '\n':outBuffer.append('\\'); outBuffer.append('n');
				break;
			case '\r':outBuffer.append('\\'); outBuffer.append('r');
				break;
			case '\f':outBuffer.append('\\'); outBuffer.append('f');
				break;
			// special:
			case '>': // Fall through
			case '<': // Fall through
			case '"': // Fall through
			case '\'':
				appendUnicodeHex(outBuffer, aChar);
				break;
			default:
				if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode ) {
					appendUnicodeHex(outBuffer, aChar);
				} else {
					outBuffer.append(aChar);
				}
			}
		}
		return outBuffer.toString();
	}

	private static void appendUnicodeHex(StringBuilder outBuffer, char aChar)
	{
		outBuffer.append('\\');
		outBuffer.append('u');
		outBuffer.append(toHex((aChar >> 12) & 0xF));
		outBuffer.append(toHex((aChar >>  8) & 0xF));
		outBuffer.append(toHex((aChar >>  4) & 0xF));
		outBuffer.append(toHex( aChar        & 0xF));
	}

	// TAKEN FROM java.util.Properties
	/**
	 * Convert a nibble to a hex character
	 * @param	nibble	the nibble to convert.
	 */
	private static char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
	}

	// TAKEN FROM java.util.Properties
	/** A table of hex digits */
	private static final char[] hexDigit = {
		'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
	};

}
