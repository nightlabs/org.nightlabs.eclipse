/**
 * 
 */
package org.nightlabs.base.ui.notification;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.eclipse.extension.EPProcessorException;
import org.nightlabs.notification.Interceptor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class NotificationManagerInterceptorEPProcessor extends AbstractEPProcessor {

	private org.nightlabs.notification.NotificationManager manager;
	
	/**
	 * 
	 */
	public NotificationManagerInterceptorEPProcessor(org.nightlabs.notification.NotificationManager manager) {
		this.manager = manager;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return "org.nightlabs.base.ui.notificationinterceptor"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(
		IExtension extension,
		IConfigurationElement element
	) throws Exception
	{
		try {
			Interceptor interceptor = (Interceptor) element.createExecutableExtension("class"); //$NON-NLS-1$
			element.getAttribute("name"); //$NON-NLS-1$

			manager.addInterceptor(interceptor);
		} catch (Throwable e) {
			throw new EPProcessorException("Extension to "+getExtensionPointID()+" with class "+element.getAttribute("class")+" has errors!", e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}

}
