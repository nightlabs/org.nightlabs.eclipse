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

package org.nightlabs.base.extensionpoint;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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
 * 
 * @author Alexander Bieber
 */
public abstract class AbstractEPProcessor 
implements IEPProcessor
{
	/**
	 * Log4J Logger used for this class.
	 */
	private static final Logger logger = Logger.getLogger(AbstractEPProcessor.class);

	/**
	 * Return the extension-point id here this EPProcessor should process.
	 */
	public abstract String getExtensionPointID();

	/**
	 * Process all extension to the extension-point defined by {@link #getExtensionPointID()}
	 */
	public abstract void processElement(IExtension extension, IConfigurationElement element) throws Exception;


	private List<IEPProcessListener> processListeners;

	public AbstractEPProcessor()
	{
		processListeners = new ArrayList<IEPProcessListener>();
	}

	private boolean processed = false;
	public boolean isProcessed() {
		return processed;
	}

	private boolean processing = false;

	protected boolean isProcessing() {
		return processing;
	}

	public synchronized void process() {
		processing = true;
		try {
			for(IEPProcessListener listener : processListeners)
				listener.preProcess();

			IExtensionRegistry registry = Platform.getExtensionRegistry();
			if (registry != null) 
			{
				IExtensionPoint extensionPoint = registry.getExtensionPoint(getExtensionPointID());
				if (extensionPoint == null) {
					throw new IllegalStateException("Unable to resolve extension-point: " + getExtensionPointID()); //$NON-NLS-1$
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
							logger.error("Error processing extension element. The element is located in an extension in bundle: " + extension.getNamespaceIdentifier(), e); //$NON-NLS-1$
						}               
					}
				}

				for(IEPProcessListener listener : processListeners)
					listener.postProcess();

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
	public void checkProcessing(boolean throwExceptionIfErrorOccurs) 
	{
		if (!isProcessed()) {
			try {
				process();
			} catch (Throwable e) {
				if (throwExceptionIfErrorOccurs)
					throw new RuntimeException(e);
				else
					logger.error("There occured an error during processing extension-point "+getExtensionPointID()+"!", e);					 //$NON-NLS-1$ //$NON-NLS-2$
			}  	  		
		}
	}

	/**
	 * 
	 * @param s the String to check
	 * @return true if the String is neither null nor an empty String otherwise 
	 * returns false
	 */
	public static boolean checkString(String s) 
	{
		if (s == null || s.trim().equals("") ) //$NON-NLS-1$
			return false;

		return true;
	}  

	public void addProcessListener(IEPProcessListener listener)
	{
		processListeners.add(listener);
	}

	public void removeProcessListener(IEPProcessListener listener)
	{
		processListeners.remove(listener);
	}
}