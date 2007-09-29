package org.nightlabs.base.exceptionhandler;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * A class providing static methods for error handling.
 * Errors are handled by showing an instance of the given
 * {@link IErrorDialog} class. This instance may be re-used
 * if the implementation supports this.
 * <p>
 * Error dialogs should never be opened directly, but only
 * by using this class.
 * 
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class ErrorDialogFactory
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ErrorDialogFactory.class);
	
	/**
	 * The error dialog registry.
	 */
	private static Map<Class, IErrorDialog> errorDialogs = null;

	/**
	 * Get an error dialog instance from the registry.
	 * @param dialogClass The error dialog class
	 * @return The error dialog instance if on is registered or
	 * 		<code>null</code> if no registered instance was found.
	 */
	private static IErrorDialog getErrorDialog(Class<? extends IErrorDialog> dialogClass)
	{
		IErrorDialog dialog = null;
		if(errorDialogs != null)
			dialog = errorDialogs.get(dialogClass);
		if(dialog == null) {			
			try {
				dialog = (IErrorDialog) dialogClass.newInstance();
			} catch(Exception e) {
				logger.fatal("Error occured when trying to instantiate " + dialogClass.getName() + " with default constructor.", e); //$NON-NLS-1$ //$NON-NLS-2$
			}						
		}
		return dialog;
	}

	/**
	 * Show an error using the given {@link IErrorDialog} implementation.
	 * If there is an instance of the given error dialog class already
	 * registered for re-use, this instance will be used.
	 * @param dialogClass The error dialog class
	 * @param dialogTitle The error dialog title
	 * @param message The error message
	 * @param thrownException The exception that was thrown
	 * @param triggerException The exception that triggered the error handler 
	 * @return <code>true</code> if the error could successfully be shown - 
	 * 		<code>false</code> otherwise.
	 */
	public static boolean showError(Class<? extends IErrorDialog> dialogClass, String dialogTitle, String message, Throwable thrownException, Throwable triggerException)
	{
		IErrorDialog dialog = getErrorDialog(dialogClass);
		if(dialog == null)
			return false;
		dialog.showError(dialogTitle, message, thrownException, triggerException);
		dialog.open();
		return true;
	}
	
//	/**
//	 * Show an error using the given {@link IErrorDialog} implementation.
//	 * If there is an instance of the given error dialog class already
//	 * registered for re-use, this instance will be used.
//	 * @param dialogClass The error dialog class
//	 * @param errorReport The error report to use
//	 * @return <code>true</code> if the error could successfully be shown - 
//	 * 		<code>false</code> otherwise.
//	 * 
//	 * @deprecated Use {@link #showError(Class, String, String, Throwable, Throwable)} instead.
//	 */
//	public static boolean showError(Class<? extends IErrorDialog> dialogClass, ErrorReport errorReport)
//	{
//		return showError(dialogClass, null, null, errorReport.getThrownException(), errorReport.getTriggerException());
//	}
	
	/**
	 * {@link IErrorDialog} implementations that want to be re-used for
	 * error message should register themselfs using this method.
	 * @param errorDialog The error dialog to add to the registry
	 */
	public static void addDialog(IErrorDialog errorDialog)
	{
		if(errorDialogs == null)
			errorDialogs = new HashMap<Class, IErrorDialog>(1);
		errorDialogs.put(errorDialog.getClass(), errorDialog);
	}

	/**
	 * {@link IErrorDialog} implementations that are registerd for re-use
	 * by having called {@link #addDialog(IErrorDialog)} can unregister
	 * themselfs by calling theis method 
	 * @param errorDialog The error dialog to remove from registry
	 */
	public static void removeDialog(IErrorDialog errorDialog)
	{
		if(errorDialogs != null) {
			IErrorDialog instance = errorDialogs.get(errorDialog.getClass());
			if(instance != null && instance == errorDialog)
				errorDialogs.remove(errorDialog.getClass());
		}
	}
}
