/* ************************************************************************
 * org.nightlabs.eclipse.ui.fckeditor - Eclipse RCP FCKeditor Integration *
 * Copyright (C) 2008 NightLabs - http://NightLabs.org                    *
 *                                                                        *
 * This library is free software; you can redistribute it and/or          *
 * modify it under the terms of the GNU Lesser General Public             *
 * License as published by the Free Software Foundation; either           *
 * version 2.1 of the License, or (at your option) any later version.     *
 *                                                                        *
 * This library is distributed in the hope that it will be useful,        *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of         *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU      *
 * Lesser General Public License for more details.                        *
 *                                                                        *
 * You should have received a copy of the GNU Lesser General Public       *
 * License along with this library; if not, write to the                  *
 *     Free Software Foundation, Inc.,                                    *
 *     51 Franklin St, Fifth Floor,                                       *
 *     Boston, MA  02110-1301  USA                                        *
 *                                                                        *
 * Or get it online:                                                      *
 *     http://www.gnu.org/copyleft/lesser.html                            *
 **************************************************************************/
package org.nightlabs.eclipse.ui.fckeditor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle and provides
 * access to the registered extensions.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
 */
public class Activator extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "org.nightlabs.eclipse.ui.fckeditor"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor.
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

//		if(!ImageIO.getImageReadersByFormatName("pcx").hasNext()) { //$NON-NLS-1$
//			IIORegistry iioReg = IIORegistry.getDefaultInstance();
//			iioReg.registerServiceProvider(new PCXImageReaderSPI());
//			iioReg.registerServiceProvider(new PCXImageWriterSPI());
//		}

		contentFileWizardRegistry = new ContentFileWizardRegistry();
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
	 * Returns the shared instance.
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	// ----------------------

	/**
	 * Log an information message.
	 * @param msg The message
	 */
	public static void log(String msg)
	{
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, msg));
	}

	/**
	 * Log an information message.
	 * @param msg The message
	 * @param e A throwable
	 */
	public static void log(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, msg, e));
	}

	/**
	 * Log an warning message.
	 * @param msg The message
	 */
	public static void warn(String msg)
	{
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, msg));
	}

	/**
	 * Log an warning message.
	 * @param msg The message
	 * @param e A throwable
	 */
	public static void warn(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, msg, e));
	}

	/**
	 * Log an error message.
	 * @param msg The message
	 */
	public static void err(String msg)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg));
	}

	/**
	 * Log an error message.
	 * @param msg The message
	 * @param e A throwable
	 */
	public static void err(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	// ----------------------

	private ContentFileWizardRegistry contentFileWizardRegistry;

	/**
	 * Get the registered content file wizard for the given mime type.
	 * If more than one wizard is registered, the wizard with the highest
	 * priority will be returned.
	 * @param mimeType The mime type
	 * @return An instance of the registered wizard class
	 * @throws CoreException in case of an error creating the wizard instance
	 */
	public IContentFileWizard getContentFileWizard(String mimeType) throws CoreException
	{
		return contentFileWizardRegistry.getContentFileWizard(mimeType);
	}

	private static abstract class AbstractEPProcessor
	{
		/**
		 * Return the extension-point id here this EPProcessor should process.
		 */
		public abstract String getExtensionPointID();

		/**
		 * Process all extension to the extension-point defined by {@link #getExtensionPointID()}.
		 */
		protected abstract void processElement(IExtension extension, IConfigurationElement element) throws Exception;

		protected synchronized void process()
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			if (registry != null) {
				IExtensionPoint extensionPoint = registry.getExtensionPoint(getExtensionPointID());
				if (extensionPoint == null) {
					err("Unable to resolve extension-point: " + getExtensionPointID()); //$NON-NLS-1$
					return;
				}

				IExtension[] extensions = extensionPoint.getExtensions();
				// For each extension ...
				for (int i = 0; i < extensions.length; i++) {
					IExtension extension = extensions[i];
					IConfigurationElement[] elements =
						extension.getConfigurationElements();
					// For each member of the extension ...
					for (int j = 0; j < elements.length; j++) {
						IConfigurationElement element = elements[j];
						try {
							processElement(extension, element);
						} catch (Exception e) {
							// Only log the error and continue
							err("Error processing extension element. The element is located in an extension in bundle: " + extension.getNamespaceIdentifier(), e); //$NON-NLS-1$
						}
					}
				}
			}
		}
	}

	private static class ContentFileWizardRegistry extends AbstractEPProcessor
	{
		private static final String TYPE_BINDING_ELEMENT = "typeBinding"; //$NON-NLS-1$
		private static final String WIZARD_CLASS_ATTRIBUTE = "wizardClass"; //$NON-NLS-1$
		private static final String MIME_TYPE_ATTRIBUTE = "mimeType"; //$NON-NLS-1$
		private static final String PRIORITY_ATTRIBUTE = "priority"; //$NON-NLS-1$

		private Map<String, WizardDescriptor> wizards;

		private static class WizardDescriptor
		{
			int priority;
			IConfigurationElement configurationElement;

			public IContentFileWizard createWizard() throws CoreException
			{
				return (IContentFileWizard)configurationElement.createExecutableExtension(WIZARD_CLASS_ATTRIBUTE);
			}
		}

		public synchronized IContentFileWizard getContentFileWizard(String mimeType) throws CoreException
		{
			if(wizards == null) {
				wizards = new HashMap<String, WizardDescriptor>();
				process();
			}
			WizardDescriptor wizardDescriptor = wizards.get(mimeType);
			if(wizardDescriptor == null)
				wizardDescriptor = wizards.get("*"); //$NON-NLS-1$
			if(wizardDescriptor == null)
				return null;
			return wizardDescriptor.createWizard();
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.eclipse.ui.fckeditor.Activator.AbstractEPProcessor#getExtensionPointID()
		 */
		@Override
		public String getExtensionPointID()
		{
			return "org.nightlabs.eclipse.ui.fckeditor.contentFileWizard"; //$NON-NLS-1$
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.eclipse.ui.fckeditor.Activator.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
		 */
		@Override
		protected void processElement(IExtension extension, IConfigurationElement element) throws CoreException
		{
			if(TYPE_BINDING_ELEMENT.equals(element.getName())) {
				String mimeType = element.getAttribute(MIME_TYPE_ATTRIBUTE);
				String className = element.getAttribute(WIZARD_CLASS_ATTRIBUTE);
				if(mimeType == null || className == null)
					throw new IllegalStateException("Invalid content file wizard type binding - mimeType: "+mimeType+" wizardClass: "+className); //$NON-NLS-1$ //$NON-NLS-2$
				String priorityString = element.getAttribute(PRIORITY_ATTRIBUTE);
				int priority;
				try {
					priority = Integer.parseInt(priorityString);
				} catch(NumberFormatException e) {
					warn("Invalid priority attribute for contentFileWizard extension in bundle "+extension.getNamespaceIdentifier()); //$NON-NLS-1$
					priority = 0;
				}
				WizardDescriptor wizardDescriptor = new WizardDescriptor();
				wizardDescriptor.priority = priority;
				wizardDescriptor.configurationElement = element;

				// only add if priority is higher than eventually existing priority
				WizardDescriptor oldWizard = wizards.get(mimeType);
				if(oldWizard == null || oldWizard.priority < priority)
					wizards.put(mimeType, wizardDescriptor);
			}
		}
	}
}
