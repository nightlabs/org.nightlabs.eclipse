/**
 * 
 */
package org.nightlabs.base.action;

import org.eclipse.jface.action.Action;
import org.eclipse.update.ui.UpdateManagerUI;
import org.nightlabs.base.util.RCPUtil;

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
		setText("Update");
		setToolTipText("Update");
	}

	@Override
	public void run() {
		UpdateManagerUI.openInstaller(RCPUtil.getActiveWorkbenchShell()); 
	}

}
