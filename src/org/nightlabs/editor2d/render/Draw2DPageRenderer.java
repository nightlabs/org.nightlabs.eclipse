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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.PageDrawComponent;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class Draw2DPageRenderer 
extends Draw2DBaseRenderer 
{
	public Draw2DPageRenderer() {
		super();
	}

	public void paint(DrawComponent dc, Graphics g) 
	{
    PageDrawComponent page = (PageDrawComponent) dc;
    g.setForegroundColor(ColorConstants.black);
    g.setLineStyle(SWT.LINE_SOLID);
    g.drawRectangle(page.getBounds().x, page.getBounds().y, 
    		page.getBounds().width, page.getBounds().height);
    super.paint(dc, g);
	}	
}
