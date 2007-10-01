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
package org.nightlabs.editor2d.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Path;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PathShapeFigure 
extends Shape 
{

	public PathShapeFigure(Path path) {
		super();
		this.path = path;
	}

	protected Path path = null;
	public Path getPath() {
		return path;
	}
	public void setPath(Path path) {
		this.path = path;
	}
	
	/**
	 * @see org.eclipse.draw2d.Shape#fillShape(org.eclipse.draw2d.Graphics)
	 */
	protected void fillShape(Graphics graphics) {
		graphics.fillPath(path);
	}

	/**
	 * @see org.eclipse.draw2d.Shape#outlineShape(org.eclipse.draw2d.Graphics)
	 */
	protected void outlineShape(Graphics graphics) {
		graphics.drawPath(path);
	}

	protected float[] boundsArray = new float[4];
	
	public Rectangle getBounds() 
	{
		path.getBounds(boundsArray);
		bounds = createBounds(boundsArray); 
		return super.getBounds();
	}

	protected Rectangle createBounds(float[] boundsArray) 
	{
		return new Rectangle((int)boundsArray[0], (int)boundsArray[1], (int)boundsArray[2], (int)boundsArray[3]);
	}
//	public boolean containsPoint(int x, int y) {
//		return path.contains(x, y, null, true);
//	}
	
	
}
