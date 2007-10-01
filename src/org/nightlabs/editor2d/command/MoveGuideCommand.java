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

import java.util.Iterator;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.resource.Messages;

public class MoveGuideCommand 
extends Command 
{
  private int pDelta;
  private EditorGuide guide;
  	
  public MoveGuideCommand(EditorGuide guide, int positionDelta) {
  	super(Messages.getString("org.nightlabs.editor2d.command.MoveGuideCommand.label")); //$NON-NLS-1$
  	this.guide = guide;
  	pDelta = positionDelta;
  }

  public void execute() 
  {
  	guide.setPosition(guide.getPosition() + pDelta);
  	Iterator<DrawComponent> iter = guide.getMap().keySet().iterator();
  	while (iter.hasNext()) {
  		DrawComponent part = iter.next();
  		Point location = new Point(part.getX(), part.getY()).getCopy();  		  		
  		if (guide.isHorizontal()) {
  			location.y += pDelta;
  		} else {
  			location.x += pDelta;
  		}
  		part.setX(location.x);
  		part.setY(location.y);
  	}
  }

  public void undo() 
  {
  	guide.setPosition(guide.getPosition() - pDelta);
  	Iterator<DrawComponent> iter = guide.getMap().keySet().iterator();
  	while (iter.hasNext()) {
  	  DrawComponent part = iter.next();
  		Point location = new Point(part.getX(), part.getY());
  		if (guide.isHorizontal()) {
  			location.y -= pDelta;
  		} else {
  			location.x -= pDelta;
  		}
  		part.setX(location.x);
  		part.setY(location.y);
  	}
  }
}
