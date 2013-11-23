package org.nightlabs.jseditor.ui.rcp.scriptwizard;

import org.eclipse.jface.text.source.SourceViewer;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;

/**
 * @author unascribed
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class JSEditorScriptWizard extends DynamicPathWizard // implements INewWizard // Why was this implementing INewWizard? I removed this! Marco.
{
	private JSEditorScriptWizardPage page;

//	private IWorkbench workbench;
//	private ISelection selection;

	private SourceViewer sourceViewer;
	
	public JSEditorScriptWizard(SourceViewer sourceViewer){
		super();
		this.sourceViewer = sourceViewer;
	}

//	public void init(IWorkbench w, IStructuredSelection s) {
//		this.workbench = w;
//		this.selection = s;
//	}

	/**
	 * Adding the page to the wizard.
	 */

	@Override
	public void addPages() {
		page = new JSEditorScriptWizardPage(sourceViewer);
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		sourceViewer.getDocument().set(page.getSrcText().getDocumentText());
		return true;
	}
}
