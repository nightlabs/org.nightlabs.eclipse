package org.nightlabs.eclipse.ui.pdfviewer.extension.action.save;

import org.nightlabs.eclipse.ui.pdfviewer.ContextElement;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElementType;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;

/**
 * Handler for saving a file to the local file system. It interacts with the
 * {@link SaveAsAction}. You can register one instance of this class for each {@link PdfViewer}
 * only!
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public abstract class SaveAsActionHandler implements ContextElement<SaveAsActionHandler>
{
	public static final ContextElementType<SaveAsActionHandler> CONTEXT_ELEMENT_TYPE = new ContextElementType<SaveAsActionHandler>(SaveAsActionHandler.class);

	private PdfViewer pdfViewer;

	public SaveAsActionHandler(PdfViewer pdfViewer) {
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		this.pdfViewer = pdfViewer;
		pdfViewer.registerContextElement(this);
	}

	@Override
	public void onUnregisterContextElement() {
	}

	@Override
	public String getContextElementId() {
		return null;
	}

	@Override
	public ContextElementType<SaveAsActionHandler> getContextElementType() {
		return CONTEXT_ELEMENT_TYPE;
	}

	@Override
	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}

	/**
	 * Implement this method to do the actual saving. This usually means opening a save-file-dialog
	 * and writing your source data to the destination data.
	 */
	public abstract void saveAs();
}
