package org.nightlabs.base.ui.wizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Abstract base implementation class for {@link IWizardDelegate}.
 * It is recommended to subclass this class instead of implement {@link IWizardDelegate} directly.
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public abstract class AbstractWizardDelegate implements IWizardDelegate
{
	private IWizard wizard;
	private IPageProvider pageProvider;

	/**
	 * Returns the new implementation of {@link IPageProvider} for this {@link IWizardDelegate}.
	 * @return the new implementation of {@link IPageProvider} for this {@link IWizardDelegate}
	 */
	protected abstract IPageProvider createPageProvider();

	@Override
	public IPageProvider getPageProvider()
	{
		if (pageProvider == null) {
			pageProvider = createPageProvider();
		}
		return pageProvider;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.IWizardDelegate#getWizard()
	 */
	@Override
	public IWizard getWizard() {
		return wizard;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.IWizardDelegate#setWizard(org.eclipse.jface.wizard.IWizard)
	 */
	@Override
	public void setWizard(IWizard wizard) {
		this.wizard = wizard;
	}

	/**
	 * Returns the {@link Shell} of the wizard.
	 * @return the {@link Shell} of the wizard
	 */
	protected Shell getShell()
	{
		if (getWizard() != null) {
			getWizard().getContainer().getShell();
		}
		return null;
	}

	/**
	 * Returns the {@link Display} of the wizard.
	 * @return the {@link Display} of the wizard
	 */
	protected Display getDisplay()
	{
		if (getShell() != null) {
			return getShell().getDisplay();
		}
		return null;
	}
}
