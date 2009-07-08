package org.nightlabs.tableprovider.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;


/**
 * Registry for extension point org.nightlabs.tableprovider.ui.
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 */
public class TableProviderRegistry
extends AbstractEPProcessor
{
	private static TableProviderRegistry _sharedInstance;

	private static boolean initializingSharedInstance = false;
	public static synchronized TableProviderRegistry sharedInstance()
	{
		if (initializingSharedInstance)
			throw new IllegalStateException("Circular call to the method sharedInstance() during initialization!"); //$NON-NLS-1$

		if (_sharedInstance == null) {
			initializingSharedInstance = true;
			try {
				_sharedInstance = new TableProviderRegistry();
				_sharedInstance.process();
			} finally {
				initializingSharedInstance = false;
			}
		}

		return _sharedInstance;
	}

	public static final String EXTENSION_POINT_ID = "org.nightlabs.tableprovider.ui";	 //$NON-NLS-1$
	public static final String ELEMENT_TABLE_PROVIDER = "tableprovider";   //$NON-NLS-1$
	public static final String ATTRIBUTE_FACTORY_CLASS = "factoryClass";   //$NON-NLS-1$
	public static final String ATTRIBUTE_ELEMENT_CLASS = "elementClass";   //$NON-NLS-1$
	public static final String ATTRIBUTE_SCOPE_CLASS = "scopeClass";   //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(TableProviderRegistry.class);

//	private Map<String, Map<String, TableProviderFactory>> element2Scope2Factory =
//		new HashMap<String, Map<String, TableProviderFactory>>();
	private Map<String, Map<String, List<TableProviderFactory>>> element2Scope2Factory =
		new HashMap<String, Map<String, List<TableProviderFactory>>>();

	private TableProviderRegistry() {}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception
	{
		if (element.getName().equals(ELEMENT_TABLE_PROVIDER)) {
			TableProviderFactory factory = null;
			try {
				factory = (TableProviderFactory) element.createExecutableExtension(ATTRIBUTE_FACTORY_CLASS);
			} catch (Exception e) {
				logger.error("Could not instantiate table provider factory with name "+element.getAttribute(ATTRIBUTE_FACTORY_CLASS));
			}
			String elementClassName = element.getAttribute(ATTRIBUTE_ELEMENT_CLASS);
			String scopeClassName = element.getAttribute(ATTRIBUTE_SCOPE_CLASS);

			if (factory != null && elementClassName != null) {
				Map<String, List<TableProviderFactory>> scope2Factories = element2Scope2Factory.get(elementClassName);
				if (scope2Factories == null) {
					scope2Factories = new HashMap<String, List<TableProviderFactory>>();
				}
				if (scopeClassName != null) {
					List<TableProviderFactory> factories = scope2Factories.get(scopeClassName);
					if (factories == null) {
						factories = new ArrayList<TableProviderFactory>();
					}
					factories.add(factory);
					scope2Factories.put(scopeClassName, factories);
				}
				element2Scope2Factory.put(elementClassName, scope2Factories);
			}
		}
	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	public Collection<TableProvider<?, ?>> createTableProviders(String elementClassName) {
		Map<String, List<TableProviderFactory>> scope2Factories = element2Scope2Factory.get(elementClassName);
		if (scope2Factories != null) {
			Collection<TableProvider<?, ?>> tableProviders = new ArrayList<TableProvider<?,?>>(scope2Factories.values().size());
			for (Map.Entry<String, List<TableProviderFactory>> entry : scope2Factories.entrySet()) {
				List<TableProviderFactory> factories = entry.getValue();
				for (TableProviderFactory factory : factories) {
					tableProviders.add(factory.createTableProvider());
				}
			}
			return tableProviders;
		}
		return Collections.emptyList();
	}

	public Collection<TableProvider<?, ?>> createTableProviders(String elementClassName, String scopeClassName) {
		Map<String, List<TableProviderFactory>> scope2Factories = element2Scope2Factory.get(elementClassName);
		if (scope2Factories != null) {
			Collection<TableProvider<?, ?>> tableProviders = new ArrayList<TableProvider<?,?>>(scope2Factories.values().size());
			List<TableProviderFactory> factories = scope2Factories.get(scopeClassName);
			if (factories != null) {
				for (TableProviderFactory factory : factories) {
					tableProviders.add(factory.createTableProvider());
				}
				return tableProviders;
			}
		}
		return Collections.emptyList();
	}

	public Collection<TableProvider<?, ?>> createTableProviders(String elementClassName, String scopeClassName, Collection<?> elements) {
		Map<String, List<TableProviderFactory>> scope2Factories = element2Scope2Factory.get(elementClassName);
		if (scope2Factories != null) {
			Collection<TableProvider<?, ?>> tableProviders = new ArrayList<TableProvider<?,?>>(scope2Factories.values().size());
			List<TableProviderFactory> factories = scope2Factories.get(scopeClassName);
			if (factories != null) {
				for (TableProviderFactory factory : factories) {
					TableProvider tp = factory.createTableProvider();
					boolean compatible = false;
					for (Object element : elements) {
						if (tp.isCompatible(element, scopeClassName)) {
							compatible = true;
							break;
						}
					}
					if (compatible) {
						tableProviders.add(tp);
					}
				}
			}
			return tableProviders;
		}
		return Collections.emptyList();
	}

}
