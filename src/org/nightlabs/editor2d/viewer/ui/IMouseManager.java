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

import java.awt.Point;

import org.nightlabs.editor2d.viewer.ui.event.IMouseChangedListener;
import org.nightlabs.editor2d.viewer.ui.event.MouseListener;
import org.nightlabs.editor2d.viewer.ui.event.MouseMoveListener;

public interface IMouseManager
{
	/**
	 * 
	 * @return the relative X-Position of the Mouse,
	 * based on the zoomFactor
	 * @see IZoomSupport
	 */
	int getRelativeX();
	
	/**
	 * 
	 * @return the relative Y-Position of the Mouse
	 * based on the zoomFactor
	 * @see IZoomSupport
	 * 
	 */
	int getRelativeY();
	
	/**
	 * 
	 * @return the relative Point
	 */
	Point getRelativePoint();
	
	/**
	 * 
	 * @return the absolute X-Position of the Mouse
	 */
	int getAbsoluteX();
	
	/**
	 * 
	 * @return the absolute Y-Position of the Mouse
	 */
	int getAbsoluteY();
	
	/**
	 * 
	 * @return the Absolute Point
	 */
	Point getAbsolutePoint();
	
//	/**
//	 *
//	 * @return the IZoomListener of the MouseManager
//	 */
//	IZoomListener getZoomListener();
	
	/**
	 * adds an IMouseChangedListener which will be notified of
	 * all mouseMovements
	 * @param l the IMouseChangedListener to add
	 * 
	 */
	void addMouseChangedListener(IMouseChangedListener l);
	
	/**
	 * 
	 * @param l the IMouseChangedListener to remove
	 */
	void removeMouseChangedListener(IMouseChangedListener l);
	
	/**
	 * 
	 * @param l the MouseListener to add
	 * @see MouseListener
	 */
	void addMouseListener(MouseListener l);
	
	/**
	 * 
	 * @param l the MouseListener to remove
	 * @see MouseListener
	 */
	void removeMouseListener(MouseListener l);
	
	/**
	 * 
	 * @param l the MouseMoveListener to add
	 * @see MouseMoveListener
	 */
	void addMouseMoveListener(MouseMoveListener l);
	
	/**
	 * 
	 * @param l the MouseMoveListener to remove
	 * @see MouseMoveListener
	 */
	void removeMouseMoveListener(MouseMoveListener l);
	
//	/**
//	 * called when the Mouse is moved
//	 * @param me the MouseEvent
//	 */
//	void mouseMoved(MouseEvent me);
//
//	/**
//	 * called when the mouse is pressed
//	 * @param me the MouseEvent
//	 */
//	void mousePressed(MouseEvent me);
//
//	/**
//	 * called when the mouse is released
//	 * @param me the MouseEvent
//	 */
//	void mouseReleased(MouseEvent me);
}
