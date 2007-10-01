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
package org.nightlabs.editor2d.actions.shape;

import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.actions.AbstractEditorSelectionAction;
import org.nightlabs.editor2d.command.shape.AbstractBooleanOperationCommand;
import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public abstract class AbstractBooleanOperationAction 
extends AbstractEditorSelectionAction 
{

	/**
	 * @param editor
	 * @param style
	 */
	public AbstractBooleanOperationAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public AbstractBooleanOperationAction(AbstractEditor editor) {
		super(editor);
	}

	protected abstract void init();
	
	@Override
	protected boolean calculateEnabled() 
	{
		if (selectionContains(ShapeDrawComponent.class, 2, true) && getSelectedObjects().size() == 2)
		{
			if (getPrimarySelectedShape().getBounds().intersects(getSecondarySelectedShape().getBounds()))
				return true;			
		}		
		return false;
	}

	protected ShapeDrawComponent getPrimarySelectedShape() 
	{
		ShapeDrawComponentEditPart sep = (ShapeDrawComponentEditPart) getPrimarySelected();
		return sep.getShapeDrawComponent();
	}
	
	protected ShapeDrawComponent getSecondarySelectedShape() 
	{
		ShapeDrawComponentEditPart sep = (ShapeDrawComponentEditPart) getSelectedObjects().get(1);
		return sep.getShapeDrawComponent();
	}
	
	protected abstract AbstractBooleanOperationCommand getBooleanCommand(
			ShapeDrawComponent primary, ShapeDrawComponent secondary);

	@Override
	public void run() 
	{
		execute(getBooleanCommand(getPrimarySelectedShape(), getSecondarySelectedShape()));
	}
		
}
