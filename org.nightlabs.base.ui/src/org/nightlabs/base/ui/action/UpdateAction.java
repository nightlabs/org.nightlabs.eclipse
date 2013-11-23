/**
 * 
 */
package org.nightlabs.base.ui.action;

import org.eclipse.jface.action.Action;
import org.nightlabs.base.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class UpdateAction
extends Action
{
	public static final String ID = UpdateAction.class.getName();
	
	public UpdateAction() {
		super();
		setId(ID);
		setText(Messages.getString("org.nightlabs.base.ui.action.UpdateAction.text.update")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.base.ui.action.UpdateAction.tooltip.update")); //$NON-NLS-1$
	}

	@Override
	public void run() {
		// FIXME commented while creating new jfire.min maven assembly, but. Is this used anyway?
//		UpdateManagerUI.openInstaller(RCPUtil.getActiveShell());
	}

}
