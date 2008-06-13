package org.nightlabs.base.ui.exceptionhandler;

public class SimpleExceptionHandler implements IExceptionHandler
{
	private String message = ""; //$NON-NLS-1$
	public SimpleExceptionHandler(String message)
	{
		this.message = message;
	}

	@Override
	public boolean handleException(ExceptionHandlerParam handlerParam)
	{
		ErrorDialogFactory.showError(DefaultErrorDialog.class, null, message,handlerParam);
		return true;
	}
}
