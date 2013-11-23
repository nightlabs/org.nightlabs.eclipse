package org.nightlabs.base.ui.exceptionhandler;

import java.util.Set;

/**
 * Used as result for {@link ExceptionHandlerRegistry#searchHandler(Throwable, Set)}.
 * @author Alexander Bieber
 * @author marco schulze - marco at nightlabs dot de
 */
public class ExceptionHandlerSearchResult {
	private ExceptionHandlerRegistryItem exceptionHandlerRegistryItem;
	private Throwable triggerException;

	public ExceptionHandlerRegistryItem getExceptionHandlerRegistryItem() {
		return exceptionHandlerRegistryItem;
	}
	public void setExceptionHandlerRegistryItem(
			ExceptionHandlerRegistryItem exceptionHandlerRegistryItem) {
		this.exceptionHandlerRegistryItem = exceptionHandlerRegistryItem;
	}

	public Throwable getTriggerException() {
		return triggerException;
	}
	public void setTriggerException(Throwable triggerException) {
		this.triggerException = triggerException;
	}
}