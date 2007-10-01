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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.gef.tools.ResizeTracker;
import org.eclipse.gef.tools.SimpleDragTracker;
import org.eclipse.gef.tools.ToolUtilities;

import org.nightlabs.editor2d.custom.EditorCursors;
import org.nightlabs.editor2d.request.EditorRequestConstants;
import org.nightlabs.editor2d.util.EditorUtil;


public abstract class AbstractDragTracker 
extends SimpleDragTracker 
implements EditorRequestConstants
{
  protected static int FLAG_TARGET_FEEDBACK = SimpleDragTracker.MAX_FLAG << 1;
  protected static final int MAX_FLAG = FLAG_TARGET_FEEDBACK;
  
  protected GraphicalEditPart owner;
  protected PrecisionRectangle sourceRect;
  protected SnapToHelper snapToHelper; 
  
  public AbstractDragTracker(GraphicalEditPart owner) 
  {
    super();
    this.owner = owner;
    setDisabledCursor(EditorCursors.NO);
  }

  public void activate() 
  {
    super.activate();
    if (owner != null) 
    {
      if (getTargetEditPart() != null)
        snapToHelper = (SnapToHelper)getTargetEditPart().getAdapter(SnapToHelper.class);
    
      IFigure figure = owner.getFigure();
      if (figure instanceof HandleBounds)
        sourceRect = new PrecisionRectangle(((HandleBounds)figure).getHandleBounds());
      else
        sourceRect = new PrecisionRectangle(figure.getBounds());
      figure.translateToAbsolute(sourceRect);
    }
  }
  
  protected List createOperationSet() 
  {
    List list = super.createOperationSet();
    ToolUtilities.filterEditPartsUnderstanding(list, getSourceRequest());
    return list;
  }
  
  /**
   * The TargetEditPart is the parent of the EditPart being dragged.
   * 
   * @return  The target EditPart; may be <code>null</code> in 2.1 applications that use
   * the now deprecated {@link ResizeTracker#ResizeTracker(int) constructor}.
   */  
  protected GraphicalEditPart getTargetEditPart() 
  {
    if (owner != null)
    {
      GraphicalEditPart targetEditPart = (GraphicalEditPart)owner.getParent();
      return targetEditPart;            
    }    
    return null;
  }
  
  protected void eraseTargetFeedback() 
  {
    if (!getFlag(FLAG_TARGET_FEEDBACK))
      return;
    if (getTargetEditPart() != null)
      getTargetEditPart().eraseTargetFeedback(getSourceRequest());
    setFlag(FLAG_TARGET_FEEDBACK, false);
  }
     
  public void deactivate() 
  {
    eraseTargetFeedback();    
    sourceRect = null;
    snapToHelper = null;
    super.deactivate();
  }  
  
  /**
   * @see org.eclipse.gef.tools.AbstractTool#getCommand()
   */
  protected Command getCommand() 
  {
    return getTargetEditPart().getCommand(getSourceRequest());
  }
  
  protected boolean handleButtonUp(int button) 
  {
    if (stateTransition(STATE_DRAG_IN_PROGRESS, STATE_TERMINAL)) {
      eraseSourceFeedback();
      eraseTargetFeedback();
      performDrag();
      // added to repaint the handle
  		performSelection();      
    }
    return true;
  }
  
  protected boolean handleDragInProgress() 
  {
    if (isInState(STATE_DRAG | STATE_DRAG_IN_PROGRESS)) 
    {
      updateSourceRequest();
      showSourceFeedback();
      showTargetFeedback();
      setCurrentCommand(getCommand());
    }
    return true;
  }
  
  protected void showTargetFeedback() 
  {
    setFlag(FLAG_TARGET_FEEDBACK, true);
    if (getTargetEditPart() != null)
      getTargetEditPart().showTargetFeedback(getSourceRequest());
  } 
  
  protected boolean isInDragInProgress() {
    return isInState(STATE_DRAG_IN_PROGRESS | STATE_ACCESSIBLE_DRAG_IN_PROGRESS);
  }   
  
  protected String getDebugName() 
  {
    return "Debug "+getCommandName(); //$NON-NLS-1$
  }   
  
  protected abstract Request createSourceRequest();
  protected abstract void updateSourceRequest();   
  protected abstract String getCommandName();

  protected void performSelection() 
  {
  	EditorUtil.selectEditPart(owner);
  }
   
}
