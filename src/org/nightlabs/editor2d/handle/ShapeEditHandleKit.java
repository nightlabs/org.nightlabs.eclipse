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

import java.util.List;

import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.PointList;

import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.util.J2DUtil;


public class ShapeEditHandleKit 
{

  public ShapeEditHandleKit() {
    super();
  }
    
  /**
   * Fills the given List with handles at PathSegment of the GeneralShape of the
   * ShapeDrawComponentEditPart.
   * 
   * @param part the handles' ShapeDrawComponentEditPart
   * @param handles the List to add the handles to
   */
  public static void addHandles(ShapeDrawComponentEditPart part, List handles) 
  {        
    Polyline polyline = J2DUtil.toPolyline(part.getGeneralShape()); 
    PointList points = polyline.getPoints();
    for (int i=0; i<points.size(); i++) 
    {
      ShapeEditHandle handle = new ShapeEditHandle(part, i);
      handles.add(handle);
    }      
    
  }
  
}
