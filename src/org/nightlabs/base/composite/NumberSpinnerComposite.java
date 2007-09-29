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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.util.Util;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * TODO @Daniel please decide whether {@link DoubleSpinnerComposite} or this class should be used - IMHO one of them can be thrown away.
 */
public class NumberSpinnerComposite 
extends XComposite 
{
	public NumberSpinnerComposite(Composite parent, int style, int spinnerStyle, 
			int numDigits, Number minValue, Number maxValue, Number increment, LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		this.spinnerStyle = spinnerStyle;
		this.numDigits = numDigits;
		this.minVal = minValue;
		this.maxVal = maxValue;
		this.increment = increment;
		createComposite(this);
	}

	public NumberSpinnerComposite(Composite parent, int style, int spinnerStyle, 
			int numDigits, Number minValue, Number maxValue, Number increment) 
	{
		super(parent, style);
		this.spinnerStyle = spinnerStyle;
		this.numDigits = numDigits;
		this.minVal = minValue;
		this.maxVal = maxValue;
		this.increment = increment;
		createComposite(this);
	}
	
	protected Number changeValue(Number value) {
//		return Math.pow(10, numDigits) * value.doubleValue(); 
		if (numDigits > 0)
			return Math.pow(10, numDigits) * value.doubleValue();
		else
			return value.longValue();
	}
	
	private Number minVal = 0;
	public Number getMinimum() {
		return minVal;
	}
	public void setMinimum(Number minimum) {
		this.minVal = minimum;
		spinner.setMinimum(changeValue(minVal).intValue());
	}
	
	private Number maxVal = Integer.MAX_VALUE;	
	public Number getMaximum() {
		return maxVal;
	}
	public void setMaximum(Number maximum) {
		this.maxVal = maximum;
		spinner.setMaximum(changeValue(maxVal).intValue());		
	}
	
	private int numDigits = 2;
	public int getNumDigits() {
		return numDigits;
	}
	public void setNumDigits(int numDigits) {
		this.numDigits = numDigits;
		spinner.setDigits(numDigits);
	}
		
	private Number increment = 1.0;
	public Number getIncrement() {
		return increment;
	}
	public void setIncrement(Number increment) {
		this.increment = increment;
		spinner.setIncrement(increment.intValue());
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
	
	private Number rest;
	public void setValue(Number value) 
	{
		Number val = changeValue(value);
		Number shortedVal = (int) Util.truncateDouble(val.doubleValue(), numDigits);		
		rest = val.doubleValue() - shortedVal.doubleValue();
		spinner.setSelection(shortedVal.intValue());		
	}
		
	public Number getValue() 
	{
//		return getShortedValue().doubleValue() + rest.doubleValue();
		return Util.getDouble(spinner.getSelection(), numDigits);
	}
	
	public Number getShortedValue() 
	{
		Number shortedValue = Util.getDouble(spinner.getSelection(), numDigits);
		return shortedValue;				
	}	
}
