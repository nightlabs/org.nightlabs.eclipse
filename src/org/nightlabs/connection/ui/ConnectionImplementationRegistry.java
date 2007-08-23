package org.nightlabs.connection.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.connection.Connection;
import org.nightlabs.connection.ConnectionImplementation;

public class ConnectionImplementationRegistry
		extends AbstractEPProcessor
{
	private static ConnectionImplementationRegistry _sharedInstance = null;
	public synchronized static ConnectionImplementationRegistry sharedInstance()
	{
		if (_sharedInstance == null) {
			try {
				_sharedInstance = new ConnectionImplementationRegistry();
				_sharedInstance.process();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return _sharedInstance;
	}

	public String getExtensionPointID()
	{
		return "org.nightlabs.connection.connectionImplementation"; //$NON-NLS-1$
	}

	private Map<String, ConnectionImplementation> connectionClassName2ConnectionImplementation = null;

	public void processElement(IExtension extension, IConfigurationElement element)
			throws Exception
	{
		try {
			Connection connection = (Connection) element.createExecutableExtension("class"); //$NON-NLS-1$
			Connection.getConnectionImplementations().add(new ConnectionImplementation(connection));
		} catch (Exception e) {
			throw new EPProcessorException(e.getLocalizedMessage(), extension, e);
		}
	}

	protected Map<String, ConnectionImplementation> _getConnectionClassName2ConnectionImplementation()
	{
		if (connectionClassName2ConnectionImplementation == null) {
			synchronized(this) {
				if (connectionClassName2ConnectionImplementation == null) {
					Map<String, ConnectionImplementation> m = new HashMap<String, ConnectionImplementation>();
					List<ConnectionImplementation> cis;
					try {
						cis = Connection.getConnectionImplementations();
					} catch (Exception e) { // should never happen here, because the data should be read already in process.
						throw new RuntimeException(e);
					}

					for (ConnectionImplementation ci : cis)
						m.put(ci.getConnectionClassName(), ci);

					connectionClassName2ConnectionImplementation = m;
				}
			}
		}

		return connectionClassName2ConnectionImplementation;
	}

	public ConnectionImplementation getConnectionImplementation(String connectionClassName, boolean throwExceptionIfNotFound)
	{
		ConnectionImplementation res = _getConnectionClassName2ConnectionImplementation().get(connectionClassName);
		if (throwExceptionIfNotFound && res == null)
			throw new IllegalArgumentException("No ConnectionImplementation registered for connectionClassName: " + connectionClassName); //$NON-NLS-1$

		return res;
	}

	public List<ConnectionImplementation> getConnectionImplementations()
	{
		try {
			return Connection.getConnectionImplementations();
		} catch (Exception e) { // should never happen here, because the data should be read already in process.
			throw new RuntimeException(e);
		}
	}

}
