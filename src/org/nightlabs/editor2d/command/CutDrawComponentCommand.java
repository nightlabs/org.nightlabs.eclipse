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
import java.util.Iterator;
import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class CutDrawComponentCommand 
extends Command 
{
	public CutDrawComponentCommand(Collection<DrawComponent> dcs) 
	{
		super();
		setLabel(Messages.getString("org.nightlabs.editor2d.command.CutDrawComponentCommand.label")); //$NON-NLS-1$
		this.dcs = dcs;
	}

	protected Collection<DrawComponent> dcs = null;
	public Collection<DrawComponent> getDrawComponents() {
		return dcs;
	}
	
	protected Map<DrawComponent, Integer> dc2Index = new HashMap<DrawComponent, Integer>();
	
	public void execute() 
	{
		Clipboard clipboard = Clipboard.getDefault();
		clipboard.setContents(dcs);
		for (Iterator<DrawComponent> it = dcs.iterator(); it.hasNext(); ) 
		{
			DrawComponent dc = it.next();
			int index = dc.getParent().getDrawComponents().indexOf(dc);
			dc2Index.put(dc, new Integer(index));
			dc.getParent().removeDrawComponent(index);
		}
	}

	public void redo() 
	{
		execute();
	}

	public void undo() 
	{
		for (Iterator<DrawComponent> it = dcs.iterator(); it.hasNext(); ) 
		{
			DrawComponent dc = it.next();
			Integer dcIndex = dc2Index.get(dc);
			if (dcIndex != null) {
				dc.getParent().addDrawComponent(dc, dcIndex.intValue());
			}
		}
	}
	
}
