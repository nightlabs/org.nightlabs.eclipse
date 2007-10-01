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
package org.nightlabs.editor2d.outline;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.Viewport;
import org.nightlabs.editor2d.util.J2DUtil;
import org.nightlabs.editor2d.viewer.ui.IViewport;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PreviewViewport 
implements IViewport 
{
	public PreviewViewport(Viewport viewport) {
		this.viewport = viewport;
	}
	
	private Viewport viewport;
	
	public int getOffsetX() {
		return getViewBounds().x - getRealBounds().x;
	}

	public int getOffsetY() {
		return getViewBounds().y - getRealBounds().y;
	}

	// TODO maybe scale with zoom
	public Rectangle getRealBounds() {
		return J2DUtil.toAWTRectangle(viewport.getContents().getClientArea());
	}

	public Rectangle getViewBounds() {
		return J2DUtil.toAWTRectangle(viewport.getClientArea());
	}

	public Point2D getViewLocation() {
		return J2DUtil.toPoint2D(viewport.getViewLocation());
	}

//	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
//	public void addPropertyChangeListener(PropertyChangeListener pcl) {
//		pcs.addPropertyChangeListener(pcl);
//	}	
//	
//	public void removePropertyChangeListener(PropertyChangeListener pcl) {
//		pcs.removePropertyChangeListener(pcl);
//	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		viewport.addPropertyChangeListener(pcl);
	}	
	
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		viewport.removePropertyChangeListener(pcl);
	}
		
	public void setRealBounds(Rectangle realBounds) {
//		viewport.setBounds(J2DUtil.toDraw2D(realBounds));
	}

	public void setViewBounds(Rectangle viewBounds) {
		
	}

	public void setViewLocation(Point2D p) {
		viewport.setViewLocation(J2DUtil.toDraw2D(p));
	}

	public void setViewLocation(int x, int y) {
		viewport.setViewLocation(x, y);
	}

	public Rectangle getInitRealBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	public Rectangle getInitViewBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
