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

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;

import javax.print.PrintService;

import org.nightlabs.eclipse.ui.pdfrenderer.PdfFileLoader;
import org.nightlabs.print.DocumentPrinter;
import org.nightlabs.print.PrintUtil;
import org.nightlabs.print.PrinterConfiguration;

import com.sun.pdfview.PDFFile;

/**
 * Implementation of {@link DocumentPrinter} to handle PDF files. Throw-away instances
 * of this class are used to print PDF documents.
 *
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 */
public class PdfPrinter implements DocumentPrinter {

	private PrinterConfiguration printerConfiguration;
	private PrinterJob printerJob;

	/**
	 * Gets a {@link PDFFile} from a {@link File} by using {@link PdfFileLoader}.
	 * @param file the file to print.
	 */
	@Override
	public void printDocument(File file) throws PrinterException {

		// Creates and returns a PrinterJob which is initially associated with the default printer.
	    printerJob = PrinterJob.getPrinterJob();

	    // Returns a PrintService describing the capabilities of the printer given by the printerConfiguration.
	    // The PrintService is also configured in compliance with the settings of this configuration.
		PrintService printService = PrintUtil.getConfiguredPrintService(printerConfiguration, true);

		// Associates the PrinterJob with the PrintService.
		printerJob.setPrintService(printService);

		new org.nightlabs.eclipse.ui.pdfrenderer.PdfPrinter().printPdf(file, printerJob);
	}

	@Override
	public void configure(PrinterConfiguration printerConfiguration) throws PrinterException {
		this.printerConfiguration = printerConfiguration;
	}

	@Override
	public PrinterConfiguration getConfiguration() {
		return printerConfiguration;
	}
}
