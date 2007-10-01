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

import java.awt.Font;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.nightlabs.math.MathUtil;

public class EditorUtil 
{
  public EditorUtil() {
    super();
  }
  
  public static double calcRotation(Point mousePoint, Point centerPoint) {
    return MathUtil.calcRotation(mousePoint.x, mousePoint.y, centerPoint.x, centerPoint.y);
  }
  
  public static Point toAbsolute(GraphicalEditPart part, Point point) 
  {
    Point p = point.getCopy(); 
    part.getFigure().translateToAbsolute(p);
		p.translate(getScrollOffset(part));
		return p;
  }  

  public static Rectangle oldToAbsolute(GraphicalEditPart part, Rectangle rect) 
  {
    Rectangle r = rect.getCopy(); 
    part.getFigure().translateToAbsolute(r);
		r.translate(getScrollOffset(part));
		return r;
  }  
   
//  public static Point toRelative(GraphicalEditPart part, Point point) 
//  {
//    Point p = point.getCopy();
//    part.getFigure().translateToRelative(p);
//    p.translate(getScrollOffset(part));
//    return p;
//  }

//  public static Rectangle toRelative(GraphicalEditPart part, Rectangle rect) 
//  {
//    Rectangle r = rect.getCopy();
//    part.getFigure().translateToRelative(r);
//    r.translate(getScrollOffset(part));
//    return r;
//  }
  
  public static Point getScrollOffset(EditPart part) 
  {
    EditPartViewer viewer = part.getRoot().getViewer();    
    FigureCanvas canvas = (FigureCanvas) viewer.getControl();
    Viewport viewport = canvas.getViewport();
    Point viewLocation = viewport.getViewLocation(); 
    return viewLocation;
  }  
        
  public static Point getZoomedScrollOffset(EditPart part) 
  {
  	Point absoluteScrollOffset = getScrollOffset(part);
  	double zoom = getZoom(part);
  	double x = absoluteScrollOffset.x;
  	double y = absoluteScrollOffset.y;
  	if (absoluteScrollOffset.x != 0)
  		x = absoluteScrollOffset.x / zoom;
  	if (absoluteScrollOffset.y != 0)
  		y = absoluteScrollOffset.y / zoom;
  	return new Point(Math.rint(x), Math.rint(y));
  }
  
  public static Rectangle toAbsolute(EditPart part, Rectangle rect) 
  {
    Point xy = EditorUtil.toAbsolute(part, rect.x, rect.y);
    Point wh = EditorUtil.toAbsolute(part, rect.width, rect.height);
    Rectangle absoluteRect = new Rectangle(xy.x, xy.y, wh.x, wh.y);
    return absoluteRect;
  }
    
  public static Rectangle toAbsoluteWithScrollOffset(EditPart part, Rectangle rect) 
  {
    Point xy = EditorUtil.toAbsolute(part, rect.x, rect.y);
    Point wh = EditorUtil.toAbsolute(part, rect.width, rect.height);
    Rectangle absoluteRect = new Rectangle(xy.x, xy.y, wh.x, wh.y);
    absoluteRect.translate(getScrollOffset(part));
    return absoluteRect;
  }
    
  public static Point toAbsolute(EditPart part, double x, double y) 
  {
    double zoom = getZoom(part);
    double newX = x / zoom;
    double newY = y / zoom;
    Point p = new Point(newX, newY);
    return p;
  }
       
  public static Point toAbsoluteWithScrollOffset(EditPart part, double x, double y) 
  {
    double zoom = getZoom(part);
    double newX = x / zoom;
    double newY = y / zoom;
    Point p = new Point(newX, newY);
//    p.translate(getScrollOffset(part));
    p.translate(getZoomedScrollOffset(part));    
    return p;
  }  
  
  public static Point toRelative(EditPart part, double x, double y) 
  {
    double zoom = getZoom(part);
    double newX = x * zoom;
    double newY = y * zoom;
    Point p = new Point(newX, newY);
    return p;
  }
    
  public static int toRelative(EditPart part, double dimension) {
  	return (int)(dimension * getZoom(part));
  }
    
  public static double getZoom(EditPart part) 
  {
  	if (part != null && part.getRoot() != null) {
      if (part.getRoot() instanceof ScalableFreeformRootEditPart) {
        ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) part.getRoot();
        return root.getZoomManager().getZoom();
      }  		
  	}
    return 1.0;
  }
  
  public static ZoomManager getZoomManager(EditPart part) 
  {
    if (part.getRoot() instanceof ScalableFreeformRootEditPart) {
      return ((ScalableFreeformRootEditPart) part.getRoot()).getZoomManager();
    }
    return null;
  }    
  
  public static Point getCenter(List editParts) 
  {
    Rectangle totalBounds = new Rectangle();
    int counter = 0;
    for (Iterator it = editParts.iterator(); it.hasNext(); ) {
      GraphicalEditPart editPart = (GraphicalEditPart) it.next();
      Rectangle figureBounds = editPart.getFigure().getBounds();
      if (counter == 0)
        totalBounds = figureBounds.getCopy();      
      totalBounds = totalBounds.getUnion(figureBounds);
      counter++;
    }
    return totalBounds.getCenter();  
  }
  
  public static void selectEditParts(List editParts) 
  { 
    if (editParts == null)
      throw new IllegalArgumentException("Param editParts must not be null!"); //$NON-NLS-1$
    
    EditPartViewer viewer = null;
    if (!editParts.isEmpty()) {
      Object o = editParts.get(0);
      if (o instanceof EditPart) {
        EditPart editPart = (EditPart) o;
        viewer = editPart.getViewer();        
      }
    }
    if (viewer != null) {
      StructuredSelection selection = new StructuredSelection(editParts);
      viewer.deselectAll();
      viewer.setSelection(selection);          
    }    
  }
  
  public static void selectEditPart(EditPart editPart) 
  { 
    if (editPart == null)
      throw new IllegalArgumentException("Param editPart must not be null!"); //$NON-NLS-1$
    
    EditPartViewer viewer = editPart.getViewer();        
    if (viewer != null) {
      StructuredSelection selection = new StructuredSelection(editPart);
      viewer.deselectAll();
      viewer.setSelection(selection);          
    }    
  }  
    
  public static final Font DEFAULT_FONT = new Font(null, Font.PLAIN, 24);
    
  public static void zoomToRelativeRect(Rectangle rect, ZoomManager zoomManager) 
  {
  	if (rect == null)
			throw new IllegalArgumentException("Param rect must not be null!"); //$NON-NLS-1$
  	
  	if (zoomManager == null)
			throw new IllegalArgumentException("Param zoomManager must not be null!"); //$NON-NLS-1$
  	
    Rectangle relativeZoomRectangle = rect.getCopy();    
    Rectangle clientArea = zoomManager.getViewport().getClientArea();    
    double zoom = zoomManager.getZoom();
    
    double absoluteWidth = (double) relativeZoomRectangle.width;
    double absoluteHeight = (double) relativeZoomRectangle.height;
    double absoluteX = (double) relativeZoomRectangle.x;	
    double absoluteY = (double) relativeZoomRectangle.y;    
        
    double absoluteClientWidth = (double) clientArea.width / zoom;
    double absoluteClientHeight = (double) clientArea.height / zoom;
    double absoluteClientX = (double) clientArea.x / zoom;
    double absoluteClientY = (double) clientArea.y / zoom;
        
	  double zoomX = absoluteClientWidth / absoluteWidth;
	  double zoomY = absoluteClientHeight / absoluteHeight;
            
    double newZoom = Math.min(zoomX, zoomY);    
    double realZoom = (double) newZoom * zoom;        
    double newX = (double) (absoluteX + absoluteClientX) * realZoom;
    double newY = (double) (absoluteY + absoluteClientY) * realZoom;
    
    zoomManager.setZoom(realZoom);
    // check if the zoom is beyond the max or min zoom
    double z = zoomManager.getZoom();
    if (z != realZoom) {
    	newX = (double) (absoluteX + absoluteClientX) * z;
    	newY = (double) (absoluteY + absoluteClientY) * z;    	    	
    } 
    zoomManager.getViewport().setViewLocation((int)newX, (int)newY);    
    zoomManager.getViewport().getUpdateManager().performUpdate(); 
  }  
  
  public static void zoomToAbsoluteRect(Rectangle rect, ZoomManager zoomManager) 
  {
    Rectangle relativeZoomRectangle = rect.getCopy();
    Rectangle clientArea = zoomManager.getViewport().getClientArea();
    
    double zoom = zoomManager.getZoom();
    double absoluteWidth = (double) relativeZoomRectangle.width / zoom;
    double absoluteHeight = (double) relativeZoomRectangle.height / zoom;
    double absoluteX = (double) relativeZoomRectangle.x / zoom;	
    double absoluteY = (double) relativeZoomRectangle.y / zoom;
        
    double absoluteClientWidth = (double) clientArea.width / zoom;
    double absoluteClientHeight = (double) clientArea.height / zoom;
    double absoluteClientX = (double) clientArea.x / zoom;
    double absoluteClientY = (double) clientArea.y / zoom;
        
	  double zoomX = absoluteClientWidth / absoluteWidth;
	  double zoomY = absoluteClientHeight / absoluteHeight;
            
    double newZoom = Math.min(zoomX, zoomY);    
    double realZoom = (double) newZoom * zoom;        
    double newX = (double) (absoluteX + absoluteClientX) * realZoom;
    double newY = (double) (absoluteY + absoluteClientY) * realZoom;
    
    zoomManager.setZoom(realZoom);     
    // check if the zoom is beyond the max or min zoom
    double z = zoomManager.getZoom();
    if (z != realZoom) {
    	newX = (double) (absoluteX + absoluteClientX) * z;
    	newY = (double) (absoluteY + absoluteClientY) * z;    	    	
    } 
    zoomManager.getViewport().setViewLocation((int)newX, (int)newY);    
    zoomManager.getViewport().getUpdateManager().performUpdate(); 
  }
   
}
