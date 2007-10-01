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
package org.nightlabs.editor2d.j2dswt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.eclipse.swt.widgets.Control;
import org.nightlabs.editor2d.DrawComponent;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PrintPreviewPaintable 
extends DrawComponentPaintable 
{

	/**
	 * @param dc
	 */
	public PrintPreviewPaintable(DrawComponent dc, Color bgColor, 
			Rectangle pageBounds, Rectangle marginBounds) 
	{
		super(dc);
//		if (bgColor == null || pageBounds == null || marginBounds == null)
//			throw new IllegalArgumentException("none of the params must be null!");
		
		this.bgColor = bgColor;
		this.pageBounds = pageBounds;
		this.marginBounds = marginBounds;		
	}

	private Color bgColor = null;
	private Rectangle pageBounds; 
	private Rectangle marginBounds;	

	@Override
	public void paint(Control control, Graphics2D g2d) 
	{
		g2d.setPaint(bgColor);
		if (pageBounds != null)
			g2d.fillRect(pageBounds.x, pageBounds.y, pageBounds.width, pageBounds.height);

//		g2d.translate(marginBounds.x, marginBounds.y);
		g2d.setPaint(Color.BLACK);
		if (marginBounds != null)
			g2d.drawRect(0, 0, marginBounds.width, marginBounds.height);		
		super.paint(control, g2d);
		
//		g2d.translate(-marginBounds.x, -marginBounds.y);		
	}
	
}
