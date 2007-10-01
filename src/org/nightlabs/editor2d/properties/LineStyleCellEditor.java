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

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class LineStyleCellEditor 
extends DialogCellEditor 
{
  public LineStyleCellEditor() {
    super();
  }

  public LineStyleCellEditor(Composite parent) {
    super(parent);
  }

  public LineStyleCellEditor(Composite parent, int style) {
    super(parent, style);
  }

  protected Object openDialogBox(Control cellEditorWindow) 
  {
    LineStyleDialog dialog = new LineStyleDialog(cellEditorWindow.getShell());
    dialog.open();
    Object[] result = dialog.getResult(); 
    if (result != null)
      return result[0];  
    
    return new Integer(1);
  }

  protected void updateContents(Object value) 
  {
    super.updateContents(value);
  }
}
