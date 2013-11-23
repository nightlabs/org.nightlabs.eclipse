package org.nightlabs.jseditor.ui.scriptwizard;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jseditor.ui.IJSEditor;

public class BuildScriptAction extends Action{
	private IJSEditor targetEditor;
	private Composite composite;
	
	public BuildScriptAction(Composite composite, IJSEditor targetEditor) {
		this.composite = composite;
		this.targetEditor = targetEditor;
	}
	
	@Override
	public void run() {
		JSEditorScriptWizard jsWizard = new JSEditorScriptWizard(targetEditor);
		//Instantiates the wizard container with the wizard and opens it
		WizardDialog dialog = new WizardDialog(composite.getShell(), jsWizard);
		dialog.open();
	}

}
