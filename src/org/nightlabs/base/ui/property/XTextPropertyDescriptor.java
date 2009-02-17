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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.ui.celleditor.XTextCellEditor;

/**
 * A {@link PropertyDescriptor} implementation that displays a simple text and renders
 * an editable (or read-only) {@link Text} for editing it.
 *
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class XTextPropertyDescriptor
//extends TextPropertyDescriptor
extends XPropertyDescriptor
{

	public XTextPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public XTextPropertyDescriptor(Object id, String displayName, boolean readOnly) {
		super(id, displayName, readOnly);
	}

	/**
	 * The <code>TextPropertyDescriptor</code> implementation of this
	 * <code>IPropertyDescriptor</code> method creates and returns a new
	 * <code>XTextCellEditor</code>.
	 * <p>
	 * The editor is configured with the current validator if there is one.
	 * </p>
	 */
	@Override
	public CellEditor createPropertyEditor(Composite parent)
	{
		CellEditor editor = new XTextCellEditor(parent, SWT.NONE, isReadOnly());
		if (getValidator() != null)
			editor.setValidator(getValidator());
		return editor;
	}
}
