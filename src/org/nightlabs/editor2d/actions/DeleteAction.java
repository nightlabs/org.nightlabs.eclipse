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
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.impl.LayerImpl;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class DeleteAction 
extends AbstractEditorSelectionAction 
{
	public static final String ID = ActionFactory.DELETE.getId();

	/**
	 * @param editor
	 * @param style
	 */
	public DeleteAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public DeleteAction(AbstractEditor editor) {
		super(editor);
	}

	@Override
	protected boolean calculateEnabled() 
	{
		if (getSelectedObjects().isEmpty())
			return false;

		Collection<DrawComponent> selection = getSelection(DrawComponent.class, true);

		if (onlyOnePage()) {
			if (selection.contains(getOnlyPage()))
				return false;
		}

		if (onlyOneLayer()) {
			if (selection.contains(getOnlyLayer()))
				return false;
		}

		if (selection.contains(getRootDrawComponent()))
			return false;

		return true;
	}

	protected boolean onlyOnePage() {
		return !(getRootDrawComponent().getDrawComponents().size() > 1);
	}

	protected DrawComponent getOnlyPage() {
		return getRootDrawComponent().getDrawComponents().get(0);
	}

	protected boolean onlyOneLayer() {
		return !(getRootDrawComponent().getDrawComponents(LayerImpl.class).size() > 1);
	}

	protected DrawComponent getOnlyLayer() {
		return getRootDrawComponent().getDrawComponents(LayerImpl.class).get(0);
	}

	@Override
	protected void init() 
	{
		setId(ID);
		setText(Messages.getString("org.nightlabs.editor2d.actions.DeleteAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.actions.DeleteAction.tooltip")); //$NON-NLS-1$
		setActionDefinitionId("org.eclipse.ui.edit.delete");		 //$NON-NLS-1$
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();		
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
	}

	/**
	 * Performs the delete action on the selected objects.
	 */
	public void run() {
		execute(createDeleteCommand(getSelectedObjects()));
	}
	
	/**
	 * Create a command to remove the selected objects.
	 * @param objects The objects to be deleted.
	 * @return The command to remove the selected objects.
	 */
	public Command createDeleteCommand(List objects) {
		if (objects.isEmpty())
			return null;
		if (!(objects.get(0) instanceof EditPart))
			return null;

		GroupRequest deleteReq =
			new GroupRequest(RequestConstants.REQ_DELETE);
		deleteReq.setEditParts(objects);

		CompoundCommand compoundCmd = new CompoundCommand(
			Messages.getString("org.nightlabs.editor2d.actions.DeleteAction.label")); //$NON-NLS-1$
		for (int i = 0; i < objects.size(); i++) {
			EditPart object = (EditPart) objects.get(i);
			Command cmd = object.getCommand(deleteReq);
			if (cmd != null) compoundCmd.add(cmd);
		}

		return compoundCmd;
	}
}
