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

package org.nightlabs.editor2d.viewer.ui;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;

public interface IViewport 
{
	public static final String VIEW_CHANGE = "viewChange";	 //$NON-NLS-1$
	public static final String REAL_CHANGE = "realChange"; //$NON-NLS-1$
//	public static final String BUFFER_CHANGE = "bufferChange";	
	
	/**
	 * @param realBounds the bounds of the whole area of the viewport
	 */
	void setRealBounds(Rectangle realBounds);
	
	/**
	 * 
	 * @return the bounds of the whole area of the viewport
	 */
	Rectangle getRealBounds();
	
	/**
	 * 
	 * @param viewBounds sets the bounds of the visible area of the viewport
	 */
	void setViewBounds(Rectangle viewBounds);
	
	/**
	 * 
	 * @return the bounds of the visible area of the viewport
	 */
	Rectangle getViewBounds();
	
	/**
	 * sets the upper left corner of visible area of the viewport
	 * @param p the upper left view location
	 */
	void setViewLocation(Point2D p);
	
	/**
	 * sets the upper left corner of visible area of the viewport
	 * @param x the X-Coordinate
	 * @param y the Y-Coordinate
	 */
	void setViewLocation(int x, int y);
	
	/**
	 * 
	 * @return the viewLocation
	 */
	Point2D getViewLocation();
			
	/**
	 * @return the offset in X-direction between the viewBounds and the realBounds 
	 */
	int getOffsetX();
		
	/**
	 * @return the offset in Y-direction between the viewBounds and the realBounds 
	 */
	int getOffsetY();
	
	/**
	 * 
	 * @param pcl the PropertyChangeListener which will be notified of 
	 * changes of the realBounds or the viewBounds, the corresponding 
	 * propertyName is either <code>VIEW_CHANGE</code> or <code>REAL_CHANGE</code> 
	 */
	void addPropertyChangeListener(PropertyChangeListener pcl);
	
	/**
	 * 
	 * @param pcl the propertyChangeListener to remove
	 */
	void removePropertyChangeListener(PropertyChangeListener pcl);	
	
	Rectangle getInitRealBounds();
	
	Rectangle getInitViewBounds();
}
