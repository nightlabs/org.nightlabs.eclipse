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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.print.PrinterConfiguration;
import org.nightlabs.print.PrinterInterface;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterConfiguratorDefault implements PrinterConfigurator {

//	private static Logger logger = Logger.getLogger(PrinterConfiguratorDefault.class); 
	
	private PrinterConfiguration initialConfiguration;
	private PrinterConfiguratorComposite configuratorComposite;
	
	/**
	 * 
	 */
	public PrinterConfiguratorDefault() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.print.PrinterConfigurator#discardComposite()
	 */
	public void discardComposite() {
		configuratorComposite = null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.print.PrinterConfigurator#init(org.nightlabs.base.print.PrinterConfiguration)
	 */
	public void init(PrinterConfiguration printerConfiguration) {
		if (printerConfiguration != null)
			this.initialConfiguration = (PrinterConfiguration)printerConfiguration;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.print.PrinterConfigurator#readPrinterConfiguration()
	 */
	public PrinterConfiguration readPrinterConfiguration() {
		if (configuratorComposite != null && !configuratorComposite.isDisposed())
			return configuratorComposite.readPrinterConfiguration();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.print.PrinterConfigurator#showComposite(org.eclipse.swt.widgets.Composite)
	 */
	public Composite showComposite(Composite parent) {
		configuratorComposite = new PrinterConfiguratorComposite(
				parent, SWT.NONE, XComposite.LayoutMode.ORDINARY_WRAPPER
			);
		configuratorComposite.init(initialConfiguration);
		return configuratorComposite;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.print.PrinterConfigurator#assignPrinterConfigurationInterface(org.nightlabs.base.print.PrinterConfiguration, org.nightlabs.base.print.PrinterInterface)
	 */
	public void assignPrinterConfigurationInterface(
			PrinterConfiguration printerConfiguration, PrinterInterface printerInterface
		) 
	{
		// TODO Auto-generated method stub
	}

}
