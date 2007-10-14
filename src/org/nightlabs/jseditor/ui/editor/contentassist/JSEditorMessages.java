package org.nightlabs.jseditor.ui.editor.contentassist;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class JSEditorMessages {

	private static final String RESOURCE_BUNDLE= "org.nightlabs.jseditor.ui.editor.contentassist.JSEditorMessages";//$NON-NLS-1$

	private static ResourceBundle fgResourceBundle= ResourceBundle.getBundle(RESOURCE_BUNDLE);

	private JSEditorMessages() {
	}

	public static String getString(String key) {
		try {
			return fgResourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return "!" + key + "!";//$NON-NLS-2$ //$NON-NLS-1$
		}
	}
	
	public static ResourceBundle getResourceBundle() {
		return fgResourceBundle;
	}
}
