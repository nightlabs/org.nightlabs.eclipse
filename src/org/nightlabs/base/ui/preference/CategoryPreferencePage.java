/**
 *
 */
package org.nightlabs.base.ui.preference;

import org.nightlabs.eclipse.preferences.ui.OverviewPage;

/**
 * Base class for empty PreferencePages which are used as entry page for an category.
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class CategoryPreferencePage
extends OverviewPage
{
	/**
	 *
	 */
	public CategoryPreferencePage() {
		super();
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
//	 */
//	@Override
//	protected void createTopContents(Composite parent)
//	{
//		Label label = new Label(parent, SWT.NONE);
//		label.setText(getText());
//	}

	protected String getText() {
		return "";
	}

}
