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

package org.nightlabs.editor2d.viewer.ui.action;

import org.eclipse.jface.action.Action;
import org.nightlabs.editor2d.viewer.ui.IZoomSupport;

public abstract class ZoomAction 
extends Action
implements IZoomAction
{
//	public ZoomAction() {
//		super();
//		init();
//	}

	public ZoomAction(IZoomSupport zoomSupport, String text, int style) {
		super(text, style);
		init();
		setZoomSupport(zoomSupport);
	}	

	public ZoomAction(IZoomSupport zoomSupport) {
		super();
		init();
		setZoomSupport(zoomSupport);
	}	
	
	protected IZoomSupport zoomSupport = null;
	public IZoomSupport getZoomSupport() {
		return zoomSupport;
	}
	public void setZoomSupport(IZoomSupport zoomSupport) 
	{
//		this.zoomSupport.removeZoomListener(this);
		this.zoomSupport = zoomSupport;
		this.zoomSupport.addZoomListener(this);
	}

	public void dispose() {
		zoomSupport.removeZoomListener(this);
	}
			
	public abstract void init();
}
