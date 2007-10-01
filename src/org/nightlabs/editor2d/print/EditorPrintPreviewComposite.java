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
package org.nightlabs.editor2d.print;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.holongate.j2d.J2DCanvas;
import org.nightlabs.base.ui.print.PrintPreviewComposite;
import org.nightlabs.base.ui.util.ColorUtil;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.j2dswt.PrintPreviewPaintable;
import org.nightlabs.editor2d.util.GeomUtil;
import org.nightlabs.editor2d.util.UnitUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorPrintPreviewComposite 
extends PrintPreviewComposite 
{
	private static final Logger logger = Logger.getLogger(EditorPrintPreviewComposite.class);
	
	/**
	 * @param pageFormat
	 * @param parent
	 * @param style
	 */
	public EditorPrintPreviewComposite(DrawComponent dc, PageFormat pageFormat, Composite parent,
			int style) 
	{
		super(pageFormat, parent, style);
		init(dc);
		super.init(pageFormat);		
	}

	/**
	 * @param pageFormat
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public EditorPrintPreviewComposite(DrawComponent dc, PageFormat pageFormat, Composite parent,
			int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(pageFormat, parent, style, layoutMode, layoutDataMode);
		init(dc);
		super.init(pageFormat);
	}

	private void init(DrawComponent dc) 
	{
		if (dc == null)
			throw new IllegalArgumentException("Param dc must NOT be null!"); //$NON-NLS-1$
		
		this.dc = dc;
		
		Point2D resolutionScale = UnitUtil.getResolutionScale(72, dc); 
		this.resolutionScale = Math.max(resolutionScale.getX(), resolutionScale.getY());
		
		paintable = new PrintPreviewPaintable(dc, getAWTBackgroundColor(), getPageRectangle(), getImageablePageRectangle());		
	}

	private Color awtbgColor = null;
	private Color getAWTBackgroundColor() {
		if (awtbgColor == null)
			awtbgColor = ColorUtil.toAWTColor(getBackgroundColor());
		return awtbgColor;
	}
	
	private DrawComponent dc = null;
	private PrintPreviewPaintable paintable = null;	
	
	@Override
	protected Canvas initCanvas(Composite parent) {		
		return new J2DCanvas(parent, paintable);
	}

	protected J2DCanvas getJ2DCanvas() {
		return (J2DCanvas) canvas;
	}
	
	@Override
	protected void init(PageFormat pf)  {
		// Overriden to do nothing and super.init(pd) is called in constructor after initialzing dc
	}
		
	private Rectangle dcBounds = null;
	protected Rectangle getDrawComponentBounds() 
	{
		if (dcBounds == null) 
		{
			dc.clearBounds();
			Point2D resolutionScale = UnitUtil.getResolutionScale(72, dc);
			dcBounds = scaleRect(
					dc.getBounds(), 
					1 / resolutionScale.getX(), 
					1 / resolutionScale.getY(), 
					true);
		}
		return dcBounds;
	}
	
	private Rectangle originalDCBounds = null;
	protected Rectangle getOriginalDCBounds() 
	{
		if (originalDCBounds == null) 
		{
			dc.clearBounds();
			originalDCBounds = dc.getBounds();
		}
		return originalDCBounds;
	}
	
	protected Rectangle getCanvasBounds() {
		return org.nightlabs.base.ui.util.GeomUtil.toAWTRectangle(getJ2DCanvas().getClientArea());
	}
		
	private double resolutionScale = 1.0;
	private double getResolutionScale() {
		return 1 / resolutionScale;
	}
	private double scale = 1.0;		
	protected double calcScale() 
	{
		double margins2DCScale = getMargins2DCBoundsScale();
		double canvas2PageScale = getCanvas2PageScale();			
		double ratio = margins2DCScale * canvas2PageScale;						
		double scale = 1 / ratio;
		
		scale = scale * getResolutionScale();
		
		if (logger.isDebugEnabled()) {
			logger.debug("marginBounds = " + getImageablePageRectangle()); //$NON-NLS-1$
			logger.debug("pageRectangle = " + getPageRectangle());			 //$NON-NLS-1$
			logger.debug("dcBounds (72DPI) = " + getDrawComponentBounds());			 //$NON-NLS-1$
			logger.debug("canvasBounds = " + getCanvasBounds()); //$NON-NLS-1$
			logger.debug("margins2DCScale = "+margins2DCScale); //$NON-NLS-1$
			logger.debug("page2DCScale = "+getPage2DCBoundsScale());			 //$NON-NLS-1$
			logger.debug("canvas2PageScale = "+canvas2PageScale); //$NON-NLS-1$
			logger.debug("ratio = "+ratio);			 //$NON-NLS-1$
			logger.debug("scale = "+scale);			 //$NON-NLS-1$
			logger.debug("resolutionScale = "+getResolutionScale()); //$NON-NLS-1$
			logger.debug("");			 //$NON-NLS-1$
		}				
		return scale;
	}
	
//	protected Point2D getMargins2DCBoundsRatio() {
//		return GeomUtil.calcScale(getImageablePageRectangle(), getDrawComponentBounds());
////		return GeomUtil.calcScale(getImageablePageRectangle(), getOriginalDCBounds());		
//	}

//	protected Point2D getPage2DCBoundsRatio() {
////		return GeomUtil.calcScale(getPageRectangle(), getDrawComponentBounds());
//		return GeomUtil.calcScale(getPageRectangle(), getOriginalDCBounds());		
//	}
 
//	protected Point2D getCanvas2PageRatio() {
//		return GeomUtil.calcScale(getCanvasBounds(), getPageRectangle());
//	}		
	
	protected double getMargins2PageScale() {
		Point2D margins2pageRatio = GeomUtil.calcScale(getImageablePageRectangle(), getPageRectangle());
		return Math.max(margins2pageRatio.getX(), margins2pageRatio.getY());		
	}
	
	protected double getPage2DCBoundsScale() 
	{		
		Point2D page2DCBoundsRatio = GeomUtil.calcScale(getPageRectangle(), getDrawComponentBounds());		
//		return Math.min(page2DCBoundsRatio.getX(), page2DCBoundsRatio.getY());				
		return Math.max(page2DCBoundsRatio.getX(), page2DCBoundsRatio.getY());		
	}

	protected double getMargins2DCBoundsScale() 
	{		
		Point2D margins2DCBoundsRatio = GeomUtil.calcScale(getImageablePageRectangle(), getDrawComponentBounds());		
//		return Math.min(margins2DCBoundsRatio.getX(), margins2DCBoundsRatio.getY());				
		return Math.max(margins2DCBoundsRatio.getX(), margins2DCBoundsRatio.getY());		
	}

	protected double getCanvas2PageScale() 
	{		
		Point2D canvas2PageRatio = GeomUtil.calcScale(getCanvasBounds(), getPageRectangle());		
//		return Math.min(canvas2PageRatio.getX(), canvas2PageRatio.getY());				
		return Math.max(canvas2PageRatio.getX(), canvas2PageRatio.getY());		
	}
	
	protected Rectangle getPaintedPageRectangle() 
	{		
//		double scale = getPage2DCBoundsScale() * resolutionScale;		
		double scale = getMargins2DCBoundsScale() * resolutionScale;		
		return scaleRect(getPageRectangle(), scale, scale, true);
	}
	
	protected Rectangle getPaintedMarginBounds() 
	{
//		double scale = getMargins2DCBoundsScale() * resolutionScale;
		double margins2PageScale = 1 / getMargins2PageScale(); 		
		double scale = getMargins2DCBoundsScale() * resolutionScale * margins2PageScale;		
		return scaleRect(getImageablePageRectangle(), scale, scale, false);		
	}
	
	@Override
	protected void updateCanvas() 
	{		
		setCanvasSize();		
		paintable = new PrintPreviewPaintable(dc, getAWTBackgroundColor(), 
				getPaintedPageRectangle(), getPaintedMarginBounds());
		getJ2DCanvas().setPaintable(paintable);
		scale = calcScale();
				
		getJ2DCanvas().getPaintableManager().setScale(scale);							
		getJ2DCanvas().repaint();
		
		if (logger.isDebugEnabled()) {
			logger.debug("original DCBounds = "+getOriginalDCBounds()); //$NON-NLS-1$
			logger.debug("paintPageRect = "+getPaintedPageRectangle()); //$NON-NLS-1$
			logger.debug("paintMarginsRect = "+getPaintedMarginBounds());			 //$NON-NLS-1$
			logger.debug("margin2PageRatio = "+getMargins2PageScale());			 //$NON-NLS-1$
		}
	}
 		
	private Rectangle scaleRect(Rectangle rect, double scaleX, double scaleY, boolean onlyDimension) {
		return org.nightlabs.base.ui.util.GeomUtil.scaleRect(rect, scaleX, scaleY, onlyDimension);
	}
	
//	@Override
//	protected void updateCanvas() 
//	{
//		getJ2DCanvas().repaint();
//	}
		
	@Override
	protected PaintListener initPagePaintListener() 
	{
		return new PaintListener()
		{		
			public void paintControl(PaintEvent e) 
			{
				
			}		
		};
	}	
		
}
