package org.nightlabs.eclipse.ui.pdfviewer.extension;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.eclipse.ui.pdfviewer.extension.resource.Messages;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PdfViewerExtensionPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.nightlabs.eclipse.ui.pdfviewer.extension"; //$NON-NLS-1$

	// The shared instance
	private static PdfViewerExtensionPlugin plugin;

	/**
	 * The constructor
	 */
	public PdfViewerExtensionPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PdfViewerExtensionPlugin getDefault() {
		return plugin;
	}

}
