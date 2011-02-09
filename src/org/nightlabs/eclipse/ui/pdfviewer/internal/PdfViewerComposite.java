/* ********************************************************************
 * NightLabs PDF Viewer - http://www.nightlabs.org/projects/pdfviewer *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.eclipse.ui.pdfviewer.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.nightlabs.eclipse.ui.pdfviewer.AutoZoom;
import org.nightlabs.eclipse.ui.pdfviewer.Dimension2DDouble;
import org.nightlabs.eclipse.ui.pdfviewer.PaintEvent;
import org.nightlabs.eclipse.ui.pdfviewer.PaintListener;
import org.nightlabs.eclipse.ui.pdfviewer.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;
import org.nightlabs.eclipse.ui.pdfviewer.Point2DDouble;

import com.sun.pdfview.PDFPage;

/**
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfViewerComposite extends Composite
{
	private static final Logger logger = Logger.getLogger(PdfViewerComposite.class);
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private RenderBuffer renderBuffer;
	private RenderThread renderThread;
	private PdfDocument pdfDocument;
	private PdfViewer pdfViewer;
	private ScrollBar scrollBarVertical, scrollBarHorizontal;
	private Point2DDouble zoomScreenResolutionFactor;
	private int currentPage;

	// https://sourceforge.net/projects/pdfviewer/forums/forum/866211/topic/4097829
	private static void _requestFocus(Component component)
	{
		logger.info("****************** _requestFocus(" + component + ") ****************");
		component.requestFocusInWindow();
	}

	/**
	 * Since the int range of the scroll bars is limited and we don't need to be able to scroll to every single
	 * coordinate value, we reduce the granularity by this divisor. This means, scrolling the real coordinate system
	 * by 200 dots will move the scroll bar's selection-value by 20 (200 / scrollBarDivisor).
	 */
	private static final int scrollBarDivisor = 10;

	/**
	 * The container of the AWT {@link Frame}. It is created with {@link SWT#EMBEDDED}
	 * and does therefore contain nothing but the {@link #viewPanelFrame}.
	 */
	private Composite viewPanelComposite;

	/**
	 * The real coordinates of the view area's left, top corner.
	 */
	private Point2DDouble viewOrigin;

	/**
	 * The zoom factor in &permil; (1/1000).
	 */
	private int zoomFactorPerMill = 1000;

//	We don't need this, because the mouse wheel listener event tells us whether CTRL is down or not.
//	and that seems to work pretty reliably. Marco.
//	/**
//	* If <code>true</code>, turning the mouse wheel zooms (forward = zoom in, backward = zoom out).
//	* If <code>false</code>, turning the mouse wheel scrolls.
//	*/
//	private boolean mouseWheelModeZoom = false;

	/**
	 * The AWT frame for this composite. It holds the {@link #intermediatePanel} and is embedded in the {@link #viewPanelComposite}.
	 */
	private Frame viewPanelFrame;
	/**
	 * This <code>Panel</code> is necessary, because otherwise the JPanel is not focusable.
	 */
	private Panel intermediatePanel;
	/**
	 * The panel within {@link #intermediatePanel}.
	 */
	private JPanel viewPanel;

//	// http://forums.sun.com/thread.jspa?messageID=3369196
//	// http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg24617.html
//	static {
//	System.setProperty("sun.awt.noerasebackground", "true");
//	}

	private boolean isViewPanelMinimumSized()
	{
		return viewPanel.isVisible() && viewPanel.getWidth() >= 20 && viewPanel.getHeight() >= 20;
	}

	public void setPdfDocument(PdfDocument pdfDocument)
	{
		if (renderThread != null) {
			long start = System.currentTimeMillis();
			while (renderThread.isAlive()) {
				if (System.currentTimeMillis() - start > 60000)
					throw new IllegalStateException("Timeout waiting for RenderThread to finish!"); //$NON-NLS-1$

				renderThread.interrupt();
				try {
					renderThread.join(10000);
				} catch (InterruptedException e) {
					logger.warn("renderThread.join(...) was interrupted!", e);
				}
			}
			renderThread = null;
		}

		if (renderBuffer != null) {
			renderBuffer = null;
		}

		this.pdfDocument = pdfDocument;

		int newPageNumber = 0;
		if (pdfDocument != null) {
			renderBuffer = new RenderBuffer(this, pdfDocument);
			renderThread = new RenderThread(this, renderBuffer);
			if (pdfDocument.getPdfFile().getNumPages() > 0)
				newPageNumber = 1;
		}
		setCurrentPage(newPageNumber);

		applyAutoZoom();
		calculateScrollbarValues();
	}

	private org.nightlabs.eclipse.ui.pdfviewer.MouseEvent createMouseEvent(MouseEvent event)
	{
		java.awt.Point pointRelative = event.getPoint();
		Point2DDouble pointAbsolute = new Point2DDouble();
		pointAbsolute.setX(
				viewOrigin.getX() + (pointRelative.getX() / (zoomScreenResolutionFactor.getX() * zoomFactorPerMill / 1000))
		);
		pointAbsolute.setY(
				viewOrigin.getY() + (pointRelative.getY() / (zoomScreenResolutionFactor.getY() * zoomFactorPerMill / 1000))
		);
		pointAbsolute.setReadOnly();
		return new org.nightlabs.eclipse.ui.pdfviewer.MouseEvent(pointRelative, pointAbsolute, event);
	}

	public PdfViewerComposite(Composite parent, int style, final PdfViewer pdfViewer)
	{
		super(parent, style);
		addDisposeListener(disposeListener);

		this.pdfViewer = pdfViewer;
		this.setLayout(new FillLayout());

		viewPanelComposite = new Composite(this, SWT.EMBEDDED | SWT.V_SCROLL | SWT.H_SCROLL) {
			@Override
			public boolean setFocus() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (viewPanel != null)
							_requestFocus(viewPanel);
					}
				});
				return true;
			}
		};

		// Initialize the zoom-screen-resolution factors already here (not lazily in the get methods)
		// so that the getters can be called on every thread (e.g. by the RenderBuffer which is
		// partially used on the RenderThread).
		Point screenDPI = getDisplay().getDPI();
		zoomScreenResolutionFactor = new Point2DDouble(
				(double)screenDPI.x / 72,
				(double)screenDPI.y / 72
		);
		zoomScreenResolutionFactor.setReadOnly();

		viewOrigin = new Point2DDouble();
		viewOrigin.setReadOnly();

		scrollBarVertical = viewPanelComposite.getVerticalBar();
		scrollBarHorizontal = viewPanelComposite.getHorizontalBar();
		viewPanelFrame = SWT_AWT.new_Frame(viewPanelComposite);

//		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=171432
//		// TODO perhaps not the right place to call this as the error still occurs sometimes
//		boolean x11ErrorHandlerFixInstalled = false;
//		if(!x11ErrorHandlerFixInstalled && "gtk".equals( SWT.getPlatform())) { //$NON-NLS-1$
//			x11ErrorHandlerFixInstalled = true;
//			EventQueue.invokeLater( new Runnable() {
//				public void run() {
//					initX11ErrorHandlerFix();
//				}
//			});
//		}

		viewPanelFrame.setFocusableWindowState(true);

//		viewPanelFrame.setFocusable(true);

		viewPanel = new JPanel(true) {
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g) {
				paintViewPanel((Graphics2D) g);
			}
		};
		viewPanel.enableInputMethods(true);
		viewPanel.setFocusable(true);

		viewPanel.addMouseListener(viewPanel_mouseListener);
		viewPanel.addMouseMotionListener(viewPanel_mouseMotionListener);
		viewPanel.addKeyListener(viewPanel_keyListener);
		viewPanel.addComponentListener(viewPanel_componentListener);

//		viewPanel.addFocusListener(new FocusListener() {
//			@Override
//			public void focusGained(FocusEvent e) {
//				if (logger.isDebugEnabled())
//					logger.debug("viewPanel.FocusListener.focusGained: entered"); //$NON-NLS-1$
//			}
//			@Override
//			public void focusLost(FocusEvent e) {
//				if (logger.isDebugEnabled())
//					logger.debug("viewPanel.FocusListener.focusLost: entered"); //$NON-NLS-1$
//			}
//		});
//
//		viewPanelFrame.addFocusListener(new FocusListener() {
//			@Override
//			public void focusGained(FocusEvent e) {
//				if (logger.isDebugEnabled())
//					logger.debug("viewPanelFrame.FocusListener.focusGained: entered"); //$NON-NLS-1$
//			}
//			@Override
//			public void focusLost(FocusEvent e) {
//				if (logger.isDebugEnabled())
//					logger.debug("viewPanelFrame.FocusListener.focusLost: entered"); //$NON-NLS-1$
//			}
//		});

//		viewPanelFrame.add(viewPanel);
		intermediatePanel = new Panel(new BorderLayout());
		intermediatePanel.setFocusable(true);
		intermediatePanel.addFocusListener(intermediatePanel_focusListener);
		viewPanelFrame.add(intermediatePanel);
		intermediatePanel.add(viewPanel, BorderLayout.CENTER);

		viewPanelComposite.addFocusListener(new org.eclipse.swt.events.FocusListener() {
			@Override
			public void focusGained(org.eclipse.swt.events.FocusEvent event) {
				if (logger.isDebugEnabled())
					logger.debug("viewPanelComposite.FocusListener.focusGained: entered"); //$NON-NLS-1$
			}
			@Override
			public void focusLost(org.eclipse.swt.events.FocusEvent event) {
				if (logger.isDebugEnabled())
					logger.debug("viewPanelComposite.FocusListener.focusLost: entered"); //$NON-NLS-1$
			}
		});

		scrollBarHorizontal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
//				mouseWheelModeZoom = false;
				scrollHorizontally();
			}
		});

		scrollBarVertical.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
//				mouseWheelModeZoom = false;
				scrollVertically();
			}
		});

		viewPanel.addMouseWheelListener(new MouseWheelListenerImpl());

		getDisplay().addFilter(SWT.KeyDown, keyDownListener);
		getDisplay().addFilter(SWT.KeyUp, keyUpListener);

		_requestFocus(viewPanel);

//		pdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_MOUSE_CLICKED, propertyChangeListenerMouseClicked);
	}

	private DisposeListener disposeListener = new DisposeListener() {
		@Override
		public void widgetDisposed(DisposeEvent event) {
			getDisplay().removeFilter(SWT.KeyDown, keyDownListener);
			getDisplay().removeFilter(SWT.KeyUp, keyUpListener);

			// There is a known memory leak when working with java.awt.Frame:
			//		http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6474128
			// Thus, we do a thorough cleanup to minimize the effect:
			//  - remove all listeners from viewPanel
			//  - remove all listeners from intermediatePanel
			//  - viewPanelFrame.removeAll() to remove all contents
			//  - set all fields null, after stopping the renderThread

			if (viewPanel != null) {
				viewPanel.removeMouseListener(viewPanel_mouseListener);
				viewPanel.removeMouseMotionListener(viewPanel_mouseMotionListener);
				viewPanel.removeKeyListener(viewPanel_keyListener);
				viewPanel.removeComponentListener(viewPanel_componentListener);
			}

			if (intermediatePanel != null) {
				intermediatePanel.removeFocusListener(intermediatePanel_focusListener);
			}

			if (viewPanelFrame != null) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (viewPanelFrame != null) {
							viewPanelFrame.removeAll();
							viewPanelFrame.dispose();
							viewPanelFrame = null;
						}
					}
				});
			}

			// Interrupting the renderThread on another Thread in order to *not* block the UI. Marco.
			new Thread() {
				@Override
				public void run() {
					if (renderThread != null) {
						renderThread.interrupt();
						try {
							renderThread.join(30000);
							renderThread = null;
						} catch (InterruptedException e) {
							logger.warn("renderThread.join(...) was interrupted!", e);
						}
					}

					if (renderThread == null) {
						// We set these fields only to null, if the renderThread was successfully stopped - otherwise it might still access them and cause NPEs.
						// nulling these fields is only additional safety and shouldn't be necessary, anyway. Marco.
						renderBuffer = null;
						pdfDocument = null;
						viewPanel = null;
						intermediatePanel = null;
						pdfViewer = null;
					}
				}
			}.start();
		}
	};

	private FocusListener intermediatePanel_focusListener = new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
			_requestFocus(viewPanel);
		}
	};

	private ComponentListener viewPanel_componentListener = new ComponentAdapter() {
		private Point pdfViewerCompositeOldSize;
		private int flickeringCounter = 0;

		@Override
		public void componentResized(ComponentEvent e) {
			if (logger.isDebugEnabled())
				logger.debug("componentResized"); //$NON-NLS-1$

			if (pdfDocument == null)
				return;

			if (!isViewPanelMinimumSized())
				return;

			final Point[] pdfViewerCompositeNewSize = new Point[1];
			getDisplay().syncExec(new Runnable() {
				public void run() {
					if (isDisposed()) {
						pdfViewerCompositeNewSize[0] = null;
						return;
					}

					pdfViewerCompositeNewSize[0] = PdfViewerComposite.this.getSize();
				}
			});
			if (pdfViewerCompositeNewSize[0] == null) // was disposed => return.
				return;

			if (pdfViewerCompositeNewSize[0].equals(pdfViewerCompositeOldSize)) {
				if (++flickeringCounter > 5)
					return; // prevent eternal flickering between two states because of scroll bars coming and going.
			}
			else
				flickeringCounter = 0;

			pdfViewerCompositeOldSize = pdfViewerCompositeNewSize[0];
			final Dimension2D viewDimensionBefore = getViewDimension();

			applyAutoZoom();
			calculateScrollbarValues();

			AutoZoom autoZoom = pdfViewer.getAutoZoom();
			if (logger.isDebugEnabled())
				logger.debug("autoZoom: " + autoZoom);

			// TODO is this repaint necessary? Marco.
			viewPanel.repaint();

			if (!isDisposed()) {
				getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) // might have become disposed in the mean time => check again => return if yes
							return;

						propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_VIEW_DIMENSION, viewDimensionBefore, getViewDimension());
					}
				});
			}
		}
		@Override
		public void componentShown(ComponentEvent e) {
		}
	};

	private KeyListener viewPanel_keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(java.awt.event.KeyEvent e) {
			if (logger.isDebugEnabled())
				logger.debug("keyPressed: " + e); //$NON-NLS-1$

//			if (e.getKeyCode() == 17)
//			mouseWheelModeZoom = true;
		}
		@Override
		public void keyReleased(java.awt.event.KeyEvent e) {
			if (logger.isDebugEnabled())
				logger.debug("keyReleased: " + e); //$NON-NLS-1$

//			if (e.getKeyCode() == 17)
//			mouseWheelModeZoom = false;
		}
	};

	private MouseMotionListener viewPanel_mouseMotionListener = new MouseMotionListener() {
		@Override
		public void mouseDragged(final MouseEvent e) {
			if (logger.isDebugEnabled())
				logger.debug("mouseDragged: " + e); //$NON-NLS-1$

			getDisplay().asyncExec(new Runnable() {
				public void run() {
					propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_MOUSE_DRAGGED, null, createMouseEvent(e));
				}
			});
		}
		@Override
		public void mouseMoved(final MouseEvent e) {
			if (logger.isDebugEnabled())
				logger.debug("mouseMoved: " + e); //$NON-NLS-1$

			getDisplay().asyncExec(new Runnable() {
				public void run() {
					propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_MOUSE_MOVED, null, createMouseEvent(e));
				}
			});
		}
	};

	private MouseListener viewPanel_mouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(final MouseEvent e) {
			if (logger.isDebugEnabled())
				logger.debug("mouseClicked: " + e); //$NON-NLS-1$

//			The following code is thumbnail-navigator-specific and should therefore not be here! I moved it into the correct class. Marco.
//			final org.nightlabs.eclipse.ui.pdfviewer.MouseEvent pdfMouseEvent = createMouseEvent(e);

//			// calculate current page from pdfMouseEvent.getPointInRealCoordinate() and set it
//			Collection<Integer> visiblePages = pdfDocument.getVisiblePages(
//			new Rectangle2D.Double(pdfMouseEvent.getPointInRealCoordinate().getX(), pdfMouseEvent.getPointInRealCoordinate().getY(), 1, 1)
//			);

//			final Integer newCurrentPage;
//			if (visiblePages.isEmpty())
//			newCurrentPage = null;
//			else
//			newCurrentPage = visiblePages.iterator().next();

			getDisplay().asyncExec(new Runnable() {
				public void run() {
//					if (newCurrentPage != null)
//					setCurrentPage(newCurrentPage);

					org.nightlabs.eclipse.ui.pdfviewer.MouseEvent pdfMouseEvent = createMouseEvent(e);
					propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_MOUSE_CLICKED, null, pdfMouseEvent);
				}
			});
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			if (logger.isDebugEnabled())
				logger.debug("mousePressed: " + e); //$NON-NLS-1$

			_requestFocus(intermediatePanel);
			_requestFocus(viewPanel);

			getDisplay().asyncExec(new Runnable() {
				public void run() {
					propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_MOUSE_PRESSED, null, createMouseEvent(e));
				}
			});
		}
		@Override
		public void mouseReleased(final MouseEvent e) {
			if (logger.isDebugEnabled())
				logger.debug("mouseReleased: " + e); //$NON-NLS-1$

			getDisplay().asyncExec(new Runnable() {
				public void run() {
					propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_MOUSE_RELEASED, null, createMouseEvent(e));
				}
			});
		}
	};

	private Listener keyDownListener = new Listener() {
		public void handleEvent(Event event)
		{
			if (logger.isDebugEnabled())
				logger.debug("keyDownListener.handleEvent: " + event.keyCode); //$NON-NLS-1$

//			switch (event.keyCode) {
//			case SWT.CTRL:
//			mouseWheelModeZoom = true;
//			break;
//			}
		}
	};

	private Listener keyUpListener = new Listener() {
		public void handleEvent(Event event)
		{
			if (logger.isDebugEnabled())
				logger.debug("keyUpListener.handleEvent: " + event.keyCode); //$NON-NLS-1$

//			switch (event.keyCode) {
//			case SWT.CTRL:
//			mouseWheelModeZoom = false;
//			break;
//			}
		}
	};

	private boolean centerHorizontally;
	private boolean centerVertically;

	private void center()
	{
		if (pdfDocument == null)
			return;

		if (logger.isDebugEnabled())
			logger.debug("center: centerHorizontally=" + centerHorizontally + " centerVertically=" + centerVertically);

		int pageNumber = getCurrentPage();
		if (pageNumber < 1)
			return;

		Rectangle2D pageBounds = pdfDocument.getPageBounds(pageNumber);


		final Point2D viewOriginBefore = viewOrigin;
		Point2DDouble newViewOrigin = null;

		if (centerHorizontally) {
//			double middleX = pdfDocument.getDocumentDimension().getWidth() / 2;
			double middleX = pageBounds.getCenterX();

			if (newViewOrigin == null)
				newViewOrigin = new Point2DDouble(viewOriginBefore);

			newViewOrigin.setX(middleX - viewPanel.getWidth() / 2 / (zoomFactorPerMill * getZoomScreenResolutionFactor().getX() / 1000));
		}

		if (centerVertically) {
//			double middleY = pdfDocument.getDocumentDimension().getHeight() / 2;
			double middleY = pageBounds.getCenterY();

			if (newViewOrigin == null)
				newViewOrigin = new Point2DDouble(viewOriginBefore);

			newViewOrigin.setY(middleY - viewPanel.getHeight() / 2 / (zoomFactorPerMill * getZoomScreenResolutionFactor().getY() / 1000));
		}

		if (newViewOrigin != null) {
			limitNewViewOrigin(newViewOrigin);

			// make the new view-origin read-only
			newViewOrigin.setReadOnly();

			// assign it, if it's different from our current situation
			if (!newViewOrigin.equals(viewOriginBefore)) {
				viewDimensionCached = null;
				viewOrigin = newViewOrigin;
				propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_VIEW_ORIGIN, viewOriginBefore, viewOrigin);
			}
		}
	}

	private void limitNewViewOrigin(Point2DDouble newViewOrigin)
	{
		if (pdfDocument == null)
			return;

		double panelWidthReal = getViewPanel().getWidth() * (zoomFactorPerMill * getZoomScreenResolutionFactor().getX() / 1000);
		double panelHeightReal = getViewPanel().getHeight() * (zoomFactorPerMill * getZoomScreenResolutionFactor().getY() / 1000);

		// don't scroll outside the document, if the document is big enough
		// check left side
		if (!centerHorizontally && newViewOrigin.getX() < 0) {
			if (panelWidthReal <= pdfDocument.getDocumentDimension().getWidth())
				newViewOrigin.setX(0);
			// else is centerHorizontally anyway (see calculateScrollbarValues)
		}

		// check top
		if (!centerVertically && newViewOrigin.getY() < 0) {
			if (panelHeightReal <= pdfDocument.getDocumentDimension().getHeight())
				newViewOrigin.setY(0);
			// else is centerVertically anyway (see calculateScrollbarValues)
		}

		// check right side
		if (!centerHorizontally && newViewOrigin.getX() + panelWidthReal > pdfDocument.getDocumentDimension().getWidth()) {
			if (panelWidthReal <= pdfDocument.getDocumentDimension().getWidth())
				newViewOrigin.setX(pdfDocument.getDocumentDimension().getWidth() - panelWidthReal);
			// else is centerHorizontally anyway (see calculateScrollbarValues)
		}

		// check bottom
		if (!centerVertically && newViewOrigin.getY() + panelHeightReal > pdfDocument.getDocumentDimension().getHeight()) {
			if (panelHeightReal <= pdfDocument.getDocumentDimension().getHeight())
				newViewOrigin.setY(pdfDocument.getDocumentDimension().getHeight() - panelHeightReal);
			// else is centerVertically anyway (see calculateScrollbarValues)
		}
	}

	private void scrollVertically()
	{
		Point2DDouble oldViewOrigin = this.viewOrigin;
		Point2DDouble newViewOrigin = new Point2DDouble(oldViewOrigin);

		newViewOrigin.setY(scrollBarVertical.getSelection() * scrollBarDivisor);
		newViewOrigin.setReadOnly();

		if (logger.isDebugEnabled()) {
			logger.debug("scrollVertically: scrollBarVerticalSelectionNew=" + scrollBarVertical.getSelection()); //$NON-NLS-1$
			logger.debug("scrollVertically: newViewOrigin.y=" + newViewOrigin.getY()); //$NON-NLS-1$
			logger.debug("scrollVertically: bottomRealY=" + (newViewOrigin.getY() + viewPanel.getHeight() / (zoomFactorPerMill * getZoomScreenResolutionFactor().getY() / 1000))); //$NON-NLS-1$
		}

		centerVertically = false;
		this.viewOrigin = newViewOrigin;

		viewPanel.repaint();

		Rectangle2D rectangleView = new Rectangle2D.Double();
		rectangleView.setRect(
				this.viewOrigin.getX(),
				this.viewOrigin.getY(),
				getViewDimension().getWidth(),
				getViewDimension().getHeight()
		);

		propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_VIEW_ORIGIN, oldViewOrigin, viewOrigin);

		if (pdfViewer.isUpdateCurrentPageOnScrolling()) {
			int newCurrentPage = pdfDocument.getMostVisiblePage(rectangleView);
			if (newCurrentPage > 0) {
				int oldCurrentPage = this.currentPage;
				this.currentPage = newCurrentPage;

				propertyChangeSupport.firePropertyChange(
						PdfViewer.PROPERTY_CURRENT_PAGE,
						oldCurrentPage,
						newCurrentPage
				);
			}
		}
	}

	private void scrollHorizontally()
	{
		Point2DDouble oldViewOrigin = this.viewOrigin;
		Point2DDouble newViewOrigin = new Point2DDouble(oldViewOrigin);

		newViewOrigin.setX(scrollBarHorizontal.getSelection() * scrollBarDivisor);
		newViewOrigin.setReadOnly();

		if (logger.isDebugEnabled())
			logger.debug("scrollHorizontally: newViewOrigin.x=" + newViewOrigin.getX()); //$NON-NLS-1$

		centerHorizontally = false;
		this.viewOrigin = newViewOrigin;

		viewPanel.repaint();

		Rectangle2D rectangleView = new Rectangle2D.Double();
		rectangleView.setRect(
				this.viewOrigin.getX(),
				this.viewOrigin.getY(),
				getViewDimension().getWidth(),
				getViewDimension().getHeight()
		);

		propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_VIEW_ORIGIN, oldViewOrigin, viewOrigin);

		if (pdfViewer.isUpdateCurrentPageOnScrolling()) {
			int newCurrentPage = pdfDocument.getMostVisiblePage(rectangleView);
			if (newCurrentPage > 0) {
				int oldCurrentPage = this.currentPage;
				this.currentPage = newCurrentPage;

				propertyChangeSupport.firePropertyChange(
						PdfViewer.PROPERTY_CURRENT_PAGE,
						oldCurrentPage,
						newCurrentPage
				);
			}
		}
	}

	private void calculateScrollbarValues() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if (isDisposed())
					return;

				if (logger.isDebugEnabled())
					logger.debug("calculateScrollbarValues: entered");

				if (pdfDocument == null) {
					scrollBarVertical.setVisible(false);
					scrollBarHorizontal.setVisible(false);
					return;
				}

				if (!isViewPanelMinimumSized())
					return;

//				boolean showScrollBarHorizontal = true;
//				if (pdfViewer.getAutoZoom() == AutoZoom.pageWidth) {
//				showScrollBarHorizontal = false;
//				}

				double zoomFactor = (zoomFactorPerMill / 1000d);

				int visibleAreaScrollHeight = (int) (getViewPanel().getHeight() / (zoomFactor * getZoomScreenResolutionFactor().getY())) / scrollBarDivisor;
				scrollBarVertical.setMinimum(0);
				scrollBarVertical.setMaximum((int) pdfDocument.getDocumentDimension().getHeight() / scrollBarDivisor);
				scrollBarVertical.setSelection((int) (viewOrigin.getY() / scrollBarDivisor));
				boolean verticalBarVisible = visibleAreaScrollHeight <= (scrollBarVertical.getMaximum() - scrollBarVertical.getMinimum());

				if (AutoZoom.page == pdfViewer.getAutoZoom() || AutoZoom.pageHeight == pdfViewer.getAutoZoom()) {
					centerVertically = true;
//					verticalBarVisible = true; // prevent eternal flickering because of permanent enabling/disabling cycles by forcing it to stay visible.
				}

				scrollBarVertical.setVisible(verticalBarVisible);
				if (!verticalBarVisible)
					centerVertically = true;

				scrollBarVertical.setThumb(visibleAreaScrollHeight);
				scrollBarVertical.setPageIncrement((int) (visibleAreaScrollHeight * 0.9d));
				scrollBarVertical.setIncrement((int) (visibleAreaScrollHeight * 0.1d));

//				if (showScrollBarHorizontal == true) {
				// PDF viewer composite does probably need a horizontal scroll-bar
				int visibleAreaScrollWidth = (int) (getViewPanel().getWidth() / (zoomFactor * getZoomScreenResolutionFactor().getX())) / scrollBarDivisor;
				scrollBarHorizontal.setMinimum(0);
				scrollBarHorizontal.setMaximum((int) pdfDocument.getDocumentDimension().getWidth() / scrollBarDivisor);
				scrollBarHorizontal.setSelection((int) (viewOrigin.getX() / scrollBarDivisor));
				boolean horizontalBarVisible = visibleAreaScrollWidth <= (scrollBarHorizontal.getMaximum() - scrollBarHorizontal.getMinimum());

				if (AutoZoom.page == pdfViewer.getAutoZoom() || AutoZoom.pageWidth == pdfViewer.getAutoZoom()) {
					centerHorizontally = true;
//					horizontalBarVisible = true; // prevent eternal flickering because of permanent enabling/disabling cycles by forcing it to stay visible.
				}

				scrollBarHorizontal.setVisible(horizontalBarVisible);
				if (!horizontalBarVisible)
					centerHorizontally = true;

				scrollBarHorizontal.setThumb(visibleAreaScrollWidth);
				scrollBarHorizontal.setPageIncrement((int) (visibleAreaScrollWidth * 0.9d));
				scrollBarHorizontal.setIncrement((int) (visibleAreaScrollWidth * 0.1d));
//				}
//				else {
//				// PDF thumbnail navigator composite doesn't need a horizontal scroll-bar at all
//				scrollBarHorizontal.setVisible(false);
//				centerHorizontally = true;
//				}

				center();

				if (logger.isDebugEnabled()) {
					logger.debug("setScrollbars: scrollBarVertical.minimum=" + scrollBarVertical.getMinimum()); //$NON-NLS-1$
					logger.debug("setScrollbars: scrollBarVertical.maximum=" + scrollBarVertical.getMaximum()); //$NON-NLS-1$
					logger.debug("setScrollbars: scrollBarVertical.thumb=" + scrollBarVertical.getThumb()); //$NON-NLS-1$
					logger.debug("setScrollbars: scrollBarVertical.size.x=" + scrollBarVertical.getSize().x); //$NON-NLS-1$
					logger.debug("setScrollbars: scrollBarVertical.size.y=" + scrollBarVertical.getSize().y); //$NON-NLS-1$
					logger.debug("setScrollbars: scrollBarVertical.selection=" + scrollBarVertical.getSelection()); //$NON-NLS-1$
					logger.debug("setScrollbars: centerHorizontally=" + centerHorizontally); //$NON-NLS-1$

					logger.debug("setScrollbars: scrollBarHorizontal.minimum=" + scrollBarHorizontal.getMinimum()); //$NON-NLS-1$
					logger.debug("setScrollbars: scrollBarHorizontal.maximum=" + scrollBarHorizontal.getMaximum()); //$NON-NLS-1$
					logger.debug("setScrollbars: scrollBarHorizontal.thumb=" + scrollBarHorizontal.getThumb()); //$NON-NLS-1$
					logger.debug("setScrollbars: scrollBarHorizontal.size.x=" + scrollBarHorizontal.getSize().x); //$NON-NLS-1$
					logger.debug("setScrollbars: scrollBarHorizontal.size.y=" + scrollBarHorizontal.getSize().y); //$NON-NLS-1$
					logger.debug("setScrollbars: scrollBarHorizontal.selection=" + scrollBarHorizontal.getSelection()); //$NON-NLS-1$
					logger.debug("setScrollbars: centerVertically=" + centerVertically); //$NON-NLS-1$
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
			JPanel viewPanel = getViewPanel();
			if (viewPanel != null) {
				g.setColor(viewPanel.getBackground());
				g.fillRect(0, 0, viewPanel.getWidth(), viewPanel.getHeight());
			}
			return;
		}

		double zoomFactor = (double)zoomFactorPerMill / 1000;

		Rectangle2D.Double region = new Rectangle2D.Double(
				viewOrigin.getX(),
				viewOrigin.getY(),
				viewPanel.getWidth() / (zoomFactor * getZoomScreenResolutionFactor().getX()),
				viewPanel.getHeight() / (zoomFactor * getZoomScreenResolutionFactor().getY())
		);

		if (logger.isDebugEnabled()) {
			logger.debug("paintViewPanel: zoomFactor=" + zoomFactor + " viewOrigin.x=" + viewOrigin.getX() + " viewOrigin.y=" + viewOrigin.getY()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			logger.debug("paintViewPanel: viewPanel.width = " + viewPanel.getWidth()); //$NON-NLS-1$
			logger.debug("paintViewPanel: viewPanel.height = " + viewPanel.getHeight()); //$NON-NLS-1$
			logger.debug("paintViewPanel: region = " + region); //$NON-NLS-1$
		}

		boolean bufferSufficient = renderBuffer.paintToView(
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

	public JPanel getViewPanel() {
		return viewPanel;
	}

	private class MouseWheelListenerImpl implements MouseWheelListener {
		int mouseRotationOrientation;
		public void mouseWheelMoved(MouseWheelEvent e) {
			boolean mouseWheelModeZoom = e.isControlDown();

//			_requestFocus(viewPanel);

			if (e.getWheelRotation() < 0)
				mouseRotationOrientation = - 1;
			else
				mouseRotationOrientation = 1;

			if (mouseWheelModeZoom == true && pdfViewer.isMouseWheelZoomEnabled())	// do not allow zooming in PDF thumbnail viewer
				zoomPDFDocumentOnMouseWheelMove(mouseRotationOrientation);
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

	private static final int ZOOM_MIN = 100;
	private static final int ZOOM_MAX = 10000;

	/**
	 * Scale all PDF pages of the currently opened PDF document
	 *
	 * @param mouseRotationOrientation the direction the user has scrolled into
	 */
	private void zoomPDFDocumentOnMouseWheelMove(int mouseRotationOrientation) {
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
				pdfViewer.setAutoZoom(AutoZoom.none);
				setZoomFactorPerMill(zoomAfter);
			}
		});
	}

	public Point2D getViewOrigin() {
		return viewOrigin;
	}

	public void setViewOrigin(Point2D viewOrigin) {
		Point2DDouble oldViewOrigin = this.viewOrigin;
		Point2DDouble newViewOrigin = new Point2DDouble(viewOrigin);
		limitNewViewOrigin(newViewOrigin);
		newViewOrigin.setReadOnly();

		this.viewOrigin = newViewOrigin;
		this.calculateScrollbarValues();
		viewPanel.repaint();
		// TODO test this method! and modify if necessary.

		propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_VIEW_ORIGIN, oldViewOrigin, this.viewOrigin);
	}

	private Dimension2D viewDimensionCached = null;

	/**
	 * Get the size of the view area in real coordinates (i.e. what is visible
	 * in the {@link PdfDocument}). Together with {@link #getViewOrigin()},
	 * this information tells you what area is currently visible.
	 *
	 * @return the size of the view area in real coordinates.
	 */
	public Dimension2D getViewDimension()
	{
		if (viewDimensionCached == null) {
			double zoomFactor = (double)zoomFactorPerMill / 1000;
			viewDimensionCached = new Dimension2DDouble(
					viewPanel.getWidth() / (zoomFactor * getZoomScreenResolutionFactor().getX()),
					viewPanel.getHeight() / (zoomFactor * getZoomScreenResolutionFactor().getY())
			);
		}
		return viewDimensionCached;
	}

	private void applyAutoZoom()
	{
		getDisplay().syncExec(new Runnable() {
			public void run() {
				if (pdfDocument == null)
					return;

				if (!isViewPanelMinimumSized())
					return;

				if (AutoZoom.none == pdfViewer.getAutoZoom())
					return;

				// An adequate zoom factor for fitting the given document into PDF thumbnail navigator composite is computed
				// considering the different widths of document and PDF viewer composite.
				// The horizontal scroll-bar of PDF thumbnail navigator composite will be set invisible as it is not needed
				// in this navigator.

//				// get the scroll-bar width
//				UIDefaults uidef = UIManager.getDefaults();
//				int swidth = Integer.parseInt(uidef.get("ScrollBar.width").toString()); //$NON-NLS-1$

				// now compute the adequate zoom factor
//				double documentHeight = pdfDocument.getDocumentDimension().getHeight();
				int pageNumber = getCurrentPage();
				if (pageNumber < 1)
					return;

				PDFPage page = pdfDocument.getPdfFile().getPage(pageNumber);

				double pdfViewerCompositeHeightReal = viewPanel.getHeight() / zoomScreenResolutionFactor.getY();
				int zoomFactorPerMillY = (int) (pdfViewerCompositeHeightReal / (page.getHeight() + pdfViewer.getAutoZoomVerticalMargin() * 2) * 1000);

//				double documentWidth = pdfDocument.getDocumentDimension().getWidth();
				double pdfViewerCompositeWidthReal = viewPanel.getWidth() / zoomScreenResolutionFactor.getX();
				int zoomFactorPerMillX = (int) (pdfViewerCompositeWidthReal / (page.getWidth() + pdfViewer.getAutoZoomHorizontalMargin() * 2) * 1000);

				switch (pdfViewer.getAutoZoom()) {
					case pageWidth:
						centerHorizontally = true;
						setZoomFactorPerMill(zoomFactorPerMillX);
						break;
					case pageHeight:
						centerVertically = true;
						setZoomFactorPerMill(zoomFactorPerMillY);
						break;
					case page:
						centerHorizontally = true;
						centerVertically = true;
						setZoomFactorPerMill(
								Math.min(zoomFactorPerMillX, zoomFactorPerMillY)
						);
						break;
					default:
						throw new IllegalStateException("Unknown autoZoom: " + pdfViewer.getAutoZoom());
				}
				center();
			}
		});
	}

//	private void zoomToPageHeight() {

//	Point screenDPI = getDisplay().getDPI();
//	zoomScreenResolutionFactor = new Point2DDouble(	(double)screenDPI.x / 72,
//	(double)screenDPI.y / 72
//	);
//	// TODO implement

//	}

	/**
	 * Get the zoom factor in &permil; (1/1000).
	 *
	 * @return the zoom factor in &permil;.
	 */
	public int getZoomFactorPerMill() {
		return zoomFactorPerMill;
	}

	public void setZoomFactorPerMill(int zoomFactorPerMill)
	{
		if (logger.isDebugEnabled())
			logger.debug("setZoomFactorPerMill: zoomFactorPerMill=" + zoomFactorPerMill);

		// TODO if PROPERTY_CURRENT_PAGE is fired again below, an error occurs
//		boolean doFire = true;
//		AutoZoom autoZoom = pdfViewer.getAutoZoom();
//		if (autoZoom == AutoZoom.pageHeight || autoZoom == AutoZoom.pageWidth) {
//		doFire = false;
//		}

		int zoomBefore = this.zoomFactorPerMill;
		Dimension2D viewDimensionBefore = getViewDimension();

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
				viewOrigin.getX() * 2 + viewPanel.getWidth() / (zoomBefore * getZoomScreenResolutionFactor().getX() / 1000)
		) / 2;

		middle.y = (
				viewOrigin.getY() * 2 + viewPanel.getHeight() / (zoomBefore * getZoomScreenResolutionFactor().getY() / 1000)
		) / 2;

		// calculate the new view origin AFTER zooming
		Point2D.Double viewPanelBoundsReal = new Point2D.Double();
		double zoomFactor = (double)zoomFactorPerMill / 1000;
		viewPanelBoundsReal.x = viewPanel.getWidth() / (zoomFactor * getZoomScreenResolutionFactor().getX());
		viewPanelBoundsReal.y = viewPanel.getHeight() / (zoomFactor * getZoomScreenResolutionFactor().getY());

		setViewOrigin(new Point2D.Double((int) (middle.x - viewPanelBoundsReal.x / 2), (int) (middle.y - viewPanelBoundsReal.y / 2)));

		if (logger.isDebugEnabled())
			logger.debug("zoomPDFDocument: zoomFactor=" + zoomFactor + " viewOrigin.x=" + viewOrigin.getX() + " viewOrigin.y=" + viewOrigin.getY()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		viewDimensionCached = null;

		propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_ZOOM_FACTOR, zoomBefore, this.zoomFactorPerMill);
		propertyChangeSupport.firePropertyChange(PdfViewer.PROPERTY_VIEW_DIMENSION, viewDimensionBefore, getViewDimension());

//		if (doFire) {
		Rectangle2D.Double rectangleView = new Rectangle2D.Double();
		rectangleView.setRect(
				(int) (middle.x - viewPanelBoundsReal.x / 2),
				(int) (middle.y - viewPanelBoundsReal.y / 2),
				getViewDimension().getWidth(),
				getViewDimension().getHeight()
		);

//		propertyChangeSupport.firePropertyChange(	PdfViewer.PROPERTY_CURRENT_PAGE,
//		0,
//		pdfDocument.getMostVisiblePage(
//		rectangleView
//		)
//		);

		int mostVisiblePage = pdfDocument.getMostVisiblePage(rectangleView);
		if (mostVisiblePage > 0)
			setCurrentPage(mostVisiblePage);
//		}
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

	@Override
	public boolean setFocus() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_requestFocus(viewPanel);
			}
		});
		return true;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage)
	{
		if (pdfDocument == null)
			return;

		if (logger.isDebugEnabled())
			logger.debug("setCurrentPage: " + currentPage);

		if (currentPage < 1)
			currentPage = 0; // make sure, we have only numbers >= 0 with 0 having the meaning that NO page is "current".

		int oldPageNumber = this.currentPage;
		this.currentPage = currentPage;

		applyAutoZoom(); // pageheight/width might have changed
		center();

		if (currentPage >= 1) {
			Rectangle2D pageBounds = pdfDocument.getPageBounds(currentPage);

			// viewOrigin changes while choosing another page
			Point2D oldViewOrigin = pdfViewer.getViewOrigin();

			// viewDimension remains the same while choosing another page
			Dimension2D viewDimension = pdfViewer.getViewDimension();

			Rectangle2D.Double oldViewBounds = new Rectangle2D.Double();
			oldViewBounds.setFrame(oldViewOrigin, viewDimension);

			// page is already visible => don't do anything
			// maybe optimize and still scroll, but without minimum scrolling (only X / only Y, if possible)
			if (!isPageVisible(oldViewBounds, pageBounds)) {
				boolean handled = false;
				for (int situation = 0; situation < 3; ++situation)
				{
					Rectangle2D.Double newViewBounds = new Rectangle2D.Double();
					newViewBounds.setFrame(oldViewOrigin, viewDimension);

					switch (situation) {
						case 0: // scrolling X is sufficient
							newViewBounds.x = pageBounds.getX();
							break;
						case 1: // scrolling Y is sufficient
							newViewBounds.y = pageBounds.getY();
							break;
						case 2: // need to scroll both, X and Y
							newViewBounds.x = pageBounds.getX();
							newViewBounds.y = pageBounds.getY();
							break;
						default:
							throw new IllegalStateException("situation unknown: " + situation); //$NON-NLS-1$
					}

					if (situation == 2 || isPageVisible(newViewBounds, pageBounds)) {
						pdfViewer.setViewOrigin(new Point2D.Double(newViewBounds.getX(), newViewBounds.getY()));
						handled = true;
						break;
					}
				}
				if (!handled)
					throw new IllegalStateException("What the fucking shit?!"); //$NON-NLS-1$
			}
		}

		propertyChangeSupport.firePropertyChange(
				PdfViewer.PROPERTY_CURRENT_PAGE,
				oldPageNumber,
				currentPage
		);
	}

	private boolean isPageVisible(Rectangle2D viewBounds, Rectangle2D pageBounds) {
		return viewBounds.contains(pageBounds) || pageBounds.contains(viewBounds) || viewBounds.intersects(pageBounds);
	}

	public Point2D getZoomScreenResolutionFactor() {
		return zoomScreenResolutionFactor;
	}

	public PdfDocument getPdfDocument() {
		return pdfDocument;
	}

//	private static void initX11ErrorHandlerFix() {
//		assert EventQueue.isDispatchThread();
//
//		try {
//			// get XlibWrapper.SetToolkitErrorHandler() and XSetErrorHandler() methods
//			Class<?> xlibwrapperClass = Class.forName( "sun.awt.X11.XlibWrapper" ); //$NON-NLS-1$
//			final Method setToolkitErrorHandlerMethod = xlibwrapperClass.getDeclaredMethod( "SetToolkitErrorHandler", (Class[])null); //$NON-NLS-1$
//			final Method setErrorHandlerMethod = xlibwrapperClass.getDeclaredMethod( "XSetErrorHandler", new Class[] { Long.TYPE } ); //$NON-NLS-1$
//			setToolkitErrorHandlerMethod.setAccessible( true );
//			setErrorHandlerMethod.setAccessible( true );
//
//			// get XToolkit.saved_error_handler field
//			Class<?> xtoolkitClass = Class.forName( "sun.awt.X11.XToolkit" ); //$NON-NLS-1$
//			final Field savedErrorHandlerField = xtoolkitClass.getDeclaredField( "saved_error_handler" ); //$NON-NLS-1$
//			savedErrorHandlerField.setAccessible( true );
//
//			// determine the current error handler and the value of XLibWrapper.ToolkitErrorHandler
//			// (XlibWrapper.SetToolkitErrorHandler() sets the X11 error handler to
//			// XLibWrapper.ToolkitErrorHandler and returns the old error handler)
//			final Object defaultErrorHandler = setToolkitErrorHandlerMethod.invoke( null, (Object[]) null);
//			final Object toolkitErrorHandler = setToolkitErrorHandlerMethod.invoke( null, (Object[]) null);
//			setErrorHandlerMethod.invoke( null, new Object[] { defaultErrorHandler } );
//
//			// create timer that watches XToolkit.saved_error_handler whether its value is equal
//			// to XLibWrapper.ToolkitErrorHandler, which indicates the start of the trouble
//			Timer timer = new Timer( 200, new ActionListener() {
//				public void actionPerformed( ActionEvent e ) {
//					try {
//						Object savedErrorHandler = savedErrorHandlerField.get( null );
//						if( toolkitErrorHandler.equals( savedErrorHandler ) ) {
//							// Last saved error handler in XToolkit.WITH_XERROR_HANDLER
//							// is XLibWrapper.ToolkitErrorHandler, which will cause
//							// the StackOverflowError when the next X11 error occurs.
//							// Workaround: restore the default error handler.
//							// Also update XToolkit.saved_error_handler so that
//							// this is done only once.
//							setErrorHandlerMethod.invoke( null, new Object[] { defaultErrorHandler } );
//							savedErrorHandlerField.setLong( null, ((Long)defaultErrorHandler).longValue() );
//						}
//					} catch( Exception ex ) {
//						// ignore
//					}
//
//				}
//			} );
//			timer.start();
//		} catch( Exception ex ) {
//			// ignore
//		}
//	}

	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}

	private ListenerList paintToBufferListeners = new ListenerList();
	private ListenerList paintToViewListeners = new ListenerList();

	protected void firePaintToBufferListeners(Graphics2D graphics2D, boolean post)
	{
		Object[] listeners = paintToBufferListeners.getListeners();
		if (listeners.length < 1)
			return;

		PaintEvent event = new PaintEvent(pdfViewer, graphics2D);
		for (Object listener : listeners) {
			if (post)
				((PaintListener)listener).postPaint(event);
			else
				((PaintListener)listener).prePaint(event);
		}
	}

	protected void firePaintToViewListeners(Graphics2D graphics2D, boolean post)
	{
		Object[] listeners = paintToViewListeners.getListeners();
		if (listeners.length < 1)
			return;

		PaintEvent event = new PaintEvent(pdfViewer, graphics2D);
		for (Object listener : listeners) {
			if (post)
				((PaintListener)listener).postPaint(event);
			else
				((PaintListener)listener).prePaint(event);
		}
	}

	public void addPaintToBufferListener(PaintListener listener)
	{
		paintToBufferListeners.add(listener);
	}
	public void removePaintToBufferListener(PaintListener listener)
	{
		paintToBufferListeners.remove(listener);
	}

	public void addPaintToViewListener(PaintListener listener)
	{
		paintToViewListeners.add(listener);
	}
	public void removePaintToViewListener(PaintListener listener)
	{
		paintToViewListeners.remove(listener);
	}

	@Override
	public void redraw() {
		super.redraw();
		viewPanel.repaint();
	}

	@Override
	public void redraw(int x, int y, int width, int height, boolean all) {
		super.redraw(x, y, width, height, all);
		viewPanel.repaint();
	}

	public void onAutoZoomChange()
	{
		applyAutoZoom();
		calculateScrollbarValues();
	}

}
