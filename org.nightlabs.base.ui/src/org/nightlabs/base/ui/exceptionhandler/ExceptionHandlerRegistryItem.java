package org.nightlabs.base.ui.exceptionhandler;

public class ExceptionHandlerRegistryItem
{
	private String targetTypeName;
	private IExceptionHandler exceptionHandler;
	private int priority;

	public ExceptionHandlerRegistryItem(String targetTypeName, IExceptionHandler exceptionHandler, int priority) {
		if (priority < 0 || priority > 999)
			priority = 500;

		this.targetTypeName = targetTypeName;
		this.exceptionHandler = exceptionHandler;
		this.priority = priority;
	}

	public String getTargetTypeName() {
		return targetTypeName;
	}
	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}
	public int getPriority() {
		return priority;
	}
}
