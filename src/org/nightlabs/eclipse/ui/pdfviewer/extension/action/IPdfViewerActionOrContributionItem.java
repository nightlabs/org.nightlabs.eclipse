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
package org.nightlabs.eclipse.ui.pdfviewer.extension.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.nightlabs.base.ui.action.IXContributionItem;
import org.nightlabs.eclipse.ui.pdfviewer.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;

/**
 * Implement this interface in your {@link IAction}s and {@link IContributionItem}
 * (or {@link IXContributionItem}) to get access to the {@link PdfViewerActionRegistry}
 * (and thus the {@link PdfViewer}).
 *
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public interface IPdfViewerActionOrContributionItem
{
	/**
	 * Set the {@link PdfViewerActionRegistry}.
	 * @param pdfViewerActionRegistry the PdfViewerActionRegistry.
	 */
	void init(PdfViewerActionRegistry pdfViewerActionRegistry);

	/**
	 * Get the {@link PdfViewerActionRegistry}.
	 * @return the PdfViewerActionRegistry.
	 */
	PdfViewerActionRegistry getPdfViewerActionRegistry();

	/**
	 * Calculates if a property change concerning the property ENABLED has occurred.
	 * This decision depends on whether a {@link PdfDocument} instance is available.
	 */
	void calculateEnabled();
}
