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

import org.eclipse.swt.widgets.Composite;

public class DoubleCellEditor  
extends XTextCellEditor
{  
  public DoubleCellEditor() {
    super();
  }

  public DoubleCellEditor(Composite parent) {
    super(parent);
  }

  public DoubleCellEditor(Composite parent, int style) {
    super(parent, style);
  }

  public DoubleCellEditor(Composite parent, int style, boolean readOnly) {
    super(parent, style, readOnly);
  }  
  
	/**
	 *
	* returns the string of the text as double or the oldValue if 
	* the string is no double
	*
	* @return the text as double
	*/
	protected Object doGetValue() 
	{
		if (text.getText().trim().equals("")) //$NON-NLS-1$
			return oldValue;
		Double d = null;
		try {
			d = new Double(text.getText());
		} catch (NumberFormatException e) {
			return oldValue;
		}
		return d;
	}

  private Object oldValue;
 
	@Override
	protected void doSetValue(Object value) {
		oldValue = value;
		if (value instanceof Double) {
			super.doSetValue(String.valueOf((Double)value));
		}
		else if (value instanceof String)
			super.doSetValue(value);
	}
	
// protected Text text;
//	
//  protected Control createControl(Composite parent) {
//    text = new Text(parent, getStyle());
//    return text;
//  }
//
//  protected Object doGetValue() {
//    String stringVal = text.getText();
//    Double d = new Double(stringVal);
//    return d; 
//  }
//
//  protected void doSetFocus() {
//    if (text != null) {
//      text.selectAll();
//      text.setFocus();
//    }     
//  }
//
//  protected void doSetValue(Object value) 
//  {
//  	checkReadOnly();
//    Assert.isTrue(text != null && (value instanceof Double));
//    Double val = (Double) value;
//    String stringVal = Double.toString(val.doubleValue());
//    text.setText(stringVal); 
//    fireApplyEditorValue();
//  }

}
