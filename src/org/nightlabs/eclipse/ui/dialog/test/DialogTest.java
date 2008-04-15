// $Id$
package org.nightlabs.eclipse.ui.dialog.test;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.nightlabs.eclipse.ui.dialog.ChangePasswordDialog;
import org.nightlabs.eclipse.ui.dialog.CountdownMessageDialog;
import org.nightlabs.eclipse.ui.dialog.DialogPlugin;
import org.nightlabs.eclipse.ui.dialog.ExpandableAreaDialog;
import org.nightlabs.eclipse.ui.dialog.FullScreenDialog;
import org.nightlabs.eclipse.ui.dialog.RememberDecisionMessageDialog;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class DialogTest
{
	private static abstract class ActionDelegate implements IWorkbenchWindowActionDelegate
	{
		protected IWorkbenchWindow window;
		
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
		 */
		public void dispose()
		{
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
		 */
		public void init(IWorkbenchWindow window)
		{
			this.window = window;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
		 */
		public void selectionChanged(IAction action, ISelection selection)
		{
		}
	}
	
	public static class ChangePasswordDialogTest extends ActionDelegate
	{
		public void run(IAction action)
		{
			String result = ChangePasswordDialog.openDialog(
					window.getShell(), 
					new IInputValidator() {
						@Override
						public String isValid(String newText)
						{
							return null;
						}
				
					},
					null // use default
//					new IPasswordMeter() {
//						@Override
//						public int getMaxPasswordMetric()
//						{
//							// TODO Auto-generated method stub
//							return 0;
//						}
//
//						@Override
//						public int ratePassword(String password)
//						{
//							// TODO Auto-generated method stub
//							return 0;
//						}
//					}
			);
			System.out.println("Result: "+result);
		}
	}

	public static class CountdownMessageDialogTest extends ActionDelegate
	{
		public void run(IAction action)
		{
			CountdownMessageDialog.openInformation(window.getShell(), "The Title", "The message of the message dialog.", 30);
		}
	}

	public static class FullScreenDialogTest extends ActionDelegate
	{
		public void run(IAction action)
		{
			new FullScreenDialog(window.getShell())	{
				/* (non-Javadoc)
				 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
				 */
				@Override
				protected Control createDialogArea(Composite parent)
				{
					Composite c = (Composite)super.createDialogArea(parent);
					new Label(c, SWT.NONE).setText("Bla bla bla jlkhasjkhaksdhkj kjahsdkjh akjsd hkja hsdkj ahksdj hka hdkj ahdskjh akjd hkja hdsk ahsdkj");
					return c;
				}
			}.open();
		}
	}
	
	public static class RememberDecisionMessageDialogTest extends ActionDelegate
	{
		public void run(IAction action)
		{
			boolean result;
			try {
				result = RememberDecisionMessageDialog.openQuestion(
						window.getShell(), 
						"What do you think?", 
						"Should it be 'yes' or 'no'?", 
						"nodeKey", 
						"preferenceKey", 
						new ConfigurationScope());
				System.out.println("Result: "+result);
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
	}

	public static class ExpandableAreaDialogTest extends ActionDelegate
	{
		public void run(IAction action)
		{
			new ExpandableAreaDialog(window.getShell())
			{
				/* (non-Javadoc)
				 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
				 */
				@Override
				protected void configureShell(Shell newShell)
				{
					super.configureShell(newShell);
					setShellStyle(getShellStyle() | SWT.RESIZE);
				}
				
				/* (non-Javadoc)
				 * @see org.nightlabs.eclipse.ui.dialog.ExpandableAreaDialog#createStaticArea(org.eclipse.swt.widgets.Composite)
				 */
				@Override
				protected Composite createStaticArea(Composite parent)
				{
					Composite staticArea = super.createStaticArea(parent);
					new Label(staticArea, SWT.NONE).setText("The static area bla bla bla");
					new Label(staticArea, SWT.NONE).setText("The static area bla bla bla");
					new Label(staticArea, SWT.NONE).setText("The static area bla bla bla");
					return staticArea;
				}
				
				/* (non-Javadoc)
				 * @see org.nightlabs.eclipse.ui.dialog.ExpandableAreaDialog#createExpandableArea(org.eclipse.swt.widgets.Composite)
				 */
				@Override
				protected Composite createExpandableArea(Composite parent)
				{
					Composite expandableArea = super.createExpandableArea(parent);
					new Label(expandableArea, SWT.NONE).setText("The expandable area bla bla bla");
					new Label(expandableArea, SWT.NONE).setText("The expandable area bla bla bla");
					new Label(expandableArea, SWT.NONE).setText("The expandable area bla bla bla");
					return expandableArea;
				}
				
				/* (non-Javadoc)
				 * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
				 */
				@Override
				protected IDialogSettings getDialogBoundsSettings()
				{
					String sectionName = getClass().getName()+".dialogBounds";
					IDialogSettings dialogSettings = DialogPlugin.getDefault().getDialogSettings();
					IDialogSettings boundsSettings = dialogSettings.getSection(sectionName);
					if(boundsSettings == null)
						boundsSettings = dialogSettings.addNewSection(sectionName);
					return boundsSettings;
				}
			}.open();
		}
	}
}
