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

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.viewer.ui.util.TransformUtil;

public class HitTestManager
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(HitTestManager.class);
	
	public HitTestManager(DrawComponent dc)
	{
		this.dc = dc;
		init(dc);
	}
	
	protected void init(DrawComponent dc)
	{
		long startTime = System.currentTimeMillis();
		initBounds(dc);
		initShapes(dc);
		long endTime = System.currentTimeMillis() - startTime;
		logger.debug("Initialzation took "+endTime+" ms");		 //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private DrawComponent dc = null;
	public DrawComponent getDrawComponent() {
		return dc;
	}
	public void setDrawComponent(DrawComponent dc) {
		this.dc = dc;
		init(dc);
	}
	
	private double hitTolerance = 3;
	public void setHitTolerance(double hitTolerance) {
		this.hitTolerance = hitTolerance;
		initShapes(dc);
	}
	public double getHitTolerance() {
		return hitTolerance;
	}
	
//	protected IDrawComponentConditional conditional = null;
//	public IDrawComponentConditional getConditional() {
//		return conditional;
//	}
//	public void setConitional(IDrawComponentConditional conditional) {
//		this.conditional = conditional;
//	}
//
//	protected Collection<DrawComponent> excludeList = null;
//	public void setExcludeList(Collection<DrawComponent> excludeList) {
//		this.excludeList = excludeList;
//	}
//	public Collection<DrawComponent> getExcludeList() {
//		return excludeList;
//	}
	
	protected void initBounds(DrawComponent dc)
	{
		if (dc instanceof DrawComponentContainer) {
			DrawComponentContainer container = (DrawComponentContainer) dc;
			for (Iterator it = container.getDrawComponents().iterator(); it.hasNext(); ) {
				DrawComponent drawComponent = (DrawComponent) it.next();
				initBounds(drawComponent);
			}
		}
		else
			dc.getBounds();
	}
	
	private Map<ShapeDrawComponent, Area> unfilledShape2Area = new HashMap<ShapeDrawComponent, Area>();
	protected void initShapes(DrawComponent dc)
	{
//		unfilledShape2Area.clear();
		if (dc instanceof DrawComponentContainer) {
			DrawComponentContainer container = (DrawComponentContainer) dc;
			for (Iterator it = container.getDrawComponents().iterator(); it.hasNext(); ) {
				DrawComponent drawComponent = (DrawComponent) it.next();
				initShapes(drawComponent);
			}
		}
		else if (dc instanceof ShapeDrawComponent) {
			ShapeDrawComponent sdc = (ShapeDrawComponent) dc;
			if (!sdc.isFill()) {
				Area outlineArea = calculateOutlineArea(sdc, hitTolerance);
				if (outlineArea != null)
					unfilledShape2Area.put(sdc, outlineArea);
			}
		}
	}
	
//	protected Area calculateOutlineArea(ShapeDrawComponent sdc, double hitTolerance)
//	{
//    Rectangle outerBounds = TransformUtil.expand(sdc.getBounds(), (int)hitTolerance, (int)hitTolerance, true);
//    Rectangle innerBounds = TransformUtil.shrink(sdc.getBounds(), (int)hitTolerance, (int)hitTolerance, true);
//    GeneralShape outerGS = (GeneralShape) sdc.getGeneralShape().clone();
//    GeneralShape innerGS = (GeneralShape) sdc.getGeneralShape().clone();
//    TransformUtil.transformGeneralShape(outerGS, sdc.getBounds(), outerBounds);
//    TransformUtil.transformGeneralShape(innerGS, sdc.getBounds(), innerBounds);
//    Area outlineArea = new Area(outerGS);
//    Area innerArea = new Area(innerGS);
//    outlineArea.exclusiveOr(innerArea);
//    return outlineArea;
//	}

	protected Area calculateOutlineArea(ShapeDrawComponent sdc, double hitTolerance)
	{
    if (sdc.getGeneralShape() != null) {
	    Rectangle outerBounds = TransformUtil.expand(sdc.getBounds(), (int)hitTolerance, (int)hitTolerance, true);
	    Rectangle innerBounds = TransformUtil.shrink(sdc.getBounds(), (int)hitTolerance, (int)hitTolerance, true);
      GeneralShape outerGS = (GeneralShape) sdc.getGeneralShape().clone();
      GeneralShape innerGS = (GeneralShape) sdc.getGeneralShape().clone();
      TransformUtil.transformGeneralShape(outerGS, sdc.getBounds(), outerBounds);
      TransformUtil.transformGeneralShape(innerGS, sdc.getBounds(), innerBounds);
      Area outlineArea = new Area(outerGS);
      Area innerArea = new Area(innerGS);
      outlineArea.exclusiveOr(innerArea);
      return outlineArea;
    }
    return null;
	}
		
	/**
	 * 
	 * @param sdc the ShapeDrawComponent to check for containment
	 * @param x the x-Coordinate
	 * @param y the y-Coordinate
	 * @return true if the ShapeDrawComponent contains x,y or false if not
	 */
	public boolean contains(ShapeDrawComponent sdc, double x, double y)
	{
		if (sdc.isFill())
			return sdc.getGeneralShape().contains(x, y);
		else {
			Area outlineArea = unfilledShape2Area.get(sdc);
			if (outlineArea == null) {
				logger.debug("outlineArea for "+sdc.getName()+" not preCalculated"); //$NON-NLS-1$ //$NON-NLS-2$
				outlineArea = calculateOutlineArea(sdc, hitTolerance);
			}
			return outlineArea.contains(x, y);
		}
	}
	
	/**
	 * 
	 * @param dc the DrawComponent to iterate through
	 * @param x the x-Coordinate
	 * @param y the y-Coordinate
	 * @return the topmost DrawComponent in the Z-Order which contains x and y or null if
	 * no drawComponent is found
	 */
	public DrawComponent findObjectAt(DrawComponent dc, int x, int y)
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
	 * @param excludeList an optional (mybe null) Collection of excluded DrawComponents
	 * @return a List which contains all DrawComponents which contain x and y and fullfill
	 * the condition as well as are not included in the excludeList
	 * if no drawComponents are found an empty List is returned
	 */
	public List findObjectsAt(DrawComponent dc, int x, int y,
			IDrawComponentConditional conditional, Collection excludeList)
	{
		List objects = findObjectsAt(dc, x, y);
		for (Iterator it = objects.iterator(); it.hasNext(); )
		{
			DrawComponent drawComponent = (DrawComponent) it.next();
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
	 * @param excludeList an optional (mybe null) Collection of excluded DrawComponents
	 * @return the topmost DrawComponent in the Z-Order which contains x and y and
	 * fullfills the condition and is not included in the excludeList
	 */
	public DrawComponent findObjectAt(DrawComponent dc, int x, int y,
			IDrawComponentConditional conditional, Collection excludeList)
	{
		List objects = findObjectsAt(dc, x, y, conditional, excludeList);
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
	public List findObjectsAt(DrawComponent dc, int x, int y)
	{
		List l = new LinkedList();
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
					List childrenObjects = findObjectsAt(child, x, y);
					if (!childrenObjects.isEmpty()) {
						l.addAll(childrenObjects);
					}
				}
			}
		}
		else {
			if (dc instanceof ShapeDrawComponent) {
				ShapeDrawComponent sdc = (ShapeDrawComponent) dc;
				//	TODO: calculate outlineArea for all not filled shapes once at initalization
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
	 * @param sdc the ShapeDrawComponent to check for intersection
	 * @param r the Rectangle2D to check for
	 * @return true if the ShapeDrawComponent intersects r
	 */
	public boolean intersects(ShapeDrawComponent sdc, Rectangle2D r)
	{
		return sdc.getGeneralShape().intersects(r);
	}
	
	/**
	 * 
	 * @param dc the DrawComponent to iterate through (if it is a DrawComponentContainer)
	 * @param r the Rectangle to check for intersection
	 * @return a List of DrawCompoennts which intersects the Rectangle
	 */
	public List findObjectsAt(DrawComponent dc, Rectangle2D r)
	{
		List l = new LinkedList();
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
					List childrenObjects = findObjectsAt(child, r);
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
	 * @param r the Rectangel to check for intersection
	 * @param conditional an optional (maybe null) IDrawComponentConditional to filter the returned Objects
	 * @param excludeList an optional (maybe null) Collection of excluded DrawComponents
	 * @return a List which contains all DrawComponents which intersect r and fullfill
	 * the condition as well as are not included in the excludeList
	 * if no drawComponents are found an empty List is returned
	 */
	public List findObjectsAt(DrawComponent dc, Rectangle2D r,
			IDrawComponentConditional conditional, Collection excludeList)
	{
		List objects = findObjectsAt(dc, r);
		for (Iterator it = objects.iterator(); it.hasNext(); )
		{
			DrawComponent drawComponent = (DrawComponent) it.next();
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
}
