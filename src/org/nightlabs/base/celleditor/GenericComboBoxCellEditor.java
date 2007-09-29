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

import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.composite.XComboComposite;

/**
 * @author Daniel.Mazurek <at> Nightlabs <dot> de
 *
 */
public class GenericComboBoxCellEditor<T>
extends XCellEditor 
{
	private List<T> types = null;
	private ILabelProvider labelProvider = null;	
	private XComboComposite<T> comboComposite = null;

	public GenericComboBoxCellEditor(Composite parent, List<T> types, ILabelProvider labelProvider) 
	{
		if (types == null)
			throw new IllegalArgumentException("param types must not be null!"); //$NON-NLS-1$
		
		if (labelProvider == null)
			throw new IllegalArgumentException("param labelProvider must not be null!"); //$NON-NLS-1$
		
		this.types = types;
		this.labelProvider = labelProvider;
		create(parent);
	}
	
	public GenericComboBoxCellEditor(Composite parent, int style, boolean readOnly, 
			List<T> types, ILabelProvider labelProvider, int comboStyle) 
	{
		if (types == null)
			throw new IllegalArgumentException("param types must not be null!"); //$NON-NLS-1$
		
		if (labelProvider == null)
			throw new IllegalArgumentException("param labelProvider must not be null!"); //$NON-NLS-1$
		
		this.types = types;
		this.labelProvider = labelProvider;
		setReadOnly(readOnly);
		setStyle(style);
		this.comboStyle = comboStyle;
		create(parent);
	}
	
	private int comboStyle;
	
	@Override
	protected Control createControl(Composite parent) {
		comboComposite = new XComboComposite<T>(parent, comboStyle,(String)null, labelProvider);
		comboComposite.setInput(types);
		return comboComposite;
	}

	@Override
	protected Object doGetValue() {
		return comboComposite.getSelectedElement();
	}

	@Override
	protected void doSetFocus() {
		comboComposite.setFocus();
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	@Override
	protected void doSetValue(Object value) {
		comboComposite.selectElement((T) value);
	}
	
}
