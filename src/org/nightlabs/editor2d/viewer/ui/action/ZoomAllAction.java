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

import org.apache.log4j.Logger;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.editor2d.viewer.ui.IZoomSupport;
import org.nightlabs.editor2d.viewer.ui.ViewerPlugin;
import org.nightlabs.editor2d.viewer.ui.resource.Messages;

public class ZoomAllAction
extends ZoomAction
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ZoomAllAction.class);

	public static final String ID = ZoomAllAction.class.getName();
	
	public ZoomAllAction(IZoomSupport zoomSupport)
	{
		super(zoomSupport);
		this.zoom = zoomSupport.getZoom();
	}

	@Override
	public void init()
	{
		setId(ID);
		setText(Messages.getString("org.nightlabs.editor2d.viewer.ui.action.ZoomAllAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.viewer.ui.action.ZoomAllAction.tooltip")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(ViewerPlugin.getDefault(), ZoomAllAction.class));
	}

	double zoom = 1.0d;
	public void zoomChanged(double zoom) {
		this.zoom = zoom;
	}
	
	@Override
	public void run()
	{
		getZoomSupport().zoomAll();
	}
}
