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


/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterUseCase {

	public static final String DEFAULT_USE_CASE_ID = "PrinterUseCase-Default"; //$NON-NLS-1$
	
	private String id;
	private String name;
	private String description;
	private String defaultConfiguratorID;
	private PrinterConfiguratorFactory defaultConfiguratorFactory;
	private boolean useOnlyDefaultConfigurator = false;
	
	/**
	 * 
	 */
	public PrinterUseCase() {}

	/**
	 * @return the defaultConfiguratorFactory
	 */
	public PrinterConfiguratorFactory getDefaultConfiguratorFactory() {
		return defaultConfiguratorFactory;
	}

	/**
	 * @param defaultConfiguratorFactory the defaultConfiguratorFactory to set
	 */
	public void setDefaultConfiguratorFactory(
			PrinterConfiguratorFactory defaultConfiguratorFactory) {
		this.defaultConfiguratorFactory = defaultConfiguratorFactory;
	}

	/**
	 * @param defaultConfiguratorFactory the defaultConfiguratorFactory to set
	 */
	public void setDefaultConfiguratorID(
			String defaultConfiguratorID) {
		this.defaultConfiguratorID = defaultConfiguratorID;
	}
	
	public String getDefaultConfiguratorID() {
		return defaultConfiguratorID;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	void validate() {
		this.defaultConfiguratorFactory = PrinterConfigurationRegistry.sharedInstance().getPrinterConfiguratorFactory(defaultConfiguratorID);
	}

	/**
	 * @return the useOnlyDefaultConfigurator
	 */
	public boolean isUseOnlyDefaultConfigurator() {
		return useOnlyDefaultConfigurator;
	}

	/**
	 * @param useOnlyDefaultConfigurator the useOnlyDefaultConfigurator to set
	 */
	public void setUseOnlyDefaultConfigurator(boolean useOnlyDefaultConfigurator) {
		this.useOnlyDefaultConfigurator = useOnlyDefaultConfigurator;
	}
}
