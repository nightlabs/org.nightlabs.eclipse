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

import javax.swing.JScrollPane;

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
	
	private Composite renderComposite;
	private RenderBuffer renderBuffer;
	private RenderThread renderThread;
	private PdfDocument pdfDocument;
	private ScrollBar scrollBarVertical; 
	private ScrollBar scrollBarHorizontal;
	private int scrollBarHorizontalSelectionOld;
	private int scrollBarVerticalSelectionOld;
	private int scrollBarHorizontalSelectionNew;
	private int scrollBarVerticalSelectionNew;
	private Point rectangleViewOrigin;
	private Dimension screenSize;
	private double zoomFactor = 1;
	private boolean startingPoint;
	private boolean wasZoomed = false;
	private boolean wantToZoom = false;
	
	/**
	 * The AWT frame for this composite.
	 */
	private Frame viewPanelFrame;
	/**
	 * The panel within {@link #viewPanelFrame}.
	 */
	private Panel viewPanel;
	private JScrollPane jScrollPane;

	public PdfViewerComposite(Composite parent, PdfDocument pdfDocument) {
		
		super(parent, SWT.V_SCROLL | SWT.H_SCROLL);		
		this.setLayout(new FillLayout());
		this.pdfDocument = pdfDocument;
		renderBuffer = new RenderBuffer(this, pdfDocument);
		renderComposite = new Composite(this, SWT.EMBEDDED);
//		renderBuffer = new RenderBuffer(pdfDocument);
		rectangleViewOrigin = new Point(0,0);
		startingPoint = true;
		
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
		
		renderThread = new RenderThread(this, pdfDocument, renderBuffer);
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
		
//		renderThread.notifyAll();
		getViewPanel().repaint();
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
		
//		renderThread.notifyAll();	
		getViewPanel().repaint();
	}
	
	private void setScrollbars() {
		
//		double heightDifference = 	pdfDocument.getDocumentBounds().y * renderBuffer.getZoomFactor() + 
//									(pdfDocument.getPdfFile().getNumPages() + 1) * pdfDocument.getMARGIN_BETWEEN_PAGES() * (1 - zoomFactor) - 
//									getViewPanel().getHeight();
		double heightDifference = 	pdfDocument.getDocumentHeightConverted() * renderBuffer.getZoomFactor() + 
									(pdfDocument.getPdfFile().getNumPages() + 1) * pdfDocument.getMARGIN_BETWEEN_PAGES() * (1 - zoomFactor)- 
									getViewPanel().getHeight();
		double widthDifference = 	renderBuffer.getBufferWidth() - getViewPanel().getWidth();
		
		Logger.getRootLogger().info("document bounds y: " + pdfDocument.getDocumentBounds().y + " zoom factor: " + renderBuffer.getZoomFactor());
		Logger.getRootLogger().info("panel height: " + getViewPanel().getHeight());
		Logger.getRootLogger().info("screen resolution: " + Toolkit.getDefaultToolkit().getScreenResolution());
		Logger.getRootLogger().info("converted document height: " + pdfDocument.getDocumentHeightConverted());
		
		if (heightDifference > 0) {
			final int scrollBarVerticalNumberOfSteps = Utilities.doubleToInt(heightDifference / (double)scrollingStepsDistance);	   
			Logger.getRootLogger().info("vertical scroll bar steps: " + scrollBarVerticalNumberOfSteps);	
			
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (isDisposed())
						return;
					scrollBarVertical.setVisible(true);
					scrollBarVertical.setMaximum(scrollBarVerticalNumberOfSteps + 10);
					if (wasZoomed) {						
						scrollBarVertical.setSelection(Utilities.doubleToIntRoundedDown(scrollBarVertical.getSelection() * renderBuffer.getZoomFactor()));
						wasZoomed = false;
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
			final int scrollBarHorizontalNumberOfSteps = Utilities.doubleToInt(widthDifference / scrollingStepsDistance);	   
//			Logger.getRootLogger().info("horizontal scroll bar steps: " + scrollBarHorizontalNumberOfSteps);					
			
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
		
/*		renderBuffer.drawRegion(g, 
								new Rectangle2D.Double(
									rectangleViewOrigin.x,
									rectangleViewOrigin.y, 
									getViewPanel().getWidth(),
									getViewPanel().getHeight()
									)		
								);		*/
		
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
	 * @param mouseRotationOrientation the direction the user has scrolled to
	 */
	public void zoomPDFDocument (int mouseRotationOrientation) {
		
		if (mouseRotationOrientation == 1) {
			if (zoomFactor - 0.2 >= 0.2) 
				zoomFactor -= 0.2;
		}
		else {
			if (zoomFactor + 0.2 <= 2) 
				zoomFactor += 0.2;
		}
				
		renderBuffer.setZoomFactor(zoomFactor);
		renderBuffer.createOrSetBufferDimensions(true);		// re-create both main and sub buffer
		wasZoomed = true;
		viewPanel.repaint();
		
	}	
	
	
}
