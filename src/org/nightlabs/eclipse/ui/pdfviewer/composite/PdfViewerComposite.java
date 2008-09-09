package org.nightlabs.eclipse.ui.pdfviewer.composite;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
	
	private int scrollingStepsDistance = 10;
	private Map<Integer, Double> zoomFactorsDownwards; 
	private Map<Integer, Double> zoomFactorsUpwards;
	private Composite renderComposite;
	private RenderBuffer renderBuffer;
	private RenderThread renderThread;
	private PdfDocument pdfDocument;
	private ScrollBar scrollBarVertical, scrollBarHorizontal;
	private int scrollBarHorizontalSelectionOld, scrollBarVerticalSelectionOld;
	private int scrollBarHorizontalSelectionNew, scrollBarVerticalSelectionNew;
	private int scrollBarVerticalNumberOfSteps, scrollBarHorizontalNumberOfSteps;
	private Point rectangleViewOrigin;
	private Dimension screenSize;
	private int zoomFactor = 10;	// = real zoom factor * 10
	private boolean startingPoint = true;
	private boolean documentWasZoomed = false;
	private boolean wantToZoom = false;
	private double zoomMultiplier;	
	/**
	 * The AWT frame for this composite.
	 */
	private Frame viewPanelFrame;
	/**
	 * The panel within {@link #viewPanelFrame}.
	 */
	private Panel viewPanel;
	

	public PdfViewerComposite(Composite parent, PdfDocument pdfDocument) {
		
		super(parent, SWT.V_SCROLL | SWT.H_SCROLL);		
		this.setLayout(new FillLayout());
		this.pdfDocument = pdfDocument;
		renderBuffer = new RenderBuffer(this, pdfDocument);
		renderComposite = new Composite(this, SWT.EMBEDDED);

		zoomFactorsDownwards = new HashMap<Integer, Double>();
		zoomFactorsDownwards.put(18, (double)9/10);
		zoomFactorsDownwards.put(16, (double)8/9);
		zoomFactorsDownwards.put(14, (double)7/8);
		zoomFactorsDownwards.put(12, (double)6/7);
		zoomFactorsDownwards.put(10, (double)5/6);
		zoomFactorsDownwards.put(8, (double)4/5);
		zoomFactorsDownwards.put(6, (double)3/4);
		zoomFactorsDownwards.put(4, (double)2/3);
		zoomFactorsDownwards.put(2, (double)1/2);
		
		zoomFactorsUpwards = new HashMap<Integer, Double>();
		zoomFactorsUpwards.put(4, (double)2);
		zoomFactorsUpwards.put(6, (double)3/2);
		zoomFactorsUpwards.put(8, (double)4/3);
		zoomFactorsUpwards.put(10, (double)5/4);
		zoomFactorsUpwards.put(12, (double)6/5);
		zoomFactorsUpwards.put(14, (double)7/6);
		zoomFactorsUpwards.put(16, (double)8/7);
		zoomFactorsUpwards.put(18, (double)9/8);
		zoomFactorsUpwards.put(20, (double)10/9);		
		
		rectangleViewOrigin = new Point(0,0);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		setScreenSize(toolkit.getScreenSize());					
		scrollBarVertical = this.getVerticalBar(); 	
		scrollBarHorizontal = this.getHorizontalBar();	
		
		viewPanelFrame = SWT_AWT.new_Frame(renderComposite);		
		viewPanel = new Panel() {
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g) {
				setScrollbars();
				paintViewPanel((Graphics2D) g);
			}
		};
		viewPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// TODO set size of RenderBuffer - optimize this not to do it every time, but keep the buffer a bit bigger and check when we exceed ranges
			}
			@Override
			public void componentShown(ComponentEvent e) {
				
			}
		});
		viewPanelFrame.add(viewPanel);
		setViewPanel(viewPanel);
		
		scrollBarHorizontal.addSelectionListener(new SelectionListener() {
				@Override
		    	public void widgetSelected(SelectionEvent event) {
//					Logger.getRootLogger().info("scrolling horizontally");
					scrollHorizontally((ScrollBar)event.widget);
		    	}		 
				@Override
		    	public void widgetDefaultSelected(SelectionEvent event) {
			        scrollHorizontally((ScrollBar)event.widget);
		    	}
	    });					
		
		scrollBarVertical.addSelectionListener(new SelectionListener() {
			@Override
	    	public void widgetSelected(SelectionEvent event) {
//		    		Logger.getRootLogger().info("scrolling vertically");
	    		scrollVertically((ScrollBar)event.widget);
	    	}
			@Override
	    	public void widgetDefaultSelected(SelectionEvent event) {
		        scrollVertically((ScrollBar)event.widget);
	    	}
	    });
		
		
		MouseWheelListener mouseWheelListener = new MouseWheelListenerImpl();
		viewPanelFrame.addMouseWheelListener(mouseWheelListener);	
		
		KeyListener keyListener = new KeyListenerImpl();
		viewPanelFrame.addKeyListener(keyListener);
		
		renderThread = new RenderThread(this, renderBuffer);
	}
	
	private void scrollVertically (ScrollBar scrollBarVertical) {
		scrollBarVerticalSelectionNew = scrollBarVertical.getSelection();
		
		if (scrollBarVerticalSelectionNew > scrollBarVerticalSelectionOld) {			
			rectangleViewOrigin.y += (scrollBarVerticalSelectionNew - scrollBarVerticalSelectionOld) * scrollingStepsDistance;
			scrollBarVerticalSelectionOld = scrollBarVerticalSelectionNew;					
		}
		else {	
			rectangleViewOrigin.y -= (scrollBarVerticalSelectionOld - scrollBarVerticalSelectionNew) * scrollingStepsDistance;
			scrollBarVerticalSelectionOld = scrollBarVerticalSelectionNew;				
		}
		
//		renderThread.needRendering(rectangleViewOrigin, zoomFactor);
		Logger.getRootLogger().info("distance to beginning of main buffer: " + (rectangleViewOrigin.y - renderBuffer.getBufferedImageMainBounds().getY()));
		Logger.getRootLogger().info("distance to end of main buffer: " + ((renderBuffer.getBufferedImageMainBounds().getY() + renderBuffer.getBufferedImageMainBounds().getHeight()) - (rectangleViewOrigin.y + viewPanel.getHeight())));
					
		viewPanel.repaint();
	}
	
	private void scrollHorizontally(ScrollBar scrollBarHorizontal) {
		scrollBarHorizontalSelectionNew = scrollBarHorizontal.getSelection();
		
		if (scrollBarHorizontalSelectionNew > scrollBarHorizontalSelectionOld) {
			rectangleViewOrigin.x += (scrollBarHorizontalSelectionNew - scrollBarHorizontalSelectionOld) * scrollingStepsDistance;
			scrollBarHorizontalSelectionOld = scrollBarHorizontalSelectionNew;			
		}
		else {
			rectangleViewOrigin.x -= (scrollBarHorizontalSelectionOld - scrollBarHorizontalSelectionNew) * scrollingStepsDistance;
			scrollBarHorizontalSelectionOld = scrollBarHorizontalSelectionNew;			
		}			
		
//		renderThread.needRendering(rectangleViewOrigin, zoomFactor);	
		viewPanel.repaint();
	}
	
	private void setScrollbars() {
		
		double realZoomFactor = ((double)zoomFactor / 10);

		int heightDifference = Utilities.doubleToInt(	pdfDocument.getDocumentBounds().y * realZoomFactor 
//														+ (pdfDocument.getPdfFile().getNumPages() + 1) * pdfDocument.getMARGIN_BETWEEN_PAGES() * (1 - realZoomFactor) 
														- getViewPanel().getHeight()
														);
		int widthDifference = Utilities.doubleToInt(renderBuffer.getBufferWidth() - getViewPanel().getWidth());
		
//		Logger.getRootLogger().info("document bounds y-value: " + pdfDocument.getDocumentBounds().y + "; real zoom factor: " + ((double)zoomFactor / 10));
//		Logger.getRootLogger().info("panel height: " + viewPanel.getHeight());
//		Logger.getRootLogger().info("screen resolution in DPI: " + Toolkit.getDefaultToolkit().getScreenResolution());
		
		if (heightDifference > 0) {
			scrollBarVerticalNumberOfSteps = Utilities.doubleToInt((double)heightDifference / scrollingStepsDistance);	   
			Logger.getRootLogger().info("steps of vertical scroll bar: " + scrollBarVerticalNumberOfSteps);			
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (isDisposed())
						return;
					scrollBarVertical.setVisible(true);
					scrollBarVertical.setMaximum(scrollBarVerticalNumberOfSteps + 10);
					if (documentWasZoomed) {	
						int newSelection = Utilities.doubleToIntRoundedDown(scrollBarVertical.getSelection() * zoomMultiplier);
						scrollBarVertical.setSelection(newSelection);
						scrollBarVerticalSelectionOld = newSelection;
						documentWasZoomed = false;
					}
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
			scrollBarHorizontalNumberOfSteps = Utilities.doubleToInt((double)widthDifference / scrollingStepsDistance);	   
//			Logger.getRootLogger().info("steps of horizontal scroll bar: " + scrollBarHorizontalNumberOfSteps);				
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (isDisposed())
						return;					
					scrollBarHorizontal.setVisible(true);
					scrollBarHorizontal.setMaximum(scrollBarHorizontalNumberOfSteps + 10);
					if (startingPoint == true) {
						scrollBarHorizontal.setSelection((scrollBarHorizontalNumberOfSteps + 10) / 2 - 5);
						startingPoint = false;
						scrollHorizontally(scrollBarHorizontal);
					}
				}
			});				
		}
		else {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (isDisposed())
						return;
					scrollBarHorizontal.setVisible(false);
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
		
		Logger.getRootLogger().info("rectangle view origin y-value: " + rectangleViewOrigin.y);
		renderBuffer.drawRegion(g, 
								new Rectangle(
									rectangleViewOrigin.x,
									rectangleViewOrigin.y, 
									getViewPanel().getWidth(),
									getViewPanel().getHeight()
									)		
								);		
				
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
							if (scrollBarVertical.getSelection() >= scrollBarVertical.getMinimum() && scrollBarVertical.getSelection() <= scrollBarVertical.getMaximum())
								scrollVertically(scrollBarVertical);
						}
						else {
							if (scrollBarHorizontal.isVisible() == true) {
								scrollBarHorizontal.setSelection(scrollBarHorizontal.getSelection() + mouseRotationOrientation * 10);
								if (scrollBarHorizontal.getSelection() >= scrollBarHorizontal.getMinimum() && scrollBarHorizontal.getSelection() <= scrollBarHorizontal.getMaximum())
									scrollHorizontally(scrollBarHorizontal);
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
	public void zoomPDFDocument (int mouseRotationOrientation) {
		
		zoomMultiplier = 1.0;
		
		if (mouseRotationOrientation == 1) {
			if (zoomFactor - 2 >= 2) { 
				zoomFactor = zoomFactor - 2;
				zoomMultiplier = zoomFactorsDownwards.get(zoomFactor);
			}
		}
		else {
			if (zoomFactor + 2 <= 20) {
				zoomFactor += 2;
				zoomMultiplier = zoomFactorsUpwards.get(zoomFactor);
			}
		}
		Logger.getRootLogger().info("currently used real zoom factor: " + ((double)zoomFactor / 10));
				
		renderBuffer.setZoomFactor(((double)zoomFactor / 10));
		documentWasZoomed = true;
//		renderThread.stop();
		rectangleViewOrigin.y = Utilities.doubleToIntRoundedDown(rectangleViewOrigin.y * zoomMultiplier);
		renderBuffer.createOrSetBufferDimensions(rectangleViewOrigin.x, rectangleViewOrigin.y, true);		// re-create both main and sub buffer
		viewPanel.repaint();
//		renderThread.start();
		
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
	
	
}
