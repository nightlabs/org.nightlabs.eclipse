package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;
import org.nightlabs.eclipse.ui.pdfviewer.util.Utilities;

import com.sun.pdfview.PDFPage;


public class RenderBuffer {

	private static final double bufferDistance = 0.6; 
	private PdfViewerComposite pdfViewerComposite;
	private PdfDocument pdfDocument;
	private GraphicsConfiguration graphicsConfiguration;
	private BufferedImage bufferedImageMain = null; 
	private BufferedImage bufferedImageSub = null;
	private Rectangle2D.Double bufferedImageMainBounds = null;
	private Rectangle2D.Double bufferedImageSubBounds = null;
//	private int bufferWidth;
//	private int bufferHeight;
	private double bufferWidth;
	private double bufferHeight;
	private int round;
	private double zoomFactor;
	private boolean buffersWereJustInitialized = false;
	
	
	public RenderBuffer(PdfViewerComposite pdfViewerComposite, PdfDocument pdfDocument) {
		this.pdfViewerComposite = pdfViewerComposite;
		this.pdfDocument = pdfDocument;
//		this.bufferWidth = Utilities.doubleToInt(pdfDocument.getDocumentBounds().x * 2);
//		this.bufferHeight = Toolkit.getDefaultToolkit().getScreenSize().height * 2;
		this.bufferWidth = pdfDocument.getDocumentBounds().x * 2;
		this.bufferHeight = Toolkit.getDefaultToolkit().getScreenSize().height * 2;
		this.zoomFactor = 1;
	}

	/**
	 * Creates a new main and a new sub buffer when opening a PDF document (x and y are zero).
	 * In the case the document was already opened a new sub buffer is created if necessary (independent of the values of x and y)
	 *
	 * @param completeRecreation true if both main and sub buffer have to be re-created
	 */
	public void createOrSetBufferDimensions(boolean completeRecreation) {
		createOrSetBufferDimensions(0, 0, completeRecreation);
	}
		
	/**
	 * Creates or sets main and sub buffer in the case new buffers are needed.
	 *
	 * @param posX the x-coordinate of the upper left corner of the buffers that have to be created.
	 * @param posY the y-coordinate of the upper left corner of the main buffer that has to be created (the y-coordinate of the sub buffer depends on this value)
	 * @param completeRecreation true if both main and sub buffer have to be (re-)created, otherwise false
	 */
	public synchronized void createOrSetBufferDimensions(int posX, int posY, boolean completeRecreation) {
		graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		
		if (completeRecreation == true) {
			Logger.getRootLogger().info("creating or re-creating both main and sub buffer");				
			bufferedImageMain = graphicsConfiguration.createCompatibleImage(Utilities.doubleToInt(bufferWidth), Utilities.doubleToInt(bufferHeight));	// perhaps to omit
			bufferedImageMainBounds = new Rectangle2D.Double(posX, posY, bufferWidth, bufferHeight);
			bufferedImageMain = renderBufferedImage(bufferedImageMain, bufferedImageMainBounds);
			
			renderBufferedImageSub();
			
		}
		else {
			Logger.getRootLogger().info("re-creating only the sub buffer");				
			bufferedImageMain = graphicsConfiguration.createCompatibleImage(Utilities.doubleToInt(bufferWidth), Utilities.doubleToInt(bufferHeight));	// perhaps to omit
			bufferedImageMain = bufferedImageSub;
			bufferedImageMainBounds.setRect(bufferedImageSubBounds);		
			
			renderBufferedImageSub();
			
		}	
		printToImageFile(bufferedImageMain, "main buffer " + round);		
	}

	/**
	 * Initializes main and sub buffer when opening the document 
	 */
	public void initBuffering() {

		graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		bufferedImageMain = graphicsConfiguration.createCompatibleImage(Utilities.doubleToInt(bufferWidth), Utilities.doubleToInt(bufferHeight));
		bufferedImageMainBounds = new Rectangle2D.Double(0, 0, bufferWidth, bufferHeight);    // first buffer begins at position (0,0) of the document
		bufferedImageSub = graphicsConfiguration.createCompatibleImage(Utilities.doubleToInt(bufferWidth), Utilities.doubleToInt(bufferHeight));
		bufferedImageSubBounds = new Rectangle2D.Double(0, bufferDistance * bufferHeight, bufferWidth, bufferHeight);	

		buffersWereJustInitialized = true;
	}

	/**
	 * Renders the given sub buffer with its buffer bounds
	 */
	private void renderBufferedImageSub() {
		
		int posX = 0;
		bufferedImageSub = graphicsConfiguration.createCompatibleImage(Utilities.doubleToInt(bufferWidth), Utilities.doubleToInt(bufferHeight));
		bufferedImageSubBounds = new Rectangle2D.Double(posX, bufferedImageMainBounds.getY() + bufferDistance * bufferHeight, bufferWidth, bufferHeight);
		
		if (bufferedImageMainBounds.getY() + bufferDistance * bufferHeight <= pdfDocument.getDocumentBounds().y * zoomFactor) {
			bufferedImageSub = renderBufferedImage(bufferedImageSub, bufferedImageSubBounds);
			buffersWereJustInitialized = false;
			printToImageFile(bufferedImageSub, "sub buffer " + round);			
		}
		else {
			// we do not have to render the sub buffer in this case
		}	
	}	

	/**
	 * Renders a buffered image by searching those PDF pages of the PDF document that are lying inside the 
	 * buffer bounds of this buffered image. 
	 *
	 * @param bufferedImage the buffered image that has to be rendered
	 * @param bufferedImageBufferBounds the dimensions of the buffered image that has to be rendered
	 * @return the rendered image
	 */
	protected synchronized BufferedImage renderBufferedImage(BufferedImage bufferedImage, Rectangle2D bufferedImageBufferBounds) {
		
//		Rectangle2D pageBoundsZoomed = new java.awt.Rectangle(0, 0, 0, 0);
		Rectangle2D.Double pageBoundsZoomed = new Rectangle2D.Double(0, 0, 0, 0);
		Rectangle2D.Double pageBounds;
		boolean endOfPageIsInBuffer;
		boolean beginningOfPageIsInBuffer;
		int bufferCoordinateY = 0;
		double direction = 0;		
		

		// clear buffer => fill grey
		Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
		graphics.setBackground(pdfViewerComposite.getViewPanel().getBackground());
		graphics.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

/*		bufferedImageBufferBounds.setRect(	bufferedImageBufferBounds.getX(), 
											bufferedImageBufferBounds.getY() * zoomFactor, 
											bufferedImageBufferBounds.getWidth(), 
											bufferedImageBufferBounds.getHeight()
											);		*/
		
		// get page numbers of those pages that are lying in buffer bounds of currently considered buffer
		List<Integer> bufferedImagePageNumbers = pdfDocument.getVisiblePages(bufferedImageBufferBounds, zoomFactor);
		Collections.sort(bufferedImagePageNumbers);		
		
		for (Integer pageNumber : bufferedImagePageNumbers) {
//			Logger.getRootLogger().info("get page " + pageNumber);
			beginningOfPageIsInBuffer = false;
			endOfPageIsInBuffer = false;
			
			if (zoomFactor >= 0.2 && zoomFactor <= 0.8)
				direction = 1;
			else
				if (zoomFactor >= 1.2 && zoomFactor <= 2)
					direction = - 1;
			
//			Rectangle2D pageBounds = pdfDocument.getPageBounds(pageNumber);
			pageBounds = pdfDocument.getPageBounds(pageNumber);
			pageBoundsZoomed.setRect(	pageBounds.getX() + direction * Math.abs((zoomFactor - 1) * pageBounds.getWidth()) / 2,
										pageBounds.getY() * zoomFactor, 
										pageBounds.getWidth() * zoomFactor, 
										pageBounds.getHeight() * zoomFactor
										);		
			PDFPage pdfPage = pdfDocument.getPdfFile().getPage(pageNumber);	
//			pdfPage.getBBox().setRect(0, 0, pdfPage.getBBox().getWidth() * zoomFactor, pdfPage.getBBox().getHeight() * zoomFactor);
//			Logger.getRootLogger().info("PDF Page height in renderBufferedImage: " + pdfPage.getHeight());

			if (bufferedImageBufferBounds.getY() <= pageBoundsZoomed.getY() && pageBoundsZoomed.getY() < bufferedImageBufferBounds.getY() + bufferedImageBufferBounds.getHeight())
				beginningOfPageIsInBuffer = true;
			if (bufferedImageBufferBounds.getY() < pageBoundsZoomed.getY() + pageBoundsZoomed.getHeight() && pageBoundsZoomed.getY() + pageBoundsZoomed.getHeight() <= bufferedImageBufferBounds.getY() + bufferedImageBufferBounds.getHeight())
				endOfPageIsInBuffer = true;		
			
			// (A) get remaining part of this image as only its remaining part is lying in buffer 
			if (beginningOfPageIsInBuffer == false && endOfPageIsInBuffer == true) {
//				Logger.getRootLogger().info("get remaining part of this image");
				final boolean[] bufferFinished = new boolean[1];
				bufferFinished[0] = false;
				Image pdfImage = pdfPage.getImage(
//						Utilities.doubleToInt(/*pageBounds.getWidth() * zoomFactor*/pageBoundsZoomed.getWidth()), 
//						Utilities.doubleToInt(/*(pageBounds.getHeight() - (bufferedImageBufferBounds.getY() - pageBounds.getY())) * zoomFactor*/pageBoundsZoomed.getHeight() - (bufferedImageBufferBounds.getY() - pageBoundsZoomed.getY())), 
						Utilities.doubleToIntRoundedDown(/*pageBounds.getWidth() * zoomFactor*/pageBoundsZoomed.getWidth()), 
						Utilities.doubleToIntRoundedDown(/*(pageBounds.getHeight() - (bufferedImageBufferBounds.getY() - pageBounds.getY())) * zoomFactor*/pageBoundsZoomed.getHeight() - (bufferedImageBufferBounds.getY() - pageBoundsZoomed.getY())), 
						new Rectangle2D.Double(	0, 													
												0,	// consideration of PDF images begins from bottom left point upwards, not from top left point downwards (old y-coordinate of this image: bufferedImageMainBounds.getY() - pageBounds.getY())
												pageBoundsZoomed.getWidth() / zoomFactor, 
												(pageBoundsZoomed.getHeight() - (bufferedImageBufferBounds.getY() - pageBoundsZoomed.getY())) / zoomFactor
												),
						new ImageObserver() {
							@Override
							public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {								
								if (infoflags == 32) 
									bufferFinished[0] = true;
								return true;
							}					
				});
				while (!bufferFinished[0]) {
					try {
						Thread.sleep(10);
					} 
					catch (InterruptedException e) {
					}					
				}
				
				printToImageFile(pdfImage, "remaining part of image " + round);
								
				// and draw it into the buffer
				graphics.drawImage(	pdfImage, 
									bufferedImage.getWidth() / 2 - Utilities.doubleToInt(zoomFactor * pageBounds.getWidth() / 2), 
									0,	// bufferCoordinateY is zero
									null
									);    
				bufferCoordinateY += pdfImage.getHeight(null);				
			}
			
			
			
			// (B) get whole part of this image as it is completely lying in buffer 
			if (beginningOfPageIsInBuffer == true && endOfPageIsInBuffer == true) {
//				Logger.getRootLogger().info("get whole part of this image");
//				Logger.getRootLogger().info("page bounds width: " + pageBounds.getWidth() + "; page bounds height: " + pageBounds.getHeight());
				final boolean[] bufferFinished = new boolean[1];
				bufferFinished[0] = false;				
				Image pdfImage = pdfPage.getImage(
//						Utilities.doubleToInt(pageBoundsZoomed.getWidth()), 
//						Utilities.doubleToInt(pageBoundsZoomed.getHeight()),
						Utilities.doubleToIntRoundedDown(pageBoundsZoomed.getWidth()), 
						Utilities.doubleToIntRoundedDown(pageBoundsZoomed.getHeight()),
						new Rectangle2D.Double(	0,
												0,
												pageBounds.getWidth(),
												pageBounds.getHeight()
												),
						new ImageObserver() {
							@Override
							public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) { 								
								if (infoflags == 32) 
									bufferFinished[0] = true;
								return true;
							}					
				});
				
				while (!bufferFinished[0]) {
					try {
						Thread.sleep(10);
					}
					catch (InterruptedException e) {
					}					
				}	
				
				printToImageFile(pdfImage, "imageWholePart" + round);
				
				// and draw it into the buffer
				bufferCoordinateY += 20;
				graphics.drawImage(	pdfImage, 
									bufferedImage.getWidth() / 2 - Utilities.doubleToInt(zoomFactor * pageBounds.getWidth() / 2), 
									bufferCoordinateY, 
									null
									);
				bufferCoordinateY += pdfImage.getHeight(null);
			
			}
			
			
			
			// (C) get upper part of this image as only its upper part is lying in buffer 
			if (beginningOfPageIsInBuffer == true && endOfPageIsInBuffer == false) {
//				Logger.getRootLogger().info("get upper part of this image");
				final boolean[] bufferFinished = new boolean[1];
				bufferFinished[0] = false;					
				Image pdfImage = pdfPage.getImage(
//						Utilities.doubleToInt(pageBoundsZoomed.getWidth()), 
//						Utilities.doubleToInt(bufferedImageBufferBounds.getY() + bufferedImageBufferBounds.getHeight() - pageBoundsZoomed.getY()),
						Utilities.doubleToIntRoundedDown(pageBoundsZoomed.getWidth()), 
						Utilities.doubleToIntRoundedDown(bufferedImageBufferBounds.getY() + bufferedImageBufferBounds.getHeight() - pageBoundsZoomed.getY()),
						new Rectangle2D.Double( 0,		
												// consideration of PDF images begins from bottom left point upwards, not from top left point downwards (old value: 0)
												pageBoundsZoomed.getHeight() - (bufferedImageBufferBounds.getY() + bufferedImageBufferBounds.getHeight() - pageBoundsZoomed.getY()),	     
												pageBoundsZoomed.getWidth() / zoomFactor,
												(bufferedImageBufferBounds.getY() + bufferedImageBufferBounds.getHeight() - pageBoundsZoomed.getY()) / zoomFactor
												),
						new ImageObserver() {
							@Override
							public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
								if (infoflags == 32) 
									bufferFinished[0] = true;
								return true;
							}					
				});
				
				while (!bufferFinished[0]) {
					try {
						Thread.sleep(10);
					} 
					catch (InterruptedException e) {
					}					
				}			
				
				printToImageFile(pdfImage, "upper part of image " + round);
				
				// and draw it into the buffer
				bufferCoordinateY += 20;
				graphics.drawImage(	pdfImage, 
									bufferedImage.getWidth() / 2 - Utilities.doubleToInt(zoomFactor * pageBounds.getWidth() / 2),  
									bufferCoordinateY, 
									null
									);				
			}		

			
						
			// (D) get part of this image as beginning AND end of it are not lying in buffer (see special case in nested intervals algorithm)
			if (beginningOfPageIsInBuffer == false && endOfPageIsInBuffer == false) {
//				Logger.getRootLogger().info("get part of this image");
				final boolean[] bufferFinished = new boolean[1];
				bufferFinished[0] = false;	
				Image pdfImage = pdfPage.getImage(
//						Utilities.doubleToInt(pageBounds.getWidth() * zoomFactor), 
//						Utilities.doubleToInt(bufferedImageBufferBounds.getHeight() * zoomFactor),
						Utilities.doubleToIntRoundedDown(pageBoundsZoomed.getWidth()), 
						Utilities.doubleToIntRoundedDown(bufferedImageBufferBounds.getHeight()),
						new Rectangle2D.Double(	0,
												bufferedImageBufferBounds.getY() - pageBoundsZoomed.getY(),
												pageBoundsZoomed.getWidth(), 
												bufferedImageBufferBounds.getHeight()
												),
						new ImageObserver() {
							@Override
							public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
								if (infoflags == 32)
									bufferFinished[0] = true;
								return true;
							}					
				});
				
				while (!bufferFinished[0]) {
					try {
						Thread.sleep(10);
					} 
					catch (InterruptedException e) {
					}					
				}	
				
				printToImageFile(pdfImage, "imagePart");
				
				// and draw it into the buffer
				
				graphics.drawImage(	pdfImage, 
									bufferedImage.getWidth() / 2 - Utilities.doubleToInt(zoomFactor * pageBounds.getWidth() / 2),  
									0, // bufferCoordinateY is zero
									null
									);    
			}			
		}
		
		round++;
		return bufferedImage;
	}

	/**
	 * Draws a certain part (the region of interest) of the main buffer onto the screen.  
	 *
	 * @param graphics2D the graphics context of the panel 
	 * @param region the region of interest of the document that shall be drawn onto the screen
	 */	
	public synchronized void drawRegion(Graphics2D graphics2D, /*Rectangle2D*/java.awt.Rectangle region) {
		
		// draw current view (region) of buffer onto the screen
		
		// works but uses up-rounding
/*		graphics2D.drawImage(getBufferedImageMain(), 
				// destination rectangle (screen) given as TWO POINTS d1 and d2 (not width and height)				
				Math.max(0, Utilities.doubleToInt(region.getWidth() / 2 - bufferWidth / 2)),  								0, 
				Utilities.doubleToInt(Math.max(0 + bufferWidth, region.getWidth() / 2 - bufferWidth / 2 + bufferWidth)), 	Utilities.doubleToInt(region.getHeight()),
				// source (certain rectangle in the current buffer) given as TWO POINTS s1 and s2 (not width and height)
				Utilities.doubleToInt(region.getX() - bufferedImageMainBounds.getX()), 					Utilities.doubleToInt(region.getY() * zoomFactor - bufferedImageMainBounds.getY()),		
				Utilities.doubleToInt(region.getX() - bufferedImageMainBounds.getX() + bufferWidth), 	Utilities.doubleToInt(region.getY() * zoomFactor - bufferedImageMainBounds.getY() + region.getHeight()),
				null
				);		*/

		graphics2D.drawImage(getBufferedImageMain(), 
				// destination rectangle (screen) given as TWO POINTS d1 and d2 (not width and height)				
				Math.max(0, Utilities.doubleToIntRoundedDown(region.getWidth() / 2 - bufferWidth / 2)),  	
				0, 
				Utilities.doubleToIntRoundedDown(Math.max(0 + bufferWidth, region.getWidth() / 2 - bufferWidth / 2 + bufferWidth)), 
				region.height,
				// source (certain rectangle in the current buffer) given as TWO POINTS s1 and s2 (not width and height)
				Utilities.doubleToIntRoundedDown(region.getX() - bufferedImageMainBounds.getX()), 
				Utilities.doubleToIntRoundedDown(region.getY() - bufferedImageMainBounds.getY()),		
				Utilities.doubleToIntRoundedDown(region.getX() - bufferedImageMainBounds.getX() + bufferWidth), 
				Utilities.doubleToIntRoundedDown(region.getY() - bufferedImageMainBounds.getY() + region.getHeight()),
				null
				);		
		
	}
	
	/**
	 * Sets the background of a given buffered image to a certain color by setting every pixel separately (expensive).
	 */
	public void bufferedImageDrawBackground () {
		for (int j = 0; j < Utilities.doubleToInt(pdfDocument.getDocumentBounds().x * zoomFactor); j++) {
			for (int i = 0; i < bufferHeight; i++)
				bufferedImageMain.setRGB(j, i, - 5000000);
		}
	}
	
	/**
	 * Writes a given image to a file (very time-consuming). 
	 *
	 * @param pdfImage the image that shall be written to a file
	 * @param filenamePrefix the prefix of the filename of the file the image is written to dependent on part and round 
	 */
	public void printToImageFile (Image pdfImage, String filenamePrefix) {
/*		if (pdfImage != null) {
			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			try {
				writer.setOutput(new FileImageOutputStream(new File("/home/loeser/images/" + filenamePrefix + ".jpg")));
				writer.write((RenderedImage) pdfImage);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	}	

	public boolean bufferedImageMainIsRendered() {
		return bufferedImageMainBounds != null;	
	}
	public boolean bufferedImageSubIsRendered() {
		return bufferedImageSubBounds != null;	
	}
	
	public synchronized BufferedImage getBufferedImageMain() {
		if (!bufferedImageMainIsRendered())
			renderBufferedImage(bufferedImageMain, bufferedImageMainBounds);
		return bufferedImageMain;
	}	
	
	public synchronized BufferedImage getBufferedImageSub() {
		if (!bufferedImageSubIsRendered())
			renderBufferedImage(bufferedImageSub, bufferedImageSubBounds);
		return bufferedImageSub;
	}

	public void setBufferedImageMain(BufferedImage bufferedImageMain) {
		this.bufferedImageMain = bufferedImageMain;
	}

	public PdfDocument getPdfDocument() {
		return pdfDocument;
	}

	public void setPdfDocument(PdfDocument pdfDocument) {
		this.pdfDocument = pdfDocument;
	}	
	
	public void setBufferedImageSub(BufferedImage bufferedImageSub) {
		this.bufferedImageSub = bufferedImageSub;
		printToImageFile(bufferedImageSub, "sub buffer " + (round - 1));
	}

	public /*Rectangle2D*/Rectangle2D.Double getBufferedImageMainBounds() {
		return bufferedImageMainBounds;
	}

	public void setBufferedImageMainBounds(/*Rectangle2D*/Rectangle2D.Double bufferedImageMainBounds) {
		this.bufferedImageMainBounds = bufferedImageMainBounds;
	}

	public /*Rectangle2D*/Rectangle2D.Double getBufferedImageSubBounds() {
		return bufferedImageSubBounds;
	}

	public void setBufferedImageSubBounds(/*Rectangle2D*/Rectangle2D.Double bufferedImageSubBounds) {
		this.bufferedImageSubBounds = bufferedImageSubBounds;
	}

	public /*int*/double getBufferWidth() {
		return bufferWidth;
	}

	public void setBufferWidth(int bufferWidth) {
		this.bufferWidth = bufferWidth;
	}

	public /*int*/double getBufferHeight() {
		return bufferHeight;
	}

	public void setBufferHeight(int bufferHeight) {
		this.bufferHeight = bufferHeight;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public double getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}

	public boolean getBuffersWereJustInitialized() {
		return buffersWereJustInitialized;
	}

	public void setBuffersWereJustInitialized(boolean buffersWereJustInitialized) {
		this.buffersWereJustInitialized = buffersWereJustInitialized;
	}
	
	
}
