/**
 *
 */
package org.nightlabs.eclipse.ui.pdfviewer.extension.printer;

import org.eclipse.ui.IStartup;
import org.nightlabs.print.DelegatingDocumentPrinterCfMod;

/**
 * @version $Revision$ - $Date$
 * @author frederik löser - frederik at nightlabs dot de
 */
public class PdfPrinterRegistrationStartup implements IStartup {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	@Override
	public void earlyStartup() {
		DelegatingDocumentPrinterCfMod cfMod = DelegatingDocumentPrinterCfMod.sharedInstance();
		if (!cfMod.getKnownExtensions().contains("pdf")) {
			cfMod.addKnownExtension("pdf");

			DelegatingDocumentPrinterCfMod.ExternalEngineDelegateConfig config = new DelegatingDocumentPrinterCfMod.ExternalEngineDelegateConfig();
			config.setClassName(PdfPrinter.class.getName());
			cfMod.setPrintConfig("pdf", config);
		}
	}

}
