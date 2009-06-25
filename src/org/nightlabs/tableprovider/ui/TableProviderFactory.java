package org.nightlabs.tableprovider.ui;

/**
 * Factory object for creating certain kinds of {@link TableProvider}.s
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 */
public interface TableProviderFactory
{
	/**
	 * Creates a new instance of a certain implementation of {@link TableProvider}.
	 *
	 * @return a new instance of the corresponding {@link TableProvider}
	 */
	TableProvider createTableProvider();

	String getInputClass();

	void setInputClass(String inputClassName);

	String getScopeClass();

	void setScopeClass(String scopeClassName);
}
