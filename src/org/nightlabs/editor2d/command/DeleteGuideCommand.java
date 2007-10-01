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

import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.EditorRuler;
import org.nightlabs.editor2d.resource.Messages;

public class DeleteGuideCommand 
extends Command 
{
  private EditorRuler parent;
  private EditorGuide guide;
  private Map<DrawComponent, Integer> oldParts;

  public DeleteGuideCommand(EditorGuide guide, EditorRuler parent) {
  	super(Messages.getString("org.nightlabs.editor2d.command,DeleteGuideCommand.label")); //$NON-NLS-1$
  	this.guide = guide;
  	this.parent = parent;
  }

  public boolean canUndo() {
  	return true;
  }

  public void execute() 
  {
  	oldParts = new HashMap<DrawComponent, Integer>(guide.getMap());
  	Iterator<DrawComponent> iter = oldParts.keySet().iterator();
  	while (iter.hasNext()) {
  		guide.detachPart(iter.next());
  	}
  	parent.removeGuide(guide);
  }
  
  public void undo() 
  {
  	parent.addGuide(guide);
  	Iterator<DrawComponent> iter = oldParts.keySet().iterator();
  	while (iter.hasNext()) {
  	  DrawComponent part = iter.next();
  		guide.attachPart(part, oldParts.get(part));
  	}
  }
  	  
}
