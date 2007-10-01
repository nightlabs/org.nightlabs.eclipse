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
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;

import org.eclipse.swt.widgets.Control;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent.LineStyle;
import org.nightlabs.editor2d.j2dswt.DrawComponentPaintable;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PrintPaintable 
extends DrawComponentPaintable 
{
	public PrintPaintable(DrawComponent dc, PageFormat pf) 
	{
		super(dc);	
		setPageFormat(pf);
	}

	protected PageFormat pageFormat;
	public void setPageFormat(PageFormat pf) {
		this.pageFormat = pf;
		pageRectangle = createRectangle(pageFormat);
	}
	
	protected Rectangle2D pageRectangle;	
	protected Rectangle2D createRectangle(PageFormat pf) 
	{
		return new Rectangle2D.Double(pageFormat.getImageableX(), pageFormat.getImageableY(),
				pageFormat.getImageableWidth(), pageFormat.getImageableHeight());		
	}
	
	/**
	 * calls paintDrawComponent with the given DrawComponent and the given
	 * Graphics2D
	 * 
	 * @see org.holongate.j2d.IPaintable#paint(org.eclipse.swt.widgets.Control, java.awt.Graphics2D)
	 */
	public void paint(Control control, Graphics2D g2d) 
	{		
		paintDrawComponent(getDrawComponent(), g2d);
		g2d.setPaint(Color.BLACK);
//		g2d.setStroke(RenderUtil.getStroke(5, 2));		
		g2d.setStroke(ShapeDrawComponent.StrokeUtil.getStroke(5, LineStyle.DASHED_1, 
				getDrawComponent().getRoot().getResolution()));		
		g2d.draw(pageRectangle);
		
//		double scaleX = g2d.getTransform().getScaleX();
//		double scaleY = g2d.getTransform().getScaleY();
//		Rectangle2D scaledRect = scaleRectangle(pageRectangle, scaleX, scaleY);
//		g2d.draw(scaledRect);		
	}	
	
	protected Rectangle2D scaleRectangle(Rectangle2D rect, double scaleX, double scaleY) 
	{
		double newWidth = rect.getWidth() / scaleX;
		double newHeight = rect.getHeight() / scaleY;
		return new Rectangle2D.Double(rect.getX(), rect.getY(), 
				newWidth, newHeight);
	}
}
