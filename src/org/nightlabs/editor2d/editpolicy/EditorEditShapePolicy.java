/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
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
package org.nightlabs.editor2d.editpolicy;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.command.shape.EditShapeCommand;
import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.request.EditorEditShapeRequest;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.util.J2DUtil;
import org.nightlabs.editor2d.util.feedback.FeedbackUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorEditShapePolicy 
extends EditorFeedbackPolicy 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(EditorEditShapePolicy.class);
	
	// TODO: find out why this Method is never triggered	
	public Command getCommand(Request request) 
  {    
  	if (REQ_EDIT_SHAPE.equals(request.getType()))
  		return getEditShapeCommand((EditorEditShapeRequest)request);
    
    logger.debug("getCommand(Request = "+request+")"); //$NON-NLS-1$ //$NON-NLS-2$
    
  	return super.getCommand(request);
  }  

	 /**
   * Returns the command contribution for the given edit shape request. 
   * By default, the request is redispatched to the host's parent as a {@link
   * org.nightlabs.editor2d.request.EditorRequestConstants#REQ_EDIT_SHAPE}.  
   * The parent's editpolicies determine how to perform the resize based on the layout manager in use.
   * @param request the edit shape request
   * @return the command contribution obtained from the parent
   */
  protected Command getEditShapeCommand(EditorEditShapeRequest request) 
  {
    EditShapeCommand editShapeCommand = null;
    if (editShapeCommand == null) 
    {
    	editShapeCommand = new EditShapeCommand();
    	ShapeDrawComponentEditPart sdcEP = (ShapeDrawComponentEditPart) request.getTargetEditPart();
    	ShapeDrawComponent sdc = sdcEP.getShapeDrawComponent();
    	editShapeCommand.setShapeDrawComponent(sdc);
    	editShapeCommand.setPathSegmentIndex(request.getPathSegmentIndex());
    	editShapeCommand.setLabel(Messages.getString("org.nightlabs.editor2d.editpolicy.EditorEditShapePolicy.command.label.editShape"));       //$NON-NLS-1$
    }
//  	Point modelPoint = getConstraintPointFor(request.getLocation());
    Point modelPoint = getConstraintFor(request.getLocation());    
  	editShapeCommand.setLocation(modelPoint); 
		return editShapeCommand;		
  } 		
  
  public void showSourceFeedback(Request request) 
  {
    if (request.getType().equals(REQ_EDIT_SHAPE))
      showEditShapeFeedback((EditorEditShapeRequest)request);
  }
  
  public void eraseSourceFeedback(Request request) 
  {
    if (request.getType().equals(REQ_EDIT_SHAPE))
      eraseEditShapeFeedback((EditorEditShapeRequest)request);
  }
  
  /**
   * Erases drag feedback.  This method called whenever an erase feedback request is
   * received of the appropriate type.
   * @param request the request
   */  
  protected void eraseEditShapeFeedback(EditorEditShapeRequest request) 
  {
  	if (feedback != null) {
  		removeFeedback(feedback);
  	}
  	feedback = null;    
  }  
  
  protected void showEditShapeFeedback(EditorEditShapeRequest request) 
  {    
  	Polyline polyline = getPolylineFeedback();
  	Point newPoint = new Point(request.getLocation().x, request.getLocation().y);
  	newPoint.translate(getScrollOffset());
  	polyline.setPoint(newPoint, request.getPathSegmentIndex());  	         	       
  }  
  
  protected Polyline getPolylineFeedback() 
  {
  	if (feedback == null) {    	
  	  feedback = createPolylineFigure((GraphicalEditPart)getHost());  	  
    	addFeedback(feedback);  	  
  	}
  	return (Polyline) feedback;    
  }  
  
  protected Polyline createPolylineFigure(GraphicalEditPart part) 
  {      
    ShapeDrawComponentEditPart sdcEP = (ShapeDrawComponentEditPart) part;
    Polyline polyline = J2DUtil.toPolyline(sdcEP.getGeneralShape());      
    polyline.setLineStyle(2);
    polyline.setXOR(true);
    polyline.setFill(true);
    polyline.setBackgroundColor(FeedbackUtil.DEFAULT_PAINT_DESCRIPTOR.getBackgroundColor());
    polyline.setForegroundColor(FeedbackUtil.DEFAULT_PAINT_DESCRIPTOR.getForegroundColor());    
        
    // transform each point to absolute
  	for (int i=0; i<polyline.getPoints().size(); i++) {
  	  Point p = polyline.getPoints().getPoint(i);
  	  Point newPoint = getConstraintFor(p);
  	  polyline.getPoints().setPoint(newPoint, i);
  	}    
    
  	return polyline;
  }

	@Override
	public boolean understandsRequest(Request req) 
	{
		if (REQ_EDIT_SHAPE.equals(req.getType()))
			return true;
		
		return super.understandsRequest(req);
	}
    
	public EditPart getTargetEditPart(Request request) {
		return getHost();		
	}  
}
