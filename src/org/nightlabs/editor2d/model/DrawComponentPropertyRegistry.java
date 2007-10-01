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
package org.nightlabs.editor2d.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class DrawComponentPropertyRegistry 
extends AbstractEPProcessor
{
	private static final Logger logger = Logger.getLogger(DrawComponentPropertyRegistry.class);
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.editor2d.drawComponentProperty"; //$NON-NLS-1$
	
	public static final String ELEMENT_DRAWCOMPONENT_PROPERTY = "drawComponentProperty"; //$NON-NLS-1$
	public static final String ATTRIBUTE_DRAWCOMPONENT_PROPERTY = "drawComponentProperty";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_DRAWCOMPONENT_CLASS = "drawComponentClass"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ROOT_DRAWCOMPONENT_CLASS = "rootDrawComponentClass"; //$NON-NLS-1$
	
	private static DrawComponentPropertyRegistry sharedInstance;
	public static DrawComponentPropertyRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new DrawComponentPropertyRegistry();
		return sharedInstance;
	}
	
	protected DrawComponentPropertyRegistry() {
		
	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element) 
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_DRAWCOMPONENT_PROPERTY)) 
		{
			String dcClassName = element.getAttribute(ATTRIBUTE_DRAWCOMPONENT_CLASS);			
			DrawComponentProperty dcProperty = null;
			try {
				dcProperty = (DrawComponentProperty) element.createExecutableExtension(ATTRIBUTE_DRAWCOMPONENT_PROPERTY);
			} catch (CoreException e) {
				logger.warn("There occured an error when trying to create the drawComponentProperty "+element.getAttribute(ATTRIBUTE_DRAWCOMPONENT_PROPERTY), e); //$NON-NLS-1$
			}
			
			String rootDrawComponentClassName = element.getAttribute(ATTRIBUTE_ROOT_DRAWCOMPONENT_CLASS);			
			if (checkString(dcClassName) && dcProperty != null && checkString(rootDrawComponentClassName)) 
			{
				Map<String, List<DrawComponentProperty>> dcClass2DrawComponentProperties = rootClass2dcClass2DrawComponentProperties.get(rootDrawComponentClassName);
				if (dcClass2DrawComponentProperties == null)
					dcClass2DrawComponentProperties = new HashMap<String, List<DrawComponentProperty>>();
				
				List<DrawComponentProperty> properties = dcClass2DrawComponentProperties.get(dcClassName);
				if (properties == null) {
					properties = new LinkedList<DrawComponentProperty>();					
				}
				properties.add(dcProperty);
				dcClass2DrawComponentProperties.put(dcClassName, properties);
				
				rootClass2dcClass2DrawComponentProperties.put(rootDrawComponentClassName, dcClass2DrawComponentProperties);
			}
		}
	}

	private Map<String, Map<String, List<DrawComponentProperty>>>  rootClass2dcClass2DrawComponentProperties = 
		new HashMap<String, Map<String, List<DrawComponentProperty>>>();
	
	public Map<String, List<DrawComponentProperty>> getDrawComponentClass2Properties(String rootDrawComponentClass) {
		checkProcessing();
		return rootClass2dcClass2DrawComponentProperties.get(rootDrawComponentClass);
	}

	public List<DrawComponentProperty> getDrawComponentProperty(String rootDrawComponentClass, String drawComponentClass) 
	{
		Map<String, List<DrawComponentProperty>> dcClass2DrawComponentProperties = 
			getDrawComponentClass2Properties(rootDrawComponentClass);
		if (dcClass2DrawComponentProperties != null) {
			return dcClass2DrawComponentProperties.get(drawComponentClass);
		}
		return null;
	}

}
