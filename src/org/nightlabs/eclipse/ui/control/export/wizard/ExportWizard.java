package org.nightlabs.eclipse.ui.control.export.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.nightlabs.eclipse.ui.control.export.FocusHistory;
import org.nightlabs.eclipse.ui.control.export.OpenCSVUtil;
import org.nightlabs.eclipse.ui.control.export.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class ExportWizard extends Wizard
{
	private IStructuredSelection initialSelection;
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		initialSelection = selection;
	}

	private ExportOptionWizardPage optionPage;
	private ExportPreviewWizardPage previewPage;
	private Control control;

	@Override
	public void addPages() {
		setWindowTitle(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportWizard.wizard.windowTitle")); //$NON-NLS-1$

		optionPage = new ExportOptionWizardPage(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportWizard.optionPage.title")); //$NON-NLS-1$
		addPage(optionPage);

		previewPage = new ExportPreviewWizardPage(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportWizard.previewPage.title")); //$NON-NLS-1$
		addPage(previewPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		Control c = control;
		if (c == null) {
			c = (Control) FocusHistory.sharedInstance().getLastItem().getWidget();
		}
		OpenCSVUtil.exportControlToCSV(optionPage.getFilePath(), c, optionPage.getSeparator());
		return true;
	}

	/**
	 * Returns the control.
	 * @return the control
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * Sets the (optional) control, otherwise it will take the control which got the last focus automatically.
	 * @param control the control to set
	 */
	public void setControl(Control control) {
		this.control = control;
	}

}
