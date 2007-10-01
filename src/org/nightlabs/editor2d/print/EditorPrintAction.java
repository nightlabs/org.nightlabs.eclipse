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
import java.awt.print.PrinterJob;

import org.apache.log4j.Logger;
import org.eclipse.ui.actions.ActionFactory;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.print.DrawComponentPrintable.PrintConstant;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.print.AWTPrinter;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorPrintAction
extends AbstractEditorPrintAction
{
	public static final String ID = ActionFactory.PRINT.getId();

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(EditorPrintAction.class);

	public EditorPrintAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	public EditorPrintAction(AbstractEditor editor) {
		super(editor);
	}

	/**
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#init()
	 */
	protected void init() 
	{
		setText(Messages.getString("org.nightlabs.editor2d.print.EditorPrintAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.print.EditorPrintAction.tooltip"));		 //$NON-NLS-1$
		setId(ID);
		setActionDefinitionId(ID);
	}

	public void run() 
	{
		AWTPrinter awtPrinter = getAWTPrinter();
		PrinterJob printJob = awtPrinter.getPrinterJob();
		printJob.setJobName(getEditor().getTitle());


		PageFormat pf = null;
		if (awtPrinter.getConfiguration() != null) {
			if (awtPrinter.getConfiguration().getPageFormat() != null)
				pf = printJob.defaultPage(awtPrinter.getConfiguration().getPageFormat());
			else
				pf = printJob.defaultPage();
			
			logger.debug("PageFormat in EditorPrintAction"); //$NON-NLS-1$
			PrintUtil.logPageFormat(pf);
		}

		if (pf != null)
			printJob.setPrintable(getPrintable(getPrintConstant()), pf);
		else
			printJob.setPrintable(getPrintable(getPrintConstant()));
		try {
			printJob.print();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PrintConstant getPrintConstant() {
		return PrintConstant.FIT_ALL;
	}
}
