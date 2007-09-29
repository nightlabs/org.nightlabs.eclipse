/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.print;

import org.eclipse.swt.widgets.Display;
import org.nightlabs.print.PrinterConfiguration;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterInterfaceManager extends org.nightlabs.print.PrinterInterfaceManager {
	
//	private static final Logger logger = Logger.getLogger(PrinterInterfaceManager.class);

	private static class ConfigHolder {
		public PrinterConfiguration printerConfiguration;
	}
	
	/**
	 * 
	 */
	public PrinterInterfaceManager() {
	}

	@Override
	public PrinterConfiguration editPrinterConfiguration(final String printerUseCaseID, final boolean preSelectionDoStore) {
		final ConfigHolder holder = new ConfigHolder();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				holder.printerConfiguration = EditPrinterConfigurationDialog.openDialog(printerUseCaseID, preSelectionDoStore);
			}
		});
		return holder.printerConfiguration;
	}

	private static PrinterInterfaceManager sharedInstance;
	
	public static PrinterInterfaceManager sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new PrinterInterfaceManager();
		return sharedInstance;
	}
	
}
