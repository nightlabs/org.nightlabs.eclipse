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

import org.eclipse.draw2d.IFigure;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.Renderer;


public interface RendererFigure 
extends IFigure
{
	/**
	 * sets the {@link Renderer} for the figure
	 * 
	 * @param renderer the Renderer to set
	 */
  public void setRenderer(Renderer renderer);
  
  /**
   * sets the drawComponent to paint
   * 
   * @param drawComponent the drawComponent to paint by the figure
   */
  public void setDrawComponent(DrawComponent drawComponent);
  
  /**
   * paints the drawComponent with the given renderer on the graphics2D
   * 
   * @param graphics the Graphics2D to paint on
   */
  public void paint(Graphics2D graphics);  
}
