package org.nightlabs.connection.ui.tcp;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.annotation.Implement;
import org.nightlabs.connection.ui.AbstractConnectionCfEdit;

public class TCPConnectionCfEdit
		extends AbstractConnectionCfEdit
{

	@Implement
	protected Composite _createConnectionCfEditComposite(Composite parent)
	{
		return new TCPConnectionCfEditComposite(parent, this);
	}

}
