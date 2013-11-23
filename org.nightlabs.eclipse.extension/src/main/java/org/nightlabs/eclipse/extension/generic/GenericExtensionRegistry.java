package org.nightlabs.eclipse.extension.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.nightlabs.eclipse.extension.ExtensionPlugin;

/**
 * Generic extension point registry. Handles extensions of type {@link IdExtension}.
 * Optional support for extension classes implementing {@link NameExtension}, {@link AttributesExtension}
 * and {@link PriorityExtension}.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class GenericExtensionRegistry<ExtensionType>
{
	private final String extensionPointId;
	private final String extensionElementName;
	private Map<String, List<ExtensionType>> extensionsById;
	private final boolean allowMultiple;
	private final String bundleSymbolicName;
	private final ILog log;
	private IExtensionRegistry extensionRegistry;

	/**
	 * Create a new GenericExtensionRegistry instance.
	 * @param extensionPointId The extension point id
	 * @param extensionElementName The extension element name. This
	 * 		element must contain the id and class attributes.
	 */
	public GenericExtensionRegistry(final String extensionPointId, final String extensionElementName)
	{
		this(extensionPointId, extensionElementName, true, null, null);
	}

	/**
	 * Create a new GenericExtensionRegistry instance.
	 * @param extensionPointId The extension point id
	 * @param extensionElementName The extension element name. This
	 * 		element must contain the id and class attributes.
	 */
	public GenericExtensionRegistry(final String extensionPointId, final String extensionElementName, final boolean allowMultiple)
	{
		this(extensionPointId, extensionElementName, allowMultiple, null, null);
	}

	/**
	 * Create a new GenericExtensionRegistry instance.
	 * @param extensionPointId The extension point id
	 * @param extensionElementName The extension element name. This
	 * 		element must contain the id and class attributes.
	 * @param plugin The calling plugin (used for logging - optional)
	 */
	public GenericExtensionRegistry(final String extensionPointId, final String extensionElementName, final boolean allowMultiple, final Plugin plugin)
	{
		this(extensionPointId, extensionElementName, allowMultiple, plugin == null ? null : plugin.getBundle().getSymbolicName(), plugin == null ? null : plugin.getLog());
	}

	/**
	 * Create a new GenericExtensionRegistry instance.
	 * @param extensionPointId The extension point id
	 * @param extensionElementName The extension element name. This
	 * 		element must contain the id and class attributes.
	 * @param bundleSymbolicName The calling bundle symbolic name (used for logging - optional)
	 * @param log The log to use (optional)
	 */
	public GenericExtensionRegistry(final String extensionPointId, final String extensionElementName, final boolean allowMultiple, final String bundleSymbolicName, final ILog log)
	{
		this.extensionPointId = extensionPointId;
		this.extensionElementName = extensionElementName;
		this.allowMultiple = allowMultiple;
		this.bundleSymbolicName = bundleSymbolicName;
		this.log = log;
	}

	@SuppressWarnings("unchecked")
	private ExtensionType createExecutableExtension(final IConfigurationElement element) throws CoreException
	{
		return (ExtensionType) element.createExecutableExtension("class");
	}

	public void logError(final String message, final Throwable e)
	{
		if(log != null && bundleSymbolicName != null) {
			log.log(new Status(IStatus.ERROR, bundleSymbolicName, message, e));
		} else {
			ExtensionPlugin.logError(message, e);
		}
	}

	private void setId(final ExtensionType extension, final String id) {
		if(extension instanceof IdExtension) {
			((IdExtension)extension).setId(id);
		}
		//		try {
		//			Method method = extension.getClass().getMethod("setName", new Class<?>[] { String.class });
		//			method.invoke(extension, name);
		//		} catch(Exception e) {
		//			// ignore.
		//		}
	}

	private void setName(final ExtensionType extension, final String name) {
		if(extension instanceof NameExtension) {
			((NameExtension)extension).setName(name);
		}
		//		try {
		//			Method method = extension.getClass().getMethod("setName", new Class<?>[] { String.class });
		//			method.invoke(extension, name);
		//		} catch(Exception e) {
		//			// ignore.
		//		}
	}

	private void setAttributes(final ExtensionType extension, final Map<String, String> attributes) {
		if(extension instanceof AttributesExtension) {
			((AttributesExtension)extension).setAttributes(attributes);
		}
		//		try {
		//			Method method = extension.getClass().getMethod("setAttributes", new Class<?>[] { Map.class });
		//			method.invoke(extension, attributes);
		//		} catch(Exception e) {
		//			// ignore.
		//		}
	}

	private void setPriority(final ExtensionType extension, final int priority) {
		if(extension instanceof PriorityExtension) {
			((PriorityExtension)extension).setPriority(priority);
		}
	}

	public void initializeLazy()
	{
		initialize(false);
	}

	private static class ExtensionComparator implements Comparator<Object>
	{
		@Override
		public int compare(final Object o1, final Object o2)
		{
			if(o1 instanceof PriorityExtension && o2 instanceof PriorityExtension) {
				return ((PriorityExtension)o1).getPriority() - ((PriorityExtension)o2).getPriority();
			} else if(o1 instanceof PriorityExtension) {
				return -1;
			} else if(o2 instanceof PriorityExtension) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private void initialize(final boolean reinitialize)
	{
		if(!reinitialize && extensionsById != null) {
			return;
		}
		extensionsById = new HashMap<String, List<ExtensionType>>();
		final IExtensionRegistry registry = getExtensionRegistry();
		final IConfigurationElement[] elements = registry.getConfigurationElementsFor(extensionPointId);
		for (final IConfigurationElement element : elements) {
			if(extensionElementName.equals(element.getName())) {
				String id = element.getAttribute("id");
				if(id == null || id.isEmpty()) {
					id = element.getAttribute("class");
				}
				ExtensionType extension;
				try {
					extension = createExecutableExtension(element);
				} catch (final CoreException e) {
					if(log != null) {
						logError("Error creating executable extension for "+extensionPointId+": "+id, e);
					}
					continue;
				}
				setId(extension, id);
				final String name = element.getAttribute("name");
				if(name != null && !name.isEmpty()) {
					setName(extension, name);
				}
				final String priorityStr = element.getAttribute("priority");
				if(priorityStr != null && !priorityStr.isEmpty()) {
					try {
						final int priority = Integer.parseInt(priorityStr);
						setPriority(extension, priority);
					} catch(final NumberFormatException e) {
						logError("Invalid prioroty for " + extensionPointId + ": " + id, e);
					}
				}
				final IConfigurationElement[] children = element.getChildren("attribute");
				if(children != null && children.length > 0) {
					final Map<String, String> attributes = new HashMap<String, String>();
					for (final IConfigurationElement child : children) {
						final String attributeName = child.getAttribute("name");
						if(attributeName != null && !attributeName.trim().isEmpty()) {
							String attributeValue = child.getAttribute("value");
							if(attributeValue == null) {
								attributeValue = child.getValue();
							}
							if(attributeValue != null) {
								attributes.put(attributeName, attributeValue);
							}
						}
					}
					if(!attributes.isEmpty()) {
						setAttributes(extension, attributes);
					}
				}
				if(allowMultiple) {
					if(!extensionsById.containsKey(id)) {
						extensionsById.put(id, new ArrayList<ExtensionType>(2));
					}
					extensionsById.get(id).add(extension);
				} else {
					extensionsById.put(id, Collections.singletonList(extension));
				}
			}
		}
		if(allowMultiple) {
			// sort by priority
			for(final List<ExtensionType> extensions : extensionsById.values()) {
				if(extensions.size() > 1) {
					Collections.sort(extensions, new ExtensionComparator());
				}
			}
		}
	}

	/**
	 * Get all extensions for the specified id ordered by priority.
	 * If no extensions are known for the given id, return an empty
	 * unmodifiable list.
	 * @param id The id
	 * @return The list of extensions
	 */
	public List<ExtensionType> getExtensionsById(final String id)
	{
		initializeLazy();
		List<ExtensionType> extensions = extensionsById.get(id);
		if(extensions == null) {
			extensions = Collections.emptyList();
		}
		return extensions;
	}

	/**
	 * Get the extension with the highest priority for the given id.
	 * @param id The id
	 * @return The extension or <code>null</code> if no extension
	 * 		is known for the given id
	 */
	public ExtensionType getExtensionById(final String id)
	{
		final List<ExtensionType> extensions = getExtensionsById(id);
		if(extensions.isEmpty()) {
			return null;
		} else {
			return extensions.iterator().next();
		}
	}

	/**
	 * Get all extensions that are compatible to the given type.
	 * ordered by priority.
	 * @param <T> The type
	 * @param type The type
	 * @return The list of extensions or an empty list if no compatible
	 * 		extension are known
	 */
	public <T> List<T> getExtensionsByImplementedType(final Class<T> type) {
		final List<T> extensionsByType = filterByType(getAllExtensions(), type);
		if(extensionsByType.size() > 1) {
			Collections.sort(extensionsByType, new ExtensionComparator());
		}
		return extensionsByType;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> filterByType(final Collection<ExtensionType> allExtensions, final Class<T> type)
	{
		final List<T> extensionsByType = new ArrayList<T>(Math.min(10, allExtensions.size()));
		for (final ExtensionType extension : allExtensions) {
			if(type.isAssignableFrom(extension.getClass())) {
				extensionsByType.add((T)extension);
			}
		}
		return extensionsByType;
	}

	/**
	 * Get the extensions with the highest priority that is compatible to the given type.
	 * ordered by priority.
	 * @param <T> The type
	 * @param type The type
	 * @return The extension or <code>null</code> if no such extension is known
	 */
	public <T> T getExtensionByImplementedType(final Class<T> type) {
		final List<T> extensionsByType = getExtensionsByImplementedType(type);
		if(extensionsByType.isEmpty()) {
			return null;
		} else {
			return extensionsByType.iterator().next();
		}
	}

	/**
	 * Get all extensions that are compatible to the given type.
	 * ordered by priority.
	 * @param <T> The type
	 * @param id The id
	 * @param type The type
	 * @return The list of extensions or an empty list if no compatible
	 * 		extension are known
	 */
	public <T> List<T> getExtensions(final String id, final Class<T> type) {
		final List<T> extensionsByType = filterByType(getExtensionsById(id), type);
		if(extensionsByType.size() > 1) {
			Collections.sort(extensionsByType, new ExtensionComparator());
		}
		return extensionsByType;
	}

	/**
	 * Get the extensions with the highest priority that is compatible to the given type.
	 * ordered by priority.
	 * @param <T> The type
	 * @param type The type
	 * @param id The id
	 * @return The extension or <code>null</code> if no such extension is known
	 */
	public <T> T getExtension(final String id, final Class<T> type) {
		final List<T> extensions = getExtensions(id, type);
		if(extensions.isEmpty()) {
			return null;
		} else {
			return extensions.iterator().next();
		}
	}

	/**
	 * Get all known extensions.
	 * @return The list of extensions or an empty list if no extensions
	 * 		are known
	 */
	public Collection<ExtensionType> getAllExtensions()
	{
		initializeLazy();
		final Collection<ExtensionType> allExtensions = new ArrayList<ExtensionType>(extensionsById.size() * 2);
		for(final List<ExtensionType> extensions : extensionsById.values()) {
			allExtensions.addAll(extensions);
		}
		return allExtensions;
	}

	/**
	 * Get the extension registry This may be an injected registry or the default
	 * platform registy if no registry was injected.
	 * @return the extension registry
	 */
	private IExtensionRegistry getExtensionRegistry()
	{
		if(extensionRegistry != null) {
			return extensionRegistry;
		} else {
			return Platform.getExtensionRegistry();
		}
	}

	/**
	 * Set the extension registry. This extension registry will always be used
	 * for extension lookup. This method is intended for testing.
	 * @param extensionRegistry the extension registry to set
	 */
	public void setExtensionRegistry(final IExtensionRegistry extensionRegistry)
	{
		this.extensionRegistry = extensionRegistry;
	}
}
