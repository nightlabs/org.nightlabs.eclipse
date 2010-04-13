package org.nightlabs.base.ui.action;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * This is a wrapper class that wraps an {@link IViewActionDelegate} around an {@link Action}. 
 * For example, one can use in the context menus, which do not implement the {@link IAction}, to synchronise with the toolbar
 * menus setup to work with views in the plugins.
 * 
 * @author khaireel at nightlabs dot de
 */
public class ViewActionDelegateWrapperAction extends Action implements IViewActionDelegate {
	private static final Logger logger = Logger.getLogger(ViewActionDelegateWrapperAction.class);
	private IViewActionDelegate actionDelegate;

	/**
	 * Creates a new instance of the ViewActionDelegateWrapperAction.
	 */
	public ViewActionDelegateWrapperAction(IViewPart view, IViewActionDelegate actionDelegate, String id, String text, ImageDescriptor imageDescriptor) {
		this.actionDelegate = actionDelegate;

		if (actionDelegate instanceof IAction)
			logger.warn("<init>: delegate implements IAction! It is not necessary to use this wrapper for instance of " + actionDelegate.getClass().getName()); //$NON-NLS-1$

		actionDelegate.init(view);
		if (id == null)
			this.setId(actionDelegate.getClass().getName());
		else
			this.setId(id);

		setText(text);

		if (imageDescriptor != null)
			setImageDescriptor(imageDescriptor);
	}

	@Override
	public void run() { run(this); }

	@Override
	public void run(IAction action) { actionDelegate.run(action); }

	@Override
	public void selectionChanged(IAction action, ISelection selection) { actionDelegate.selectionChanged(action, selection); }

	@Override
	public void init(IViewPart view) { throw new UnsupportedOperationException("This method should never be used."); }
}
