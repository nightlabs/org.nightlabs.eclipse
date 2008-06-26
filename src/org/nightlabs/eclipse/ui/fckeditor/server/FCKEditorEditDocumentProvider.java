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
		return contents
				.replace("\\", "\\\\") //$NON-NLS-1$ //$NON-NLS-2$
				.replace("'", "\\'") //$NON-NLS-1$ //$NON-NLS-2$
				.replace("\n", "\\n") //$NON-NLS-1$ //$NON-NLS-2$
				.replace("\r", ""); //$NON-NLS-1$ //$NON-NLS-2$
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
}
