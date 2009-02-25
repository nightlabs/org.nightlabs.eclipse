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
package org.nightlabs.editor2d.ui.render;

import java.awt.image.BufferedImage;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.ui.util.J2DUtil;
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

	@Override
	public void paint(DrawComponent dc, Graphics g)
	{
		ImageDrawComponent image = (ImageDrawComponent) dc;
		if (image.getImage() != null) 
		{
			g.setAntialias(SWT.ON);
			
//			Image img = convertImage(image.getImage());
//			g.drawImage(img, image.getX(), image.getY());
//			img.dispose();
			
//			AffineTransform at = image.getAffineTransform();			
//			float scaleX = (float)at.getScaleX();
//			float scaleY = (float)at.getScaleY();
//			float translateX = (float) at.getTranslateX();
//			float translateY = (float) at.getTranslateY();
//			float rotation = (float) image.getRotation();
//			g.translate(translateX, translateY);
//			g.scale(scaleX);
//			g.rotate((float)image.getRotation());
//			g.drawImage(img, image.getX(), image.getY());
//			g.scale(1/scaleX);
//			g.translate(-translateX, -translateY);
//			g.rotate(-rotation);
			
			Rectangle imageBounds = J2DUtil.toDraw2D(image.getBounds());
			Image img = convertImage(image.getOriginalImage());
			g.drawImage(img, 0, 0, image.getOriginalImage().getWidth(), image.getOriginalImage().getHeight(),
					imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height);
			
			img.dispose();
		}
	}

	protected Image convertImage(BufferedImage img) {
		return AWTSWTUtil.toSWTImage(img, Display.getDefault());
	}
}
