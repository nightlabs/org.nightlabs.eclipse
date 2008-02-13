package org.nightlabs.editor2d.viewer.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.util.GeomUtil;
import org.nightlabs.editor2d.util.RenderUtil;
import org.nightlabs.editor2d.util.RenderingHintsManager;

public abstract class AbstractBufferedCanvas
extends JPanel
implements IBufferedCanvas, IViewport
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(AbstractBufferedCanvas.class);
	
	protected ITempContentManager tempContentManager = null;
	public ITempContentManager getTempContentManager()
	{
		if (tempContentManager == null)
			tempContentManager = new TempContentManager();
				
		return tempContentManager;
	}

	protected PropertyChangeSupport pcs = null;
	protected PropertyChangeSupport getPropertyChangeSupport()
	{
		if (pcs == null) {
			pcs = new PropertyChangeSupport(this);
		}
		return pcs;
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		getPropertyChangeSupport().addPropertyChangeListener(pcl);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		getPropertyChangeSupport().removePropertyChangeListener(pcl);
	}

	@Override
	public void firePropertyChange(String propertyName, Object newVal, Object oldVal) {
		getPropertyChangeSupport().firePropertyChange(propertyName, oldVal, newVal);
	}
	
	public AbstractBufferedCanvas(DrawComponent dc)
	{
		super();
		this.dc = dc;
		Rectangle dcBounds = GeomUtil.translateToOriginAndAdjustSize(dc.getBounds());
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
	
	protected int imageType = BufferedImage.TYPE_INT_RGB;
	protected Rectangle bufferBounds;
	protected DrawComponent dc;
	protected BufferedImage bufferedImage;
	protected BufferedImage viewImage;
	protected int initSize = 100;
	protected RenderingHintsManager renderingHintsManager = RenderingHintsManager.sharedInstance();
	
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
		
		// fill the Background
		g2d.setPaint(bgColor);
		Rectangle bgRect = initRealBounds;
		g2d.setClip(0, 0, bgRect.width, bgRect.height);
		g2d.fillRect(0, 0, bgRect.width, bgRect.height);
		
		if (debugBounds)
			logger.debug("bgRect = "+bgRect); //$NON-NLS-1$
					
		if (bufferedImage != null)
		{
			long startTime = 0;
			if (debugPaint)
				startTime = System.currentTimeMillis();
									
			// Do a BitBlock Transfer
			calcBufferedViewImage();
			g2d.drawImage(viewImage, 0, 0, null);
						
			if (debugPaint) {
				logger.debug("bgColor = "+bgColor); //$NON-NLS-1$
				logger.debug("BitBlockTransfer took "+(System.currentTimeMillis()-startTime)+" ms!");				 //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			if (debugBounds) {
				logger.debug("viewImage width = "+viewImage.getWidth()); //$NON-NLS-1$
				logger.debug("viewImage height = "+viewImage.getHeight()); //$NON-NLS-1$
			}
		}
		
		// Draw Temporary Content above Buffer (e.g. SelectionRectangle etc.)
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
				
		if ((bufferedImage.getWidth() >= viewBounds.width + offsetX) &&
				(bufferedImage.getHeight() >= viewBounds.height + offsetY))
		{
			viewImage =
				bufferedImage.getSubimage(offsetX, offsetY,
																	viewBounds.width,
																	viewBounds.height);
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
		Rectangle newRealBounds = new Rectangle(0, 0, newWidth, newHeight);
		setRealBounds(newRealBounds);
	}
	
	protected double scale = 1.0d;
	protected double oldScale = scale;
	
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
	
	protected Color bgColor = Color.WHITE;
	public void setBackground(int red, int green, int blue, int alpha) {
		bgColor = new Color(red, green, blue, alpha);
		notifyChange();
	}
							
	protected Rectangle realBounds;
	
	/**
	 * 
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
//		int newViewX = (int) (((double)viewBounds.x) * scale);
//		int newViewY = (int) (((double)viewBounds.y) * scale);
				
		if (debugBounds) {
			logger.debug("newViewX = "+newViewX); //$NON-NLS-1$
			logger.debug("newViewY = "+newViewY); //$NON-NLS-1$
		}
		
		setViewLocation(newViewX, newViewY);
	}
	
	protected Rectangle viewBounds;
	
	/**
	 * 
	 * @return the visible area of the IViewport
	 */
	public Rectangle getViewBounds() {
		return viewBounds;
	}
	
	/**
	 * @see IViewport#getViewLocation()
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
		Rectangle oldView = this.viewBounds;
		
		if (isRectangleInReal(viewBounds))
			this.viewBounds = viewBounds;
		else
			this.viewBounds = GeomUtil.checkBounds(viewBounds, realBounds);
		
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
	
	protected Rectangle initRealBounds;
	protected Rectangle initViewBounds;
	
	protected void init(Rectangle realBounds)
	{
		initRealBounds = new Rectangle(realBounds);
		initViewBounds = getVisibleRect();
		
		this.realBounds = realBounds;
		viewBounds = getVisibleRect();
		initBuffer();
		paintDrawComponent();
//		addComponentListener(resizeListener);
				
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
		
	protected boolean isChanged = false;
	
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
			g2d.translate(-bufferBounds.x, -bufferBounds.y);
			
			g2d.setClip(bufferBounds);
			g2d.fillRect(bufferBounds.x, bufferBounds.y, bufferBounds.width, bufferBounds.height);
										
			g2d.scale(scale, scale);
			DrawComponentPaintable.paintDrawComponent(dc, g2d);
		}
		
		if (debugPaint)
		{
			long endTime = System.currentTimeMillis() - startTime;
			logger.debug("paintDrawComponent took "+endTime+" ms!"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * 
	 * @return a Rectangle which determines the Size of the BufferedImage to create
	 * If it fits the size of the BufferedImage is viewBounds * bufferScaleFactor
	 */
	protected Rectangle getBufferRectangle()
	{
		double bufferScaleFactor = BufferManager.sharedInstance().getBufferScaleFactor();
		int bufferWidth = (int) (viewBounds.width * bufferScaleFactor);
		int bufferHeight = (int) (viewBounds.height * bufferScaleFactor);
		
		Rectangle newBufferBounds = new Rectangle(viewBounds.x - ((bufferWidth - viewBounds.width)/2),
				viewBounds.y - ((bufferHeight-viewBounds.height)/2), bufferWidth, bufferHeight);
		
		if (isRectangleInReal(newBufferBounds))
			return newBufferBounds;
		else
			return GeomUtil.checkBounds(newBufferBounds, realBounds);
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
		
		if (debugBounds) {
			logger.debug("viewBounds = "+viewBounds); //$NON-NLS-1$
			logger.debug("realBounds = "+realBounds); //$NON-NLS-1$
			logger.debug("initViewBounds = "+initViewBounds); //$NON-NLS-1$
		}
	}
	
	protected boolean drawTempContent = true;
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
		}
	}
		
	@Override
	public abstract Rectangle getVisibleRect();
	
	protected void componentResized()
	{
		Rectangle visibleRect = getVisibleRect();
		initViewBounds = getVisibleRect();
		Rectangle newView = new Rectangle(viewBounds.x, viewBounds.y,
				visibleRect.width, visibleRect.height);
			
		if (!newView.equals(viewBounds))
		{
			setViewBounds(newView);
			if (debugBounds) {
				logger.debug("Viewport resized!"); //$NON-NLS-1$
				logger.debug("viewBounds = "+viewBounds); //$NON-NLS-1$
			}
		}
	}
	
}
