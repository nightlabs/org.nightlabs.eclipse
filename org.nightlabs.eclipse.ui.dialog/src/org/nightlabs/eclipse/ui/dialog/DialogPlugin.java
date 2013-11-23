package org.nightlabs.eclipse.ui.dialog;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The plugin class for the dialog plugin. Used to obtain
 * {@link DialogSettings}.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class DialogPlugin extends AbstractUIPlugin
{
	private static DialogPlugin pluginInstance;
	
	/**
	 * Create a new DialogPlugin instance.
	 */
	public DialogPlugin()
	{
		assert pluginInstance == null;
		pluginInstance = this;
	}
	
	/**
	 * Get the plugin singleton.
	 * @return The plugin singleton or <code>null</code> if the plugin
	 * 		is not yet initialized.
	 */
	public static DialogPlugin getDefault()
	{
		return pluginInstance;
	}
}
