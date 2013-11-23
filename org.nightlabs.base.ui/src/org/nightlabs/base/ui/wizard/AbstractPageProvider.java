package org.nightlabs.base.ui.wizard;

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * Abstract base class for {@link IPageProvider}.
 * It is recommended to use subclass this class instead of implement {@link IPageProvider} directly.
 *
 * @author Daniel Mazurek
 *
 */
public abstract class AbstractPageProvider implements IPageProvider
{
	private List<? extends IWizardPage> wizardPages;

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.wizard.IPageProvider#getPages()
	 */
	@Override
	public List<? extends IWizardPage> getPages()
	{
		if (wizardPages == null) {
			wizardPages = createPages();
		}
		return wizardPages;
	}

	/**
	 * Return the {@link IWizardPage}s in the order in which should they should be displayed.
	 * Implement this method and do not override {@link #getPages()}.
	 *
	 * @return the {@link IWizardPage}s in the order in which should they should be displayed.
	 */
	protected abstract List<? extends IWizardPage> createPages();
}
