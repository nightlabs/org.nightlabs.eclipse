/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.nightlabs.base.config.DialogCf;
import org.nightlabs.base.config.DialogCfMod;
import org.nightlabs.config.Config;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class CenteredDialog 
extends Dialog 
{
	private static final Logger logger = Logger.getLogger(CenteredDialog.class);

	/**
	 * @param parentShell
	 */
	public CenteredDialog(Shell parentShell) {
		super(parentShell);	
	}

	/**
	 * @param parentShell
	 */
	public CenteredDialog(IShellProvider parentShell) {
		super(parentShell);	
	}

	protected DialogCfMod getDialogCfMod()
	{
		return (DialogCfMod) Config.sharedInstance().createConfigModule(DialogCfMod.class);
	}

	protected String getDialogIdentifier()
	{
		return this.getClass().getName();
	}

	@Override
	public boolean close()
	{
		if (getShell() == null)
			logger.error("No shell!", new IllegalStateException("No shell existing!")); //$NON-NLS-1$ //$NON-NLS-2$
		else {
			getDialogCfMod().createDialogCf(
					getDialogIdentifier(),
					getShell().getLocation().x,
					getShell().getLocation().y,
					getShell().getSize().x,
					getShell().getSize().y);
		}
		return super.close();
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);

		DialogCf cf = getDialogCfMod().getDialogCf(getDialogIdentifier());
		if (cf == null) {
			setToCenteredLocation(newShell);
		}
		else {
			newShell.setLocation(cf.getX(), cf.getY());
			newShell.setSize(cf.getWidth(), cf.getHeight());
		}
	}

	public void create() 
	{
		super.create();

//		DialogCf cf = getDialogCfMod().getDialogCf(getDialogIdentifier());
//		if (cf == null) {
//			setToCenteredLocation();
//		}
//		else {
//			getShell().setLocation(cf.getX(), cf.getY());
//			getShell().setSize(cf.getWidth(), cf.getHeight());
//		}
	}

	/**
	 * This is called by {@link #create()} but can be used to have a centered dialog with a specific size.
	 * To center the dialog its current size will be taken.
	 * <p>
	 * A code snippet for that would be (overwriting create()):
	 * <pre>
	 * super.create();
	 * getShell().setSize(300, 400);
	 * setToCenteredLocation();
	 * </pre>
	 * </p>
	 * <p>
	 * Please do not call this method outside of your implementation oif {@link #configureShell(Shell)}!
	 * Instead (after <code>configureShell(Shell)</code> was called) you should call {@link #setToCenteredLocation()}.
	 * </p>
	 */
	protected void setToCenteredLocation(Shell newShell) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point shellSize = newShell.getSize();
		int diffWidth = screenSize.width - shellSize.x;
		int diffHeight = screenSize.height - shellSize.y;
		newShell.setLocation(diffWidth/2, diffHeight/2);
	}

	protected void setToCenteredLocation() {
		setToCenteredLocation(getShell());
	}

	/**
	 * Use this method to create a centered dialog of a specific size.
	 * Note that the size given here will only apply to dialogs whose 
	 * size and location was not previously stored, so this method should
	 * be used to initialise a dialog that is created for the first time.
	 * <p>
	 * A code snippet for doing so while overwriting {@link #configureShell(Shell)} is:
	 * <pre>
	 * super.configureShell(Shell)
	 * setToCenteredLocationPreferredSize(300, 400);
	 * </pre>
	 * </p>
	 * <p>
	 * Please do not call this method outside of your implementation oif {@link #configureShell(Shell)}!
	 * Instead (after <code>configureShell(Shell)</code> was called) you should call {@link #setToCenteredLocationPreferredSize(int, int)}.
	 * </p>
	 * 
	 * @param width The preferred width of the dialog (when no width was stored previously)
	 * @param height The preferred height of the dialog (when no height was stored previously)
	 */
	protected void setToCenteredLocationPreferredSize(Shell newShell, int width, int height) {
		DialogCf cf = getDialogCfMod().getDialogCf(getDialogIdentifier());
		if (cf == null) {
			newShell.setSize(width, height);
			setToCenteredLocation(newShell);
		}
		else {
			newShell.setSize(cf.getWidth(), cf.getHeight());
			newShell.setLocation(cf.getX(), cf.getY());
		}
	}

	/**
	 * Since this method relies on the shell being already instantiated, you <b>must</b> only call it
	 * after {@link #create()} has been called!
	 *   
	 * @param width the width if none has been set before.
	 * @param height the height if none has been set before.
	 */
	protected void setToCenteredLocationPreferredSize(int width, int height) {
		setToCenteredLocationPreferredSize(getShell(), width, height);
	}
	
	public boolean checkWidget(Widget w) 
	{
		if (w != null && !w.isDisposed())
			return true;
		else
			return false;
	}	
}
