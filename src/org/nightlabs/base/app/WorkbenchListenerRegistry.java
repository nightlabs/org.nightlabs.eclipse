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
package org.nightlabs.base.app;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class WorkbenchListenerRegistry
extends AbstractEPProcessor
{
	private static final Logger logger = Logger.getLogger(WorkbenchListenerRegistry.class);
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.workbenchListener"; //$NON-NLS-1$
	public static final String ELEMENT_WORKBENCH_LISTENER = "workbenchListener"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
	
	private static WorkbenchListenerRegistry sharedInstance;
	public static WorkbenchListenerRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (WorkbenchListenerRegistry.class) {
				if (sharedInstance == null)
					sharedInstance = new WorkbenchListenerRegistry();
			}
		}
		return sharedInstance;
	}
	
	protected WorkbenchListenerRegistry() {
	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_WORKBENCH_LISTENER)) {
			String className = element.getAttribute(ATTRIBUTE_CLASS);
			if (checkString(className)) {
				IWorkbenchListener listener;
				try {
					listener = (IWorkbenchListener) element.createExecutableExtension(ATTRIBUTE_CLASS);
					listeners.add(listener);
				} catch (CoreException e) {
					logger.error("Could not create IWorkbenchListener "+className); //$NON-NLS-1$
				}
			}
		}
	}

	private Set<IWorkbenchListener> listeners = new HashSet<IWorkbenchListener>();
	public Set<IWorkbenchListener> getListener() {
		checkProcessing();
		return listeners;
	}
}
