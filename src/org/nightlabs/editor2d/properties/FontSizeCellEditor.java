/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.properties;


import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.celleditor.ComboBoxCellEditor;
import org.nightlabs.util.FontUtil;

public class FontSizeCellEditor 
extends ComboBoxCellEditor 
{
  public FontSizeCellEditor(Composite parent) {
    super(parent, FontUtil.getFontSizes());
  }
 
  protected Object doGetValue() {      	
    return new Integer(getItems()[getComboBox().getSelectionIndex()]); 
  }    
  
  protected void doSetValue(Object value) 
  {
    String string = ""; //$NON-NLS-1$
    
    if (value instanceof String) {
      string = (String) value;
    }
    else if (value instanceof Integer) {
      string = ((Integer)value).toString();
    }
    
    for (int i=0; i<getItems().length; i++) {
      String s = getItems()[i];
      if (s.equals(string)) {
      	getComboBox().select(i);
        break;
      }          
    }
  }
  
}
