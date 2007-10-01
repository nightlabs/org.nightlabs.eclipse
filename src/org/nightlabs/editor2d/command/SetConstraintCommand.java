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
import org.nightlabs.editor2d.resource.Messages;

public class SetConstraintCommand 
extends Command
{
	private DrawComponent part;
	private java.awt.Rectangle oldBounds;
	private java.awt.Rectangle newBounds;

	public void execute() 
	{
	  oldBounds = new java.awt.Rectangle(part.getBounds());  
	  part.setBounds(newBounds);	  
	}
	
	public String getLabel() 
	{
	  if ((oldBounds.getWidth() == newBounds.getWidth()) && 
	      (oldBounds.getHeight() == newBounds.getHeight()))
	    return Messages.getString("org.nightlabs.editor2d.command.SetConstraintCommand.label.changeLocation"); //$NON-NLS-1$
	  
		return Messages.getString("org.nightlabs.editor2d.command.SetConstraintCommand.label.resize"); //$NON-NLS-1$
	}	
	
	public void redo() {
	  part.setBounds(newBounds);
	}
		
	public void setPart(DrawComponent part) {
		this.part = part;
	}
		
	public void undo() {
	  part.setBounds(oldBounds);
	}
	
	public void setBounds(java.awt.Rectangle bounds) {
	  newBounds = bounds;
	}
}
