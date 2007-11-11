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
package org.nightlabs.editor2d.ui.actions.group;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ui.AbstractEditor;
import org.nightlabs.editor2d.ui.actions.AbstractEditorSelectionAction;
import org.nightlabs.editor2d.ui.command.GroupCommand;
import org.nightlabs.editor2d.ui.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class GroupAction 
extends AbstractEditorSelectionAction 
{
	public static final String ID = GroupAction.class.getName();
	
	/**
	 * @param editor
	 * @param style
	 */
	public GroupAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public GroupAction(AbstractEditor editor) {
		super(editor);
	}

	@Override
	protected void init() 
	{
		setId(ID);
		setText(Messages.getString("org.nightlabs.editor2d.ui.actions.group.GroupAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.ui.actions.group.GroupAction.tooltip")); //$NON-NLS-1$
	}

	@Override
	protected boolean calculateEnabled() {
//		return selectionContains(getDefaultIncludes(true), 2, true);
		return !selectionContains(getDefaultExcludes(true), Integer.MAX_VALUE, true) && !getSelectedObjects().isEmpty();
	}

	@Override
	public void run() 
	{
		List selectedObjects = getSelectedObjects();
		Collection<DrawComponent> selection = new HashSet<DrawComponent>();
		for (Object o : selectedObjects) {
			if (o instanceof EditPart) {
				EditPart ep = (EditPart) o;
				Object model = ep.getModel();
				if (model instanceof DrawComponent) {
					selection.add((DrawComponent)model);
				}
			}
		}
		if (!selection.isEmpty()) {
			GroupCommand cmd = new GroupCommand(selection);
			execute(cmd);
			if (cmd.getGroup() != null)
				selectEditPart(cmd.getGroup());			
		}
	}
}
