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
import org.nightlabs.eclipse.ui.pdfviewer.PDFViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PDFViewerActionRegistry;

/**
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class PDFViewerEditorActionBarContributor extends EditorActionBarContributor
{
	private PDFViewerEditor editor;

	public PDFViewerEditorActionBarContributor() {
	}

	@Override
	public void contributeToCoolBar(final ICoolBarManager coolBarManager) {
		contribute();
	}

	private ICoolBarManager getCoolBarManager()
	{
		final IActionBars2 actionBars = (IActionBars2) getActionBars();
		final ICoolBarManager coolBarManager = actionBars.getCoolBarManager();
		if (coolBarManager == null) {
			throw new IllegalStateException("coolBarManager is null!"); //$NON-NLS-1$
		}
		return coolBarManager;
	}

	protected void contribute()
	{
		final ICoolBarManager coolBarManager = getCoolBarManager();

		if (editor == null) {
			coolBarManager.removeAll();
			return;
		}

		final PDFViewer pdfViewer = editor.getPDFViewer();
		if (pdfViewer == null) {
			coolBarManager.removeAll();
			return;
		}

		final PDFViewerActionRegistry pdfViewerActionRegistry = getPDFViewerActionRegistry();

		if (pdfViewerActionRegistry == null) {
			coolBarManager.removeAll();
			return;
		}

		pdfViewerActionRegistry.contributeToCoolBar(coolBarManager);
	}

	private PDFViewerActionRegistry getPDFViewerActionRegistry()
	{
		if (editor == null) {
			return null;
		}

		final PDFViewer pdfViewer = editor.getPDFViewer();
		if (pdfViewer == null) {
			return null;
		}

		return pdfViewer.getContextElement(
				PDFViewerActionRegistry.CONTEXT_ELEMENT_TYPE,
				PDFViewerEditorActionBarContributor.class.getName()
		);
	}

	public PDFViewerEditor getActiveEditor() {
		return editor;
	}

	@Override
	public void setActiveEditor(final IEditorPart targetEditor) {
		PDFViewerActionRegistry pdfViewerActionRegistry = getPDFViewerActionRegistry();

		if (pdfViewerActionRegistry != null) {
			final PDFViewer pdfViewer = pdfViewerActionRegistry.getPDFViewer();
//			IActionBars2 actionBars = (IActionBars2) getActionBars();
//
//			ICoolBarManager coolBarManager = actionBars.getCoolBarManager();
//
//			if (coolBarManager == null)
//				throw new IllegalStateException("coolBarManager is null!"); //$NON-NLS-1$
//
//			pdfViewerActionRegistry.removeAllFromCoolBar(coolBarManager);
			pdfViewer.removePropertyChangeListener(PDFViewer.PROPERTY_REGISTER_CONTEXT_ELEMENT, propertyChangeListenerContribute);

			pdfViewerActionRegistry.removeAllFromCoolBar(getCoolBarManager());
			this.editor = null;
			contribute();
		}

		this.editor = (PDFViewerEditor) targetEditor;
		if (this.editor != null) {
			this.editor.setPdfViewerEditorActionBarContributor(this);
		}

		pdfViewerActionRegistry = getPDFViewerActionRegistry();
		if (pdfViewerActionRegistry != null) {
			final PDFViewer pdfViewer = pdfViewerActionRegistry.getPDFViewer();
			pdfViewer.addPropertyChangeListener(PDFViewer.PROPERTY_REGISTER_CONTEXT_ELEMENT, propertyChangeListenerContribute);
		}

		contribute();
	}

	private PropertyChangeListener propertyChangeListenerContribute = new PropertyChangeListener()
	{
		public void propertyChange(final java.beans.PropertyChangeEvent evt) {
			contribute();
		}
	};
}
