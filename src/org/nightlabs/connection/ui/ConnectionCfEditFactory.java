package org.nightlabs.connection.ui;

import org.nightlabs.connection.ConnectionImplementation;
import org.nightlabs.connection.config.ConnectionCf;

public interface ConnectionCfEditFactory
{
	void setConnectionImplementation(ConnectionImplementation connectionImplementation);
	ConnectionImplementation getConnectionImplementation();

	/**
	 * This method is called after all properties have been set (i.e. the setters have been called).
	 */
	void init();

	/**
	 * This method creates a new instance of a class implementing {@link ConnectionCfEdit}
	 * and calls the following methods in order to initialize it:
	 * <ul>
	 *		<li>
	 *		{@link ConnectionCfEdit#setConnectionCfEditFactory(ConnectionCfEditFactory)}
	 *		</li>
	 *		<li>
	 *		{@link ConnectionCfEdit#setConnectionCf(ConnectionCf)}
	 *		</li>
	 *		<li>
	 *		{@link ConnectionCfEdit#init()} - this method <b>must</b> be called last!
	 *		</li>
	 * </ul>
	 *
	 * @return A new instance of an implementation for {@link ConnectionCfEdit}.
	 */
	ConnectionCfEdit createConnectionCfEdit(ConnectionCf connectionCf);
}
