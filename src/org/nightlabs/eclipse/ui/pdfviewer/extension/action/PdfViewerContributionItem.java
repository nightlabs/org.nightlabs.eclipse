package org.nightlabs.eclipse.ui.pdfviewer.extension.action;

import org.nightlabs.base.ui.action.XContributionItem;

public abstract class PdfViewerContributionItem
extends XContributionItem
implements IPdfViewerActionOrContributionItem
{
	private PdfViewerActionRegistry pdfViewerActionRegistry;

	@Override
	public void init(PdfViewerActionRegistry pdfViewerActionRegistry) {
		this.pdfViewerActionRegistry = pdfViewerActionRegistry;
	}

	@Override
	public PdfViewerActionRegistry getPdfViewerActionRegistry() {
		return pdfViewerActionRegistry;
	}

	@Override
	public void calculateEnabled() {
		setEnabled(pdfViewerActionRegistry.getPdfViewer().getPdfDocument() != null);
	}
}
