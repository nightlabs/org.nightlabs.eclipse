/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.eclipse.ui.dialog;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * This Dialog will open full-screen on the primary screen 
 * at the first time it will be used. From then on it will store
 * its bounds using a prefix: {@link #getDialogIdentfier()}.
 * 
 * @author unascribed
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class FullScreenDialog
extends ResizableTrayDialog
{

	public FullScreenDialog(Shell shell) {
		super(shell, null);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MIN | SWT.MAX);
	}

	public FullScreenDialog(IShellProvider shellProvider) {
		super(shellProvider, null);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MIN | SWT.MAX );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#getInitialSize()
	 */
	@Override
	protected Point getInitialSize()
	{
		Point size = getResizableDialogSupport().getInitialSize();
		if (size != null)
			return size;
		Monitor monitor;
		Composite parent = getShell().getParent();
		if (parent != null)
			monitor = parent.getMonitor();
		else
			monitor = getShell().getDisplay().getPrimaryMonitor();
		return new Point(monitor.getClientArea().width, monitor.getClientArea().height);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#getInitialLocation(org.eclipse.swt.graphics.Point)
	 */
	@Override
	protected Point getInitialLocation(Point initialSize)
	{
		Point location = getResizableDialogSupport().getInitialLocation();
		if (location != null)
			return location;
		Monitor monitor;
		Composite parent = getShell().getParent();
		if (parent != null)
			monitor = parent.getMonitor();
		else
			monitor = getShell().getDisplay().getPrimaryMonitor();
		return new Point(monitor.getClientArea().x, monitor.getClientArea().y);
	}
}
