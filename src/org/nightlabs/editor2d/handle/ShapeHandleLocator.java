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

package org.nightlabs.editor2d.handle;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.j2d.PathSegment;
import org.nightlabs.editor2d.util.J2DUtil;


public class ShapeHandleLocator  
implements Locator
{
  protected IFigure figure;  
  protected IFigure getFigure() {
    return figure;
  }

  protected ShapeDrawComponentEditPart sdcep;
  public GeneralShape getGeneralShape() {
    return sdcep.getGeneralShape();
  }
      
  protected int index;  
  public int getIndex() {
    return index;
  }
  public void setIndex(int index) {
    this.index = index;
  }
  
  public ShapeHandleLocator(ShapeDrawComponentEditPart sdcep, int index) 
  {
    super();
    this.figure = sdcep.getFigure();
    this.sdcep = sdcep;
    this.index = index;
  }
  
  protected Point getReferencePoint(IFigure target) 
  {
    Point p = getLocation();
    getFigure().translateToAbsolute(p);
    target.translateToRelative(p);
   		
    return p;
  }
  
  protected Point getLocation() 
  {
    PathSegment ps = getGeneralShape().getPathSegment(index);
    if (ps != null) {
      return J2DUtil.toDraw2D(ps.getPoint());
    }
    return new Point();    
  }  
  
  protected IFigure getReference() {
    return figure;
  }
  
  public void relocate(IFigure target) 
  {    
    target.setLocation(calcCenterPoint(target.getBounds(), getReferencePoint(target)));        
  }
  
  protected Point calcCenterPoint(Rectangle rect, Point point) 
  {
    Dimension boundsSize = rect.getSize();
    return new Point(point.x - boundsSize.width/2, point.y - boundsSize.height/2);
  }
}
