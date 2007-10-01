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
import org.nightlabs.editor2d.command.shape.AbstractBooleanOperationCommand;
import org.nightlabs.editor2d.command.shape.ShapeSubtractCommand;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ShapeSubtractAction 
extends AbstractBooleanOperationAction 
{
	public static final String ID = ShapeSubtractAction.class.getName();
	
	public ShapeSubtractAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	public ShapeSubtractAction(AbstractEditor editor) {
		super(editor);
	}

	@Override
	protected AbstractBooleanOperationCommand getBooleanCommand(
			ShapeDrawComponent primary, ShapeDrawComponent secondary) 
	{
		return new ShapeSubtractCommand(primary, secondary);
	}

	@Override
	protected void init() 
	{
		setId(ID);
		setText(Messages.getString("org.nightlabs.editor2d.actions.shape.ShapeSubtractAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.actions.shape.ShapeSubtractAction.tooltip"));		 //$NON-NLS-1$
	}
}
