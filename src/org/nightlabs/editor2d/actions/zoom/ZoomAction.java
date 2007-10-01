/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

import org.eclipse.gef.Disposable;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;


public abstract class ZoomAction 
extends Action
implements ZoomListener, Disposable
{
	/**
	* The ZoomManager used to zoom in or out 
	*/
	protected ZoomManager zoomManager;
	
	/**
	* Constructor
	* @param text the action's text, or <code>null</code> if there is no text
	* @param image the action's image, or <code>null</code> if there is no image
	* @param zoomManager the ZoomManager used to zoom in or out
	*/
	public ZoomAction(String text, ImageDescriptor image, ZoomManager zoomManager) 
	{
		super(text, image);
		this.zoomManager = zoomManager;
		zoomManager.addZoomListener(this);
	}
	
	/**
	* @see org.eclipse.gef.Disposable#dispose()
	*/
	public void dispose() {
	  zoomManager.removeZoomListener(this);
	} 

}
