/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.editor2d;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.spi.IIORegistry;

import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.J2DGraphics;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.holongate.j2d.J2DRegistry;
import org.nightlabs.io.pcx.PCXImageReaderSPI;
import org.nightlabs.io.pcx.PCXImageWriterSPI;
import org.nightlabs.util.FontUtil;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class EditorPlugin 
extends AbstractUIPlugin 
{	
	//The shared instance.
	private static EditorPlugin plugin;
	//Resource bundle.
	private static ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public EditorPlugin() 
	{
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception 
	{
		super.start(context);
		resourceBundle = Platform.getResourceBundle(getBundle());	
		
		// init System Fonts
    FontUtil.getSystemFonts();
    
    // TODO: Holongate Draw2D-PreferencePage does not store values 
    Map hints = new HashMap();
    hints.put(J2DGraphics.KEY_USE_JAVA2D, Boolean.TRUE);
	  J2DRegistry.setHints(hints);   
	  
	  // register additional ImageReader + ImageWriter
	  IIORegistry.getDefaultInstance().registerServiceProvider(new PCXImageReaderSPI());
	  IIORegistry.getDefaultInstance().registerServiceProvider(new PCXImageWriterSPI());
	  
//	  ImageUtil.logAvailableFileFormats();
	}
	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception 
	{
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static EditorPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns a string from the resource bundle, knowing its key. Note: the generated
	 * code is not strong enough because a NullPointerException is raised if there is no
	 * resource bundle, instead of 'key' to be returned for consistency.
	 * TODO: Why do you write this note? your try-catch-block will make sure that the NPE is caught and key returned. Marco :-)
	 * 
	 * @return The string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		try {
			return resourceBundle.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle. Note that for this plugin, the resource
	 * bundle is the same as the plugin descriptor resource bundle.
	 * 
	 * @return The plugin's resource bundle
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}	
	
}
