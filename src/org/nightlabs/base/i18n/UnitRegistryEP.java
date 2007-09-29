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
package org.nightlabs.base.i18n;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.i18n.unit.IUnit;
import org.nightlabs.i18n.unit.Unit;
import org.nightlabs.i18n.unit.UnitRegistry;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class UnitRegistryEP 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.unit"; //$NON-NLS-1$
	
	public static final String ELEMENT_UNIT_CLASS = "unitClass"; //$NON-NLS-1$
	public static final String ATTRIBUTE_UNIT = "unit"; //$NON-NLS-1$
	
	public static final String ELEMENT_UNIT = "unit"; //$NON-NLS-1$
	public static final String ATTRIBUTE_UNIT_ID = "unitID";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_NAME = "name";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_FACTOR = "factor";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_SYMBOL = "symbol"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CONTEXT = "context"; //$NON-NLS-1$
	
	protected UnitRegistryEP() {

	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_UNIT)) 
		{
			String unitID = element.getAttribute(ATTRIBUTE_UNIT_ID);
			if (!checkString(unitID))
				throw new IllegalArgumentException("unitID must not be null nor empty!"); //$NON-NLS-1$
			
			String name = element.getAttribute(ATTRIBUTE_NAME);
			if (!checkString(name))
				throw new IllegalArgumentException("unit name must not be null nor empty!"); //$NON-NLS-1$
 
			double factor;
			try {
				factor = Double.parseDouble(element.getAttribute(ATTRIBUTE_FACTOR));				
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("factor must be a double!"); //$NON-NLS-1$
			}			
			
			String symbol = element.getAttribute(ATTRIBUTE_SYMBOL);
			if (!checkString(symbol))
				throw new IllegalArgumentException("unit symbol must not be null nor empty!"); //$NON-NLS-1$
			
			String context = element.getAttribute(ATTRIBUTE_CONTEXT);
			if (context == null || context.equals("")) //$NON-NLS-1$
				context = UnitRegistry.GLOBAL_CONTEXT;
			
			IUnit unit = new Unit(unitID, name, symbol, factor);
			getUnitRegistry().addUnit(unit, context);
		}
		if (element.getName().equalsIgnoreCase(ELEMENT_UNIT_CLASS)) 
		{
			String context = element.getAttribute(ATTRIBUTE_CONTEXT);
			if (context == null || context.equals("")) //$NON-NLS-1$
				context = UnitRegistry.GLOBAL_CONTEXT;
			
			try {
				IUnit unit = (IUnit) element.createExecutableExtension(ATTRIBUTE_UNIT);
				getUnitRegistry().addUnit(unit, context);				
			} catch (CoreException ce) {
				throw new EPProcessorException(ce); 
			}
		}
	}

	private UnitRegistry unitRegistry = null;
	public UnitRegistry getUnitRegistry() 
	{
		if (unitRegistry == null) {
			unitRegistry = UnitRegistry.sharedInstance();			
		}
		return unitRegistry;
	}	
	
	private static UnitRegistryEP registry = null;
	public static UnitRegistryEP sharedInstance() {
		if (registry == null) {
			registry = new UnitRegistryEP();
			registry.checkProcessing();
		}		
		return registry;
	}
}
