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

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.print.PrinterConfiguration;
import org.nightlabs.print.PrinterInterface;

/**
 * {@link PrinterConfigurator}s are used to visualize and edit
 * a {@link PrinterConfiguration} when the {@link PrinterInterfaceManager}
 * is asked to provide a {@link PrinterInterface} and also to configure
 * a {@link PrinterUseCase} in the preference page. 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface PrinterConfigurator {
	
	/**
	 * Initialize the configurator with the given {@link PrinterConfiguration}.
	 * @param printerConfiguration The printer configuration.
	 */
	public void init(PrinterConfiguration printerConfiguration);
	
	/**
	 * Create the UI of this configurator as child of the given parent and show
	 * the {@link PrinterConfiguration} set in {@link #init(PrinterConfiguration)}.
	 * 
	 * @param parent The parent to create the UI for.
	 * @return The newly created {@link Composite}.
	 */
	public Composite showComposite(Composite parent);
	/**
	 * Read the current {@link PrinterConfiguration} from the UI. 
	 * @return The configured {@link PrinterConfiguration}.
	 */
	public PrinterConfiguration readPrinterConfiguration();
}
