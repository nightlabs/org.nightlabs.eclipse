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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.tools.AbstractTool;
import org.eclipse.swt.widgets.Display;

import org.nightlabs.editor2d.request.EditorRequestConstants;
import org.nightlabs.editor2d.util.EditorUtil;


public class ZoomTool 
extends AbstractTool 
implements EditorRequestConstants 
{
  protected IFigure zoomRectangleFigure;
  
  protected ZoomManager zoomManager;
  
  public ZoomTool() 
  {
    setDefaultCursor(SharedCursors.CROSS);
  }
  
  protected String getCommandName() 
  {
    return REQ_ZOOM_RECT;
  }

  private void eraseZoomFeedback() 
  {
  	if (zoomRectangleFigure != null) {
  		removeFeedback(zoomRectangleFigure);
  		zoomRectangleFigure = null;
  	}
  }  
  
  /**
   * Erases feedback if necessary and puts the tool into the terminal state.
   */
  public void deactivate() 
  {
  	if (isInState(STATE_DRAG_IN_PROGRESS)) {
  		eraseZoomFeedback();
  	}
  	super.deactivate();
  	setState(STATE_TERMINAL);
  }  
  
  /**
   * @see org.eclipse.gef.tools.AbstractTool#getDebugName()
   */
  protected String getDebugName() 
  {
  	return "Zoom Tool";//$NON-NLS-1$
  }  
  
  protected IFigure getZoomFeedbackFigure() 
  {		
  	if (zoomRectangleFigure == null) {
  		zoomRectangleFigure = new ZoomRectangleFigure();
  		addFeedback(zoomRectangleFigure);
  	}
  	return zoomRectangleFigure;
  }  
  
  protected Rectangle getZoomSelectionRectangle() 
  {
  	return new Rectangle(getStartLocation(), getLocation());
  }
  
  protected ZoomManager getZoomManager() 
  {
    if (getCurrentViewer().getContents().getRoot() instanceof ScalableRootEditPart) 
    {
      if (zoomManager == null) {
        zoomManager = ((ScalableRootEditPart) getCurrentViewer().getContents().getRoot()).getZoomManager();        
      }
      return zoomManager;
    }
    else if (getCurrentViewer().getContents().getRoot() instanceof ScalableFreeformRootEditPart) 
    {
      if (zoomManager == null) {
        zoomManager = ((ScalableFreeformRootEditPart) getCurrentViewer().getContents().getRoot()).getZoomManager();        
      }
      return zoomManager;      
    }
    return null;
  }
    
  protected boolean isGraphicalViewer() 
  {
  	return getCurrentViewer() instanceof GraphicalViewer;
  }  
  
  /**
   * @see org.eclipse.gef.tools.AbstractTool#handleButtonDown(int)
   */
  protected boolean handleButtonDown(int button) 
  {
  	if (!isGraphicalViewer())
  		return true;
  	if (button != 1) {
  		setState(STATE_INVALID);
  		handleInvalidInput();
  	}
  	if (stateTransition(STATE_INITIAL, STATE_DRAG_IN_PROGRESS)) 
  	{
  	  // TODO: if shift is pressed draw symetric Rectangle
//  		if (getCurrentInput().isControlKeyDown())
//  			setSelectionMode(TOGGLE_MODE);
//  		else if (getCurrentInput().isShiftKeyDown())
//  			setSelectionMode(APPEND_MODE);
  	}
  	return true;
  }
  
  /**
   * @see org.eclipse.gef.tools.AbstractTool#handleButtonUp(int)
   */
  protected boolean handleButtonUp(int button) 
  {
  	if (stateTransition(STATE_DRAG_IN_PROGRESS, STATE_TERMINAL)) {
  		eraseZoomFeedback();
  		performZoom();
  	}
  	handleFinished();
  	return true;
  }
  
  /**
   * @see org.eclipse.gef.tools.AbstractTool#handleDragInProgress()
   */
  protected boolean handleDragInProgress() 
  {
  	if (isInState(STATE_DRAG | STATE_DRAG_IN_PROGRESS)) {
  		showZoomFeedback();
  	}
  	return true;
  }  
  
  /**
   * @see org.eclipse.gef.tools.AbstractTool#handleFocusLost()
   */
  protected boolean handleFocusLost() 
  {
  	if (isInState(STATE_DRAG | STATE_DRAG_IN_PROGRESS)) {
  		handleFinished();
  		return true;
  	}
  	return false;
  }

  /**
   * This method is called when mouse or keyboard input is invalid and erases the feedback.
   * @return <code>true</code>
   */
  protected boolean handleInvalidInput() 
  {
  	eraseZoomFeedback();
  	return true;
  }
  
  /**
   * @see org.eclipse.gef.Tool#setViewer(org.eclipse.gef.EditPartViewer)
   */
  public void setViewer(EditPartViewer viewer) 
  {
  	if (viewer == getCurrentViewer())
  		return;
  	super.setViewer(viewer);
  	if (viewer instanceof GraphicalViewer)
  		setDefaultCursor(SharedCursors.CROSS);
  	else
  		setDefaultCursor(SharedCursors.NO);
  }
    
  protected void performZoom() 
  {
//  	EditorUtil.zoomToRelativeRect(getZoomSelectionRectangle().getCopy(), getZoomManager());
  	EditorUtil.zoomToAbsoluteRect(getZoomSelectionRectangle().getCopy(), getZoomManager());  	
  }  
  
  protected void showZoomFeedback() 
  {
  	Rectangle rect = getZoomSelectionRectangle().getCopy();
  	getZoomFeedbackFigure().translateToRelative(rect);
  	getZoomFeedbackFigure().setBounds(rect);
  }
    
class ZoomRectangleFigure 
extends Figure 
{
  private int offset = 0;
  private boolean schedulePaint = true;
  private static final int DELAY = 110; //animation delay in millisecond
  
  /**
   * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
   */
  protected void paintFigure(Graphics graphics) 
  {	
  	Rectangle bounds = getBounds().getCopy();
  	graphics.translate(getLocation());
  	
  	graphics.setXORMode(true);
  	graphics.setForegroundColor(ColorConstants.white);
  	graphics.setBackgroundColor(ColorConstants.black);
  	
  	graphics.setLineStyle(Graphics.LINE_DOT);
  	
  	int[] points = new int[6];
  	
  	points[0] = 0 + offset;
  	points[1] = 0;
  	points[2] = bounds.width - 1;
  	points[3] = 0;
  	points[4] = bounds.width - 1;
  	points[5] = bounds.height - 1;
  	
  	graphics.drawPolyline(points);
  	
  	points[0] = 0;
  	points[1] = 0 + offset;
  	points[2] = 0;
  	points[3] = bounds.height - 1;
  	points[4] = bounds.width - 1;
  	points[5] = bounds.height - 1;
  	
  	graphics.drawPolyline(points);
  	
  	graphics.translate(getLocation().getNegated());
  	
  	if (schedulePaint) {
  		Display.getCurrent().timerExec(DELAY, new Runnable() {
  			public void run() {
  				offset++;
  				if (offset > 5)
  					offset = 0;	
  				
  				schedulePaint = true;
  				repaint();
  			}
  		});
  	}
  	
  	schedulePaint = false;
  }
  	
} // class ZoomRectangleFigure

}
