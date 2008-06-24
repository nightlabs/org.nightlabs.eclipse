package org.nightlabs.eclipse.ui.fckeditor.test;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorComposite;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorInput;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorInput;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class EditorDialogAction implements IWorkbenchWindowActionDelegate
{
	IWorkbenchWindow window;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	@Override
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	private class MyDialog extends Dialog
	{
		private IFCKEditorInput input;

		/**
		 * Create a new EditorDialogAction.MyDialog instance.
		 */
		public MyDialog(Shell shell, IFCKEditorInput input)
		{
			super(shell);
			this.input = input;
			setShellStyle(getShellStyle() | SWT.RESIZE);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected Control createDialogArea(Composite parent)
		{
			Composite c = (Composite)super.createDialogArea(parent);
			FCKEditorComposite editorComposite = new FCKEditorComposite(c, SWT.NONE, input);
			editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			return c;
		}
	};

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		try {
			final IFCKEditorInput editorInput = new FCKEditorInput(TestUtil.getContent(), "Bla bla "+Math.random());
			MyDialog dlg = new MyDialog(window.getShell(), editorInput);
			dlg.open();
		} catch (Throwable e) {
			MessageDialog.openError(window.getShell(), "Error", "Error: "+e.toString());
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub

	}
}
