package org.nightlabs.tableprovider.ui;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public abstract class AbstractTableProviderFactory
implements TableProviderFactory
{
	private String inputClass;
	private String scopeClass;

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProviderFactory#getInputClass()
	 */
	@Override
	public String getInputClass() {
		return inputClass;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProviderFactory#getScopeClass()
	 */
	@Override
	public String getScopeClass() {
		return scopeClass;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProviderFactory#setInputClass(java.lang.String)
	 */
	@Override
	public void setInputClass(String inputClassName) {
		this.inputClass = inputClassName;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProviderFactory#setScopeClass(java.lang.String)
	 */
	@Override
	public void setScopeClass(String scopeClassName) {
		this.scopeClass = scopeClassName;
	}

}
