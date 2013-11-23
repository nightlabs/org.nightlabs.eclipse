package org.nightlabs.base.ui.wizard;

import org.eclipse.jface.wizard.IWizard;

/**
 * Implementations of this interface perform the final action when the user presses the finish button in a {@link IWizard}
 * and provide the implementation of {@link IPageProvider} for displaying the pages inside wizard.
 * In case there is no need for separation the page providing the implementation can also implement {@link IPageProvider} as well,
 * to keep the code in one class.
 *
 * It is recommended to subclass {@link AbstractWizardDelegate} instead of directly implement this interface.
 *
 * @author Daniel Mazurek
 *
 */
public interface IWizardDelegate
{
	/**
	 * Returns the IPageProvider which provides the wizard pages for the wizards.
	 * @return the IPageProvider
	 */
	IPageProvider getPageProvider();

	/**
     * Subclasses must implement this method to perform
     * any special finish processing for their wizard.
     * @return true if the finish action was successful or false if not
	 */
	public boolean performFinish();

	/**
	 * Sets the the implementation of {@link IWizard} where this {@link IWizardDelegate} is used
	 * @param wizard the implementation of {@link IWizard} for which this {@link IWizardDelegate} is registered
	 */
	void setWizard(IWizard wizard);

	/**
	 * Returns the implementation of {@link IWizard} which has been set with {@link #setWizard(IWizard)} before.
	 * @return the implementation of {@link IWizard} which has been set with {@link #setWizard(IWizard)} before
	 */
	IWizard getWizard();
}
