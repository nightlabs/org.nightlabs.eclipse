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
package org.nightlabs.eclipse.ui.pdfviewer.extension.editor;

import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerActionRegistry;

/**
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfViewerEditorActionBarContributor extends EditorActionBarContributor
{
	private PdfViewerEditor editor;

	public PdfViewerEditorActionBarContributor() {
	}

	@Override
	public void contributeToCoolBar(ICoolBarManager coolBarManager) {
		contribute();
	}

	private ICoolBarManager getCoolBarManager()
	{
		IActionBars2 actionBars = (IActionBars2) getActionBars();
		ICoolBarManager coolBarManager = actionBars.getCoolBarManager();
		if (coolBarManager == null)
			throw new IllegalStateException("coolBarManager is null!"); //$NON-NLS-1$
		return coolBarManager;
	}

	protected void contribute()
	{
		ICoolBarManager coolBarManager = getCoolBarManager();

		if (editor == null) {
			coolBarManager.removeAll();
			return;
		}

		PdfViewer pdfViewer = editor.getPdfViewer();
		if (pdfViewer == null) {
			coolBarManager.removeAll();
			return;
		}

		PdfViewerActionRegistry pdfViewerActionRegistry = getPdfViewerActionRegistry();

		if (pdfViewerActionRegistry == null) {
			coolBarManager.removeAll();
			return;
		}

		pdfViewerActionRegistry.contributeToCoolBar(coolBarManager);
	}

	private PdfViewerActionRegistry getPdfViewerActionRegistry()
	{
		if (editor == null)
			return null;

		PdfViewer pdfViewer = editor.getPdfViewer();
		if (pdfViewer == null)
			return null;

		return pdfViewer.getContextElement(
				PdfViewerActionRegistry.CONTEXT_ELEMENT_TYPE,
				PdfViewerEditorActionBarContributor.class.getName()
		);
	}

	public PdfViewerEditor getActiveEditor() {
		return editor;
	}

	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		PdfViewerActionRegistry pdfViewerActionRegistry = getPdfViewerActionRegistry();

		if (pdfViewerActionRegistry != null) {
			PdfViewer pdfViewer = pdfViewerActionRegistry.getPdfViewer();
//			IActionBars2 actionBars = (IActionBars2) getActionBars();
//
//			ICoolBarManager coolBarManager = actionBars.getCoolBarManager();
//
//			if (coolBarManager == null)
//				throw new IllegalStateException("coolBarManager is null!"); //$NON-NLS-1$
//
//			pdfViewerActionRegistry.removeAllFromCoolBar(coolBarManager);
			pdfViewer.removePropertyChangeListener(PdfViewer.PROPERTY_REGISTER_CONTEXT_ELEMENT, propertyChangeListenerContribute);

			pdfViewerActionRegistry.removeAllFromCoolBar(getCoolBarManager());
			this.editor = null;
			contribute();
		}

		this.editor = (PdfViewerEditor) targetEditor;
		if (this.editor != null) {
			this.editor.setPdfViewerEditorActionBarContributor(this);
		}

		pdfViewerActionRegistry = getPdfViewerActionRegistry();
		if (pdfViewerActionRegistry != null) {
			PdfViewer pdfViewer = pdfViewerActionRegistry.getPdfViewer();
			pdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_REGISTER_CONTEXT_ELEMENT, propertyChangeListenerContribute);
		}

		contribute();
	}

	private PropertyChangeListener propertyChangeListenerContribute = new PropertyChangeListener()
	{
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			contribute();
		}
	};
}
