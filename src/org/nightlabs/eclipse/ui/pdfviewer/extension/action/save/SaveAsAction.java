/* ********************************************************************
 * NightLabs PDF Viewer - http://www.nightlabs.org/projects/pdfviewer *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.eclipse.ui.pdfviewer.extension.action.save;

import java.util.Collection;

import org.eclipse.swt.widgets.Event;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerAction;

import com.sun.pdfview.PDFFile;

/**
 * An action for saving the current PDF to a new file. Because the {@link PdfViewer} doesn't
 * have a reference to the raw data (only the {@link PDFFile} which contains the data well-structured,
 * after parsing), this action cannot save itself. It delegates work to an instance of
 * {@link SaveAsActionHandler}.
 * <p>
 * This action is <b>disabled</b>, if there is no {@link SaveAsActionHandler} registered in the
 * {@link PdfViewer}!
 * </p>
 *
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class SaveAsAction
extends PdfViewerAction
{
//	@Override
//	public void run() {
//		MessageDialog.openInformation(RCPUtil.getActiveShell(), "Save as", "This should be a dialog for saving the PDF to a local file.");
//	}

	@Override
	public void calculateEnabled() {
		PdfViewer pdfViewer = getPdfViewerActionRegistry().getPdfViewer();

		boolean enabled = pdfViewer.getPdfDocument() != null;
		if (enabled)
			enabled = !pdfViewer.getContextElements(SaveAsActionHandler.CONTEXT_ELEMENT_TYPE).isEmpty();

		setEnabled(enabled);
	}

	@Override
	public void runWithEvent(Event event) {
		PdfViewer pdfViewer = getPdfViewerActionRegistry().getPdfViewer();
		Collection<SaveAsActionHandler> contextElements = pdfViewer.getContextElements(SaveAsActionHandler.CONTEXT_ELEMENT_TYPE);
		if (contextElements.isEmpty()) {
			setEnabled(false);
			return;
		}
		SaveAsActionHandler saveAsActionHandler = contextElements.iterator().next();
		saveAsActionHandler.saveAs();
	}
}
