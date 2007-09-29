/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class GeomUtil
{

	public GeomUtil() {
		super();
	}

	public static Point toSWTPoint(Point2D p) {
		return new Point((int) p.getX(), (int) p.getY());
	}

	public static Point2D toPoint2D(int x, int y) {
		return new Point2D.Double(x, y);
	}

	public static Point2D toPoint2D(Point p) {
		return new Point2D.Double(p.x, p.y);
	}	
	
	public static Rectangle2D toRectangle2D(Rectangle r) {
		return new Rectangle2D.Double(r.x, r.y, r.width, r.height);
	}

	public static java.awt.Rectangle toAWTRectangle(Rectangle r) {
		return new java.awt.Rectangle(r.x, r.y, r.width, r.height);
	}
	
	public static Rectangle toSWTRectangle(Rectangle2D r2d) {
		java.awt.Rectangle r = r2d.getBounds();
		return new Rectangle(r.x, r.y, r.width, r.height);
	}
	
	public static Rectangle toSWTRectangle(java.awt.Rectangle r) {
		return new Rectangle(r.x, r.y, r.width, r.height);
	}	
	
//	/**
//	 * checks if the given source rectangle is contained in the target Rectangle,
//	 * if the source rectangle is outside of the target Rectangle, 
//	 * the returned rectangle is trimmed so that it fits in the target rectangle
//	 * 
//	 * @param source the source rectangle to check 
//	 * @param target the target rectangle to check if the source rectangle fits in
//	 * @return the a new modified rectangle that it fits in the target rectangle
//	 */
//	public static java.awt.Rectangle checkBounds(java.awt.Rectangle source, java.awt.Rectangle target) 
//	{
//		if (target.contains(source))
//			return new java.awt.Rectangle(target);
//		else 
//		{
//			java.awt.Rectangle trimmedSource = new java.awt.Rectangle(source); 			
//			// is source outter target left
//			if (trimmedSource.x < target.x) {
//				trimmedSource.x = target.x;
//				if (trimmedSource.width > target.width)
//					trimmedSource.width = target.width;
//			}
//			// is source outter target top
//			if (trimmedSource.y < target.y) {
//				trimmedSource.y = target.y;
//				if (trimmedSource.height > target.height)
//					trimmedSource.height = target.height;
//			}
//			// is source outter target right
//			if (trimmedSource.getMaxX() > target.getMaxX()) {
//				trimmedSource.x = (int)target.getMaxX() - trimmedSource.width;
//				if (trimmedSource.width > target.width)
//					trimmedSource.width = target.width;
//			}
//			// is source outter target bottom
//			if (trimmedSource.getMaxY() > target.getMaxY()) {
//				trimmedSource.y = (int) target.getMaxY() - trimmedSource.height;
//				if (trimmedSource.height > target.height)
//					trimmedSource.height = target.height; 
//			}
//			return trimmedSource;
//		}			
//	}
//		
//	/**
//	 * @param rect the Rectangle to translate to Origin (0/0) and adjust the size
//	 * @return a new Rectangle with the origin at (0/0) and newWidth = (oldWidth + oldX), 
//	 * newHeight = (oldHeight + oldY), which means that the size of the returned rectangle 
//	 * is always greater than before, if the oldOrigin wasn't already at (0/0)
//	 */
//	public static java.awt.Rectangle translateToOriginAndAdjustSize(java.awt.Rectangle rect) 
//	{
//		java.awt.Rectangle r = new java.awt.Rectangle(rect);
//		if (r.x < 0) {
//			r.width = r.width - r.x;
//			r.x = 0;
//		}
//		if (r.x > 0) {
//			r.width = r.width + r.x;
//			r.x = 0;
//		}
//		if (r.y < 0) {
//			r.height = r.height - r.y;
//			r.y = 0;
//		}
//		if (r.y > 0) {
//			r.height = r.height + r.y;
//			r.y = 0;
//		}
//		return r;
//	}		
	
	/**
	 * calculates the scaleFactor for width and height by r2 / r1 and returns it
	 * as Point2D(scaleX, scaleY)
	 * 
	 * @param r1 Rectangle r1
	 * @param r2 Rectangle r2 
	 * @return the scaleFactors scaleX and scaleY as Point2D 
	 */
	public static Point2D calcScale(java.awt.Rectangle r1, java.awt.Rectangle r2) 
	{
		double scaleX = 1.0;
		double scaleY = 1.0;
		if (r1.width != 0 && r2.width != 0)
			scaleX = (double)r2.width / (double)r1.width;
		if (r1.height != 0 && r2.height != 0)
			scaleY = (double)r2.height / (double)r1.height;
		
		return new Point2D.Double(scaleX, scaleY);
	}	
	
	public static java.awt.Rectangle scaleRect(java.awt.Rectangle rect, double scaleX, double scaleY, boolean onlyDimension) 
	{
		int x = rect.x;
		int y = rect.y;		
		int width = (int) Math.rint(rect.width * scaleX);
		int height = (int) Math.rint(rect.height * scaleY);		
		if (!onlyDimension) {
			x = (int) Math.rint(rect.x * scaleX);
			y = (int) Math.rint(rect.y * scaleY);			
		} 
		return new java.awt.Rectangle(x, y, width, height);		
	}
	
//	/**
//	 * 
//	 * @param r the Rectangle to translate to Origin (0/0)
//	 * @return a new translated rectangle
//	 */
//	public static java.awt.Rectangle translateToOrigin(java.awt.Rectangle r) 
//	{
//		java.awt.Rectangle newRect = new java.awt.Rectangle(r);
//		newRect.x = 0;
//		newRect.y = 0;
//		return newRect;
//	}			
//	
//	/**
//	 * checks if the dimension of target rectangle is contained in the source rectangle,
//	 * if the dimension target rectangle is greater it gets the dimension of the source rectangle 
//	 * 
//	 * @param source the source rectangle
//	 * @param target the target rectangle to check 
//	 * @return the (if necessary) modified target rectangle
//	 */
//	public static java.awt.Rectangle checkDimension(java.awt.Rectangle source, java.awt.Rectangle target) 
//	{
//		if (target.width > source.width)
//			target.width = source.width;
//		
//		if (target.height > source.height)
//			target.height = source.height;
//		
//		return target;
//	}
	
}
