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

import org.eclipse.swt.graphics.Color;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.RenderModeManager;

public interface IViewer
{
	/**
	 * 
	 * @return the drawComponent to draw
	 */
	DrawComponent getDrawComponent();
	
	/**
	 * 
	 * @param dc the DrawComponent to draw
	 */
	void setDrawComponent(DrawComponent dc);
	
	/**
	 * refreshes/repaints the Canvas
	 *
	 */
	void updateCanvas();
	
	/**
	 * 
	 * @return the Background Color of the Viewer
	 */
	Color getBgColor();
	
	/**
	 * 
	 * @param bgColor the Background Color of the Viewer to set
	 */
	void setBgColor(Color bgColor);
			
	/**
	 * 
	 * @param zoomFactor the zoomFactor of the viewer (100% = 1.0)
	 */
	void setZoom(double zoomFactor);
	
	/**
	 * 
	 * @return the zoomFactor of the viewer (100% = 1.0)
	 */
	double getZoom();
	
	/**
	 * 
	 * @return the IZoomSupport of the viewer
	 * @see org.nightlabs.editor2d.viewer.ui.IZoomSupport
	 */
	IZoomSupport getZoomSupport();
	
	/**
	 * 
	 * @return the RenderModeManager which manages the visual representation
	 * (renderers) for the given DrawComponent
	 * @see RenderModeManager
	 */
	RenderModeManager getRenderModeManager();
	
	/**
	 * 
	 * @return the viewport which displays the DrawComponent
	 */
	IViewport getViewport();
	
	/**
	 * 
	 * @return the BufferedCanvas of the Viewer
	 * (in most cases this is the viewport itself)
	 */
	IBufferedCanvas getBufferedCanvas();
	
//	IBufferedViewport getViewport();
		
	/**
	 * 
	 * @return the implementation of IMouseManager, which is responsible for
	 * calculating the right Mouse-Coordinates
	 * @see IMouseManager
	 */
	IMouseManager getMouseManager();
	
	/**
	* 
	* @return the SelectionManager which manages the selection of the viewer
	* @see SelectionManager
	*/
	SelectionManager getSelectionManager();
	
	/**
	 * 
	 * @return the HitTestManager which manages hitTesting
	 * @see HitTestManager
	 */
	HitTestManager getHitTestManager();
	
	/**
	 * releases ressources of the viewer
	 */
	void dispose();
	
//	/**
//	 * Returns the {@link IToolManager} for the Viewer
//	 * @return the {@link IToolManager} for the Viewer
//	 */
//	IToolManager getToolManager();
//	
//	/**
//	 * Sets the {@link IToolManager} for the Viewer
//	 * @param toolManager the {@link IToolManager} to set
//	 */
//	void setToolManager(IToolManager toolManager);
}
