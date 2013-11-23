/**
 *
 */
package org.nightlabs.base.ui.action;

import javax.security.auth.login.LoginException;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;
import org.eclipse.ui.internal.LegacyResourceSupport;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.NewWizard;
import org.eclipse.ui.internal.util.Util;
import org.nightlabs.base.ui.login.Login;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;

/**
 * This class is a subclass of {@link org.eclipse.ui.actions.NewWizardAction},
 * to provide 2 more things.
 * 1. It uses an {@link DynamicPathWizardDialog} instead of normal {@link WizardDialog}
 * so that the {@link IWizardContainer} for all implementations of {@link INewWizard}
 * which are registered at the extension-point org.eclipse.ui.newWizard have it.
 * 2. It performs a {@link Login.login()} before the new wizard dialog opens, to avoid
 * ClassNotFoundExceptions when some implementations of {@link INewWizard} have imports
 * of remote classes.
 *
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
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
    @Override
	public void run()
    {
    	try {
			Login.login();
			doRun();
		} catch (LoginException e) {
			// if not logged in do nothing, because it could lead to NoClassDefFoundError,
			// when instantiating Wizard classes which need remote classes (e.g. have imports)
		} catch (IllegalStateException e2) {
			// if no ILoginDelegate is registered the application has no login,
			// then also just open the newWizard dialog
			doRun();
		}
    }

    /**
     * This method is copied from org.eclipse.ui.actions.NewWizardAction.run()
     * but it uses an DynamicPathWizardDialog for the wizards instead of an "normal"
     * WizardDialog.
     */
    @SuppressWarnings("restriction")
	protected void doRun()
    {
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
            Class<?> resourceClass = LegacyResourceSupport.getResourceClass();
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
