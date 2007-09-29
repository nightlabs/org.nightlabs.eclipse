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
package org.nightlabs.base.entity.editor.action;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.action.registry.AbstractActionRegistry;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class EntityEditorActionBarContributorRegistry 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.entityEditorActionBarContribution"; //$NON-NLS-1$
	public static final String ELEMENT_EDITOR_CONTRIBUTION = "entityEditorActionBarContribution"; //$NON-NLS-1$
	public static final String ELEMENT_PAGE_CONTRIBUTION = "entityEditorPageContribution"; //$NON-NLS-1$
	public static final String ATTRIBUTE_TARGET_EDITOR_ID = "targetEditorID"; //$NON-NLS-1$
	public static final String ELEMENT_EDITOR_ACTION_BAR_CONTRIBUTION = "editorActionBarContribution"; //$NON-NLS-1$
	public static final String ATTRIBUTE_PAGE_ID = "pageID"; //$NON-NLS-1$
	
	protected EntityEditorActionBarContributorRegistry() {
		super();
	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	private Map<String, Map<String, AbstractActionRegistry>> editorID2PageID2ActionRegistry = 
		new HashMap<String, Map<String, AbstractActionRegistry>>();
	
	public Map<String, AbstractActionRegistry> getPageID2ActionRegistry(String editorID) {
		checkProcessing();
		return editorID2PageID2ActionRegistry.get(editorID);
	}
	
	public AbstractActionRegistry getActionRegistry(String editorID, String pageID) {
		checkProcessing();
		Map<String, AbstractActionRegistry> pageID2ActionRegistry = 
			editorID2PageID2ActionRegistry.get(editorID);
		if (pageID2ActionRegistry != null) {
			return pageID2ActionRegistry.get(pageID);
		}
		return null;
	}

	
	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_EDITOR_CONTRIBUTION)) {
			String targetEditorID = element.getAttribute(ATTRIBUTE_TARGET_EDITOR_ID);
			if (!checkString(targetEditorID))
				throw new EPProcessorException("The attribute targetEditorID must be set!", extension); //$NON-NLS-1$
			IConfigurationElement[] children = element.getChildren();
			for (IConfigurationElement childElement : children) {
				if (childElement.getName().equals(ELEMENT_PAGE_CONTRIBUTION)) {
					String pageID = childElement.getAttribute(ATTRIBUTE_PAGE_ID);
					if (!checkString(pageID))
						throw new EPProcessorException("The attribute pageID must be set!", extension); //$NON-NLS-1$
					Map<String, AbstractActionRegistry> pageID2ActionRegistry = editorID2PageID2ActionRegistry.get(pageID);
					if (pageID2ActionRegistry == null) {
						pageID2ActionRegistry = new HashMap<String, AbstractActionRegistry>();
						editorID2PageID2ActionRegistry.put(targetEditorID, pageID2ActionRegistry);
					}
					IConfigurationElement[] children2 = childElement.getChildren();
					AbstractActionRegistry registry = null;
					for (IConfigurationElement childElement2 : children2) {
						registry = pageID2ActionRegistry.get(pageID);
						if (registry == null) {
							registry = new DefaultEntityEditorActionBarContributor();
							pageID2ActionRegistry.put(pageID, registry);
						}
						registry.processElement(extension, childElement2);						
					}
				}
			}
		}
	}
	
	private static EntityEditorActionBarContributorRegistry sharedInstance;	
	public static EntityEditorActionBarContributorRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new EntityEditorActionBarContributorRegistry();
		return sharedInstance;
	}

}
