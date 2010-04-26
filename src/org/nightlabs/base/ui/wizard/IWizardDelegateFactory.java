package org.nightlabs.base.ui.wizard;

/**
 * Interface for the factory pattern (creation of new instances) for creating instances of {@link IWizardDelegate}.
 * Implementations of this interface can be registered via the extension-point {@link WizardDelegateRegistry#EXTENSION_POINT_ID} 
 *  
 * @author Daniel Mazurek
 *
 */
public interface IWizardDelegateFactory 
{
	/**
	 * Returns a new instance of an implementation of {@link IWizardDelegate}.
	 * 
	 * @return a new instance of an implementation of {@link IWizardDelegate}.
	 */
	IWizardDelegate createWizardDelegate();
}
