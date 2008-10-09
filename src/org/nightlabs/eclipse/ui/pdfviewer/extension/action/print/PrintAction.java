package org.nightlabs.eclipse.ui.pdfviewer.extension.action.print;

import java.awt.print.PrinterException;

import org.nightlabs.base.ui.print.PrinterInterfaceManager;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerAction;
import org.nightlabs.eclipse.ui.pdfviewer.extension.printer.PdfPrinter;
import org.nightlabs.print.PrinterConfiguration;
import org.nightlabs.print.PrinterInterface;

import com.sun.pdfview.PDFFile;

/**
 * @version $Revision$ - $Date$
 * @author kiran telukunta - kiran at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PrintAction extends PdfViewerAction {
	public static final String PRINTER_USE_CASE_ID = "org.nightlabs.eclipse.ui.pdfviewer.extension.printerUseCase";

	@Override
	public void run() {
		try {
			PrinterInterface iFace = PrinterInterfaceManager.sharedInstance().getConfiguredPrinterInterface(org.nightlabs.print.PrinterInterfaceManager.INTERFACE_FACTORY_DOCUMENT, PRINTER_USE_CASE_ID);
//			if (!(iFace instanceof DocumentPrinter))
//				throw new PrinterException("Obtained PrinterInterface was no DocumentPrinter but "+((iFace != null) ? iFace.getClass().getName() : "null")); //$NON-NLS-1$ //$NON-NLS-2$
			PdfPrinter pdfPrinter = new PdfPrinter();
			PrinterConfiguration printerConfiguration = iFace.getConfiguration();
			pdfPrinter.configure(printerConfiguration);
			PDFFile pdfFile = getPdfViewerActionRegistry().getPdfViewer().getPdfDocument().getPdfFile();
			pdfPrinter.printDocument(pdfFile);
		} catch (PrinterException e) {
			throw new RuntimeException(e);
		}
	}

}
