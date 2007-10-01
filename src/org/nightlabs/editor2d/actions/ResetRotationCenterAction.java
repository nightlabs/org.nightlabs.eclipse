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

import java.util.Iterator;

import org.eclipse.gef.EditPart;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import org.nightlabs.editor2d.request.EditorRequestConstants;
import org.nightlabs.editor2d.resource.Messages;


public class ResetRotationCenterAction 
extends AbstractEditorSelectionAction
implements EditorRequestConstants
{
  public static final String ID = ResetRotationCenterAction.class.getName();
  
  public ResetRotationCenterAction(AbstractEditor part) {
    super(part);
  }

  protected boolean calculateEnabled() 
  {
    for (Iterator it = getSelectedObjects().iterator(); it.hasNext(); ) {
      Object o = it.next();
      if (o instanceof EditPart) {
        EditPart editPart = (EditPart) o;
        if (editPart instanceof AbstractDrawComponentEditPart) {
          DrawComponent dc = ((AbstractDrawComponentEditPart) editPart).getDrawComponent();
          if (dc.getRotationX() != DrawComponent.ROTATION_X_DEFAULT || 
              dc.getRotationX() != DrawComponent.ROTATION_Y_DEFAULT) 
          {
            return true;
          }
        }        
      }       
    }
    return false;
  }

  public void run() 
  { 
    for (Iterator it = getSelectedObjects().iterator(); it.hasNext(); ) 
    {
      EditPart editPart = (EditPart) it.next();
      if (editPart instanceof AbstractDrawComponentEditPart) {
        DrawComponent dc = ((AbstractDrawComponentEditPart) editPart).getDrawComponent();
        if (dc.getRotationX() != DrawComponent.ROTATION_X_DEFAULT || 
            dc.getRotationX() != DrawComponent.ROTATION_Y_DEFAULT) 
        {
          dc.setRotationX(DrawComponent.ROTATION_X_DEFAULT);
          dc.setRotationY(DrawComponent.ROTATION_Y_DEFAULT);
          clearSelection();
          selectEditPart(dc);          
        }
      }
    }        
  }
  
  protected void init() 
  {
  	setText(Messages.getString("org.nightlabs.editor2d.actions.ResetRotationCenterAction.text")); //$NON-NLS-1$
  	setToolTipText(Messages.getString("org.nightlabs.editor2d.actions.ResetRotationCenterAction.tooltip")); //$NON-NLS-1$
  	setId(ID);
//  	setImageDescriptor(ImageDescriptor.createFromFile(EditorPlugin.class,"icons/editShape16.gif"));
  }   
}
