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

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.dialog.resource.Messages;

/**
 * @deprecated Is this in use? Is it useful?
 */
@Deprecated
public abstract class AbstractInputDialog
extends InputDialog
{
	public AbstractInputDialog(Shell shell, String title, String message, String initalValue, IInputValidator validator) {
		super(shell, title, message, initalValue, validator);
	}

	public AbstractInputDialog(Shell shell, String title, String message, String initValue) {
		this(shell, title, message, initValue, inputValidator);
	}

	public AbstractInputDialog(Shell shell, String title, String message) {
		this(shell, title, message, "", inputValidator); //$NON-NLS-1$
	}

	protected static final IInputValidator inputValidator = new IInputValidator()
	{
		public String isValid(String input)
		{
			if(input.trim().equals("")) //$NON-NLS-1$
				return Messages.getString("org.nightlabs.eclipse.ui.dialog.AbstractInputDialog.inputValidator.emptyString"); //$NON-NLS-1$

			return null;
		}
	};

	public IInputValidator getInputValidator() {
		return inputValidator;
	}

	@Override
	protected abstract void okPressed();
}
