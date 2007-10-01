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

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.nightlabs.editor2d.command.CreateShapeCommand;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.model.IModelCreationFactory;
import org.nightlabs.editor2d.request.EditorCreateShapeRequest;
import org.nightlabs.editor2d.request.LineCreateRequest;
import org.nightlabs.editor2d.util.J2DUtil;


public class LineTool 
extends EditorCreationTool 
{
//  public LineTool(CreationFactory factory) {
  public LineTool(IModelCreationFactory factory) {  
    super(factory);
  }
  
  /**
   * Creates a {@link EditorCreateShapeRequest} and sets this tool's factory on the request.
   * @see org.eclipse.gef.tools.TargetingTool#createTargetRequest()
   */
  protected Request createTargetRequest() 
  {
  	LineCreateRequest request = new LineCreateRequest();
  	request.setFactory(getFactory());
  	return request;
  }
  
  protected LineCreateRequest getLineCreateRequest() 
  {
    return (LineCreateRequest) getTargetRequest();
  }
  
  /* 
   * the Real Location of a mousePoint including ScrollOffset
   */
  protected Point realLocation;
  
  /*
   * the Location of a mousePoint excluding ScrollOffset
   */ 
  protected Point relativeLocation;
  
  protected GeneralShape feedbackGeneralShape; 
  
  protected GeneralShape creationGeneralShape;
  
  protected Rectangle creationBounds;
  
  protected boolean handleButtonDown(int button) 
  {
    if (button == 1) 
    {
    	if (isInState(STATE_DRAG | STATE_DRAG_IN_PROGRESS)) 
    	{
    	  realLocation = getRealLocation();
    	  relativeLocation = getLocation();
    	  
    	  feedbackGeneralShape.lineTo(realLocation.x, realLocation.y);
    	  creationGeneralShape.lineTo(relativeLocation.x, relativeLocation.y);
    	  
    	  creationBounds.setBounds(J2DUtil.toDraw2D(creationGeneralShape.getBounds()));
    	  getLineCreateRequest().setCreationBounds(creationBounds);
    	}
    	else 
    	{    	      		
    		lockTargetEditPart(getTargetEditPart());
    		// Snap only when size on drop is employed
    		helper = (SnapToHelper)getTargetEditPart().getAdapter(SnapToHelper.class);
    		
    	  Point p = getRealLocation();    
    	  Point p2 = getLocation();
    		    		    		
    		feedbackGeneralShape = new GeneralShape();
    		feedbackGeneralShape.moveTo(p.x, p.y);
    		feedbackGeneralShape.lineTo(p.x+1, p.y+1);

    		creationGeneralShape = new GeneralShape();
    		creationGeneralShape.moveTo(p2.x, p2.y);
    		creationGeneralShape.lineTo(p2.x+1, p2.y+1);
    		    		
    		creationBounds = new Rectangle(p2.x, p2.y, p2.x+1, p2.y+1);    		
    		
    	  getLineCreateRequest().setLocation(getLocation());
    	  getLineCreateRequest().setMode(EditorCreateShapeRequest.BOUNDS_FIX_MODE);
    		getLineCreateRequest().setCreationBounds(creationBounds);
    	  
    		stateTransition(STATE_INITIAL, STATE_DRAG_IN_PROGRESS);
    		setCursor(getDefaultCursor());    		
    	}    	
    }    	  
  	return true;
  }  
    
  /**
   * Does nothing all actions are performed by handleButtonDown(int) or
   * updateTargetRequest
   * 
   * @see org.eclipse.gef.tools.AbstractTool#handleButtonUp(int)
   * @see #handleButtonDown(int)
   */
  protected boolean handleButtonUp(int button) 
  {
    return false;
  }
  
  /**
   * Sets the location (and size if the user is performing size-on-drop) of the request.
   * @see org.eclipse.gef.tools.TargetingTool#updateTargetRequest()
   */
  protected void updateTargetRequest() 
  {
  	LineCreateRequest req = (LineCreateRequest) getLineCreateRequest();
  	if (isInState(STATE_DRAG_IN_PROGRESS)) 
  	{  	    	  
  	  // TODO: Optimize bounds calc, by getting rect which is a union of
  	  // of all points in the PointList and getLocation
  	  // and union Rectangle can be cached until next buttonDown  	    	  
//  	  Rectangle bounds = J2DUtil.toDraw2D(creationGeneralShape.getBounds());
  	  Rectangle bounds = creationBounds.getCopy();
  	  bounds.union(getLocation());
  		
  		req.setSize(bounds.getSize());
  		req.setLocation(bounds.getLocation());
  		req.setCreationBounds(creationBounds);
//  		req.setSize(creationBounds.getSize());
//  		req.setLocation(creationBounds.getLocation());

  		req.getExtendedData().clear();
  		
  		updateShape(req);	
  		
  		if (!getCurrentInput().isAltKeyDown() && helper != null) 
  		{
  			PrecisionRectangle baseRect = new PrecisionRectangle(bounds);
  			PrecisionRectangle result = baseRect.getPreciseCopy();
  			helper.snapRectangle(req, PositionConstants.NSEW, 
  				baseRect, result);
  			req.setLocation(result.getLocation());
  			req.setSize(result.getSize());

  			updateShape(req);
  		}
  	} else {
  		req.setSize(null);
  		req.setLocation(getLocation());  		
  	}
  } 
  
  protected boolean handleKeyDown(KeyEvent e) 
  {
    if (e.character == SWT.ESC) {
    	if (stateTransition(STATE_DRAG | STATE_DRAG_IN_PROGRESS, STATE_TERMINAL)) 
    	{    	      	  
    		eraseTargetFeedback();
    		unlockTargetEditPart();    		    		
    		performCreation(1);
    	}
    	setState(STATE_TERMINAL);
    	handleFinished();
    	return true;
    }
    return false;
  }
         
  protected void updateShape(EditorCreateShapeRequest request) 
  { 
	  Point p = getRealLocation();
	  Point p2 = getLocation();
        
    feedbackGeneralShape.setLastPoint(p.x, p.y);
    creationGeneralShape.setLastPoint(p2.x, p2.y);
    if (request.getGeneralShape() == null)
      request.setGeneralShape(feedbackGeneralShape);    
  }
    
  public void performCreation(int button) 
  {		
		if (getCurrentCommand() instanceof CreateShapeCommand) 
		{
		  CreateShapeCommand command = (CreateShapeCommand) getCurrentCommand();
//		  command.setGeneralShape(creationGeneralShape);
		  GeneralShape gs = J2DUtil.removePathSegment(creationGeneralShape, creationGeneralShape.getSize()-1);
		  command.setGeneralShape(gs);
		}
		
		super.performCreation(button);
  }
    
  /**
   * @see org.eclipse.gef.Tool#deactivate()
   */
  public void deactivate() 
  {
  	super.deactivate();
  	helper = null;
  }  
    
}
