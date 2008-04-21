package org.nightlabs.eclipse.ui.dialog;

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

/**
 * A resizable tray dialog base implementation that stores its size and position.
 * @see ResizableDialogSupport
 * @see TrayDialog
 * @author Marc Klinger - marc[at]nightlabs[dot]de
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

	/* (non-Javadoc)
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
