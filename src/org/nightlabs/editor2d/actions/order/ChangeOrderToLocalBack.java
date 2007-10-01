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
package org.nightlabs.editor2d.actions.order;

import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.actions.EditorCommandConstants;
import org.nightlabs.editor2d.resource.Messages;

/**
 * changes the order of the selected Objects to the back
 * of the currentLayer  
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ChangeOrderToLocalBack 
extends AbstractChangeOrderSelectionAction 
{
	public static final String ID = ChangeOrderToLocalBack.class.getName();
	
	/**
	 * @param editor
	 * @param style
	 */
	public ChangeOrderToLocalBack(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public ChangeOrderToLocalBack(AbstractEditor editor) {
		super(editor);
	}

	public void init() 
	{
		setText(Messages.getString("org.nightlabs.editor2d.actions.order.ChangeOrderToLocalBack.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.actions.order.ChangeOrderToLocalBack.tooltip")); //$NON-NLS-1$
		setId(ID);
		setActionDefinitionId(EditorCommandConstants.ORDER_TO_LOCAL_BACK_ID);
	}	

	/** 
	 *@return the firstIndex of the drawComponents-List where
	 * the primary selected is contained in 
	 * @see AbstractChangeOrderSelectionAction#getPrimarySelectedDrawComponent()
	 *     
	 */
	public int getNewIndex() 
	{
		return 0;
	}
	
	/**
	 * @return the parent of the primary selected DrawComponent
	 * @see AbstractChangeOrderSelectionAction#getPrimarySelectedDrawComponent()
	 */
	public DrawComponentContainer getContainer() 
	{
		return primarySelected.getParent();		 		
	}
}
