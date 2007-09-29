/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

public class EditorFileFilterRegistry 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.editorfilefilter"; //$NON-NLS-1$

	private List<String> patterns = new ArrayList<String>();
	
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	public void processElement(IExtension extension, IConfigurationElement element) 
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase("editorFileFilter")) { //$NON-NLS-1$
			String pattern = element.getAttribute("pattern"); //$NON-NLS-1$
			if (pattern == null || "".equals(pattern)) //$NON-NLS-1$
				throw new EPProcessorException("Element editorFileFilter has to define attribute pattern."); //$NON-NLS-1$
			patterns.add(pattern);
		}
	}

	private String globalPattern;
	
	public String getGlobalPattern() 
	{
		checkProcessing();
		
		if (globalPattern == null){
			StringBuffer globalPatternSB = new StringBuffer("^"); //$NON-NLS-1$
			for (Iterator it = patterns.iterator(); it.hasNext(); ) {
				globalPatternSB.append('(');
				globalPatternSB.append(it.next());
				globalPatternSB.append(')');
				if (it.hasNext())
					globalPatternSB.append('|');
			}
			globalPatternSB.append('$');
			globalPattern = globalPatternSB.toString();
		}
		return globalPattern;			
	}
	
	public boolean doesMatchEditorID(String id) 
	{
		checkProcessing();
		if (patterns.size() == 0)
			return true;
		return Pattern.matches(getGlobalPattern(), id);
	}

	public List getPatterns() 
	{
		checkProcessing();
		return patterns;
	}
	
	private static EditorFileFilterRegistry sharedInstance;
	
	/**
	 * @deprecated Use {@link #sharedInstance()} instead
	 */
	public static EditorFileFilterRegistry getSharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new EditorFileFilterRegistry();
		return sharedInstance;
	}
	
	public static EditorFileFilterRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new EditorFileFilterRegistry();
		return sharedInstance;
	}
}
