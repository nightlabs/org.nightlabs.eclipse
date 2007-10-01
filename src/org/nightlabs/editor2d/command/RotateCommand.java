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
import org.nightlabs.editor2d.request.EditorRotateRequest;
import org.nightlabs.editor2d.resource.Messages;

public class RotateCommand 
extends Command 
{
  protected Map<DrawComponent, Double> dc2Rotation; 
  protected Map<DrawComponent, Point> dc2RotationCenter;
  protected EditorRotateRequest request;
  protected boolean multiple;
  protected Point rotationCenter;
  
  public RotateCommand(EditorRotateRequest request) 
  {
    super();
    setLabel(Messages.getString("org.nightlabs.editor2d.command.RotateCommand.label")); //$NON-NLS-1$
    this.request = request;
    this.multiple = request.isMultiple();
    this.rotationCenter = request.getRotationCenter();
  }

  public void execute() 
  {
    dc2Rotation = new HashMap<DrawComponent, Double>(request.getEditParts().size());
    dc2RotationCenter = new HashMap<DrawComponent, Point>(request.getEditParts().size());
    for (Iterator<EditPart> it = request.getEditParts().iterator(); it.hasNext(); ) 
    {
      EditPart editPart = it.next();
      DrawComponent dc = (DrawComponent) editPart.getModel();
      dc2Rotation.put(dc, new Double(dc.getRotation()));
      if (multiple) {
        dc2RotationCenter.put(dc, new Point(dc.getTmpRotationX(), dc.getTmpRotationY()));
      } else {
        dc2RotationCenter.put(dc, new Point(dc.getRotationX(), dc.getRotationY()));        
      }
      double realRotation = rotation + dc.getRotation();
      if (multiple) {
        dc.setTmpRotationX(rotationCenter.x);
        dc.setTmpRotationY(rotationCenter.y);
      } else {
        dc.setRotationX(rotationCenter.x);
        dc.setRotationY(rotationCenter.y);
      }
      dc.setRotation(realRotation); 
      dc.setTmpRotationX(DrawComponent.ROTATION_X_DEFAULT);
      dc.setTmpRotationY(DrawComponent.ROTATION_Y_DEFAULT);
    }
  }   
  
  public void redo() 
  {
    for (Iterator<DrawComponent> it = dc2Rotation.keySet().iterator(); it.hasNext(); ) 
    {
      DrawComponent dc = it.next();
      if (multiple) {
        dc.setTmpRotationX(rotationCenter.x);
        dc.setTmpRotationY(rotationCenter.y);
      } else {
        dc.setRotationX(rotationCenter.x);
        dc.setRotationY(rotationCenter.y);
      }
      double realRotation = rotation + dc.getRotation();
      dc.setRotation(realRotation);
      dc.setTmpRotationX(DrawComponent.ROTATION_X_DEFAULT);
      dc.setTmpRotationY(DrawComponent.ROTATION_Y_DEFAULT);      
    }    
  }

  public void undo() 
  {
    for (Iterator<DrawComponent> it = dc2Rotation.keySet().iterator(); it.hasNext(); ) 
    {
      DrawComponent dc = it.next();
      double oldRotation = dc2Rotation.get(dc);
      Point oldRotationCenter = dc2RotationCenter.get(dc);
      if (multiple) {
        dc.setTmpRotationX(rotationCenter.x);
        dc.setTmpRotationY(rotationCenter.y);                
      } else {
        dc.setRotationX(oldRotationCenter.x);
        dc.setRotationY(oldRotationCenter.y);
      }      
      dc.setRotation(oldRotation);
      dc.setTmpRotationX(DrawComponent.ROTATION_X_DEFAULT);
      dc.setTmpRotationY(DrawComponent.ROTATION_Y_DEFAULT);      
    }
  }

  protected double rotation;
  public double getRotation() {
    return rotation;
  }
  public void setRotation(double rotation) {
    this.rotation = rotation;
  }
}
