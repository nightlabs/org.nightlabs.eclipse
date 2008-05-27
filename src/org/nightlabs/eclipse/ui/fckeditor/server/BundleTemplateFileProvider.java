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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
 */
public abstract class BundleTemplateFileProvider extends BundleFileProvider
{
	/**
	 * Create a new BundleTemplateFileProvider instance.
	 * @param editor
	 */
	public BundleTemplateFileProvider(IFCKEditor editor)
	{
		super(editor);
	}

	@Override
	protected String getBundleFilename(String subUri)
	{
		return "/serverfiles/fckeditor-custom" + subUri; //$NON-NLS-1$
	}
	
	@Override
	public InputStream getFileContents(String subUri, Properties parms) throws IOException
	{
		InputStream in = super.getFileContents(subUri, parms);
		if(in == null)
			return null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		while(true) {
			String line = reader.readLine();
			if(line == null)
				break;
			for (Map.Entry<String, String> e : getReplacements().entrySet())
				line = line.replace("${"+e.getKey()+"}", e.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append(line);
			sb.append('\n');
		}
		return new ByteArrayInputStream(sb.toString().getBytes("UTF-8")); //$NON-NLS-1$
	}
	
	protected Map<String, String> getReplacements()
	{
		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put("baseUrl", getEditor().getBaseUrl()); //$NON-NLS-1$
		replacements.put("editorId", getEditor().getFCKEditorId()); //$NON-NLS-1$
		return replacements;
	}
	
}
