/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
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

import java.awt.Rectangle;

import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class CloneDrawComponentCommand 
extends Command 
{
	/** 
	 * @param source the DrawComponent to clone
	 * @param parent the DrawComponentContainer to which the clone should be added
	 */
	public CloneDrawComponentCommand(DrawComponent source, DrawComponentContainer parent) 
	{
		super();
		setLabel(Messages.getString("org.nightlabs.editor2d.command.CloneDrawComponentCommand.label")); 		 //$NON-NLS-1$
		this.drawComponent = source;
		this.parent = parent;
	}

	protected DrawComponent drawComponent = null;
	public void setDrawComponent(DrawComponent dc) {
		this.drawComponent = dc;
	}
	public DrawComponent getDrawComponent() {
		return drawComponent;
	}
	
	protected DrawComponentContainer parent = null;
	public DrawComponentContainer getParent() {
		return parent;
	}
	public void setParent(DrawComponentContainer parent) {
		this.parent = parent;
	}	
	
	protected String cloneName = null;	
	/**
	 * 
	 * @return the name of the clone
	 */
	public String getCloneName() 
	{
		if (cloneName == null) {
			if (drawComponent != null) {
				cloneName = drawComponent.getName() + " " + getCopyString(); //$NON-NLS-1$
			} else {
				cloneName = getCopyString();
			}
		}
		return cloneName;
	}
	/**
	 * 
	 * @param cloneName sets the name of the cloned DrawComponent
	 */
	public void setCloneName(String cloneName) {
		this.cloneName = cloneName;
	}
	
	protected static final Rectangle DEFAULT_CLONE_BOUNDS = new Rectangle(0, 0, 10, 10);
	
	protected Rectangle cloneBounds = null;
	public Rectangle getCloneBounds() 
	{
		if (cloneBounds == null) 
		{
			if (drawComponent != null) {
				cloneBounds = new Rectangle(drawComponent.getBounds());
			}
			else {
				cloneBounds = DEFAULT_CLONE_BOUNDS;
			}
		}
		return cloneBounds;
	}
	public void setCloneBounds(Rectangle cloneBounds) {
		this.cloneBounds = cloneBounds;
	}		
	
	protected DrawComponent clone = null;	
	
	public void execute() 
	{
		clone = (DrawComponent) drawComponent.clone(getParent());
		clone.setName(getCloneName());
		clone.setBounds(getCloneBounds());
	}
		
	public void redo() 
	{
		parent.addDrawComponent(clone);
	}
	
	public void undo() 
	{
		parent.removeDrawComponent(clone);
	}
			
	protected String getCopyString() 
	{
		return "("+Messages.getString("org.nightlabs.editor2d.command.CloneDrawComponentCommand.copyText")+")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
