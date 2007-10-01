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

package org.nightlabs.editor2d.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Rectangle;
import org.nightlabs.base.ui.util.ColorUtil;
import org.nightlabs.editor2d.j2d.GeneralShape;


public class J2DUtil
extends ColorUtil
{
	public static org.eclipse.draw2d.geometry.Point toDraw2D(Point2D p) {
	  return new org.eclipse.draw2d.geometry.Point(p.getX(), p.getY());
	}
	
	public static Point2D toPoint2D(org.eclipse.draw2d.geometry.Point p) {
	  return new Point2D.Double(p.x, p.y);
	}
	
  public static Rectangle toSWTRectangle(org.eclipse.draw2d.geometry.Rectangle rect) {
    return new Rectangle(rect.x, rect.y, rect.width, rect.height);
  }
  
  public static org.eclipse.draw2d.geometry.Rectangle toDraw2D(Rectangle rect) {
    return new org.eclipse.draw2d.geometry.Rectangle(rect.x, rect.y, rect.width, rect.height);
  }
  
	public static java.awt.Rectangle toAWTRectangle(org.eclipse.draw2d.geometry.Rectangle rectangle) {
	  return new java.awt.Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}	
	
	public static org.eclipse.draw2d.geometry.Rectangle toDraw2D(Rectangle2D r2d) {
	  java.awt.Rectangle r = r2d.getBounds();
	  return new org.eclipse.draw2d.geometry.Rectangle(r.x, r.y, r.width, r.height);
	}

	public static Rectangle2D toRectangle2D(org.eclipse.draw2d.geometry.Rectangle r) {
	  return new Rectangle2D.Double(r.x, r.y, r.width, r.height);
	}
	
	public static void transformAWTGeneralShape(GeneralShape gs, 
			 java.awt.Rectangle oldBounds, 
			 java.awt.Rectangle newBounds,
			 boolean cloneGS) 
	{
	  transformGeneralShape(gs, oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height,
	      newBounds.x, newBounds.y, newBounds.width, newBounds.height, cloneGS);
	}
	
	public static void transformAWTGeneralShape(GeneralShape gs, 
			 java.awt.Rectangle oldBounds, 
			 java.awt.Rectangle newBounds) 
	{
	  transformGeneralShape(gs, oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height,
	      newBounds.x, newBounds.y, newBounds.width, newBounds.height, false);
	}	
	
	public static void transformGeneralShape(GeneralShape gs, 
	    																		 org.eclipse.draw2d.geometry.Rectangle oldBounds, 
	    																		 org.eclipse.draw2d.geometry.Rectangle newBounds,
	    																		 boolean cloneGS) 
	{
	  transformGeneralShape(gs, oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height,
	      newBounds.x, newBounds.y, newBounds.width, newBounds.height, false);
	}
		
	public static void transformGeneralShape(GeneralShape gs, 
			 org.eclipse.draw2d.geometry.Rectangle oldBounds, 
			 org.eclipse.draw2d.geometry.Rectangle newBounds) 
	{
		transformGeneralShape(gs, oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height,
		newBounds.x, newBounds.y, newBounds.width, newBounds.height, false);
	}	
	
	protected static AffineTransform at = new AffineTransform();
	
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
//	    LOGGER.debug("Both Rectangles are Equal!");
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
	    double x = (double)x1;
	    double y = (double)y1;
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

  public static AffineTransform getTranslateAffineTransform(java.awt.Rectangle oldBounds, 
      java.awt.Rectangle newBounds)
  {
    return getTranslateAffineTransform(oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height,
        newBounds.x, newBounds.y, newBounds.width, newBounds.height);
  }  
  
  /*
   * Should only be used in Combination with getScaledAffineTransform() by calling it afterwards 
   */
  public static AffineTransform getTranslateAffineTransform(int x1, int y1, int w1, int h1, 
      int x2, int y2, int w2, int h2)
  {
    // if both Rectangles are equal do nothing
    if (x1 == x2 && y1 == y2 && w1 == w2 && h1 == h2) {
//      LOGGER.debug("Both Rectangles are Equal!");
      at.setToIdentity();
      return at;
    }
          
    // if only a Translation is performed, just translate
    if (w1 == w2 && h1 == h2) 
    {
      at.setToIdentity();
    }
    // translate to origin and scale    
    else 
    {
      at.setToIdentity();
      int distanceX = x2 - x1;
      int distanceY = y2 - y1;    
      at.translate(distanceX, distanceY);
    }
    return at;
  }

  public static AffineTransform getScaleAffineTransform(java.awt.Rectangle oldBounds, 
      java.awt.Rectangle newBounds)
  {
    return getScaleAffineTransform(oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height,
        newBounds.x, newBounds.y, newBounds.width, newBounds.height);
  }  
  
  /*
   * Should only be used in Combination with getTranslateAffineTransform() by calling it first 
   */  
  public static AffineTransform getScaleAffineTransform(int x1, int y1, int w1, int h1, 
      int x2, int y2, int w2, int h2)
  {
    // if both Rectangles are equal do nothing
    if (x1 == x2 && y1 == y2 && w1 == w2 && h1 == h2) {
      at.setToIdentity();      
      return at;
    }
    
    // if only a Translation is performed, just translate
    if (w1 == w2 && h1 == h2) 
    {
      at.setToIdentity();
      at.translate(x2 - x1, y2 - y1);
    }
    // translate to origin and scale    
    else 
    {
      double ratioX = ((double)w2) / ((double)w1);
      double ratioY = ((double)h2) / ((double)h1);    
      double x = (double)x1;
      double y = (double)y1;
      double distanceX = x - (x*ratioX);
      double distanceY = y - (y*ratioY);
      at.setToIdentity();
      at.translate(distanceX, distanceY);
      at.scale(ratioX, ratioY);              
    }   
    return at;
  }
  
  /**
   * @see GeomUtil#getAffineTransform(java.awt.Rectangle, java.awt.Rectangle) 
   */
  public static AffineTransform getAffineTransform(org.eclipse.draw2d.geometry.Rectangle oldBounds,
      org.eclipse.draw2d.geometry.Rectangle newBounds)
  {
    return GeomUtil.getAffineTransform(oldBounds.x, oldBounds.y, oldBounds.width, oldBounds.height,
        newBounds.x, newBounds.y, newBounds.width, newBounds.height);
  }
  
  /**
   * @see GeomUtil#getAffineTransform(java.awt.Rectangle, java.awt.Rectangle) 
   */
  public static AffineTransform getAffineTransform(java.awt.Rectangle oldBounds,
      java.awt.Rectangle newBounds)
  {
  	return GeomUtil.getAffineTransform(oldBounds, newBounds);
  }  
  
  /**
   * converts the roattion from degrees to radians
   * 
   * @param _degrees the rotation given in degrees
   * @return the rotation given in radians
   */
  public static double calcRotationInRadians(double _degrees) 
  {
    double degreesToRotate = 0;
      	
  	if (_degrees > 360 || _degrees < -360)
  	  degreesToRotate = _degrees%360;
  	  	  	
  	return Math.toRadians(degreesToRotate);
  }  
    
  public static PointList getPathSegments(GeneralShape gs) 
  {
    PointList points = new PointList();
    if (gs != null) 
    {
      double[] coords = new double[6];
      org.eclipse.draw2d.geometry.Point p, p2, p3;
      for (PathIterator pi = gs.getPathIterator(null); !pi.isDone(); pi.next()) 
      {
        int segType = pi.currentSegment(coords);
        switch (segType) 
        {
	        case (PathIterator.SEG_MOVETO):
	        	p = new org.eclipse.draw2d.geometry.Point(coords[0], coords[1]);
	        	points.addPoint(p);
	          break;
	        case (PathIterator.SEG_LINETO):        	
	        	p = new org.eclipse.draw2d.geometry.Point(coords[0], coords[1]);
	        	points.addPoint(p);        
	          break;
	        case (PathIterator.SEG_QUADTO):
	        	p = new org.eclipse.draw2d.geometry.Point(coords[0], coords[1]);
	      		p2 = new org.eclipse.draw2d.geometry.Point(coords[2], coords[3]);
	        	points.addPoint(p);
	        	points.addPoint(p2);
	          break;
	        case (PathIterator.SEG_CUBICTO):
	        	p = new org.eclipse.draw2d.geometry.Point(coords[0], coords[1]);
		    		p2 = new org.eclipse.draw2d.geometry.Point(coords[2], coords[3]);
		    		p3 = new org.eclipse.draw2d.geometry.Point(coords[4], coords[5]);
		      	points.addPoint(p);
		      	points.addPoint(p2);
		      	points.addPoint(p3);	      	
	          break;
	        case (PathIterator.SEG_CLOSE):
	
	          break;
        }
      }      
    }  
    return points;    
  }
  
  public static Polyline toPolyline(GeneralShape gs, org.eclipse.draw2d.geometry.Rectangle newBounds) 
  {
    at.setToIdentity();
    org.eclipse.draw2d.geometry.Rectangle oldBounds = toDraw2D(gs.getBounds());
    transformGeneralShape(gs, oldBounds, newBounds, true);
    return toPolyline(gs);
  }
  
  public static Polyline toPolyline(GeneralShape gs) 
  {
    Polyline polyline = new Polyline();
    double[] coords = new double[6];
    
    for (PathIterator pi = gs.getPathIterator(new AffineTransform()); 
    			!pi.isDone(); pi.next()) 
    {
      int segType = pi.currentSegment(coords);
      switch (segType) {
      case (PathIterator.SEG_MOVETO):
        pi.currentSegment(coords);
      	polyline.addPoint(new org.eclipse.draw2d.geometry.Point(coords[0], coords[1]));
        break;
      case (PathIterator.SEG_LINETO):
        pi.currentSegment(coords);
      	polyline.addPoint(new org.eclipse.draw2d.geometry.Point(coords[0], coords[1]));
        break;
      case (PathIterator.SEG_QUADTO):
        pi.currentSegment(coords);
      	polyline.addPoint(new org.eclipse.draw2d.geometry.Point(coords[0], coords[1]));
        break;
      case (PathIterator.SEG_CUBICTO):
        pi.currentSegment(coords);
    		polyline.addPoint(new org.eclipse.draw2d.geometry.Point(coords[0], coords[1]));    	
        break;
      case (PathIterator.SEG_CLOSE):
//        pi.currentSegment(coords);
//      	polyline.addPoint(new org.eclipse.draw2d.geometry.Point(coords[0], coords[1]));    	
        break;
      }
    }
    return polyline;
  }
  
  public static GeneralShape toGeneralShape(Polyline polyline) 
  {
    PointList points = polyline.getPoints();
    GeneralShape gs = new GeneralShape(); 
    for (int i=0; i<points.size(); i++) 
    {
      org.eclipse.draw2d.geometry.Point p = points.getPoint(i);
      if (i==0)
        gs.moveTo(p.x, p.y);
      else
        gs.lineTo(p.x, p.y);
    }
    return gs;
  }
    
  public static GeneralShape removePathSegment(GeneralShape generalShape, int index) 
  {
    if (generalShape == null)
      throw new IllegalArgumentException("Param generalShape MUST not be null!"); //$NON-NLS-1$
      
    if (index > generalShape.getNumTypes()) 
      throw new IndexOutOfBoundsException("Param index is out of GeneralShape PathSegment Bounds!"); //$NON-NLS-1$

    if (index == 0)
      removeFirstPathSegment(generalShape);
    
    float[] coords = new float[6];
    int pathIndex = 0;
    GeneralShape gs = new GeneralShape();
    boolean indexSet = false;
    for (PathIterator pi = generalShape.getPathIterator(new AffineTransform()); !pi.isDone(); pi.next()) 
    {      
      if (pathIndex == index) 
      {	
        pathIndex = -1;
        indexSet = true;
        continue;
      }
                
      int segType = pi.currentSegment(coords);
      switch (segType) 
      {
	      case (PathIterator.SEG_MOVETO):
	        gs.moveTo(coords[0], coords[1]);
	      	if (!indexSet)
	      	  pathIndex++;
	        break;
	      case (PathIterator.SEG_LINETO):
	        gs.lineTo(coords[0], coords[1]);
	      	if (!indexSet)
	      	  pathIndex++;
	        break;
	      case (PathIterator.SEG_QUADTO):
	        gs.quadTo(coords[0], coords[1], coords[2], coords[3]);
	      	if (!indexSet)
	      	  pathIndex++;      
	        break;
	      case (PathIterator.SEG_CUBICTO):
	        gs.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
	      	if (!indexSet)
	      	  pathIndex++;
	        break;
	      case (PathIterator.SEG_CLOSE):
	        gs.closePath();
	      	if (!indexSet)
	      	  pathIndex++;      
	        break;
      }
    }
    return gs;
  }
  
  public static GeneralShape removeFirstPathSegment(GeneralShape generalShape)
  {
    float[] coords = new float[6];
    int pathIndex = 0;
    GeneralShape gs = new GeneralShape();
    for (PathIterator pi = generalShape.getPathIterator(null); !pi.isDone(); pi.next()) 
    {                      
      int segType = pi.currentSegment(coords);
      switch (segType) 
      {
	      case (PathIterator.SEG_MOVETO):	        
	      	pathIndex++;
	        break;
	      case (PathIterator.SEG_LINETO):
	        if (pathIndex == 1)
	          gs.moveTo(coords[0], coords[1]);
	        else
	          gs.lineTo(coords[0], coords[1]);	      
	      	pathIndex++;
	        break;
	      case (PathIterator.SEG_QUADTO):
	        gs.quadTo(coords[0], coords[1], coords[2], coords[3]);
	      	pathIndex++;
	        break;
	      case (PathIterator.SEG_CUBICTO):
	        gs.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
	      	pathIndex++;	      
	        break;
	      case (PathIterator.SEG_CLOSE):
	        pathIndex++;
	        break;
      }
    }
    return gs;    
  }
  
  public static GeneralShape addPathSegment(GeneralShape generalShape, int type, int index, float[] newCoords) 
  {
    float[] coords = new float[6];
    GeneralShape gs = new GeneralShape();
    int pathIndex = 0;
    boolean indexSet = false;
    for (PathIterator pi = generalShape.getPathIterator(null); !pi.isDone(); pi.next()) 
    {
      if (pathIndex == index) 
      {	
        switch (type) 
        {
	      	case (PathIterator.SEG_MOVETO):
	      	  gs.moveTo(newCoords[0], newCoords[1]);
	      	  break;
	      	case (PathIterator.SEG_LINETO):
	      	  gs.lineTo(newCoords[0], newCoords[1]);
	      	  break;
		      case (PathIterator.SEG_QUADTO):
		        gs.quadTo(newCoords[0], newCoords[1], newCoords[2], newCoords[3]);
		        break;
		      case (PathIterator.SEG_CUBICTO):
		        gs.curveTo(newCoords[0], newCoords[1], newCoords[2], newCoords[3], newCoords[4], newCoords[5]);
		        break;      	  
        }
        pathIndex = -1;
        indexSet = true;        
      }
      
      int segType = pi.currentSegment(coords);
      switch (segType) 
      {
	      case (PathIterator.SEG_MOVETO):
	        gs.moveTo(coords[0], coords[1]);
	      	if (!indexSet)
	      	  pathIndex++;
	        break;
	      case (PathIterator.SEG_LINETO):
	        gs.lineTo(coords[0], coords[1]);
	      	if (!indexSet)
	      	  pathIndex++;
	        break;
	      case (PathIterator.SEG_QUADTO):
	        gs.quadTo(coords[0], coords[1], coords[2], coords[3]);
	      	if (!indexSet)
	      	  pathIndex++;
	        break;
	      case (PathIterator.SEG_CUBICTO):
	        gs.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
	      	if (!indexSet)
	      	  pathIndex++;
	        break;
	      case (PathIterator.SEG_CLOSE):
	        gs.closePath();
	      	if (!indexSet)
	      	  pathIndex++;
	        break;
      }
    }
    return gs;
  }

}

