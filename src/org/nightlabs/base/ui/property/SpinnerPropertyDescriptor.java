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
package org.nightlabs.base.ui.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.ui.celleditor.XSpinnerCellEditor;

/**
 * A {@link PropertyDescriptor} rendering a spinner (i.e. a composite where numbers can be entered, incremented and decremented).
 *
 * @author Daniel.Mazurek <at> Nightlabs <dot> de
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class SpinnerPropertyDescriptor
extends XPropertyDescriptor
{
	private int minimum = 0;
	private int maximum = Integer.MAX_VALUE;

	public SpinnerPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public SpinnerPropertyDescriptor(Object id, String displayName, boolean readOnly) {
		super(id, displayName, readOnly);
	}

	public SpinnerPropertyDescriptor(Object id, String displayName, boolean readOnly,
			int minimum, int maximum)
	{
		super(id, displayName, readOnly);
		this.minimum = minimum;
		this.maximum = maximum;
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		return new XSpinnerCellEditor(parent, SWT.NONE, isReadOnly(), minimum, maximum);
	}

}
