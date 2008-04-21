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

package org.nightlabs.base.ui.dialog;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 * @deprecated Inherit {@link ResizableTrayDialog} or use {@link org.nightlabs.eclipse.ui.dialog.ResizableDialogSupport}.
 */
@Deprecated
public class CenteredDialog
extends ResizableTrayDialog
{
	@Deprecated
	public CenteredDialog(Shell parentShell) {
		super(parentShell, null);
	}

	@Deprecated
	public CenteredDialog(IShellProvider parentShell) {
		super(parentShell, null);
	}

	/**
	 * @deprecated Made final to prevent further use. Use the {@link #getDialogBoundsSettings()}
	 * 		mechanism to store bounds or inherit {@link ResizableTrayDialog} or use 
	 * 		{@link org.nightlabs.eclipse.ui.dialog.ResizableDialogSupport}.
	 */
	@Deprecated
	final protected String getDialogIdentifier()
	{
		return this.getClass().getName();
	}
}
