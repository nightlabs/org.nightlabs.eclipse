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
package org.nightlabs.editor2d.render;

import java.awt.Shape;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.graphics.Path;
import org.nightlabs.base.ui.util.ColorUtil;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent.LineStyle;
import org.nightlabs.editor2d.viewer.ui.util.AWTSWTUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class Draw2DShapeDefaultRenderer 
extends Draw2DBaseRenderer 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(Draw2DShapeDefaultRenderer.class);
	
	public Draw2DShapeDefaultRenderer() {
		super();
	}

	public void paint(DrawComponent dc, Graphics g) 
	{
    ShapeDrawComponent sdc = (ShapeDrawComponent) dc;
    Path path = convertShape(sdc.getGeneralShape());
    if (sdc.isFill()) {
      g.setBackgroundColor(ColorUtil.toSWTColor(sdc.getFillColor()));
      g.fillPath(path);
    }
    g.setForegroundColor(ColorUtil.toSWTColor(sdc.getLineColor()));
//    g.setLineWidth(sdc.getLineWidth());
    g.setLineWidth((int)sdc.getLineWidth());    
    g.setLineStyle(convertLineStyle(sdc.getLineStyle()));
    g.drawPath(path);  
    
    logger.debug("shape painted!"); //$NON-NLS-1$
	}
	
	protected Path convertShape(Shape s) 
	{
		return AWTSWTUtil.convertShape(s, null, null);
	}
	 
	protected int convertLineStyle(LineStyle lineStyle) 
	{
		switch (lineStyle) 
		{
			case SOLID:
				return 1;
			case DASHED_1:
				return 2;
			case DASHED_2:
				return 3;
			case DASHED_3:
				return 4;
			case DASHED_4:
				return 5;
			default:
				return 1;
		}
	}		
}
