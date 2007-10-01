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

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.handles.SquareHandle;
import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.tools.ShapeEditTracker;


public class ShapeEditHandle  
extends SquareHandle
{
  protected ShapeDrawComponentEditPart owner;
  protected int pathSegmentIndex;
  
  public ShapeEditHandle(ShapeDrawComponentEditPart owner, int pathSegmentIndex) 
  {
    super(owner, new ShapeHandleLocator(owner, pathSegmentIndex));
    this.owner = owner;
    this.pathSegmentIndex = pathSegmentIndex;
    initialize();
//    setLocation(p);
  }
    
  /* (non-Javadoc)
   * @see org.eclipse.gef.handles.AbstractHandle#createDragTracker()
   */
  protected DragTracker createDragTracker() {
    return new ShapeEditTracker(owner, pathSegmentIndex);    
  }
  
  public static final int DEFAULT_SIZE = 6;
      
  /**
   * Initializes the handle.  Sets the {@link DragTracker} and
   * DragCursor.
   */
  protected void initialize() {
  	setOpaque(false);
  	setBorder(new LineBorder(1));
  	setCursor(Cursors.CROSS);
  	setPreferredSize(new Dimension(DEFAULT_SIZE, DEFAULT_SIZE));
  	setSize(DEFAULT_SIZE, DEFAULT_SIZE);
  	
//  	setLocation(getPathSegmentLocation());
  }  
     
  public void setBounds(Rectangle rect) 
  {
    super.setBounds(new Rectangle(rect.x, rect.y, DEFAULT_SIZE, DEFAULT_SIZE));
  }
  
  private boolean fixed = false;
  
  /**
   * Returns true if the handle cannot be dragged.
   * @return <code>true</code> if the handle cannot be dragged
   */
  protected boolean isFixed() {
  	return fixed;
  }
  
  /**
   * Sets whether the handle is fixed and cannot be moved
   * @param fixed <code>true</code> if the handle should be unmovable
   */
  public void setFixed(boolean fixed) 
  {
  	this.fixed = fixed;
  	if (fixed)
  		setCursor(Cursors.NO);
  	else
  		setCursor(Cursors.CROSS);
  }     
    
//  protected Point getPathSegmentLocation() 
//  {
//    ShapeFigure sf = (ShapeFigure) owner.getFigure();
//    PathSegment ps = sf.getGeneralShape().getPathSegment(pathSegmentIndex);
//    if (ps != null) {
//      return J2DUtil.toDraw2D(ps.getPoint());
//    }
//    return new Point();    
//  }
 
}
