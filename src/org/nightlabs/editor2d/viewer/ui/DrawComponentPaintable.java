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

package org.nightlabs.editor2d.viewer.ui;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.util.GeomUtil;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.util.RenderUtil;

public class DrawComponentPaintable
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(DrawComponentPaintable.class.getName());
	
	public DrawComponentPaintable(DrawComponent dc)
	{
		super();
		this.dc = dc;
	}

	private DrawComponent dc;
	public DrawComponent getDrawComponent() {
		return dc;
	}
	
	/**
	 * calls paintDrawComponent with the given DrawComponent and the given
	 * Graphics2D
	 * 
	 * @see org.holongate.j2d.IPaintable#paint(org.eclipse.swt.widgets.Control, java.awt.Graphics2D)
	 */
	public void paint(Control control, Graphics2D g2d)
	{
		long startTime = 0;
		if (logger.isDebugEnabled()) {
			logger.debug("paint called!"); //$NON-NLS-1$
			startTime = System.currentTimeMillis();
		}
		paintDrawComponent(dc, g2d);
		if (logger.isDebugEnabled()) {
			long endTime = System.currentTimeMillis() - startTime;
			logger.debug("paint took = "+endTime+" ms!");			 //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * does nothing
	 * @see org.holongate.j2d.IPaintable#redraw(org.eclipse.swt.widgets.Control, org.eclipse.swt.graphics.GC)
	 */
	public void redraw(Control control, GC gc)
	{
		// TODO: should use SWTRenderContext
		logger.debug("redraw called!"); //$NON-NLS-1$
		long startTime = System.currentTimeMillis();
//		paintDrawComponent(dc, new SWTGraphics2D(gc));
//		paintDrawComponent(dc, gc);
		long endTime = System.currentTimeMillis() - startTime;
		logger.debug("redraw took = "+endTime+" ms!");		 //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * returns the bounds of the Control
	 * @see org.holongate.j2d.IPaintable#getBounds(org.eclipse.swt.widgets.Control)
	 */
	public Rectangle2D getBounds(Control control)
	{
		return GeomUtil.toRectangle2D(control.getBounds());
	}

	/**
	 * paints the {@link DrawComponent} on the given {@link Graphics2D}
	 * @param dc the DrawComponent to paint
	 * @param g2d the Graphics2D to paint on
	 * 
	 * @see RenderUtil#paintDrawComponent(DrawComponent, Graphics2D)
	 */
	public static void paintDrawComponent(DrawComponent dc, Graphics2D g2d)
	{
		RenderUtil.paintDrawComponent(dc, g2d);
	}
}

