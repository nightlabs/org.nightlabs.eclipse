package org.nightlabs.base.ui.exceptionhandler;

public class SimpleExceptionHandler implements IExceptionHandler
{
	private String message = ""; //$NON-NLS-1$
	private String title = ""; //$NON-NLS-1$

	public SimpleExceptionHandler(String message, String title)
	{
		this.message = message;
		this.title = title;
	}

	@Override
	public boolean handleException(ExceptionHandlerParam handlerParam)
	{
		ErrorDialogFactory.showError(DefaultErrorDialog.class, title, message, handlerParam);
		return true;
	}
}
