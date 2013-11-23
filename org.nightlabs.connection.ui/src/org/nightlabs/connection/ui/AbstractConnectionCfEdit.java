package org.nightlabs.connection.ui;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.connection.config.ConnectionCf;


public abstract class AbstractConnectionCfEdit
implements ConnectionCfEdit
{
	private ConnectionCfEditFactory connectionCfEditFactory;
	private ConnectionCf connectionCf;

	public ConnectionCf getConnectionCf()
	{
		return connectionCf;
	}

	public void setConnectionCf(ConnectionCf connectionCf)
	{
		this.connectionCf = connectionCf;
	}

	public ConnectionCfEditFactory getConnectionCfEditFactory()
	{
		return connectionCfEditFactory;
	}

	public void setConnectionCfEditFactory(
			ConnectionCfEditFactory connectionCfEditFactory)
	{
		this.connectionCfEditFactory = connectionCfEditFactory;
	}

	public void init()
	{
	}

	private Composite connectionCfEditComposite;

	/**
	 * Implement this method instead of {@link #createConnectionCfEditComposite(Composite)}!
	 * Note, that {@link #createConnectionCfEditComposite(Composite)} calls
	 * {@link ConnectionCfEdit#load()} after this method. Therefore, you should not fill
	 * your UI elements in this method and you should not call <code>load()</code> manually.
	 * You must, however, implement <code>load()</code> correctly!
	 *
	 * @param parent The parent composite.
	 * @return The new composite.
	 */
	protected abstract Composite _createConnectionCfEditComposite(Composite parent);

	/**
	 * Do not override this method! Implement {@link #_createConnectionCfEditComposite(Composite)}
	 * instead!
	 */
	public Composite createConnectionCfEditComposite(Composite parent)
	{
		if (connectionCfEditComposite != null) {
			connectionCfEditComposite.dispose();
			connectionCfEditComposite = null;
		}

		connectionCfEditComposite = _createConnectionCfEditComposite(parent);
//		load();
		return connectionCfEditComposite;
	}

	public Composite getConnectionCfEditComposite()
	{
		return connectionCfEditComposite;
	}
}
