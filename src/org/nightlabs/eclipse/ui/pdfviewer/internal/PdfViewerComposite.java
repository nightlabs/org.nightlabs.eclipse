package org.nightlabs.eclipse.ui.pdfviewer.internal;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.nightlabs.eclipse.ui.pdfviewer.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;

/**
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfViewerComposite extends Composite
{
	private static final Logger logger = Logger.getLogger(PdfViewerComposite.class);

	/**
	 * Since the int range of the scroll bars is limited and we don't need to be able to scroll to every single
	 * coordinate value, we reduce the granularity by this divisor. This means, scrolling the real coordinate system
	 * by 200 dots will move the scroll bar's selection-value by 20 (200 / scrollBarDivisor).
	 */
	private static final int scrollBarDivisor = 10;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private Composite renderComposite;
	private RenderBuffer renderBuffer;
	private RenderThread renderThread;
	private PdfDocument pdfDocument;
	private ScrollBar scrollBarVertical, scrollBarHorizontal;

	/**
	 * The real coordinates of the view area's left, top corner.
	 */
	private Point2D.Double viewOrigin;

	/**
	 * The zoom factor in %o (1/1000).
	 */
	private int zoomFactorPerMill = 1000;
	private boolean wantToZoom = false;
	/**
	 * The AWT frame for this composite.
	 */
	private Frame viewPanelFrame;
	/**
	 * The panel within {@link #viewPanelFrame}.
	 */
	private Panel viewPanel;

	// http://forums.sun.com/thread.jspa?messageID=3369196
	// http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg24617.html
	static {
		System.setProperty("sun.awt.noerasebackground", "true");
	}

	public void setPdfDocument(PdfDocument pdfDocument)
	{
		if (renderThread != null) {
			long start = System.currentTimeMillis();
			while (renderThread.isAlive()) {
				if (System.currentTimeMillis() - start > 60000)
					throw new IllegalStateException("Timeout waiting for RenderThread to finish!");

				renderThread.interrupt();
				try {
					renderThread.join(10000);
				} catch (InterruptedException e) {
					// ignore
				}
			}
			renderThread = null;
		}

		if (renderBuffer != null) {
			renderBuffer = null;
		}

		this.pdfDocument = pdfDocument;

		if (pdfDocument != null) {
			renderBuffer = new RenderBuffer(this, pdfDocument);
			renderThread = new RenderThread(this, renderBuffer);
		}
		setScrollbars();
	}


	public PdfViewerComposite(Composite parent)
	{
		super(parent, SWT.NONE);
		this.setLayout(new FillLayout());

//		renderBuffer = new RenderBuffer(this, pdfDocument);
		renderComposite = new Composite(this, SWT.EMBEDDED | SWT.V_SCROLL | SWT.H_SCROLL);

		viewOrigin = new Point2D.Double();

		scrollBarVertical = renderComposite.getVerticalBar();
		scrollBarHorizontal = renderComposite.getHorizontalBar();
		viewPanelFrame = SWT_AWT.new_Frame(renderComposite);

		viewPanel = new Panel() {
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g) {
				paintViewPanel((Graphics2D) g);
			}
		};

		viewPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (pdfDocument == null)
					return;

				if (centerHorizontally) {
					double middleX = pdfDocument.getDocumentDimension().getWidth() / 2;
					viewOrigin.x = middleX - viewPanel.getWidth() / 2 / ((double)zoomFactorPerMill / 1000);
				}

				if (centerVertically) {
					double middleY = pdfDocument.getDocumentDimension().getHeight() / 2;
					viewOrigin.y = middleY - viewPanel.getHeight() / 2 / ((double)zoomFactorPerMill / 1000);
				}

				setScrollbars();
				viewPanel.repaint();
			}
			@Override
			public void componentShown(ComponentEvent e) {

			}
		});
		viewPanelFrame.add(viewPanel);

		scrollBarHorizontal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				wantToZoom = false;
				scrollHorizontally();
			}
		});

		scrollBarVertical.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				wantToZoom = false;
				scrollVertically();
			}
		});

		viewPanelFrame.addMouseWheelListener(new MouseWheelListenerImpl());
		viewPanelFrame.addKeyListener(new KeyListenerImpl());

//		renderThread = new RenderThread(this, renderBuffer);
	}

	private boolean centerHorizontally;
	private boolean centerVertically;

	private void scrollVertically()
	{
		Point2D.Double oldViewOrigin = new Point2D.Double();
		oldViewOrigin.setLocation(this.viewOrigin);

		viewOrigin.y = scrollBarVertical.getSelection() * scrollBarDivisor;

		if (logger.isDebugEnabled()) {
			logger.debug("scrollVertically: scrollBarVerticalSelectionNew=" + scrollBarVertical.getSelection());
			logger.debug("scrollVertically: new viewOrigin.y=" + viewOrigin.y);
			logger.debug("scrollVertically: bottomRealY=" + (viewOrigin.y + viewPanel.getHeight() / ((double)zoomFactorPerMill / 1000)));
		}

		centerVertically = false;

		viewPanel.repaint();
		propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_VIEW_ORIGIN, oldViewOrigin, viewOrigin);
	}

	private void scrollHorizontally()
	{
		Point2D.Double oldViewOrigin = new Point2D.Double();
		oldViewOrigin.setLocation(this.viewOrigin);

		viewOrigin.x = scrollBarHorizontal.getSelection() * scrollBarDivisor;

		if (logger.isDebugEnabled())
			logger.debug("scrollHorizontally: new viewOrigin.x=" + viewOrigin.x);

		centerHorizontally = false;

		viewPanel.repaint();
		propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_VIEW_ORIGIN, oldViewOrigin, viewOrigin);
	}

	private void setScrollbars() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if (pdfDocument == null) {
					scrollBarVertical.setVisible(false);
					scrollBarHorizontal.setVisible(false);
					return;
				}

				double zoomFactor = (zoomFactorPerMill / 1000d);

				int visibleAreaScrollHeight = (int) (getViewPanel().getHeight() / zoomFactor) / scrollBarDivisor;
				scrollBarVertical.setMinimum(0);
				scrollBarVertical.setMaximum((int) pdfDocument.getDocumentDimension().getHeight() / scrollBarDivisor);
				scrollBarVertical.setSelection((int) (viewOrigin.y / scrollBarDivisor));
				boolean verticalBarVisible = visibleAreaScrollHeight <= (scrollBarVertical.getMaximum() - scrollBarVertical.getMinimum());
				scrollBarVertical.setVisible(verticalBarVisible);
				if (!verticalBarVisible)
					centerVertically = true;

				scrollBarVertical.setThumb(visibleAreaScrollHeight);
				scrollBarVertical.setPageIncrement((int) (visibleAreaScrollHeight * 0.9d));
				scrollBarVertical.setIncrement((int) (visibleAreaScrollHeight * 0.1d));

				int visibleAreaScrollWidth = (int) (getViewPanel().getWidth() / zoomFactor) / scrollBarDivisor;
				scrollBarHorizontal.setMinimum(0);
				scrollBarHorizontal.setMaximum((int) pdfDocument.getDocumentDimension().getWidth() / scrollBarDivisor);
				scrollBarHorizontal.setSelection((int) (viewOrigin.x / scrollBarDivisor));
				boolean horizontalBarVisible = visibleAreaScrollWidth <= (scrollBarHorizontal.getMaximum() - scrollBarHorizontal.getMinimum());
				scrollBarHorizontal.setVisible(horizontalBarVisible);
				if (!horizontalBarVisible)
					centerHorizontally = true;

				scrollBarHorizontal.setThumb(visibleAreaScrollWidth);
				scrollBarHorizontal.setPageIncrement((int) (visibleAreaScrollWidth * 0.9d));
				scrollBarHorizontal.setIncrement((int) (visibleAreaScrollWidth * 0.1d));

				if (logger.isDebugEnabled()) {
					logger.debug("setScrollbars: scrollBarVertical.minimum=" + scrollBarVertical.getMinimum());
					logger.debug("setScrollbars: scrollBarVertical.maximum=" + scrollBarVertical.getMaximum());
					logger.debug("setScrollbars: scrollBarVertical.thumb=" + scrollBarVertical.getThumb());
					logger.debug("setScrollbars: scrollBarVertical.size.x=" + scrollBarVertical.getSize().x);
					logger.debug("setScrollbars: scrollBarVertical.size.y=" + scrollBarVertical.getSize().y);
					logger.debug("setScrollbars: scrollBarVertical.selection=" + scrollBarVertical.getSelection());
					logger.debug("setScrollbars: centerHorizontally=" + centerHorizontally);

					logger.debug("setScrollbars: scrollBarHorizontal.minimum=" + scrollBarHorizontal.getMinimum());
					logger.debug("setScrollbars: scrollBarHorizontal.maximum=" + scrollBarHorizontal.getMaximum());
					logger.debug("setScrollbars: scrollBarHorizontal.thumb=" + scrollBarHorizontal.getThumb());
					logger.debug("setScrollbars: scrollBarHorizontal.size.x=" + scrollBarHorizontal.getSize().x);
					logger.debug("setScrollbars: scrollBarHorizontal.size.y=" + scrollBarHorizontal.getSize().y);
					logger.debug("setScrollbars: scrollBarHorizontal.selection=" + scrollBarHorizontal.getSelection());
					logger.debug("setScrollbars: centerVertically=" + centerVertically);
				}
			}
		});
	}

	/**
	 * Paint the {@link #viewPanel} whenever it needs repaint.
	 *
	 * @param g the graphics to draw into.
	 */
	private void paintViewPanel(Graphics2D g)
	{
		if (renderBuffer == null || renderThread == null) {
			g.setColor(getViewPanel().getBackground());
			g.fillRect(0, 0, getViewPanel().getWidth(), getViewPanel().getHeight());
			return;
		}

		double zoomFactor = (double)zoomFactorPerMill / 1000;

		Rectangle2D.Double region = new Rectangle2D.Double(
				viewOrigin.x,
				viewOrigin.y,
				viewPanel.getWidth() / zoomFactor,
				viewPanel.getHeight() / zoomFactor
		);

		if (logger.isDebugEnabled()) {
			logger.debug("paintViewPanel: zoomFactor=" + zoomFactor + " viewOrigin.x=" + viewOrigin.x + " viewOrigin.y=" + viewOrigin.y);
			logger.debug("paintViewPanel: viewPanel.width = " + viewPanel.getWidth());
			logger.debug("paintViewPanel: viewPanel.height = " + viewPanel.getHeight());
			logger.debug("paintViewPanel: region = " + region);
		}

		boolean bufferSufficient = renderBuffer.drawRegion(
				g,
				viewPanel.getWidth(),
				viewPanel.getHeight(),
				(double)zoomFactorPerMill / 1000,
				region
		);

		if (!bufferSufficient) {
			synchronized (renderThread) {
				renderThread.notifyAll();
			}
		}
	}

	public Panel getViewPanel() {
		return viewPanel;
	}

	private class MouseWheelListenerImpl implements MouseWheelListener {
		int mouseRotationOrientation;
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() < 0)
				mouseRotationOrientation = - 1;
			else
				mouseRotationOrientation = 1;

			if (wantToZoom == true)
				zoomPDFDocument(mouseRotationOrientation);
			else {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (scrollBarVertical.isVisible() == true) {	// vertical scroll bar has priority if visible
							scrollBarVertical.setSelection(scrollBarVertical.getSelection() + mouseRotationOrientation * scrollBarVertical.getIncrement());
							scrollVertically();
						}
						else {
							if (scrollBarHorizontal.isVisible() == true) {
								scrollBarHorizontal.setSelection(scrollBarHorizontal.getSelection() + mouseRotationOrientation * scrollBarHorizontal.getIncrement());
								scrollHorizontally();
							}
						}
					}
				});
			}
		}
	}

	private class KeyListenerImpl implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.isControlDown()) {
				wantToZoom = true;
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			wantToZoom = false;
		}
		@Override
		public void keyTyped(KeyEvent e) {
		}

	}

	private static final int ZOOM_MIN = 100;
	private static final int ZOOM_MAX = 10000;

	/**
	 * Scale all PDF pages of the currently opened PDF document
	 *
	 * @param mouseRotationOrientation the direction the user has scrolled into
	 */
	private void zoomPDFDocument(int mouseRotationOrientation) {
		int _zoomAfter = zoomFactorPerMill;

		if (mouseRotationOrientation == 1)
			_zoomAfter -= 100;
		else
			_zoomAfter += 100;

		if (_zoomAfter < ZOOM_MIN)
			_zoomAfter = ZOOM_MIN;

		if (_zoomAfter > ZOOM_MAX)
			_zoomAfter = ZOOM_MAX;

		if (zoomFactorPerMill == _zoomAfter)
			return;

		final int zoomAfter = _zoomAfter;

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				setZoomFactorPerMill(zoomAfter);
			}
		});
	}

	public Point2D getViewOrigin() {
		return viewOrigin;
	}

	public void setViewOrigin(Point2D newViewOrigin) {
		Point2D.Double oldViewOrigin = new Point2D.Double();
		oldViewOrigin.setLocation(this.viewOrigin);

		this.viewOrigin.setLocation(newViewOrigin);
		this.setScrollbars();
		viewPanel.repaint();
		// TODO test this method! and modify if necessary.

		propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_VIEW_ORIGIN, oldViewOrigin, this.viewOrigin);
	}

	public int getZoomFactorPerMill() {
		return zoomFactorPerMill;
	}

	public void setZoomFactorPerMill(int zoomFactorPerMill)
	{
		int zoomBefore = this.zoomFactorPerMill;

		if (zoomFactorPerMill < ZOOM_MIN)
			zoomFactorPerMill = ZOOM_MIN;

		if (zoomFactorPerMill > ZOOM_MAX)
			zoomFactorPerMill = ZOOM_MAX;

		if (this.zoomFactorPerMill == zoomFactorPerMill)
			return;

		this.zoomFactorPerMill = zoomFactorPerMill;

		// get the middle point BEFORE zooming
		Point2D.Double middle = new Point2D.Double();

		middle.x = (
				viewOrigin.x + viewOrigin.x + viewPanel.getWidth() / ((double)zoomBefore / 1000)
		) / 2;

		middle.y = (
				viewOrigin.y + viewOrigin.y + viewPanel.getHeight() / ((double)zoomBefore / 1000)
		) / 2;

		// calculate the new view origin AFTER zooming
		Point2D.Double viewPanelBoundsReal = new Point2D.Double();
		double zoomFactor = (double)zoomFactorPerMill / 1000;
		viewPanelBoundsReal.x = viewPanel.getWidth() / zoomFactor;
		viewPanelBoundsReal.y = viewPanel.getHeight() / zoomFactor;

		setViewOrigin(new Point2D.Double((int) (middle.x - viewPanelBoundsReal.x / 2), (int) (middle.y - viewPanelBoundsReal.y / 2)));

		if (logger.isDebugEnabled())
			logger.debug("zoomPDFDocument: zoomFactor=" + zoomFactor + " viewOrigin.x=" + viewOrigin.x + " viewOrigin.y=" + viewOrigin.y);

		propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_ZOOM_FACTOR, zoomBefore, this.zoomFactorPerMill);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
}
