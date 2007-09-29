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

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.print.DelegatingDocumentPrinterCfMod;
import org.nightlabs.print.DocumentPrinterDelegateConfig;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DocumentPrinterPreferencePage
extends PreferencePage
implements IWorkbenchPreferencePage {

	private XComposite wrapper; 
	
	private String editedFileExt = null;
	private EditDocumentPrinterTypeRegsComposite typeRegsComposite;
	private EditDocumentPrinterConfigComposite printerConfigComposite;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		typeRegsComposite = new EditDocumentPrinterTypeRegsComposite(wrapper, SWT.NONE);
		typeRegsComposite.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				if (editedFileExt != null) {
					if (typeRegsComposite.hasRegistration(editedFileExt))
						typeRegsComposite.setDelegateConfig(editedFileExt, printerConfigComposite.readDelegateConfig());
				}
				editedFileExt = typeRegsComposite.getSelectedFileExt();
				printerConfigComposite.setDelegateConfig(typeRegsComposite.getDelegateConfig(editedFileExt));
			}
		});
		printerConfigComposite = new EditDocumentPrinterConfigComposite(wrapper, SWT.NONE);
		
		typeRegsComposite.setDelegateConfigs(DelegatingDocumentPrinterCfMod.sharedInstance().getPrintConfigs());
		return wrapper;
	}

	@Override
	public boolean performOk() {
		if (editedFileExt != null)
			typeRegsComposite.setDelegateConfig(editedFileExt, printerConfigComposite.readDelegateConfig());
		Map<String, DocumentPrinterDelegateConfig> configs = typeRegsComposite.getDelegateConfigs();
		DelegatingDocumentPrinterCfMod cfMod = DelegatingDocumentPrinterCfMod.sharedInstance();
		cfMod.getPrintConfigs().clear();
		for (Entry<String, DocumentPrinterDelegateConfig> entry : configs.entrySet()) {
			cfMod.getPrintConfigs().put(entry.getKey(), entry.getValue());
		}
		cfMod.setPrintConfigs(cfMod.getPrintConfigs());
		try {
			Config.sharedInstance().save();
		} catch (ConfigException e) {
			ExceptionHandlerRegistry.asyncHandleException(e);
//			MessageDialog dlg = new MessageDialog(RCPUtil.getActiveWorkbenchShell(), "Saving DocumentPrinter config failed", null, e.getMessage(), 0, new String[]{"OK"}, 0);
//			dlg.open();
		}
		return super.performOk();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}
