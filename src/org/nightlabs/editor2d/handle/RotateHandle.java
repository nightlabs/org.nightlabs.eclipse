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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.gef.handles.RelativeHandleLocator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import org.nightlabs.editor2d.custom.EditorCursors;
import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import org.nightlabs.editor2d.tools.RotateTracker;

public class RotateHandle 
extends AbstractHandle 
{
  protected int cursorDirection = 0;
  protected Image image;
  
//  public RotateHandle(GraphicalEditPart owner, int direction) 
  public RotateHandle(AbstractDrawComponentEditPart owner, int direction)
  {
    setOwner(owner);
    setLocator(new RelativeHandleLocator(owner.getFigure(), direction));
    setCursor(EditorCursors.ROTATE);
    cursorDirection = direction;
  }

  protected DragTracker createDragTracker() {
    return new RotateTracker((AbstractDrawComponentEditPart)getOwner(), cursorDirection);
  }

  /**
   * Returns <code>true</code> if the handle's owner is the primary selection.
   * @return <code>true</code> if the handles owner has primary selection.
   */
  protected boolean isPrimary() {
    return getOwner().getSelected() == EditPart.SELECTED_PRIMARY;
  }
  
  /**
   * Returns the color for the inside of the handle.
   * @return the color of the handle
   */
  protected Color getFillColor() {
    return (isPrimary())
      ? ColorConstants.black
      : ColorConstants.white;
  }
  
  /**
   * Returns the color for the outside of the handle.
   * @return the color for the border
   */
  protected Color getBorderColor() {
    return (isPrimary())
      ? ColorConstants.white
      : ColorConstants.black;
  }
  
  /**
   * Draws the handle with fill color and outline color dependent 
   * on the primary selection status of the owner editpart.
   *
   * @param g The graphics used to paint the figure.
   */
  public void paintFigure(Graphics g) 
  {
    // TODO: draw Rotate Handles       
    Rectangle r = getBounds();
    r.shrink(1, 1);
    try {
      g.setBackgroundColor(getFillColor());
      g.fillRectangle(r.x, r.y, r.width, r.height);
      g.setForegroundColor(getBorderColor()); 
      g.drawRectangle(r.x, r.y, r.width, r.height);
    } finally {
      //We don't really own rect 'r', so fix it.
      r.expand(1, 1);
    }    
  }  
  
  /**
   * The default size for square handles.
   */
  protected static final int DEFAULT_HANDLE_SIZE = 7;

  {
    init();
  }  
  
  /**
   * Initializes the handle.
   */
  protected void init() {
    setPreferredSize(new Dimension(DEFAULT_HANDLE_SIZE, DEFAULT_HANDLE_SIZE));
  }  
}

