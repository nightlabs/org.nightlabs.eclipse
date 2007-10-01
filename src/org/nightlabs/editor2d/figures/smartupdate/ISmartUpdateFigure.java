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

package org.nightlabs.editor2d.figures.smartupdate;

import java.awt.Graphics2D;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Interface used for optimized painting.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public interface ISmartUpdateFigure {
	/**
	 * Paint region on Draw2D Graphics
	 * 
	 * @param graphics The graphics to draw on
	 * @param region The region to draw
	 */
	public void paintRegion(Graphics graphics, Rectangle region);
	/**
	 * Paint region on Swing Graphics2D
	 * 
	 * @param graphics The graphics to draw on
	 * @param region The region to draw
	 */
	public void paintRegion(Graphics2D graphics, Rectangle region);
	
	/**
	 * Rebuild the information
	 * used for optimized painting. 
	 */
	public void refresh();

	/**
	 * Rebuild the information used
	 * for optimized painting concerning
	 * the given Figure.
	 * 
	 * @param figure The Figure that has changed
	 */
	public void refresh(IFigure figure);
}
