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

import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.SnapToHelper;
import org.nightlabs.editor2d.command.CreateShapeCommand;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.model.IModelCreationFactory;
import org.nightlabs.editor2d.request.EditorCreateShapeRequest;
import org.nightlabs.editor2d.util.EditorGeneralShapeFactory;


public class EllipseTool 
extends EditorCreationTool 
{
//  public EllipseTool(CreationFactory aFactory) 
  public EllipseTool(IModelCreationFactory aFactory)  
  {
    super(aFactory);
  }

  protected Ellipse ellipse;
  
  /**
   * @see org.eclipse.gef.Tool#deactivate()
   */
  public void deactivate() 
  {
  	super.deactivate();
  	helper = null;
  }   
  
  protected boolean handleButtonDown(int button) 
  {
    if (button == 1) 
    {
	  	Point p = getLocation();
	  	getEditorCreateRequest().setLocation(p);    		    	  
  		lockTargetEditPart(getTargetEditPart());
  		// Snap only when size on drop is employed
  		helper = (SnapToHelper)getTargetEditPart().getAdapter(SnapToHelper.class);
  		    		    		    		
  		ellipse = new Ellipse();
  		Rectangle bounds = new Rectangle(p.x, p.y, p.x+1, p.y+1);
  		ellipse.setBounds(bounds);
  		getEditorCreateRequest().setUseShape(true);
  		getEditorCreateRequest().setShape(ellipse);
  		
  		stateTransition(STATE_INITIAL, STATE_DRAG_IN_PROGRESS);
  		setCursor(getDefaultCursor());    		    	
    }    	  
  	return true;
  } 
  
  protected void updateTargetRequest() 
  {
  	EditorCreateShapeRequest req = (EditorCreateShapeRequest) getCreateRequest();
  	if (isInState(STATE_DRAG_IN_PROGRESS)) 
  	{
  		Point loq = getStartLocation();
  		Rectangle bounds = new Rectangle(loq, loq);
  		bounds.union(loq.getTranslated(getDragMoveDelta()));
  		req.setSize(bounds.getSize());
  		req.setLocation(bounds.getLocation());
  		req.getExtendedData().clear();
  		if (!getCurrentInput().isAltKeyDown() && helper != null) {
  			PrecisionRectangle baseRect = new PrecisionRectangle(bounds);
  			PrecisionRectangle result = baseRect.getPreciseCopy();
  			helper.snapRectangle(req, PositionConstants.NSEW, 
  				baseRect, result);
  			req.setLocation(result.getLocation());
  			req.setSize(result.getSize());
  		}
  	} else {
  		req.setSize(null);
  		req.setLocation(getLocation());
  	}    
  }
  
  public void performCreation(int button) 
  {
    GeneralShape gs = EditorGeneralShapeFactory.createEllipse(ellipse.getBounds());
		getEditorCreateRequest().setUseShape(false);
		if (getCurrentCommand() instanceof CreateShapeCommand) {
		  CreateShapeCommand command = (CreateShapeCommand) getCurrentCommand();		  
		  command.setGeneralShape(gs);		  
		}
		
		super.performCreation(button);
  }  
}
