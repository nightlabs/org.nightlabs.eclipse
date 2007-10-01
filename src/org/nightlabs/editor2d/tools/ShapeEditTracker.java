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

package org.nightlabs.editor2d.tools;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.request.EditorEditShapeRequest;

// TODO; Use SelectEditPartTracker instead to avoid multiple Selections
public class ShapeEditTracker 
extends AbstractDragTracker 
{
  protected ShapeDrawComponentEditPart getShapeDrawComponentEditPart() {
    return (ShapeDrawComponentEditPart) owner;
  }
  protected GeneralShape getGeneralShape() {
    return getShapeDrawComponentEditPart().getGeneralShape();
  }
//  protected ShapeFigure sourceFigure;  
  protected int pathSegmentIndex;
  
  public ShapeEditTracker(ShapeDrawComponentEditPart owner, int pathSegmentIndex) 
  {    
    super(owner);
    this.pathSegmentIndex = pathSegmentIndex;
  }
  
  protected String getCommandName() {
    return REQ_EDIT_SHAPE;
  }
  
  /**
   * @see org.eclipse.gef.tools.AbstractTool#deactivate()
   */
  public void deactivate() 
  {  	
  	super.deactivate();
//  	sourceFigure = null;  	
  }  
        
  /**
   * @see org.eclipse.gef.tools.AbstractTool#getDebugName()
   */
  protected String getDebugName() 
  {
  	return "Edit Shape Handle Tracker";//$NON-NLS-1$
  }  
        
  /**
   * @see org.eclipse.gef.tools.SimpleDragTracker#createSourceRequest()
   */
  protected Request createSourceRequest() 
  {    
  	EditorEditShapeRequest request = new EditorEditShapeRequest();
  	request.setType(REQ_EDIT_SHAPE);
  	request.setPathSegmentIndex(pathSegmentIndex);
  	request.setLocation(getLocation());
  	List selectedEditParts = getCurrentViewer().getSelectedEditParts();
  	if (selectedEditParts != null && !selectedEditParts.isEmpty()) {
  	  EditPart selectedEditPart = (EditPart)selectedEditParts.get(0);
  		request.setTargetEditPart(selectedEditPart);  	  
  	}  	  	  	  	
  	return request;        
  }  
        
  /**
   * @see org.eclipse.gef.tools.SimpleDragTracker#updateSourceRequest()
   */
  protected void updateSourceRequest() 
  {
    EditorEditShapeRequest request = (EditorEditShapeRequest) getSourceRequest();
	  request.setLocation(getLocation());    	  		
  }  
    
}
