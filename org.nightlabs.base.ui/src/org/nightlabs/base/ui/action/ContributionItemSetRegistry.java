/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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
package org.nightlabs.base.ui.action;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.action.registry.AbstractActionRegistry;
import org.nightlabs.base.ui.action.registry.ActionVisibilityDecider;
import org.nightlabs.eclipse.extension.EPProcessorException;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ContributionItemSetRegistry
extends AbstractActionRegistry
{
	private static final Logger logger = Logger.getLogger(ContributionItemSetRegistry.class);
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.ui.contributionItemSet"; //$NON-NLS-1$
	public static final String ELEMENT_CONTRIBUTION_ITEM = "contributionItem"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	public static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$

	public ContributionItemSetRegistry(ActionVisibilityDecider actionVisibilityDecider) {
		super(actionVisibilityDecider);
	}

	@Override
	protected Object createActionOrContributionItem(IExtension extension, IConfigurationElement element)
	throws EPProcessorException
	{
		if (element.getName().equals(ELEMENT_CONTRIBUTION_ITEM))
		{
			String id = element.getAttribute(ATTRIBUTE_ID);
			if (checkString(id))
				logger.error("id is empty!"); //$NON-NLS-1$
			
			String name = element.getAttribute(ATTRIBUTE_NAME);
			if (checkString(name))
				logger.error("name is empty!"); //$NON-NLS-1$
			
			String className = element.getAttribute(ATTRIBUTE_CLASS);
			if (checkString(className)) {
				try {
					IXContributionItem contributionItem = (IXContributionItem) element.createExecutableExtension(ATTRIBUTE_CLASS);
					contributionItem.setId(id);
					if (contributionItem instanceof AbstractContributionItem) {
						AbstractContributionItem abstractContributionItem = (AbstractContributionItem) contributionItem;
						abstractContributionItem.setName(name);
					}
					return contributionItem;
				} catch (CoreException e) {
					logger.error("Could not instantiate Class "+className+"!", e); //$NON-NLS-1$ //$NON-NLS-2$
					throw new EPProcessorException("Could not instantiate Class "+className+"!", e); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		return null;
	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	protected String getActionElementName() {
		return ELEMENT_CONTRIBUTION_ITEM;
	}
		
}
