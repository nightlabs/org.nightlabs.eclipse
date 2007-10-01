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

package org.nightlabs.editor2d.editpolicy;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.swt.graphics.Color;
import org.nightlabs.editor2d.EditorStateManager;
import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.figures.ShapeFigure;
import org.nightlabs.editor2d.handle.EditorNonResizableHandleKit;
import org.nightlabs.editor2d.handle.EditorResizableHandleKit;
import org.nightlabs.editor2d.handle.RotateCenterHandle;
import org.nightlabs.editor2d.handle.RotateHandleKit;
import org.nightlabs.editor2d.handle.ShapeEditHandleKit;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.request.EditorEditShapeRequest;
import org.nightlabs.editor2d.request.EditorRequestConstants;
import org.nightlabs.editor2d.request.EditorRotateCenterRequest;
import org.nightlabs.editor2d.request.EditorRotateRequest;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.util.EditorUtil;
import org.nightlabs.editor2d.util.J2DUtil;
import org.nightlabs.editor2d.util.feedback.FeedbackUtil;


public class DrawComponentResizeEditPolicy 
extends ResizableEditPolicy 
implements EditorRequestConstants
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(DrawComponentResizeEditPolicy.class);
     
  public DrawComponentResizeEditPolicy() 
  {
  	this(true, true);
  }
 
  public DrawComponentResizeEditPolicy(boolean rotation, boolean scale) 
  {
  	this(rotation, scale, rotation);
  }

  public DrawComponentResizeEditPolicy(boolean rotation, boolean scale, boolean rotationCenter) 
  {
  	super();
  	this.rotation = rotation;
  	this.rotateCenter = rotationCenter;
  	this.scale = scale;  	
  	if (!scale)
  		setResizeDirections(PositionConstants.NONE);  		
  }
  
  private boolean rotation = true;
  private boolean scale = true;
  private boolean rotateCenter = true;
  
  /**
   * Creates the figure used for feedback.
   * @return the new feedback figure
   */
  protected IFigure createDragSourceFeedbackFigure() 
  {       	
    IFigure figure = getCustomFeedbackFigure(getHost().getModel());
  	figure.setBounds(getInitialFeedbackBounds());
  	addFeedback(figure);
  	return figure;
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
      
  protected ShapeFigure getCustomFeedbackFigure(Object modelPart) 
  {
  	return FeedbackUtil.getCustomFeedbackFigure(modelPart);
  }  
  
  protected Rectangle getInitialFeedbackBounds() 
  {
//    LOGGER.debug("InitalFeedbackBounds = "+getHostFigure().getBounds());
  	return getHostFigure().getBounds();
  }

  /**
   * @see org.eclipse.gef.EditPolicy#understandsRequest(org.eclipse.gef.Request)
   */
  public boolean understandsRequest(Request request) 
  {
  	if (REQ_EDIT_SHAPE.equals(request.getType()))
  		return true;
  	
  	if (rotation) {
    	if (request instanceof EditorRotateRequest)
    		return true;
  	}

  	if (rotateCenter) {
    	if (request instanceof EditorRotateCenterRequest)
    		return true;  		    	  		
  	}
  	
  	if (REQ_RESIZE.equals(request.getType())) {
  		if (scale)
  			return true;
  		else
  			return false;
  	}

  	return super.understandsRequest(request);
  }
    
  protected List createSelectionHandles() 
  {
  	List list = new ArrayList();  	
    if (EditorStateManager.getCurrentState() == EditorStateManager.STATE_EDIT_SHAPE) 
    {
    	if (getHost() instanceof ShapeDrawComponentEditPart) {
    	  ShapeDrawComponentEditPart sdcEditPart = (ShapeDrawComponentEditPart) getHost();
    	  ShapeEditHandleKit.addHandles(sdcEditPart, list);
    	  return list;
    	}  	        
    }
    else if (EditorStateManager.getCurrentState() == EditorStateManager.STATE_ROTATE)
    {
      clearHandleLayer();
      RotateHandleKit.addHandles(getHost().getViewer().getSelectedEditParts(), list);
      return list;
    }
    else if (EditorStateManager.getCurrentState() == EditorStateManager.STATE_NORMAL_SELECTION)
    {    	
    	// WORKAROUND: use own ResizableHandleKit and NonResizeableHandleKit
    	// because MoveHandle are created with white line by default instead of black line
    	int directions = getResizeDirections();
    	if (directions == 0) {
    		EditorNonResizableHandleKit.addHandles((GraphicalEditPart)getHost(), list);
    		return list;
    	}
    	else if (directions != -1) {
    		EditorResizableHandleKit.addMoveHandle((GraphicalEditPart)getHost(), list);
    		if ((directions & PositionConstants.EAST) != 0)
    			EditorResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    					PositionConstants.EAST);
    		else
    			EditorNonResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    					PositionConstants.EAST);
    		if ((directions & PositionConstants.SOUTH_EAST) == PositionConstants.SOUTH_EAST)
    			EditorResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    					PositionConstants.SOUTH_EAST);
    		else
    			EditorNonResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list,
    					PositionConstants.SOUTH_EAST);
    		if ((directions & PositionConstants.SOUTH) != 0)
    			EditorResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    					PositionConstants.SOUTH);
    		else
    			EditorNonResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    					PositionConstants.SOUTH);
    		if ((directions & PositionConstants.SOUTH_WEST) == PositionConstants.SOUTH_WEST)
    			EditorResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    					PositionConstants.SOUTH_WEST);
    		else
    			EditorNonResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    						PositionConstants.SOUTH_WEST);
    		if ((directions & PositionConstants.WEST) != 0)
    			EditorResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    					PositionConstants.WEST);
    		else
    			EditorNonResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    						PositionConstants.WEST);
    		if ((directions & PositionConstants.NORTH_WEST) == PositionConstants.NORTH_WEST)
    			EditorResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    					PositionConstants.NORTH_WEST);
    		else
    			EditorNonResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    					PositionConstants.NORTH_WEST);
    		if ((directions & PositionConstants.NORTH) != 0)
    			EditorResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    					PositionConstants.NORTH);
    		else
    			EditorNonResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    					PositionConstants.NORTH);
    		if ((directions & PositionConstants.NORTH_EAST) == PositionConstants.NORTH_EAST)
    			EditorResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    					PositionConstants.NORTH_EAST);
    		else
    			EditorNonResizableHandleKit.addHandle((GraphicalEditPart)getHost(), list, 
    						PositionConstants.NORTH_EAST);
    		
    		return list;
    	} else {
    		EditorResizableHandleKit.addHandles((GraphicalEditPart)getHost(), list);
    		return list;
    	}    	    	
    }
   	return list;    
//  	return super.createSelectionHandles();  	  	   	
  }
    
  protected void clearHandleLayer() 
  {
  	IFigure layer = getLayer(LayerConstants.HANDLE_LAYER);
  	for (int i = 0; i < layer.getChildren().size(); i++) {
  	  IFigure figure = (IFigure) layer.getChildren().get(i);
  		layer.remove(figure);  	  
  	}
  }  
  
  protected void removeSelectionHandles() 
  {
  	if (handles == null)  	  
  		return;
  	
  	IFigure layer = getLayer(LayerConstants.HANDLE_LAYER);  	
  	if (layer.getChildren().isEmpty())
  	  return;
  	
  	for (int i = 0; i < handles.size(); i++) {
  	  if (layer.getChildren().contains(handles.get(i)))
  	    layer.remove((IFigure)handles.get(i));  	  
  	}
  	
  	handles = null;
  }  
  
  public void eraseSourceFeedback(Request request) 
  {
    if (request.getType().equals(REQ_EDIT_SHAPE))
      eraseEditShapeFeedback((EditorEditShapeRequest)request);
    
    else if (request.getType().equals(REQ_ROTATE))
    	if (rotation)
    		eraseRotateFeedback((EditorRotateRequest)request);
    
    else if (request.getType().equals(REQ_EDIT_ROTATE_CENTER))
    	if (rotateCenter)
    		eraseEditRotateCenterFeedback((EditorRotateCenterRequest)request);
    
//    else if (request.getType().equals(REQ_SHEAR))
//      eraseShearFeedback();
    
    if (REQ_RESIZE.equals(request.getType())) 
    {
    	if (scale)
    		eraseChangeBoundsFeedback((ChangeBoundsRequest)request);
    	else
    		return;
    }       
    else
      super.eraseSourceFeedback(request);
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
    
  protected void eraseEditRotateCenterFeedback(EditorRotateCenterRequest request) 
  {
  	if (feedback != null) {
  		removeFeedback(feedback);
  	}
  	feedback = null;    
  }
  
//  public void showSourceFeedback(Request request) 
//  {
//    if (request.getType().equals(REQ_EDIT_SHAPE))
//      showEditShapeFeedback((EditorEditShapeRequest)request);    
//    else if (request.getType().equals(REQ_ROTATE))    	
//      showRotateFeedback((EditorRotateRequest)request);
//    else if (request.getType().equals(REQ_EDIT_ROTATE_CENTER))
//      showEditRotateCenterFeedback((EditorRotateCenterRequest)request);
//    else if (request.getType().equals(REQ_SHEAR))
//      showShearFeedback((EditorShearRequest)request);        
//    else  
//      super.showSourceFeedback(request);
//  }

  public void showSourceFeedback(Request request) 
  {
    if (request.getType().equals(REQ_EDIT_SHAPE))
      showEditShapeFeedback((EditorEditShapeRequest)request);    
    else if (request.getType().equals(REQ_ROTATE))
    	if (rotation)
    		showRotateFeedback((EditorRotateRequest)request);
    else if (request.getType().equals(REQ_EDIT_ROTATE_CENTER))
    	if (rotateCenter)
    		showEditRotateCenterFeedback((EditorRotateCenterRequest)request);
    if (REQ_RESIZE.equals(request.getType())) 
    {
    	if (scale)
    		showChangeBoundsFeedback((ChangeBoundsRequest)request);
    	else
    		return;
    }
//    else if (request.getType().equals(REQ_SHEAR))
//      showShearFeedback((EditorShearRequest)request);  
    else  
      super.showSourceFeedback(request);
  }
  
//  protected ShapeFigure getShearFeedbackFigure() 
//  {
//    if (feedback == null) {
//      feedback = createDragSourceFeedbackFigure();       
//    	PrecisionRectangle rect = new PrecisionRectangle(getInitialFeedbackBounds().getCopy());
//    	feedback.setBounds(getConstraintFor(rect));
//    }      
//    return (ShapeFigure) feedback;
//  }
  
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
  
  protected Polyline getPolylineFeedback() 
  {
  	if (feedback == null) {    	
  	  feedback = createPolylineFigure((GraphicalEditPart)getHost());  	  
    	addFeedback(feedback);  	  
  	}
  	return (Polyline) feedback;    
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
        
    // calculated the rotation angle based on mouseLocation and rotatioCenter
    double rotationTmp = EditorUtil.calcRotation(location, rotationCenter);    
    double rotation = - (rotationTmp - rotationOffset);
    
    // if the rotation request is constrained get the closest value
    if (request.isConstrainedRotation()) {
    	rotation = getClosestValue(request.getConstrainedValues(), rotation);    	
    }
      	
    request.setRotation(rotation);
    double rotationInRadinans = Math.toRadians(rotation);
    
    at.setToIdentity();
    at.rotate(rotationInRadinans, rotationCenter.x, rotationCenter.y);
    rotatedShape = (GeneralShape) unrotatedShape.clone();
    rotatedShape.transform(at);
    rotationFeedback.setGeneralShape(rotatedShape);
    getFeedbackLayer().repaint();
  }  
  
  protected double getClosestValue(List<Double> values, double rotation) 
  {
  	Collections.sort(values);
  	rotation = checkRotation(rotation);
  	double rotationToChoose = rotation;  	
  	double diffUp = Double.MAX_VALUE;
  	double closestUp = Double.MAX_VALUE;  	
  	double diffDown = -Double.MAX_VALUE;
  	double closestDown = -Double.MAX_VALUE;
  	
  	for (int i=0; i<values.size(); i++) 
  	{
  		double d = values.get(i);
  		double diff = rotation - d;
  		if (diff > 0) {
  			diffUp = Math.min(diff, diffUp);
  			if (diffUp == diff)
  				closestUp = d;
  		}
  		else {
  			diffDown = Math.max(diff, diffDown);
  			if (diffDown == diff)
  				closestDown = d;
  		}
  		
  		double closest = Math.min(Math.abs(closestUp), Math.abs(closestDown));
  		rotationToChoose = closest;
  	}
  	return rotationToChoose;  	
  }
  
  // converts a negative rotation into a positive 
  protected double checkRotation(double rotation) 
  {
  	if (rotation > 0)
  		return rotation;
  	else {
  		return rotation += 360;
  	}
  }
  
//  protected void eraseShearFeedback() 
//  {
//    if (feedback != null)
//      removeFeedback(feedback);
//    
//    feedback = null;
//    rotatedShape = null;
//    unrotatedShape = null;
//  }
  
//  protected void showShearFeedback(EditorShearRequest request) 
//  {
//    ShapeFigure shearFeedback = getShearFeedbackFigure();
//    if (unrotatedShape == null)
//      unrotatedShape = (GeneralShape) shearFeedback.getGeneralShape().clone();     
//    
//    Point location = request.getLocation();
//    location.translate(getScrollOffset());    
//    LOGGER.debug("location = "+location);
//    
//    Rectangle bounds = request.getShearBounds();
//    LOGGER.debug("shearBounds = "+bounds);
//    
//    AffineTransform at = getShearTransform(location, bounds, request.getDirection());
//    rotatedShape = (GeneralShape) unrotatedShape.clone();
//    rotatedShape.transform(at);
//    shearFeedback.setGeneralShape(rotatedShape);
//    getFeedbackLayer().repaint();        
//    
//    request.setAffineTransform(at);    
//  }  
  
//  protected AffineTransform getShearTransform(Point location, Rectangle bounds, int direction)  
//  {
//    double shear = 0.0d;
//    double idleShear = 0.0d;
//    at.setToIdentity();
//    switch(direction)
//    {
//    	case(PositionConstants.WEST):
//    	case(PositionConstants.EAST):
//    	  double heightMiddle = bounds.y + bounds.height/2;
//    		double y = location.y;
//    		double distanceY = heightMiddle - y;
//    	  double height = bounds.height;
//    		shear = distanceY / height;
//    		at.shear(idleShear, shear);
//    		break;
//    	case(PositionConstants.NORTH):
//    	case(PositionConstants.SOUTH):
//    	  double widthMiddle = bounds.x + bounds.width/2;
//    		double x = location.x;
//    		double distanceX = widthMiddle - x;
//    	  double width = bounds.width;
//    		shear = distanceX / width;
//    		at.shear(shear, idleShear);
//    		break;
//    }
//		LOGGER.debug("shear = "+shear);    
//    return at;
//  }  
  
  protected ShapeFigure getRotateFeedbackFigure() 
  {
    if (feedback == null) {
      feedback = createDragSourceFeedbackFigure();       
    	PrecisionRectangle rect = new PrecisionRectangle(getInitialFeedbackBounds().getCopy());
    	feedback.setBounds(getConstraintFor(rect));
    }      
    return (ShapeFigure) feedback;
  }
    
  protected void showEditShapeFeedback(EditorEditShapeRequest request) 
  {    
  	Polyline polyline = getPolylineFeedback();
  	Point newPoint = new Point(request.getLocation().x, request.getLocation().y);
  	newPoint.translate(getScrollOffset());
  	polyline.setPoint(newPoint, request.getPathSegmentIndex());  	         	       
  }
  
  protected ShapeFigure getEditShapeFeedbackFigure() 
  {
  	if (feedback == null)
  		feedback = createEditShapeFeedbackFigure();
  	return (ShapeFigure) feedback;
  }
  
//  protected ShapeFigure createEditShapeFeedbackFigure() 
  protected IFigure createEditShapeFeedbackFigure()
  {  	
    IFigure figure = getCustomFeedbackFigure(getHost().getModel());    
  	figure.setBounds(getInitialFeedbackBounds());
  	addFeedback(figure);
  	return figure;    
  }
      
  public Rectangle getConstraintFor(Rectangle rectangle) {
    return EditorUtil.oldToAbsolute((GraphicalEditPart)getHost(), rectangle);
  }
//  public Rectangle getConstraintFor(Rectangle rectangle) {
//    return EditorUtil.toAbsoluteWithScrollOffset(getHost(), rectangle);
//  }
  
  public Point getConstraintFor(Point point){
    return EditorUtil.toAbsolute((GraphicalEditPart)getHost(), point);
  } 
//  public Point getConstraintFor(Point point){
//    return EditorUtil.toAbsoluteWithScrollOffset(getHost(), point.x, point.y);
//  } 
  
  protected Point getScrollOffset() {
    return EditorUtil.getScrollOffset(getHost());
  }
  
// ****************************** BEGIN Workaround private feedback *****************************  
  
  protected IFigure feedback;
  
  /**
   * Lazily creates and returns the feedback figure used during drags.
   * @return the feedback figure
   */
  protected IFigure getDragSourceFeedbackFigure() 
  {
  	if (feedback == null)
  		feedback = createDragSourceFeedbackFigure();
  	return feedback;
  }
      
  /**
   * @see org.eclipse.gef.EditPolicy#deactivate()
   */
  public void deactivate() {
  	if (feedback != null) {
  		removeFeedback(feedback);
  		feedback = null;
  	}
  	hideFocus();
  	super.deactivate();
  }  
    
// ****************************** END Workaround private feedback *****************************

  protected boolean showFeedBackText = true;
  protected Label feedbackLabel;
    
  protected void showChangeBoundsFeedback(ChangeBoundsRequest request) 
  {
  	IFigure feedback = getDragSourceFeedbackFigure();
  	
  	PrecisionRectangle rect = new PrecisionRectangle(getInitialFeedbackBounds().getCopy());
  	getHostFigure().translateToAbsolute(rect);
  	rect.translate(request.getMoveDelta());
  	rect.resize(request.getSizeDelta());
  	
  	feedback.translateToRelative(rect);
  	feedback.setBounds(rect);

  	if (showFeedBackText) {
  		showFeedbackText(request);
  	}
  	  	
  	getFeedbackLayer().repaint();  	
  }    
  
  /**
   * Erases drag feedback.  This method called whenever an erase feedback request is
   * received of the appropriate type.
   * @param request the request
   */
  protected void eraseChangeBoundsFeedback(ChangeBoundsRequest request) 
  {
  	if (feedback != null) {
  		removeFeedback(feedback);
  		if (showFeedBackText) {
  			eraseFeedbackText();
  		}
  	}
  	feedback = null;
  }   
  
  protected Label getFeedbackTextFigure()
  {
  	if (feedbackLabel == null)
  		feedbackLabel = createFeedbackTextFigure(""); //$NON-NLS-1$
  	return feedbackLabel;
  }
  
  protected Color outlineColor = ColorConstants.black;
  protected Color getOutlineColor() {
  	return outlineColor;
  }
  
  protected Label createFeedbackTextFigure(String text) 
  {       	
    Label l = new Label(text);
  	l.setForegroundColor(getOutlineColor());    
  	l.setBounds(getInitialFeedbackBounds());
  	addFeedback(l);  	  	
  	return l;
  }  
    
  protected void showFeedbackText(ChangeBoundsRequest request) 
  {
  	Label feedbackText = getFeedbackTextFigure(); 
  	
  	feedbackText.setText(getText(request));
  	feedbackText.setLocation(getFeedbackTextLocation(request));  	
  	feedbackText.setSize(100, 20);
  	  	
  	getFeedbackLayer().repaint();  	
  }  
  
  protected static final Dimension EMPTY_DIMENSION = new Dimension(0,0);
  protected static final Point EMPTY_POINT = new Point(0,0);
  
  protected Point getFeedbackTextLocation(ChangeBoundsRequest request) 
  {
  	Point loc = request.getLocation();
  	if (loc != null) {
  		loc.translate(EditorUtil.getScrollOffset(getHost()));  	
  		return loc;  		
  	} else {
  		return new Point();
  	}
  }
  
  protected String getText(ChangeBoundsRequest request) 
  {
  	Dimension sizeDelta = request.getSizeDelta();
//  	Point moveDelta = request.getMoveDelta();  
  	StringBuffer sb = new StringBuffer();
  	Rectangle feedbackBounds = getDragSourceFeedbackFigure().getBounds();
  	
  	if (sizeDelta.equals(EMPTY_DIMENSION)) {  	
    	Point absoluteLocation = EditorUtil.toAbsolute(getHost(), feedbackBounds.x, feedbackBounds.y);
  		
    	String x = Messages.getString("org.nightlabs.editor2d.editpolicy.DrawComponentResizeEditPolicy.x"); //$NON-NLS-1$
    	String y = Messages.getString("org.nightlabs.editor2d.editpolicy.DrawComponentResizeEditPolicy.y"); //$NON-NLS-1$
    	sb.append(x+" "); //$NON-NLS-1$
    	sb.append(absoluteLocation.x);
    	sb.append(", "); //$NON-NLS-1$
    	sb.append(y+" "); //$NON-NLS-1$
    	sb.append(absoluteLocation.y);    	  		
  	}
  	else {
    	Point absoluteSize = EditorUtil.toAbsolute(getHost(), feedbackBounds.width, feedbackBounds.height);
    	
    	String width = Messages.getString("org.nightlabs.editor2d.editpolicy.DrawComponentResizeEditPolicy.shortWidth"); //$NON-NLS-1$
    	String height = Messages.getString("org.nightlabs.editor2d.editpolicy.DrawComponentResizeEditPolicy.shortHeight"); //$NON-NLS-1$
    	sb.append(width+" "); //$NON-NLS-1$
    	sb.append(absoluteSize.x);
    	sb.append(", "); //$NON-NLS-1$
    	sb.append(height+" "); //$NON-NLS-1$
    	sb.append(absoluteSize.y);    	  		   		
  	}
  	return sb.toString();  	
  }
    
  protected void eraseFeedbackText() 
  {
    if (feedbackLabel != null)
      removeFeedback(feedbackLabel);
    
    feedbackLabel = null;
  }  
}
