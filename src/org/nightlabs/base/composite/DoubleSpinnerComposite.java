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
package org.nightlabs.base.composite;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.util.Util;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * TODO @Daniel please decide whether {@link NumberSpinnerComposite} or this class should be used - IMHO one of them can be thrown away.
 */
public class DoubleSpinnerComposite 
extends XComposite 
{

	public DoubleSpinnerComposite(Composite parent, int style, int spinnerStyle, 
			int numDigits, double minValue, double maxValue, double increment, LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.spinnerStyle = spinnerStyle;
		this.numDigits = numDigits;
		this.minVal = minValue;
		this.maxVal = maxValue;
		this.increment = increment;
		createComposite(this);
	}

	public DoubleSpinnerComposite(Composite parent, int style, int spinnerStyle, 
			int numDigits, double minValue, double maxValue, double increment) 
	{
		super(parent, style);
		this.spinnerStyle = spinnerStyle;
		this.numDigits = numDigits;
		this.minVal = minValue;
		this.maxVal = maxValue;
		this.increment = increment;
		createComposite(this);
	}
		
	protected double changeValue(double value) {
		return Math.pow(10, numDigits) * value; 
	}
	
	private double minVal = 0;
	public double getMinimum() {
		return minVal;
	}
	public void setMinimum(double minimum) {
		this.minVal = minimum;
		spinner.setMinimum((int)changeValue(minVal));
	}
	
	private double maxVal = Integer.MAX_VALUE;	
	public double getMaximum() {
		return maxVal;
	}
	public void setMaximum(double maximum) {
		this.maxVal = maximum;
		spinner.setMaximum((int)changeValue(maxVal));		
	}
	
	private int numDigits = 2;
	public int getNumDigits() {
		return numDigits;
	}
	public void setNumDigits(int numDigits) {
		this.numDigits = numDigits;
		spinner.setDigits(numDigits);
	}
		
	private double increment = 1.0;
	public double getIncrement() {
		return increment;
	}
	public void setIncrement(double increment) {
		this.increment = increment;
		spinner.setIncrement((int)increment);
	}
	
	private int spinnerStyle;
	private Spinner spinner = null;
	
	protected void createComposite(Composite parent) 
	{
		spinner = new Spinner(parent, spinnerStyle);
		setNumDigits(numDigits);
		setMinimum(minVal);
		setMaximum(maxVal);
		setIncrement(increment);
		spinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private double rest;
	public void setValue(double value) 
	{
		double val = changeValue(value);
		double shortedDouble = (int) Util.truncateDouble(val, numDigits);		
		rest = (val - shortedDouble) / Math.pow(10, numDigits);		
		spinner.setSelection((int)shortedDouble);		
	}

	public double getValue() {
		return getShortedValue() + rest;
	}

	public double getShortedValue() {
		return Util.getDouble(spinner.getSelection(), numDigits);				
	}
	
	public void addSelectionListener(SelectionListener listener) {
		spinner.addSelectionListener(listener);
	}
	public void removeSelectionListener(SelectionListener listener) {
		spinner.removeSelectionListener(listener);
	}
	
	public void addModifyListener(ModifyListener listener) {
		spinner.addModifyListener(listener);
	}
	
	public void removeModifyListener(ModifyListener listener) {
		spinner.removeModifyListener(listener);
	}

}
