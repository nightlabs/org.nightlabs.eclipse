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

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.nightlabs.eclipse.ui.pdfviewer.PdfDocument;

/**
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfViewerComposite extends Composite {
	private static final Logger logger = Logger.getLogger(PdfViewerComposite.class);

	/**
	 * Since the int range of the scroll bars is limited and we don't need to be able to scroll to every single
	 * coordinate value, we reduce the granularity by this divisor. This means, scrolling the real coordinate system
	 * by 200 dots will move the scroll bar's selection-value by 20 (200 / scrollBarDivisor).
	 */
	private static final int scrollBarDivisor = 10;

	private Composite renderComposite;
	private RenderBuffer renderBuffer;
	private RenderThread renderThread;
	private PdfDocument pdfDocument;
	private ScrollBar scrollBarVertical, scrollBarHorizontal;

	/**
	 * The real coordinates of the view area's left, top corner.
	 */
	private Point viewOrigin;

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

	public PdfViewerComposite(Composite parent, final PdfDocument pdfDocument)
	{
		super(parent, SWT.NONE);
		this.setLayout(new FillLayout());
		this.pdfDocument = pdfDocument;

		renderBuffer = new RenderBuffer(this, pdfDocument);
		renderComposite = new Composite(this, SWT.EMBEDDED | SWT.V_SCROLL | SWT.H_SCROLL);

		viewOrigin = new Point(0, 0);

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
				if (centerHorizontally) {
					double middleX = pdfDocument.getDocumentDimension().getWidth() / 2;
					viewOrigin.x = (int) (middleX - viewPanel.getWidth() / 2 / ((double)zoomFactorPerMill / 1000));
				}

				if (centerVertically) {
					double middleY = pdfDocument.getDocumentDimension().getHeight() / 2;
					viewOrigin.y = (int) (middleY - viewPanel.getHeight() / 2 / ((double)zoomFactorPerMill / 1000));
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

		setScrollbars();

		renderThread = new RenderThread(this, renderBuffer);
	}

	private boolean centerHorizontally;
	private boolean centerVertically;

	private void scrollVertically() {
		viewOrigin.y = scrollBarVertical.getSelection() * scrollBarDivisor;

		if (logger.isDebugEnabled()) {
			logger.debug("scrollVertically: scrollBarVerticalSelectionNew=" + scrollBarVertical.getSelection());
			logger.debug("scrollVertically: new viewOrigin.y=" + viewOrigin.y);
			logger.debug("scrollVertically: bottomRealY=" + (viewOrigin.y + viewPanel.getHeight() / ((double)zoomFactorPerMill / 1000)));
		}

		centerVertically = false;

		viewPanel.repaint();
	}

	private void scrollHorizontally() {
		viewOrigin.x = scrollBarHorizontal.getSelection() * scrollBarDivisor;

		if (logger.isDebugEnabled())
			logger.debug("scrollHorizontally: new viewOrigin.x=" + viewOrigin.x);

		centerHorizontally = false;

		viewPanel.repaint();
	}

	private void setScrollbars() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				double zoomFactor = (zoomFactorPerMill / 1000d);

				int visibleAreaScrollHeight = (int) (getViewPanel().getHeight() / zoomFactor) / scrollBarDivisor;
				scrollBarVertical.setMinimum(0);
				scrollBarVertical.setMaximum((int) pdfDocument.getDocumentDimension().getHeight() / scrollBarDivisor);
				scrollBarVertical.setSelection(viewOrigin.y / scrollBarDivisor);
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
				scrollBarHorizontal.setSelection(viewOrigin.x / scrollBarDivisor);
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
	private void paintViewPanel(Graphics2D g) {
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


	/**
	 * Scale all PDF pages of the currently opened PDF document
	 *
	 * @param mouseRotationOrientation the direction the user has scrolled into
	 */
	private void zoomPDFDocument(int mouseRotationOrientation) {
		int zoomBefore = zoomFactorPerMill;
		int zoomAfter = zoomFactorPerMill;

		if (mouseRotationOrientation == 1)
			zoomAfter -= 100;
		else
			zoomAfter += 100;

		if (zoomAfter < 100)
			zoomAfter = 100;

		if (zoomAfter > 10000)
			zoomAfter = 10000;

		if (zoomBefore == zoomAfter)
			return;

		zoomFactorPerMill = zoomAfter;

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
		viewOrigin.x = (int) (middle.x - viewPanelBoundsReal.x / 2);
		viewOrigin.y = (int) (middle.y - viewPanelBoundsReal.y / 2);

		if (logger.isDebugEnabled())
			logger.debug("zoomPDFDocument: zoomFactor=" + zoomFactor + " viewOrigin.x=" + viewOrigin.x + " viewOrigin.y=" + viewOrigin.y);

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				setScrollbars();
			}
		});

		viewPanel.repaint();
	}

	public Point getViewOrigin() {
		return viewOrigin;
	}

	public void setViewOrigin(Point rectangleViewOrigin) {
		this.viewOrigin = rectangleViewOrigin;
		this.setScrollbars();
		viewPanel.repaint();
		// TODO test this method! and modify if necessary.
	}

	public int getZoomFactorPerMill() {
		return zoomFactorPerMill;
	}
}
