// $Id$
package org.nightlabs.base.ui.dialog;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class RememberDecisionMessageDialog extends MessageDialog
{
	private boolean rememberDecision;
	
  /**
   * Create a message dialog. Note that the dialog will have no visual
   * representation (no widgets) until it is told to open.
   * <p>
   * The labels of the buttons to appear in the button bar are supplied in
   * this constructor as an array. The <code>open</code> method will return
   * the index of the label in this array corresponding to the button that was
   * pressed to close the dialog. If the dialog was dismissed without pressing
   * a button (ESC, etc.) then -1 is returned. Note that the <code>open</code>
   * method blocks.
   * </p>
   * 
   * @param parentShell
   *            the parent shell
   * @param dialogTitle
   *            the dialog title, or <code>null</code> if none
   * @param dialogTitleImage
   *            the dialog title image, or <code>null</code> if none
   * @param dialogMessage
   *            the dialog message
   * @param dialogImageType
   *            one of the following values:
   *            <ul>
   *            <li><code>MessageDialog.NONE</code> for a dialog with no
   *            image</li>
   *            <li><code>MessageDialog.ERROR</code> for a dialog with an
   *            error image</li>
   *            <li><code>MessageDialog.INFORMATION</code> for a dialog
   *            with an information image</li>
   *            <li><code>MessageDialog.QUESTION </code> for a dialog with a
   *            question image</li>
   *            <li><code>MessageDialog.WARNING</code> for a dialog with a
   *            warning image</li>
   *            </ul>
   * @param dialogButtonLabels
   *            an array of labels for the buttons in the button bar
   * @param defaultIndex
   *            the index in the button label array of the default button
   */
	public RememberDecisionMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex)
	{
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createCustomArea(Composite parent)
	{
		final Button rememberDecisionButton = new Button(parent, SWT.CHECK);
		rememberDecisionButton.setText("&Remember my decision");
		rememberDecisionButton.setSelection(rememberDecision);
		rememberDecisionButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				rememberDecision = rememberDecisionButton.getSelection();
			}
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				rememberDecision = rememberDecisionButton.getSelection();
			}
		});
		return rememberDecisionButton;
	}
	
  /**
   * Convenience method to open a simple confirm (OK/Cancel) dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   * @return <code>true</code> if the user presses the OK button,
   *         <code>false</code> otherwise
   */
  public static boolean openConfirm(Shell parent, String title, String message) {
  	RememberDecisionMessageDialog dialog = new RememberDecisionMessageDialog(parent, title, null, // accept
              // the
              // default
              // window
              // icon
              message, QUESTION, new String[] { IDialogConstants.OK_LABEL,
                      IDialogConstants.CANCEL_LABEL }, 0); // OK is the
      // default
      return dialog.open() == 0;
  }

  /**
   * Convenience method to open a simple Yes/No question dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   * @return <code>true</code> if the user presses the OK button,
   *         <code>false</code> otherwise
   */
  public static boolean openQuestion(Shell parent, String title,
          String message) {
  	RememberDecisionMessageDialog dialog = new RememberDecisionMessageDialog(parent, title, null, // accept
              // the
              // default
              // window
              // icon
              message, QUESTION, new String[] { IDialogConstants.YES_LABEL,
                      IDialogConstants.NO_LABEL }, 0); // yes is the default
      return dialog.open() == 0;
  }
  
  
  
  // ----------------
  
  
  
  /**
   * Convenience method to open a simple confirm (OK/Cancel) dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   * @return <code>true</code> if the user presses the OK button,
   *         <code>false</code> otherwise
	 * @throws BackingStoreException if this operation cannot be 
	 * 		completed due to a failure in the backing store, or inability
	 * 		to communicate with it.
   */
  public static boolean openConfirm(Shell parent, String title, String message, String nodeKey, String preferenceKey, IScopeContext preferenceScope) throws BackingStoreException {
  	Boolean rememberedDecision = getRememberedDecision(nodeKey, preferenceKey, preferenceScope);
  	if(rememberedDecision != null)
  		return rememberedDecision;
  	RememberDecisionMessageDialog dialog = new RememberDecisionMessageDialog(parent, title, null, // accept the default window icon
  			message, QUESTION, new String[] { IDialogConstants.OK_LABEL,
  			IDialogConstants.CANCEL_LABEL }, 0); // OK is the default
  	int result = dialog.open();
  	if((result == 0 || result == 1) && dialog.isRememberDecision())
  		setRememberedDecision(nodeKey, preferenceKey, preferenceScope, result == 0);
  	return result == 0;
  }

  /**
   * Convenience method to open a simple Yes/No question dialog.
   * 
   * @param parent
   *            the parent shell of the dialog, or <code>null</code> if none
   * @param title
   *            the dialog's title, or <code>null</code> if none
   * @param message
   *            the message
   * @return <code>true</code> if the user presses the OK button,
   *         <code>false</code> otherwise
	 * @throws BackingStoreException if this operation cannot be 
	 * 		completed due to a failure in the backing store, or inability
	 * 		to communicate with it.
   */
  public static boolean openQuestion(Shell parent, String title, String message, String nodeKey, String preferenceKey, IScopeContext preferenceScope) throws BackingStoreException {
  	Boolean rememberedDecision = getRememberedDecision(nodeKey, preferenceKey, preferenceScope);
  	if(rememberedDecision != null)
  		return rememberedDecision;
  	RememberDecisionMessageDialog dialog = new RememberDecisionMessageDialog(parent, title, null, // accept the default window icon
              message, QUESTION, new String[] { IDialogConstants.YES_LABEL,
                      IDialogConstants.NO_LABEL }, 0); // yes is the default
  	int result = dialog.open();
  	if((result == 0 || result == 1) && dialog.isRememberDecision())
  		setRememberedDecision(nodeKey, preferenceKey, preferenceScope, result == 0);
  	return result == 0;
  }

	/**
	 * Get the remembered decision for the given keys.
	 * @param nodeKey The preferences node key (usually the plugin id)
	 * @param preferenceKey The preference key
	 * @param preferenceScope The preference scope
	 * @return The remembered decision if there is one - <code>null</code>
	 * 		otherwise
	 */
	private static Boolean getRememberedDecision(String nodeKey, String preferenceKey, IScopeContext preferenceScope)
	{
		Boolean rememberedDecision = null;
  	IEclipsePreferences node = preferenceScope.getNode(nodeKey);
  	if(node != null) {
  		String value = node.get(preferenceKey, null);
  		if(value != null)
  			rememberedDecision = Boolean.parseBoolean(value);
  	}
		return rememberedDecision;
	}

	/**
	 * Set the remembered decision for the given keys.
	 * @param nodeKey The preferences node key (usually the plugin id)
	 * @param preferenceKey The preference key
	 * @param preferenceScope The preference scope
	 * @param decision The decision to store
	 * @return The remembered decision if there is one - <code>null</code>
	 * 		otherwise
	 * @throws BackingStoreException if this operation cannot be 
	 * 		completed due to a failure in the backing store, or inability
	 * 		to communicate with it.
	 */
	private static void setRememberedDecision(String nodeKey, String preferenceKey, IScopeContext preferenceScope, boolean decision) throws BackingStoreException
	{
  	IEclipsePreferences node = preferenceScope.getNode(nodeKey);
  	if(node == null)
  		throw new IllegalStateException("getNode returned null for "+nodeKey);
  	node.putBoolean(preferenceKey, decision);
  	node.flush();
	}
	
	/**
	 * Get the remember decision value.
	 * @return the remember decision value
	 */
	public boolean isRememberDecision()
	{
		return rememberDecision;
	}

	/**
	 * Set the remember decision value. This is only useful, before the
	 * dialog is open. It defines the initial check box selection.
	 * @param rememberDecision the remember decision value to set
	 */
	public void setRememberDecision(boolean rememberDecision)
	{
		this.rememberDecision = rememberDecision;
	}
}
