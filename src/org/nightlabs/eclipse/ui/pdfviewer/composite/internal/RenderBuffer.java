package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.log4j.Logger;
import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;
import org.nightlabs.util.IOUtil;

import com.sun.pdfview.PDFPage;

/**
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class RenderBuffer
{
	private static final Logger logger = Logger.getLogger(RenderBuffer.class);

	private PdfViewerComposite pdfViewerComposite;
	private PdfDocument pdfDocument;

	// BEGIN the following fields require SYNCHRONIZED access via the mutex field!
	private Object mutex = new Object();
	private BufferedImage bufferedImage = null;
	private Rectangle2D.Double bufferedImageBounds = null;
	private double zoomFactor;
	private int bufferWidth;
	private int bufferHeight;
	// END the above fields require SYNCHRONIZED access via the mutex field!

	public static final double BUFFER_WIDTH_FACTOR = 2;
	public static final double BUFFER_HEIGHT_FACTOR = 2;

	public RenderBuffer(PdfViewerComposite pdfViewerComposite, PdfDocument pdfDocument) {
		this.pdfViewerComposite = pdfViewerComposite;
		this.pdfDocument = pdfDocument;
	}

	/**
	 * Renders the buffer's area.
	 *
	 * @param bufferWidth
	 *            the currently used buffer width, i.e. width of viewPanel multiplied with a constant buffer width factor (because the
	 *            buffer is of course bigger than the panel).
	 * @param bufferHeight
	 *            the currently used buffer height, i.e. height of viewPanel multiplied with a constant buffer height factor (because the
	 *            buffer is of course bigger than the panel).
	 * @param posX
	 *            the x-coordinate in the real coordinate system of the upper left corner of the buffer that has to be created.
	 * @param posY
	 *            the y-coordinate in the real coordinate system of the upper left corner of the buffer that has to be created
	 * @param zoomFactor
	 *            the currently used zoom factor
	 */
	public void render(int bufferWidth, int bufferHeight, int posX, int posY, double zoomFactor)
	{
		if (bufferWidth < 1)
			throw new IllegalArgumentException("bufferWidth < 1");

		if (bufferHeight < 1)
			throw new IllegalArgumentException("bufferHeight < 1");

		String dumpImageRenderID = DUMP_IMAGE_BUFFER || DUMP_IMAGE_PAGE ? Long.toString(System.currentTimeMillis(), 36) : null;

		GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage bufferedImage = graphicsConfiguration.createCompatibleImage(bufferWidth, bufferHeight);

		// bufferedImageBounds is the position and the size of the buffer in the real coordinate system.
		Rectangle2D.Double bufferedImageBounds = new Rectangle2D.Double(
				posX,
				posY,
				bufferWidth / zoomFactor,
				bufferHeight / zoomFactor
		);

		// clear buffer => fill grey
		Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
		graphics.setColor(pdfViewerComposite.getViewPanel().getBackground());
		graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

		// get page numbers of those pages that are lying in buffer bounds of currently considered buffer
		List<Integer> bufferedImagePageNumbers = pdfDocument.getVisiblePages(bufferedImageBounds);

		for (Integer pageNumber : bufferedImagePageNumbers) {
			Rectangle2D.Double pageBounds = pdfDocument.getPageBounds(pageNumber);
			PDFPage pdfPage = pdfDocument.getPdfFile().getPage(pageNumber);

			int pdfImageWidth = (int) (pageBounds.getWidth() * zoomFactor);
			int pdfImageHeight = (int) (pageBounds.getHeight() * zoomFactor);

			// PDF coordinate system begins from bottom-left point upwards, not from top-left point downwards
			Rectangle2D.Double clipLeftBottom = new Rectangle2D.Double(0, 0, 0, 0);
			clipLeftBottom.width = pdfPage.getWidth();
			clipLeftBottom.height = pdfPage.getHeight();

			if (pageBounds.getMinX() < bufferedImageBounds.getMinX()) {
				double d = bufferedImageBounds.getMinX() - pageBounds.getMinX();
				pdfImageWidth -= d * zoomFactor;
				clipLeftBottom.x = d;
				clipLeftBottom.width -= d;
			}

			if (pageBounds.getMinY() < bufferedImageBounds.getMinY()) {
				double d = bufferedImageBounds.getMinY() - pageBounds.getMinY();
				pdfImageHeight -= d * zoomFactor;
				clipLeftBottom.height -= d;
			}

			if (pageBounds.getMaxX() > bufferedImageBounds.getMaxX()) {
				double d = pageBounds.getMaxX() - bufferedImageBounds.getMaxX();
				pdfImageWidth -= d * zoomFactor;
				clipLeftBottom.width -= d;
			}

			if (pageBounds.getMaxY() > bufferedImageBounds.getMaxY()) {
				double d = pageBounds.getMaxY() - bufferedImageBounds.getMaxY();
				pdfImageHeight -= d * zoomFactor;
				clipLeftBottom.height -= d;
				clipLeftBottom.y = d;
			}

			if (pdfImageWidth < 1 || pdfImageHeight < 1) // skip a 0-height/width image
				continue;

			Image pdfImage = getPdfImage(
					pdfPage,
					pdfImageWidth,
					pdfImageHeight,
					clipLeftBottom
			);

			if (DUMP_IMAGE_PAGE)
				printToImageFile(pdfImage, String.format("%s-pdfImage-%03d", dumpImageRenderID, pageNumber));

			// In contrast to clipLeftBottom clipAbsoluteLeftTop specifies the top-left point of the clip relative
			// to the PdfDocument's complete coordinate system.
			// PDF coordinate system begins from bottom-left point upwards, not from top-left point downwards
			Rectangle2D.Double clipAbsoluteLeftTop = new Rectangle2D.Double();
			clipAbsoluteLeftTop.x = pageBounds.getX() + clipLeftBottom.x;
			clipAbsoluteLeftTop.y = pageBounds.getY() + pageBounds.getHeight() - (clipLeftBottom.y + clipLeftBottom.height);
			clipAbsoluteLeftTop.width = clipLeftBottom.width;
			clipAbsoluteLeftTop.height = clipLeftBottom.height;

			drawImage(
					graphics,
					pdfImage,
					(int) ((clipAbsoluteLeftTop.getX() - bufferedImageBounds.getX()) * zoomFactor),
					(int) ((clipAbsoluteLeftTop.getY() - bufferedImageBounds.getY()) * zoomFactor)
			);

			graphics.setColor(Color.BLACK);
			graphics.drawRect(
					(int) ((pageBounds.getX() - bufferedImageBounds.getX()) * zoomFactor),
					(int) ((pageBounds.getY() - bufferedImageBounds.getY()) * zoomFactor),
					(int) (pageBounds.getWidth() * zoomFactor),
					(int) (pageBounds.getHeight() * zoomFactor)
			);
		} // for (Integer pageNumber : bufferedImagePageNumbers) {

		if (DUMP_IMAGE_BUFFER)
			printToImageFile(bufferedImage, String.format("%s-bufferedImage", dumpImageRenderID));

		synchronized (mutex) {
			this.bufferWidth = bufferWidth;
			this.bufferHeight = bufferHeight;
			this.bufferedImage = bufferedImage;
			this.bufferedImageBounds = bufferedImageBounds;
			this.zoomFactor = zoomFactor;
		}
	}

	private static void drawImage(Graphics2D graphics2D, Image image, int x, int y)
	{
		// TODO WORKAROUND for:
		// http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg21170.html
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=74095
		// When this bug is fixed, remove this method and rename _drawImage(...) to drawImage(...)!
		int tryCount = 0;
		while (true) {
			++tryCount;

			try {
				_drawImage(graphics2D, image, x, y);
				return;
			} catch (StackOverflowError error) {
				logger.warn("drawImage: WORKAROUND: Caught StackOverflowError with tryCount=" + tryCount, error);

				if (tryCount > 2)
					throw error;
			}
		}
	}

	/**
	 * Draws the given image into the currently considered buffer.
	 *
	 * @param graphics2D the graphics context of the current buffer
	 * @param image the image to draw into the current buffer
	 * @param x the x coordinate of that point in the current buffer where the top-left corner of the given
	 * image that shall be drawn will be lying
	 * @param y	the y coordinate of that point in the current buffer where the top-left corner of the given
	 * image that shall be drawn will be lying
	 */
	private static void _drawImage(Graphics2D graphics2D, Image image, int x, int y)
	{
		BlockingImageObserver bio = new BlockingImageObserver();

		boolean renderingComplete = graphics2D.drawImage(image, x, y, bio);

		if (!renderingComplete)
			bio.waitForRendering();
	}

	private static class BlockingImageObserver implements ImageObserver {
		private boolean renderingFinished = false;
		private boolean renderingAborted = false;
		private int lastInfoflags = 0;

		public BlockingImageObserver() { }

		private static final long timeoutMSec = 60000;

		@Override
		public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
			lastInfoflags = infoflags;

			if ((infoflags & ImageObserver.ALLBITS) != 0)
				renderingFinished = true;

			if ((infoflags & ImageObserver.ABORT) != 0)
				renderingAborted = true;

			this.notifyAll();
			return !renderingFinished;
		}

		public synchronized void waitForRendering()
		{
			long start = System.currentTimeMillis();
			while (!renderingFinished && !renderingAborted) {
				if (System.currentTimeMillis() - start > timeoutMSec)
					throw new IllegalStateException("Timeout waiting for rendering to finish or abort!");

				try {
					this.wait(10000);
				} catch (InterruptedException e) {
					// ignore
				}
			}

			if (renderingAborted)
				throw new IllegalStateException("Rendering was aborted! lastInfoflags=" + lastInfoflags);
		}
	}

	/**
	 * Gets the image of the currently considered PDF page taking a given clip into consideration.
	 *
	 * @param pdfPage the currently considered page of the PDF document
	 * @param pdfImageWidth the image width in the image coordinate system
	 * @param pdfImageHeight the image height in the image coordinate system
	 * @param clipLeftBottom a rectangle describing the region of interest of the currently considered PDF page
	 */
	private static Image getPdfImage(PDFPage pdfPage, int pdfImageWidth, int pdfImageHeight, Rectangle2D clipLeftBottom)
	{
		BlockingImageObserver bio = new BlockingImageObserver();

		Image pdfImage = pdfPage.getImage(
				pdfImageWidth,
				pdfImageHeight,
				clipLeftBottom,
				bio
		);

		bio.waitForRendering();

		return pdfImage;
	}

	/**
	 * Draws a certain part (the region of interest) of the buffer onto the screen - or more precisely the viewPanel
	 * (passed as <code>graphics2D</code>).
	 *
	 * @param graphics2D the graphics context of the panel
	 * @param graphics2DWidth the panel width
	 * @param graphics2DHeight the panel height
	 * @param requestedZoomFactor the zoom factor to use
	 * @param region the region of interest of the document that shall be drawn onto the screen in real coordinates
	 * @return <code>true</code>, if the region could be completely copied from the buffer. <code>false</code>, if the requested region
	 *		exceeds the buffer and therefore could only copy partially or not at all. Empty pages will be drawn instead.
	 */
	public boolean drawRegion(
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

		synchronized (mutex) {
			boolean zoomMismatch = (int)(requestedZoomFactor * 1000) != (int) (zoomFactor * 1000);
			boolean bufferSufficient = !zoomMismatch;

			int destinationX1 = 0;
			int destinationY1 = 0;
			int destinationX2 = graphics2DWidth;
			int destinationY2 = graphics2DHeight;

			int sourceX1 = 0;
			int sourceY1 = 0;
			int sourceX2 = 0;
			int sourceY2 = 0;

			if (bufferedImageBounds == null)
				bufferSufficient = false;
			else {

				sourceX1 = (int) ((region.getMinX() - bufferedImageBounds.getMinX()) * zoomFactor);
				sourceY1 = (int) ((region.getMinY() - bufferedImageBounds.getMinY()) * zoomFactor);

				if (bufferedImageBounds.getMinX() > region.getMinX()) {
					sourceX1 = 0;
					destinationX1 = (int) ((bufferedImageBounds.getMinX() - region.getMinX()) * zoomFactor);
					bufferSufficient = false;
				}

				if (bufferedImageBounds.getMinY() > region.getMinY()) {
					sourceY1 = 0;
					destinationY1 = (int) ((bufferedImageBounds.getMinY() - region.getMinY()) * zoomFactor);
					bufferSufficient = false;
				}

				if (bufferedImageBounds.getMaxX() < region.getMaxX()) {
					destinationX2 = graphics2DWidth - (int) ((region.getMaxX() - bufferedImageBounds.getMaxX()) * zoomFactor);
					bufferSufficient = false;
				}

				if (bufferedImageBounds.getMaxY() < region.getMaxY()) {
					destinationY2 = graphics2DWidth - (int) ((region.getMaxY() - bufferedImageBounds.getMaxY()) * zoomFactor);
					bufferSufficient = false;
				}

				sourceX2 = sourceX1 + destinationX2 - destinationX1;
				sourceY2 = sourceY1 + destinationY2 - destinationY1;
			}

			if (!bufferSufficient) {
				// draw background
				graphics2D.setColor(pdfViewerComposite.getViewPanel().getBackground());
				graphics2D.fillRect(0, 0, graphics2DWidth, graphics2DHeight);

				// draw empty pages
				List<Integer> visiblePages = pdfDocument.getVisiblePages(region);
				for (Integer pageNumber : visiblePages) {
					Rectangle2D page = pdfDocument.getPageBounds(pageNumber);

					int x = (int) ((page.getX() - region.getX()) * requestedZoomFactor);
					int y = (int) ((page.getY() - region.getY()) * requestedZoomFactor);
					int w = (int) (page.getWidth() * requestedZoomFactor);
					int h = (int) (page.getHeight() * requestedZoomFactor);

					graphics2D.setColor(COLOR_DRAFT_PAGE_BORDER);
					graphics2D.drawRect(x, y, w, h);

					graphics2D.setColor(COLOR_DRAFT_PAGE_AREA);
					graphics2D.fillRect(x + 1, y + 1, w - 1, h - 1);

					// TODO draw page numbers in the middle of each page
					graphics2D.setColor(COLOR_DRAFT_PAGE_BORDER);
					graphics2D.drawString(Integer.toString(pageNumber), (int)(x + (double)w / 2), (int)(y + (double)h / 2));

				}
			}

			if (
					bufferedImage != null &&
					!zoomMismatch &&
					destinationX2 > destinationX1 &&
					destinationY2 > destinationY1 &&
					sourceX2 > sourceX1 &&
					sourceY2 > sourceY1
			)
			{
				BlockingImageObserver bio = new BlockingImageObserver();
				boolean renderingComplete = graphics2D.drawImage(
						bufferedImage,
						destinationX1,
						destinationY1,
						destinationX2,
						destinationY2,
						sourceX1,
						sourceY1,
						sourceX2,
						sourceY2,
						bio
				);
				if (!renderingComplete)
					bio.waitForRendering();
			}

			return bufferSufficient;
		} // synchronized (bufferedImageMutex) {
	}

	private static final Color COLOR_DRAFT_PAGE_BORDER = Color.RED;
	private static final Color COLOR_DRAFT_PAGE_AREA = new Color(240, 240, 240);

	private static final boolean DUMP_IMAGE_PAGE = false;
	private static final boolean DUMP_IMAGE_BUFFER = false;

	/**
	 * Writes a given image to a file (very time-consuming - ONLY FOR DEBUGGING!).
	 *
	 * @param image the image that shall be written to a file
	 * @param filenamePrefix the prefix of the filename of the file the image is written to dependent on part and round
	 */
	private static void printToImageFile(Image image, String filenamePrefix) {
		if (image == null)
			throw new IllegalArgumentException("image must not be null!");

		File dumpDir = IOUtil.getUserTempDir("pdfviewer_images", null);
		if (!dumpDir.exists())
			dumpDir.mkdirs();

		String fileFormat = "png";
		ImageWriter writer = ImageIO.getImageWritersByFormatName(fileFormat).next();
		try {
			FileImageOutputStream out = new FileImageOutputStream(new File(dumpDir, filenamePrefix + '.' + fileFormat));
			try {
				writer.setOutput(out);
				writer.write((RenderedImage) image);
			} finally {
				out.close();
			}
		} catch (Exception e) {
			logger.warn("printToImageFile: writing image failed!", e);
		} finally {
			writer.dispose();
		}
	}

	public PdfDocument getPdfDocument() {
		return pdfDocument;
	}

	public void setPdfDocument(PdfDocument pdfDocument) {
		this.pdfDocument = pdfDocument;
	}

	public Rectangle2D.Double getBufferedImageBounds() {
		return bufferedImageBounds;
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

	public double getZoomFactor() {
		return zoomFactor;
	}

}
