// $Id$
package org.nightlabs.eclipse.ui.fckeditor.resource;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class Messages
{
	private static final String BUNDLE_NAME = "org.nightlabs.eclipse.ui.fckeditor.resource.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages()
	{
	}

	public static String getString(String key)
	{
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
