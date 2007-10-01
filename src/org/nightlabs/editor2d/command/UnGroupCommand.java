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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.GroupDrawComponent;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class UnGroupCommand 
extends Command 
{
	public UnGroupCommand(GroupDrawComponent group) 
	{
		if (group == null)
			throw new IllegalArgumentException("Param group must not be null!"); //$NON-NLS-1$
		
		setLabel(Messages.getString("org.nightlabs.editor2d.command.UnGroupCommand.label")); //$NON-NLS-1$
		this.group = group;
	}

	private GroupDrawComponent group = null;
	private DrawComponentContainer groupParent = null;
	private Collection<DrawComponent> groupedDcs = null;
	public Collection<DrawComponent> getGroupedDrawComponents() {
		return groupedDcs;
	}
	
	@Override
	public void execute() 
	{
		groupParent = group.getParent();
		groupedDcs = new ArrayList<DrawComponent>(group.getDrawComponents());
		group.removeDrawComponents(groupedDcs);
		groupParent.addDrawComponents(groupedDcs);
		groupParent.removeDrawComponent(group);
	}
	
	@Override
	public void redo() {
		execute();
	}
	
	@Override
	public void undo() 
	{
		groupParent.removeDrawComponents(groupedDcs);
		group.addDrawComponents(groupedDcs);
		groupParent.addDrawComponent(group);
	}
}
