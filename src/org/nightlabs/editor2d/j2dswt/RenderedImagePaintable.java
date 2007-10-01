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
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.holongate.j2d.IPaintable;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class RenderedImagePaintable 
implements IPaintable 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(RenderedImagePaintable.class);
	
	private RenderedImage image = null;
	public void setImage(RenderedImage bi) {
		this.image = bi;
	}
	public RenderedImage getImage() {
		return image;
	}
	
	public RenderedImagePaintable(RenderedImage bi) {
		super();
		this.image = bi;
	}

	/**
	 * @see org.holongate.j2d.IPaintable#paint(org.eclipse.swt.widgets.Control, java.awt.Graphics2D)
	 */
	public void paint(Control control, Graphics2D g2d) 
	{
		if (image == null) {
			logger.debug("image = null!"); //$NON-NLS-1$
			return;			
		}
		
		long start = System.currentTimeMillis();
		g2d.drawRenderedImage(image, null);
		long end = System.currentTimeMillis() - start;		
		logger.debug("paint took "+end+" ms!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see org.holongate.j2d.IPaintable#redraw(org.eclipse.swt.widgets.Control, org.eclipse.swt.graphics.GC)
	 */
	public void redraw(Control control, GC gc) {

	}

	/**
	 * @see org.holongate.j2d.IPaintable#getBounds(org.eclipse.swt.widgets.Control)
	 */
	public Rectangle2D getBounds(Control control) {
		return new Rectangle2D.Double(0,0,image.getWidth(),image.getHeight());
	}
}
