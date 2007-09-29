/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
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

package org.nightlabs.base;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.base.app.AbstractApplication;
import org.nightlabs.base.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.exceptionhandler.SimpleExceptionHandlerRegistry;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class NLBasePlugin extends AbstractUIPlugin 
{
	public static final String PLUGIN_ID = "org.nightlabs.base"; //$NON-NLS-1$
	
	//The shared instance.
	private static NLBasePlugin plugin;
//	//Resource bundle.
//	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public NLBasePlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ExceptionHandlerRegistry.sharedInstance().addProcessListener(SimpleExceptionHandlerRegistry.sharedInstance());
//		try {
//			resourceBundle = Platform.getResourceBundle(getBundle());
//		} catch (MissingResourceException x) {
//			resourceBundle = null;
//		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static NLBasePlugin getDefault() {
		return plugin;
	}

//	/**
//	 * Returns the string from the plugin's resource bundle,
//	 * or 'key' if not found.
//	 */
//	public static String getResourceString(String key) {
//		ResourceBundle bundle = NLBasePlugin.getDefault().getResourceBundle();
//		try {
//			return (bundle != null) ? bundle.getString(key) : key;
//		} catch (MissingResourceException e) {
//			return key;
//		}
//	}

//	/**
//	 * Returns the plugin's resource bundle,
//	 */
//	public ResourceBundle getResourceBundle() {
//		return resourceBundle;
//	}

	private AbstractApplication application = null;

	public AbstractApplication getApplication()
	{
		return application;
	}
	public void setApplication(AbstractApplication application)
	{
		if (this.application != null && this.application != application)
			throw new IllegalStateException("Cannot overwrite application! It is already initialized!"); //$NON-NLS-1$

		this.application = application;
	}
}
