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
package org.nightlabs.editor2d.ui.actions.order;

import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.ui.AbstractEditor;
import org.nightlabs.editor2d.ui.actions.EditorCommandConstants;
import org.nightlabs.editor2d.ui.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ChangeOrderOneUp
extends AbstractChangeOrderSelectionAction
{
	public static final String ID = ChangeOrderOneUp.class.getName();
	
	/**
	 * @param editor
	 * @param style
	 */
	public ChangeOrderOneUp(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public ChangeOrderOneUp(AbstractEditor editor) {
		super(editor);
	}

	@Override
	public void init()
	{
		setText(Messages.getString("org.nightlabs.editor2d.ui.actions.order.ChangeOrderOneUp.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.ui.actions.order.ChangeOrderOneUp.tooltip")); //$NON-NLS-1$
		setId(ID);
		setActionDefinitionId(EditorCommandConstants.ORDER_ONE_UP_ID);
	}
	
	/**
	 * @return the index + 1 of the primary selected
	 * @see AbstractChangeOrderSelectionAction#getPrimarySelectedDrawComponent()
	 */
	@Override
	public int getNewIndex()
	{
		int index = primarySelected.getParent().getDrawComponents().indexOf(primarySelected);
		int highestIndex = getLastIndex(primarySelected.getParent());
		if (index < highestIndex)
			return index + 1;
		else
			return highestIndex;
	}

	/**
	 * @return the parent of the primary selected DrawComponent
	 * @see AbstractChangeOrderSelectionAction#getPrimarySelectedDrawComponent()
	 */
	@Override
	public DrawComponentContainer getContainer()
	{
		return primarySelected.getParent();
	}
}
