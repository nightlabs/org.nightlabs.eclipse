package org.nightlabs.base.ui.wizard;

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * Implementations of this interface define a order of implementations of {@link IWizardPage}.
 * Implementations of {@link IWizardDelegate} can also implement this interface if there is no need for separation.
 *
 * @author Daniel Mazurek
 */
public interface IPageProvider
{
	/**
	 * Return a {@link List} of {@link IWizardPage}s which should be used in a wizard.
	 * The order or the list defines the order of the pages inside the wizard.
	 * Implementations should instantiate this list lazy and always return the same list.
	 *
	 * @return a {@link List} of {@link IWizardPage}s which should be used in a wizard.
	 */
	List<? extends IWizardPage> getPages();
}
