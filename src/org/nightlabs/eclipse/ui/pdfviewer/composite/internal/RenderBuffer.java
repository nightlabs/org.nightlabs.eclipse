package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.log4j.Logger;
import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;
import org.nightlabs.eclipse.ui.pdfviewer.util.Utilities;

import com.sun.pdfview.PDFPage;


public class RenderBuffer
{
	private static final Logger logger = Logger.getLogger(RenderBuffer.class);

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
	private int bufferWidth;
	private int bufferHeight;
	private int round;
	private double zoomFactor;
	private boolean buffersWereJustInitialized = false;


	public RenderBuffer(PdfViewerComposite pdfViewerComposite, PdfDocument pdfDocument) {
		this.pdfViewerComposite = pdfViewerComposite;
		this.pdfDocument = pdfDocument;
//		this.bufferWidth = Utilities.doubleToInt(pdfDocument.getDocumentBounds().x * 2);
//		this.bufferHeight = Toolkit.getDefaultToolkit().getScreenSize().height * 2;
//		this.bufferWidth = pdfDocument.getDocumentBounds().x * 2;
		this.bufferWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 1.5);
		this.bufferHeight = Toolkit.getDefaultToolkit().getScreenSize().height * 2;
		this.zoomFactor = 1;
	}

	/**
	 * Creates a new main and a new sub buffer when opening a PDF document (x and y are zero).
	 * In the case the document was already opened a new sub buffer is created if necessary (independent of the values of x and y)
	 */
	public void createOrSetBufferDimensions() {
		createOrSetBufferDimensions(0, 0, zoomFactor, false);
	}

	/**
	 * Creates or sets main and sub buffer in the case new buffers are needed.
	 *
	 * @param posX the x-coordinate of the upper left corner of the buffers that have to be created.
	 * @param posY the y-coordinate of the upper left corner of the main buffer that has to be created (the y-coordinate of the sub buffer depends on this value)
	 */
	public void createOrSetBufferDimensions(int posX, int posY, double zoomFactor) {
		createOrSetBufferDimensions(posX, posY, zoomFactor, true);
	}

	/**
	 * Creates or sets main and sub buffer in the case new buffers are needed.
	 *
	 * @param posX the x-coordinate of the upper left corner of the buffers that have to be created.
	 * @param posY the y-coordinate of the upper left corner of the main buffer that has to be created (the y-coordinate of the sub buffer depends on this value)
	 * @param completeRecreation true if both main and sub buffer have to be (re-)created, otherwise false
	 */
	private synchronized void createOrSetBufferDimensions(int posX, int posY, double zoomFactor, boolean completeRecreation) {
		this.setZoomFactor(zoomFactor);
		graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

		if (completeRecreation) {
			if (logger.isDebugEnabled())
				logger.debug("createOrSetBufferDimensions: creating or re-creating both main and sub buffer. bufferWidth=" + bufferWidth + " bufferHeight=" + bufferHeight);

			bufferedImageMain = graphicsConfiguration.createCompatibleImage(Utilities.doubleToInt(bufferWidth), Utilities.doubleToInt(bufferHeight));	// perhaps to omit
			bufferedImageMainBounds = new Rectangle2D.Double(posX, posY, bufferWidth / zoomFactor, bufferHeight / zoomFactor);
			bufferedImageMain = renderBufferedImage(bufferedImageMain, bufferedImageMainBounds);

			renderBufferedImageSub();
		}
		else {
			Logger.getRootLogger().info("re-creating only the sub buffer");
//			bufferedImageMain = graphicsConfiguration.createCompatibleImage(Utilities.doubleToInt(bufferWidth), Utilities.doubleToInt(bufferHeight));	// perhaps to omit
			bufferedImageMain = bufferedImageSub;
			bufferedImageMainBounds = bufferedImageSubBounds;
//			bufferedImageMainBounds.setRect(bufferedImageSubBounds);

			renderBufferedImageSub();
		}
//		printToImageFile(bufferedImageMain, "main buffer " + round);
	}

	/**
	 * Initializes main and sub buffer when opening the document
	 */
	public void initBuffering() {

		graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		bufferedImageMain = graphicsConfiguration.createCompatibleImage(Utilities.doubleToInt(bufferWidth), Utilities.doubleToInt(bufferHeight));
		bufferedImageMainBounds = new Rectangle2D.Double(0, 0, bufferWidth / zoomFactor, bufferHeight / zoomFactor);    // first buffer begins at position (0,0) of the document
		bufferedImageSub = graphicsConfiguration.createCompatibleImage(Utilities.doubleToInt(bufferWidth), Utilities.doubleToInt(bufferHeight));
		bufferedImageSubBounds = new Rectangle2D.Double(0, bufferDistance * bufferHeight / zoomFactor, bufferWidth / zoomFactor, bufferHeight / zoomFactor);

		buffersWereJustInitialized = true;
	}

	/**
	 * Renders the given sub buffer with its buffer bounds
	 */
	private void renderBufferedImageSub() {

		int posX = 0;
		bufferedImageSub = graphicsConfiguration.createCompatibleImage(Utilities.doubleToInt(bufferWidth), Utilities.doubleToInt(bufferHeight));
		bufferedImageSubBounds = new Rectangle2D.Double(
				posX, bufferedImageMainBounds.getY() + bufferDistance * bufferHeight / zoomFactor,
				bufferWidth / zoomFactor, bufferHeight / zoomFactor);

		if (bufferedImageMainBounds.getY() + bufferDistance * bufferHeight / zoomFactor <= pdfDocument.getDocumentBounds().getY()) {
			bufferedImageSub = renderBufferedImage(bufferedImageSub, bufferedImageSubBounds);
			buffersWereJustInitialized = false;
//			printToImageFile(bufferedImageSub, "sub buffer " + round);
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
//		Rectangle2D.Double pageBoundsZoomed = new Rectangle2D.Double(0, 0, 0, 0);
		Rectangle2D.Double pageBounds;
//		boolean endOfPageIsInBuffer;
//		boolean beginningOfPageIsInBuffer;
//		int bufferCoordinateY = 0;
//		double direction = 0;


		// clear buffer => fill grey
		Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
//		graphics.setBackground(pdfViewerComposite.getViewPanel().getBackground());
//		graphics.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		graphics.setColor(pdfViewerComposite.getViewPanel().getBackground());
		graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

/*		bufferedImageBufferBounds.setRect(	bufferedImageBufferBounds.getX(),
											bufferedImageBufferBounds.getY() * zoomFactor,
											bufferedImageBufferBounds.getWidth(),
											bufferedImageBufferBounds.getHeight()
											);		*/

		// get page numbers of those pages that are lying in buffer bounds of currently considered buffer
		List<Integer> bufferedImagePageNumbers = pdfDocument.getVisiblePages(bufferedImageBufferBounds);
//		Collections.sort(bufferedImagePageNumbers);

		for (Integer pageNumber : bufferedImagePageNumbers) {
			pageBounds = pdfDocument.getPageBounds(pageNumber);
//			pageBoundsZoomed.setRect(	pageBounds.getX() + direction * Math.abs((zoomFactor - 1) * pageBounds.getWidth()) / 2,
//										pageBounds.getY() * zoomFactor,
//										pageBounds.getWidth() * zoomFactor,
//										pageBounds.getHeight() * zoomFactor
//										);
			PDFPage pdfPage = pdfDocument.getPdfFile().getPage(pageNumber);

			int pdfImageWidth = (int) (pageBounds.getWidth() * zoomFactor);
			int pdfImageHeight = (int) (pageBounds.getHeight() * zoomFactor);

			// PDF coordinate system begins from bottom left point upwards, not from top left point downwards
			Rectangle2D.Double clipLeftBottom = new Rectangle2D.Double(0, 0, 0, 0);
			clipLeftBottom.width = pdfPage.getWidth();
			clipLeftBottom.height = pdfPage.getHeight();

			if (pageBounds.getMinX() < bufferedImageBufferBounds.getMinX()) {
				double d = bufferedImageBufferBounds.getMinX() - pageBounds.getMinX();
				pdfImageWidth -= d * zoomFactor;
				clipLeftBottom.x = d;
				clipLeftBottom.width -= d;
			}

			if (pageBounds.getMinY() < bufferedImageBufferBounds.getMinY()) {
				double d = bufferedImageBufferBounds.getMinY() - pageBounds.getMinY();
				pdfImageHeight -= d * zoomFactor;
				clipLeftBottom.height -= d;
			}

			if (pageBounds.getMaxX() > bufferedImageBufferBounds.getMaxX()) {
				double d = pageBounds.getMaxX() - bufferedImageBufferBounds.getMaxX();
				pdfImageWidth -= d * zoomFactor;
				clipLeftBottom.width -= d;
			}

			if (pageBounds.getMaxY() > bufferedImageBufferBounds.getMaxY()) {
				double d = pageBounds.getMaxY() - bufferedImageBufferBounds.getMaxY();
				pdfImageHeight -= d * zoomFactor;
				clipLeftBottom.height -= d;
				clipLeftBottom.y = pdfPage.getHeight() - d;
			}

			Image pdfImage = getPdfImage(
					pdfPage,
					pdfImageWidth,
					pdfImageHeight,
					clipLeftBottom
			);

//			printToImageFile(pdfImage, "pdfImage-" + round);

			// In contrast to clipLeftBottom the clipAbsoluteLeftTop specifies the left-top-point of the clip relative
			// to the PdfDocument's complete coordinate system.
			Rectangle2D.Double clipAbsoluteLeftTop = new Rectangle2D.Double();
			clipAbsoluteLeftTop.x = pageBounds.getX() + clipLeftBottom.x;
			clipAbsoluteLeftTop.y = pageBounds.getY() + pageBounds.getHeight() - (clipLeftBottom.y + clipLeftBottom.height);
			clipAbsoluteLeftTop.width = clipLeftBottom.width;
			clipAbsoluteLeftTop.height = clipLeftBottom.height;

			drawImage(
					graphics,
					pdfImage,
					(int) ((clipAbsoluteLeftTop.getX() - bufferedImageBufferBounds.getX()) * zoomFactor),
					(int) ((clipAbsoluteLeftTop.getY() - bufferedImageBufferBounds.getY()) * zoomFactor)
			);

		} // for (Integer pageNumber : bufferedImagePageNumbers) {

//		printToImageFile(bufferedImage, "bufferedImage-" + round);

		round++;
		return bufferedImage;
	}

	private static void drawImage(Graphics2D graphics2D, Image image, int x, int y)
	{
		final boolean[] bufferFinished = new boolean[1];
		bufferFinished[0] = graphics2D.drawImage(
				image,
				x,
				y,
				new ImageObserver() {
					@Override
					public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
						if (infoflags == ImageObserver.ALLBITS)
							bufferFinished[0] = true;
						return true;
					}
				}
		);
		while (!bufferFinished[0]) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	private static Image getPdfImage(PDFPage pdfPage, int pdfImageWidth, int pdfImageHeight, Rectangle2D clipLeftBottom)
	{
		final boolean[] bufferFinished = new boolean[1];
		bufferFinished[0] = false;
		Image pdfImage = pdfPage.getImage(
				pdfImageWidth,
				pdfImageHeight,
				clipLeftBottom,
				new ImageObserver() {
					@Override
					public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
						if (infoflags == ImageObserver.ALLBITS)
							bufferFinished[0] = true;
						return true;
					}
				}
		);
		while (!bufferFinished[0]) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		return pdfImage;
	}

	/**
	 * Draws a certain part (the region of interest) of the main buffer onto the screen.
	 *
	 * @param graphics2D the graphics context of the panel
	 * @param region the region of interest of the document that shall be drawn onto the screen in real coordinates
	 * @return <code>true</code>, if the region could be completely copied from the buffer. <code>false</code>, if the requested region
	 *		exceeds the buffer and therefore could only copy partially or not at all. Empty pages will be drawn instead.
	 */
	public synchronized boolean drawRegion(
			Graphics2D graphics2D,
			int graphics2DWidth, int graphics2DHeight,
			double requestedZoomFactor,
			Rectangle2D region
	)
	{
		if (graphics2D == null)
			throw new IllegalArgumentException("graphics2D must not be null!");

		if (region == null)
			throw new IllegalArgumentException("region must not be null!");

		boolean zoomMismatch = (int)(requestedZoomFactor * 1000) != (int) (zoomFactor * 1000);
		boolean bufferSufficient = !zoomMismatch;

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

		// copy buffer to view
//		printToImageFile(getBufferedImageMain(), "bufferedImage");

		int destinationX1 = 0;
		int destinationY1 = 0;
		int destinationX2 = graphics2DWidth;
		int destinationY2 = graphics2DHeight;

		int sourceX1 = 0;
		int sourceY1 = 0;
		int sourceX2 = 0;
		int sourceY2 = 0;

		if (bufferedImageMainBounds == null)
			bufferSufficient = false;
		else {

			// destination rectangle (screen coordinates relative to the panel! i.e. usually the complete panel)
			// given as TWO POINTS d1 and d2 (not width and height)
			sourceX1 = (int) ((region.getMinX() - bufferedImageMainBounds.getMinX()) * zoomFactor);
			sourceY1 = (int) ((region.getMinY() - bufferedImageMainBounds.getMinY()) * zoomFactor);

			if (bufferedImageMainBounds.getMinX() > region.getMinX()) {
				sourceX1 = 0;
				destinationX1 = (int) ((bufferedImageMainBounds.getMinX() - region.getMinX()) * zoomFactor);
				bufferSufficient = false;
			}

			if (bufferedImageMainBounds.getMinY() > region.getMinY()) {
				sourceY1 = 0;
				destinationY1 = (int) ((bufferedImageMainBounds.getMinY() - region.getMinY()) * zoomFactor);
				bufferSufficient = false;
			}

			if (bufferedImageMainBounds.getMaxX() < region.getMaxX()) {
				destinationX2 = graphics2DWidth - (int) ((region.getMaxX() - bufferedImageMainBounds.getMaxX()) * zoomFactor);
				bufferSufficient = false;
			}

			if (bufferedImageMainBounds.getMaxY() < region.getMaxY()) {
				destinationY2 = graphics2DWidth - (int) ((region.getMaxY() - bufferedImageMainBounds.getMaxY()) * zoomFactor);
				bufferSufficient = false;
			}

			sourceX2 = sourceX1 + destinationX2 - destinationX1;
			sourceY2 = sourceY1 + destinationY2 - destinationY1;
		}

		if (!bufferSufficient) {
			// draw background


			// draw empty pages
		}

		if (
				bufferedImageMain != null &&
				!zoomMismatch &&
				destinationX2 > destinationX1 &&
				destinationY2 > destinationY1 &&
				sourceX2 > sourceX1 &&
				sourceY2 > sourceY1
		)
		{
			graphics2D.drawImage(
					bufferedImageMain,
					destinationX1,
					destinationY1,
					destinationX2,
					destinationY2,
					sourceX1,
					sourceY1,
					sourceX2,
					sourceY2,
					null
			);
		}

//		int destinationX1 = Math.max(0, Utilities.doubleToIntRoundedDown(region.getWidth() / 2 - bufferWidth / 2));
//		int destinationY1 = 0;
//		int destinationX2 = (int) (Math.max(0 + bufferWidth, region.getWidth() / 2 - bufferWidth / 2 + bufferWidth));
//		int destinationY2 = (int) region.getHeight();
//
//		graphics2D.drawImage(
//				getBufferedImageMain(),
//				destinationX1,
//				destinationY1,
//				destinationX2,
//				destinationY2,
//
//				// source rectangle (certain part of current buffer) given as TWO POINTS s1 and s2 (not width and height)
//				(int) (region.getX() - bufferedImageMainBounds.getX()),
//				(int) (region.getY() - bufferedImageMainBounds.getY()),
//				(int) (region.getX() - bufferedImageMainBounds.getX() + bufferWidth),
//				(int) (region.getY() - bufferedImageMainBounds.getY() + region.getHeight()),
//				null
//		);

		return bufferSufficient;
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
		if (pdfImage != null) {
			ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
			try {
				writer.setOutput(new FileImageOutputStream(new File("/home/frederik/images/" + filenamePrefix + ".png")));
				writer.write((RenderedImage) pdfImage);
				writer.dispose();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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

	public int getBufferWidth() {
		return bufferWidth;
	}

	public void setBufferWidth(int bufferWidth) {
		this.bufferWidth = bufferWidth;
	}

	public int getBufferHeight() {
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

	private void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}

	public boolean getBuffersWereJustInitialized() {
		return buffersWereJustInitialized;
	}

	public void setBuffersWereJustInitialized(boolean buffersWereJustInitialized) {
		this.buffersWereJustInitialized = buffersWereJustInitialized;
	}


}
