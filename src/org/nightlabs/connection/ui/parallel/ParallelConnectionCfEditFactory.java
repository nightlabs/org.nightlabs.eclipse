package org.nightlabs.connection.ui.parallel;

import org.nightlabs.connection.config.ConnectionCf;
import org.nightlabs.connection.ui.AbstractConnectionCfEditFactory;
import org.nightlabs.connection.ui.ConnectionCfEdit;

public class ParallelConnectionCfEditFactory
		extends AbstractConnectionCfEditFactory
{

	public ConnectionCfEdit createConnectionCfEdit(ConnectionCf connectionCf)
	{
		ParallelConnectionCfEdit res = new ParallelConnectionCfEdit();
		res.setConnectionCfEditFactory(this);
		res.setConnectionCf(connectionCf);
		res.init();
		return res;
	}

}
