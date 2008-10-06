package org.nightlabs.eclipse.ui.pdfviewer.extension.editor;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

public class PdfViewerEditorActionBarContributor extends EditorActionBarContributor
{
	private PdfViewerEditor editor;

	public PdfViewerEditorActionBarContributor() {
	}

	@Override
	public void contributeToCoolBar(ICoolBarManager coolBarManager) {
		if (editor == null) {
			coolBarManager.removeAll();
			return;
		}

// TODO implement this (with the correct id! not null!)
//		PdfViewerActionRegistry pdfViewerActionRegistry = editor.getPdfViewer().getContextElement(PdfViewerActionRegistry.CONTEXT_ELEMENT_TYPE, null);
//		pdfViewerActionRegistry.contributeToCoolBar(coolBarManager);
	}

	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		this.editor = (PdfViewerEditor) targetEditor;
	}
}
