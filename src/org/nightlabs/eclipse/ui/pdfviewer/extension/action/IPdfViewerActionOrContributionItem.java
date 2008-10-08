package org.nightlabs.eclipse.ui.pdfviewer.extension.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.nightlabs.base.ui.action.IXContributionItem;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;

/**
 * Implement this interface in your {@link IAction}s and {@link IContributionItem}
 * (or {@link IXContributionItem}) to get access to the {@link PdfViewerActionRegistry}
 * (and thus the {@link PdfViewer}).
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public interface IPdfViewerActionOrContributionItem
{
	void init(PdfViewerActionRegistry pdfViewerActionRegistry);
	PdfViewerActionRegistry getPdfViewerActionRegistry();
	void calculateEnabled();
}
