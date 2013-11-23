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

package org.nightlabs.editor2d.viewer.ui.util;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.j2d.GeneralShape;

public class TransformUtil
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(TransformUtil.class);
	
	private static final AffineTransform at = new AffineTransform();
	
	public static void transformGeneralShape(GeneralShape gs,
			Rectangle oldBounds, Rectangle newBounds)
	{
		transformGeneralShape(gs, oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height,
		newBounds.x, newBounds.y, newBounds.width, newBounds.height, false);
	}
	
	public static void transformGeneralShape(GeneralShape gs,
			org.eclipse.swt.graphics.Rectangle oldBounds,
			org.eclipse.swt.graphics.Rectangle newBounds)
	{
		transformGeneralShape(gs, oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height,
		newBounds.x, newBounds.y, newBounds.width, newBounds.height, false);
	}
	
	public static void transformGeneralShape(GeneralShape generalShape, int x1, int y1, int w1, int h1,
	    int x2, int y2, int w2, int h2, boolean cloneGS)
	{
	  // TODO: if cloneGS is true return the cloned GeneralShape in a seperate Method
	  // else return the transformed generalShape for convience
	  GeneralShape gs;
	  if (cloneGS) {
	    gs = (GeneralShape) generalShape.clone();
	  } else {
	    gs = generalShape;
	  }
	    	    
	  // if both Rectangles are equal do nothing
	  if (x1 == x2 && y1 == y2 && w1 == w2 && h1 == h2) {
	    logger.debug("Both Rectangles are Equal!"); //$NON-NLS-1$
	    return;
	  }
	    	  
	  // if only a Translation is performed, just translate
	  if (w1 == w2 && h1 == h2)
	  {
	    at.setToIdentity();
	    at.translate(x2 - x1, y2 - y1);
	    gs.transform(at);
	  }
	  // translate to origin and scale
	  else
	  {
		  double ratioX = ((double)w2) / ((double)w1);
		  double ratioY = ((double)h2) / ((double)h1);
	    double x = x1;
	    double y = y1;
	    double distanceX = x - (x*ratioX);
	    double distanceY = y - (y*ratioY);
	    at.setToIdentity();
	    at.translate(distanceX, distanceY);
	    at.scale(ratioX, ratioY);
	    gs.transform(at);
		  
	    // translate back
	    distanceX = x2 - x1;
	    distanceY = y2 - y1;
	    at.setToIdentity();
	    at.translate(distanceX, distanceY);
	    gs.transform(at);
	  }
	}

	/**
	 * 
	 * expands a Rectangle, the center is kept constant
	 * @param r the source Rectangle
	 * @param h the horizonal expand
	 * @param v the vertical expand
	 * @param clone determines if the source rectangle is expanded or a cloned object is returned
	 * @return the expanded Rectangle
	 */
	public static Rectangle expand(Rectangle r, int h, int v, boolean clone)
	{
		if (!clone) {
			r.x = r.x - v;
			r.width = r.width + v;
			r.y = r.y - h;
			r.height = r.y + h;
			return r;
		}
		else {
			return new Rectangle(r.x - v, r.y - h, r.width + v, r.y + h);
		}
	}
	
	/**
	 * 
	 * shrinks a Rectangle, the center is kept constant
	 * @param r the source Rectangle
	 * @param h the horizonal shrink
	 * @param v the vertical shrink
	 * @param clone determines if the source rectangle is shrinked or a cloned object is returned
	 * @return the shrinked Rectangle
	 */
	public static Rectangle shrink(Rectangle r, int h, int v, boolean clone)
	{
		if (!clone) {
			r.x = r.x + v;
			r.width = r.width - v;
			r.y = r.y + h;
			r.height = r.height - h;
			return r;
		}
		else {
			return new Rectangle(r.x + v, r.width - v, r.y + h, r.height - h);
		}
	}
	
	/**
	 * returns a rectangle given in relative coordiantes to a new rectangle in absolute coordinates
	 * 
	 * @param rect The Rectangle dimensions (x, y, width, height) divided through the given scale
	 * @param zoom the zoom/scale-factor
	 * @return a new scaled Rectangle
	 */
	public static Rectangle toAbsolute(Rectangle rect, double zoom)
	{
		Rectangle r = new Rectangle(rect);
		if (zoom != 0) {
			r.x = (int) (rect.x / zoom);
			r.y = (int) (rect.y / zoom);
			r.width = (int) (rect.width / zoom);
			r.height = (int) (rect.height / zoom);
		}
		return r;
	}

	/**
	 * returns a rectangle given in absolute coordiantes to a new rectangle in relative coordinates
	 * 
	 * @param rect The Rectangle dimensions (x, y, width, height) multiplied with the given scale
	 * @param zoom the zoom/scale-factor
	 * @return a new scaled Rectangle
	 */
	public static Rectangle toRelative(Rectangle rect, double zoom)
	{
		Rectangle r = new Rectangle(rect);
		if (zoom != 0) {
			r.x = (int) (rect.x * zoom);
			r.y = (int) (rect.y * zoom);
			r.width = (int) (rect.width * zoom);
			r.height = (int) (rect.height * zoom);
		}
		return r;
	}
	
}
