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

package org.nightlabs.editor2d.ui.tools;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.SnapToHelper;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.ui.command.CreateShapeCommand;
import org.nightlabs.editor2d.ui.model.IModelCreationFactory;
import org.nightlabs.editor2d.ui.request.EditorCreateShapeRequest;
import org.nightlabs.editor2d.ui.util.EditorGeneralShapeFactory;


public class RectangleTool
extends EditorCreationTool
{
//  public RectangleTool(CreationFactory aFactory)
  public RectangleTool(IModelCreationFactory aFactory)
  {
    super(aFactory);
  }
  
  /**
   * @see org.eclipse.gef.Tool#deactivate()
   */
  @Override
	public void deactivate()
  {
  	super.deactivate();
  	helper = null;
  }
  
  protected Rectangle bounds;
  @Override
	protected boolean handleButtonDown(int button)
  {
    if (button == 1)
    {
	  	Point p = getLocation();
	  	getEditorCreateRequest().setLocation(p);
  		lockTargetEditPart(getTargetEditPart());
  		// Snap only when size on drop is employed
  		helper = (SnapToHelper)getTargetEditPart().getAdapter(SnapToHelper.class);
  		    		    		    		
  		bounds = new Rectangle(p.x, p.y, p.x+1, p.y+1);
  		RectangleFigure rectFigure = new RectangleFigure();
  		rectFigure.setBounds(bounds);
  		getEditorCreateRequest().setUseShape(true);
  		getEditorCreateRequest().setShape(rectFigure);
  		
  		stateTransition(STATE_INITIAL, STATE_DRAG_IN_PROGRESS);
  		setCursor(getDefaultCursor());
    }
  	return true;
  }
  
  @Override
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
  
  @Override
	public void performCreation(int button)
  {
    GeneralShape gs = EditorGeneralShapeFactory.createRectangle(bounds);
		getEditorCreateRequest().setUseShape(false);
		if (getCurrentCommand() instanceof CreateShapeCommand) {
		  CreateShapeCommand command = (CreateShapeCommand) getCurrentCommand();
		  command.setGeneralShape(gs);
		}
		
		super.performCreation(button);
  }
}
