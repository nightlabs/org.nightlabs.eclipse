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
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;


public abstract class EditorAbstractHandle 
extends AbstractHandle 
{
  /**
   * The default size for the RotateCenterHandle.
   */
  protected static final int DEFAULT_HANDLE_SIZE = 7;

  {
    init();
  }  
  
  protected void init() {
    setPreferredSize(new Dimension(DEFAULT_HANDLE_SIZE, DEFAULT_HANDLE_SIZE));
    setSize(getPreferredSize());
  }   
  
  public void setBounds(Rectangle rect) {
    super.setBounds(new Rectangle(rect.x, rect.y, DEFAULT_HANDLE_SIZE, DEFAULT_HANDLE_SIZE));
  }  
  
  public EditorAbstractHandle() 
  {
    super();
    init();    
  }
  
  /**
   * @param owner
   * @param loc
   */
  public EditorAbstractHandle(GraphicalEditPart owner, Locator loc) {
    super(owner, loc);
    init();
  }

  /**
   * @param owner
   * @param loc
   * @param c
   */
  public EditorAbstractHandle(GraphicalEditPart owner, Locator loc, Cursor c) {
    super(owner, loc, c);
    init();
  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.handles.AbstractHandle#createDragTracker()
   */
  protected abstract DragTracker createDragTracker();

  private Color fillColor = ColorConstants.black;
  public Color getFillColor() {
  	return fillColor;
  }
  public void setFillColor(Color c) {
  	this.fillColor = c;
  }
  
  private Color lineColor = ColorConstants.black;
  public Color getLineColor() {
  	return lineColor;
  }
  public void setLineColor(Color c) {
  	this.lineColor = c;
  }
  
}
