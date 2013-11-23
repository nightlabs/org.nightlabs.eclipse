/**
 *
 */
package org.nightlabs.base.ui.wizard;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public interface IDynamicPathWizardListener
{
	/**
	 *
	 * @param buttonId the id of the button which was pressed in the {@link DynamicPathWizard}.
	 */
	void buttonPressed(int buttonId);

	/**
	 *
	 * @param currentPage the currentPage of the wizard
	 */
	void pageChanged(IWizardPage currentPage);
}
