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

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.PrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintRequestAttributeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.print.PrintUtil;
import org.nightlabs.print.PrinterConfiguration;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterConfiguratorComposite extends XComposite {

	private Button alwaysAsk;
	
	private Group printerGroup;
//	private Label printerName;
	private Button useSysDefaultPrinter;
	private PrintServiceCombo printServiceCombo;
	private Button selectPrinterButton;
	
	private Group pageFormatGroup;
	private Label pageFormatDescription;
	private Button editPageFormat;
	
	private PageFormat pageFormat;	
	private PrinterJob printerJob;
	private PrintRequestAttributeSet printRequestAttributeSet;
	
	/**
	 * @param parent
	 * @param style
	 */
	public PrinterConfiguratorComposite(Composite parent, int style) {
		super(parent, style);
		initGUI(parent);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 */
	public PrinterConfiguratorComposite(Composite parent, int style,
			LayoutMode layoutMode) {
		super(parent, style, layoutMode);
		initGUI(parent);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutDataMode
	 */
	public PrinterConfiguratorComposite(Composite parent, int style,
			LayoutDataMode layoutDataMode) {
		super(parent, style, layoutDataMode);
		initGUI(parent);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public PrinterConfiguratorComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		initGUI(parent);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param cols
	 */
	public PrinterConfiguratorComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, int cols) {
		super(parent, style, layoutMode, layoutDataMode, cols);
		initGUI(parent);
	}

	protected void initGUI(Composite parent) 
	{
		alwaysAsk = new Button(this, SWT.CHECK);
		alwaysAsk.setText(Messages.getString("org.nightlabs.base.print.PrinterConfiguratorComposite.alwaysAsk.text")); //$NON-NLS-1$
		printerGroup = new Group(this, SWT.NONE);
		printerGroup.setText(Messages.getString("org.nightlabs.base.print.PrinterConfiguratorComposite.printerGroup.text")); //$NON-NLS-1$
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		printerGroup.setLayout(gl);
		printerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		useSysDefaultPrinter = new Button(printerGroup, SWT.CHECK);
		useSysDefaultPrinter.setText(Messages.getString("org.nightlabs.base.print.PrinterConfiguratorComposite.useSysDefaultPrinter.text")); //$NON-NLS-1$
		useSysDefaultPrinter.addSelectionListener(useSysDefaultListener);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		useSysDefaultPrinter.setLayoutData(gd);
		printServiceCombo = new PrintServiceCombo(printerGroup, SWT.WRAP);
		printServiceCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectPrinterButton = new Button(printerGroup, SWT.PUSH);
		selectPrinterButton.setText("..."); //$NON-NLS-1$
		selectPrinterButton.addSelectionListener(selectPrinterListener);
		
		pageFormatGroup = new Group(this, SWT.NONE);
		pageFormatGroup.setText(Messages.getString("org.nightlabs.base.print.PrinterConfiguratorComposite.pageFormatGroup.text")); //$NON-NLS-1$
		GridLayout gl1 = new GridLayout();
		gl1.numColumns = 2;
		pageFormatGroup.setLayout(gl1);
		pageFormatGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		pageFormatDescription = new Label(pageFormatGroup, SWT.WRAP);
		pageFormatDescription.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editPageFormat = new Button(pageFormatGroup, SWT.PUSH);		
		editPageFormat.setText("..."); //$NON-NLS-1$
		editPageFormat.addSelectionListener(pageFormatListener);
				
		addDisposeListener(disposeListener);
	}
	
	private SelectionListener useSysDefaultListener = new SelectionListener() 
	{
		public void widgetDefaultSelected(SelectionEvent arg0) {
		}
		public void widgetSelected(SelectionEvent arg0) {
			updateEnabled();
		}
	};
	
	private SelectionListener selectPrinterListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent arg0) {
		}
		public void widgetSelected(SelectionEvent arg0) {
			PrinterJob printerJob = getPrinterJob();
			PrintService printService = printServiceCombo.getSelectedElement();
			if (printService != null)
				try {
					printerJob.setPrintService(printService);
				} catch (PrinterException e) {
					throw new RuntimeException(e);
				}
//			if (printerJob.printDialog(printRequestAttributeSet)) {
			if (printerJob.printDialog()) {
				printServiceCombo.selectElement(printerJob.getPrintService());
				// TODO: This does not work at all. Somehow the job get the attributes assigned, but 
				// I can't access them
				printRequestAttributeSet = convertPrintRequestAttributeSet(
						printerJob.getPrintService().createPrintJob().getAttributes()
					);
				updateForPrintRequestAttributes(printRequestAttributeSet);
			}
		}
	};
	
	private SelectionListener pageFormatListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent arg0) {
		}
		public void widgetSelected(SelectionEvent arg0) {
			PrinterJob printerJob = getPrinterJob();			
			PageFormat newPageFormat;
			if (PrinterConfiguratorComposite.this.pageFormat != null)
				newPageFormat = printerJob.pageDialog(PrinterConfiguratorComposite.this.pageFormat);
			else
				newPageFormat = printerJob.pageDialog(printerJob.defaultPage());
			PrinterConfiguratorComposite.this.pageFormat = newPageFormat;
//			printerJob.g
			pageFormatDescription.setText(getPageFormatDescription(PrinterConfiguratorComposite.this.pageFormat));
			if (pageSetupComposite != null)
				pageSetupComposite.refresh(PrinterConfiguratorComposite.this.pageFormat);
		}
	};
	
	private DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) {
			editPageFormat.removeSelectionListener(pageFormatListener);
			selectPrinterButton.removeSelectionListener(selectPrinterListener);
			useSysDefaultPrinter.removeSelectionListener(useSysDefaultListener);
		}	
	};	
		
	public void init(PrinterConfiguration printerConfiguration) 
	{
		if (printerConfiguration != null)
			alwaysAsk.setSelection(printerConfiguration.isAlwaysAsk());
		else
			alwaysAsk.setSelection(false);
		
		if (printerConfiguration != null && printerConfiguration.getPrintServiceName() != null) {
			PrintService printService = PrintUtil.lookupPrintService(printerConfiguration.getPrintServiceName());
			if (printService != null)
				printServiceCombo.selectElement(printService);
			useSysDefaultPrinter.setSelection(false);
		}
		else
			useSysDefaultPrinter.setSelection(true);
			
//		else
//			printerName.setText(NLBasePlugin.getResourceString("dialog.printerConfiguration.default.noPrinterAssigned"));

		if (printerConfiguration != null) {
			this.pageFormat = printerConfiguration.getPageFormat();
			pageFormatDescription.setText(getPageFormatDescription(printerConfiguration.getPageFormat()));			
		}
		if (printerConfiguration == null || printerConfiguration.getPageFormat() == null)
			pageFormatDescription.setText(Messages.getString("org.nightlabs.base.print.PrinterConfiguratorComposite.pageFormatDescription_noPageFormatAssigned")); //$NON-NLS-1$

		if (pageFormat != null)
			pageSetupComposite = initPageSetupComposite(this);
		
		if (printerConfiguration != null && printerConfiguration.getPrintRequestAttributeSet() != null)
			printRequestAttributeSet = convertPrintRequestAttributeSet(printerConfiguration.getPrintRequestAttributeSet());
		else
			printRequestAttributeSet = new HashPrintRequestAttributeSet();
		
		updateEnabled();
	}
	
	private PageSetupComposite pageSetupComposite = null; 
	protected PageSetupComposite initPageSetupComposite(Composite parent) {
		return new PageSetupComposite(pageFormat, parent, SWT.NONE);
	}
	
	public PrinterConfiguration readPrinterConfiguration() {
		PrinterConfiguration configuration = new PrinterConfiguration();
		configuration.setAlwaysAsk(alwaysAsk.getSelection());
		if (!useSysDefaultPrinter.getSelection() && printServiceCombo.getSelectedElement() != null)
			configuration.setPrintServiceName(printServiceCombo.getSelectedElement().getName());
		configuration.setPageFormat(pageFormat);
		return configuration;
	}
	
	private PrinterJob getPrinterJob() {
		if (printerJob == null)
			printerJob = PrinterJob.getPrinterJob();
		return printerJob;
	}
	
	private String getPageFormatDescription(PageFormat pageFormat) {
		if (pageFormat == null)
			return Messages.getString("org.nightlabs.base.print.PrinterConfiguratorComposite.pageFormatDescription_noPageFormatAssigned"); //$NON-NLS-1$
		else
			return Messages.getString("org.nightlabs.base.print.PrinterConfiguratorComposite.pageFormatDescription_customPageFormat"); //$NON-NLS-1$
	}
	
	public PageFormat getPageFormat() {
		return pageFormat;
	}
	
	protected void updateForPrintRequestAttributes(PrintRequestAttributeSet printRequestAttributeSet) {
		// TODO: Here we could update UI for specific PrintRequestAttributes: e.g. Copies, NumberUp, OrientationRequested, PageRanges
	}
	
	private PrintRequestAttributeSet convertPrintRequestAttributeSet(AttributeSet attributeSet) {
		PrintRequestAttributeSet result = new HashPrintRequestAttributeSet();
		Attribute[] attributes = attributeSet.toArray();
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i] instanceof PrintRequestAttribute)
				result.add(attributes[i]);
		}
		return result;
	}
	
	protected void updateEnabled() {
		printServiceCombo.setEnabled(!useSysDefaultPrinter.getSelection());
		selectPrinterButton.setEnabled(!useSysDefaultPrinter.getSelection());
	}
	
}
