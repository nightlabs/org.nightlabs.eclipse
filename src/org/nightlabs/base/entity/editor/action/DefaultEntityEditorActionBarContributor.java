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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.action.registry.AbstractActionRegistry;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DefaultEntityEditorActionBarContributor 
extends AbstractActionRegistry 
{
	public DefaultEntityEditorActionBarContributor() {
	}

	@Override
	protected Object createActionOrContributionItem(
			IExtension extension, IConfigurationElement element) 
	throws EPProcessorException 
	{
		try {
			return element.createExecutableExtension("class"); //$NON-NLS-1$
		} catch (CoreException e) {
			throw new EPProcessorException(e.getMessage(), extension, e);
		}
	}
	
	@Override
	public String getExtensionPointID() {
		return DefaultEntityEditorActionBarContributor.class.getName();
	}

	@Override
	protected String getActionElementName() {
		return EntityEditorActionBarContributorRegistry.ELEMENT_EDITOR_ACTION_BAR_CONTRIBUTION;
	}	
}
