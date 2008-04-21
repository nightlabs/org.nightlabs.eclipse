package org.nightlabs.base.ui.dialog;

import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 * @deprecated Inherit {@link ResizableTitleAreaDialog} or use {@link org.nightlabs.eclipse.ui.dialog.ResizableDialogSupport}.
 */
@Deprecated
public class CenteredTitleDialog 
extends ResizableTitleAreaDialog 
{
	public CenteredTitleDialog(Shell parentShell) {
		super(parentShell, null);
	}

	/**
	 * @deprecated Made final to prevent further use. Use the {@link #getDialogBoundsSettings()}
	 * 		mechanism to store bounds or inherit {@link ResizableTrayDialog} or use 
	 * 		{@link org.nightlabs.eclipse.ui.dialog.ResizableDialogSupport}.
	 */
	@Deprecated
	final protected String getDialogIdentifier() {
		return this.getClass().getName();
	}
}
