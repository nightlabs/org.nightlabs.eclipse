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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.celleditor.ComboBoxCellEditor;
import org.nightlabs.base.labelprovider.ComboBoxLabelProvider;

public class ComboBoxPropertyDescriptor 
extends PropertyDescriptor
{
  /**
   * The list of possible values to display in the combo box
   */
  protected String[] values;
  
  public ComboBoxPropertyDescriptor(Object id, String displayName, String[] valuesArray) 
  {
    super(id, displayName);
    values = valuesArray;
    setLabelProvider(new ComboBoxLabelProvider(values));
  }
  
  /**
   * The <code>ComboBoxPropertyDescriptor</code> implementation of this 
   * <code>IPropertyDescriptor</code> method creates and returns a new
   * <code>ComboBoxCellEditor</code>.
   * <p>
   * The editor is configured with the current validator if there is one.
   * </p>
   */
  public CellEditor createPropertyEditor(Composite parent) 
  {
    CellEditor editor = new ComboBoxCellEditor(parent, values, SWT.READ_ONLY);
    if (getValidator() != null)
      editor.setValidator(getValidator());
    return editor;
  }  

}
