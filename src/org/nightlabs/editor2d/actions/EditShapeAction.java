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

package org.nightlabs.editor2d.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.editor2d.EditorStateManager;
import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.request.EditorRequestConstants;
import org.nightlabs.editor2d.resource.Messages;


public class EditShapeAction 
extends SelectionAction 
implements EditorRequestConstants
{  
  public static final String ID = EditShapeAction.class.getName();   
  
  /**
   * @param part
   */
  public EditShapeAction(IWorkbenchPart part) {
    super(part);
  }
  
  /**
   * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#init()
   */
  protected void init() 
  {
  	super.init();
  	setText(Messages.getString("org.nightlabs.editor2d.actions.EditShapeAction.text")); //$NON-NLS-1$
  	setToolTipText(Messages.getString("org.nightlabs.editor2d.actions.EditShapeAction.tooltip")); //$NON-NLS-1$
  	setId(ID);
  	setActionDefinitionId(EditorCommandConstants.EDIT_SHAPE_ID);
//  	setImageDescriptor(ImageDescriptor.createFromFile(EditorPlugin.class,"icons/editShape16.gif"));
  }  
  
  /**
   * @see org.eclipse.jface.action.IAction#run()
   */
  public void run() 
  {
  	if (getSelectedObjects().size() == 1 && (getSelectedObjects().get(0) instanceof ShapeDrawComponentEditPart)) 
  	{    		
  		ShapeDrawComponentEditPart sdcEP = (ShapeDrawComponentEditPart) getSelectedObjects().get(0); 
  	  EditorStateManager.setEditShapeMode(sdcEP);
  	}   	          
  }
    
  /**
   * returns <code>true</code> if there is exactly 1 EditPart selected that understand
   * a request of type: {@link EditorRequestConstants#REQ_EDIT_SHAPE}.
   * @return <code>true</code> if enabled
   */
  protected boolean calculateEnabled() 
  {
    if (EditorStateManager.getCurrentState() != EditorStateManager.STATE_EDIT_SHAPE) 
    {
    	if (getSelectedObjects().size() == 1 && (getSelectedObjects().get(0) instanceof ShapeDrawComponentEditPart)) {    		
        return true;    		
    	} 
    }    
    return false;
  }
  
}
