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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.RelativeHandleLocator;
import org.eclipse.swt.graphics.Cursor;

import org.nightlabs.editor2d.custom.EditorCursors;
import org.nightlabs.editor2d.tools.ShearTracker;


public class ShearHandle 
extends EditorAbstractHandle 
{
  protected int cursorDirection = 0;
  
  public ShearHandle(GraphicalEditPart owner, int direction) 
  {
    setOwner(owner);
    setLocator(new RelativeHandleLocator(owner.getFigure(), direction));
    cursorDirection = direction;
    setCursor(getDefaultCursor(direction));
  }
  
  protected Cursor getDefaultCursor(int direction) 
  {
    switch(direction)
    {
    	case(PositionConstants.NORTH):
    	case(PositionConstants.SOUTH):
    	  return EditorCursors.SHEAR_HORIZONTAL;
    	case(PositionConstants.WEST):
    	case(PositionConstants.EAST):
    	  return EditorCursors.SHEAR_VERTICAL;    		
    }
    return EditorCursors.NO;
  } 
//  public void paintFigure(Graphics g) 
//  {
//    Rectangle r = getBounds();
//    r.shrink(1, 1);
//    try {    
//      paintHandle(g, cursorDirection);
//    } finally {
//      //We don't really own rect 'r', so fix it.
//      r.expand(1, 1);
//    }    
//  } 
  
  public void paintFigure(Graphics g) 
  {
    // TODO: draw Rotate Handles       
    Rectangle r = getBounds();
    r.shrink(1, 1);
    try {
      g.setBackgroundColor(ColorConstants.black);
      g.fillRectangle(r.x, r.y, r.width, r.height);
      g.setForegroundColor(ColorConstants.white); 
      g.drawRectangle(r.x, r.y, r.width, r.height);
    } finally {
      //We don't really own rect 'r', so fix it.
      r.expand(1, 1);
    }    
  } 
  
  protected void paintHandle(Graphics g, int direction) 
  {
    g.setForegroundColor(ColorConstants.black);
    switch(direction) 
    {
    	case(PositionConstants.EAST):
    	case(PositionConstants.WEST):
        // draw down Arrow
        g.drawLine(2, 2, 6, 6);
        g.drawLine(6, 6, 5, 5);
        g.drawLine(6, 6, 6, 5);
        // draw up Arrow
        g.drawLine(6, 6, 2, 2);
        g.drawLine(2, 2, 3, 3);
        g.drawLine(2, 2, 1, 3);
        break;
      case(PositionConstants.NORTH):
      case(PositionConstants.SOUTH):
        // draw down Arrow
        g.drawLine(2, 2, 6, 6);
        g.drawLine(6, 6, 5, 5);
        g.drawLine(6, 6, 6, 5);
        // draw up Arrow
        g.drawLine(6, 6, 2, 2);
        g.drawLine(2, 2, 3, 3);
        g.drawLine(2, 2, 1, 3);
        break;
    }
  }
  
  protected DragTracker createDragTracker() {
    return new ShearTracker(getOwner(), cursorDirection);
  }

}
