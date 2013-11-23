package org.nightlabs.eclipse.extension;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.nightlabs.eclipse.extension.generic.ServiceExtensionRegistry;
import org.osgi.framework.BundleContext;

/**
 * Extension plugin.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ExtensionPlugin extends Plugin {

	// The shared instance
	private static ExtensionPlugin plugin;

	private ServiceExtensionRegistry serviceExtensionRegistry;

	/**
	 * The constructor
	 */
	public ExtensionPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * @return the shared instance
	 */
	public static ExtensionPlugin getDefault() {
		return plugin;
	}

	/**
	 * Log an error.
	 * @param msg The error message
	 * @param e The optional exception
	 */
	public static void logError(final String msg, final Throwable e) {
		getDefault().getLog().log(new Status(IStatus.ERROR, getDefault().getBundle().getSymbolicName(), msg, e));
	}

	/**
	 * Log an error.
	 * @param msg The error message
	 */
	public static void logError(final String msg) {
		getDefault().getLog().log(new Status(IStatus.ERROR, getDefault().getBundle().getSymbolicName(), msg));
	}

	public synchronized ServiceExtensionRegistry getServiceExtensionRegistry() {
		if(serviceExtensionRegistry == null) {
			serviceExtensionRegistry = new ServiceExtensionRegistry();
		}
		return serviceExtensionRegistry;
	}
}
