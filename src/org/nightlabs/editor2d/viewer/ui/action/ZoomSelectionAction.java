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

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.ui.IViewer;
import org.nightlabs.editor2d.viewer.ui.IZoomSupport;
import org.nightlabs.editor2d.viewer.ui.ViewerPlugin;
import org.nightlabs.editor2d.viewer.ui.event.ISelectionChangedListener;
import org.nightlabs.editor2d.viewer.ui.event.SelectionEvent;
import org.nightlabs.editor2d.viewer.ui.resource.Messages;

public class ZoomSelectionAction
extends ZoomAction
{
	public static final String ID = ZoomSelectionAction.class.getName();
	
	public ZoomSelectionAction(IZoomSupport zoomSupport, IViewer viewer)
	{
		super(zoomSupport);
		this.viewer = viewer;
		viewer.getSelectionManager().addSelectionChangedListener(selectionListener);
	}
	
	protected IViewer viewer = null;
		
	@Override
	public void init()
	{
		setId(ID);
		setText(Messages.getString("org.nightlabs.editor2d.viewer.ui.action.ZoomSelectionAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.viewer.ui.action.ZoomSelectionAction.tooltip")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(ViewerPlugin.getDefault(), ZoomSelectionAction.class));
	}

	public void zoomChanged(double zoom) {

	}

	@Override
	public void run()
	{
		Rectangle bounds = getSelectionBounds();
		if (bounds != null) {
			getZoomSupport().setZoomAll(false);
			viewer.getZoomSupport().zoomTo(bounds);
			viewer.updateCanvas();
		}
	}
	
	protected Rectangle getSelectionBounds()
	{
		List<DrawComponent> selectedDrawComponents = viewer.getSelectionManager().getSelectedDrawComponents();
		Rectangle totalBounds = null;
		for (Iterator<DrawComponent> it = selectedDrawComponents.iterator(); it.hasNext(); ) {
			DrawComponent dc = it.next();
			Rectangle bounds = dc.getBounds();
			if (totalBounds == null)
				totalBounds = bounds;
			else
				totalBounds = totalBounds.union(bounds);
		}
		return totalBounds;
	}
	
	protected ISelectionChangedListener selectionListener = new ISelectionChangedListener()
	{
		public void selectionChanged(SelectionEvent e)
		{
			setEnabled(!e.isEmpty());
		}
	};
}
