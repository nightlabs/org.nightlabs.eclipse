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
package org.nightlabs.base.perspective;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.action.ContributionItemSetRegistry;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class PerspectiveExtensionRegistry 
extends AbstractEPProcessor 
{
	private static final Logger logger = Logger.getLogger(PerspectiveExtensionRegistry.class);

	private static PerspectiveExtensionRegistry sharedInstance;
	public static PerspectiveExtensionRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (PerspectiveExtensionRegistry.class) {
				if (sharedInstance == null)
					sharedInstance = new PerspectiveExtensionRegistry();
			}
		}
		return sharedInstance;
	}

	protected PerspectiveExtensionRegistry() {

	}

	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.perspectiveExtension"; //$NON-NLS-1$
	public static final String ELEMENT_PERSPECTIVE_EXTENSION = "perspectiveExtension"; //$NON-NLS-1$
	public static final String ATTRIBUTE_TARGET_ID = "targetID"; //$NON-NLS-1$
	public static final String ELEMENT_CONTRIBUTION_ITEM_SET_ID = "contributionItemSetID"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equals(ELEMENT_PERSPECTIVE_EXTENSION)) 
		{
			String targetID = element.getAttribute(ATTRIBUTE_TARGET_ID);
			if (checkString(targetID)) 
			{
				IConfigurationElement[] children = element.getChildren(ELEMENT_CONTRIBUTION_ITEM_SET_ID);
				for (int i=0; i<children.length; i++) {
					IConfigurationElement childElement = children[i];
					String id = childElement.getAttribute(ATTRIBUTE_ID);
					if (checkString(id)) 
					{
						String extensionPointID = ContributionItemSetRegistry.EXTENSION_POINT_ID;
						Map<String, Collection<String>> extensionPointID2ExtensionIDs = 
							perspectiveID2ExtensionPointID2ExtensionID.get(targetID);
						if (extensionPointID2ExtensionIDs == null)
							extensionPointID2ExtensionIDs = new HashMap<String, Collection<String>>();						
						Collection<String> extensionIDs = extensionPointID2ExtensionIDs.get(extensionPointID);						
						if (extensionIDs == null)
							extensionIDs = new HashSet<String>();
						extensionIDs.add(id);
						extensionPointID2ExtensionIDs.put(extensionPointID, extensionIDs);
						perspectiveID2ExtensionPointID2ExtensionID.put(targetID, extensionPointID2ExtensionIDs);

						Map<String, Collection<String>> perspectiveID2ExtensionIDs = 
							extensionPointID2PerspectiveID2extensionIDs.get(extensionPointID);
						if (perspectiveID2ExtensionIDs == null)
							perspectiveID2ExtensionIDs = new HashMap<String, Collection<String>>();
						Collection<String> extensionIDs2 = perspectiveID2ExtensionIDs.get(targetID);
						if (extensionIDs2 == null)
							extensionIDs2 = new HashSet<String>();
						extensionIDs2.add(id);
						perspectiveID2ExtensionIDs.put(targetID, extensionIDs2);
						extensionPointID2PerspectiveID2extensionIDs.put(extensionPointID, perspectiveID2ExtensionIDs);
					}
				}				
			} else {
				logger.error("targetID is empty!"); //$NON-NLS-1$
			}			
		}
	}

	private Map<String, Map<String, Collection<String>>> perspectiveID2ExtensionPointID2ExtensionID =
		new HashMap<String, Map<String,Collection<String>>>();
	public Map<String, Collection<String>> getExtensionPointID2ExtensionIDs(String perspectiveID) 
	{
		checkProcessing(false);

		if (perspectiveID2ExtensionPointID2ExtensionID.get(perspectiveID) != null)
			return Collections.unmodifiableMap(
					perspectiveID2ExtensionPointID2ExtensionID.get(perspectiveID));
		else
			return Collections.emptyMap();
	}

	private Collection<String> registeredExtensionPointIDs = null;
	public Collection<String> getRegisteredExtensionPointIDs() 
	{
		checkProcessing(false);

		if (registeredExtensionPointIDs == null) 
		{
			registeredExtensionPointIDs = new HashSet<String>();
			Collection<Map<String, Collection<String>>> extensionPointID2ExtensionID = 
				perspectiveID2ExtensionPointID2ExtensionID.values();
			for (Map<String, Collection<String>> map : extensionPointID2ExtensionID) {
				registeredExtensionPointIDs.addAll(map.keySet());
			}
		}
		return registeredExtensionPointIDs;
	}

	private Map<String, Map<String, Collection<String>>> extensionPointID2PerspectiveID2extensionIDs = 
		new HashMap<String, Map<String,Collection<String>>>();	
	public Map<String, Collection<String>> getPerspectiveID2ExtensionIDs(String extensionPointID) 
	{
		checkProcessing(false);

		if (extensionPointID2PerspectiveID2extensionIDs.get(extensionPointID) != null)
			return Collections.unmodifiableMap(
					extensionPointID2PerspectiveID2extensionIDs.get(extensionPointID));
		else
			return Collections.emptyMap();
	}
}
