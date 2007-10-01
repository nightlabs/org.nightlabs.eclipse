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

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.requests.SelectionRequest;


public class EditorRotateCenterRequest 
extends SelectionRequest 
{
	public EditorRotateCenterRequest() 
	{
		super();
		setType(EditorRequestConstants.REQ_EDIT_ROTATE_CENTER);
	}
	
  protected Point rotationCenter;  
  public Point getRotationCenter() {
    return rotationCenter;
  }
  public void setRotationCenter(Point rotationCenter) {
    this.rotationCenter = rotationCenter;
  }
  
  protected List<EditPart> parts;
  public List<EditPart> getEditParts() {
    return parts;
  }
  public void setEditParts(List<EditPart> list) {
    parts = list;
  }  
  
  protected boolean multiple = false;  
  public boolean isMultiple() {
    return multiple;
  }
  public void setMultiple(boolean multiple) {
    this.multiple = multiple;
  }
}
