package org.nightlabs.connection.ui;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.connection.config.ConnectionCf;

public interface ConnectionCfEdit
{
	void setConnectionCfEditFactory(ConnectionCfEditFactory factory);
	ConnectionCfEditFactory getConnectionCfEditFactory();

	void setConnectionCf(ConnectionCf connectionCf);
	ConnectionCf getConnectionCf();

	void init();

	/**
	 * This method creates a new <code>Composite</code> and stores it locally
	 * to allow {@link #getConnectionCfEditComposite()} to return it on a subsequent call.
	 * <p>
	 * If this method is called a second time, it is expected to dispose the previously
	 * created composite and to create a new instance.
	 * </p>
	 * The returned UI should already have been filled with correct data (the {@link #load()}
	 * method might never be called).
	 *
	 * @param parent The parent of the new composite.
	 * @return The new composite containing UI for modification of the {@link ConnectionCf}.
	 */
	Composite createConnectionCfEditComposite(Composite parent);

	/**
	 * This method returns the composite that has been created by {@link #createConnectionCfEditComposite(Composite)}.
	 * If the method has not been called, it returns <code>null</code>.
	 *
	 * @return The UI created by {@link #createConnectionCfEditComposite(Composite)}.
	 */
	Composite getConnectionCfEditComposite();

//	/**
//	 * This method loads the data from the {@link ConnectionCf} and fills it into the
//	 * UI elements. It is used to load the data again, after the composite has been created
//	 * (e.g. to revert the modifications). It might never be called! {@link #createConnectionCfEditComposite(Composite)}
//	 * should already fill all data into the UI elements!
//	 * <p>
//	 * This method should be called after {@link #createConnectionCfEditComposite(Composite)}.
//	 * If it is called before, this method should throw an {@link IllegalStateException}.
//	 * </p>
//	 */
//	void load();
//
//	/**
//	 * This method takes the data from the UI elements and fills the {@link ConnectionCf}
//	 * with them.
//	 * <p>
//	 * This method should be called after {@link #createConnectionCfEditComposite(Composite)}.
//	 * If it is called before, this method should throw an {@link IllegalStateException}.
//	 * </p>
//	 */
//	void save();
}
