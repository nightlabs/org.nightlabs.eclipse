package org.nightlabs.eclipse.ui.pdfviewer.extension.action;

public interface IPdfViewerActionOrContributionItem
{
	void init(PdfViewerActionRegistry pdfViewerActionRegistry);
	PdfViewerActionRegistry getPdfViewerActionRegistry();
}
