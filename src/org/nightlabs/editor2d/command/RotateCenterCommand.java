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

package org.nightlabs.editor2d.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.request.EditorRotateCenterRequest;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.util.EditorUtil;


public class RotateCenterCommand 
extends Command 
{
  protected Map<DrawComponent, Point> dc2RotationCenter;
  protected EditorRotateCenterRequest request;
  
  protected Point rotationCenter;    
  public Point getRotationCenter() {
    return rotationCenter;
  }
  public void setRotationCenter(Point rotationCenter) {
    this.rotationCenter = rotationCenter;
  }
  
  protected boolean multiple = false;    
  public boolean isMultiple() {
    return multiple;
  }
  public void setMultiple(boolean multiple) {
    this.multiple = multiple;
  }
  
  public RotateCenterCommand(EditorRotateCenterRequest request) 
  {
    super();
    this.request = request;
    this.multiple = request.isMultiple();
    setLabel(Messages.getString("org.nightlabs.editor2d.command.RotateCenterCommand.label")); //$NON-NLS-1$
  }

  public void execute() 
  {
    dc2RotationCenter = new HashMap<DrawComponent, Point>(request.getEditParts().size());
    for (Iterator<EditPart> it = request.getEditParts().iterator(); it.hasNext(); ) 
    {
      EditPart editPart = it.next();
      DrawComponent dc = (DrawComponent) editPart.getModel();
      Point oldRotationCenter = new Point(dc.getRotationX(), dc.getRotationY());
      dc2RotationCenter.put(dc, oldRotationCenter);
      if (isMultiple()) {
        dc.setTmpRotationX(rotationCenter.x);
        dc.setTmpRotationY(rotationCenter.y);
      } else {
        dc.setRotationX(rotationCenter.x); 
        dc.setRotationY(rotationCenter.y);        
      }      
    }
    EditorUtil.selectEditParts(request.getEditParts());
  } 
  
  public void redo() 
  {
    for (Iterator<DrawComponent> it = dc2RotationCenter.keySet().iterator(); it.hasNext(); ) 
    {
      DrawComponent dc = it.next();
      if (isMultiple()) {
        dc.setTmpRotationX(rotationCenter.x);
        dc.setTmpRotationY(rotationCenter.y);
      } else {
        dc.setRotationX(rotationCenter.x); 
        dc.setRotationY(rotationCenter.y);        
      }      
    }
    EditorUtil.selectEditParts(request.getEditParts());    
  }
  
  public void undo() 
  {
    for (Iterator<DrawComponent> it = dc2RotationCenter.keySet().iterator(); it.hasNext(); ) 
    {
      DrawComponent dc = it.next();
      Point oldRotationCenter = (Point) dc2RotationCenter.get(dc);
      if (isMultiple()) {
        dc.setTmpRotationX(DrawComponent.ROTATION_X_DEFAULT);
        dc.setTmpRotationY(DrawComponent.ROTATION_Y_DEFAULT);
      } else {
        dc.setRotationX(oldRotationCenter.x);
        dc.setRotationY(oldRotationCenter.y);
      }       
    }
    EditorUtil.selectEditParts(request.getEditParts());    
  }   
}
