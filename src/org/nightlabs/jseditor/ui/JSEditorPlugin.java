package org.nightlabs.jseditor.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.jseditor.ui.editor.colorprovider.JSEditorColorProvider;
import org.nightlabs.jseditor.ui.editor.scanner.JSEditorCodeScanner;
import org.nightlabs.jseditor.ui.editor.scanner.JSEditorPartitionScanner;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JSEditorPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.nightlabs.jseditor.ui"; //$NON-NLS-1$
	public final static String JSEDITOR_PARTITIONING= "__js_editor_partitioning";   //$NON-NLS-1$

	// The shared instance
	private static JSEditorPlugin plugin;
	
	private JSEditorColorProvider colorProvider;
	private JSEditorPartitionScanner partitionScanner;
	private JSEditorCodeScanner codeScanner;
	
	/**
	 * The constructor
	 */
	public JSEditorPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
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
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static JSEditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Return a scanner for creating JSEditor partitions.
	 * 
	 * @return a scanner for creating JSEditor partitions
	 */
	 public JSEditorPartitionScanner getJSEditorPartitionScanner() {
		if (partitionScanner == null)
			partitionScanner= new JSEditorPartitionScanner();
		return partitionScanner;
	}
	
	/**
	 * Returns the singleton JSEditor code scanner.
	 * 
	 * @return the singleton JSEditor code scanner
	 */
	 public RuleBasedScanner getJSEditorCodeScanner() {
	 	if (codeScanner == null)
			codeScanner= new JSEditorCodeScanner(getJSEditorColorProvider());
		return codeScanner;
	}
	
	/**
	 * Returns the singleton JSEditor color provider.
	 * 
	 * @return the singleton JSEditor color provider
	 */
	 public JSEditorColorProvider getJSEditorColorProvider() {
	 	if (colorProvider == null)
			colorProvider= new JSEditorColorProvider();
		return colorProvider;
	}
	
	 /**
	  * Returns an image descriptor for the image file at the given
	  * plug-in relative path.
	  *
	  * @param path the path
	  * @return the image descriptor
	  */
	 public static ImageDescriptor getImageDescriptor(String path) {
		 return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path); //$NON-NLS-1$
	 }
}
