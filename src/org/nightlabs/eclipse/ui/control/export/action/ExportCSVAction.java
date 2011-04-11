/**
 *
 */
package org.nightlabs.eclipse.ui.control.export.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.nightlabs.eclipse.ui.control.export.FocusHistory;
import org.nightlabs.eclipse.ui.control.export.resource.Messages;
import org.nightlabs.eclipse.ui.control.export.wizard.ExportWizard;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class ExportCSVAction implements IWorkbenchWindowActionDelegate {

	public static final String ID = ExportCSVAction.class.getName();

	@Override
	public void dispose() {

	}

	private IWorkbenchWindow window;

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void run(IAction action) {
		if (FocusHistory.sharedInstance().getItems().size() != 0) {
			ExportWizard wizard = new ExportWizard();
			WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
			dialog.setPageSize(SWT.DEFAULT, 300);
			dialog.open();
		}
		else {
			MessageDialog.openWarning(window.getShell(), Messages.getString("org.nightlabs.eclipse.ui.control.export.action.ExportCSVAction.errorDialog.title"), Messages.getString("org.nightlabs.eclipse.ui.control.export.action.ExportCSVAction.errorDialog.message")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}
}
