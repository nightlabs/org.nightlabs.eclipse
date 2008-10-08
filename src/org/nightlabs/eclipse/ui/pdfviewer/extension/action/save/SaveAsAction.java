package org.nightlabs.eclipse.ui.pdfviewer.extension.action.save;

import java.util.Collection;

import org.eclipse.swt.widgets.Event;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerAction;

import com.sun.pdfview.PDFFile;

/**
 * An action for saving the current PDF to a new file. Because the {@link PdfViewer} doesn't
 * have a reference to the raw data (only the {@link PDFFile} which contains the data well-structured,
 * after parsing), this action cannot save itself. It delegates work to an instance of
 * {@link SaveAsActionHandler}.
 * <p>
 * This action is <b>disabled</b>, if there is no {@link SaveAsActionHandler} registered in the
 * {@link PdfViewer}!
 * </p>
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public class SaveAsAction
extends PdfViewerAction
{
//	@Override
//	public void run() {
//		MessageDialog.openInformation(RCPUtil.getActiveShell(), "Save as", "This should be a dialog for saving the PDF to a local file.");
//	}

	@Override
	public void calculateEnabled() {
		PdfViewer pdfViewer = getPdfViewerActionRegistry().getPdfViewer();

		boolean enabled = pdfViewer.getPdfDocument() != null;
		if (enabled)
			enabled = !pdfViewer.getContextElements(SaveAsActionHandler.CONTEXT_ELEMENT_TYPE).isEmpty();

		setEnabled(enabled);
	}

	@Override
	public void runWithEvent(Event event) {
		PdfViewer pdfViewer = getPdfViewerActionRegistry().getPdfViewer();
		Collection<SaveAsActionHandler> contextElements = pdfViewer.getContextElements(SaveAsActionHandler.CONTEXT_ELEMENT_TYPE);
		if (contextElements.isEmpty()) {
			setEnabled(false);
			return;
		}
		SaveAsActionHandler saveAsActionHandler = contextElements.iterator().next();
		saveAsActionHandler.saveAs();
	}
}
