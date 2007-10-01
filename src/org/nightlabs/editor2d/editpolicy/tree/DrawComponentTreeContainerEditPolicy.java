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

package org.nightlabs.editor2d.editpolicy.tree;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.TreeContainerEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.command.DrawComponentReorderCommand;
import org.nightlabs.editor2d.command.OrphanChildCommand;
import org.nightlabs.editor2d.resource.Messages;


public class DrawComponentTreeContainerEditPolicy 
extends TreeContainerEditPolicy 
{
	private static final Logger logger = Logger.getLogger(DrawComponentTreeContainerEditPolicy.class);
	
	protected Command getAddCommand(ChangeBoundsRequest request)
	{
		if (logger.isDebugEnabled())
			logger.debug("getAddCommand()"); //$NON-NLS-1$
		
		CompoundCommand command = new CompoundCommand();
		command.setLabel("Add Children in Tree"); //$NON-NLS-1$
		List editparts = request.getEditParts();
		int index = findIndexOfTreeItemAt(request.getLocation());
				
		for(int i = 0; i < editparts.size(); i++) 
		{
		  EditPart child = (EditPart)editparts.get(i);
		  boolean mayAdd = mayAdd(child, getHost());
		  logger.debug("mayAdd = "+mayAdd); //$NON-NLS-1$
			if(!mayAdd)
			  command.add(UnexecutableCommand.INSTANCE);
			else 
			{
				if (index == -1)
					index = 0;
				
				logger.debug("index = "+index); //$NON-NLS-1$
				command.add(new DrawComponentReorderCommand(
						(DrawComponent)child.getModel(), 
						(DrawComponentContainer)getHost().getModel(), 
						index));
			}
		}
		return command;
	}

	protected Command getCreateCommand(CreateRequest request)
	{
		if (logger.isDebugEnabled())
			logger.debug("getCreateCommand()");		 //$NON-NLS-1$
		return null;
	}

	protected Command getMoveChildrenCommand(ChangeBoundsRequest request)
	{
		if (logger.isDebugEnabled())
			logger.debug("getMoveChildrenCommand()"); //$NON-NLS-1$
		
		CompoundCommand command = new CompoundCommand();
		List editparts = request.getEditParts();
		List children = getHost().getChildren();
		int newIndex = findIndexOfTreeItemAt(request.getLocation());
		
		for(int i = 0; i < editparts.size(); i++)
		{
			EditPart child = (EditPart)editparts.get(i);
			int tempIndex = newIndex;
			int oldIndex = children.indexOf(child);
			if(oldIndex == tempIndex || oldIndex + 1 == tempIndex) {
			  command.add(UnexecutableCommand.INSTANCE);
			  return command;
			} else if(oldIndex <= tempIndex) {
			  tempIndex--;
			}
			logger.debug("index = "+tempIndex); //$NON-NLS-1$
			if (tempIndex != -1) 
			{
				command.add(new DrawComponentReorderCommand(
						(DrawComponent)child.getModel(), 
						(DrawComponentContainer)getHost().getModel(), 
						tempIndex));				
			} 
			else {
			  command.add(UnexecutableCommand.INSTANCE);
			  return command;				
			}				
		}
		command.setLabel(Messages.getString("org.nightlabs.editor2d.editpolicy.treeDrawComponentTreeContainerEditPolicy.command.label.moveChildren")); //$NON-NLS-1$
		return command;
	}

	protected boolean mayAdd(EditPart source, EditPart target) 
	{
		if (source.getModel() instanceof Layer) 
		{
			if (target.getModel() instanceof PageDrawComponent)
				return true;			
			return false;				
		}
		else if (source.getModel() instanceof PageDrawComponent) 
		{
			if (target.getModel() instanceof RootDrawComponent)
				return true;						
			return false;				
		}
		else if (target.getModel() instanceof RootDrawComponent) 
		{
			if (source.getModel() instanceof PageDrawComponent)
				return true;						
			return false;				
		}		
		else if ( (!(source.getModel() instanceof DrawComponentContainer)) && 
				target.getModel() instanceof DrawComponentContainer) 
		{
			DrawComponentContainer container = (DrawComponentContainer) target.getModel();			
			if (container instanceof PageDrawComponent)
				return false;
			
			return true;
		}
		return true;
	}

	protected Command getOrphanChildrenCommand(GroupRequest request) 
	{
		if (logger.isDebugEnabled())
			logger.debug("getOrphanChildrenCommand()"); //$NON-NLS-1$
		
		List<EditPart> parts = request.getEditParts();
		CompoundCommand result = 
			new CompoundCommand(Messages.getString("org.nightlabs.editor2d.editpolicy.treeDrawComponentTreeContainerEditPolicy.command.label.orphan.children")); //$NON-NLS-1$
		for (int i = 0; i < parts.size(); i++) {
			DrawComponent child = (DrawComponent)((EditPart)parts.get(i)).getModel();
			OrphanChildCommand orphan = new OrphanChildCommand(child);  		
			result.add(orphan);
		}		
		return result.unwrap();
	}
		
}
