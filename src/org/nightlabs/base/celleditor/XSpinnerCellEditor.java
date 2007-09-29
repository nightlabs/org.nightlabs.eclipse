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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author Daniel.Mazurek <at> Nightlabs <dot> de
 *
 */
public class XSpinnerCellEditor 
extends XCellEditor 
{
//	public XSpinnerCellEditor() {
//		super();
//	}

	public XSpinnerCellEditor(Composite parent) {
		this(parent, SWT.NONE, false);
	}

	public XSpinnerCellEditor(Composite parent, int style) {
		this(parent, style, false);
	}

	public XSpinnerCellEditor(Composite parent, int style, boolean readOnly) {
		this(parent, style, readOnly, 0, Integer.MAX_VALUE);
	}

	public XSpinnerCellEditor(Composite parent, int style, boolean readOnly,
			int minimum, int maximum) 
	{
		this(parent, style, readOnly, minimum, maximum, SWT.NONE);
	}

	public XSpinnerCellEditor(Composite parent, int style, boolean readOnly,
			int minimum, int maximum, int spinnerStyle) 
	{
//		super(parent, style, readOnly);
		this.minimum = minimum;
		this.maximum = maximum;
		this.spinnerStyle = spinnerStyle;
		create(parent);
		setReadOnly(readOnly);		
	}
	
	private int spinnerStyle = SWT.None;
	private int minimum = 0;
	private int maximum = Integer.MAX_VALUE;	
	
	@Override
	protected Control createControl(Composite parent) 
	{
		Spinner spinner = new Spinner(parent, spinnerStyle);
		spinner.setMinimum(minimum);
		spinner.setMaximum(maximum);		
		return spinner;
	}
	
	@Override
	protected Object doGetValue() {		
		return getSpinner().getSelection();		
	}
	
	@Override
	protected void doSetValue(Object value) {
		if (isReadOnly())
			return;
		
		if (value instanceof Integer)
			getSpinner().setSelection((Integer)value);
		if (value instanceof Float)
			getSpinner().setSelection(((Float)value).intValue());		
		if (value instanceof Double)
			getSpinner().setSelection(((Double)value).intValue());				
	}	
	
	@Override
	protected void doSetFocus() {
		getControl().setFocus();		
	}
	
	public Spinner getSpinner() {
		return (Spinner) getControl();
	}
	
}
