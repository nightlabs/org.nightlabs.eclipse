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

package org.nightlabs.base.ui.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * {@link CellEditor} for editing {@link Double} objects. 
 */
public class DoubleCellEditor
extends XTextCellEditor
{
	private Object oldValue;
	
	public DoubleCellEditor() {
		super();
	}

	public DoubleCellEditor(Composite parent) {
		super(parent);
	}

	public DoubleCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	public DoubleCellEditor(Composite parent, int style, boolean readOnly) {
		super(parent, style, readOnly);
	}

	/**
	 *
	 * returns the string of the text as double or the oldValue if
	 * the string is no double
	 *
	 * @return the text as double
	 */
	@Override
	protected Object doGetValue()
	{
		if (text.getText().trim().equals("")) //$NON-NLS-1$
			return oldValue;
		Double d = null;
		try {
			d = new Double(text.getText());
		} catch (NumberFormatException e) {
			return oldValue;
		}
		return d;
	}

	@Override
	protected void doSetValue(Object value) {
		oldValue = value;
		if (value instanceof Double) {
			super.doSetValue(String.valueOf(value));
		}
		else if (value instanceof String)
			super.doSetValue(value);
	}

}
