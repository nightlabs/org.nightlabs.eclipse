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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;

import org.nightlabs.editor2d.custom.EditorCursors;
import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import org.nightlabs.editor2d.tools.RotateCenterTracker;


public class RotateCenterHandle 
extends EditorAbstractHandle
{
  protected List editParts;
  public RotateCenterHandle(List editParts) 
  {
    super();
    if (editParts.size() == 1) {
      setLocator(new RotateCenterLocator((AbstractDrawComponentEditPart)editParts.get(0)));
    } else {
      setLocator(new MultipleCenterLocator(editParts));
      multiple = true;
    }
    setOwner((AbstractDrawComponentEditPart)editParts.get(0));
    setCursor(EditorCursors.CROSS);
  }

  protected DragTracker createDragTracker() {
    return new RotateCenterTracker((AbstractDrawComponentEditPart)getOwner());
  }
  
  public void paintFigure(Graphics g) 
  {
    Rectangle r = getBounds();
    r.shrink(1, 1);
    try {    
    	g.setBackgroundColor(getFillColor());
      g.fillOval(r);      
      g.setForegroundColor(getLineColor());
      g.drawOval(r);
    } finally {
      //We don't really own rect 'r', so fix it.
      r.expand(1, 1);
    }    
  }   
  
  protected boolean multiple = false;  
  public boolean isMultiple() {
    return multiple;
  }
}
