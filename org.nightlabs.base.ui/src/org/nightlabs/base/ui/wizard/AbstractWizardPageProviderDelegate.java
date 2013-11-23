package org.nightlabs.base.ui.wizard;

/**
 * Abstract base class for implementations of {@link IWizardDelegate} which also combine
 * providing of pages by implementing {@link IPageProvider} in one class.
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 */
public abstract class AbstractWizardPageProviderDelegate
extends AbstractWizardDelegate
implements IPageProvider
{
	@Override
	protected IPageProvider createPageProvider() {
		return this;
	}
}
