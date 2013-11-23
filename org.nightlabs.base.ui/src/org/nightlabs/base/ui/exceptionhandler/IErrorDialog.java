package org.nightlabs.base.ui.exceptionhandler;

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
	 * @param ExceptionHandlerParam  The thrown exception
	 */
	public void showError(String dialogTitle, String message, ExceptionHandlerParam handlerParam);
	
	/**
	 * Open the error dialog.
	 * @see Window#open()
	 * @return the return code
	 */
	public int open();
}
