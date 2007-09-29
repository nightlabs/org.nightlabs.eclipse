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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterConfigurationRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.printerConfiguration"; //$NON-NLS-1$
	
	public static class ConfiguratorFactoryEntry {
		private String id;
		private String name;
		private PrinterConfiguratorFactory printerConfiguratorFactory;		
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
		 * @return the printerConfiguratorFactory
		 */
		public PrinterConfiguratorFactory getPrinterConfiguratorFactory() {
			return printerConfiguratorFactory;
		}
		/**
		 * @param printerConfiguratorFactory the printerConfiguratorFactory to set
		 */
		public void setPrinterConfiguratorFactory(
				PrinterConfiguratorFactory printerConfiguratorFactory) {
			this.printerConfiguratorFactory = printerConfiguratorFactory;
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
		
		
		
	}
	
	private Map<String, PrinterUseCase> printerUseCases = new HashMap<String, PrinterUseCase>();
	private Map<String, ConfiguratorFactoryEntry> printerConfiguratorFactories = new HashMap<String, ConfiguratorFactoryEntry>();
	
	/**
	 * 
	 */
	public PrinterConfigurationRegistry() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element) 
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase("printerConfiguratorFactory")) //$NON-NLS-1$
			processConfigurator(extension, element);
		else if (element.getName().equalsIgnoreCase("printerUseCase")) //$NON-NLS-1$
			processUseCase(extension, element);
	}
	
	private void processUseCase(IExtension extension, IConfigurationElement element) 
	throws EPProcessorException 
	{
		String id = element.getAttribute("id"); //$NON-NLS-1$
		if (!checkString(id))
			throw new EPProcessorException("The attribute id must be defined for element printerUseCase.", extension); //$NON-NLS-1$
		String name = element.getAttribute("name"); //$NON-NLS-1$
		if (!checkString(name))
			throw new EPProcessorException("The attribute id must be defined for element printerUseCase.", extension); //$NON-NLS-1$
		String description = element.getAttribute("description"); //$NON-NLS-1$
		String defConfigurator = element.getAttribute("defaultConfigurator"); //$NON-NLS-1$
		String useOnlyDef = element.getAttribute("useOnlyDefaultConfigurator"); //$NON-NLS-1$
		PrinterUseCase useCase = new PrinterUseCase();
		useCase.setId(id);
		useCase.setName(name);
		useCase.setDescription(description);
		useCase.setDefaultConfiguratorID(defConfigurator);
		printerUseCases.put(id, useCase);
		if (useOnlyDef != null && !"".equals(useOnlyDef)) //$NON-NLS-1$
			useCase.setUseOnlyDefaultConfigurator(Boolean.parseBoolean(useOnlyDef));
	} 

	private void processConfigurator(IExtension extension, IConfigurationElement element) 
	throws EPProcessorException 
	{
		String id = element.getAttribute("id"); //$NON-NLS-1$
		if (!checkString(id))
			throw new EPProcessorException("The attribute id must be defined for element printerConfiguratorFactory.", extension); //$NON-NLS-1$
		
		String name = element.getAttribute("name"); //$NON-NLS-1$
		if (!checkString(id))
			throw new EPProcessorException("The attribute name must be defined for element printerConfiguratorFactory.", extension); //$NON-NLS-1$
		
		PrinterConfiguratorFactory factory;
		try {
			factory = (PrinterConfiguratorFactory)element.createExecutableExtension("class"); //$NON-NLS-1$
		} catch (CoreException e) {
			throw new EPProcessorException(e);
		}
		ConfiguratorFactoryEntry entry = new ConfiguratorFactoryEntry();
		entry.setId(id);
		entry.setName(name);
		entry.setPrinterConfiguratorFactory(factory);
		printerConfiguratorFactories.put(id, entry);
	}
	
	private void validate() {
		for (PrinterUseCase useCase : printerUseCases.values()) {
			useCase.validate();
		}
	}

	/**
	 * Returns the registered {@link PrinterConfiguratorFactory} for the given id.
	 * 
	 * @param id The unique id to search a {@link PrinterConfiguratorFactory} for.
	 * @return The registered {@link PrinterConfiguratorFactory} for the given id.
	 */
	public PrinterConfiguratorFactory getPrinterConfiguratorFactory(String id) {
		ConfiguratorFactoryEntry entry = printerConfiguratorFactories.get(id);
		if (entry == null)
			return null;
		return entry.getPrinterConfiguratorFactory();
	}
	
	public Collection<ConfiguratorFactoryEntry> getPrinterConfiguratorEntries() {
		return printerConfiguratorFactories.values();
	}
	
	public ConfiguratorFactoryEntry getPrinterConfiguratorEntry(String id) {
		return printerConfiguratorFactories.get(id);
	}
	
	/**
	 * Returns the registered {@link PrinterUseCase} with the given id or null if none found.
	 * 
	 * @param id The id to search a {@link PrinterUseCase} for.
	 * @return The registered {@link PrinterUseCase} with the given id or null if none found.
	 */
	public PrinterUseCase getPrinterUseCase(String id) {
		return printerUseCases.get(id);
	}
	
	public List<PrinterUseCase> getPrinterUseCases() {
		return new ArrayList<PrinterUseCase>(printerUseCases.values());
	}
	
	private static PrinterConfigurationRegistry sharedInstance;
	
	public static PrinterConfigurationRegistry sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new PrinterConfigurationRegistry();
			sharedInstance.process();
			sharedInstance.validate();
		}
		return sharedInstance;
	}
}
