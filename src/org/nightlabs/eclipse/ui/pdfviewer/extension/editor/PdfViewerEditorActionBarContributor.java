package org.nightlabs.eclipse.ui.pdfviewer.extension.editor;

import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerActionRegistry;

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
