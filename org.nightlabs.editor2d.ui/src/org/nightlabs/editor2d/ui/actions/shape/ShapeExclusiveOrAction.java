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
package org.nightlabs.editor2d.ui.actions.shape;

import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.ui.AbstractEditor;
import org.nightlabs.editor2d.ui.command.shape.AbstractBooleanOperationCommand;
import org.nightlabs.editor2d.ui.command.shape.ShapeExclusiveOrCommand;
import org.nightlabs.editor2d.ui.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ShapeExclusiveOrAction
extends AbstractBooleanOperationAction
{
	public static final String ID = ShapeExclusiveOrAction.class.getName();
	
	public ShapeExclusiveOrAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	public ShapeExclusiveOrAction(AbstractEditor editor) {
		super(editor);
	}

	@Override
	protected AbstractBooleanOperationCommand getBooleanCommand(
			ShapeDrawComponent primary, ShapeDrawComponent secondary)
	{
		return new ShapeExclusiveOrCommand(primary, secondary);
	}

	@Override
	protected void init()
	{
		setId(ID);
		setText(Messages.getString("org.nightlabs.editor2d.ui.actions.shape.ShapeExclusiveOrAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.ui.actions.shape.ShapeExclusiveOrAction.tooltip")); //$NON-NLS-1$
	}
}
