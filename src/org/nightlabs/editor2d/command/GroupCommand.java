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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.GroupDrawComponent;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.impl.GroupDrawComponentImpl;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class GroupCommand 
extends Command 
{
	public GroupCommand(Collection<DrawComponent> drawComponents) 
	{
		if (drawComponents == null)
			throw new IllegalArgumentException("Param drawComponents must not be null!"); //$NON-NLS-1$
		
		setLabel(Messages.getString("org.nightlabs.editor2d.command.GroupCommand.label")); //$NON-NLS-1$
		this.drawComponents = drawComponents;
	}

	private Collection<DrawComponent> drawComponents = null;	
	private Map<DrawComponent, DrawComponentContainer> drawComponent2OldParent = null;		
	private Map<DrawComponent, Integer> drawComponent2OldParentIndex = null;	
	private GroupDrawComponent group = null;	
	public GroupDrawComponent getGroup() {
		return group;
	}

	@Override
	public void execute() 
	{
		if (!drawComponents.isEmpty()) 
		{
			drawComponent2OldParent = new HashMap<DrawComponent, DrawComponentContainer>();
			drawComponent2OldParentIndex = new HashMap<DrawComponent, Integer>();		
			Layer currentLayer = null;
			for (DrawComponent drawComponent : drawComponents) 
			{
				if (currentLayer == null) {
					currentLayer = drawComponent.getRoot().getCurrentLayer();
					group = new GroupDrawComponentImpl();
					group.setParent(currentLayer);
				}				
				drawComponent2OldParent.put(drawComponent, drawComponent.getParent());
				drawComponent2OldParentIndex.put(drawComponent, 
						drawComponent.getParent().getDrawComponents().indexOf(drawComponent));				
				drawComponent.getParent().removeDrawComponent(drawComponent);
			}			
			currentLayer.addDrawComponent(group);						
			group.addDrawComponents(drawComponents);			
		}
	}
	
	@Override
	public void redo() 
	{
		execute();
	}

	@Override
	public void undo() 
	{
		if (group != null) 
		{
			group.removeDrawComponents(drawComponents);
			group.getParent().removeDrawComponent(group);
			for (DrawComponent dc : drawComponents) 
			{
				DrawComponentContainer oldParent = drawComponent2OldParent.get(dc);
				int oldIndex = drawComponent2OldParentIndex.get(dc);
				oldParent.addDrawComponent(dc, oldIndex);
			}			
		}
	}
	
}
