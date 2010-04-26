package org.nightlabs.base.ui.wizard;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.wizard.IWizard;
import org.nightlabs.base.ui.NLBasePlugin;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;

/**
 * This class represents the registry for processing the extensions which are registered for the extension-point with the id {@link #EXTENSION_POINT_ID}.
 * This class is an singleton which can be obtained via {@link #sharedInstance()}.
 * With the help of the method {@link #getFactory(Class)} users can obtain registered {@link IWizardDelegateFactory} for
 * specific subclasses of {@link WizardActionHandlerWizard}.
 *
 * @author Daniel Mazurek
 *
 */
public class WizardDelegateRegistry
extends AbstractEPProcessor
{
	private static final String ELEMENT_FACTORY_NAME = "wizardDelegateFactory";
	private static final String ATTRIBUTE_FACTORY_CLASS = "class";
	private static final String ATTRIBUTE_WIZARD_CLASS = "wizardClass";
	private static final String ATTRIBUTE_PRIORITY = "priority";

	public static final String EXTENSION_POINT_ID = NLBasePlugin.getDefault().getBundle().getSymbolicName() + "." + ELEMENT_FACTORY_NAME;
	private static final int DEFAULT_PRIORITY = 50;

	private static WizardDelegateRegistry sharedInstance = null;

	/**
	 * Returns the shared instance (singleton) for this object
	 **/
	public static WizardDelegateRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (WizardDelegateRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new WizardDelegateRegistry();
					sharedInstance.process();
				}
			}
		}
		return sharedInstance;
	}

	private Map<String, SortedMap<Integer, IWizardDelegateFactory>> wizardClass2Factories;

	protected WizardDelegateRegistry()
	{
		wizardClass2Factories = new HashMap<String, SortedMap<Integer,IWizardDelegateFactory>>();
	};

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception
	{
		if (element.getName().equals(ELEMENT_FACTORY_NAME)) {
			IWizardDelegateFactory factory = (IWizardDelegateFactory) element.createExecutableExtension(ATTRIBUTE_FACTORY_CLASS);
			String wizardClassName = element.getAttribute(ATTRIBUTE_WIZARD_CLASS);
			int priority = DEFAULT_PRIORITY;
			String prio = element.getAttribute(ATTRIBUTE_PRIORITY);
			try {
				priority = Integer.parseInt(prio);
			} catch (NumberFormatException e) {
				// Do nothing is already set to default
			}
			SortedMap<Integer, IWizardDelegateFactory> index2factory = wizardClass2Factories.get(wizardClassName);
			if (index2factory == null) {
				index2factory = new TreeMap<Integer, IWizardDelegateFactory>();
				wizardClass2Factories.put(wizardClassName, index2factory);
			}
			index2factory.put(priority, factory);
		}
	}

	/**
	 * Returns the registered implementation of {@link IWizardDelegateFactory} for the given wizardClass or null if nothing is registered for the class.
	 *
	 * @param wizardClass a subclass/implementation of {@link IWizard}.
	 * @return the registered implementation of {@link IWizardDelegateFactory} for the given wizardClass or null if nothing is registered for the class.
	 */
	public IWizardDelegateFactory getFactory(Class<? extends IWizard> wizardClass)
	{
		SortedMap<Integer, IWizardDelegateFactory> index2factory = wizardClass2Factories.get(wizardClass.getName());
		if (index2factory != null) {
			return index2factory.get(index2factory.firstKey());
		}
		return null;
	}
}
