package org.nightlabs.eclipse.ui.pdfviewer.composite;


import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.nightlabs.base.ui.util.ImageUtil;
import org.nightlabs.eclipse.ui.pdfviewer.composite.internal.RenderBuffer;
import org.nightlabs.eclipse.ui.pdfviewer.composite.internal.RenderThread;
import org.nightlabs.util.IOUtil;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/**
 * This composite displays a PDF file.
 *
 * @author frederik löser - frederik at nightlabs dot de
 */
public class PdfViewerCompositeOld extends Composite {
	
	public PdfViewerCompositeOld(Composite parent, int style, File file) {
		super(parent, style);
	}
	
/*	private static final int MARGIN = 20;
	private static final int BUFFER_SIZE_BOTH = 1;
	private static final int BUFFER_SIZE_HORIZONTAL = 1;
	private static final int BUFFER_SIZE_VERTICAL = 2;
	

	private int pdfDocumentNumberOfPages;
	private double pdfPageWidthMax, pdfPageHeightMax;
	private List<PDFPage> pdfPages = new ArrayList<PDFPage>();
	private List<Double> pdfPageHeights = new ArrayList<Double>();
	private List<Double> pdfPageWidths = new ArrayList<Double>();
	private List<java.awt.Image> pdfImages = new ArrayList<java.awt.Image>();
	private BufferedImage bufferedImageAWT;
	private ImageObserver imageObserver;
	private ImageData imageData;
	private Rectangle rectangleView;
	private Point rectangleViewOrigin;
	private Point runningBufferPoint;
	private int scrollBarHorizontalSelectionOld = 0;
	private int scrollBarVerticalSelectionOld = 0;
	private int scrollBarHorizontalSelectionNew;
	private int scrollBarVerticalSelectionNew;
	private double scrollBarVerticalNumberOfSteps = 0.0;
	private int scrollBarHorizontalNumberOfSteps = 0;
	private int heightDifference;
	private double pdfDocumentHeight;
	private int pdfDocumentHeightConverted;
	private int eventType;
	private int pagesPerBuffer;
	private int runningBufferCoordinateX, runningBufferCoordinateY;
	private boolean nextPageExists = true;
	private int alreadyDrawn;
	private int currentPageIndex = 0;
	private RenderBuffer renderBuffer;
	private RenderThread renderThread;
	private Rectangle rectangleBufferedImage;
	private int processedPagesCounter;
	private int scrollingStepsDownwards, scrollingStepsUpwards;
	private int scrollingStepsToTheLeft, scrollingStepsToTheRight;
	
	
	public PdfViewerCompositeOld(Composite parent, int style, File file) {
		super(parent, style);
//		viewer = new PDFViewer(false);
//		SWT_AWT.getFrame(this).add(viewer);
		rectangleViewOrigin = new Point(0,0);
		runningBufferPoint = new Point(0,0);
				
		try {			
			// get scrollbars for this composite                                                                                                                                                                                                                                                                                                                                                                                                 	
			
			// base coordinate system
			final Point pointOrigin = new Point(0,0);		
			Rectangle rectanglePDFDocument = new Rectangle(0, 0, convert(pdfPageWidthMax), convert(pdfDocumentHeight));
	
			// virtual image (buffer)
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension screenSize = toolkit.getScreenSize();	
			rectangleBufferedImage = new Rectangle(0, 0, 
					screenSize.width * BUFFER_SIZE_HORIZONTAL, screenSize.height * BUFFER_SIZE_VERTICAL);
			GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			bufferedImageAWT = graphicsConfiguration.createCompatibleImage(screenSize.width * BUFFER_SIZE_HORIZONTAL, screenSize.height * BUFFER_SIZE_VERTICAL);
			// for testing purposes
//			bufferedImageAWT = graphicsConfiguration.createCompatibleImage((int)pdfPageWidthMax, (int) (pdfPages.get(0).getHeight() + pdfPages.get(1).getHeight() + pdfPages.get(2).getHeight() + pdfPages.get(3).getHeight() + 4 * MARGIN));
			Graphics2D g2D = bufferedImageAWT.createGraphics();			
		
			
			// draw a certain given number of pages into the buffer
//			pagesPerBuffer = bufferedImageAWT.getHeight() / (int)Math.ceil(pdfPageHeightMax);
//			for (int j = 0; j < pagesPerBuffer; j++) {
//				if (j < pdfImages.size()) {
//					g2D.drawImage(pdfImages.get(j), 0, runningBufferCoordinateY, imageObserver);
//					runningBufferCoordinateY += pdfPageHeights.get(j) + MARGIN;
//				}
//			}
			
			// (A) and (B) not used at the beginning
			// (C) draw images into the buffer as long as there is enough place in the buffer
			runningBufferPoint.y += MARGIN;
			while (runningBufferPoint.y + pdfPageHeights.get(currentPageIndex) <= rectangleBufferedImage.height) {
				System.out.println("y-coordinate: " + (runningBufferPoint.y) + "; page height of page "+currentPageIndex+": "+pdfPageHeights.get(currentPageIndex)+"; rectangleBufferedImage.height: "+rectangleBufferedImage.height);
//				System.out.println("current page index: " + currentPageIndex);
				g2D.drawImage(pdfImages.get(currentPageIndex), runningBufferPoint.x, runningBufferPoint.y, imageObserver);
				runningBufferPoint.y += pdfPageHeights.get(currentPageIndex) + MARGIN;
				if (currentPageIndex + 1  >= pdfPages.size()) {
					nextPageExists = false;
					break;
				}
				currentPageIndex++;
			}
			
			// (D) finally draw a part of the next image into the remaining buffer space
			if (nextPageExists == true) {
				// the remaining part of this image has to be drawn in the next buffer
				alreadyDrawn = rectangleBufferedImage.height - runningBufferPoint.y;				
//				g2D.drawImage(	pdfImages.get(currentPageIndex),
//								// destination (remaining part of the current buffer)
//								runningBufferPoint.x, runningBufferPoint.y,
//								runningBufferPoint.x + convert(pdfPageWidths.get(currentPageIndex)), runningBufferPoint.y + alreadyDrawn,								 
//								// source (part of the current image)
//								0, 0,
//								convert(pdfPageWidths.get(currentPageIndex)), alreadyDrawn, 								
//							
//								imageObserver
//								);
				
				g2D.drawImage(pdfImages.get(currentPageIndex), runningBufferPoint.x, runningBufferPoint.y, imageObserver);				
				
			}

			// (for testing purposes)
//			g2D.drawImage(pdfImages.get(0), 0, 1 * 20, imageObserver);
//			g2D.drawImage(pdfImages.get(1), 0, (int) (pdfPageHeights.get(0) +  2 * 20), imageObserver);
//			g2D.drawImage(pdfImages.get(2), 0, (int) (pdfPageHeights.get(0) + pdfPageHeights.get(1) + 3 * 20), imageObserver);
//			g2D.drawImage(pdfImages.get(3), 0, (int) (pdfPageHeights.get(0) + pdfPageHeights.get(1) + pdfPageHeights.get(2) + 4 * 20), imageObserver);
			
			// convert from abstract windowing toolkit to standard widget toolkit
			System.out.println("now converting image...");
			imageData = ImageUtil.convertToSWT((BufferedImage) bufferedImageAWT);
			System.out.println("image converted");
				
			// real image (view)	
			
			pdfDocumentHeightConverted = convert(pdfDocumentHeight);
			
			addPaintListener(new PaintListener() {
				private Image bufferedImageSWT;

				@Override
				public void paintControl(PaintEvent event) {
//					System.out.println("painting control...");
					GC gc = event.gc;
					rectangleView = gc.getClipping();
					bufferedImageSWT = new Image(gc.getDevice(), imageData);
					
//					System.out.println("rectangleViewOrigin.y: " + rectangleViewOrigin.y);
//					System.out.println("rectangleView.height: " + rectangleView.height);
//					System.out.println("rectangleBufferedImage.height: " + rectangleBufferedImage.height);
					System.out.println("difference: " + (rectangleBufferedImage.height - rectangleViewOrigin.y - rectangleView.height));
					
					if ((rectangleViewOrigin.x + rectangleView.width <= rectangleBufferedImage.width) &&
							(rectangleViewOrigin.y + rectangleView.height <= rectangleBufferedImage.height)) {
						// The current view rectangle is completely contained in the buffer.
//						System.out.println("The current view rectangle is completely contained in the buffer.");						
						
						heightDifference = pdfDocumentHeightConverted - rectangleView.height;

						if (heightDifference > 0) {
							scrollBarVerticalNumberOfSteps = (double) heightDifference / MARGIN;						
							scrollBarVertical.setMaximum((int)Math.ceil(scrollBarVerticalNumberOfSteps));
						}
						else
							scrollBarVertical.setMaximum(0);	

//						System.out.println("scrollBarVertical maximum: " + scrollBarVertical.getMaximum());

						gc.drawImage(	bufferedImageSWT, 
										// source (certain rectangle in the current buffer)
										rectangleViewOrigin.x, rectangleViewOrigin.y, 
										convert(pdfPageWidthMax), rectangleView.height, 
										// destination (screen)
										(rectangleView.width - convert(pdfPageWidthMax)) / 2, 0, 
										convert(pdfPageWidthMax), rectangleView.height
										);		
						
						// for testing purposes						
//						gc.drawImage(	bufferedImageSWT, 
//										rectangleViewOrigin.x, rectangleViewOrigin.y, 
//										(int) pdfPageWidthMax, rectangleView.height, 
//										(rectangleView.width - (int) pdfPageWidthMax) / 2, 0, 
//										(int) pdfPageWidthMax, rectangleView.height
//										);		
												
						bufferedImageSWT.dispose();

					}
					else {
						// The current view rectangle is NOT completely contained in the buffer.
						// => re-render buffer (expensive operation) on a background thread
						// => draw dummy (empty pages) (fast operation), for the beginning, we keep it empty.
//						System.out.println("The current view rectangle is not completely contained in the buffer.");
						
						// set the origin to 0 again (as we are using a new buffer now)
//						rectangleViewOrigin.x = 0;
//						rectangleViewOrigin.y = 0;					
						
						// render new buffer
						System.out.println("currentPageIndex: " + currentPageIndex);
						renderThread.needRendering(bufferedImageAWT, rectangleViewOrigin, rectangleView, 
								pdfPages, pdfPageHeights, pdfPageWidths, pdfPageWidthMax, pdfImages, currentPageIndex, 
								alreadyDrawn, scrollingStepsDownwards, scrollingStepsUpwards);						
						
					}
					
					// TODO DRAW
	//				
	//				
	//				gc.fillRectangle(rect);
				}
			});
			
	//		java.awt.Image image = page.getImage(rectanglePDFPage.width, rectanglePDFPage.height, rectanglePDFPage, null);
			
			
	//		Image image = page.getImage(pdfPageWidth, pdfPageHeight, rectanglePDFPage, new ImageObserver() {
	//			@Override
	//			public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
	//				// TODO Auto-generated method stub
	//				return false;
	//			}
	//		});	
			
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
			
			scrollBarHorizontal.addListener(eventType, new Listener() {
				@Override
				public void handleEvent(Event arg0) {
					// TODO Auto-generated method stub					
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
			
			scrollBarVertical.addListener(eventType, new Listener() {
				@Override
				public void handleEvent(Event arg0) {
					// TODO Auto-generated method stub					
				}				
			});			
						
		}
		catch (IOException exception) {
			System.out.println(exception.getStackTrace());
		}
		
	}
	
	private void scrollVertically (ScrollBar scrollBarVertical) {
		scrollBarVerticalSelectionNew = scrollBarVertical.getSelection();
		
		if (scrollBarVerticalSelectionNew > scrollBarVerticalSelectionOld) {			
			rectangleViewOrigin.y += (scrollBarVerticalSelectionNew - scrollBarVerticalSelectionOld) * MARGIN;
			setScrollingStepsDownwards(scrollBarVerticalSelectionNew - scrollBarVerticalSelectionOld);
			scrollBarVerticalSelectionOld = scrollBarVerticalSelectionNew;					
		}
		else {	
			rectangleViewOrigin.y -= (scrollBarVerticalSelectionOld - scrollBarVerticalSelectionNew) * MARGIN;
			setScrollingStepsUpwards(scrollBarVerticalSelectionOld - scrollBarVerticalSelectionNew);
			scrollBarVerticalSelectionOld = scrollBarVerticalSelectionNew;				
		}
		
		redraw();		
	}
	
	private void scrollHorizontally(ScrollBar scrollBarHorizontal) {
		scrollBarHorizontalSelectionNew = scrollBarHorizontal.getSelection();
		
		if (scrollBarHorizontalSelectionNew > scrollBarHorizontalSelectionOld) {
			rectangleViewOrigin.x += (scrollBarHorizontalSelectionNew - scrollBarHorizontalSelectionOld) * MARGIN;
			setScrollingStepsToTheRight(scrollBarHorizontalSelectionNew - scrollBarHorizontalSelectionOld);
			scrollBarHorizontalSelectionOld = scrollBarHorizontalSelectionNew;			
		}
		else {
			rectangleViewOrigin.x -= (scrollBarHorizontalSelectionOld - scrollBarHorizontalSelectionNew) * MARGIN;
			setScrollingStepsToTheLeft(scrollBarHorizontalSelectionOld - scrollBarHorizontalSelectionNew);
			scrollBarHorizontalSelectionOld = scrollBarHorizontalSelectionNew;			
		}			
		redraw();		
	}
		

		
	public void getPDFDocumentData (final PDFFile pdfDocument) {
		
		pdfDocumentNumberOfPages = pdfDocument.getNumPages();
		
		for (int j = 0; j < pdfDocumentNumberOfPages; j++) {			
			// get the corresponding PDF page and its properties (height and width), beginning with index one, not zero !!!!!
			PDFPage pdfPage = pdfDocument.getPage(j + 1);
			double pdfPageHeight = pdfPage.getBBox().getHeight();
			double pdfPageWidth = pdfPage.getBBox().getWidth();
			// maintain maximum width and height of all PDF pages
			if (pdfPageWidth > pdfPageWidthMax)
				pdfPageWidthMax = pdfPageWidth;
			if (pdfPageHeight > pdfPageHeightMax)
				pdfPageHeightMax = pdfPageHeight;
			// maintain PDF pages and their corresponding page heights and page widths 
			pdfPages.add(pdfPage);
			pdfPageHeights.add(pdfPageHeight);
			pdfPageWidths.add(pdfPageWidth);			
			// maintain abstract windowing toolkit images (one image per PDF page)						
//			pdfImages.add(	pdfPages.get(j).getImage(
//							convert(pdfPageWidths.get(j)), 
//							convert(pdfPageHeights.get(j)),
//							new Rectangle2D.Double(0, 0, pdfPageWidths.get(j), pdfPageHeights.get(j)), null)
//							);	
			// maintain the PDF document height (1/2)
			pdfDocumentHeight += pdfPageHeight;
		}				
		// maintain the PDF document height (2/2)
		pdfDocumentHeight += (pdfDocumentNumberOfPages - 1) * MARGIN;		
	}	
	
	private int convert(double pageProperty) {
		return (int)Math.ceil(pageProperty);
	}

	public ImageData getImageData() {
		return imageData;
	}

	public void setImageData(ImageData imageData) {
		this.imageData = imageData;
	}

	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

	public void setCurrentPageIndex(int currentPageIndex) {
		this.currentPageIndex = currentPageIndex;
//		System.out.println("current page index was set to: " + currentPageIndex);
	}

	public Point getRectangleViewOrigin() {
		return rectangleViewOrigin;
	}

	public void setRectangleViewOrigin(Point rectangleViewOrigin) {
		this.rectangleViewOrigin = rectangleViewOrigin;
	}	
	
	public void setRectangleOriginToDefault () {
		this.rectangleViewOrigin.x = 0;
		this.rectangleViewOrigin.y = 0;
	}

	public BufferedImage getBufferedImageAWT() {
		return bufferedImageAWT;
	}

	public void setBufferedImageAWT(BufferedImage bufferedImageAWT) {
		this.bufferedImageAWT = bufferedImageAWT;
	}

	public int getAlreadyDrawn() {
		return alreadyDrawn;
	}

	public void setAlreadyDrawn(int alreadyDrawn) {
		this.alreadyDrawn = alreadyDrawn;
	}

	public int getScrollingStepsDownwards() {
		return scrollingStepsDownwards;
	}

	public void setScrollingStepsDownwards(int scrollingStepsDownwards) {
		this.scrollingStepsDownwards = scrollingStepsDownwards;
	}

	public int getScrollingStepsUpwards() {
		return scrollingStepsUpwards;
	}

	public void setScrollingStepsUpwards(int scrollingStepsUpwards) {
		this.scrollingStepsUpwards = scrollingStepsUpwards;
	}

	public int getScrollingStepsToTheLeft() {
		return scrollingStepsToTheLeft;
	}

	public void setScrollingStepsToTheLeft(int scrollingStepsToTheLeft) {
		this.scrollingStepsToTheLeft = scrollingStepsToTheLeft;
	}

	public int getScrollingStepsToTheRight() {
		return scrollingStepsToTheRight;
	}

	public void setScrollingStepsToTheRight(int scrollingStepsToTheRight) {
		this.scrollingStepsToTheRight = scrollingStepsToTheRight;
	}*/
	
}


