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

import java.awt.image.BufferedImage;

import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.viewer.ui.util.AWTSWTUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class Draw2DImageDefaultRenderer 
extends Draw2DBaseRenderer 
{
	public Draw2DImageDefaultRenderer() {
		super();
	}

	public void paint(DrawComponent dc, Graphics g) 
	{
    ImageDrawComponent image = (ImageDrawComponent) dc;
    if (image.getImage() != null) {
    	Image img = convertImage(image.getImage());
      g.drawImage(img, image.getX(), image.getY());    	
    }
	}
	
	protected Image convertImage(BufferedImage img) {
//		return AWTSWTUtil.toSWTImage(img, null);
		return AWTSWTUtil.toSWTImage(img, Display.getDefault());
	}
}
