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

package org.nightlabs.editor2d.request;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.requests.SelectionRequest;


public class EditorEditShapeRequest  
extends SelectionRequest
implements EditorRequestConstants,
					 EditorLocationRequest
{
  public EditorEditShapeRequest() 
  {
    super();
    setType(REQ_EDIT_SHAPE);
  }
  
  protected int pathSegmentIndex;  
  public int getPathSegmentIndex() {
    return pathSegmentIndex;
  }
  public void setPathSegmentIndex(int pathSegmentIndex) {
    this.pathSegmentIndex = pathSegmentIndex;
  }
    
  protected EditPart targetEditPart;  
  public EditPart getTargetEditPart() {
    return targetEditPart;
  }
  public void setTargetEditPart(EditPart targetEditPart) {
    this.targetEditPart = targetEditPart;
  }
  
//  protected Point mouseLocation;
//  /**
//   * Returns the location of the mouse pointer.
//   *
//   * @return The location of the mouse pointer.
//   */
//  public Point getLocation() {
//  	return mouseLocation;
//  }
//  
//  /**
//   * Sets the location where the New PathSegment will be placed.
//   *
//   * @param location the location
//   */
//  public void setLocation(Point location) {
//  	this.mouseLocation = location;
//  }
      
}
