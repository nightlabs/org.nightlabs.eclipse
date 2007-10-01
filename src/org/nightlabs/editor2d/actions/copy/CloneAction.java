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
package org.nightlabs.editor2d.actions.copy;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.gef.commands.CompoundCommand;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.actions.AbstractEditorSelectionAction;
import org.nightlabs.editor2d.command.CloneDrawComponentCommand;
import org.nightlabs.editor2d.config.QuickOptionsConfigModule;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class CloneAction  
extends AbstractEditorSelectionAction
{
	public static final String ID = CloneAction.class.getName();
	
	/**
	 * @param part
	 * @param style
	 */
	public CloneAction(AbstractEditor part, int style) {
		super(part, style);
	}

	/**
	 * @param part
	 */
	public CloneAction(AbstractEditor part) {
		super(part);
	}

  protected void init() 
  {
  	setText(Messages.getString("org.nightlabs.editor2d.actions.copy.CloneAction.text")); //$NON-NLS-1$
  	setToolTipText(Messages.getString("org.nightlabs.editor2d.actions.copy.CloneAction.tooltip")); //$NON-NLS-1$
  	setId(ID);
//  	setImageDescriptor(ImageDescriptor.createFromFile(EditorPlugin.class,"icons/editShape16.gif"));
  	initConfigModule();
  } 	
	
	protected QuickOptionsConfigModule confMod = null;
	protected QuickOptionsConfigModule getConfigModule() 
	{
		if (confMod == null)
			initConfigModule();
		
		return confMod;
	}
	
	protected void initConfigModule() 
	{
		try {
			confMod = (QuickOptionsConfigModule) Config.sharedInstance().createConfigModule(QuickOptionsConfigModule.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		} 
	}  
  
	/**
	*@return true, if objects are selected, except the RootEditPart or LayerEditParts
	*/
	protected boolean calculateEnabled() {
		return !getDefaultSelection(false).isEmpty();
	}
		
	/**
	 * clones all selected DrawComponents and combines them to one Command
	 *  
	 */
	public void run() 
	{
		Collection<DrawComponent> dcs = getSelection(DrawComponent.class, true);		
		CompoundCommand cmd = new CompoundCommand();
		for (Iterator<DrawComponent> it = dcs.iterator(); it.hasNext(); ) 
		{
			DrawComponent dc = it.next();
			CloneDrawComponentCommand cloneCmd = new CloneDrawComponentCommand(dc, dc.getParent());
			int distX = getConfigModule().getCloneDistanceX();
			int distY = getConfigModule().getCloneDistanceY();
			if (distX != 0 || distY != 0) {
				Rectangle dcBounds = new Rectangle(dc.getBounds());
				dcBounds.setLocation(dcBounds.x + distX, dcBounds.y + distY);
				cloneCmd.setCloneBounds(dcBounds);			 				
			}
			cmd.add(cloneCmd);
		}
		execute(cmd);
	}	
	
}
