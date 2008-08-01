package org.nightlabs.eclipse.ui.dialog;

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

/**
 * A resizable tray dialog base implementation that stores its size and position.
 * To define the preferred size (the size on first startup) place entries in
 * the {@link ResourceBundle} you pass to the constructor. See the JavaDoc of {@link ResizableDialogSupport}
 * for the format of the entries.
 * 
 * @see ResizableDialogSupport
 * @see TrayDialog
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class ResizableTrayDialog extends TrayDialog
{
	private ResizableDialogSupport resizableDialogSupport;
	
	/**
	 * Create a new ResizableTrayDialog instance.
	 * @param shell The parent shell
	 * @param resourceBundle The resource bundle to use for initial size 
	 * 		and location hints. May be <code>null</code>.
	 */
	public ResizableTrayDialog(Shell shell, ResourceBundle resourceBundle)
	{
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		this.resizableDialogSupport = new ResizableDialogSupport(this, resourceBundle);
	}

	/**
	 * Create a new ResizableTrayDialog instance.
	 * @param parentShell The parent shell provider
	 * @param resourceBundle The resource bundle to use for initial size 
	 * 		and location hints. May be <code>null</code>.
	 */
	public ResizableTrayDialog(IShellProvider parentShell, ResourceBundle resourceBundle)
	{
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		this.resizableDialogSupport = new ResizableDialogSupport(this, resourceBundle);
	}

	/**
	 * Do not override this method to set the initial size, use entries in the 
	 * {@link ResourceBundle} passed to the constructor instead.
	 * <p>
	 * This implementation uses the {@link ResizableDialogSupport} 
	 * to restore the old size.
	 * </p>
	 * @see org.eclipse.jface.dialogs.Dialog#getInitialSize()
	 */
	@Override
	protected Point getInitialSize()
	{
		Point size = resizableDialogSupport.getInitialSize();
		if(size != null)
			return size;
		return super.getInitialSize();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#getInitialLocation(org.eclipse.swt.graphics.Point)
	 */
	@Override
	protected Point getInitialLocation(Point initialSize)
	{
		Point loc = resizableDialogSupport.getInitialLocation();
		if(loc != null)
			return loc;
		return super.getInitialLocation(initialSize);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TrayDialog#close()
	 */
	@Override
	public boolean close()
	{
		resizableDialogSupport.saveBounds();
		return super.close();
	}
}
