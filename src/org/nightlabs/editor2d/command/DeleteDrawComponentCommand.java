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
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.resource.Messages;

public class DeleteDrawComponentCommand 
extends Command
{
	/** The DrawComponent to delete */
	protected DrawComponent child;
	
	/** parent to remove from. */
	protected DrawComponentContainer parent;
  
	/** the index of the DrawComponentContainer */
	protected int index;
	
	/** True, if child was removed from its parent. */	
	protected boolean wasRemoved;
  
	/**
	 * Create a command that will remove the shape from its parent.
	 * @param parent the ShapesDiagram containing the child
	 * @param child    the Shape to remove
	 * @throws IllegalArgumentException if any parameter is null
	 */
	public DeleteDrawComponentCommand(DrawComponentContainer parent, DrawComponent child) 	
	{
		if (parent == null || child == null) {
			throw new IllegalArgumentException("Neither param parent not param child may be null!"); //$NON-NLS-1$
		}
		setLabel(Messages.getString("org.nightlabs.editor2d.command,DeleteDrawComponentCommand.label")); //$NON-NLS-1$
		this.parent = parent;
		this.child = child;
	}	
	
	public boolean canUndo() {
		return wasRemoved;
	}
	
	public void execute() 
	{
	  index = parent.getDrawComponents().indexOf(child);
	  if (index == -1)
	  	throw new IllegalStateException("DrawComponent "+child.getId()+" is not contained in DrawComponentContainer "+parent.getId()); //$NON-NLS-1$ //$NON-NLS-2$
    parent.removeDrawComponent(child);	  
    wasRemoved = true;
	}
	
	public void redo() 
	{	  
//    parent.removeDrawComponent(index);
		parent.removeDrawComponent(child);		
	}
	
	public void undo() 
	{	  
    parent.addDrawComponent(child, index);    
	}
}
