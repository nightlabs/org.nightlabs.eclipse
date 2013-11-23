package org.nightlabs.base.ui.message;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public interface IErrorMessageDisplayer 
extends IMessageDisplayer 
{
	/**
	 * Sets the errorMessage, set null to clear.
	 * 
	 * @param errorMessage the errorMessage to display,
	 * null clears the errorMessage.
	 */
	public void setErrorMessage(String errorMessage);
}
