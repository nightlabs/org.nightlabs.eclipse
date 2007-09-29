package org.nightlabs.base.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class WorkbenchWindowAndViewActionDelegate implements IViewActionDelegate, IWorkbenchWindowActionDelegate
{
	/**
	 * The shell for this action.
	 */
	private Shell shell = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view)
	{
		shell = view.getViewSite().getShell();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window)
	{
		shell = window.getShell();
	}

	/**
	 * Get the shell.
	 * @return the shell
	 */
	public Shell getShell()
	{
		return shell;
	}
}
