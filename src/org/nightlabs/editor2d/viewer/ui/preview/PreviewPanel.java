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
package org.nightlabs.editor2d.viewer.ui.preview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.util.GeomUtil;
import org.nightlabs.editor2d.viewer.ui.DrawComponentPaintable;
import org.nightlabs.editor2d.viewer.ui.IViewport;

/**
 * Shows a preview of an viewport so that the whole content
 * is visible and the viewBounds of the viewport are displayed
 * 
 * <p> Project: org.nightlabs.editor2d.viewer.ui </p>
 * <p> Creation Date: 04.01.2006 </p>
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PreviewPanel
extends JPanel
{
	private static final long serialVersionUID = 1L;
	//	private static final Logger logger = Logger.getLogger(PreviewPanel.class);
	private IViewport viewport = null;
	private DrawComponent dc = null;
	private Color bgColor = Color.WHITE;
	private BufferedImage bufferImage = null;
	private double zoom = 1.0;
	private double proportionScale = 1.0;
	private Color sysBGColor = null;
	private Rectangle viewRectangle;
	private boolean pressed = false;
	private int startX;
	private int startY;
	private int diffX;
	private int diffY;
	private int startViewX;
	private int startViewY;

	/**
	 * @param dc The DrawComponent
	 */
	public PreviewPanel(DrawComponent dc, IViewport viewport, Color sysBGColor)
	{
		super();
		this.dc = dc;
		this.viewport = viewport;
		this.sysBGColor = sysBGColor;
		init();
	}

	private ComponentListener resizeListener = new ComponentAdapter(){
		@Override
		public void componentResized(ComponentEvent e) {
			clearBuffer();
		}
	};

	private MouseListener mouseListener = new MouseAdapter(){
		@Override
		public void mouseReleased(MouseEvent e) {
			pressed = false;
		}
		@Override
		public void mousePressed(MouseEvent e)
		{
			if (viewRectangle.contains(e.getX(), e.getY())) {
				pressed = true;
				startX = e.getX();
				startY = e.getY();
				startViewX = viewRectangle.getLocation().x;
				startViewY = viewRectangle.getLocation().y;
			}
		}
	};

	private MouseMotionListener mouseMotionListener = new MouseMotionAdapter()
	{
		@Override
		public void mouseDragged(MouseEvent e)
		{
			if (pressed) {
				diffX = e.getX() - startX;
				diffY = e.getY() - startY;
				double newX = startViewX + diffX;
				double newY = startViewY + diffY;
				double realX = newX / proportionScale;
				double realY = newY / proportionScale;
				viewport.setViewLocation((int)realX, (int)realY);
			}
		}
	};

	protected void init()
	{
		addComponentListener(resizeListener);
		viewport.addPropertyChangeListener(viewChangeListener);
		viewport.addPropertyChangeListener(realChangeListener);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseMotionListener);
	}

	/**
	 * gets notified when the viewBounds of the Viewport change
	 * @see org.nightlabs.editor2d.viewer.ui.IViewport#getViewBounds()
	 */
	private PropertyChangeListener viewChangeListener = new PropertyChangeListener()
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			repaint();
		}
	};

	/**
	 * gets notified when the realBounds of the Viewport change
	 * @see org.nightlabs.editor2d.viewer.ui.IViewport#getRealBounds()
	 */
	private PropertyChangeListener realChangeListener = new PropertyChangeListener()
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			repaint();
		}
	};

	@Override
	public void paintComponent(Graphics g)
	{
		if (disposed)
			return;

		Graphics2D g2d = (Graphics2D) g;

		if (bufferImage == null) {
			double width = getVisibleRect().getWidth();
			double height = getVisibleRect().getHeight();
			bufferImage = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D bg2d = (Graphics2D) bufferImage.getGraphics();
			//			Rectangle dcBounds = dc.getBounds();
			Rectangle dcBounds = GeomUtil.translateToOriginAndAdjustSize(dc.getBounds());
			//			Rectangle dcBounds = GeomUtil.translateToOrigin(dc.getBounds());
			double scaleX = width / dcBounds.getWidth();
			double scaleY = height / dcBounds.getHeight();
			zoom = Math.min(scaleX, scaleY);
			paintDrawComponent(bg2d, zoom, dcBounds, (int)width, (int)height);
			//			if (logger.isDebugEnabled()) {
			//				logger.debug("dc.bounds = "+dc.getBounds());
			//				logger.debug("visibleRect = "+getVisibleRect());
			//			}
		}
		if (bufferImage != null)
			g2d.drawImage(bufferImage, 0, 0, this);

		drawViewRect(g2d);
	}

	private void drawViewRect(Graphics2D g2d)
	{
		if (dc == null || dc.isDisposed())
			return;

		Rectangle realBounds = viewport.getRealBounds();
		Rectangle viewBounds = viewport.getViewBounds();
		if (realBounds == null || viewBounds == null) {
			// the viewport is obviously shut down (disposed) => return.
			return;
		}

		double viewRealWidth = realBounds.getWidth();
		double viewRealHeight = realBounds.getHeight();
		//		Rectangle dcBounds = dc.getBounds();
		Rectangle dcBounds = GeomUtil.translateToOriginAndAdjustSize(dc.getBounds());
		//		Rectangle dcBounds = GeomUtil.translateToOrigin(dc.getBounds());
		double dcWidth = dcBounds.getWidth();
		double dcHeight = dcBounds.getHeight();
		double scaleX = dcWidth / viewRealWidth;
		double scaleY = dcHeight / viewRealHeight;
		double scale = Math.min(scaleX, scaleY);
		proportionScale = scale * zoom;

		double proportionScaleX = scaleX * zoom;
		double proportionScaleY = scaleY * zoom;
		double translateX = -dcBounds.getX() * proportionScale;
		double translateY = -dcBounds.getY() * proportionScale;

		viewRectangle = GeomUtil.scaleRect(viewBounds,
				proportionScaleX, proportionScaleY, false);
		g2d.translate(translateX, translateY);
		g2d.setPaint(Color.RED);
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(viewRectangle);
	}

	private void paintDrawComponent(Graphics2D g2d, double scale, Rectangle bounds, int width, int height)
	{
		//		RenderingHints rh = g2d.getRenderingHints();
		//		RenderingHintsManager.setSpeedRenderMode(rh);
		//		g2d.setRenderingHints(rh);
		g2d.setPaint(sysBGColor);
		g2d.fillRect(0, 0, width, height);
		g2d.setBackground(bgColor);
		g2d.setPaint(bgColor);
		g2d.translate(-bounds.x, -bounds.y);
		g2d.setClip(bounds);
		g2d.scale(scale, scale);
		g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		DrawComponentPaintable.paintDrawComponent(dc, g2d);
	}

	public void clearBuffer() {
		if (bufferImage != null)
			bufferImage.flush();
		bufferImage = null;
		repaint();
	}

	private volatile boolean disposed = false;

	public void dispose()
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				disposed = true;

				if (dc != null) {
					dc.dispose();
					dc = null;
				}

				if (bufferImage != null) {
					bufferImage.flush();
					bufferImage = null;
				}
				if (viewport != null) {
					viewport.removePropertyChangeListener(realChangeListener);
					viewport.removePropertyChangeListener(viewChangeListener);
					viewport = null;
				}
				bgColor = null;
				sysBGColor = null;
				listenerList = null;
				mouseListener = null;
				mouseMotionListener = null;
			}
		});
	}
}
