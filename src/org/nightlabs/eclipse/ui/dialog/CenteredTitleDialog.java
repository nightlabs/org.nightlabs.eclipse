/**
 * 
 */
package org.nightlabs.eclipse.ui.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.nightlabs.config.Config;
import org.nightlabs.eclipse.ui.dialog.config.DialogCf;
import org.nightlabs.eclipse.ui.dialog.config.DialogCfMod;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class CenteredTitleDialog 
extends TitleAreaDialog 
{
	private static final Logger logger = Logger.getLogger(CenteredTitleDialog.class);
	
	/**
	 * @param parentShell
	 */
	public CenteredTitleDialog(Shell parentShell) {
		super(parentShell);
	}

	protected DialogCfMod getDialogCfMod() {
		return Config.sharedInstance().createConfigModule(DialogCfMod.class);
	}

	protected String getDialogIdentifier() {
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
	 * Please do not call this method outside of your implementation of {@link #configureShell(Shell)}!
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
