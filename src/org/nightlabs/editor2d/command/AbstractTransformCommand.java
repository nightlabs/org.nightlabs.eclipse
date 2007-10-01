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

import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.resource.Messages;


public class AbstractTransformCommand 
extends Command 
{ 
  protected Map<DrawComponent, AffineTransform> dc2AffineTransform;  
  public AbstractTransformCommand() 
  {
    super();
    setLabel(Messages.getString("org.nightlabs.editor2d.command.AbstractTransformCommand.label")); //$NON-NLS-1$
  }

  protected List<EditPart> editParts;  
  public List<EditPart> getEditParts() {
    return editParts;
  }
  public void setEditParts(List<EditPart> editParts) {
    this.editParts = editParts;
  }
  
  protected AffineTransform affineTransform;    
  public AffineTransform getAffineTransform() {
    return affineTransform;
  }
  public void setAffineTransform(AffineTransform affineTransform) {
    this.affineTransform = affineTransform;
  }
  
  public void execute() 
  {
    dc2AffineTransform = new HashMap<DrawComponent, AffineTransform>(getEditParts().size());
    for (Iterator<EditPart> it = getEditParts().iterator(); it.hasNext(); ) 
    {
      EditPart editPart = it.next();
      DrawComponent dc = (DrawComponent) editPart.getModel();
      AffineTransform oldAffineTransform = dc.getAffineTransform(); 
      dc2AffineTransform.put(dc, oldAffineTransform);
      AffineTransform at = new AffineTransform(oldAffineTransform);
      at.preConcatenate(getAffineTransform());
      dc.setAffineTransform(at); 
    }    
  }
  public void redo() 
  {
    for (Iterator<DrawComponent> it = dc2AffineTransform.keySet().iterator(); it.hasNext(); ) 
    {
      DrawComponent dc = it.next(); 
      dc.setAffineTransform(getAffineTransform()); 
    }        
  }
  
  public void undo() 
  {
    for (Iterator<DrawComponent> it = dc2AffineTransform.keySet().iterator(); it.hasNext(); ) 
    {
      DrawComponent dc = it.next(); 
      AffineTransform oldAffineTransform = dc2AffineTransform.get(dc);
      dc.setAffineTransform(oldAffineTransform); 
    }     
  }
}
