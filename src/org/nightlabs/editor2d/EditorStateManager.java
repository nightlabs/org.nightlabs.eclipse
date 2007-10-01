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

package org.nightlabs.editor2d;

import java.util.List;

import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.util.EditorUtil;


public class EditorStateManager 
{
  public static final int STATE_NORMAL_SELECTION = 1;
  public static final int STATE_EDIT_SHAPE = 2;
  public static final int STATE_ROTATE = 3;
  
  public EditorStateManager() {
    super();
  }  
  
  protected static int currentState = STATE_NORMAL_SELECTION;  
  public static int getCurrentState() {
    return currentState;
  }
  public static void setCurrentState(int currentState) {
    EditorStateManager.currentState = currentState;
  }
   
  public static void setEditShapeMode(ShapeDrawComponentEditPart sdc) 
  {
    setCurrentState(STATE_EDIT_SHAPE);
//    sdc.getViewer().deselectAll();
//    sdc.getViewer().select(sdc);
    EditorUtil.selectEditPart(sdc);
  }

  public static void setNormalSelectionMode(List editParts) 
  {
    setCurrentState(STATE_NORMAL_SELECTION);
    EditorUtil.selectEditParts(editParts);    
  }
  
  public static void setRotateMode(List editParts) 
  {
    setCurrentState(STATE_ROTATE);
    EditorUtil.selectEditParts(editParts);
  }
    
}
