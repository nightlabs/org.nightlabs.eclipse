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

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class RepaintAction 
extends AbstractEditorAction 
{
	public static final String ID = RepaintAction.class.getName();
	
	/**
	 * @param editor
	 * @param style
	 */
	public RepaintAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public RepaintAction(AbstractEditor editor) {
		super(editor);
	}

	/**
	 * @see org.nightlabs.editor2d.actions.AbstractEditorAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		return true;
	}

	protected void init() 
	{
		setId(ID);
		setText(Messages.getString("org.nightlabs.editor2d.actions.RepaintAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.actions.RepaintAction.tooltip")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), RepaintAction.class));
	}

	public void run() {
		getEditor().updateViewer();
	}
}
