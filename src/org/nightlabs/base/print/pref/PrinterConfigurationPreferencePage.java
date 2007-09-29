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

package org.nightlabs.base.print.pref;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;
import org.nightlabs.base.print.EditPrinterConfigurationComposite;
import org.nightlabs.base.print.PrinterUseCase;
import org.nightlabs.print.PrinterConfiguration;
import org.nightlabs.print.PrinterConfigurationCfMod;

/**
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterConfigurationPreferencePage 
extends PreferencePage
implements IWorkbenchPreferencePage 
{

	private XComposite wrapper;
	private PrinterUseCaseCombo useCaseCombo;
	private EditPrinterConfigurationComposite editPrinterConfigurationComposite;
	private Map<String, PrinterConfiguration> printerConfigurations = new HashMap<String, PrinterConfiguration>();
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {		
		wrapper = new XComposite(parent, SWT.NONE);
		useCaseCombo = new PrinterUseCaseCombo(wrapper, SWT.READ_ONLY);
		useCaseCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		useCaseCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				updatePrinterConfigurationComposite();
			}
		});
		Map<String, PrinterConfiguration> configs = PrinterConfigurationCfMod.getPrinterConfigurationCfMod().getPrinterConfigurations();
		for (Entry<String, PrinterConfiguration> entry : configs.entrySet()) {
			printerConfigurations.put(entry.getKey(), (PrinterConfiguration)entry.getValue().clone());
		}
		if (useCaseCombo.getElements().size() > 0) {
			useCaseCombo.selectElement( useCaseCombo.getElements().get(0) );
			updatePrinterConfigurationComposite();
		}
		return wrapper;
	}
	
	protected void updatePrinterConfigurationComposite() {
		applyConfigurationLocally();
		if (editPrinterConfigurationComposite != null && !editPrinterConfigurationComposite.isDisposed()) {
			editPrinterConfigurationComposite.dispose();
		}
		PrinterUseCase useCase = useCaseCombo.getSelectedElement();
		PrinterConfiguration printerConfiguration = printerConfigurations.get(useCase.getId());
		if (useCase != null)
			editPrinterConfigurationComposite = new EditPrinterConfigurationComposite(wrapper, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, useCase.getId(), printerConfiguration);
		else
			editPrinterConfigurationComposite = null;
		wrapper.layout(true, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	protected void applyConfigurationLocally() {
		if (editPrinterConfigurationComposite != null && !editPrinterConfigurationComposite.isDisposed()) {
			printerConfigurations.put(editPrinterConfigurationComposite.getPrinterUseCaseID(), editPrinterConfigurationComposite.getCurrentPrinterConfiguration());
		}
	}
	
	@Override
	public boolean performOk() {
		applyConfigurationLocally();
		for (Entry<String, PrinterConfiguration> entry : printerConfigurations.entrySet()) {
			PrinterConfigurationCfMod.setPrinterConfiguration(entry.getKey(), entry.getValue());
		}
		return super.performOk();
	}
}
