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
package org.nightlabs.base.extensionpoint;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.internal.registry.RegistryObject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class RemoveExtensionRegistry 
extends AbstractEPProcessor 
{
	private static final Logger logger = Logger.getLogger(RemoveExtensionRegistry.class);
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.removeExtension"; //$NON-NLS-1$

	public static final String ELEMENT_REMOVE_EXTENSION = "removeExtension"; //$NON-NLS-1$
	public static final String ATTRIBUTE_EXTENSION_POINT_ID = "extensionPointID"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ELEMENT_PATH = "elementPath";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_ATTRIBUTE_NAME = "attributeName";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_ATTRIBUTE_PATTERN = "attributePattern"; //$NON-NLS-1$
	
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_REMOVE_EXTENSION)) 
		{
			String extensionPointID = element.getAttribute(ATTRIBUTE_EXTENSION_POINT_ID);
			if (!checkString(extensionPointID)) {
				logger.error("attribute extensionPoint must not be null nor empty!"); //$NON-NLS-1$
				return;
			}
			
			String elementPath = element.getAttribute(ATTRIBUTE_ELEMENT_PATH);
			if (!checkString(elementPath)) {
				logger.error("attribute elementPath must not be null nor empty!"); //$NON-NLS-1$
				return;
			}				
			
			String attributeName = element.getAttribute(ATTRIBUTE_ATTRIBUTE_NAME);
			if (!checkString(attributeName)) {
				logger.error("attribute attributeName must not be null nor empty!"); //$NON-NLS-1$
				return;
			}
			
			String attributePattern = element.getAttribute(ATTRIBUTE_ATTRIBUTE_PATTERN);
			if (!checkString(attributePattern)) {
				logger.error("attribute attributePattern must not be null nor empty!"); //$NON-NLS-1$
				return;
			}
			
			RemoveExtension removeExtension = new RemoveExtension(extensionPointID, elementPath,
					attributeName, attributePattern);
			List<RemoveExtension> removeExtensions = extensionPointID2RemoveExtensions.get(extensionPointID);
			if (removeExtensions == null)
				removeExtensions = new ArrayList<RemoveExtension>();
			removeExtensions.add(removeExtension);
			extensionPointID2RemoveExtensions.put(extensionPointID, removeExtensions);
		}
	}

	private Map<String, List<RemoveExtension>> extensionPointID2RemoveExtensions = new HashMap<String, List<RemoveExtension>>();
	public Map<String, List<RemoveExtension>> getExtensionPointID2RemoveExtensions() 
	{
		checkProcessing();
		return extensionPointID2RemoveExtensions;
	}	
	
	class RemoveExtension 
	{
		public RemoveExtension(String extensionPointID, String elementPath, 
				String attributeName, String attributePattern)
		{
			this.extensionPointID = extensionPointID;
			this.attributeName = attributeName;
			this.elementPath = elementPath;
			this.attributePattern = attributePattern;
		}
		
		private String extensionPointID;
		private String elementPath;
		private String attributeName;
		private String attributePattern;
		
		/**
		 * @return the attributeName
		 */
		public String getAttributeName() {
			return attributeName;
		}
		/**
		 * @return the attributePattern
		 */
		public String getAttributePattern() {
			return attributePattern;
		}
		/**
		 * @return the elementPath
		 */
		public String getElementPath() {
			return elementPath;
		}
		/**
		 * @return the extensionPointID
		 */
		public String getExtensionPointID() {
			return extensionPointID;
		}						
	}
	
	public void removeRegisteredExtensions() 
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		
		if (registry != null) 
		{
			for (Map.Entry<String, List<RemoveExtension>> entry : getExtensionPointID2RemoveExtensions().entrySet()) {
				String extensionPointID = entry.getKey();
				IExtensionPoint extensionPoint = registry.getExtensionPoint(extensionPointID);
				IExtension[] extensions = extensionPoint.getExtensions();
				// For each extension ...
				for (int i = 0; i < extensions.length; i++) {           
					IExtension extension = extensions[i];
					IConfigurationElement[] elements = extension.getConfigurationElements();
					// For each member of the extension ...
					for (int j = 0; j < elements.length; j++) {
						IConfigurationElement element = elements[j];
						List<RemoveExtension> removeExtensions = entry.getValue();
						for (RemoveExtension removeExtension : removeExtensions) {
							String elementPath = removeExtension.getElementPath();
							Set<IConfigurationElement> matchingElements = new HashSet<IConfigurationElement>();
							checkElementPath(elementPath, element, matchingElements, 
									removeExtension.getAttributeName(), removeExtension.getAttributePattern());
							if (!matchingElements.isEmpty()) {
								for (IConfigurationElement element2 : matchingElements) {
									IExtension ext = element2.getDeclaringExtension();
									try {
										if (!removedExtensions.contains(ext)) {
											registry.removeExtension(ext, getMasterToken((ExtensionRegistry)registry));
											removedExtensions.add(ext);											
										}
									} catch (Throwable t) {
										logger.error("There occured an error while trying to remove the IExtension "+ext.getLabel()+" from the ExtensionRegistry"); //$NON-NLS-1$ //$NON-NLS-2$
										t.printStackTrace();
									}										
//									if (element2 instanceof RegistryObject) {
//										try {
//											removeRegistryObject((RegistryObject)element2);
//										} catch (Throwable t) {
//											logger.error("There occured an error while trying to remove the IConfigurationElement "+element.getName()+" from the ExtensionRegistry");
//											t.printStackTrace();
//										}
//									}
								}
							}
						}						
					}
				}				
			}
		}
	}	
	
	private Set<IExtension> removedExtensions = new HashSet<IExtension>();
	
	public static void removeRegistryObject(RegistryObject registryObject) 
	throws SecurityException, NoSuchFieldException, 
		IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		Field masterTokenField = registry.getClass().getField("masterToken"); //$NON-NLS-1$
		masterTokenField.setAccessible(true);
		Object masterToken = masterTokenField.get(registry);
		Method removeRegistryObjectMethod = registry.getClass().getDeclaredMethod("removeObject",  //$NON-NLS-1$
				new Class[] {RegistryObject.class, Boolean.class, Object.class});
		removeRegistryObjectMethod.invoke(registry, new Object[] {registryObject, false, masterToken});
	}
	
	/**
	 * returns the masterToken field from the ExtensionRegistry by reflection
	 * 
	 * @param registry the ExtensionRegistry
	 * @return the masterToken field from the given ExtensionRegistry, which is necessary to call
	 * registry.removeExtension(IExtension, Object token)
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Object getMasterToken(ExtensionRegistry registry) 
	throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException 
	{
		Field masterTokenField = registry.getClass().getDeclaredField("masterToken"); //$NON-NLS-1$
		masterTokenField.setAccessible(true);
		Object masterToken = masterTokenField.get(registry);
		return masterToken;
	}	
	
	private void checkElementPath(String elementPath, IConfigurationElement element, 
			Set<IConfigurationElement> elements, String attributeName, String attributePattern) 
	{
		Pattern pattern = Pattern.compile("/"); //$NON-NLS-1$
		String[] splits = pattern.split(elementPath);
		String element0 = splits[0];
		if (element0.equals(elementPath)) {
			// element path matches
			if (element.getName().equals(element0)) {
				// attribute name matches
				if (element.getAttribute(attributeName) != null) {
					String attributeValue = element.getAttribute(attributeName);
					Pattern attrPattern = Pattern.compile(attributePattern);
					if (attrPattern.matches(attributePattern, attributeValue)) {
						// attribute pattern matches
						elements.add(element);
					}
				}
			}
		}
		else if (element0.equals(element.getName())) {
			String newElementPath = elementPath.substring(element0.length() + 1);
			IConfigurationElement[] children = element.getChildren();
			for (IConfigurationElement child : children) {
				checkElementPath(newElementPath, child, elements, attributeName, attributePattern);
			}
		}		
	}
	
	private static RemoveExtensionRegistry sharedInstance = null;
	public static RemoveExtensionRegistry sharedInstance() 
	{
		if (sharedInstance == null)
			sharedInstance = new RemoveExtensionRegistry();
		
		return sharedInstance;
	}
}
