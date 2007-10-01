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

package org.nightlabs.editor2d.actions.zoom;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.actions.AbstractEditorSelectionAction;
import org.nightlabs.editor2d.actions.EditorCommandConstants;
import org.nightlabs.editor2d.figures.OversizedBufferFreeformLayer;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.util.EditorUtil;


public class ZoomSelectionAction  
extends AbstractEditorSelectionAction
{
  public static final String ID = ZoomSelectionAction.class.getName();  
  private final Rectangle EMPTY_RECTANGLE = new Rectangle();
  
  public ZoomSelectionAction(AbstractEditor part) {
    super(part); 
  }

  protected void init() 
  {
  	setText(Messages.getString("org.nightlabs.editor2d.actions.zoom.ZoomSelectionAction.text")); //$NON-NLS-1$
  	setToolTipText(Messages.getString("org.nightlabs.editor2d.actions.zoom.ZoomSelectionAction.tooltip")); //$NON-NLS-1$
  	setId(ID);  	
  	setImageDescriptor(SharedImages.getSharedImageDescriptor(
  			EditorPlugin.getDefault(), ZoomSelectionAction.class));
  	setActionDefinitionId(EditorCommandConstants.ZOOM_SELECTION_ID);
  } 
      
  @Override
  protected boolean calculateEnabled() 
  {
  	Collection selectedObjects = getDefaultSelection(false);  	
    if (!selectedObjects.isEmpty()) {
    	for (Iterator it = selectedObjects.iterator(); it.hasNext(); ) {
    		Object o = it.next();
    		if (o instanceof GraphicalEditPart) {
    			GraphicalEditPart editPart = (GraphicalEditPart) o;
    			IFigure f = editPart.getFigure();
    			if (!f.getBounds().equals(EMPTY_RECTANGLE) &&
    					!f.getBounds().equals(OversizedBufferFreeformLayer.INIT_BOUNDS))
    				return true;
    		}
    	}
    }        
    return false;
  }  
  
  public void run() 
  {
    Rectangle totalBounds = null;
    GraphicalEditPart editPart = null;
    for (Iterator it = getSelectedObjects().iterator(); it.hasNext(); ) {
      Object o = it.next();
      if (o instanceof GraphicalEditPart) {
      	editPart = (GraphicalEditPart) o;
        Rectangle bounds = editPart.getFigure().getBounds();
        if (totalBounds == null) {
        	totalBounds = bounds.getCopy();
        }
        totalBounds.union(bounds);
      }
    }
    if (totalBounds != null & editPart != null) {
    	ZoomManager zoomManager = EditorUtil.getZoomManager(editPart);
    	if (zoomManager != null)
      	EditorUtil.zoomToRelativeRect(totalBounds, zoomManager);
    }
  }
    
}
