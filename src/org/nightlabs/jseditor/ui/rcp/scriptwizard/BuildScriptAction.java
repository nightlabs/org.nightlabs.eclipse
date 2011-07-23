package org.nightlabs.jseditor.ui.rcp.scriptwizard;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;

public class BuildScriptAction extends Action{
	private SourceViewer sourceViewer;
	private Composite composite;
	
	public BuildScriptAction(Composite composite, SourceViewer sourceViewer){
		this.composite = composite;
		this.sourceViewer = sourceViewer;
	}
	
	@Override
	public void run() {
		JSEditorScriptWizard jsWizard = new JSEditorScriptWizard(sourceViewer);
		//Instantiates the wizard container with the wizard and opens it
		WizardDialog dialog = new WizardDialog(composite.getShell(), jsWizard);
		dialog.open();
	}

}
