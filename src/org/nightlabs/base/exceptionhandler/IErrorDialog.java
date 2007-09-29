package org.nightlabs.base.exceptionhandler;

import org.eclipse.jface.window.Window;

/**
 * An interface for error dialogs instanciated by
 * the {@link ErrorDialogFactory}. Implementations
 * must provide a default constructor.
 * 
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface IErrorDialog
{
	/**
	 * Show an error in this error dialog.
	 * @param dialogTitle The dialog title
	 * @param message The error message
	 * @param thrownException The thrown exception
	 * @param triggerException The exception that triggered the 
	 * 		corresponding error handler
	 */
	public void showError(String dialogTitle, String message, Throwable thrownException, Throwable triggerException);
	
	/**
	 * Open the error dialog.
	 * @see Window#open()
	 * @return the return code
	 */
	public int open();
}
