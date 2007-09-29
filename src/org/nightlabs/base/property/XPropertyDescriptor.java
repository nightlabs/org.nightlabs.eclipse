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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class XPropertyDescriptor 
extends PropertyDescriptor 
{

	/**
	 * @param id the ID of the PropertyDescriptor
	 * @param displayName the DisplayName of the PropertyDescriptor
	 */
	public XPropertyDescriptor(Object id, String displayName) {
		this(id, displayName, false);
	}

	/**
	 * 
	 * @param id the ID of the PropertyDescriptor
	 * @param displayName the DisplayName of the PropertyDescriptor
	 * @param readOnly determines if the used CellEditor should be readOnly or not
	 */
	public XPropertyDescriptor(Object id, String displayName, boolean readOnly) {
		super(id, displayName);
		this.readOnly = readOnly;
	}
	
	protected boolean readOnly = false;
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
//	protected void init() {
//		setLabelProvider(new LabelProvider() {
//			@Override
//			public String getText(Object element) {
//				if (isReadOnly()) {
//					
//				}
//				return super.getText(element);
//			}
//		});
//	}
}
