package org.nightlabs.base.ui.exceptionhandler.errorreport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;

/**
 * Registry for error senders provided by extensions.
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReportSenderRegistry extends AbstractEPProcessor
{
	private static final String EXTENSION_POINT_ID = "org.nightlabs.base.ui.errorReportSender"; //$NON-NLS-1$

	private static ErrorReportSenderRegistry registry = null;
	
	/**
	 * Get the shared registry.
	 * @return The registry
	 */
	public static ErrorReportSenderRegistry getRegistry()
	{
		if(registry == null)
			registry = new ErrorReportSenderRegistry();
		return registry;
	}
	
	private Map<String, ErrorReportSenderDescriptor> senders;
	
	@Override
	public String getExtensionPointID()
	{
		return EXTENSION_POINT_ID;
	}

	@Override
	public synchronized void processElement(IExtension extension, IConfigurationElement element) throws Exception
	{
		if(senders == null)
			senders = new HashMap<String, ErrorReportSenderDescriptor>();
		ErrorReportSenderDescriptor sd = new ErrorReportSenderDescriptor(element);
		senders.put(sd.getId(), sd);
	}
	
	/**
	 * Get an unmodifiable senders map id -&gt; sender descriptor.
	 * @return the senders
	 */
	public synchronized Map<String, ErrorReportSenderDescriptor> getSenders()
	{
		process();
		if(senders == null)
			return Collections.emptyMap();
		return Collections.unmodifiableMap(senders);
	}
	
	public ErrorReportSenderDescriptor getDefaultSender()
	{
		ErrorReportSenderDescriptor highestPrioritySender = null;
		Map<String, ErrorReportSenderDescriptor> senders = getSenders();
		for (ErrorReportSenderDescriptor sd : senders.values()) {
			if(highestPrioritySender == null || highestPrioritySender.getPriority() < sd.getPriority())
				highestPrioritySender = sd;
		}
		return highestPrioritySender;
	}
}
