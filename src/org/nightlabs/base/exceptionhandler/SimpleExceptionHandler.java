package org.nightlabs.base.exceptionhandler;

public class SimpleExceptionHandler implements IExceptionHandler
{
	private String message = ""; //$NON-NLS-1$
	public SimpleExceptionHandler(String message)
	{
		this.message = message;
	}
	
	public void handleException(Thread thread, Throwable thrownException, Throwable triggerException)
	{
//		DefaultErrorDialog.addError(DefaultErrorDialog.class, null, message, thrownException, triggerException);
		ErrorDialogFactory.showError(DefaultErrorDialog.class, null, message, thrownException, triggerException);
	}
}
