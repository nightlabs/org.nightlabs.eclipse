package org.nightlabs.eclipse.ui.pdfviewer.extension.action;

import org.eclipse.jface.action.Action;

public class PdfViewerAction extends Action implements IPdfViewerActionOrContributionItem
{
	PdfViewerActionRegistry pdfViewerActionRegistry;

	@Override
	public void init(PdfViewerActionRegistry pdfViewerActionRegistry) {
		this.pdfViewerActionRegistry = pdfViewerActionRegistry;
	}

	@Override
	public PdfViewerActionRegistry getPdfViewerActionRegistry() {
		return pdfViewerActionRegistry;
	}
}
