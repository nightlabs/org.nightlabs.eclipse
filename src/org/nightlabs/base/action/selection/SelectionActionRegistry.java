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

package org.nightlabs.base.action.selection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.action.registry.AbstractActionRegistry;
import org.nightlabs.base.extensionpoint.EPProcessorException;

public class SelectionActionRegistry 
extends AbstractActionRegistry
{
	private static SelectionActionRegistry _sharedInstance;

	private static boolean initializingSharedInstance = false;
	public static synchronized SelectionActionRegistry sharedInstance()
	throws EPProcessorException
	{
		if (initializingSharedInstance)
			throw new IllegalStateException("Circular call to the method sharedInstance() during initialization!"); //$NON-NLS-1$

		if (_sharedInstance == null) {
			initializingSharedInstance = true;
			try {
				_sharedInstance = new SelectionActionRegistry();
				_sharedInstance.process();
			} finally {
				initializingSharedInstance = false;
			}
		}

		return _sharedInstance;
	}	
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.selectionActionRegistry";	 //$NON-NLS-1$
	private static final String ATTRIBUTE_NAME_ACTION_CLASS = "class";	 //$NON-NLS-1$
	
	public SelectionActionRegistry() {
		super();
	}

	protected Object createActionOrContributionItem(IExtension extension, IConfigurationElement element) 
	throws EPProcessorException 
	{
		String className = element.getAttribute(ATTRIBUTE_NAME_ACTION_CLASS);
		if (className == null || "".equals(className)) //$NON-NLS-1$
			return null;

		SelectionAction res;
		try {
			res = (SelectionAction) element.createExecutableExtension(ATTRIBUTE_NAME_ACTION_CLASS);
		} catch (CoreException e) {
			throw new EPProcessorException(e);
		}
		return res;
	}

//	public static final String SELECTION_ZONE = "Selection Zone"; // This was not used (I didn't find any references) - therefore I removed it!
// *** Furthermore, this is a BAD NAME for a selection-zone! It should contain a fully qualified class/package name! ***

//	protected void initAction(IAction _action, IExtension extension, IConfigurationElement element) throws EPProcessorException
//	{
//		SelectionAction action = (SelectionAction) _action;
//		action.init(SELECTION_ZONE, Object.class, "SelectionAction");
//	}	
	
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	protected String getActionElementName()
	{
		return "selectionAction"; //$NON-NLS-1$
	}	
}
