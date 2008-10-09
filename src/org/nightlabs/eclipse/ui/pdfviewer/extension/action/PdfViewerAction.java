package org.nightlabs.eclipse.ui.pdfviewer.extension.action;

import org.eclipse.jface.action.Action;

/**
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public abstract class PdfViewerAction extends Action implements IPdfViewerActionOrContributionItem
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

	@Override
	public void calculateEnabled() {
		setEnabled(pdfViewerActionRegistry.getPdfViewer().getPdfDocument() != null);
	}
}
