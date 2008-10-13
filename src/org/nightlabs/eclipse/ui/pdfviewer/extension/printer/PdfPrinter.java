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
package org.nightlabs.eclipse.ui.pdfviewer.extension.printer;

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

import javax.print.PrintService;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfviewer.PdfFileLoader;
import org.nightlabs.eclipse.ui.pdfviewer.Point2DDouble;
import org.nightlabs.print.DocumentPrinter;
import org.nightlabs.print.PrintUtil;
import org.nightlabs.print.PrinterConfiguration;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

/**
 * Implementation of {@link DocumentPrinter} to handle PDF files. Throw-away instances
 * of this class are used to print PDF documents.
 *
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 */
public class PdfPrinter implements DocumentPrinter {

	private static final Logger logger = Logger.getLogger(PdfPrinter.class);
	private PrinterConfiguration printerConfiguration;
	private PDFFile pdfFile;
	private PdfPrintable pdfPrintable;
	private PrinterJob printerJob;
	private PageFormat defaultPage;

	/**
	 * Gets a {@link PDFFile} from a {@link File} by using {@link PdfFileLoader}.
	 * @param file the file to print.
	 */
	@Override
	public void printDocument(File file) throws PrinterException {
		PDFFile pdfFile;
		try {
	        pdfFile = PdfFileLoader.loadPdf(file, new NullProgressMonitor());
        } catch (IOException exception) {
        	PrinterException x = new PrinterException(exception.getMessage());
        	x.initCause(exception);
        	throw x;
        }
        this.printDocument(pdfFile);
	}

	/**
	 * Creates {@link PrinterJob} and {@link PrintService} instances for printing and prints
	 * the loaded {@link PDFFile} using a {@link PdfPageable}.
	 * @param file the PDFFile to print.
	 */
	public void printDocument(PDFFile file) throws PrinterException {
		this.pdfFile = file;

		// Creates and returns a PrinterJob which is initially associated with the default printer.
	    printerJob = PrinterJob.getPrinterJob();

	    // Returns a PrintService describing the capabilities of the printer given by the printerConfiguration.
	    // The PrintService is also configured in compliance with the settings of this configuration.
		PrintService printService = PrintUtil.getConfiguredPrintService(printerConfiguration, true);

		// Associates the PrinterJob with the PrintService.
		printerJob.setPrintService(printService);

	    // Creates a new PageFormat instance and sets it to a default size and orientation.
		defaultPage = printerJob.defaultPage();

	    printerJob.setPageable(new PdfPageable());

	    // Prints a set of pages (the PDF document).
		printerJob.print();
	}

	@Override
	public void configure(PrinterConfiguration printerConfiguration) throws PrinterException {
		this.printerConfiguration = printerConfiguration;
	}

	@Override
	public PrinterConfiguration getConfiguration() {
		return printerConfiguration;
	}

	private class PdfPageable implements Pageable {

		/**
		 * Gets the number of pages of the given {@link PDFFile}.
		 * @return the number of pages.
		 */
		@Override
        public int getNumberOfPages() {
	        int numberOfPages = pdfFile.getNumPages();
	        return numberOfPages;
        }

		/**
		 * Gets the {@link PageFormat} for the current page of the given {@link PDFFile}.
		 * @param pageIndex the number of the current page (zero-based) to get the {@link PageFormat} for.
		 * @return the PageFormat for the considered page.
		 */
		@Override
        public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {

			PDFPage pdfPage = pdfFile.getPage(pageIndex + 1);	// pageIndex is zero-based!
			PageFormat pageFormat = new PageFormat();
			Paper paper = new Paper();

			// Sets the width and height of the Paper object to the measures of the default page (see above).
			paper.setSize(defaultPage.getWidth(), defaultPage.getHeight());
			// Sets the orientation of the PageFormat object to the one of the currently considered PDF page.
			pageFormat.setOrientation(getOrientation(pdfPage));
			// Sets the imageable area (the area in which printing occurs) of the Paper object according to the
			// measures of the default page. The imageable area of the paper begins directly at the top-left point.
			paper.setImageableArea(0, 0, defaultPage.getWidth(), defaultPage.getHeight());

			pageFormat.setPaper(paper);

			if (logger.isDebugEnabled()) {
				logger.debug("getPageFormat: width " + pageFormat.getWidth());
				logger.debug("getPageFormat: height " + pageFormat.getHeight());
				logger.debug("getPageFormat: imageableX " + pageFormat.getImageableX());
				logger.debug("getPageFormat: imageableY " + pageFormat.getImageableY());
				logger.debug("getPageFormat: imageableWidth " + pageFormat.getImageableWidth());
				logger.debug("getPageFormat: imageableHeight " + pageFormat.getImageableHeight());
			}

			return pageFormat;
        }

		/**
		 * Gets a {@link Printable} for the current page of the given {@link PDFFile}.
		 * @param pageIndex the number of the current page (zero-based) to get the {@link Printable} for.
		 * @return a {@link Printable} for the considered page.
		 */
		@Override
        public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
			if (pdfPrintable == null)
	            pdfPrintable = new PdfPrintable();
            return pdfPrintable;
        }
	}

	private class PdfPrintable implements Printable {

		/**
		 * Prints the page at the specified index (zero-based) into the specified {@link Graphics} context in the specified format.
		 * @param graphics the context into which the page is drawn.
		 * @param pageFormat the {@link PageFormat} (size and orientation) of the current page to be drawn.
		 * @param pageIndex the number of the current page to be drawn (zero-based).
		 * @return PAGE_EXISTS if the page is rendered successfully or NO_SUCH_PAGE if <code>pageIndex</code>
		 * specifies a non-existent page.
		 */
		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

			/**
			 * The factors widthScaleFactor and heightScaleFactor are used for scaling the given {@link PDFPage} to fit the paper dimensions
			 * described by the default page. Only the smallest factor minScaleFactor of both will be taken into consideration for scaling.
			 */
			double widthScaleFactor = 0;
			double heightScaleFactor = 0;
			double minScaleFactor;

			boolean needScaling = false;


			// Gets the dimensions of the current PageFormat (i.e. the dimensions of the default page).
			Point2D.Double defaultPageDimensions = new Point2D.Double(pageFormat.getWidth(), pageFormat.getHeight());

			if (logger.isDebugEnabled()) {
				logger.debug("print: Print pageIndex " + pageIndex);
				logger.debug("print: PageFormat width " + pageFormat.getWidth());
				logger.debug("print: PageFormat height " + pageFormat.getHeight());
				logger.debug("print: PageFormat imageableX " + pageFormat.getImageableX());
				logger.debug("print: PageFormat imageableY " + pageFormat.getImageableY());
				logger.debug("print: PageFormat imageableWidth " + pageFormat.getImageableWidth());
				logger.debug("print: PageFormat imageableHeight " + pageFormat.getImageableHeight());
			}

			int pageNumber = pageIndex + 1; 	// pageIndex is zero-based!
			Graphics2D graphics2D = (Graphics2D)graphics;
			PDFPage pdfPage = pdfFile.getPage(pageNumber);

			// Gets the dimensions of the page bounds of the current PDF page before applying a screenResolutionFactor.
			double pageWidth = pdfPage.getBBox().getWidth();
			double pageHeight = pdfPage.getBBox().getHeight();

			// Gets the screen resolution of default display
			final Point[] screenDPI = new Point[1];
			final Display display = Display.getDefault();
			display.syncExec(new Runnable() {
	            public void run() {
	            	screenDPI[0] = display.getDPI();
	            }
            });

			// Sets the screen resolution factor.
			Point2DDouble screenResolutionFactor = new Point2DDouble(
					(double)screenDPI[0].x / 72,
					(double)screenDPI[0].y / 72
			);

			// Sets the dimensions of the page bounds of the current PDF page after applying the computed screenResolutionFactor.
			Point2D.Double pageBoundsImage = new Point2D.Double(
					(pageWidth * screenResolutionFactor.getX()),
					(pageHeight * screenResolutionFactor.getY())
			);

			if (logger.isDebugEnabled())
				logger.debug("print: Page bounds before scaling: " + pageBoundsImage.x + " " + pageBoundsImage.y);

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

			if (logger.isDebugEnabled()) {
				logger.debug("print: Scale-Factor: " + minScaleFactor);
				logger.debug("print: Page bounds after scaling: " + pageBoundsImage.x + " " + pageBoundsImage.y);
			}

			// Creates a new PDFGraphics state, given a Graphics2D.
			// The image bounds into which to fit the page are described by a rectangle using the (possibly scaled)
			// page bounds as bounds.
		    // A clipping will not be used as the whole page should be drawn.
			PDFRenderer renderer = new PDFRenderer(	pdfPage,
													graphics2D,
													new Rectangle(
														0,
														0,
														(int)(pageBoundsImage.x),
														(int)(pageBoundsImage.y)
													),
													null,	// do not use any clipping
													null	// do not use any background color
			);

			try {
				pdfPage.waitForFinish();
            } catch (InterruptedException exception) {
               	PrinterException x = new PrinterException(exception.getMessage());
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
	private int getOrientation(PDFPage pdfPage) {
		float aspectRatio = pdfPage.getAspectRatio();
		if (aspectRatio < 1) {
			return PageFormat.PORTRAIT;
		}
		else {
			return PageFormat.LANDSCAPE;
		}
	}

}
