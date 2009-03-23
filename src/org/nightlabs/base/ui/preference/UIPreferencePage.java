/**
 *
 */
package org.nightlabs.base.ui.preference;

import org.nightlabs.base.ui.resource.Messages;


/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class UIPreferencePage extends CategoryPreferencePage {

	/**
	 *
	 */
	public UIPreferencePage() {
	}

	@Override
	protected String getText() {
		return Messages.getString("org.nightlabs.base.ui.preference.UIPreferencePage.text"); //$NON-NLS-1$
	}
}
