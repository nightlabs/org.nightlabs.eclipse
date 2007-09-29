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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.dialog.CenteredDialog;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.print.PrinterConfiguration;
import org.nightlabs.print.PrinterConfigurationCfMod;

/**
 * Dialog to edit the {@link PrinterConfiguration} of a certain
 * {@link PrinterUseCase}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EditPrinterConfigurationDialog extends CenteredDialog {

	private EditPrinterConfigurationComposite configurationComposite;
	private String printerUseCaseID;
	private boolean preSelectionDoStore;
	private Button useConfigOnlyForNextRun;
	private PrinterConfiguration printerConfiguration;
	
	
	/**
	 * @param parentShell
	 */
	public EditPrinterConfigurationDialog(Shell parentShell, String printerUseCaseID, boolean preSelectionDoStore) {
		super(parentShell);
		this.printerUseCaseID = printerUseCaseID;
		this.preSelectionDoStore = preSelectionDoStore;
		setShellStyle(getShellStyle() | SWT.RESIZE);				
	}
	
	protected void configureShell(Shell newShell) 
	{
		super.configureShell(newShell);		
		newShell.setText(Messages.getString("org.nightlabs.base.print.EditPrinterConfigurationDialog.title")); //$NON-NLS-1$
		newShell.setSize(400, 525);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		configurationComposite = new EditPrinterConfigurationComposite(parent, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER, printerUseCaseID);
		configurationComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		useConfigOnlyForNextRun = new Button(parent, SWT.CHECK);
		useConfigOnlyForNextRun.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		useConfigOnlyForNextRun.setSelection(!preSelectionDoStore);
		useConfigOnlyForNextRun.setText(Messages.getString("org.nightlabs.base.print.EditPrinterConfigurationDialog.useConfigOnlyForNextRun.text")); //$NON-NLS-1$
		return configurationComposite;
	}

	@Override
	protected void okPressed() {
		printerConfiguration = configurationComposite.getCurrentPrinterConfiguration();
		if (printerConfiguration != null && !useConfigOnlyForNextRun.getSelection())
			PrinterConfigurationCfMod.setPrinterConfiguration(printerUseCaseID, printerConfiguration);
		super.okPressed();
	}
	
	/**
	 * Opens a new {@link EditPrinterConfigurationDialog} for the given
	 * printerUseCaseID. It will return the edited printerConfiguration
	 * or null if the dialog was canceled.
	 * 
	 * @param printerUseCaseID The use case the configuration should be edited for.
	 * @param preSelectionDoStore A pre-selection for the option whether to store the edited configuration or use it only for the next run.
	 * @return The edited printerConfiguration
	 * or null if the dialog was canceled.
	 */
	public static PrinterConfiguration openDialog(String printerUseCaseID, boolean preSelectionDoStore) {
		EditPrinterConfigurationDialog dlg = new EditPrinterConfigurationDialog(
				RCPUtil.getActiveWorkbenchShell(),
				printerUseCaseID,
				preSelectionDoStore
			);
		if (dlg.open() == Dialog.OK)
			return dlg.printerConfiguration;
		return null;
	}
	
}
