package org.nightlabs.base.ui.exceptionhandler.errorreport;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.nightlabs.base.ui.resource.Messages;

public class ErrorReportSenderDescriptor 
{
	private IErrorReportSender instance;
	private IConfigurationElement configurationElement;
	private Integer priority;

	/**
	 * Create a new ErrorReportSenderRegistry.SenderDescriptor instance.
	 */
	public ErrorReportSenderDescriptor(IConfigurationElement configurationElement)
	{
		this.configurationElement = configurationElement;
	}

	/**
	 * Get the id.
	 * @return the id
	 */
	public String getId()
	{
		return configurationElement.getAttribute("id"); //$NON-NLS-1$
	}

	/**
	 * Get the priority. The higher the value, the higher the priority. The default priority is 0.
	 * @return the priority
	 */
	public int getPriority()
	{
		if(priority == null) {
			String p = configurationElement.getAttribute("priority"); //$NON-NLS-1$
			if(p == null)
				priority = 0;
			try {
				priority = Integer.parseInt(p);
			} catch(NumberFormatException e) {
				priority = 0;
			}
		}
		return priority;
	}

	/**
	 * Get the localized human readable name.
	 * @return the name
	 */
	public String getName()
	{
		String name = configurationElement.getAttribute("name"); //$NON-NLS-1$
		return name == null ? getId() : name;
	}

	/**
	 * Get the preference page id associated with this error report sender.
	 * @return The preference page id or <code>null</code> if this sender does not
	 * 		have a preference page associated.
	 */
	public String getPreferencePageId()
	{
		return configurationElement.getAttribute("preferencePageId"); //$NON-NLS-1$
	}
	
	/**
	 * Get the instance.
	 * @return the instance
	 * @throws CoreException If instanciating failed
	 */
	public synchronized IErrorReportSender getInstance() throws CoreException
	{
		if(instance == null)
			instance = (IErrorReportSender) configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
		return instance;
	}
}