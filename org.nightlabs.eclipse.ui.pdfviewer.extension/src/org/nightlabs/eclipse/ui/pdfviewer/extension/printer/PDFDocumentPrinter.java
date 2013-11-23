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

import org.nightlabs.base.ui.print.pref.PDFDocumentPrinterPreferencePage;
import org.nightlabs.eclipse.ui.pdfrenderer.PDFPrinter;
import org.nightlabs.print.DocumentPrinter;
import org.nightlabs.print.PrintUtil;
import org.nightlabs.print.PrinterConfiguration;

/**
 * Implementation of {@link DocumentPrinter} to handle PDF files. Throw-away instances of this class are used to
 * print PDF documents.
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 */
public class PDFDocumentPrinter implements DocumentPrinter {

	private PrinterConfiguration printerConfiguration;

	private boolean usePDFPageDimensions;

	/**
	 * Prints the given file via PDF printer mechanism.
	 * @param file The file to print.
	 */
	@Override
	public void printDocument(final File file) throws PrinterException {
		// Creates and returns a PrinterJob which is configured with the given PrinterConfiguration
	    final PrinterJob printerJob = PrintUtil.getConfiguredPrinterJob(printerConfiguration);

		new PDFPrinter().printPDF(file, printerJob, usePDFPageDimensions);
	}

	@Override
	public void configure(final PrinterConfiguration printerConfiguration) throws PrinterException {
		this.printerConfiguration = printerConfiguration;

		// TODO in the case the printer configuration is not refreshed attributes like
		// PDFDocumentPrinterPreferencePage.ATTRIBUTE_NAME_PDF_PAGE_DIMENSIONS_USAGE_STATE could still have old values (restart
		// required); see also org.nightlabs.eclipse.ui.pdfviewer.extension.action.print.PrintAction
		usePDFPageDimensions = false;
		if (printerConfiguration != null && printerConfiguration.getAttributes().containsKey(
				PDFDocumentPrinterPreferencePage.ATTRIBUTE_NAME_PDF_PAGE_DIMENSIONS_USAGE_STATE)) {
			usePDFPageDimensions = (Boolean) printerConfiguration.getAttributes().get(
				PDFDocumentPrinterPreferencePage.ATTRIBUTE_NAME_PDF_PAGE_DIMENSIONS_USAGE_STATE);
		}
	}

	@Override
	public PrinterConfiguration getConfiguration() {
		return printerConfiguration;
	}
}
