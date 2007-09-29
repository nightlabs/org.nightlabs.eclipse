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

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.celleditor.GenericComboBoxCellEditor;

/**
 * @author Daniel.Mazurek <at> Nightlabs <dot> de
 *
 */
public class GenericComboBoxPropertyDescriptor<T> 
extends XPropertyDescriptor 
{
	private ILabelProvider labelProvider = null;
	private List<T> types = null;

	/**
	 * @param id
	 * @param displayName
	 */
	public GenericComboBoxPropertyDescriptor(Object id, String displayName, 
			List<T> types, ILabelProvider labelProvider) 
	{
		super(id, displayName);
		if (types == null)
			throw new IllegalArgumentException("param types must not be null!"); //$NON-NLS-1$
		if (labelProvider == null)
			throw new IllegalArgumentException("param labelProvider must not be null!"); //$NON-NLS-1$
		
		this.labelProvider = labelProvider;
		this.types = types;
		init();
	}

	/**
	 * @param id
	 * @param displayName
	 * @param readOnly
	 */
	public GenericComboBoxPropertyDescriptor(Object id, String displayName,
			boolean readOnly, List<T> types, ILabelProvider labelProvider) 
	{
		super(id, displayName, readOnly);
		if (types == null)
			throw new IllegalArgumentException("param types must not be null!"); //$NON-NLS-1$
		if (labelProvider == null)
			throw new IllegalArgumentException("param labelProvider must not be null!"); //$NON-NLS-1$
		
		this.labelProvider = labelProvider;		
		this.types = types;		
		init();
	}
	
	protected void init() {
		setLabelProvider(labelProvider);
	}
	
	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		return new GenericComboBoxCellEditor<T>(parent, types, labelProvider);
	}
	
}
