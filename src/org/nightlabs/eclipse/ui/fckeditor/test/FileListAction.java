package org.nightlabs.eclipse.ui.fckeditor.test;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.nightlabs.eclipse.ui.fckeditor.file.FileListDialog;
import org.nightlabs.eclipse.ui.fckeditor.file.ImageProvider;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FileListAction implements IWorkbenchWindowActionDelegate
{
	IWorkbenchWindow window;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	@Override
	public void dispose()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	public void init(IWorkbenchWindow window)
	{
		this.window = window;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action)
	{
		try {
			Dialog dialog = new FileListDialog(window.getShell(), TestUtil.getFiles(), new ImageProvider(window.getShell().getDisplay()));
			dialog.open();
		} catch(Exception e) {
			e.printStackTrace();
			MessageDialog.openError(window.getShell(), "Error", e.toString());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
	}
}

