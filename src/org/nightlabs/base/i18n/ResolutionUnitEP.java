/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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
import org.nightlabs.i18n.unit.UnitRegistry;
import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.i18n.unit.resolution.ResolutionUnit;
import org.nightlabs.i18n.unit.resolution.ResolutionUnitRegistry;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ResolutionUnitEP 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.resolutionUnit"; //$NON-NLS-1$
	
	public static final String ELEMENT_UNIT_CLASS = "resolutionUnitClass"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
	
	public static final String ELEMENT_UNIT = "resolutionUnit"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ID = "id";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_NAME = "name";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_UNIT_ID = "unitID"; //$NON-NLS-1$
	
	private static ResolutionUnitEP resolutionUnitEP = null;
	public static ResolutionUnitEP sharedInstance() {
		if (resolutionUnitEP == null) {
			resolutionUnitEP = new ResolutionUnitEP();
			resolutionUnitEP.checkProcessing();
		}
		return resolutionUnitEP;
	}
	
	protected ResolutionUnitEP() {
		
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
			String id = element.getAttribute(ATTRIBUTE_ID);
			if (!checkString(id))
				throw new IllegalArgumentException("resolutionUnitID must not be null nor empty!"); //$NON-NLS-1$
			
			String name = element.getAttribute(ATTRIBUTE_NAME);
			if (!checkString(name))
				throw new IllegalArgumentException("resolutionUnit name must not be null nor empty!"); //$NON-NLS-1$
 
			String unitID = element.getAttribute(ATTRIBUTE_UNIT_ID);
			if (!checkString(unitID))
				throw new IllegalArgumentException("unitID must not be null nor empty!"); //$NON-NLS-1$
			
			IUnit unit = getUnitRegistry().getUnit(unitID);
			if (unit == null)
				throw new IllegalArgumentException("There is no unit registered with unitID "+unitID); //$NON-NLS-1$
				
			IResolutionUnit resUnit = new ResolutionUnit(id, name, unit);
			getResolutionUnitRegistry().addResolutionUnit(resUnit);
		}
		if (element.getName().equalsIgnoreCase(ELEMENT_UNIT_CLASS)) 
		{
			try {
				IResolutionUnit resUnit = (IResolutionUnit) element.createExecutableExtension(ATTRIBUTE_CLASS);
				getResolutionUnitRegistry().addResolutionUnit(resUnit);
			} catch (CoreException ce) {
				throw new EPProcessorException(ce); 
			}
		}
	}
	
	public ResolutionUnitRegistry getResolutionUnitRegistry() {
		return ResolutionUnitRegistry.sharedInstance();
	}
	
	private UnitRegistry getUnitRegistry() {
		return UnitRegistryEP.sharedInstance().getUnitRegistry();			
	}
}
