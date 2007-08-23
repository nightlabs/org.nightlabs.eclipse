package org.nightlabs.connection.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.connection.ConnectionImplementation;

public class ConnectionCfEditRegistry
		extends AbstractEPProcessor
{
	private static ConnectionCfEditRegistry _sharedInstance = null;
	public synchronized static ConnectionCfEditRegistry sharedInstance()
	{
		if (_sharedInstance == null) {
			try {
				_sharedInstance = new ConnectionCfEditRegistry();
				_sharedInstance.process();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return _sharedInstance;
	}

	public String getExtensionPointID()
	{
		return "org.nightlabs.connection.connectionCfEditFactory"; //$NON-NLS-1$
	}

	private Map<String, ConnectionCfEditFactory> connectionClassName2ConnectionCfEditFactory = new HashMap<String, ConnectionCfEditFactory>();

	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception
	{
		try {
			ConnectionCfEditFactory factory = (ConnectionCfEditFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
			String connectionClassName = element.getAttribute("connectionClass"); //$NON-NLS-1$
			ConnectionImplementation connectionImplementation =
					ConnectionImplementationRegistry.sharedInstance().getConnectionImplementation(connectionClassName, true);
			factory.setConnectionImplementation(connectionImplementation);
			factory.init();
			connectionClassName2ConnectionCfEditFactory.put(connectionClassName, factory);
		} catch (CoreException e) {
			throw new EPProcessorException(e.getMessage(), extension, e);
		}
	}

	public ConnectionCfEditFactory getConnectionCfEditFactory(String connectionClassName, boolean throwExceptionIfNotFound)
	{
		ConnectionCfEditFactory res = connectionClassName2ConnectionCfEditFactory.get(connectionClassName);
		if (throwExceptionIfNotFound && res == null)
			throw new IllegalArgumentException("No ConnectionCfEditFactory registered for connection class: " + connectionClassName); //$NON-NLS-1$

		return res;
	}
}
