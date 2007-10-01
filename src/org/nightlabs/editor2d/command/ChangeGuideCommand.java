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

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorGuide;

public class ChangeGuideCommand 
extends Command 
{
  private DrawComponent part;
  private EditorGuide oldGuide, newGuide;
  private int oldAlign, newAlign;
  private boolean horizontal;

  public ChangeGuideCommand(DrawComponent part, boolean horizontalGuide) {
  	super();
  	this.part = part;
  	horizontal = horizontalGuide;
  }

  protected void changeGuide(EditorGuide oldGuide, EditorGuide newGuide, int newAlignment) {
  	if (oldGuide != null && oldGuide != newGuide) {
  		oldGuide.detachPart(part);
  	}
  	// You need to re-attach the part even if the oldGuide and the newGuide are the same
  	// because the alignment could have changed
  	if (newGuide != null) {
  		newGuide.attachPart(part, newAlignment);
  	}
  }

  public void execute() {
  	// Cache the old values
  	oldGuide = horizontal ? part.getHorizontalGuide() : part.getVerticalGuide();		
  	if (oldGuide != null)
  		oldAlign = oldGuide.getAlignment(part);
  	
  	redo();
  }

  public void redo() {
  	changeGuide(oldGuide, newGuide, newAlign);
  }

  public void setNewGuide(EditorGuide guide, int alignment) {
  	newGuide = guide;
  	newAlign = alignment;
  }

  public void undo() {
  	changeGuide(newGuide, oldGuide, oldAlign);
  }
}
