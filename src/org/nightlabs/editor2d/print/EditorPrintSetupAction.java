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

import org.apache.log4j.Logger;
import org.nightlabs.base.ui.print.PrinterInterfaceManager;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorPrintSetupAction 
extends AbstractEditorPrintAction
{
	public static final String ID = EditorPrintSetupAction.class.getName();
	
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(EditorPrintSetupAction.class);

	/**
	 * @param editor
	 * @param style
	 */
	public EditorPrintSetupAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public EditorPrintSetupAction(AbstractEditor editor) {
		super(editor);
	}

	protected void init() 
	{
		setId(ID);
		setText(Messages.getString("org.nightlabs.editor2d.print.EditorPrintSetupAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.print.EditorPrintSetupAction.tooltip")); //$NON-NLS-1$
	}

	public void run() 
	{
		PrinterInterfaceManager.sharedInstance().editPrinterConfiguration(
				PrintUtil.PRINTER_USE_CASE_EDITOR_2D, true);
//		PrinterJob printJob = PrinterJob.getPrinterJob();
////		PageFormat defaultPageFormat = printJob.defaultPage();
//		logger.debug("Page Setup before Modification");
//		PrintUtil.logPageFormat(getPageFormat());		
//		PageFormat pageFormat = printJob.pageDialog(getPageFormat());
//		logger.debug("Page Setup after Modification");
//		PrintUtil.logPageFormat(pageFormat);
//		setPageFormat(pageFormat);
	}
	
}
