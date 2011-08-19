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
package org.nightlabs.eclipse.ui.pdfrenderer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfrenderer.internal.NullPDFProgressMonitor;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

/**
 * Handles the printing of PDF files. Throw-away instances of this class are used to print PDF documents.
 * @version $Revision: 349 $ - $Date: 2008-10-13 15:32:46 +0200 (Mon, 13 Oct 2008) $
 * @author frederik loeser - frederik at nightlabs dot de
 */
public class PDFPrinter {

	private static final Logger LOGGER = Logger.getLogger(PDFPrinter.class);

	//	private static final double PAGE_WIDTH_A4 = (21 / 2.54) * 72;
	//	private static final double PAGE_HEIGHT_A4 = (29.7 / 2.54) * 72;

	private PDFFile pdfFile;
	private PDFPrintable pdfPrintable;
	private PageFormat defaultPage;

	private boolean usePDFPageDimensions;

	/**
	 * Gets a {@link PDFFile} from a {@link File} by using {@link PDFFileLoader}.
	 * @param file the file to print.
	 */
	public void printPDF(final File file, final PrinterJob printerJob, final boolean usePDFPageDimensions) throws PrinterException {
		PDFFile pdfFile = null;
		try {
			pdfFile = PDFFileLoader.loadPDF(file, new NullPDFProgressMonitor());
		} catch (final IOException exception) {
			final PrinterException x = new PrinterException(exception.getMessage());
			x.initCause(exception);
			throw x;
		}
		printPDF(pdfFile, printerJob, usePDFPageDimensions);
	}

	public void printPDF(final PDFFile pdfFile, final PrinterJob printerJob, final boolean usePDFPageDimensions)
		throws PrinterException {

		this.pdfFile = pdfFile;
		this.usePDFPageDimensions = usePDFPageDimensions;

		// Creates a new PageFormat instance and sets it to a default size and orientation.
		defaultPage = printerJob.defaultPage();

		printerJob.setPageable(new PDFPageable());

		// Prints a set of pages (the PDF document).
		printerJob.print();
	}

	private class PDFPageable implements Pageable {

		/**
		 * Gets the number of pages of the given {@link PDFFile}.
		 * @return the number of pages.
		 */
		@Override
		public int getNumberOfPages() {
			final int numberOfPages = pdfFile.getNumPages();
			return numberOfPages;
		}

		/**
		 * Gets the {@link PageFormat} for the current page of the given {@link PDFFile}.
		 * @param pageIndex the number of the current page (zero-based) to get the {@link PageFormat} for.
		 * @return the PageFormat for the considered page.
		 */
		@Override
		public PageFormat getPageFormat(final int pageIndex) throws IndexOutOfBoundsException {

			final PDFPage pdfPage = pdfFile.getPage(pageIndex + 1);	// pageIndex is zero-based!

			final Paper paper = new Paper();
			// Sets the width and height of the Paper object to the measures of the default page (see above).
			paper.setSize(usePDFPageDimensions ? pdfPage.getBBox().getWidth() : defaultPage.getWidth(),
				usePDFPageDimensions ? pdfPage.getBBox().getHeight() : defaultPage.getHeight());

			// Sets the imageable area (the area in which printing occurs) of the Paper object according to the
			// measures of the default page. The imageable area of the paper begins directly at the top-left point.
			paper.setImageableArea(0, 0, usePDFPageDimensions ? pdfPage.getBBox().getWidth() : defaultPage.getWidth(),
				usePDFPageDimensions ? pdfPage.getBBox().getHeight() : defaultPage.getHeight());

			final PageFormat pageFormat = new PageFormat();
			// Sets the orientation of the PageFormat object to the one of the currently considered PDF page.
			pageFormat.setOrientation(getOrientation(pdfPage));
			pageFormat.setPaper(paper);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("getPageFormat: width " + pageFormat.getWidth()); //$NON-NLS-1$
				LOGGER.debug("getPageFormat: height " + pageFormat.getHeight()); //$NON-NLS-1$
				LOGGER.debug("getPageFormat: imageableX " + pageFormat.getImageableX()); //$NON-NLS-1$
				LOGGER.debug("getPageFormat: imageableY " + pageFormat.getImageableY()); //$NON-NLS-1$
				LOGGER.debug("getPageFormat: imageableWidth " + pageFormat.getImageableWidth()); //$NON-NLS-1$
				LOGGER.debug("getPageFormat: imageableHeight " + pageFormat.getImageableHeight()); //$NON-NLS-1$
			}

			return pageFormat;
		}

		/**
		 * Gets a {@link Printable} for the current page of the given {@link PDFFile}.
		 * @param pageIndex the number of the current page (zero-based) to get the {@link Printable} for.
		 * @return a {@link Printable} for the considered page.
		 */
		@Override
		public Printable getPrintable(final int pageIndex) throws IndexOutOfBoundsException {
			if (pdfPrintable == null) {
				pdfPrintable = new PDFPrintable();
			}
			return pdfPrintable;
		}
	}

	private class PDFPrintable implements Printable {

		/**
		 * Prints the page at the specified index (zero-based) into the specified {@link Graphics} context in the specified format.
		 * @param graphics the context into which the page is drawn.
		 * @param pageFormat the {@link PageFormat} (size and orientation) of the current page to be drawn.
		 * @param pageIndex the number of the current page to be drawn (zero-based).
		 * @return PAGE_EXISTS if the page is rendered successfully or NO_SUCH_PAGE if <code>pageIndex</code>
		 * specifies a non-existent page.
		 */
		@Override
		public int print(final Graphics graphics, final PageFormat pageFormat, final int pageIndex) throws PrinterException {

			/**
			 * The factors widthScaleFactor and heightScaleFactor are used for scaling the given {@link PDFPage} to fit the paper dimensions
			 * described by the default page. Only the smallest factor minScaleFactor of both will be taken into consideration for scaling.
			 */
			double widthScaleFactor = 0;
			double heightScaleFactor = 0;
			double minScaleFactor;

			boolean needScaling = false;


			// Gets the dimensions of the current PageFormat (i.e. the dimensions of the default page).
			final Point2D.Double defaultPageDimensions = new Point2D.Double(pageFormat.getWidth(), pageFormat.getHeight());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("print: Print pageIndex " + pageIndex); //$NON-NLS-1$
				LOGGER.debug("print: PageFormat width " + pageFormat.getWidth()); //$NON-NLS-1$
				LOGGER.debug("print: PageFormat height " + pageFormat.getHeight()); //$NON-NLS-1$
				LOGGER.debug("print: PageFormat imageableX " + pageFormat.getImageableX()); //$NON-NLS-1$
				LOGGER.debug("print: PageFormat imageableY " + pageFormat.getImageableY()); //$NON-NLS-1$
				LOGGER.debug("print: PageFormat imageableWidth " + pageFormat.getImageableWidth()); //$NON-NLS-1$
				LOGGER.debug("print: PageFormat imageableHeight " + pageFormat.getImageableHeight()); //$NON-NLS-1$
			}

			final int pageNumber = pageIndex + 1; 	// pageIndex is zero-based!
			final Graphics2D graphics2D = (Graphics2D)graphics;
			final PDFPage pdfPage = pdfFile.getPage(pageNumber);

			// Gets the dimensions of the page bounds of the current PDF page before applying a screenResolutionFactor.
			final double pageWidth = pdfPage.getBBox().getWidth();
			final double pageHeight = pdfPage.getBBox().getHeight();

			// TODO: Alex: Frederik, what does the screen-resolution have to do with printing? Is the following, commented code really necessary?
			// Gets the screen resolution of default display
			final org.eclipse.swt.graphics.Point[] screenDPI = new org.eclipse.swt.graphics.Point[1];

			final Display display = Display.getDefault();
			display.syncExec(new Runnable() {
				public void run() {
					screenDPI[0] = display.getDPI();
				}
			});

			// Sets the screen resolution factor.
			final Point2D.Double screenResolutionFactor = new Point2D.Double(
					(double)screenDPI[0].x / 72,
					(double)screenDPI[0].y / 72
			);

			// Sets the dimensions of the page bounds of the current PDF page after applying the computed screenResolutionFactor.
			final Point2D.Double pageBoundsImage = new Point2D.Double(
					(pageWidth) * screenResolutionFactor.getX(),
					(pageHeight) * screenResolutionFactor.getY()
			);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("print: Page bounds before scaling: " + pageBoundsImage.x + " " + pageBoundsImage.y); //$NON-NLS-1$ //$NON-NLS-2$
			}

			// Tests if scaling has to be done to fit the current PDF page to the dimensions of default page.
			if (pageBoundsImage.x > defaultPageDimensions.x) {
				widthScaleFactor = defaultPageDimensions.x / pageBoundsImage.x;
				needScaling = true;
			}
			if (pageBoundsImage.y > defaultPageDimensions.y) {
				heightScaleFactor = defaultPageDimensions.y / pageBoundsImage.y;
				needScaling = true;
			}
			// Only the smallest factor of both factors will be taken into consideration and used for scaling.
			minScaleFactor = Math.min(widthScaleFactor, heightScaleFactor);

			if (needScaling) {
				pageBoundsImage.x = pageBoundsImage.x * minScaleFactor;
				pageBoundsImage.y = pageBoundsImage.y * minScaleFactor;
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("print: Scale-Factor: " + minScaleFactor); //$NON-NLS-1$
				LOGGER.debug("print: Page bounds after scaling: " + pageBoundsImage.x + " " + pageBoundsImage.y); //$NON-NLS-1$ //$NON-NLS-2$
			}

			// Creates a new PDFGraphics state, given a Graphics2D.
			// The image bounds into which to fit the page are described by a rectangle using the (possibly scaled)
			// page bounds as bounds.
			// A clipping will not be used as the whole page should be drawn.
			final PDFRenderer renderer = new PDFRenderer(pdfPage, graphics2D,
				new Rectangle(0, 0, (int) pageBoundsImage.x, (int) pageBoundsImage.y),
				null,	// do not use any clipping
				null	// do not use any background color
			);

			try {
				pdfPage.waitForFinish();
			} catch (final InterruptedException exception) {
				final PrinterException x = new PrinterException(exception.getMessage());
				x.initCause(exception);
				throw x;
			}

			renderer.run();

			return Printable.PAGE_EXISTS;
		}
	}

	//	public static void main(String[] args) throws PrinterException {
	//
	//		PrinterConfiguration printerConfiguration = new PrinterConfiguration();
	//		printerConfiguration.setPrintServiceName("KyoceraFS-1030D");
	////		printerConfiguration.setPrintServiceName("CupsPDF");
	//	    PdfPrinter printer = new PdfPrinter();
	//	    printer.configure(printerConfiguration);
	////	    printer.configure(null);
	//
	//	    printer.printDocument(new File("/home/frederik/pdfs/JFire-Order.pdf"));
	////	    printer.printDocument(new File("/home/frederik/pdfs/recommender-systems.pdf"));
	////	    printer.printDocument(new File("/home/frederik/pdfs/numerics0304.pdf"));
	////	    printer.printDocument(new File("/home/frederik/pdfs/landscape-format.pdf"));
	//
	//    }

	/**
	 * Gets the orientation of the given PDF page.
	 * @param pdfPage the PDF page to get the orientation from.
	 * @return the orientation of the given PDF page.
	 */
	private int getOrientation(final PDFPage pdfPage) {
		final float aspectRatio = pdfPage.getAspectRatio();
		if (aspectRatio < 1) {
			return PageFormat.PORTRAIT;
		}
		else {
			return PageFormat.LANDSCAPE;
		}
	}

}
