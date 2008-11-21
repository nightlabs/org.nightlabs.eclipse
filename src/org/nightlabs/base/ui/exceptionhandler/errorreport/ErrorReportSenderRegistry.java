package org.nightlabs.base.ui.exceptionhandler.errorreport;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.exceptionhandler.IExceptionHandler;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;

public class ErrorReportSenderRegistry extends AbstractEPProcessor
{
	private static final Logger logger = Logger.getLogger(ErrorReportSenderRegistry.class);
	private static final String EXTENSION_POINT_ID = "org.nightlabs.base.ui.exceptionhandler.errorreport"; //$NON-NLS-1$

	private Map<String, IErrorReportSender> senders = new HashMap<String, IErrorReportSender>();
	
	public void addSender(String name, IErrorReportSender sender)
	{
		synchronized (senders)
		{
			senders.put(name, sender);
		}
	}

	public void removeSender(String name)
	{
		synchronized (senders)
		{
			senders.remove(name);
		}
	}

	public Map<String, IErrorReportSender> getSenders()
	{
		return senders;
	}
	
	@Override
	public String getExtensionPointID()
	{
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception
	{
		if (element.getName().toLowerCase().equals("errorreport")) { //$NON-NLS-1$
			String name = element.getAttribute("name"); //$NON-NLS-1$

			IErrorReportSender sender = (IErrorReportSender) element.createExecutableExtension("class"); //$NON-NLS-1$
			if (!IErrorReportSender.class.isAssignableFrom(sender.getClass()))
				throw new IllegalArgumentException("Specified class for element errorreport must implement "+IErrorReportSender.class.getName()+". "+sender.getClass().getName()+" does not."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			addSender(name, sender);
		}
		else 
		{
			// wrong element according to schema, probably checked earlier
			throw new IllegalArgumentException("Element "+element.getName()+" is not supported by extension-point " + EXTENSION_POINT_ID); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}
}
