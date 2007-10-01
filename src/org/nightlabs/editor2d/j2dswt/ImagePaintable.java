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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.holongate.j2d.IPaintable;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ImagePaintable 
implements IPaintable 
{
	private static final Logger logger = Logger.getLogger(ImagePaintable.class);
	
	public ImagePaintable(Image img) {
		super();
		this.image = img;
	}
	
	private Image image = null;
	public void setImage(Image img) {
		this.image = img;
	}
	public Image getRenderedImage() {
		return image;
	}
	
	public Rectangle2D getBounds(Control arg0) {
		return new Rectangle2D.Double(0, 0, image.getWidth(null), image.getHeight(null));
	}

	public void paint(Control arg0, Graphics2D g2d) 
	{
		if (image == null) {
			logger.debug("image = null!"); //$NON-NLS-1$
			return;			
		}

//		RenderingHints renderHints = g2d.getRenderingHints();
//		RenderingHintsManager.setSpeedRenderMode(renderHints);
//		g2d.setRenderingHints(renderHints);
		
		long start = System.currentTimeMillis();
		g2d.drawImage(image, 0, 0, null);
		long end = System.currentTimeMillis() - start;		
		logger.debug("paint took "+end+" ms!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void redraw(Control arg0, GC arg1) {

	}

}
