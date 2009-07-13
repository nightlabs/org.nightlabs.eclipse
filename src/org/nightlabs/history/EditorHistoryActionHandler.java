package org.nightlabs.history;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.commands.ActionHandler;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditorHistoryActionHandler
extends AbstractHandler
{
	private ActionHandler actionHandler;

	/**
	 *
	 */
	public EditorHistoryActionHandler(IAction action) {
		super();
		this.actionHandler = new ActionHandler(action);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#addHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		actionHandler.addHandlerListener(handlerListener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return actionHandler.execute(event);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return actionHandler.isEnabled();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#isHandled()
	 */
	@Override
	public boolean isHandled() {
		return actionHandler.isHandled();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#removeHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		actionHandler.removeHandlerListener(handlerListener);
	}

}
