/**
 * 
 */
package org.nightlabs.base.ui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.update.ui.UpdateManagerUI;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.util.RCPUtil;

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
		UpdateManagerUI.openInstaller(RCPUtil.getActiveShell());
	}

}
