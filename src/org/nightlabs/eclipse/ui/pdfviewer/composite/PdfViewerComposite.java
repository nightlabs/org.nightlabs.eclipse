package org.nightlabs.eclipse.ui.pdfviewer.composite;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

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
import org.nightlabs.eclipse.ui.pdfviewer.util.Conversion;


public class PdfViewerComposite extends Composite {
	
//	private static final int MARGIN_BETWEEN_PAGES = 20;
	private static final int SCROLLING_STEPS_DISTANCE = 10;
	
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
	private ImageObserver imageObserver;
	private Point rectangleViewOrigin;
	private Dimension screenSize;
	
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
		renderComposite = new Composite(this, SWT.EMBEDDED);
		renderBuffer = new RenderBuffer(this, pdfDocument);
//		renderBuffer.setPdfDocument(pdfDocument);  // from render thread
		rectangleViewOrigin = new Point(0,0);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		setScreenSize(toolkit.getScreenSize());		
				
		scrollBarVertical = this.getVerticalBar();	
		scrollBarHorizontal = this.getHorizontalBar();	

		imageObserver = new ImageObserver() {
			@Override
			public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y, int width, int height) {					
				return false;
			}
		};	
		
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
				// TODO set size of RenderBuffer - optimize this not to do it every time, but keep the buffer a bit bigger and check when we exceed ranges
			}
		});
		viewPanelFrame.add(viewPanel);
		setViewPanel(viewPanel);
		
		scrollBarHorizontal.addSelectionListener(new SelectionListener() {
				@Override
		    	public void widgetSelected(SelectionEvent event) {
//					System.out.println("scrolling horizontally");
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
//		    		System.out.println("scrolling vertically");
	    		scrollVertically((ScrollBar)event.widget);
	    	}
			@Override
	    	public void widgetDefaultSelected(SelectionEvent event) {
		        scrollVertically((ScrollBar)event.widget);
	    	}
	    });
		
		MouseWheelListener mouseWheelListener = new MouseWheelListenerImpl();
		viewPanelFrame.addMouseWheelListener(mouseWheelListener);
//		viewPanel.addMouseWheelListener(mouseWheelListener);
		
//		addMouseWheelListener( new MouseWheelListener() {
//			@Override
//			public void mouseScrolled(MouseEvent mouseEvent) {
//				
//				System.out.println("mouse was scrolled");
////				if (mouseEvent.)
//				scrollBarVertical.setSelection(scrollBarVertical.getSelection() + 1);
//				scrollVertically(scrollBarVertical);				
//			}
//						
//		});		
		
		renderThread = new RenderThread(this, pdfDocument, renderBuffer);
//		renderBuffer.createOrSetBufferRectangle(0, 0, Conversion.convert(pdfDocument.getDocumentBounds().x), getScreenSize().height * 3);   // from render thread
//		viewPanel.repaint();   // from render thread
	}
	
	private void scrollVertically (ScrollBar scrollBarVertical) {
		scrollBarVerticalSelectionNew = scrollBarVertical.getSelection();
		
		if (scrollBarVerticalSelectionNew > scrollBarVerticalSelectionOld) {			
			rectangleViewOrigin.y += (scrollBarVerticalSelectionNew - scrollBarVerticalSelectionOld) * SCROLLING_STEPS_DISTANCE;
			scrollBarVerticalSelectionOld = scrollBarVerticalSelectionNew;					
		}
		else {	
			rectangleViewOrigin.y -= (scrollBarVerticalSelectionOld - scrollBarVerticalSelectionNew) * SCROLLING_STEPS_DISTANCE;
			scrollBarVerticalSelectionOld = scrollBarVerticalSelectionNew;				
		}
		
//		renderThread.notifyAll();
		getViewPanel().repaint();
	}
	
	private void scrollHorizontally(ScrollBar scrollBarHorizontal) {
		scrollBarHorizontalSelectionNew = scrollBarHorizontal.getSelection();
		
		if (scrollBarHorizontalSelectionNew > scrollBarHorizontalSelectionOld) {
			rectangleViewOrigin.x += (scrollBarHorizontalSelectionNew - scrollBarHorizontalSelectionOld) * SCROLLING_STEPS_DISTANCE;
			scrollBarHorizontalSelectionOld = scrollBarHorizontalSelectionNew;			
		}
		else {
			rectangleViewOrigin.x -= (scrollBarHorizontalSelectionOld - scrollBarHorizontalSelectionNew) * SCROLLING_STEPS_DISTANCE;
			scrollBarHorizontalSelectionOld = scrollBarHorizontalSelectionNew;			
		}			
		
//		renderThread.notifyAll();	
		getViewPanel().repaint();
	}
	
	/**
	 * Paint the {@link #viewPanel} whenever it needs repaint.
	 *
	 * @param g the graphics to draw into.
	 */
	private void paintViewPanel(Graphics2D g) {
//		g.setBackground(Color.BLUE);
//		g.fillRect(0, 0, 10, 10);
//		g.drawImage(renderBuffer.getBufferedImage(), null, 0, 0);
		
		double heightDifference = pdfDocument.getDocumentBounds().y - g.getClipBounds().height; 
		double widthDifference = pdfDocument.getDocumentBounds().x - g.getClipBounds().width;
		System.out.println("document bounds: " + pdfDocument.getDocumentBounds().y + " clip bounds: " + g.getClipBounds().height + " height difference: " + heightDifference);

		if (heightDifference > 0) {
			final int scrollBarVerticalNumberOfSteps = Conversion.convert(heightDifference / SCROLLING_STEPS_DISTANCE);	   System.out.println("vertical scroll bar steps: " + scrollBarVerticalNumberOfSteps );					
			
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					scrollBarVertical.setVisible(true);
					scrollBarVertical.setMaximum(scrollBarVerticalNumberOfSteps + 10);
				}
			});				
		}
		else {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					scrollBarVertical.setVisible(false);
				}
			});
		}		
		
		if (widthDifference > 0) {
			final int scrollBarHorizontalNumberOfSteps = Conversion.convert(widthDifference / SCROLLING_STEPS_DISTANCE);	   System.out.println("horizontal scroll bar steps: " + scrollBarHorizontalNumberOfSteps );					
			
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					scrollBarHorizontal.setVisible(true);
					scrollBarHorizontal.setMaximum(scrollBarHorizontalNumberOfSteps + 10);
				}
			});				
		}
		else {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					scrollBarHorizontal.setVisible(false);
				}
			});
		}				
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
//				System.out.println("scrollbar minimum: " + scrollBarVertical.getMinimum() + " scrollbar maximum: " + scrollBarVertical.getMaximum());
//				System.out.println("increment: " + scrollBarVertical.getIncrement() + " page increment: " + scrollBarVertical.getPageIncrement());
//				System.out.println("selection: " + scrollBarVertical.getSelection());
			}
		});
		
		
		renderBuffer.drawRegion(	g, 
									new Rectangle2D.Double(
											rectangleViewOrigin.x,
//											getScreenSize().width / 2 - g.getClipBounds().width / 2 + rectangleViewOrigin.x,
											rectangleViewOrigin.y, 
											g.getClipBounds().width,
											g.getClipBounds().height											
									)		
								);		
		
//		System.out.println("clip bounds width: " + g.getClipBounds().width + "; clip bounds height: " + g.getClipBounds().height);
//		System.out.println("screen size width: " + getScreenSize().width + "; screen size height: " + getScreenSize().height);
				
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
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (scrollBarVertical.isVisible() == true) {	// vertical scroll bar has priority (if visible)
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
