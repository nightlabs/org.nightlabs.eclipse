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
