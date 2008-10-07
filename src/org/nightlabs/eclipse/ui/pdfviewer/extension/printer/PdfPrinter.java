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

public class PdfPrinter implements DocumentPrinter {

	private static final Logger logger = Logger.getLogger(PdfPrinter.class);
	private PrinterConfiguration printerConfiguration;
	private PDFFile pdfFile;
	private PdfPrintable pdfPrintable;
	private PrinterJob printerJob;
	private PageFormat defaultPage;

	@Override
	public void printDocument(File file) throws PrinterException {
		try {
	        pdfFile = PdfFileLoader.loadPdf(file, new NullProgressMonitor());
        } catch (IOException exception) {
        	PrinterException x = new PrinterException(exception.getMessage());
        	x.initCause(exception);
        	throw x;
        }
        this.printDocument(pdfFile);
	}

	public void printDocument(PDFFile file) throws PrinterException {

	    printerJob = PrinterJob.getPrinterJob();
		PrintService printService = PrintUtil.getConfiguredPrintService(printerConfiguration, true);
		printerJob.setPrintService(printService);
		defaultPage = printerJob.defaultPage();
	    printerJob.setPageable(new PdfPageable());

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

		@Override
        public int getNumberOfPages() {
	        int numberOfPages = pdfFile.getNumPages();
	        return numberOfPages;
        }

		@Override
        public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
			PDFPage pdfPage = pdfFile.getPage(pageIndex + 1);
			PageFormat pageFormat = new PageFormat();
			Paper paper = new Paper();
			paper.setSize(defaultPage.getWidth(), defaultPage.getHeight());
			pageFormat.setOrientation(getOrientation(pdfPage));
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

		@Override
        public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
			if (pdfPrintable == null)
	            pdfPrintable = new PdfPrintable();
            return pdfPrintable;
        }
	}

	private class PdfPrintable implements Printable {
		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

			double widthFactor = 0;
			double heightFactor = 0;
			double minFactor;
			boolean needScaling = false;

			Point2D.Double a4DimensionImage = new Point2D.Double(pageFormat.getWidth(), pageFormat.getHeight());

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
			double pageWidth = pdfPage.getBBox().getWidth();
			double pageHeight = pdfPage.getBBox().getHeight();

			Point screenDPI = Display.getDefault().getDPI();
			Point2DDouble zoomScreenResolutionFactor = new Point2DDouble(
					(double)screenDPI.x / 72,
					(double)screenDPI.y / 72
			);

			Point2D.Double pageBoundsDimensionImage = new Point2D.Double(
					(pageWidth * zoomScreenResolutionFactor.getX()),
					(pageHeight * zoomScreenResolutionFactor.getY())
			);

			if (logger.isDebugEnabled())
				logger.debug("print: Page bounds before scaling: " + pageBoundsDimensionImage.x + " " + pageBoundsDimensionImage.y);

			if (pageBoundsDimensionImage.x > a4DimensionImage.x) {
				widthFactor = a4DimensionImage.x / pageBoundsDimensionImage.x;
				needScaling = true;
			}

			if (pageBoundsDimensionImage.y > a4DimensionImage.y) {
				heightFactor = a4DimensionImage.y / pageBoundsDimensionImage.y;
				needScaling = true;
			}

			minFactor = Math.min(widthFactor, heightFactor);
			if (logger.isDebugEnabled())
				logger.debug("print: Scale-Factor: " + minFactor);

			if (needScaling) {
				pageBoundsDimensionImage.x = pageBoundsDimensionImage.x * minFactor;
				pageBoundsDimensionImage.y = pageBoundsDimensionImage.y * minFactor;
			}

			if (logger.isDebugEnabled())
				logger.debug("print: Page bounds after scaling: " + pageBoundsDimensionImage.x + " " + pageBoundsDimensionImage.y);

			PDFRenderer renderer = new PDFRenderer(	pdfPage,
													graphics2D,
													new Rectangle(	0,
																	0,
																	(int)(pageBoundsDimensionImage.x),
																	(int)(pageBoundsDimensionImage.y)),
													null,	// the whole page is drawn
													null);

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
