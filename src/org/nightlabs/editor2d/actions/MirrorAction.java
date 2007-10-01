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
package org.nightlabs.editor2d.actions;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.command.CreateDrawComponentCommand;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class MirrorAction 
extends AbstractEditorSelectionAction 
{
	public static final String ID = MirrorAction.class.getName();
	
	/**
	 * @param editor
	 * @param style
	 */
	public MirrorAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public MirrorAction(AbstractEditor editor) {
		super(editor);
	}

  protected void init() 
  {
  	setText("Mirror");
  	setToolTipText("Mirrors the current selected objects");
  	setId(ID);
  } 		
	
	/**
	*@return true, if objects are selected, except the RootEditPart or LayerEditParts
	*/
	protected boolean calculateEnabled() {
		return !getDefaultSelection(false).isEmpty();
	}

	public void run() 
	{
		Collection<DrawComponent> dcs = getSelection(DrawComponent.class, true);		
		Command cmd = new CompoundCommand();
		for (Iterator<DrawComponent> it = dcs.iterator(); it.hasNext(); ) {
			DrawComponent dc = it.next();
			CreateDrawComponentCommand createCmd = new CreateDrawComponentCommand();
			DrawComponent clone = (DrawComponent) dc.clone();
//			AffineTransform at = new AffineTransform();
			// TODO: find out how to mirror with an AffineTransform
			clone.setName(clone.getName() + getCopyString());
			createCmd.setChild(clone);
			createCmd.setParent(dc.getParent());
			
			cmd.chain(createCmd);
		}
		execute(cmd);
	}
	
	protected String getCopyString() 
	{
		return " ("+"Copy"+")";
	}
}
