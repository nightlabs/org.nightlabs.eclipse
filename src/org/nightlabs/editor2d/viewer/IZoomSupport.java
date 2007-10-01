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

package org.nightlabs.editor2d.viewer;

import java.awt.Rectangle;

public interface IZoomSupport
{
	/**
	 * 
	 * @param zoomListener the IZoomListener to add
	 */
	void addZoomListener(IZoomListener zoomListener);
	
	/**
	 * 
	 * @param zoomListener the zoomListener to remove
	 */
	void removeZoomListener(IZoomListener zoomListener);
	
	/**
	 * 
	 * @param zoomFactor the zoomFactor (100% = 1.0)
	 */
	void setZoom(double zoomFactor);
	
	/**
	 * 
	 * @return the zoomFactor (100% = 1.0)
	 */
	double getZoom();	
	
	/**
	 * 
	 * @return the minimum zoomFactor
	 */
	double getMinZoom();
	
	/**
	 * 
	 * @param minZoom the minimum zoomFactor to set
	 */
	void setMinZoom(double minZoom);
	
	/**
	 *  
	 * @return the maximum zoomFactor
	 */
	double getMaxZoom();
	
	/**
	 * 
	 * @param maxZoom the maximum zoomFactor to set
	 */
	void setMaxZoom(double maxZoom);
	
	/**
	 * 
	 * @return the value of each zoomIncrement (e.g. zoomIn/zoomOut)
	 */
	double getZoomStep();
	
	/**
	 * 
	 * @param zoomStep the value of each zoomIncrement to set 
	 */
	void setZoomStep(double zoomStep);
	
	/**
	 * if the maxZoomFactor is not yet reached, the zoomFactor is incremented
	 * by one zoomStep
	 *
	 */
	void zoomIn();
	
	/**
	 * if the minZoomFactor is not yet reached, the zoomFactor is decremented
	 * by one zoomStep
	 *
	 */
	void zoomOut();
	
	/**
	 * 
	 * @return if the maxZoomFactor is not yet reached returns true, 
	 * otherwise false
	 */
	boolean canZoomIn();
	
	/**
	 * 
	 * @return if the minZoomFactor is not yet reached returns true, 
	 * otherwise false
	 */
	boolean canZoomOut();
	
	/**
	 * 
	 * @return the zoomFactor as procentual String
	 */
	String getZoomAsString();
	
	/**
	 * zooms the Viewport to the given Rectangle
	 * @param r the Rectangle to zoom to 
	 */
	void zoomTo(Rectangle r);
	
	/**
	 * 
	 * @return the viewport for the ZoomSupport
	 */
	IViewport getViewport();
	
	/**
	 * 
	 * @param viewport the viewport for the ZoomSupport 
	 */
	void setViewport(IViewport viewport);
	
	/**
	 * returns true if the zoomAllState is set and false if not
	 * @return true if the zoomAllState is set and false if not
	 */
	boolean isZoomAll();
	
	/**
	 * 
	 * @param zoomAll determines if the content of the viewport should always be zoomed,
	 * so that everything is visible
	 */
	void setZoomAll(boolean zoomAll);
	
	/**
	 * zooms all
	 *
	 */
	void zoomAll();
}
