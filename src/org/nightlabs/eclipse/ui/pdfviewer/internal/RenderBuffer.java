package org.nightlabs.eclipse.ui.pdfviewer.internal;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.TextLayout;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.log4j.Logger;
import org.nightlabs.eclipse.ui.pdfviewer.Dimension2DDouble;
import org.nightlabs.eclipse.ui.pdfviewer.PdfDocument;

import com.sun.pdfview.PDFPage;

/**
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class RenderBuffer
{
	private static final Logger logger = Logger.getLogger(RenderBuffer.class);

	private PdfViewerComposite pdfViewerComposite;
	private PdfDocument pdfDocument;
//	private boolean shadowsHaveToBeDrawn;
//	private Rectangle2D shadowBounds1, shadowBounds2, shadowBounds3, shadowBounds4;
//	private int zoomFactorPerMill;

	// BEGIN the following fields require SYNCHRONIZED access via the mutex field!
	private Object mutex = new Object();
	private BufferedImage bufferedImage = null;
	private Rectangle2D.Double bufferedImageBounds = null;
	private double zoomFactor;
	private int bufferWidth;
	private int bufferHeight;
	// END the above fields require SYNCHRONIZED access via the mutex field!

//	public static final double BUFFER_WIDTH_FACTOR = 2;
//	public static final double BUFFER_HEIGHT_FACTOR = 2;

	public static final double BUFFER_SIZE_FACTOR_SMALL = 2;
	public static final double BUFFER_SIZE_FACTOR_LARGE = 3;

	public RenderBuffer(PdfViewerComposite pdfViewerComposite, PdfDocument pdfDocument) {
		this.pdfViewerComposite = pdfViewerComposite;
		this.pdfDocument = pdfDocument;
//		pdfViewerComposite.addPropertyChangeListener(PdfViewer.PROPERTY_MOUSE_CLICKED, propertyChangeListenerMouseClicked);
	}

	private BufferedImagePool bufferedImagePool = new BufferedImagePool();

	/**
	 * Renders the buffer's area.
	 *
	 * @param bufferWidth the currently used buffer width, i.e. width of viewPanel multiplied with a constant buffer width factor
	 * (because the buffer is of course bigger than the panel).
	 * @param bufferHeight the currently used buffer height, i.e. height of viewPanel multiplied with a constant buffer height factor
	 * (because the buffer is of course bigger than the panel).
	 * @param posX the x-coordinate in the real coordinate system of the upper left corner of the buffer that has to be created.
	 * @param posY the y-coordinate in the real coordinate system of the upper left corner of the buffer that has to be created
	 * @param zoomFactor the currently used zoom factor
	 */
	public void paintToBuffer(int bufferWidth, int bufferHeight, int posX, int posY, double zoomFactor)
	{
		if (bufferWidth < 1)
			throw new IllegalArgumentException("bufferWidth < 1"); //$NON-NLS-1$

		if (bufferHeight < 1)
			throw new IllegalArgumentException("bufferHeight < 1"); //$NON-NLS-1$

		String dumpImageRenderID = DUMP_IMAGE_BUFFER || DUMP_IMAGE_PAGE ? Long.toString(System.currentTimeMillis(), 36) : null;

//		GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
//		BufferedImage bufferedImage = graphicsConfiguration.createCompatibleImage(bufferWidth, bufferHeight);
//		BufferedImage bufferedImage = new BufferedImage(bufferWidth, bufferHeight, BufferedImage.TYPE_INT_ARGB);
		BufferedImage bufferedImage = bufferedImagePool.acquire(bufferWidth, bufferHeight);

		// bufferedImageBounds is the position and the size of the buffer in the real coordinate system.
		Rectangle2D.Double bufferedImageBounds = new Rectangle2D.Double(
				posX,
				posY,
				bufferWidth / (zoomFactor * getZoomScreenResolutionFactorX()),
				bufferHeight / (zoomFactor * getZoomScreenResolutionFactorY())
		);

		Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
// We should enable anti-aliasing, if we switch to direct rendering (if we really ever do this):
//		https://pdf-renderer.dev.java.net/examples.html
//		(see "How do I draw a PDF directly to my own Graphics2D object?")
//
// With the current intermediate image, however, it doesn't make a difference (at least I saw none)
// and it's not mentioned in the example.
// Therefore, we leave the following line commented out for now. Marco.
//		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// clear buffer => fill grey
		graphics.setColor(pdfViewerComposite.getViewPanel().getBackground());
		graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

		// get page numbers of those pages that are lying in buffer bounds of currently considered buffer
		Collection<Integer> bufferedImagePageNumbers = pdfDocument.getVisiblePages(bufferedImageBounds);

		pdfViewerComposite.firePaintToBufferListeners(graphics, false);

		for (Integer pageNumber : bufferedImagePageNumbers) {
			Rectangle2D pageBounds = pdfDocument.getPageBounds(pageNumber);
			PDFPage pdfPage = pdfDocument.getPdfFile().getPage(pageNumber);

			int pdfImageWidth = (int) (pageBounds.getWidth() * zoomFactor * getZoomScreenResolutionFactorX());
			int pdfImageHeight = (int) (pageBounds.getHeight() * zoomFactor * getZoomScreenResolutionFactorY());

			// PDF coordinate system begins from bottom-left point upwards, not from top-left point downwards
			Rectangle2D.Double clipLeftBottom = new Rectangle2D.Double(0, 0, 0, 0);
			clipLeftBottom.width = pdfPage.getWidth();
			clipLeftBottom.height = pdfPage.getHeight();

			if (pageBounds.getMinX() < bufferedImageBounds.getMinX()) {
				double d = bufferedImageBounds.getMinX() - pageBounds.getMinX();
				pdfImageWidth -= d * zoomFactor * getZoomScreenResolutionFactorX();
				clipLeftBottom.x = d;
				clipLeftBottom.width -= d;
			}

			if (pageBounds.getMinY() < bufferedImageBounds.getMinY()) {
				double d = bufferedImageBounds.getMinY() - pageBounds.getMinY();
				pdfImageHeight -= d * zoomFactor * getZoomScreenResolutionFactorY();
				clipLeftBottom.height -= d;
			}

			if (pageBounds.getMaxX() > bufferedImageBounds.getMaxX()) {
				double d = pageBounds.getMaxX() - bufferedImageBounds.getMaxX();
				pdfImageWidth -= d * zoomFactor * getZoomScreenResolutionFactorX();
				clipLeftBottom.width -= d;
			}

			if (pageBounds.getMaxY() > bufferedImageBounds.getMaxY()) {
				double d = pageBounds.getMaxY() - bufferedImageBounds.getMaxY();
				pdfImageHeight -= d * zoomFactor * getZoomScreenResolutionFactorY();
				clipLeftBottom.height -= d;
				clipLeftBottom.y = d;
			}

			if (pdfImageWidth < 1 || pdfImageHeight < 1) // skip a 0-height/width image
				continue;


			{ // render the PDF into an image and draw the image. this works fine except for occasional bugs like ImageObserver timeouts
				Image pdfImage = getPdfImage(
						pdfPage,
						pdfImageWidth,
						pdfImageHeight,
						clipLeftBottom
				);

				if (DUMP_IMAGE_PAGE)
					printToImageFile(pdfImage, String.format("%s-pdfImage-%03d", dumpImageRenderID, pageNumber)); //$NON-NLS-1$

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
						(int) ((clipAbsoluteLeftTop.getX() - bufferedImageBounds.getX()) * zoomFactor * getZoomScreenResolutionFactorX()),
						(int) ((clipAbsoluteLeftTop.getY() - bufferedImageBounds.getY()) * zoomFactor * getZoomScreenResolutionFactorY())
				);
			}

//			{ // render the PDF directly into the buffer - doesn't work, because I've no clue about the arguments necessary - and I'm too lazy now to find out what clip etc. I need (and how I can calculate it). Marco.
//				PDFRenderer pdfRenderer = new PDFRenderer(
//						pdfPage,
//						graphics,
//						new Rectangle(pdfImageWidth, pdfImageHeight), // WHAT NEEDS TO BE HERE?
//						clipLeftBottom, // WHAT NEEDS TO BE HERE?
//						pdfViewerComposite.getViewPanel().getBackground()
//				);
//				try {
//					pdfPage.waitForFinish();
//				} catch (InterruptedException e) {
//					throw new RuntimeException(e); // TODO what the hell should we do? does this ever happen in normal operation?
//				}
//				pdfRenderer.run();
//			}

			graphics.setColor(Color.BLACK);
			graphics.drawRect(
					(int) ((pageBounds.getX() - bufferedImageBounds.getX()) * zoomFactor * getZoomScreenResolutionFactorX()),
					(int) ((pageBounds.getY() - bufferedImageBounds.getY()) * zoomFactor * getZoomScreenResolutionFactorY()),
					(int) (pageBounds.getWidth() * zoomFactor * getZoomScreenResolutionFactorX()),
					(int) (pageBounds.getHeight() * zoomFactor * getZoomScreenResolutionFactorY())
			);
		} // for (Integer pageNumber : bufferedImagePageNumbers) {

		pdfViewerComposite.firePaintToBufferListeners(graphics, true);

		if (DUMP_IMAGE_BUFFER)
			printToImageFile(bufferedImage, String.format("%s-bufferedImage", dumpImageRenderID)); //$NON-NLS-1$

		synchronized (mutex) {
			if (this.bufferedImage != null)
				bufferedImagePool.release(this.bufferedImage);

			this.bufferWidth = bufferWidth;
			this.bufferHeight = bufferHeight;
			this.bufferedImage = bufferedImage;
			this.bufferedImageBounds = bufferedImageBounds;
			this.zoomFactor = zoomFactor;
		}
	}

	private static void drawImage(Graphics2D graphics2D, Image image, int x, int y)
	{
		// TODO WORKAROUND for StackOverflowError documented here:
		// http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg21170.html
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=74095
		// When this bug is fixed, remove this method and rename _getPdfImage(...) to getPdfImage(...)!
		//
		// Additionally, sometimes the rendering of the pdf image fails with a timeout. Therefore, I added
		// catching WaitForRenderingException. So maybe we keep this method?
		int tryCount = 0;
		while (true) {
			++tryCount;

			try {
				_drawImage(graphics2D, image, x, y);
				return;
			} catch (StackOverflowError error) {
				logger.warn("drawImage: WORKAROUND: Caught StackOverflowError with tryCount=" + tryCount, error); //$NON-NLS-1$

				if (tryCount > 5)
					throw error;
			} catch (WaitForRenderingException exception) {
				logger.warn("drawImage: WORKAROUND: Caught WaitForRenderingException with tryCount=" + tryCount, exception); //$NON-NLS-1$

				if (tryCount > 5)
					throw exception;
			}
		}
	}

	/**
	 * Draws the given image into the currently considered buffer.
	 *
	 * @param graphics2D the graphics context of the current buffer.
	 * @param image the image to draw into the current buffer.
	 * @param x the x coordinate of that point in the current buffer where the top-left corner of the given
	 * image that shall be drawn will be lying.
	 * @param y	the y coordinate of that point in the current buffer where the top-left corner of the given
	 * image that shall be drawn will be lying.
	 */
	private static void _drawImage(Graphics2D graphics2D, Image image, int x, int y)
	{
		BlockingImageObserver bio = new BlockingImageObserver();

		boolean renderingComplete = graphics2D.drawImage(image, x, y, bio);

		if (!renderingComplete)
			bio.waitForRendering();
	}

	/**
	 * Gets the image of the currently considered PDF page taking a given clip into consideration.
	 *
	 * @param pdfPage the currently considered page of the PDF document.
	 * @param pdfImageWidth the image width in the image coordinate system.
	 * @param pdfImageHeight the image height in the image coordinate system.
	 * @param clipLeftBottom a rectangle describing the region of interest of the currently considered PDF page.
	 * @return the image of the given PDF page, clipped as pretended by clipLeftBottom.
	 */
	private static Image _getPdfImage(PDFPage pdfPage, int pdfImageWidth, int pdfImageHeight, Rectangle2D clipLeftBottom)
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

	private static Image getPdfImage(PDFPage pdfPage, int pdfImageWidth, int pdfImageHeight, Rectangle2D clipLeftBottom)
	{
		// TODO WORKAROUND for StackOverflowError documented here:
		// http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg21170.html
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=74095
		// When this bug is fixed, remove this method and rename _getPdfImage(...) to getPdfImage(...)!
		//
		// Additionally, sometimes the rendering of the pdf image fails with a timeout. Therefore, I added
		// catching WaitForRenderingException. So maybe we keep this method?
		int tryCount = 0;
		while (true) {
			++tryCount;

			try {
				return _getPdfImage(pdfPage, pdfImageWidth, pdfImageHeight, clipLeftBottom);
			} catch (StackOverflowError error) {
				logger.warn("getPdfImage: WORKAROUND: Caught StackOverflowError with tryCount=" + tryCount, error); //$NON-NLS-1$

				if (tryCount > 5)
					throw error;
			} catch (WaitForRenderingException exception) {
				logger.warn("getPdfImage: WORKAROUND: Caught WaitForRenderingException with tryCount=" + tryCount, exception); //$NON-NLS-1$

				if (tryCount > 5)
					throw exception;
			}
		}
	}

	/**
	 * Draws a certain part (the region of interest) of the buffer onto the screen - or more precisely the viewPanel
	 * (passed as <code>graphics2D</code>).
	 *
	 * @param graphics2D the graphics context of the panel.
	 * @param graphics2DWidth the panel width.
	 * @param graphics2DHeight the panel height.
	 * @param requestedZoomFactor the zoom factor to use.
	 * @param region the region of interest of the document that shall be drawn onto the screen in real coordinates.
	 * @return <code>true</code>, if the region could be completely copied from the buffer. <code>false</code>, if the requested region
	 *		exceeds the buffer and therefore could only copy partially or not at all. Empty pages will be drawn instead.
	 */
	public boolean paintToView(
			Graphics2D graphics2D,
			int graphics2DWidth, int graphics2DHeight,
			double requestedZoomFactor,
			Rectangle2D region
	)
	{
		if (graphics2D == null)
			throw new IllegalArgumentException("graphics2D must not be null!"); //$NON-NLS-1$

		if (region == null)
			throw new IllegalArgumentException("region must not be null!"); //$NON-NLS-1$

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

				sourceX1 = (int) ((region.getMinX() - bufferedImageBounds.getMinX()) * zoomFactor * getZoomScreenResolutionFactorX());
				sourceY1 = (int) ((region.getMinY() - bufferedImageBounds.getMinY()) * zoomFactor * getZoomScreenResolutionFactorY());

				if (bufferedImageBounds.getMinX() > region.getMinX()) {
					sourceX1 = 0;
					destinationX1 = (int) ((bufferedImageBounds.getMinX() - region.getMinX()) * zoomFactor * getZoomScreenResolutionFactorX());
					bufferSufficient = false;
				}

				if (bufferedImageBounds.getMinY() > region.getMinY()) {
					sourceY1 = 0;
					destinationY1 = (int) ((bufferedImageBounds.getMinY() - region.getMinY()) * zoomFactor * getZoomScreenResolutionFactorY());
					bufferSufficient = false;
				}

				if (bufferedImageBounds.getMaxX() < region.getMaxX()) {
					destinationX2 = graphics2DWidth - (int) ((region.getMaxX() - bufferedImageBounds.getMaxX()) * zoomFactor * getZoomScreenResolutionFactorX());
					bufferSufficient = false;
				}

				if (bufferedImageBounds.getMaxY() < region.getMaxY()) {
					destinationY2 = graphics2DHeight - (int) ((region.getMaxY() - bufferedImageBounds.getMaxY()) * zoomFactor * getZoomScreenResolutionFactorY());
					bufferSufficient = false;
				}

				sourceX2 = sourceX1 + destinationX2 - destinationX1;
				sourceY2 = sourceY1 + destinationY2 - destinationY1;
			}

			pdfViewerComposite.firePaintToViewListeners(graphics2D, false);

			if (!bufferSufficient) {
				// draw background
				graphics2D.setColor(pdfViewerComposite.getViewPanel().getBackground());
				graphics2D.fillRect(0, 0, graphics2DWidth, graphics2DHeight);

				float referenceFontSize = 1000f;
				Font referenceFont = graphics2D.getFont().deriveFont(referenceFontSize);

				// draw empty pages and page numbers
				Collection<Integer> visiblePages = pdfDocument.getVisiblePages(region);
				for (Integer pageNumber : visiblePages) {
					Rectangle2D page = pdfDocument.getPageBounds(pageNumber);

					int x = (int) ((page.getX() - region.getX()) * requestedZoomFactor * getZoomScreenResolutionFactorX());
					int y = (int) ((page.getY() - region.getY()) * requestedZoomFactor * getZoomScreenResolutionFactorY());
					int w = (int) (page.getWidth() * requestedZoomFactor * getZoomScreenResolutionFactorX());
					int h = (int) (page.getHeight() * requestedZoomFactor * getZoomScreenResolutionFactorY());

//					int x = (int)Math.round((page.getX() - region.getX()) * requestedZoomFactor * getZoomScreenResolutionFactorX());
//					int y = (int)Math.round((page.getY() - region.getY()) * requestedZoomFactor * getZoomScreenResolutionFactorY());
//					int w = (int)Math.round(page.getWidth() * requestedZoomFactor * getZoomScreenResolutionFactorX());
//					int h = (int)Math.round(page.getHeight() * requestedZoomFactor * getZoomScreenResolutionFactorY());

					// draw empty page (border)
					graphics2D.setColor(COLOR_DRAFT_PAGE_BORDER);
					graphics2D.drawRect(x, y, w, h);

					// draw empty page (area)
					graphics2D.setColor(COLOR_DRAFT_PAGE_AREA);
					graphics2D.fillRect(x + 1, y + 1, w - 1, h - 1);

					// draw page number in the middle of each page
					graphics2D.setColor(COLOR_DRAFT_PAGE_NUMBER);
					String pageNumberString = Integer.toString(pageNumber);

					graphics2D.setFont(referenceFont);
					TextLayout layout = new TextLayout(pageNumberString, graphics2D.getFont(), graphics2D.getFontRenderContext());
					Rectangle2D referenceBounds = layout.getPixelBounds(null, 0, 0);

					Dimension2D desiredBounds = new Dimension2DDouble(0.9d * w, 0.9d * h);
					double factorX = desiredBounds.getWidth() / referenceBounds.getWidth();
					double factorY = desiredBounds.getHeight() / referenceBounds.getHeight();
					double factor = Math.min(factorX, factorY);

					graphics2D.setFont(
							graphics2D.getFont().deriveFont((float) (factor * referenceFontSize))
					);
					layout = new TextLayout(pageNumberString, graphics2D.getFont(), graphics2D.getFontRenderContext());
					Rectangle2D pageNumberBounds = layout.getPixelBounds(null, 0, 0);

// debug: draw bounds of the page-number
//					graphics2D.drawRect(
//							(int) (x + (double)w / 2 - pageNumberBounds.getWidth() / 2),
//							(int) (y + (double)h / 2 - pageNumberBounds.getHeight() / 2),
//							(int) pageNumberBounds.getWidth(),
//							(int) pageNumberBounds.getHeight()
//					);

					layout.draw(
							graphics2D,
							(float) (x + (double)w / 2 - pageNumberBounds.getWidth() / 2 - pageNumberBounds.getX()),
							(float) (y + (double)h / 2 - pageNumberBounds.getHeight() / 2 - pageNumberBounds.getY())
					);
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

			pdfViewerComposite.firePaintToViewListeners(graphics2D, true);

//			if (shadowsHaveToBeDrawn) {
//				Point2DDouble zoomScreenResolutionFactor = new Point2DDouble(pdfViewerComposite.getZoomScreenResolutionFactor());
//				Point2D viewOrigin = pdfViewerComposite.getViewOrigin();
//				double x = viewOrigin.getX();
//				double y = viewOrigin.getY();
//
//				graphics2D.setColor(Color.BLUE);
//				if (shadowBounds1 != null && shadowBounds2 != null && shadowBounds3 != null && shadowBounds4 != null) {
//					graphics2D.fillRect(	(int) (shadowBounds1.getX() - x * zoomScreenResolutionFactor.getX() * zoomFactorPerMill / 1000),
//											(int) (shadowBounds1.getY() - y * zoomScreenResolutionFactor.getY() * zoomFactorPerMill / 1000),
//											(int) shadowBounds1.getWidth(),
//											(int) shadowBounds1.getHeight()
//										);
//					graphics2D.fillRect(	(int) (shadowBounds2.getX() - x * zoomScreenResolutionFactor.getX() * zoomFactorPerMill / 1000),
//											(int) (shadowBounds2.getY() - y * zoomScreenResolutionFactor.getY() * zoomFactorPerMill / 1000),
//											(int) shadowBounds2.getWidth(),
//											(int) shadowBounds2.getHeight()
//										);
//					graphics2D.fillRect(	(int) (shadowBounds3.getX() - x * zoomScreenResolutionFactor.getX() * zoomFactorPerMill / 1000),
//											(int) (shadowBounds3.getY() - y * zoomScreenResolutionFactor.getY() * zoomFactorPerMill / 1000),
//											(int) shadowBounds3.getWidth(),
//											(int) shadowBounds3.getHeight()
//										);
//					graphics2D.fillRect(	(int) (shadowBounds4.getX() - x * zoomScreenResolutionFactor.getX() * zoomFactorPerMill / 1000),
//											(int) (shadowBounds4.getY() - y * zoomScreenResolutionFactor.getY() * zoomFactorPerMill / 1000),
//											(int) shadowBounds4.getWidth(),
//											(int) shadowBounds4.getHeight()
//										);
////					shadowsHaveToBeDrawn = false;
//				}
//			}

			return bufferSufficient;
		} // synchronized (bufferedImageMutex) {
	}

	private static final Color COLOR_DRAFT_PAGE_NUMBER = Color.GRAY;
	private static final Color COLOR_DRAFT_PAGE_BORDER = Color.RED;
	private static final Color COLOR_DRAFT_PAGE_AREA = new Color(240, 240, 240);

	private static final boolean DUMP_IMAGE_PAGE = false;
	private static final boolean DUMP_IMAGE_BUFFER = false;

	/**
	 * Writes a given image to a file (very time-consuming - ONLY FOR DEBUGGING!).
	 *
	 * @param image the image that shall be written to a file.
	 * @param filenamePrefix the prefix of the filename of the file the image is written to dependent on part and round.
	 */
	private static void printToImageFile(Image image, String filenamePrefix) {
		if (image == null)
			throw new IllegalArgumentException("image must not be null!"); //$NON-NLS-1$

		File dumpDir = new File(Util.getTempDir(), "pdfviewer_images"); //$NON-NLS-1$
		if (!dumpDir.exists())
			dumpDir.mkdirs();

		String fileFormat = "png"; //$NON-NLS-1$
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
			logger.warn("printToImageFile: writing image failed!", e); //$NON-NLS-1$
		} finally {
			writer.dispose();
		}
	}

//	private PropertyChangeListener propertyChangeListenerMouseClicked = new PropertyChangeListener() {
//		@Override
//		public void propertyChange(PropertyChangeEvent event) {
//
//			final Integer newCurrentPage;
//
//			// calculate chosen page from pdfMouseEvent.getPointInRealCoordinate()
//			MouseEvent pdfMouseEvent = (MouseEvent) event.getNewValue();
//			Collection<Integer> visiblePages = pdfDocument.getVisiblePages(
//								new Rectangle2D.Double(	pdfMouseEvent.getPointInRealCoordinate().getX(),
//														pdfMouseEvent.getPointInRealCoordinate().getY(),
//														1,
//														1
//														)
//			);
//
//			if (visiblePages.isEmpty()) {
//				newCurrentPage = null;
//			}
//			else {
//				newCurrentPage = visiblePages.iterator().next();
//
//				// get the page bounds of the chosen page in real coordinates
//				Rectangle2D pageBoundsReal = pdfDocument.getPageBounds(newCurrentPage);
//
//				Point2DDouble zoomScreenResolutionFactor = new Point2DDouble(pdfViewerComposite.getZoomScreenResolutionFactor());
//				zoomFactorPerMill = pdfViewerComposite.getZoomFactorPerMill();
//				double zoomFactor = (double)zoomFactorPerMill / 1000;
//
//				// get the page bounds of the chosen page in image coordinates
//				Rectangle2D pageBoundsImage = new Rectangle2D.Double();
//				pageBoundsImage.setRect	(	pageBoundsReal.getMinX() * zoomScreenResolutionFactor.getX() * zoomFactor,
//											pageBoundsReal.getMinY() * zoomScreenResolutionFactor.getY() * zoomFactor,
//											pageBoundsReal.getWidth() * zoomScreenResolutionFactor.getX() * zoomFactor,
//											pageBoundsReal.getHeight() * zoomScreenResolutionFactor.getY() * zoomFactor
//										);
//
//				shadowBounds1 = new Rectangle2D.Double();
//				shadowBounds2 = new Rectangle2D.Double();
//				shadowBounds3 = new Rectangle2D.Double();
//				shadowBounds4 = new Rectangle2D.Double();
//				shadowBounds1.setRect(	pageBoundsImage.getX() - 5,
//										pageBoundsImage.getY() - 5,
//										pageBoundsImage.getWidth() + 10,
//										5
//										);
//				shadowBounds2.setRect(	pageBoundsImage.getX() - 5,
//										pageBoundsImage.getY() - 5,
//										5,
//										pageBoundsImage.getHeight() + 10
//										);
//				shadowBounds3.setRect(	pageBoundsImage.getX() - 5,
//										pageBoundsImage.getMaxY(),
//										pageBoundsImage.getWidth() + 10,
//										5
//										);
//				shadowBounds4.setRect(	pageBoundsImage.getMaxX(),
//										pageBoundsImage.getY() - 5,
//										5,
//										pageBoundsImage.getHeight() + 10
//										);
//
//				shadowsHaveToBeDrawn = true;
//				pdfViewerComposite.getViewPanel().repaint();
//
//				if (logger.isDebugEnabled()) {
//					logger.debug(pageBoundsImage.getMinX() + " " + pageBoundsImage.getMinY() + " " + pageBoundsImage.getMaxX() + " " + pageBoundsImage.getMaxY());
//					logger.debug(shadowBounds1.getMinX() + " " + shadowBounds1.getMinY() + " " + shadowBounds1.getMaxX() + " " + shadowBounds1.getMaxY());
//					logger.debug(shadowBounds2.getMinX() + " " + shadowBounds2.getMinY() + " " + shadowBounds2.getMaxX() + " " + shadowBounds2.getMaxY());
//					logger.debug(shadowBounds3.getMinX() + " " + shadowBounds3.getMinY() + " " + shadowBounds3.getMaxX() + " " + shadowBounds3.getMaxY());
//					logger.debug(shadowBounds4.getMinX() + " " + shadowBounds4.getMinY() + " " + shadowBounds4.getMaxX() + " " + shadowBounds4.getMaxY());
//				}
//			}
//		}
//	};

	public PdfDocument getPdfDocument() {
		return pdfDocument;
	}

	public Rectangle2D.Double getBufferedImageBounds() {
		return bufferedImageBounds;
	}

	public int getBufferWidth() {
		return bufferWidth;
	}

	public int getBufferHeight() {
		return bufferHeight;
	}

	public double getZoomFactor() {
		return zoomFactor;
	}

	protected double getZoomScreenResolutionFactorX()
	{
		return pdfViewerComposite.getZoomScreenResolutionFactor().getX();
	}

	protected double getZoomScreenResolutionFactorY()
	{
		return pdfViewerComposite.getZoomScreenResolutionFactor().getY();
	}
}
