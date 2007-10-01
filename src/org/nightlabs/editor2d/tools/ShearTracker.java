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

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Cursor;

import org.nightlabs.editor2d.custom.EditorCursors;
import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import org.nightlabs.editor2d.request.EditorShearRequest;
import org.nightlabs.editor2d.util.J2DUtil;


public class ShearTracker 
extends AbstractDragTracker 
{
  protected int direction;
  
  /**
   * @param owner
   */
  public ShearTracker(GraphicalEditPart owner, int direction) {
    super(owner);
    this.direction = direction;
  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.tools.SimpleDragTracker#createSourceRequest()
   */
  protected Request createSourceRequest() 
  {
    EditorShearRequest request = new EditorShearRequest();
    request.setType(REQ_SHEAR);
    request.setLocation(getLocation());
    request.setDirection(direction);
    List selectedParts = getCurrentViewer().getSelectedEditParts();
    if (!selectedParts.isEmpty()) {
      AbstractDrawComponentEditPart part = (AbstractDrawComponentEditPart) selectedParts.get(0);
      request.setShearBounds(J2DUtil.toDraw2D(part.getDrawComponent().getBounds()));
    }
    request.setEditParts(selectedParts);     
    return request;
  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.tools.SimpleDragTracker#updateSourceRequest()
   */
  protected void updateSourceRequest() 
  {
//    Point loq = getLocation().getCopy();
//    loq.translate(getStartLocation());
//    getEditorShearRequest().setLocation(loq);
    getEditorShearRequest().setLocation(getLocation());
  }
 
  protected EditorShearRequest getEditorShearRequest() {
    return (EditorShearRequest) getSourceRequest();
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.tools.AbstractTool#getCommandName()
   */
  protected String getCommandName() {
    return REQ_SHEAR;
  }

  /**
   * @see org.eclipse.gef.tools.AbstractTool#getDefaultCursor()
   */
  protected Cursor getDefaultCursor() 
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
}
