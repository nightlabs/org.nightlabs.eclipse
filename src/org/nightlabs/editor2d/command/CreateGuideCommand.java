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

import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.EditorRuler;
import org.nightlabs.editor2d.impl.EditorGuideImpl;
import org.nightlabs.editor2d.resource.Messages;

public class CreateGuideCommand 
extends Command 
{
  private EditorGuide guide;  
  private EditorRuler parent;
  private int position;

  public CreateGuideCommand(EditorRuler parent, int position) 
  {
  	super(Messages.getString("org.nightlabs.editor2d.command.CreateGuideCommand.label")); //$NON-NLS-1$
  	this.parent = parent;
  	this.position = position;
  }

//  private Editor2DFactory factory = null;
//  public CreateGuideCommand(EditorRuler parent, int position, Editor2DFactory factory) 
//  {
//  	super("Create guide");
//  	this.parent = parent;
//  	this.position = position;
//  	this.factory = factory;
//  }
  
  public boolean canUndo() {
  	return true;
  }

  public void execute() 
  {
    if (guide == null)
//      guide = factory.createEditorGuide();
    	guide = new EditorGuideImpl();    	
    
  	guide.setPosition(position);
  	guide.setHorizontal(!parent.isHorizontal());
  	parent.addGuide(guide);  
  }

  public void undo() {
    parent.removeGuide(guide);
  }
}
