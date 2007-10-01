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

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public interface BufferedFreeformLayer {
	/**
	 * Should force the buffer to 
	 * be rebuild before painting the
	 * next time.
	 */
	public void refresh();

	/**
	 * Should try to update the buffer
	 * concerning a change in the given
	 * Figure.
	 */
	public void refresh(IFigure figure);

	/**
	 * Most buffered Layers will need a
	 * EditPart (or its root Control) to
	 * know about their buffer sizes etc.
	 * 
	 * @param editPart The EditPart.
	 */
	public void init(EditPart editPart);
	
	/**
	 * disposes and frees any kind of ressources which are allocated
	 * for the buffering
	 *
	 */
	public void dispose();
//	public void repaint();
}
