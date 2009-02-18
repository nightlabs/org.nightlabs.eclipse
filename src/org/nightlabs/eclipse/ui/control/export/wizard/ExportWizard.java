/**
 * 
 */
package org.nightlabs.eclipse.ui.control.export.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

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
		setWindowTitle("Export");

		optionPage = new ExportOptionWizardPage("Option Page");
		addPage(optionPage);

		previewPage = new ExportPreviewWizardPage("Preview Page");
		addPage(previewPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return false;
	}
}
