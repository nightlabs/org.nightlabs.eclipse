/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

import java.awt.print.PageFormat;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.resource.Messages;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class PrintUtil 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(PrintUtil.class);
	
	/**
	 * Use case string for prints of Editor 2D graphics
	 * TODO: Daniel, you might want to move this somewhere else?
	 */
	public static final String PRINTER_USE_CASE_EDITOR_2D = "PrinterUseCase-Editor2D";	 //$NON-NLS-1$
	
	public PrintUtil() {
		super();
	}

	public static void logPageFormat(PageFormat pf) 
	{
		if (logger.isDebugEnabled()) {
			logger.debug("PageFormat Width = "+pf.getWidth()); //$NON-NLS-1$
			logger.debug("PageFormat Height = "+pf.getHeight());		 //$NON-NLS-1$
			logger.debug("PageFormat ImageableX = "+pf.getImageableX()); //$NON-NLS-1$
			logger.debug("PageFormat ImageableY = "+pf.getImageableY()); //$NON-NLS-1$
			logger.debug("PageFormat ImageableWidth = "+pf.getImageableWidth()); //$NON-NLS-1$
			logger.debug("PageFormat ImageableHeight = "+pf.getImageableHeight()); //$NON-NLS-1$
			logger.debug("PageFormat Orientation = "+getOrientationAsString(pf.getOrientation()));	 //$NON-NLS-1$
			logger.debug("");			 //$NON-NLS-1$
		}
	}
	
	protected static String getOrientationAsString(int orientation) 
	{
		switch (orientation) 
		{
			case(PageFormat.LANDSCAPE):
				return Messages.getString("org.nightlabs.editor2d.print.PrintUtil.landscape"); //$NON-NLS-1$
			case(PageFormat.PORTRAIT):
				return Messages.getString("org.nightlabs.editor2d.print.PrintUtil.portrait"); //$NON-NLS-1$
			case(PageFormat.REVERSE_LANDSCAPE):
				return Messages.getString("org.nightlabs.editor2d.print.PrintUtil.reverseLandscape");						 //$NON-NLS-1$
		}
		return Messages.getString("org.nightlabs.editor2d.print.PrintUtil.noValidOrientation"); //$NON-NLS-1$
	}	
	
//	public static void prepareGraphics(Graphics2D g2d, DrawComponent dc, PageFormat pageFormat) 
//	{
//		if (logger.isDebugEnabled())
//			logger.debug("dc.getBounds() = "+dc.getBounds());
//		
//		Rectangle dcBounds = GeomUtil.translateToOrigin(dc.getBounds());
//		
//		if (logger.isDebugEnabled())
//			logger.debug("originBounds = "+dcBounds);
//		
////		Point2D resolutionScale = UnitUtil.getResolutionScale(72, dc);
////		GeneralShape gs = new GeneralShape(dcBounds);
////		AffineTransform scaleAT = AffineTransform.getScaleInstance(resolutionScale.getX(), resolutionScale.getY());
////		gs.transform(scaleAT);
////		dcBounds = gs.getBounds();
////		
////		if (logger.isDebugEnabled()) {
////			logger.debug("resolutionScale = "+resolutionScale);
////			logger.debug("dcBounds = "+dcBounds);
////		}
//		
//		Rectangle pageRectangle = new Rectangle(0, 0, 
//				(int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());		
//		
//		Point2D scales = GeomUtil.calcScale(dcBounds, pageRectangle);		
//		double scale = Math.min(scales.getX(), scales.getY()); 
//		double translateX = (((double)pageFormat.getImageableX()) - (dc.getX() * scale ));
//		double translateY = (((double)pageFormat.getImageableY()) - (dc.getY() * scale )); 		
//		g2d.translate(translateX, translateY);		
//		g2d.scale(scale, scale);
//		
//		if (logger.isDebugEnabled()) {
//			logger.debug("pageRectangle = "+pageRectangle);
//			logger.debug("scale = "+scale);
//			logger.debug("translateX = "+translateX);
//			logger.debug("translateY = "+translateY);			
//		}
//	}	
//
//	public static void prepareGraphics(Graphics2D g2d, PageDrawComponent page, PageFormat pageFormat) 
//	{
//		if (logger.isDebugEnabled())
//			logger.debug("page.getBounds() = "+page.getBounds());
//		
//		Rectangle pageBounds = GeomUtil.translateToOrigin(page.getBounds());
//		
//		if (logger.isDebugEnabled())
//			logger.debug("originBounds = "+pageBounds);
//				
//		Rectangle pageRectangle = new Rectangle(0, 0, 
//				(int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());		
//		
//		Point2D scales = GeomUtil.calcScale(pageBounds, pageRectangle);		
//		double scale = Math.min(scales.getX(), scales.getY()); 
//		double translateX = (((double)pageFormat.getImageableX()) - (page.getX() * scale ));
//		double translateY = (((double)pageFormat.getImageableY()) - (page.getY() * scale )); 		
//		g2d.translate(translateX, translateY);		
//		g2d.scale(scale, scale);
//		
//		if (logger.isDebugEnabled()) {
//			logger.debug("pageRectangle = "+pageRectangle);
//			logger.debug("scale = "+scale);
//			logger.debug("translateX = "+translateX);
//			logger.debug("translateY = "+translateY);			
//		}
//	}	
	
}
