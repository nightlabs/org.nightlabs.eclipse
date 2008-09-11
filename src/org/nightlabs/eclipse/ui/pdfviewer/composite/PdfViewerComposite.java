package org.nightlabs.eclipse.ui.pdfviewer.composite;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Toolkit;
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
import org.nightlabs.eclipse.ui.pdfviewer.composite.internal.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.composite.internal.RenderBuffer;
import org.nightlabs.eclipse.ui.pdfviewer.composite.internal.RenderThread;
import org.nightlabs.eclipse.ui.pdfviewer.util.Utilities;


public class PdfViewerComposite extends Composite {
	private static final Logger logger = Logger.getLogger(PdfViewerComposite.class);

	private static final int scrollingStepDistance = 10;
//	private Map<Integer, Double> zoomFactorsDownwards;
//	private Map<Integer, Double> zoomFactorsUpwards;
	private Composite renderComposite;
	private RenderBuffer renderBuffer;
	private RenderThread renderThread;
	private PdfDocument pdfDocument;
	private ScrollBar scrollBarVertical, scrollBarHorizontal;
//	private Scrollbar scrollBarVertical, scrollBarHorizontal;
//	private int scrollBarHorizontalSelectionOld, scrollBarVerticalSelectionOld;
	private int scrollBarHorizontalSelectionNew, scrollBarVerticalSelectionNew;
	private int scrollBarVerticalNumberOfSteps, scrollBarHorizontalNumberOfSteps;
	private Point rectangleViewOrigin;
	private Dimension screenSize;

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


	public PdfViewerComposite(Composite parent, final PdfDocument pdfDocument)
	{
		super(parent, SWT.NONE);
		this.setLayout(new FillLayout());
		this.pdfDocument = pdfDocument;
		renderBuffer = new RenderBuffer(this, pdfDocument);
		renderComposite = new Composite(this, SWT.EMBEDDED | SWT.V_SCROLL | SWT.H_SCROLL);

		rectangleViewOrigin = new Point(0, 0);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		setScreenSize(toolkit.getScreenSize());
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
				if (horizontallyCentered) {
					double middleX = pdfDocument.getDocumentBounds().getX() / 2;
					rectangleViewOrigin.x = (int) (middleX - viewPanel.getWidth() / 2 / ((double)zoomFactorPerMill / 1000));
				}
				setScrollbars();
				viewPanel.repaint();
			}
			@Override
			public void componentShown(ComponentEvent e) {

			}
		});
		viewPanelFrame.add(viewPanel);
		setViewPanel(viewPanel);

		scrollBarHorizontal.addSelectionListener(new SelectionAdapter() {
				@Override
		    	public void widgetSelected(SelectionEvent event) {
					scrollHorizontally((ScrollBar)event.widget);
		    	}
	    });

		scrollBarVertical.addSelectionListener(new SelectionAdapter() {
			@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		scrollVertically((ScrollBar)event.widget);
	    	}
	    });

/*		scrollBarHorizontal.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				// TODO Auto-generated method stub
				scrollHorizontally();
			}
		});

		scrollBarVertical.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				// TODO Auto-generated method stub
				scrollVertically();
			}
		});*/


		MouseWheelListener mouseWheelListener = new MouseWheelListenerImpl();
		viewPanelFrame.addMouseWheelListener(mouseWheelListener);

		KeyListener keyListener = new KeyListenerImpl();
		viewPanelFrame.addKeyListener(keyListener);

		setScrollbars();

		renderThread = new RenderThread(this, renderBuffer);
	}

	private boolean horizontallyCentered = true;

	private void scrollVertically (ScrollBar scrollBarVertical) {
		scrollBarVerticalSelectionNew = scrollBarVertical.getSelection();

		rectangleViewOrigin.y = (int) (scrollBarVerticalSelectionNew * scrollingStepDistance / ((double)zoomFactorPerMill / 1000));

		if (logger.isDebugEnabled()) {
			logger.debug("scrollVertically: scrollBarVerticalSelectionNew=" + scrollBarVerticalSelectionNew);
			logger.debug("scrollVertically: new rectangleViewOrigin.y=" + rectangleViewOrigin.y);
			logger.debug("scrollVertically: bottomRealY=" + (rectangleViewOrigin.y + viewPanel.getHeight() / ((double)zoomFactorPerMill / 1000)));
		}

//		logger.info("distance to beginning of main buffer: " + (rectangleViewOrigin.y - renderBuffer.getBufferedImageMainBounds().getY()));
//		logger.info("distance to end of main buffer: " + ((renderBuffer.getBufferedImageMainBounds().getY() + renderBuffer.getBufferedImageMainBounds().getHeight()) - (rectangleViewOrigin.y + viewPanel.getHeight())));
//		logger.info("scrollbar selection after scrolling: " + scrollBarVertical.getSelection());

		viewPanel.repaint();
	}

	private void scrollHorizontally(ScrollBar scrollBarHorizontal) {
		scrollBarHorizontalSelectionNew = scrollBarHorizontal.getSelection();
//		scrollBarHorizontalSelectionNew = scrollBarHorizontal.getValue();

		rectangleViewOrigin.x = (int) (scrollBarHorizontalSelectionNew * scrollingStepDistance / ((double)zoomFactorPerMill / 1000));

//		if (scrollBarHorizontalSelectionNew > scrollBarHorizontalSelectionOld) {
//			rectangleViewOrigin.x += (scrollBarHorizontalSelectionNew - scrollBarHorizontalSelectionOld) * scrollingStepDistance;
//			scrollBarHorizontalSelectionOld = scrollBarHorizontalSelectionNew;
//		}
//		else {
//			rectangleViewOrigin.x -= (scrollBarHorizontalSelectionOld - scrollBarHorizontalSelectionNew) * scrollingStepDistance;
//			scrollBarHorizontalSelectionOld = scrollBarHorizontalSelectionNew;
//		}

		if (logger.isDebugEnabled())
			logger.debug("scrollHorizontally: new rectangleViewOrigin.x=" + rectangleViewOrigin.x);

		horizontallyCentered = false;

		viewPanel.repaint();
	}

	private void setScrollbars() {
		if (logger.isDebugEnabled()) {
			logger.debug("setScrollbars: document bounds y-value: " + pdfDocument.getDocumentBounds().y + "; real zoom factor: " + (zoomFactorPerMill / 1000));
			logger.debug("setScrollbars: panel height: " + viewPanel.getHeight());
		}

		double realZoomFactor = (zoomFactorPerMill / 1000);
		int heightDifference = (int) (pdfDocument.getDocumentBounds().y * realZoomFactor - getViewPanel().getHeight());
		int widthDifference = (int) (pdfDocument.getDocumentBounds().x * realZoomFactor - getViewPanel().getWidth());

		if (heightDifference > 0) {
			scrollBarVerticalNumberOfSteps = heightDifference / scrollingStepDistance + 2;

			if (logger.isDebugEnabled())
				logger.debug("setScrollbars: steps of vertical scroll bar: " + scrollBarVerticalNumberOfSteps);

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (isDisposed())
						return;
					scrollBarVertical.setVisible(true);
					scrollBarVertical.setThumb(1);
					scrollBarVertical.setMaximum(scrollBarVerticalNumberOfSteps);
					scrollBarVertical.setSelection((int) (rectangleViewOrigin.y * ((double)zoomFactorPerMill / 1000) / scrollingStepDistance));

					if (logger.isDebugEnabled()) {
						logger.debug("setScrollbars: scrollBarVertical.maximum=" + scrollBarVertical.getMaximum());
						logger.debug("setScrollbars: scrollBarVertical.selection=" + scrollBarVertical.getSelection());
					}

//					if (documentWasZoomed) {
//						int newSelection = Utilities.doubleToIntRoundedDown(scrollBarVertical.getSelection() * zoomMultiplier);
////						int newSelection = Utilities.doubleToIntRoundedDown(scrollBarVertical.getValue() * zoomMultiplier);
//						Logger.getRootLogger().info("new scrollbar selection after zooming: " + newSelection);
//						scrollBarVertical.setSelection(newSelection);
////						scrollBarVertical.setValue(newSelection);
////						scrollBarVerticalSelectionOld = newSelection;
//						documentWasZoomed = false;
//					}
				}
			});
		}
		else {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (isDisposed())
						return;
					scrollBarVertical.setVisible(false);
				}
			});
		}

		if (widthDifference > 0) {
			scrollBarHorizontalNumberOfSteps = Utilities.doubleToInt((double)widthDifference / scrollingStepDistance);
//			Logger.getRootLogger().info("steps of horizontal scroll bar: " + scrollBarHorizontalNumberOfSteps);
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (isDisposed())
						return;
					scrollBarHorizontal.setVisible(true);
					scrollBarHorizontal.setThumb(1);
					scrollBarHorizontal.setMaximum(scrollBarHorizontalNumberOfSteps + 10);
					scrollBarHorizontal.setSelection((int) (rectangleViewOrigin.x * ((double)zoomFactorPerMill / 1000) / scrollingStepDistance));
//					if (startingPoint == true) {
//						scrollBarHorizontal.setSelection((scrollBarHorizontalNumberOfSteps + 10) / 2 - 5);
////						scrollBarHorizontal.setValue((scrollBarHorizontalNumberOfSteps + 10) / 2 - 5);
//						startingPoint = false;
//						scrollHorizontally(scrollBarHorizontal);
////						scrollHorizontally();
//					}
				}
			});
		}
		else {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (isDisposed())
						return;

					scrollBarHorizontal.setVisible(false);
					horizontallyCentered = true;
				}
			});
		}
	}

	/**
	 * Paint the {@link #viewPanel} whenever it needs repaint.
	 *
	 * @param g the graphics to draw into.
	 */
	private void paintViewPanel(Graphics2D g) {
		double zoomFactor = (double)zoomFactorPerMill / 1000;

		Rectangle2D.Double region = new Rectangle2D.Double(
				rectangleViewOrigin.x,
				rectangleViewOrigin.y,
				viewPanel.getWidth() / zoomFactor,
				viewPanel.getHeight() / zoomFactor
		);

		if (logger.isDebugEnabled()) {
//			logger.debug("paintViewPanel: rectangleViewOrigin.x = " + rectangleViewOrigin.x);
//			logger.debug("paintViewPanel: rectangleViewOrigin.y = " + rectangleViewOrigin.y);
			logger.debug("paintViewPanel: zoomFactor=" + zoomFactor + " rectangleViewOrigin.x=" + rectangleViewOrigin.x + " rectangleViewOrigin.y=" + rectangleViewOrigin.y);
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

	public Dimension getScreenSize() {
		return screenSize;
	}

	public void setScreenSize(Dimension screenSize) {
		this.screenSize = screenSize;
	}

	public Panel getViewPanel() {
		return viewPanel;
	}

	public void setViewPanel(Panel viewPanel) {
		this.viewPanel = viewPanel;
	}

	public class MouseWheelListenerImpl implements MouseWheelListener {
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
							scrollBarVertical.setSelection(scrollBarVertical.getSelection() + mouseRotationOrientation * 10);
//							scrollBarVertical.setValue(scrollBarVertical.getValue() + mouseRotationOrientation * 10);
							if (scrollBarVertical.getSelection() >= scrollBarVertical.getMinimum() && scrollBarVertical.getSelection() <= scrollBarVertical.getMaximum())
//							if (scrollBarVertical.getValue() >= scrollBarVertical.getMinimum() && scrollBarVertical.getValue() <= scrollBarVertical.getMaximum())
								scrollVertically(scrollBarVertical);
//								scrollVertically();
						}
						else {
							if (scrollBarHorizontal.isVisible() == true) {
								scrollBarHorizontal.setSelection(scrollBarHorizontal.getSelection() + mouseRotationOrientation * 10);
//								scrollBarHorizontal.setValue(scrollBarHorizontal.getValue() + mouseRotationOrientation * 10);
								if (scrollBarHorizontal.getSelection() >= scrollBarHorizontal.getMinimum() && scrollBarHorizontal.getSelection() <= scrollBarHorizontal.getMaximum())
//								if (scrollBarHorizontal.getValue() >= scrollBarHorizontal.getMinimum() && scrollBarHorizontal.getValue() <= scrollBarHorizontal.getMaximum())
									scrollHorizontally(scrollBarHorizontal);
//									scrollHorizontally();
							}
						}
					}
				});
			}
		}
	}

	public class KeyListenerImpl implements KeyListener {

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
	public void zoomPDFDocument(int mouseRotationOrientation) {
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
				rectangleViewOrigin.x + rectangleViewOrigin.x + viewPanel.getWidth() / ((double)zoomBefore / 1000)
		) / 2;

		middle.y = (
				rectangleViewOrigin.y + rectangleViewOrigin.y + viewPanel.getHeight() / ((double)zoomBefore / 1000)
		) / 2;

		// calculate the new view origin AFTER zooming
		Point2D.Double viewPanelBoundsReal = new Point2D.Double();
		double zoomFactor = (double)zoomFactorPerMill / 1000;
		viewPanelBoundsReal.x = viewPanel.getWidth() / zoomFactor;
		viewPanelBoundsReal.y = viewPanel.getHeight() / zoomFactor;
		rectangleViewOrigin.x = (int) (middle.x - viewPanelBoundsReal.x / 2);
		rectangleViewOrigin.y = (int) (middle.y - viewPanelBoundsReal.y / 2);

		logger.debug("zoomPDFDocument: zoomFactor=" + zoomFactor + " rectangleViewOrigin.x=" + rectangleViewOrigin.x + " rectangleViewOrigin.y=" + rectangleViewOrigin.y);

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				setScrollbars();
			}
		});

		viewPanel.repaint();
	}

	public RenderThread getRenderThread() {
		return renderThread;
	}

	public void setRenderThread(RenderThread renderThread) {
		this.renderThread = renderThread;
	}

	public Point getRectangleViewOrigin() {
		return rectangleViewOrigin;
	}

	public void setRectangleViewOrigin(Point rectangleViewOrigin) {
		this.rectangleViewOrigin = rectangleViewOrigin;
	}

	public int getZoomFactorPerMill() {
		return zoomFactorPerMill;
	}

}
