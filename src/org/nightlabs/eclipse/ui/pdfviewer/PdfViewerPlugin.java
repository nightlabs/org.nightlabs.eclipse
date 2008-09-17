package org.nightlabs.eclipse.ui.pdfviewer;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @version $Revision$ - $Date$
 * @author frederik löser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfViewerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.nightlabs.eclipse.ui.pdfviewer";

	// The shared instance
	private static PdfViewerPlugin plugin;

	/**
	 * The constructor
	 */
	public PdfViewerPlugin() {
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
	public static PdfViewerPlugin getDefault() {
		return plugin;
	}

}
