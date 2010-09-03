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
package org.nightlabs.eclipse.extension;

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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class RemoveExtensionRegistry extends AbstractEPProcessor
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.ui.removeExtension"; //$NON-NLS-1$

	public static final String ELEMENT_REMOVE_EXTENSION = "removeExtension"; //$NON-NLS-1$
	public static final String ATTRIBUTE_EXTENSION_POINT_ID = "extensionPointID"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ELEMENT_PATH = "elementPath";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_ATTRIBUTE_NAME = "attributeName";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_ATTRIBUTE_PATTERN = "attributePattern"; //$NON-NLS-1$

	private static RemoveExtensionRegistry sharedInstance = null;

	private final Set<IExtension> removedExtensions = new HashSet<IExtension>();
	private final Map<String, List<RemoveExtension>> extensionPointID2RemoveExtensions = new HashMap<String, List<RemoveExtension>>();

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.extension.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.extension.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(final IExtension extension, final IConfigurationElement element) throws Exception
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_REMOVE_EXTENSION))
		{
			final String extensionPointID = element.getAttribute(ATTRIBUTE_EXTENSION_POINT_ID);
			if (!isNonEmpty(extensionPointID)) {
				ExtensionPlugin.logError("attribute extensionPoint must not be null nor empty!"); //$NON-NLS-1$
				return;
			}

			final String elementPath = element.getAttribute(ATTRIBUTE_ELEMENT_PATH);
			if (!isNonEmpty(elementPath)) {
				ExtensionPlugin.logError("attribute elementPath must not be null nor empty!"); //$NON-NLS-1$
				return;
			}

			final String attributeName = element.getAttribute(ATTRIBUTE_ATTRIBUTE_NAME);
			if (!isNonEmpty(attributeName)) {
				ExtensionPlugin.logError("attribute attributeName must not be null nor empty!"); //$NON-NLS-1$
				return;
			}

			final String attributePattern = element.getAttribute(ATTRIBUTE_ATTRIBUTE_PATTERN);
			if (!isNonEmpty(attributePattern)) {
				ExtensionPlugin.logError("attribute attributePattern must not be null nor empty!"); //$NON-NLS-1$
				return;
			}

			final RemoveExtension removeExtension = new RemoveExtension(elementPath, attributeName, attributePattern);
			List<RemoveExtension> removeExtensions = extensionPointID2RemoveExtensions.get(extensionPointID);
			if (removeExtensions == null) {
				removeExtensions = new ArrayList<RemoveExtension>();
			}
			removeExtensions.add(removeExtension);
			extensionPointID2RemoveExtensions.put(extensionPointID, removeExtensions);
		}
	}

	public Map<String, List<RemoveExtension>> getExtensionPointID2RemoveExtensions()
	{
		checkProcessing();
		return extensionPointID2RemoveExtensions;
	}

	private static class RemoveExtension
	{
		private final String elementPath;
		private final String attributeName;
		private final String attributePattern;

		public RemoveExtension(final String elementPath, final String attributeName, final String attributePattern)
		{
			this.attributeName = attributeName;
			this.elementPath = elementPath;
			this.attributePattern = attributePattern;
		}

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
	}

	public void removeRegisteredExtensions()
	{
		final IExtensionRegistry registry = Platform.getExtensionRegistry();

		if(registry == null) {
			return;
		}

		for (final Map.Entry<String, List<RemoveExtension>> entry : getExtensionPointID2RemoveExtensions().entrySet()) {
			final String extensionPointID = entry.getKey();
			final List<RemoveExtension> removeExtensions = entry.getValue();
			final IExtensionPoint extensionPoint = registry.getExtensionPoint(extensionPointID);
			final IExtension[] extensions = extensionPoint.getExtensions();
			// For each extension ...
			for (final IExtension extension : extensions) {
				removeExtensions(registry, extension, removeExtensions);
			}
		}
	}

	private void removeExtensions(final IExtensionRegistry registry, final IExtension extension, final List<RemoveExtension> removeExtensions)
	{
		final IConfigurationElement[] elements = extension.getConfigurationElements();
		// For each member of the extension ...
		for (final IConfigurationElement element : elements) {
			for (final RemoveExtension removeExtension : removeExtensions) {
				final String elementPath = removeExtension.getElementPath();
				final Set<IConfigurationElement> matchingElements = new HashSet<IConfigurationElement>();
				checkElementPath(elementPath, element, matchingElements,
						removeExtension.getAttributeName(), removeExtension.getAttributePattern());
				if (!matchingElements.isEmpty()) {
					for (final IConfigurationElement element2 : matchingElements) {
						final IExtension ext = element2.getDeclaringExtension();
						try {
							if (!removedExtensions.contains(ext)) {
								registry.removeExtension(ext, getMasterToken(registry));
								removedExtensions.add(ext);
							}
						} catch (final Throwable t) {
							ExtensionPlugin.logError("There occured an error while trying to remove the IExtension "+ext.getLabel()+" from the ExtensionRegistry", t); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
		}
	}

	/**
	 * @deprecated is this method in use? Why is it public? Remove deprecated annotation if this is needed somewhere. mklinger
	 */
	@Deprecated
	public static void removeRegistryObject(final org.eclipse.core.internal.registry.RegistryObject registryObject) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		final Field masterTokenField = registry.getClass().getField("masterToken"); //$NON-NLS-1$
		masterTokenField.setAccessible(true);
		final Object masterToken = masterTokenField.get(registry);
		final Method removeRegistryObjectMethod = registry.getClass().getDeclaredMethod("removeObject",  //$NON-NLS-1$
				new Class[] {org.eclipse.core.internal.registry.RegistryObject.class, Boolean.class, Object.class});
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
	 * 
	 * FIXME why is this method public?
	 */
	public static Object getMasterToken(final IExtensionRegistry registry) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		final Field masterTokenField = registry.getClass().getDeclaredField("masterToken"); //$NON-NLS-1$
		masterTokenField.setAccessible(true);
		final Object masterToken = masterTokenField.get(registry);
		return masterToken;
	}

	private void checkElementPath(final String elementPath, final IConfigurationElement element, final Set<IConfigurationElement> elements, final String attributeName, final String attributePattern)
	{
		final Pattern pattern = Pattern.compile("/"); //$NON-NLS-1$
		final String[] splits = pattern.split(elementPath);
		final String element0 = splits[0];
		if (element0.equals(elementPath)) {
			try {
				// element path matches
				if (element.getName().equals(element0)) {
					// attribute name matches
					if (element.getAttribute(attributeName) != null) {
						final String attributeValue = element.getAttribute(attributeName);
						if (Pattern.matches(attributePattern, attributeValue)) {
							// attribute pattern matches
							elements.add(element);
						}
					}
				}
				// necessary since eclipse 3.6 migration
			} catch (InvalidRegistryObjectException e) {
				// TODO Log to eclipse log
				System.out.println("Exception occured while trying to access element name for element "+element);
//				logger.warn("Exception occured while trying to access element name for element "+element, e);
			}
		}
		else if (element0.equals(element.getName())) {
			final String newElementPath = elementPath.substring(element0.length() + 1);
			final IConfigurationElement[] children = element.getChildren();
			for (final IConfigurationElement child : children) {
				checkElementPath(newElementPath, child, elements, attributeName, attributePattern);
			}
		}
	}

	public static RemoveExtensionRegistry sharedInstance()
	{
		if (sharedInstance == null) {
			sharedInstance = new RemoveExtensionRegistry();
		}
		return sharedInstance;
	}

	/**
	 * Test if a string is non null and non-empty. A string is non-empty if
	 * it contains other character than spaces.
	 * @param s the String to check
	 * @return <code>true</code> if the string is neither null nor an empty String - otherwise
	 * 		returns <code>false</code>
	 */
	private static boolean isNonEmpty(final String s)
	{
		return s != null && !s.trim().isEmpty();
	}
}
