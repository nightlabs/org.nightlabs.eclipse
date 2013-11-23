/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.ui.print;

import org.eclipse.swt.widgets.Display;
import org.nightlabs.print.PrinterConfiguration;

/**
 * Implements {@link org.nightlabs.print.PrinterInterfaceManager} and shows an 
 * {@link EditPrinterConfigurationDialog} to build the {@link PrinterConfiguration}
 * when asked to {@link #editPrinterConfiguration(String, boolean)}.
 *  
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class PrinterInterfaceManager extends org.nightlabs.print.PrinterInterfaceManager {
	
	/**
	 * 
	 */
	public PrinterInterfaceManager() {
	}

	@Override
	public PrinterConfiguration editPrinterConfiguration(final String printerUseCaseID, final boolean preSelectionDoStore) {
		final PrinterConfiguration[] config = new PrinterConfiguration[1]; 
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				config[0] = EditPrinterConfigurationDialog.openDialog(printerUseCaseID, preSelectionDoStore);
			}
		});
		return config[0];
	}

	private static PrinterInterfaceManager sharedInstance;
	
	public static PrinterInterfaceManager sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new PrinterInterfaceManager();
		return sharedInstance;
	}
	
}
