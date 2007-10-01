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
import java.awt.Shape;
import java.awt.geom.Area;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.J2DGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ZoomListener;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.render.Draw2DRenderContext;
import org.nightlabs.editor2d.render.RenderContext;
import org.nightlabs.editor2d.render.RenderContextFinder;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.util.J2DUtil;
import org.nightlabs.editor2d.util.RenderUtil;


public class DrawComponentFigure  
extends Figure
implements RendererFigure
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(DrawComponentFigure.class.getName());
	
  private J2DGraphics j2d;
  private Graphics2D g2d;
    
  public void paint(Graphics2D graphics) 
  {
  	paintJ2D(graphics, drawComponent, renderer);
  }  
  
  public static void paintJ2D(Graphics2D graphics, DrawComponent drawComponent, Renderer renderer) 
  {
  	RenderUtil.paintJ2DRenderer(renderer, drawComponent, graphics);  	
  }
          
  public static void paintDraw2D(Graphics g, DrawComponent dc, Renderer r) 
  {
  	RenderContextFinder<Graphics> rcf = new RenderContextFinder<Graphics>();
  	rcf.paintRenderContext(g, dc, r, Draw2DRenderContext.RENDER_CONTEXT_TYPE);
//		if (r != null && dc != null && g != null) {				
//			RenderContext rc = r.getRenderContext(Draw2DRenderContext.RENDER_CONTEXT_TYPE);
//			if (rc != null) {
//				((Draw2DRenderContext) rc).paint(dc, g);
//			}
//			else {
//				r = dc.getRenderModeManager().getDefaultRenderer(dc.getClass());
//				rc = r.getRenderContext(Draw2DRenderContext.RENDER_CONTEXT_TYPE);
//				if (rc != null) {
//					((Draw2DRenderContext) rc).paint(dc, g);
//				}
//			}
//		}					
  }
    
  public void paint(Graphics graphics) 
  {  	
    if (graphics instanceof J2DGraphics) 
    {
      j2d = (J2DGraphics) graphics;
      j2d.clipRect(null);
      g2d = j2d.createGraphics2D();
      g2d.setClip(null);      
      paint(g2d);      
//      paintHitTestArea(g2d);
      g2d.dispose();
    } 
    else {
    	paintDraw2D(graphics, drawComponent, renderer);
    }    	
  }
        
  private Renderer renderer;   
  public void setRenderer(Renderer renderer) {
    this.renderer = renderer;
  }
  
  private DrawComponent drawComponent;  
  public void setDrawComponent(DrawComponent drawComponent) {
    this.drawComponent = drawComponent;
//    clearHitTestArea();
  }   
     
  private boolean contains = true;
  /**
   * determins if the {@link Figure#containsPoint(int, int)} should return the right
   * value of always false 
   * This can be used to remove a Figure from HitTesting of the {@link GraphicalViewer}
   * 
   * @param contains determins if {@link Figure#containsPoint(int, int)} should be
   * done or always false is returned
   */
  public void setContains(boolean contains) {
  	this.contains = contains;
  }
  
  /**
   * true if the {@link Figure#containsPoint(int, int)} is activated or false if hitTesting is off,
   * then {@link Figure#containsPoint(int, int)} always return false
   * 
   * @return true if the "normal" {@link Figure#containsPoint(int, int)} is activated or false if hitTesting is off  
   */
  public boolean isContains() {
  	return contains;
  }
  
  private boolean accurateContains = true;
  /**
   * determines if the {@link Figure#containsPoint(int, int)} should be calculated accurately
   * e.g. only the interior of a {@link Shape} including the {@link DrawComponentFigure#getHitTolerance()} 
   * would return true or if the {@link Figure#containsPoint(int, int)} should be calculated
   * on the basis of the {@link Figure#getBounds()}
   *  
   * @param containBounds determine if an accurate hitTesting should be performed or the 
   * {@link Figure#getBounds()} should be used
   * 
   * @see DrawComponentFigure#isContains()
   * @see DrawComponentFigure#getHitTolerance()
   * @see Figure#containsPoint(int, int)
   *   
   */
  public void setAccurateContains(boolean containBounds) {
  	this.accurateContains = containBounds;
  }
  /**
   * returns true if an accurate hitTesting is performed or false if the {@link Figure#getBounds()}
   * should are used for hitTesting  
   * 
   * @return true if an accurate hitTesting is performed or false if the {@link Figure#getBounds()}
   * should are used for hitTesting
   * 
   * @see DrawComponentFigure#setAccurateContains(boolean)
   * @see DrawComponentFigure#isContains()
   * @see DrawComponentFigure#getHitTolerance()
   * @see Figure#containsPoint(int, int)
   */
  public boolean isAccurateContains() {
  	return accurateContains;
  }
    
  @Override
  public boolean containsPoint(int x, int y) 
  {
  	if (contains) {  		
  		if (drawComponent != null) {
  			if (drawComponent instanceof TextDrawComponent) {
  				return super.containsPoint(x, y);
  			}
        if (drawComponent instanceof ShapeDrawComponent) {
          ShapeDrawComponent sdc = (ShapeDrawComponent) drawComponent;
          // if shape is not filled always do accurate hit test
          if (!sdc.isFill()) {
          	hitTestArea = getAccurateHitTestArea();
          	boolean contains = hitTestArea.contains(x, y);
          	// TODO cache the hitTestArea if the bounds did not changed
          	hitTestArea = null;
          	return contains;
          }
          else {
          	if (accurateContains)
              return sdc.getGeneralShape().contains(x,y);
          	// if not accurate just test the bounds
          	else
          		return super.containsPoint(x, y);
          }
        }   			
  		}     
      return super.containsPoint(x, y);  		
  	} 
  	return false;
  }
  
	@Override
	public boolean intersects(Rectangle rect) 
	{
	 	if (contains) {  		
  		if (drawComponent != null) {
        if (drawComponent instanceof ShapeDrawComponent) {
          ShapeDrawComponent sdc = (ShapeDrawComponent) drawComponent;
          // if shape is not filled always do accurate hit test
          if (!sdc.isFill()) {
          	hitTestArea = getAccurateHitTestArea();
          	boolean intersects = hitTestArea.intersects(rect.x, rect.y, rect.width, rect.height);
          	// TODO cache the hitTestArea if the bounds did not changed
          	hitTestArea = null;
          	return intersects;
          }
          else {
          	if (accurateContains)
              return sdc.getGeneralShape().intersects(rect.x, rect.y, rect.width, rect.height);
          	// if not accurate just test the bounds
          	else
          		return super.intersects(rect);
          }
        }   			
  		}     
      return super.intersects(rect);	
  	} 
  	return false;
	}
	
//  private Area outlineArea = null;  
//  public boolean containsPoint(int x, int y) 
//  {
//  	if (contains) {  		
//      if (accurateContains && drawComponent != null) {
//        if (drawComponent instanceof ShapeDrawComponent) {
//          ShapeDrawComponent sdc = (ShapeDrawComponent) drawComponent;
//          if (sdc.isFill())
//            return sdc.getGeneralShape().contains(x,y);
//          else {
//            if (outlineArea == null) {
//              Rectangle outerBounds = getBounds().getCopy();
//              Rectangle innerBounds = getBounds().getCopy();
//              outerBounds.expand((int)hitTolerance, (int)hitTolerance);
//              innerBounds.shrink((int)hitTolerance, (int)hitTolerance);
//              GeneralShape outerGS = (GeneralShape) sdc.getGeneralShape().clone();
//              GeneralShape innerGS = (GeneralShape) sdc.getGeneralShape().clone();
//              J2DUtil.transformGeneralShape(outerGS, getBounds(), outerBounds);
//              J2DUtil.transformGeneralShape(innerGS, getBounds(), innerBounds);
//              outlineArea = new Area(outerGS);
//              Area innerArea = new Area(innerGS); 
//              outlineArea.exclusiveOr(innerArea);             
//            }
//            boolean contains = outlineArea.contains(x,y);
//            outlineArea = null;
//            return contains;
//          }
//        }      
//      }
//      return super.containsPoint(x, y);  		
//  	} 
//  	return false;
//  }
    
  private Shape hitTestArea = null;
  public void clearHitTestArea() {
  	hitTestArea = null;
  }
  
  // only for debug
  private void paintHitTestArea(Graphics2D g2d) {
  	g2d.draw(getAccurateHitTestArea());
  }
  
  protected Shape getAccurateHitTestArea() 
  {
    if (hitTestArea == null) 
    {
    	if (drawComponent instanceof ShapeDrawComponent) 
    	{
    		ShapeDrawComponent sdc = (ShapeDrawComponent) drawComponent;
        Rectangle outerBounds = getBounds().getCopy();
        Rectangle innerBounds = getBounds().getCopy();
        outerBounds.expand((int)hitTolerance, (int)hitTolerance);
        innerBounds.shrink((int)hitTolerance, (int)hitTolerance);
        GeneralShape outerGS = (GeneralShape) sdc.getGeneralShape().clone();
        GeneralShape innerGS = (GeneralShape) sdc.getGeneralShape().clone();
        J2DUtil.transformGeneralShape(outerGS, getBounds(), outerBounds);
        J2DUtil.transformGeneralShape(innerGS, getBounds(), innerBounds);
        Area outlineArea = new Area(outerGS);
        Area innerArea = new Area(innerGS); 
        outlineArea.exclusiveOr(innerArea);  
        hitTestArea = outlineArea;
    	}
    	else 
    	{
        Rectangle outerBounds = getBounds().getCopy();
    		outerBounds.expand((int)hitTolerance, (int)hitTolerance);
    		hitTestArea = J2DUtil.toAWTRectangle(outerBounds);
    	}
    }
  	return hitTestArea;
  }
  
  public static final double DEFAULT_HIT_TOLERANCE = 5;
  private double hitTolerance = DEFAULT_HIT_TOLERANCE;
  /**
   * return the hitTolerance which is used when accurate hittesting 
   * {@link DrawComponentFigure#isAccurateContains()} is used when
   * {@link Figure#containsPoint(int, int)} is called
   * 
   * @return the hitTolerance for accurate hitTesting 
   * @see Figure#containsPoint(int, int)
   * @see DrawComponentFigure#isAccurateContains()
   */
  public double getHitTolerance() {
    return hitTolerance;
  }
  /**
   * determines the amount of tolerance when accurate hitTesting {@link DrawComponentFigure#isAccurateContains()}
   * is performed. This value is given in User Space Coordinates. 
   * e.g. when the user clicks only a little bit outside of the outline of shape, and this
   * distance is smaller than the hitTolerance {@link Figure#containsPoint(int, int)} will
   * still return true. Thia occurs only if {@link DrawComponentFigure#isContains()} and 
   * {@link DrawComponentFigure#isAccurateContains()} both return true 
   *   
   * @param hitTolerance the amount of the hitTolernace for accurate hittesting
   * @see Figure#containsPoint(int, int)
   * @see DrawComponentFigure#isAccurateContains()
   */
  public void setHitTolerance(double hitTolerance) {
    this.hitTolerance = hitTolerance;
  }
    
  private ZoomListener zoomListener = new ZoomListener() 
  {
    public void zoomChanged(double zoom) {
      hitTolerance = DEFAULT_HIT_TOLERANCE / zoom;
//      if (logger.isDebugEnabled())
//      	logger.debug("hitTolerance = "+hitTolerance);
    }    
  };
  public ZoomListener getZoomListener() {
    return zoomListener;
  }

  @Override
	public Rectangle getBounds() 
	{
		if (drawComponent != null)
			return J2DUtil.toDraw2D(drawComponent.getBounds());
		
		return super.getBounds();
	}  
 	
	public void dispose() 
	{
//		outlineArea = null;
		hitTestArea = null;
		if (j2d != null)
			j2d.dispose();
		j2d = null;
		if (g2d != null)
			g2d.dispose();
	}
		
}
