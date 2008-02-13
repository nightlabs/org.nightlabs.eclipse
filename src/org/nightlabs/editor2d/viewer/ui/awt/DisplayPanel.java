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

package org.nightlabs.editor2d.viewer.ui.awt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JToolTip;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.util.GeomUtil;
import org.nightlabs.editor2d.util.RenderUtil;
import org.nightlabs.editor2d.util.RenderingHintsManager;
import org.nightlabs.editor2d.viewer.ui.BufferManager;
import org.nightlabs.editor2d.viewer.ui.DrawComponentPaintable;
import org.nightlabs.editor2d.viewer.ui.IBufferedViewport;
import org.nightlabs.editor2d.viewer.ui.ITempContentManager;
import org.nightlabs.editor2d.viewer.ui.TempContentManager;

public class DisplayPanel
extends JPanel
implements IBufferedViewport
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(DisplayPanel.class);
		
	public DisplayPanel(DrawComponent dc)
	{
		super();
		this.dc = dc;
//		setDebug(false, false);
		Rectangle dcBounds = new Rectangle(dc.getBounds());
		AffineTransform at = new AffineTransform();
		at.translate(-dcBounds.x, -dcBounds.y);
		if (debugBounds) {
			logger.debug("init dcBounds "+dcBounds); //$NON-NLS-1$
			logger.debug("translateX = "+(-dcBounds.x)); //$NON-NLS-1$
			logger.debug("translateY = "+(-dcBounds.y)); //$NON-NLS-1$
		}
		dc.transform(at);
		dc.clearBounds();
		if (debugBounds)
			logger.debug("tranlated dcBounds = " + dc.getBounds()); //$NON-NLS-1$
		dcBounds = GeomUtil.translateToOriginAndAdjustSize(dc.getBounds());
		if (debugBounds)
			logger.debug("translateToOriginAndAdjustSize dcBounds = " + dcBounds); //$NON-NLS-1$
		
		setBackground(bgColor);
		renderingHintsManager.setRenderMode(RenderingHintsManager.RENDER_MODE_QUALITY);
		init(dcBounds);
	}
			
	public DrawComponent getDrawComponent() {
		return dc;
	}
	
	private boolean debugBounds = false;
	private boolean debugPaint = false;
	
	private void setDebug(boolean paint, boolean bounds)
	{
		debugPaint = paint;
		debugBounds = bounds;
	}
	
	private int imageType = BufferedImage.TYPE_INT_RGB;
	private Rectangle bufferBounds;
	private DrawComponent dc;
	private BufferedImage bufferedImage;
	private BufferedImage viewImage;
	private int initSize = 100;
	private RenderingHintsManager renderingHintsManager = RenderingHintsManager.sharedInstance();
	
	/**
	 * paints the drawComponent, if no notifyChange has been called a BitBlockTransfer
	 * from the BufferedImage is performed
	 */
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		long startPaintTime = 0;
		if (debugPaint) {
			startPaintTime = System.currentTimeMillis();
		}
				
		if (isChanged)
		{
			if (debugPaint)
				logger.debug("buffer cleared!");				 //$NON-NLS-1$
							
			paintDrawComponent();
			isChanged = false;
		}
							
		if (bufferedImage != null)
		{
			long startTime = 0;
			if (debugPaint)
				startTime = System.currentTimeMillis();
									
			// Do a BitBlock Transfer
			calcBufferedViewImage();
			if (viewImage != null)
				g2d.drawImage(viewImage,0,0,this);
						
			if (debugPaint)
				logger.debug("BitBlockTransfer took "+(System.currentTimeMillis()-startTime)+" ms!"); //$NON-NLS-1$ //$NON-NLS-2$
			
			if (debugBounds) {
				logger.debug("viewImage width = "+viewImage.getWidth()); //$NON-NLS-1$
				logger.debug("viewImage height = "+viewImage.getHeight()); //$NON-NLS-1$
			}
		}
		
		// Draw Temporary Content above Buffer (SelectionRectangle etc.)
		if (drawTempContent == true) {
			g2d.translate(-getOffsetX(), -getOffsetY());
			g2d.scale(getScale(), getScale());
			drawTempContent(g2d);
		}
				
		if (debugPaint) {
			logger.debug("Total Paint took "+(System.currentTimeMillis()-startPaintTime)+" ms"); //$NON-NLS-1$ //$NON-NLS-2$
			logger.debug("");			 //$NON-NLS-1$
		}
	}
				
	/**
	 * calculates the viewImage, which is a subImage (equivalent to the viewBounds)
	 * of the BufferedImage
	 *
	 */
	protected void calcBufferedViewImage()
	{
		int offsetX = getBufferOffsetX();
		int offsetY = getBufferOffsetY();
				
		if (viewImage != null)
			viewImage.flush();
		
		long startTime = 0;
		if (debugPaint) {
			startTime = System.currentTimeMillis();
		}

		if (offsetX < 0) {
			offsetX = 0;
			logger.warn("offsetX "+offsetX+" < 0"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (offsetY < 0) {
			offsetY = 0;
			logger.warn("offsetX "+offsetX+" < 0"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if (bufferedImage != null)
		{
			if ((bufferedImage.getWidth() >= viewBounds.width + offsetX) &&
					(bufferedImage.getHeight() >= viewBounds.height + offsetY) &&
					viewBounds.width > 0 && viewBounds.height > 0
//					&& offsetX >= 0 && offsetY >= 0
					)
			{
				viewImage =
					bufferedImage.getSubimage(offsetX, offsetY, viewBounds.width, viewBounds.height);
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("ViewImage NOT updated!"); //$NON-NLS-1$
					logger.debug("viewBounds.width = "+viewBounds.width);			 //$NON-NLS-1$
					logger.debug("viewBounds.height = "+viewBounds.height); //$NON-NLS-1$
					logger.debug("offsetX = " + offsetX); //$NON-NLS-1$
					logger.debug("offsetY = " + offsetY);	 //$NON-NLS-1$
					logger.debug(""); //$NON-NLS-1$
				}
			}
		}
										
		if (debugBounds) {
			Rectangle viewImageBounds = new Rectangle(offsetX, offsetY, viewBounds.width, viewBounds.height);
			logger.debug("viewImage Bounds = "+viewImageBounds); //$NON-NLS-1$
		}
		
		if (debugPaint) {
			long endTime = System.currentTimeMillis() - startTime;
			logger.debug("create viewImage took "+endTime+" ms!"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * sets realBounds to a new scaled Rectangle which is determined
	 * by the given scale
	 * 
	 * @param scale the scaleFactor
	 */
	protected void setZoomedRealBounds(double scale)
	{
		int newWidth = (int) Math.floor(initRealBounds.width * scale);
		int newHeight = (int) Math.floor(initRealBounds.height * scale);
//		int newX = (int) Math.floor(initRealBounds.x * scale);
//		int newY = (int) Math.floor(initRealBounds.y * scale);
		Rectangle newRealBounds = new Rectangle(0, 0, newWidth, newHeight);
		setRealBounds(newRealBounds);
	}
	
	private double scale = 1.0d;
	private double oldScale = scale;
	
	/**
	 * 
	 * @param scale the scale of the Graphics
	 */
	public void setScale(double scale) {
		oldScale = this.scale;
		this.scale = scale;
		setZoomedRealBounds(scale);
	}
	
	/**
	 * @return the scale of the Graphics
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * @see ICanvas#translateX(float)
	 */
	public void translateX(float translateX) {
		setViewLocation((int)(viewBounds.x + translateX), viewBounds.y);
	}
	
	/**
	 * @see ICanvas#translateY(float)
	 */
	public void translateY(float translateY) {
		setViewLocation(viewBounds.x, (int)(viewBounds.y + translateY));
	}
	
	private Color bgColor = Color.WHITE;
	public void setBackground(int red, int green, int blue) {
		bgColor = new Color(red, green, blue);
		notifyChange();
	}
							
	private Rectangle realBounds;
	
	/**
	 * @return the realBounds which determine the whole area which
	 * can be displayed by the DisplayPanel
	 */
	public Rectangle getRealBounds() {
		return realBounds;
	}
	public void setRealBounds(Rectangle realBounds)
	{
		Rectangle oldReal = this.realBounds;
		checkScale(realBounds);
		scaleToCenter();
		firePropertyChange(REAL_CHANGE, oldReal, this.realBounds);
		setViewBounds(getViewBounds());
		notifyChange();
	}
	
	/**
	 * sets the viewLocation so that the scale is performed into the center of the
	 * visibleArea
	 *
	 */
	protected void scaleToCenter()
	{
		int newViewX = (int) (((viewBounds.x) / oldScale) * scale);
		int newViewY = (int) (((viewBounds.y) / oldScale) * scale);
				
		if (debugBounds) {
			logger.debug("newViewX = "+newViewX); //$NON-NLS-1$
			logger.debug("newViewY = "+newViewY); //$NON-NLS-1$
		}
		
		setViewLocation(newViewX, newViewY);
	}
	
	private Rectangle viewBounds;
	
	/**
	 * 
	 * @return the visible area of the IViewport
	 */
	public Rectangle getViewBounds() {
		return viewBounds;
	}
	
	/**
	 * @see org.nightlabs.editor2d.viewer.ui.IViewport#getViewLocation()
	 */
	public Point2D getViewLocation()
	{
		return getViewBounds().getLocation();
	}
	
	/**
	 * sets the visible area of the IViewport
	 * @param viewBounds the new Visible Area
	 */
	public void setViewBounds(Rectangle viewBounds)
	{
		if (viewBounds == null)
			throw new IllegalArgumentException("Param viewBounds must not be null!"); //$NON-NLS-1$
							
		Rectangle oldView = this.viewBounds;
		
		if (isRectangleInReal(viewBounds))
			this.viewBounds = viewBounds;
		else {
			if (viewBounds.contains(realBounds))
				this.realBounds = new Rectangle(viewBounds);
			
			this.viewBounds = GeomUtil.checkBounds(viewBounds, realBounds);
		}
		
		firePropertyChange(VIEW_CHANGE, oldView, this.viewBounds);
		checkBuffer();
		repaint();
				
		if (debugBounds) {
			logger.debug("realBounds = " + realBounds); //$NON-NLS-1$
			logger.debug("bufferBounds = " + bufferBounds); //$NON-NLS-1$
			logger.debug("viewBounds = "+viewBounds); //$NON-NLS-1$
			logger.debug(""); //$NON-NLS-1$
		}
	}
		
	private Rectangle initRealBounds;
	public Rectangle getInitRealBounds() {
		return initRealBounds;
	}
	private Rectangle initViewBounds;
	public Rectangle getInitViewBounds() {
		return initViewBounds;
	}
	
	protected void init(Rectangle realBounds)
	{
		initRealBounds = new Rectangle(realBounds);
		initViewBounds = getVisibleRect();
		
		this.realBounds = realBounds;
		viewBounds = getVisibleRect();
		initBuffer();
		paintDrawComponent();
		addComponentListener(resizeListener);
				
		if (debugBounds) {
			logger.debug("realBounds = "+realBounds); //$NON-NLS-1$
			logger.debug("viewBounds = "+viewBounds);			 //$NON-NLS-1$
		}
	}
	
	protected void initBuffer()
	{
		bufferBounds = new Rectangle(0, 0, initSize, initSize);
		bufferedImage = new BufferedImage(bufferBounds.width, bufferBounds.height, imageType);
	}
	
	private ComponentListener resizeListener = new ComponentAdapter()
	{
		@Override
		public void componentResized(ComponentEvent e)
		{
			initViewBounds = getVisibleRect();
			Rectangle newView = new Rectangle(viewBounds.x, viewBounds.y,
					initViewBounds.width, initViewBounds.height);
 			
			if (!newView.equals(viewBounds))
			{
				setViewBounds(newView);
				if (debugBounds) {
					logger.debug("Viewport resized!"); //$NON-NLS-1$
					logger.debug("viewBounds = "+viewBounds); //$NON-NLS-1$
					logger.debug("getVisibleRect() = "+getVisibleRect()); //$NON-NLS-1$
					logger.debug("getBounds() = "+getBounds());					 //$NON-NLS-1$
				}
			}
		}
	};
	
	private boolean isChanged = false;
	
	/**
	 * notifys that a new painting has to occur, and that
	 * the buffer must be cleared
	 */
	public void notifyChange()
	{
		isChanged = true;
		repaint();
		
		logger.debug("notifyChange!"); //$NON-NLS-1$
	}
	
	/**
	 * paints the DrawComponent into the Graphics of the BufferedImage
	 */
	protected void paintDrawComponent()
	{
		long startTime = 0;
		if (debugPaint)
			startTime = System.currentTimeMillis();
		
		if (bufferedImage != null)
		{
			Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
			g2d.setRenderingHints(renderingHintsManager.getRenderingHints());
			g2d.setBackground(bgColor);
			g2d.setPaint(bgColor);
			g2d.translate(-bufferBounds.x, -bufferBounds.y);
			
			g2d.setClip(bufferBounds);
			g2d.fillRect(bufferBounds.x, bufferBounds.y, bufferBounds.width, bufferBounds.height);
							
			if (debugPaint)
			{
				drawRealRectangle(g2d);
				drawBufferRectangle(g2d);
				drawViewRectangle(g2d);
			}
			
			g2d.scale(scale, scale);
			DrawComponentPaintable.paintDrawComponent(dc, g2d);
		}
		
		if (debugPaint)
		{
			long endTime = System.currentTimeMillis() - startTime;
			logger.debug("paintDrawComponent took "+endTime+" ms!"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	// for test purposes only
	private void drawViewRectangle(Graphics2D g2)
	{
		if (viewBounds != null)
		{
			int distance = 10;
			g2.setPaint(java.awt.Color.YELLOW);
			g2.setStroke(new BasicStroke(2));
			g2.drawRect(viewBounds.x+distance,
					viewBounds.y+distance,
					viewBounds.width-distance,
					viewBounds.height-distance);
		}
	}

	// for test purposes only
	private void drawBufferRectangle(Graphics2D g2)
	{
		if (bufferBounds != null)
		{
			int distance = 10;
			g2.setPaint(java.awt.Color.GREEN);
			g2.setStroke(new BasicStroke(4));
			g2.drawRect(bufferBounds.x+distance,
					bufferBounds.y+distance,
					bufferBounds.width-distance,
					bufferBounds.height-distance);
		}
	}

	// for test purposes only
	private void drawRealRectangle(Graphics2D g2)
	{
		if (bufferBounds != null)
		{
			int distance = 10;
			g2.setPaint(java.awt.Color.BLUE);
			g2.setStroke(new BasicStroke(6));
			g2.drawRect(realBounds.x+distance,
					realBounds.y+distance,
					realBounds.width-distance,
					realBounds.height-distance);
		}
	}
	
	/**
	 * 
	 * @return a Rectangle which determines the Size of the BufferedImage to create
	 * If it fits the size of the BufferedImage is viewBounds * bufferScaleFactor
	 */
	protected Rectangle getBufferRectangle()
	{
		// TODO: should be checked if width + height < 0
		double bufferScaleFactor = BufferManager.sharedInstance().getBufferScaleFactor();
		int bufferWidth = (int) (viewBounds.width * bufferScaleFactor);
		int bufferHeight = (int) (viewBounds.height * bufferScaleFactor);
		
		Rectangle newBufferBounds = new Rectangle(viewBounds.x - ((bufferWidth - viewBounds.width)/2),
				viewBounds.y - ((bufferHeight-viewBounds.height)/2), bufferWidth, bufferHeight);
		
		if (!isRectangleInReal(newBufferBounds)) {
			newBufferBounds = GeomUtil.checkBounds(newBufferBounds, realBounds);
		}
		newBufferBounds = checkOrigin(newBufferBounds);
				
		return newBufferBounds;
	}
	
	protected Rectangle checkOrigin(Rectangle r)
	{
		if (r.x < 0 || r.y < 0) {
			Rectangle newRect = new Rectangle(r);
			if (r.x < 0)
				newRect.x = 0;
			if (r.y < 0)
				newRect.y = 0;
			
			return newRect;
		}
		return r;
	}
	
	/**
	 * checks if the View is still in the Buffer, if not a new BufferedImage
	 * is created and paintDrawComponent is painted into the Graphics of the
	 * new BufferedImage.
	 *
	 */
	protected void checkBuffer()
	{
		if (!isViewInBuffer())
		{
			if (bufferedImage != null)
				bufferedImage.flush();
			bufferedImage = null;
			createOffScreenImage();
			notifyChange();
			
			if (debugBounds)
				logger.debug("Buffer updated!");				 //$NON-NLS-1$
		}
	}
	
	/**
	 * 
	 * @return true if the viewBounds are contained in the bufferBounds, else false
	 */
	protected boolean isViewInBuffer()
	{
		if (bufferBounds != null && viewBounds != null)
			return bufferBounds.contains(viewBounds);
		else
			return false;
	}
	
	/**
	 * 
	 * @param r the Rectangle to check
	 * @return true if the given Rectangle is conatined in the realBounds, else false
	 */
	protected boolean isRectangleInReal(Rectangle r)
	{
		if (realBounds != null && r != null)
			return realBounds.contains(r);
		else
			return false;
	}
	
	/**
	 * creates the Offscreen Image.
	 * The Size of the BufferedImage is the viewBounds * bufferScaleFactor
	 *
	 */
	protected void createOffScreenImage()
	{
		bufferBounds = getBufferRectangle();
		if (logger.isDebugEnabled()) {
			logger.debug("bufferBounds = "+bufferBounds); //$NON-NLS-1$
		}
		
		if (bufferBounds.width <= 0)
			bufferBounds.width = 1;
		if (bufferBounds.height <= 0)
			bufferBounds.height = 1;
		
//		bufferedImage.flush();
		bufferedImage = new BufferedImage(bufferBounds.width, bufferBounds.height, imageType);
	}
	
	public int getOffsetX() {
		return viewBounds.x - realBounds.x;
	}
	
	public int getOffsetY() {
		return viewBounds.y - realBounds.y;
	}
	
	protected int getBufferOffsetX() {
		return viewBounds.x - bufferBounds.x;
	}
	
	protected int getBufferOffsetY() {
		return viewBounds.y - bufferBounds.y;
	}
	
	public void setViewLocation(int x, int y) {
		setViewBounds(new Rectangle(x, y, viewBounds.width, viewBounds.height));
	}
	
	public void setViewLocation(Point2D p) {
		setViewLocation((int)p.getX(), (int)p.getY());
	}
	
	public void setViewCenter(float x, float y)
	{
		Rectangle newView = new Rectangle();
		newView.setFrameFromCenter(x, y,
															 x + viewBounds.getWidth() / 2,
															 y + viewBounds.getHeight() / 2);
		setViewBounds(newView);
	}
	
	public Point2D getViewCenter()
	{
		double viewCenterX = viewBounds.getCenterX();
		double viewCenterY = viewBounds.getCenterY();
		return new Point2D.Float((float)viewCenterX, (float)viewCenterY);
	}
		
	protected void checkScale(Rectangle newReal)
	{
		int maxRealX = (int)newReal.getMaxX();
		int maxRealY = (int)newReal.getMaxY();
		int maxViewX = (int)initViewBounds.getMaxX();
		int maxViewY = (int)initViewBounds.getMaxY();
		int maxX = Math.max(maxRealX, maxViewX);
		int maxY = Math.max(maxRealY, maxViewY);
		realBounds = new Rectangle(0, 0, maxX, maxY);
//		realBounds = new Rectangle(newReal.x, newReal.y, maxX, maxY);
		
		if (debugBounds) {
			logger.debug("viewBounds = "+viewBounds); //$NON-NLS-1$
			logger.debug("realBounds = "+realBounds); //$NON-NLS-1$
			logger.debug("initViewBounds = "+initViewBounds); //$NON-NLS-1$
		}
	}
		
	private boolean drawTempContent = true;
	public void drawTempContent(Graphics2D g2d)
	{
		for (Iterator it = getTempContentManager().getTempContent().iterator(); it.hasNext(); )
		{
			Object o = it.next();
			if (o instanceof DrawComponent) {
				DrawComponent dc = (DrawComponent) o;
				Renderer r = null;
				if (dc.getRoot() != null) {
					r = dc.getRenderer();
				}
				if (r == null) {
					String renderMode = dc.getRenderMode();
//					r = getDrawComponent().getRenderModeManager().getRenderer(renderMode, dc.getClass());
					r = getDrawComponent().getRenderModeManager().getRenderer(renderMode, dc.getClass().getName());
				}
				RenderUtil.paintJ2DRenderer(r, dc, g2d);
			}
			else if (o instanceof JToolTip)
			{
				// TODO: find out why Tooltips are not painted at given location
				JToolTip tooltip = (JToolTip) o;
				tooltip.setComponent(this);
				tooltip.paint(g2d);
								
				logger.debug("TooltTip painted!"); //$NON-NLS-1$
				logger.debug("TooltTip Location = "+tooltip.getLocation()); //$NON-NLS-1$
			}
			else if (o instanceof Component) {
				Component c = (Component) o;
				c.paint(g2d);
			}
		}
	}
		
	private ITempContentManager tempContentMan = new TempContentManager();
	public ITempContentManager getTempContentManager() {
		return tempContentMan;
	}
	
	public void dispose()
	{
		if (viewImage != null)
			viewImage.flush();
		if (bufferedImage != null)
			bufferedImage.flush();
		viewImage = null;
		bufferedImage = null;
		if (logger.isDebugEnabled()) {
			logger.debug("dispose!"); //$NON-NLS-1$
		}
	}

}
