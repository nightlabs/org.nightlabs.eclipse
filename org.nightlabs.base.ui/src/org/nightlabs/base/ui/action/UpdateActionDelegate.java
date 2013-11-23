/**
 * 
 */
package org.nightlabs.base.ui.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class UpdateActionDelegate
implements IWorkbenchWindowActionDelegate
{
	private Shell shell = null;
	public void dispose() {
		this.shell = null;
	}

	public void init(IWorkbenchWindow window) {
		this.shell = window.getShell();
	}

	public void run(IAction action) {
		// FIXME commented while creating new jfire.min maven assembly, but. Is this used anyway?
//		UpdateManagerUI.openInstaller(shell);
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}
}
