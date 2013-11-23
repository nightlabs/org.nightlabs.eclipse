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
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.viewer.ui.IDrawComponentConditional;
import org.nightlabs.editor2d.viewer.ui.IZoomSupport;

public class ViewerUtil
{
//	private static final Logger logger = Logger.getLogger(ViewerUtil.class);
	private static int hitTolerance = 3;
	
	/**
	 * 
	 * @param dc the DrawComponent to iterate through
	 * @param x the x-Coordinate
	 * @param y the y-Coordinate
	 * @return the topmost DrawComponent in the Z-Order which contains x and y or null if
	 * no drawComponent is found
	 */
	public static DrawComponent findObjectAt(DrawComponent dc, int x, int y)
	{
		if (dc instanceof DrawComponentContainer)
		{
			if (dc instanceof Layer)
			{
				Layer layer = (Layer) dc;
				if (!layer.isVisible())
					return null;
			}
			DrawComponentContainer container = (DrawComponentContainer) dc;
			int size = container.getDrawComponents().size();
			if (container.getBounds().contains(x, y) && size != 0)
			{
				for (int i = size - 1; i >= 0; i--) {
					DrawComponent child = container.getDrawComponents().get(i);
					if (findObjectAt(child, x, y) != null) {
						return findObjectAt(child, x, y);
					}
				}
			}
			return null;
		}
		if (dc instanceof ShapeDrawComponent) {
			ShapeDrawComponent sdc = (ShapeDrawComponent) dc;
			//  TODO: calculate outlineArea for all not filled shapes once at initialization
			return contains(sdc, x, y) ? sdc : null;
		}
		else {
			return dc.getBounds().contains(x, y) ? dc : null;
		}
	}
	 
	/**
	 * 
	 * @param dc the DrawComponent to iterate through (if it is a DrawComponentContainer)
	 * @param x the x-Coordinate
	 * @param y the y-Coordinate
	 * @param conditional an optional (maybe null) IDrawComponentConditional to filter the returned Objects
	 * @param excludeList an optional (maybe null) Collection of excluded DrawComponents
	 * @return a List which contains all DrawComponents which contain x and y and fulfill
	 * the condition as well as are not included in the excludeList
	 * if no drawComponents are found an empty List is returned
	 */
	public static List<DrawComponent> findObjectsAt(DrawComponent dc, int x, int y,
			IDrawComponentConditional conditional, Collection<DrawComponent> excludeList)
	{
		List<DrawComponent> objects = findObjectsAt(dc, x, y);
		for (Iterator<DrawComponent> it = objects.iterator(); it.hasNext(); )
		{
			DrawComponent drawComponent = it.next();
			if (conditional != null) {
				if (!conditional.evalute(drawComponent))
					it.remove();
			}
			if (excludeList != null) {
				if (excludeList.contains(drawComponent))
					it.remove();
			}
		}
		return objects;
	}

	/**
	 * 
	 * @param dc the DrawComponent to iterate through
	 * @param x the x-Coordinate
	 * @param y the y-Coordinate
	 * @param conditional an optional (maybe null) IDrawComponentConditional to filter the returned Objects
	 * @param excludeList an optional (maybe null) Collection of excluded DrawComponents
	 * @return the topmost DrawComponent in the Z-Order which contains x and y and
	 * fulfills the condition and is not included in the excludeList
	 */
	public static DrawComponent findObjectAt(DrawComponent dc, int x, int y,
			IDrawComponentConditional conditional, Collection<DrawComponent> excludeList)
	{
		List<DrawComponent> objects = findObjectsAt(dc, x, y, conditional, excludeList);
		return !objects.isEmpty() ? (DrawComponent) objects.get(0) : null;
	}
	
	/**
	 * the order of the returned List represents the Z-Order of the hit-testing
	 * (first entry = topmost, last entry = bottommost)
	 * 
	 * 
	 * @param dc the DrawComponent to iterate through
	 * @param x the x-Coordinate
	 * @param y the y-Coordinate
	 * @return a List which contains all DrawComponents which contain x and y,
	 * if no drawComponents are found an empty List is returned
	 * 
	 */
	public static List<DrawComponent> findObjectsAt(DrawComponent dc, int x, int y)
	{
		List<DrawComponent> l = new LinkedList<DrawComponent>();
		if (dc instanceof DrawComponentContainer)
		{
			if (dc instanceof Layer)
			{
				Layer layer = (Layer) dc;
				if (!layer.isVisible())
					return l;
			}
			DrawComponentContainer container = (DrawComponentContainer) dc;
			int size = container.getDrawComponents().size();
			if (container.getBounds().contains(x, y) && size != 0)
			{
				for (int i = size - 1; i >= 0; i--)
				{
					DrawComponent child = container.getDrawComponents().get(i);
					List<DrawComponent> childrenObjects = findObjectsAt(child, x, y);
					if (!childrenObjects.isEmpty()) {
						l.addAll(childrenObjects);
					}
				}
			}
		}
		else {
			if (dc instanceof ShapeDrawComponent) {
				ShapeDrawComponent sdc = (ShapeDrawComponent) dc;
				//	TODO: calculate outlineArea for all not filled shapes once at initialization
				if (contains(sdc, x, y))
					l.add(sdc);
			}
			else if (dc.getBounds().contains(x, y))
			 l.add(dc);
		}
		return l;
	}
	
	/**
	 * 
	 * @param sdc the ShapeDrawComponent to check for containment
	 * @param x the x-Coordinate
	 * @param y the y-Coordinate
	 * @return true if the ShapeDrawComponent contains x,y or false if not
	 */
	public static boolean contains(ShapeDrawComponent sdc, double x, double y)
	{
		if (sdc.isFill())
			return sdc.getGeneralShape().contains(x, y);
		else {
      Rectangle outerBounds = TransformUtil.expand(sdc.getBounds(), hitTolerance, hitTolerance, true);
      Rectangle innerBounds = TransformUtil.shrink(sdc.getBounds(), hitTolerance, hitTolerance, true);
      GeneralShape outerGS = (GeneralShape) sdc.getGeneralShape().clone();
      GeneralShape innerGS = (GeneralShape) sdc.getGeneralShape().clone();
      TransformUtil.transformGeneralShape(outerGS, sdc.getBounds(), outerBounds);
      TransformUtil.transformGeneralShape(innerGS, sdc.getBounds(), innerBounds);
      Area outlineArea = new Area(outerGS);
      Area innerArea = new Area(innerGS);
      outlineArea.exclusiveOr(innerArea);
      return outlineArea.contains(x,y);
		}
	}
	
	/**
	 * 
	 * @param sdc the ShapeDrawComponent to check for intersection
	 * @param r the Rectangle2D to check for
	 * @return true if the ShapeDrawComponent intersects r
	 */
	public static boolean intersects(ShapeDrawComponent sdc, Rectangle2D r)
	{
		return sdc.getGeneralShape().intersects(r);
	}
	
	/**
	 * 
	 * @param dc the DrawComponent to iterate through (if it is a DrawComponentContainer)
	 * @param r the Rectangle to check for intersection
	 * @return a List of DrawCompoennts which intersects the Rectangle
	 */
	public static List<DrawComponent> findObjectsAt(DrawComponent dc, Rectangle2D r)
	{
		List<DrawComponent> l = new LinkedList<DrawComponent>();
		if (dc instanceof DrawComponentContainer)
		{
			if (dc instanceof Layer)
			{
				Layer layer = (Layer) dc;
				if (!layer.isVisible())
					return l;
			}
			DrawComponentContainer container = (DrawComponentContainer) dc;
			int size = container.getDrawComponents().size();
			if (container.getBounds().intersects(r) && size != 0)
			{
				for (int i = size - 1; i >= 0; i--)
				{
					DrawComponent child = container.getDrawComponents().get(i);
					List<DrawComponent> childrenObjects = findObjectsAt(child, r);
					if (!childrenObjects.isEmpty()) {
						l.addAll(childrenObjects);
					}
				}
			}
		}
		else {
			if (dc instanceof ShapeDrawComponent) {
				ShapeDrawComponent sdc = (ShapeDrawComponent) dc;
				if (intersects(sdc, r))
					l.add(sdc);
			}
			else if (dc.getBounds().intersects(r))
			 l.add(dc);
		}
		return l;
	}
	
	/**
	 * @param dc the DrawComponent to iterate through (if it is a DrawComponentContainer)
	 * @param r the Rectangle to check for intersection
	 * @param conditional an optional (maybe null) IDrawComponentConditional to filter the returned Objects
	 * @param excludeList an optional (maybe null) Collection of excluded DrawComponents
	 * @return a List which contains all DrawComponents which intersect r and fulfill
	 * the condition as well as are not included in the excludeList
	 * if no drawComponents are found an empty List is returned
	 */
	public static List<DrawComponent> findObjectsAt(DrawComponent dc, Rectangle2D r,
			IDrawComponentConditional conditional, Collection<DrawComponent> excludeList)
	{
		List<DrawComponent> objects = findObjectsAt(dc, r);
		for (Iterator<DrawComponent> it = objects.iterator(); it.hasNext(); )
		{
			DrawComponent drawComponent = it.next();
			if (conditional != null) {
				if (!conditional.evalute(drawComponent))
					it.remove();
			}
			if (excludeList != null) {
				if (excludeList.contains(drawComponent))
					it.remove();
			}
		}
		return objects;
	}
	
//	public static void zoomAll(IZoomSupport zoomSupport, double zoom)
//	{
//		Rectangle bounds = zoomSupport.getViewport().getRealBounds();
//		Rectangle absoluteRealBounds = TransformUtil.toAbsolute(bounds, zoom);
//		zoomSupport.zoomTo(absoluteRealBounds);
//	}
		
//	public static void zoomAll(IZoomSupport zoomSupport)
//	{
//		Rectangle realBounds = zoomSupport.getViewport().getRealBounds();
//		Rectangle viewBounds = zoomSupport.getViewport().getViewBounds();
//		double scaleX = viewBounds.getWidth() / realBounds.getWidth();
//		double scaleY = viewBounds.getHeight() / realBounds.getHeight();
//		double scale = Math.min(scaleX, scaleY);
//		zoomSupport.setZoom(scale);
//
//		if (logger.isDebugEnabled()) {
//			logger.debug("realBounds = "+realBounds);
//			logger.debug("viewBounds = "+viewBounds);
//			logger.debug("scale = "+scale);
//		}
//	}
	
	public static void zoomAll(Rectangle realBounds, Rectangle viewBounds, IZoomSupport zoomSupport)
	{
		double scaleX = viewBounds.getWidth() / realBounds.getWidth();
		double scaleY = viewBounds.getHeight() / realBounds.getHeight();
		double scale = Math.min(scaleX, scaleY);
		zoomSupport.setZoom(scale);
		
//		if (logger.isDebugEnabled()) {
//			logger.debug("realBounds = "+realBounds);
//			logger.debug("viewBounds = "+viewBounds);
//			logger.debug("scale = "+scale);
//		}
	}
}
