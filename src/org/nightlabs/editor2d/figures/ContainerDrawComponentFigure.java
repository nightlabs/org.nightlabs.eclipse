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

package org.nightlabs.editor2d.figures;

import java.awt.Graphics2D;
import java.util.Iterator;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;

/**
 * Overrides paint methods of DrawComponentFigures
 * and paints only its children instead.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ContainerDrawComponentFigure 
extends DrawComponentFigure 
{
	
	/**
	 * Overridden to paint only children
	 */
	public void paint(Graphics2D graphics) {
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Figure figure = (Figure) iter.next();
			if (figure instanceof DrawComponentFigure) {
				((DrawComponentFigure)figure).paint(graphics);
			}
		}
	}  
	
	/**
	 * Overridden to paint only children
	 */
	public void paint(Graphics graphics) {
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Figure figure = (Figure) iter.next();
			figure.paint(graphics);
		}
	}
	
}
