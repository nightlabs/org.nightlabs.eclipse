package org.nightlabs.eclipse.preferences.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision: 1734 $ - $Date: 2008-01-08 17:02:20 +0100 (Di, 08 Jan 2008) $
 */
public class PreferencesUIPlugin extends AbstractUIPlugin {

	/**
	 * The plugin ID
	 */
	public static final String PLUGIN_ID = "org.nightlabs.eclipse.preferences.ui";

	// The shared instance
	private static PreferencesUIPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public PreferencesUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static PreferencesUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Log the given status.
	 * @param status The status to log
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
	
	/**
	 * Log the given message.
	 * @param message The message to log
	 */
	public static void logErrorMessage(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, null));
	}

	/**
	 * Log the given error status.
	 * @param message The message to log
	 * @param status The error status to log
	 */
	public static void logErrorStatus(String message, IStatus status) {
		if (status == null) {
			logErrorMessage(message);
			return;
		}
		MultiStatus multi= new MultiStatus(PLUGIN_ID, IStatus.ERROR, message, null);
		multi.add(status);
		log(multi);
	}
	
	/**
	 * Log the given Throwable.
	 * @param e The Throwable to log
	 */
	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "Error", e)); 
	}
	

	private FormToolkit fDialogsFormToolkit;
	
	/**
	 * Get a forms toolkit for dialogs.
	 * @return The forms toolkit.
	 */
	public FormToolkit getDialogsFormToolkit() {
		if (fDialogsFormToolkit == null) {
			FormColors colors= new FormColors(Display.getCurrent());
			colors.setBackground(null);
			colors.setForeground(null);	
			fDialogsFormToolkit= new FormToolkit(colors);
		}
		return fDialogsFormToolkit;
	}
	
}
