package org.nightlabs.connection.ui;

import org.nightlabs.connection.ConnectionImplementation;

public abstract class AbstractConnectionCfEditFactory
		implements ConnectionCfEditFactory
{
	ConnectionImplementation connectionImplementation;

	public void setConnectionImplementation(
			ConnectionImplementation connectionImplementation)
	{
		this.connectionImplementation = connectionImplementation;
	}

	public ConnectionImplementation getConnectionImplementation()
	{
		return connectionImplementation;
	}

	public void init()
	{
	}

}
