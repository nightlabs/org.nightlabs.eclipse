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

import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.command.RotateCenterCommand;
import org.nightlabs.editor2d.command.RotateCommand;
import org.nightlabs.editor2d.figures.ShapeFigure;
import org.nightlabs.editor2d.handle.RotateCenterHandle;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.request.EditorRotateCenterRequest;
import org.nightlabs.editor2d.request.EditorRotateRequest;
import org.nightlabs.editor2d.util.EditorUtil;
import org.nightlabs.editor2d.util.feedback.FeedbackUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorRotateEditPolicy
extends EditorFeedbackPolicy
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(EditorRotateEditPolicy.class);
	
	// TODO: find out why this Method is never triggered
  public Command getCommand(Request request) 
  {    
    if (request instanceof EditorRotateRequest)
      return getRotateCommand((EditorRotateRequest)request);

    if (request instanceof EditorRotateCenterRequest)
      return getRotateCenterCommand((EditorRotateCenterRequest)request);
    
    if (logger.isDebugEnabled())
    	logger.debug("getCommand(Request = "+request+")"); //$NON-NLS-1$ //$NON-NLS-2$
    
  	return super.getCommand(request);
  }  
	
  protected Command getRotateCenterCommand(EditorRotateCenterRequest request) 
  {
    RotateCenterCommand cmd = new RotateCenterCommand(request);
    Point rotationCenter = request.getRotationCenter().getCopy();
    rotationCenter = EditorUtil.toAbsolute(getHost(), rotationCenter.x, rotationCenter.y);
    cmd.setRotationCenter(rotationCenter);  
    if (logger.isDebugEnabled())
    	logger.debug("cmd.rotationCenter = "+rotationCenter); //$NON-NLS-1$
    return cmd;
  }
  
  protected Command getRotateCommand(EditorRotateRequest request) 
  {
    RotateCommand cmd = new RotateCommand(request);
    double rotation = request.getRotation();
    cmd.setRotation(rotation);
    if (logger.isDebugEnabled())
    	logger.debug("getRotateCommand().rotation = "+rotation); //$NON-NLS-1$
    return cmd;
  }  
	
  public void showSourceFeedback(Request request) 
  {
    if (request.getType().equals(REQ_ROTATE))
      showRotateFeedback((EditorRotateRequest)request);
    else if (request.getType().equals(REQ_EDIT_ROTATE_CENTER))
      showEditRotateCenterFeedback((EditorRotateCenterRequest)request);
    else  
      super.showSourceFeedback(request);
  }  
  
  protected final AffineTransform at = new AffineTransform();
  protected Point rotationCenter;
  protected GeneralShape unrotatedShape;
  protected GeneralShape rotatedShape;
  protected double rotationOffset = Double.MAX_VALUE;
  protected void showRotateFeedback(EditorRotateRequest request) 
  {
    ShapeFigure rotationFeedback = getRotateFeedbackFigure();
      	
    if (unrotatedShape == null)
      unrotatedShape = (GeneralShape) rotationFeedback.getGeneralShape().clone();     
      
    if (rotationCenter == null && request.getRotationCenter() != null)      
      rotationCenter = getConstraintFor(request.getRotationCenter());         
    
    Point location = request.getLocation();
    location.translate(getScrollOffset());
     
    if (rotationOffset == Double.MAX_VALUE)
      rotationOffset = EditorUtil.calcRotation(location, rotationCenter);
    
    double rotationTmp = EditorUtil.calcRotation(location, rotationCenter);
    double rotation = - (rotationTmp - rotationOffset);
    request.setRotation(rotation);
    double rotationInRadinans = Math.toRadians(rotation);
    at.setToIdentity();
    at.rotate(rotationInRadinans, rotationCenter.x, rotationCenter.y);
//    rotationFeedback.transform(at);
    rotatedShape = (GeneralShape) unrotatedShape.clone();
    rotatedShape.transform(at);
    rotationFeedback.setGeneralShape(rotatedShape);
    getFeedbackLayer().repaint();
  }  
  
  protected Rectangle rotateCenterBounds;
  protected void showEditRotateCenterFeedback(EditorRotateCenterRequest request)
  {
    IFigure feedback = getEditRotateCenterFeedback(request);
    Point location = request.getRotationCenter();
    location.translate(getScrollOffset());
    Point feedbackLocation = location.getCopy();
    feedbackLocation.translate(-rotateCenterBounds.width/2, -rotateCenterBounds.height/2);
    feedback.setLocation(feedbackLocation);
    feedback.repaint();
    
    if (logger.isDebugEnabled())
    	logger.debug("feedBack.location = "+feedback.getBounds()); //$NON-NLS-1$
  }
  
  protected IFigure createEditRotateCenterFeedback(EditorRotateCenterRequest request) 
  {
    RotateCenterHandle figure = new RotateCenterHandle(request.getEditParts()); 
    request.setMultiple(figure.isMultiple());
    figure.setBackgroundColor(FeedbackUtil.DEFAULT_PAINT_DESCRIPTOR.getBackgroundColor());
    figure.setForegroundColor(FeedbackUtil.DEFAULT_PAINT_DESCRIPTOR.getForegroundColor());         
    return figure;
  }
  
  protected IFigure getEditRotateCenterFeedback(EditorRotateCenterRequest request) 
  {
    if (feedback == null) {
      feedback = createEditRotateCenterFeedback(request);
      rotateCenterBounds = feedback.getBounds();
      addFeedback(feedback);
    }
    return feedback;
  }

  protected ShapeFigure getRotateFeedbackFigure() 
  {
    if (feedback == null) {
      feedback = createDragSourceFeedbackFigure();       
    	PrecisionRectangle rect = new PrecisionRectangle(getInitialFeedbackBounds().getCopy());
    	feedback.setBounds(getConstraintFor(rect));
    }      
    return (ShapeFigure) feedback;
  }  
  
  public void eraseSourceFeedback(Request request) 
  {
    if (request.getType().equals(REQ_ROTATE))
      eraseRotateFeedback((EditorRotateRequest)request);
    else if (request.getType().equals(REQ_EDIT_ROTATE_CENTER))
      eraseEditRotateCenterFeedback((EditorRotateCenterRequest)request);
  }  
  
  protected void eraseRotateFeedback(EditorRotateRequest request) 
  {
    if (feedback != null)
      removeFeedback(feedback);
    
    feedback = null;
    rotatedShape = null;
    unrotatedShape = null;
    rotationCenter = null;
    rotationOffset = Double.MAX_VALUE;
  }

  protected void eraseEditRotateCenterFeedback(EditorRotateCenterRequest request) 
  {
  	if (feedback != null) {
  		removeFeedback(feedback);
  	}
  	feedback = null;    
  }

	@Override
	public boolean understandsRequest(Request request) 
	{
    if (request instanceof EditorRotateRequest)
    	return true;
    
    if (request instanceof EditorRotateCenterRequest)
    	return true;
    
		return super.understandsRequest(request);
	}
    
	@Override
	public EditPart getTargetEditPart(Request request) {
		return getHost();		
	}  
	
}
