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

import org.eclipse.jface.dialogs.Dialog;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorPrintPreviewAction  
extends AbstractEditorPrintAction
{
	public static final String ID = EditorPrintPreviewAction.class.getName();
	
	/**
	 * @param editor
	 * @param style
	 */
	public EditorPrintPreviewAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public EditorPrintPreviewAction(AbstractEditor editor) {
		super(editor);
	}

	protected void init() 
	{
		setId(ID);
		setText(Messages.getString("org.nightlabs.editor2d.print.EditorPrintPreviewAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.print.EditorPrintPreviewAction.tooltip")); //$NON-NLS-1$
	}
			
	public void run() 
	{
//		AWTPrinter awtPrinter = getAWTPrinter();
//		PageFormat pf = null;
//		if (awtPrinter.getConfiguration() != null && awtPrinter.getConfiguration().getPageFormat() != null)
//			pf = awtPrinter.getConfiguration().getPageFormat();
//		if (pf == null)
//			pf = PrinterJob.getPrinterJob().defaultPage();
//		J2DPrintDialog printDialog = new J2DPrintDialog(getShell(), getDrawComponent(), pf);
		
		EditorPrintPreviewDialog printDialog = new EditorPrintPreviewDialog(getDrawComponent(), getShell());		
		if (printDialog.open() == Dialog.CANCEL)
			return;
		
//		awtPrinter.getPrinterJob().setPrintable(
//				getPrintable(PrintConstant.FIT_PAGE), 
//				printDialog.getPageFormat()
//			);			
//		try {
//			awtPrinter.getPrinterJob().print();				
//		} catch (PrinterException pe) {
//			throw new RuntimeException(pe);
//		}			
	}
}
