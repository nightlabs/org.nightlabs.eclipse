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
package org.nightlabs.base.print.page;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.base.i18n.UnitRegistryEP;
import org.nightlabs.i18n.unit.IUnit;
import org.nightlabs.i18n.unit.UnitRegistry;
import org.nightlabs.print.page.IPredefinedPage;
import org.nightlabs.print.page.PredefinedPage;
import org.nightlabs.print.page.PredefinedPageRegistry;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PredefinedPageEP 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.predefinedPage"; //$NON-NLS-1$
	
	public static final String ELEMENT_PAGE_CLASS = "pageClass"; //$NON-NLS-1$
	public static final String ATTRIBUTE_PAGE = "page"; //$NON-NLS-1$
	
	public static final String ELEMENT_PAGE = "page"; //$NON-NLS-1$
	public static final String ATTRIBUTE_PAGE_ID = "pageID";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_NAME = "name";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_WIDTH = "width";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_HEIGHT = "height";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_UNIT_ID = "unitID";	 //$NON-NLS-1$
	
	protected PredefinedPageEP() {
	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_PAGE)) 
		{
			String name = element.getAttribute(ATTRIBUTE_NAME);
			if (!checkString(name))
				throw new IllegalArgumentException("page name must not be null nor empty!"); //$NON-NLS-1$
			
			String pageID = element.getAttribute(ATTRIBUTE_PAGE_ID);
			if (!checkString(pageID))
				throw new IllegalArgumentException("pageID must not be null nor empty!"); //$NON-NLS-1$
				
			double width;
			try {
				width = Double.parseDouble(element.getAttribute(ATTRIBUTE_WIDTH));				
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("width must be a double!"); //$NON-NLS-1$
			}

			double height;
			try {
				height = Double.parseDouble(element.getAttribute(ATTRIBUTE_HEIGHT));				
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("height must be a double!"); //$NON-NLS-1$
			}
			
			String unitID = element.getAttribute(ATTRIBUTE_UNIT_ID);
			if (!checkString(unitID))
				throw new IllegalArgumentException("unitID must not be null nor empty!"); //$NON-NLS-1$
			
			IUnit unit = getUnitRegistry().getUnit(unitID);
			if (unit == null)
				throw new IllegalArgumentException("There exists no unit with the unitID "+unitID); //$NON-NLS-1$
			
			IPredefinedPage page = new PredefinedPage(pageID, name, width, height, unit);
			getPageRegistry().addPredefinedPage(page);							 
		}
		if (element.getName().equalsIgnoreCase(ELEMENT_PAGE_CLASS)) 
		{
			try {
				IPredefinedPage page = (IPredefinedPage) element.createExecutableExtension(ATTRIBUTE_PAGE);
				getPageRegistry().addPredefinedPage(page);				
			} catch (CoreException ce) {
				throw new EPProcessorException(ce); 
			}
		}
	}

	private PredefinedPageRegistry pageRegistry = null;
	public PredefinedPageRegistry getPageRegistry() 
	{
		if (pageRegistry == null) {
			pageRegistry = PredefinedPageRegistry .sharedInstance();			
		}
		return pageRegistry;
	}	
	
	private UnitRegistry getUnitRegistry() 
	{
		return UnitRegistryEP.sharedInstance().getUnitRegistry();			
	}
	
	private static PredefinedPageEP registry = null;
	public static PredefinedPageEP sharedInstance() {
		if (registry == null) {
			registry = new PredefinedPageEP();
			registry.checkProcessing();
		}
		return registry;
	}
}
