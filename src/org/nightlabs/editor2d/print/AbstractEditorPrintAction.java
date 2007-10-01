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

import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.nightlabs.base.ui.print.PrinterInterfaceManager;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.actions.AbstractEditorAction;
import org.nightlabs.editor2d.print.DrawComponentPrintable.PrintConstant;
import org.nightlabs.print.AWTPrinter;
import org.nightlabs.print.PrinterInterface;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public abstract class AbstractEditorPrintAction 
extends AbstractEditorAction 
{	
	/**
	 * @param editor
	 * @param style
	 */
	public AbstractEditorPrintAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public AbstractEditorPrintAction(AbstractEditor editor) {
		super(editor);
	}

	/**
	 * returns true if there are printers available otherwise returns false
	 * 
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() 
	{
		// Changed to Swing way of getting a printer list, 
		// as this system is acutally used for printing.
		PrintService[] pServices = PrintServiceLookup.lookupPrintServices(null, null);
		return pServices != null && pServices.length > 0;		
	}

	protected AWTPrinter getAWTPrinter() 
	{
		PrinterInterface printer;
		try {
			printer = PrinterInterfaceManager.sharedInstance().getConfiguredPrinterInterface(
					PrinterInterfaceManager.INTERFACE_FACTORY_AWT,
					PrintUtil.PRINTER_USE_CASE_EDITOR_2D 
				);
		} catch (PrinterException e) {
			throw new RuntimeException(e);
		}
		return (AWTPrinter) printer;
	}
		
	public Printable getPrintable(PrintConstant printConstant) {
		return new EditorPrintable(getDrawComponent(), printConstant);		
	}
	
	public DrawComponent getDrawComponent() {
		return getRootDrawComponent();
	}
		
}
