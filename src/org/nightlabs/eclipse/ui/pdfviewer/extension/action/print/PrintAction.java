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
package org.nightlabs.eclipse.ui.pdfviewer.extension.action.print;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import org.nightlabs.base.ui.print.PrinterInterfaceManager;
import org.nightlabs.eclipse.ui.pdfrenderer.PdfPrinter;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerAction;
import org.nightlabs.print.PrintUtil;
import org.nightlabs.print.PrinterInterface;

import com.sun.pdfview.PDFFile;


/**
 * @version $Revision$ - $Date$
 * @author kiran telukunta - kiran at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PrintAction extends PdfViewerAction {

	public static final String PRINTER_USE_CASE_ID = "org.nightlabs.eclipse.ui.pdfviewer.extension.printerUseCase";

	/**
	 * Creates a new {@link PDFPrinter} instance, sets its configuration and prints the given {@link PDFFile}.
	 */
	@Override
	public void run() {
		try {
			PrinterInterface iFace = PrinterInterfaceManager.sharedInstance().getConfiguredPrinterInterface(org.nightlabs.print.PrinterInterfaceManager.INTERFACE_FACTORY_DOCUMENT, PRINTER_USE_CASE_ID);

			if (iFace.getConfiguration() == null)
				return;

			PdfPrinter pdfPrinter = new PdfPrinter();
			
			// Creates and returns a PrinterJob which is configured with the given PrinterConfiguration
		    PrinterJob printerJob = PrintUtil.getConfiguredPrinterJob(iFace.getConfiguration());

			PDFFile pdfFile = getPdfViewerActionRegistry().getPdfViewer().getPdfDocument().getPdfFile();
			
			// Print the file
			pdfPrinter.printPdf(pdfFile, printerJob);

		} catch (PrinterException e) {
			throw new RuntimeException(e);
		}
	}

}
