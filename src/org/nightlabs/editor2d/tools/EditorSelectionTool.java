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

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.tools.PanningSelectionTool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.nightlabs.editor2d.EditorStateManager;
import org.nightlabs.editor2d.request.EditorRequestConstants;


public class EditorSelectionTool 
extends PanningSelectionTool
implements EditorRequestConstants
{	
  protected static final int STATE_ROTATE = PanningSelectionTool.MAX_STATE << 1;
  protected static final int STATE_ROTATE_IN_PROGRESS = STATE_ROTATE << 1;
  protected static final int MAX_STATE = STATE_ROTATE;
 
  public EditorSelectionTool() {
    super();
  }
  
  protected boolean handleDoubleClick(int button) 
  {
    if (EditorStateManager.getCurrentState() == EditorStateManager.STATE_NORMAL_SELECTION) 
    {
      List selectedParts = getCurrentViewer().getSelectedEditParts();
      if (!selectedParts.isEmpty()) {
        stateTransition(STATE_ROTATE, STATE_INITIAL);
        EditorStateManager.setRotateMode(selectedParts);
      }
    }
    else if (EditorStateManager.getCurrentState() == EditorStateManager.STATE_ROTATE) 
    {
      List selectedParts = getCurrentViewer().getSelectedEditParts();      
      if (!selectedParts.isEmpty()) {
        stateTransition(STATE_ROTATE, STATE_INITIAL);
        EditorStateManager.setNormalSelectionMode(selectedParts);
      }        
    }
    return true;
  }
    
  protected String getCommandName() 
  {
    if (isInState(STATE_ROTATE) || isInState(STATE_ROTATE_IN_PROGRESS))
      return REQ_ROTATE;
  
    return super.getCommandName();
  }
     
  public void activate() 
  {
    EditorStateManager.setCurrentState(EditorStateManager.STATE_NORMAL_SELECTION);    
    super.activate();
  }
    
  protected boolean handleKeyDown(KeyEvent e) 
  {
    if (e.character == SWT.ESC) 
    {
      EditorStateManager.setCurrentState(EditorStateManager.STATE_NORMAL_SELECTION);            
      getCurrentViewer().deselectAll();
    }
      
    return super.handleKeyDown(e);
  }

  // TODO: find out why mouseHover reacts so slow, and how
  // the hover (also with mouseMove workaround) can be speed up
	public void mouseHover(MouseEvent me, EditPartViewer viewer) 
	{ 
		super.mouseHover(me, viewer);
//		LOGGER.debug("mouseHover!");
	}

	public void mouseMove(MouseEvent me, EditPartViewer viewer) 
	{
		super.mouseMove(me, viewer);				
		handleHover();
//		LOGGER.debug("mouseMove!");		
	}
      
}
