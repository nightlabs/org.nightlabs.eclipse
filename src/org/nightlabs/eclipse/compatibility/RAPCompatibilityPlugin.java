package org.nightlabs.eclipse.compatibility;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class RAPCompatibilityPlugin extends AbstractUIPlugin {
	private static RAPCompatibilityPlugin instance;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		instance = this;
	}

	public static RAPCompatibilityPlugin getDefault() {
		return instance;
	}
	
	
	public Image getImage(String key) {
		Image result = getImageRegistry().get(key);

		if(result == null) {
			result = ImageDescriptor.createFromURL(getBundle().getEntry(key)).createImage();
			getImageRegistry().put(key, result);
		}
		
		return result;
	}
	
}
