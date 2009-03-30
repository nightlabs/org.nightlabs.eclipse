/**
 *
 */
package org.nightlabs.base.ui.preference;

import org.nightlabs.base.ui.resource.Messages;


/**
 * Category class which represents the preference category "UI Preferences".
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class UIPreferencePage extends CategoryPreferencePage {

	/**
	 * Creates an UIPreferencePage.
	 */
	public UIPreferencePage() {
	}

	@Override
	protected String getText() {
		return Messages.getString("org.nightlabs.base.ui.preference.UIPreferencePage.text"); //$NON-NLS-1$
	}
}
