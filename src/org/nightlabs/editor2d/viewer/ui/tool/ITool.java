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

package org.nightlabs.editor2d.viewer.ui.tool;

import org.nightlabs.editor2d.viewer.ui.IDrawComponentConditional;
import org.nightlabs.editor2d.viewer.ui.IViewer;
import org.nightlabs.editor2d.viewer.ui.event.MouseEvent;

public interface ITool
{
	/**
	 * called when the Tool becomes the active Tool for the Viewer
	 * implementations can perform any necessary initialization here.
	 */
	void activate();
	
	/**
	 * called when the Tool is deactivated
	 * implementations can perform cleanup for free resources
	 */
	void deactivate();
	
	/**
	 * sets the Viewer for the Tool
	 * 
	 * @param viewer the Viewer for the Tool
	 */
	void setViewer(IViewer viewer);
	
	/**
	 * returns the Viewer of the Tool
	 * @return the Viewer for the Tool
	 */
	IViewer getViewer();
	
	/**
	 * called when the Mouse is moved
	 * @param me the MouseEvent
	 */
	void mouseMoved(MouseEvent me);
	
	/**
	 * called when the mouse is pressed
	 * @param me the MouseEvent
	 */
	void mousePressed(MouseEvent me);
	
	/**
	 * called when the mouse is released
	 * @param me the MouseEvent
	 */
	void mouseReleased(MouseEvent me);
	
	/**
	 * returns the relative X-Coordinate
	 * 
	 * @param x the X-Coordinate to convert to relative (zoom + scrollOffset)
	 * @return the converted relative X-Coordinate
	 */
	int getRelativeX(int x);
	
	/**
	 * returns the relative Y-Coordinate
	 * 
	 * @param y the Y-Coordinate to convert to relative (zoom + scrollOffset)
	 * @return the converted relative Y-Coordinate
	 */
	int getRelativeY(int y);
			
	/**
	 * sets the ID of the Tool
	 * 
	 * @param id the id of the Tool
	 */
	void setID(String id);
	
	/**
	 * returns the ID of the Tool
	 * 
	 * @return the id of the Tool
	 */
	String getID();
		
	/**
	 * sets the the {@link IDrawComponentConditional} for the Tool
	 * 
	 * @param conditional the conditional for the HitTestManager
	 * @see HitTestManager
	 * @see IDrawComponentConditional
	 */
	void setConditional(IDrawComponentConditional conditional);
	
	/**
	 * returns the {@link IDrawComponentConditional} for the Tool
	 * 
	 * @return the conditional of the HitTestManager
	 * @see HitTestManager
	 */
	IDrawComponentConditional getConditional();
	
	boolean isRepaintNeeded();
	
	void setRepaintNeeded(boolean repaint);
}
