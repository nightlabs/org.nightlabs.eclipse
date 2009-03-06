/**
 * 
 */
package org.nightlabs.base.ui.action;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;
import org.eclipse.ui.internal.LegacyResourceSupport;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.NewWizard;
import org.eclipse.ui.internal.util.Util;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class NewWizardAction extends org.eclipse.ui.actions.NewWizardAction {

	private IWorkbenchWindow window;
    /**
     * The wizard dialog width
     */
    private static final int SIZING_WIZARD_WIDTH = 500;

    /**
     * The wizard dialog height
     */
    private static final int SIZING_WIZARD_HEIGHT = 500;
    
	/**
	 * @param window
	 */
	public NewWizardAction(IWorkbenchWindow window) {
		super(window);
		this.window = window;
		setId(ActionFactory.NEW.getId());
	}

	/* (non-Javadoc)
     * Method declared on IAction.
     */
    public void run() {
        if (window == null) {
            // action has been disposed
            return;
        }
        NewWizard wizard = new NewWizard();
        wizard.setCategoryId(getCategoryId());

        ISelection selection = window.getSelectionService()
                .getSelection();
        IStructuredSelection selectionToPass = StructuredSelection.EMPTY;
        if (selection instanceof IStructuredSelection) {
            selectionToPass = (IStructuredSelection) selection;
        } else {
            // @issue the following is resource-specific legacy code
            // Build the selection from the IFile of the editor
            Class resourceClass = LegacyResourceSupport.getResourceClass();
            if (resourceClass != null) {
                IWorkbenchPart part = window.getPartService()
                        .getActivePart();
                if (part instanceof IEditorPart) {
                    IEditorInput input = ((IEditorPart) part).getEditorInput();
                    Object resource = Util.getAdapter(input, resourceClass);
                    if (resource != null) {
                        selectionToPass = new StructuredSelection(resource);
                    }
                }
            }
        }

        wizard.init(window.getWorkbench(), selectionToPass);
        IDialogSettings workbenchSettings = WorkbenchPlugin.getDefault()
                .getDialogSettings();
        IDialogSettings wizardSettings = workbenchSettings
                .getSection("NewWizardAction"); //$NON-NLS-1$
        if (wizardSettings == null) {
			wizardSettings = workbenchSettings.addNewSection("NewWizardAction"); //$NON-NLS-1$
		}
        wizard.setDialogSettings(wizardSettings);
        wizard.setForcePreviousAndNextButtons(true);

        Shell parent = window.getShell();
//        WizardDialog dialog = new WizardDialog(parent, wizard);
        WizardDialog dialog = new DynamicPathWizardDialog(parent, wizard);
        dialog.create();
        dialog.getShell().setSize(
                Math.max(SIZING_WIZARD_WIDTH, dialog.getShell().getSize().x),
                SIZING_WIZARD_HEIGHT);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(),
				IWorkbenchHelpContextIds.NEW_WIZARD);
        dialog.open();
    }	
}
