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

import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class OrphanChildCommand 
extends Command 
{
	private DrawComponent child = null;
	private DrawComponentContainer parent = null;	
	private int index = -1;
	
	public OrphanChildCommand(DrawComponent child) 
	{
		super();
		setLabel(Messages.getString("org.nightlabs.editor2d.command.OrphanChildCommand.label")); //$NON-NLS-1$
		this.child = child;
	}
	
	@Override
	public void execute() 
	{
		parent = child.getParent();
		index = parent.getDrawComponents().indexOf(child);
		parent.removeDrawComponent(child);
	}

	@Override
	public void redo() 
	{
		execute();
	}

	@Override
	public void undo() 
	{
		parent.addDrawComponent(child, index);
	}
}
