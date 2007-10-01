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

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.util.J2DUtil;

public class CreateDrawComponentCommand 
extends Command
{
	/** The DrawComponent to add */
	protected DrawComponent drawComponent;
	/** DrawComponentContainer to add to. */
	protected DrawComponentContainer parent;
	/** the DrawOrderIndex of the DrawComponentContainer */
	protected int drawOrderIndex;
	/** the bounds of the created drawComponent **/
	protected Rectangle rect;
	
	public CreateDrawComponentCommand() {
	  super(Messages.getString("org.nightlabs.editor2d.command.CreateDrawComponentCommand.label"));	   //$NON-NLS-1$
	}

	public CreateDrawComponentCommand(DrawComponent dc, DrawComponentContainer parent, int index) 
	{
	  super();
	  setLabel(Messages.getString("org.nightlabs.editor2d.command.CreateDrawComponentCommand.label")); //$NON-NLS-1$
	  this.drawComponent = dc;
	  this.parent = parent;
	  this.drawOrderIndex = index;
	}
	
	public void execute() 
	{
    drawComponent.setBounds(J2DUtil.toAWTRectangle(rect));	  
    parent.addDrawComponent(drawComponent);
		drawOrderIndex = parent.getDrawComponents().indexOf(drawComponent);		
	}	
		
	public void redo() { 
    parent.addDrawComponent(drawComponent, drawOrderIndex);    
	}
		
	public void undo() { 
    parent.removeDrawComponent(drawComponent);
	}	
	
	public void setParent(DrawComponentContainer newParent) {
		parent = newParent;
	}	
	
	public void setBounds(Rectangle r) {
		rect = r;
	}	
	protected Rectangle getBounds() {
		return rect;
	}
	
	public void setChild(DrawComponent dc) {
		drawComponent = dc;
	}	
	public DrawComponent getChild() {
	  return drawComponent;
	}
	
	public void setIndex(int index) {
	  this.drawOrderIndex = index;
	}  
}
