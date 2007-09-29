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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class FullScreenDialog 
extends Dialog
{

	public FullScreenDialog(Shell arg0) {
		super(arg0);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MIN | SWT.MAX);		
	}

	public FullScreenDialog(IShellProvider arg0) {
		super(arg0);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MIN | SWT.MAX );		
	}

	public void create() 
	{
		super.create();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		getShell().setSize(screenSize.width, screenSize.height);
		getShell().setLocation(0,0);	
	}
	
}
