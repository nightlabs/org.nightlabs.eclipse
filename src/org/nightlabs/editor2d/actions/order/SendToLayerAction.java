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
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class SendToLayerAction 
extends AbstractChangeOrderSelectionAction 
{
	protected static final String ID = SendToLayerAction.class.getName(); 
		
	/**
	 * @param editor
	 * @param style
	 */
	public SendToLayerAction(AbstractEditor editor, int style, Layer l) {
		super(editor, style);
		this.layer = l;
	}

	/**
	 * @param editor
	 */
	public SendToLayerAction(AbstractEditor editor, Layer l) {
		super(editor);
		this.layer = l;
	}

	protected Layer layer = null;
	
	public void init() 
	{
		setText(layer.getName());
		setToolTipText(Messages.getString("org.nightlabs.editor2d.actions.order.SendToLayerAction.tooltip")); //$NON-NLS-1$
		setId(ID+layer.getId());
	}
	
	/**
	 * @return the index of the primary selected DrawComponent
	 */
	public int getNewIndex() {		
		return indexOf(primarySelected);
	}

	/**
	 * @return the layer the selected objects should be send to 
	 */
	public DrawComponentContainer getContainer() {
		return layer;
	}
}
