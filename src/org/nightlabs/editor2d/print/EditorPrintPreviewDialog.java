/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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
package org.nightlabs.editor2d.print;

import java.awt.print.PageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.print.PrinterConfigurationRegistry;
import org.nightlabs.base.ui.print.PrinterUseCase;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.print.PrinterConfiguration;
import org.nightlabs.print.PrinterConfigurationCfMod;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorPrintPreviewDialog 
extends CenteredDialog 
{
	private PrinterUseCase printerUseCase;
	private PrinterConfiguration printerConfiguration;
	
	public static final String PRINTER_USECASE_EDITOR2D = "PrinterUseCase-Editor2D"; //$NON-NLS-1$
			
	/**
	 * @param parentShell
	 */
	public EditorPrintPreviewDialog(DrawComponent dc, Shell parentShell) 
	{
		super(parentShell);
		if (dc == null)
			throw new IllegalArgumentException("Param dc must not be null!"); //$NON-NLS-1$
		
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.dc = dc;		
		printerUseCase = PrinterConfigurationRegistry.sharedInstance().getPrinterUseCase(PRINTER_USECASE_EDITOR2D);
		if (printerUseCase == null)
			throw new RuntimeException("The PrinterUseCase to be edited is not registered: "+PRINTER_USECASE_EDITOR2D); //$NON-NLS-1$
		printerConfiguration = PrinterConfigurationCfMod.getPrinterConfiguration(PRINTER_USECASE_EDITOR2D);		
	}

	private DrawComponent dc = null;
	private EditorPrinterConfiguratorComposite printConfiguratorComp = null;
	@Override
	protected Control createDialogArea(Composite parent) 
	{
		printConfiguratorComp = new EditorPrinterConfiguratorComposite(dc, parent, SWT.NONE);
		printConfiguratorComp.init(printerConfiguration);
		return printConfiguratorComp;
	}
	
	public PageFormat getPageFormat() {
		return printConfiguratorComp.getPageFormat();
	}
}
