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
package org.nightlabs.base.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.celleditor.XSpinnerCellEditor;

/**
 * @author Daniel.Mazurek <at> Nightlabs <dot> de
 *
 */
public class SpinnerPropertyDescriptor 
extends XPropertyDescriptor 
{

	/**
	 * @param id
	 * @param displayName
	 */
	public SpinnerPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	/**
	 * @param id
	 * @param displayName
	 * @param readOnly
	 */
	public SpinnerPropertyDescriptor(Object id, String displayName, boolean readOnly) {
		super(id, displayName, readOnly);
	}

	/**
	 * @param id
	 * @param displayName
	 * @param readOnly
	 */
	public SpinnerPropertyDescriptor(Object id, String displayName, boolean readOnly, 
			int minimum, int maximum) 
	{
		super(id, displayName, readOnly);
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	private int minimum = 0;
	private int maximum = Integer.MAX_VALUE;
	
	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		return new XSpinnerCellEditor(parent, SWT.NONE, readOnly, minimum, maximum);
	}
	
}
