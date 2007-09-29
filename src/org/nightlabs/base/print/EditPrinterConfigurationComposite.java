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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.print.PrinterConfigurationRegistry.ConfiguratorFactoryEntry;
import org.nightlabs.print.PrinterConfiguration;
import org.nightlabs.print.PrinterConfigurationCfMod;

/**
 * Composite to edit the {@link PrinterConfiguration} for a certain
 * {@link PrinterUseCase}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EditPrinterConfigurationComposite extends XComposite {

	private String printerUseCaseID;
	private PrinterUseCase printerUseCase;
	private PrinterConfiguration printerConfiguration;
	private PrinterConfiguratorFactory configuratorFactory;
	private PrinterConfigurator configurator;
	private Composite configuratorComposite;
	
	private XComposite configuratorWrapper;
	private ConfiguratorCombo configuratorCombo;
	
	private ConfiguratorFactoryEntry selectedConfiguratorFactoryEntry;
	
	public EditPrinterConfigurationComposite(Composite parent, int style, XComposite.LayoutMode layoutMode, String printerUseCaseID) {
		this(parent, style, layoutMode, printerUseCaseID, null);
	}
	
	/**
	 * @param parent
	 * @param style
	 */
	public EditPrinterConfigurationComposite(Composite parent, int style, XComposite.LayoutMode layoutMode, String printerUseCaseID, PrinterConfiguration _printerConfiguration) {
		super(parent, style, layoutMode);
		this.printerUseCaseID = printerUseCaseID;		
		printerUseCase = PrinterConfigurationRegistry.sharedInstance().getPrinterUseCase(printerUseCaseID);
		if (printerUseCase == null)
			throw new RuntimeException("The PrinterUseCase to be edited is not registered: '"+printerUseCaseID+"'!"); //$NON-NLS-1$ //$NON-NLS-2$
		if (_printerConfiguration != null)
			printerConfiguration = _printerConfiguration;
		else
			printerConfiguration = PrinterConfigurationCfMod.getPrinterConfiguration(printerUseCaseID);
		if (printerConfiguration != null)
			printerConfiguration = (PrinterConfiguration)printerConfiguration.clone();

		Collection<ConfiguratorFactoryEntry> factoryEnties = null;
		if (printerUseCase.isUseOnlyDefaultConfigurator() && printerUseCase.getDefaultConfiguratorFactory() != null) {
			ConfiguratorFactoryEntry factoryEntry = PrinterConfigurationRegistry.sharedInstance().getPrinterConfiguratorEntry(printerUseCase.getDefaultConfiguratorID());
			factoryEnties = new ArrayList<ConfiguratorFactoryEntry>();
			factoryEnties.add(factoryEntry);
		}
		else 
			factoryEnties = PrinterConfigurationRegistry.sharedInstance().getPrinterConfiguratorEntries();
		
		if (factoryEnties.size() <= 0)
			throw new IllegalStateException("No printerConfiguratorFactory was registered yet."); //$NON-NLS-1$
		
		if (factoryEnties.size() > 1) {
			configuratorCombo = new ConfiguratorCombo(this, SWT.READ_ONLY);
			configuratorCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			// TODO: Set default configurator for usecase
			configuratorCombo.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent arg0) {				
				}
				public void widgetSelected(SelectionEvent arg0) {
					selectedConfiguratorFactoryEntry = configuratorCombo.getSelectedElement();
					updateConfigurator();
				}
			});
		} else
			// Do not display the combo when only one entry
			selectedConfiguratorFactoryEntry = factoryEnties.iterator().next();
		
		configuratorWrapper = new XComposite(this, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
		
		updateConfigurator();
	}

	private void updateConfigurator() {
		if (selectedConfiguratorFactoryEntry != null) {
			
			if (configurator != null && (configuratorComposite != null && (!configuratorComposite.isDisposed())))
				printerConfiguration = configurator.readPrinterConfiguration();
			
			configuratorFactory = PrinterConfigurationRegistry.sharedInstance().getPrinterConfiguratorFactory(selectedConfiguratorFactoryEntry.getId());
			if (configuratorFactory == null)
				throw new IllegalStateException("Could not find configuratorFactory for registered printerConfiguration ?!?"); //$NON-NLS-1$
			configurator = configuratorFactory.createPrinterConfigurator();
			configurator.init(printerConfiguration);
		}
		
		if (configuratorComposite != null && (!configuratorComposite.isDisposed()))
			configuratorComposite.dispose();

		if (selectedConfiguratorFactoryEntry != null) {
			configuratorComposite = configurator.showComposite(configuratorWrapper);
			configuratorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		}
		
		this.layout(true, true);
	}

	/**
	 * Returns the {@link PrinterConfiguration} read from the current
	 * {@link PrinterConfigurator} in this dialog if possible and null
	 * if nothing else can be returned. 
	 * 
	 * @return The current {@link PrinterConfiguration} or null.
	 */
	public PrinterConfiguration getCurrentPrinterConfiguration() {
		if (configurator != null && configuratorComposite != null && !configuratorComposite.isDisposed())
			return configurator.readPrinterConfiguration();
		return null;
	}
	
	public String getPrinterUseCaseID() {
		return printerUseCaseID;
	}
}
