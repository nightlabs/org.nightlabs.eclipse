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

package org.nightlabs.editor2d.handle;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;

import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;

public class RotateHandleKit 
{

  public RotateHandleKit() {
    super();
  }

  public static void addHandles(List selectedEditParts, List handles) 
  {
    for (Iterator it = selectedEditParts.iterator(); it.hasNext(); ) {      
      GraphicalEditPart editPart = (GraphicalEditPart) it.next();
      if (editPart instanceof AbstractDrawComponentEditPart)
        createRotateHandles((AbstractDrawComponentEditPart)editPart, handles);      
    }
    
    handles.add(createRotateCenterHandle(selectedEditParts));
  }
  
  protected static Handle createRotateCenterHandle(List editParts)
  {
    RotateCenterHandle handle = new RotateCenterHandle(editParts);
    return handle;
  }
  
  protected static void createRotateHandles(AbstractDrawComponentEditPart owner, List handles)
  {
    handles.add(createRotateHandle(owner, PositionConstants.NORTH_WEST));
    handles.add(createRotateHandle(owner, PositionConstants.NORTH_EAST));
    handles.add(createRotateHandle(owner, PositionConstants.SOUTH_EAST));
    handles.add(createRotateHandle(owner, PositionConstants.SOUTH_WEST));
    
//    handles.add(createRotateCenterHandle(owner));
    
//    handles.add(createShearHandle(owner, PositionConstants.NORTH));
//    handles.add(createShearHandle(owner, PositionConstants.WEST));
//    handles.add(createShearHandle(owner, PositionConstants.SOUTH));
//    handles.add(createShearHandle(owner, PositionConstants.EAST));
  }
  
  protected static Handle createRotateHandle(AbstractDrawComponentEditPart owner, int direction) 
  {
    RotateHandle handle = new RotateHandle(owner, direction);
    return handle;
  }  
  
  protected static Handle createShearHandle(AbstractDrawComponentEditPart owner, int direction) 
  {
    ShearHandle handle = new ShearHandle(owner, direction);
    return handle;
  }   
}
