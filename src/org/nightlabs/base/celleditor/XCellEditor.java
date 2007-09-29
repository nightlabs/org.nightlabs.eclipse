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

package org.nightlabs.base.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public abstract class XCellEditor 
extends CellEditor 
implements IReadOnlyCellEditor
{

	public XCellEditor() {
		super();
	}

	/**
	 * @param parent
	 */
	public XCellEditor(Composite parent) {
		super(parent);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public XCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public XCellEditor(Composite parent, int style, boolean readOnly) {
		super(parent, style);
		setReadOnly(readOnly);
	}

	private boolean readOnly = false;
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		getControl().setEnabled(!readOnly);
	}
	
}
