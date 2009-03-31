/**
 *
 */
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
	public boolean performFinish() {
		OpenCSVUtil.exportControlToCSV(optionPage.getFilePath(), (Control)FocusHistory.sharedInstance().getLastItem().getWidget(), optionPage.getSeparator());
		return true;
	}
}
