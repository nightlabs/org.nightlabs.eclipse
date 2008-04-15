/**
 * 
 */
package org.nightlabs.base.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 * @deprecated This dialog is completely useless, since {@link Dialog} is
 * 		centered by default and bounds are stored using the {@link #getDialogBoundsSettings()}
 * 		mechanism. Marc
 */
@Deprecated
public class CenteredTitleDialog 
extends TitleAreaDialog 
{
	public CenteredTitleDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * @deprecated Made final to prevent further use. Use the {@link #getDialogBoundsSettings()}
	 * 		mechanism to store bounds. Marc
	 */
	@Deprecated
	final protected String getDialogIdentifier() {
		return this.getClass().getName();
	}
}
