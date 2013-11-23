package org.nightlabs.connection.ui.tcp;

import org.nightlabs.connection.config.ConnectionCf;
import org.nightlabs.connection.ui.AbstractConnectionCfEditFactory;
import org.nightlabs.connection.ui.ConnectionCfEdit;

public class TCPConnectionCfEditFactory
		extends AbstractConnectionCfEditFactory
{
	@Override
	public ConnectionCfEdit createConnectionCfEdit(ConnectionCf connectionCf)
	{
		TCPConnectionCfEdit res = new TCPConnectionCfEdit();
		res.setConnectionCfEditFactory(this);
		res.setConnectionCf(connectionCf);
		res.init();
		return res;
	}
}
