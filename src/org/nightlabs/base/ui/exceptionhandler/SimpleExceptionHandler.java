package org.nightlabs.base.ui.exceptionhandler;

public class SimpleExceptionHandler implements IExceptionHandler
{
	private String message = ""; //$NON-NLS-1$
	public SimpleExceptionHandler(String message)
	{
		this.message = message;
	}

	@Override
	public boolean handleException(Thread thread, Throwable thrownException, Throwable triggerException)
	{
		ErrorDialogFactory.showError(DefaultErrorDialog.class, null, message, thrownException, triggerException);
		return true;
	}
}
