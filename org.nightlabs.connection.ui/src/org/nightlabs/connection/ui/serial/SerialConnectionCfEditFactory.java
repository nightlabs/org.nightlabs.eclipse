package org.nightlabs.connection.ui.serial;

import org.nightlabs.connection.config.ConnectionCf;
import org.nightlabs.connection.ui.AbstractConnectionCfEditFactory;
import org.nightlabs.connection.ui.ConnectionCfEdit;

public class SerialConnectionCfEditFactory
extends AbstractConnectionCfEditFactory
{

	public ConnectionCfEdit createConnectionCfEdit(ConnectionCf connectionCf)
	{
		SerialConnectionCfEdit res = new SerialConnectionCfEdit();
		res.setConnectionCfEditFactory(this);
		res.setConnectionCf(connectionCf);
		res.init();
		return res;
	}

}
