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
package org.nightlabs.editor2d.actions.zoom;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.ZoomManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.actions.AbstractEditorAction;
import org.nightlabs.editor2d.actions.EditorCommandConstants;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.util.EditorUtil;
import org.nightlabs.editor2d.util.J2DUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ZoomPageAction 
extends AbstractEditorAction 
{
	public static final String ID = ZoomPageAction.class.getName();
	
	/**
	 * @param editor the {@link AbstractEditor}
	 * @param zoomManager the {@link ZoomManager}
	 */
	public ZoomPageAction(AbstractEditor editor, ZoomManager zoomManager) 
	{
		super(editor);
		this.zoomManager = zoomManager;
		setId(ID);
		setText(Messages.getString("org.nightlabs.editor2d.actions.zoom.ZoomPageAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.actions.zoom.ZoomPageAction.tooltip")); //$NON-NLS-1$
		setActionDefinitionId(EditorCommandConstants.ZOOM_PAGE_ID);
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				EditorPlugin.getDefault(), ZoomPageAction.class));
	}

	private ZoomManager zoomManager = null;
	
	/**
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() 
	{
		Rectangle pageBounds = J2DUtil.toDraw2D(
				getRootDrawComponent().getCurrentPage().getPageBounds());
		EditorUtil.zoomToRelativeRect(pageBounds, zoomManager);		
	}
}
