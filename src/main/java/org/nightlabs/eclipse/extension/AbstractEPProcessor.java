/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.eclipse.extension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Used for parsing extensions to a certain extension-point.
 * <p>
 * Calling {@link #process()} will cause {@link #processElement(IExtension, IConfigurationElement)}
 * to be called for every extension defined to the point returned by {@link #getExtensionPointID()}.
 * </p>
 * <p>
 * The IConfigurationElement passed to processElement is the root element of the extension.
 * Usually one will use element.createExecutableExtension() to get a instance of
 * the extensions object.
 * </p>
 * <p>
 * A common usage is subclassing this to a registry which registeres its
 * entries in processElement lazily by calling {@link #checkProcessing()}
 * everytime when asked for one element fist.
 * </p>
 * <!-- serialize doesn't work because of different class loaders!
 * <p>
 * Since 2008-10-06, this class is {@link Serializable} in order to make it possible to clone
 * extension-registries which usually extend this class (via {@link Util#cloneSerializable(Object)}).
 * This is necessary, if the contents of
 * the registry need to be instantiated multiple times for different use cases (for example
 * {@link IAction}s used at different places simultaneously as in
 * <code>org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerActionRegistry</code>).
 * It's still recommended, not to use this feature and instead model your extension point in a way
 * (use factories!) that supports multiple instances of the elements held by your {@link AbstractEPProcessor}
 * implementation.
 * </p>
 * -->
 *
 * @author Alexander Bieber
 */
public abstract class AbstractEPProcessor
implements IEPProcessor // , Serializable
{
	/**
	 * Return the extension-point id here this EPProcessor should process.
	 */
	public abstract String getExtensionPointID();

	/**
	 * Process all extension to the extension-point defined by {@link #getExtensionPointID()}
	 */
	public abstract void processElement(IExtension extension, IConfigurationElement element) throws Exception;


	private final List<IEPProcessListener> processListeners;

	public AbstractEPProcessor()
	{
		processListeners = new ArrayList<IEPProcessListener>();
	}

	private boolean processed = false;
	public boolean isProcessed() {
		return processed;
	}

	private volatile boolean processing = false;

	protected boolean isProcessing() {
		return processing;
	}

	public synchronized void process() {
		if (processed) {
			return;
		}

		processing = true;
		try {
			for(final IEPProcessListener listener : processListeners) {
				listener.preProcess();
			}

			final IExtensionRegistry registry = Platform.getExtensionRegistry();
			if (registry != null)
			{
				final IExtensionPoint extensionPoint = registry.getExtensionPoint(getExtensionPointID());
				if (extensionPoint == null) {
					throw new IllegalStateException("Unable to resolve extension-point: " + getExtensionPointID()); //$NON-NLS-1$
				}

				final IExtension[] extensions = extensionPoint.getExtensions();
				// For each extension ...
				for (final IExtension extension : extensions) {
					final IConfigurationElement[] elements = extension.getConfigurationElements();
					// For each member of the extension ...
					for (final IConfigurationElement element : elements) {
						try {
							processElement(extension, element);
						} catch (final Throwable e) { // we must catch Throwable instead of Exception since we often have NoClassDefFoundErrors (during first start or when server's class configuration changes)
							// Only log the error and continue
							ExtensionPlugin.logError("Error processing extension element. The element is located in an extension in bundle: " + extension.getNamespaceIdentifier(), e); //$NON-NLS-1$
						}
					}
				}

				for(final IEPProcessListener listener : processListeners) {
					listener.postProcess();
				}

				processed = true;
			}
		} finally {
			processing = false;
		}
	}

	/**
	 * Assures that this processor
	 * has processed its extensions
	 */
	public void checkProcessing()
	{
		checkProcessing(true);
	}

	/**
	 * Assures that this processor has processed its extensions
	 *
	 * @param throwExceptionIfErrorOccurs determines if a RuntimeException should be thrown
	 * if a EPProcessorException occurs or only an error should be logged
	 */
	public void checkProcessing(final boolean throwExceptionIfErrorOccurs)
	{
		if (!isProcessed()) {
			try {
				process();
			} catch (final Throwable e) {
				if (throwExceptionIfErrorOccurs) {
					throw new RuntimeException(e);
				} else {
					ExtensionPlugin.logError("There occured an error during processing extension-point "+getExtensionPointID()+"!", e);					 //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}

	/**
	 *
	 * @param s the String to check
	 * @return true if the String is neither null nor an empty String otherwise
	 * returns false
	 */
	public static boolean checkString(final String s)
	{
		return s != null && !s.trim().isEmpty();
	}

	public void addProcessListener(final IEPProcessListener listener)
	{
		processListeners.add(listener);
	}

	public void removeProcessListener(final IEPProcessListener listener)
	{
		processListeners.remove(listener);
	}
}