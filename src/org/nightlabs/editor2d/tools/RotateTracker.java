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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Cursor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.custom.EditorCursors;
import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import org.nightlabs.editor2d.request.EditorRotateRequest;
import org.nightlabs.editor2d.util.EditorUtil;

public class RotateTracker 
extends AbstractDragTracker 
{
  protected int direction; 
  
  public RotateTracker(AbstractDrawComponentEditPart owner, int direction)
  {
    super(owner);
    // TODO: direction is never used
    this.direction = direction;
  }

  protected AbstractDrawComponentEditPart getAbstractDrawComponentEditPart() {
    return (AbstractDrawComponentEditPart) owner;
  }
  
  protected DrawComponent getDrawComponent() {
  	return getAbstractDrawComponentEditPart().getDrawComponent();
  }
  
  /**
   * @see org.eclipse.gef.tools.SimpleDragTracker#createSourceRequest()
   */
  protected Request createSourceRequest() 
  {
    EditorRotateRequest rotateRequest = new EditorRotateRequest();
    rotateRequest.setType(REQ_ROTATE);
    rotateRequest.setLocation(getLocation());   
    rotateRequest.setEditParts(getCurrentViewer().getSelectedEditParts());
    if (getDrawComponent().getConstrainedRotationValues() != null) {
    	rotateRequest.setConstrainedRotation(true);
    	rotateRequest.setConstrainedValues(getDrawComponent().getConstrainedRotationValues());
    }
    return rotateRequest;
  }
  
  protected String getCommandName() 
  {
    return REQ_ROTATE;
  }

  /**
   * @see org.eclipse.gef.tools.AbstractTool#getDefaultCursor()
   */
  protected Cursor getDefaultCursor() 
  {
    return EditorCursors.ROTATE;
  }  
          
  protected Point rotationCenter;
  
  /**
   * @see org.eclipse.gef.tools.SimpleDragTracker#updateSourceRequest()
   */
  protected void updateSourceRequest() 
  {
    if (getEditorRotateRequest().getRotationCenter() == null && owner != null) 
    {
	    if (getEditorRotateRequest().getEditParts().size() > 1) {
	      rotationCenter = EditorUtil.getCenter(getEditorRotateRequest().getEditParts());
	      getEditorRotateRequest().setRotationCenter(rotationCenter);	      
	      getEditorRotateRequest().setMultiple(true);
	    }
	    else {      
        rotationCenter = new Point(
        		getAbstractDrawComponentEditPart().getDrawComponent().getRotationX(),
            getAbstractDrawComponentEditPart().getDrawComponent().getRotationY());
        getEditorRotateRequest().setRotationCenter(rotationCenter);
        getEditorRotateRequest().setMultiple(false);        
	    }                  
    }
    getEditorRotateRequest().setLocation(getLocation());        
  }
  
  protected EditorRotateRequest getEditorRotateRequest() 
  {
    return (EditorRotateRequest) getSourceRequest();
  }
  
  protected String getDebugName() 
  {
    return "Rotate Handle Tracker";//$NON-NLS-1$
  }

  // Override to avoid the single selection of the EditPart whiches handle has been selected
	protected void performSelection() 
	{
		
	}       
}
