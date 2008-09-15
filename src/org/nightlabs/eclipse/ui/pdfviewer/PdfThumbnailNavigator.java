package org.nightlabs.eclipse.ui.pdfviewer;

import java.util.Collection;

public class PdfThumbnailNavigator
{
	public static final ContextElementType CONTEXT_ELEMENT_TYPE = new ContextElementType(PdfThumbnailNavigator.class);

	private PdfViewer pdfViewer;

	/**
	 * Get the <code>PdfThumbnailNavigator</code> that is assigned to the given <code>pdfViewer</code>.
	 *
	 * @param pdfViewer the {@link PdfViewer} for which to get the <code>PdfThumbnailNavigator</code>.
	 * @return the <code>PdfThumbnailNavigator</code> or <code>null</code>, if none has been created for the given <code>pdfViewer</code>.
	 */
	public static PdfThumbnailNavigator getPdfThumbnailNavigator(PdfViewer pdfViewer, String id)
	{
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		return (PdfThumbnailNavigator) pdfViewer.getContextElement(CONTEXT_ELEMENT_TYPE, id);
	}

	@SuppressWarnings("unchecked")
	public static Collection<? extends PdfThumbnailNavigator> getPdfThumbnailNavigators(PdfViewer pdfViewer)
	{
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		return (Collection<? extends PdfThumbnailNavigator>) pdfViewer.getContextElements(CONTEXT_ELEMENT_TYPE);
	}

	public PdfThumbnailNavigator(PdfViewer pdfViewer) {
		this(pdfViewer, null);
	}

	public PdfThumbnailNavigator(PdfViewer pdfViewer, String id) {
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		this.pdfViewer = pdfViewer;
		pdfViewer.setContextElement(CONTEXT_ELEMENT_TYPE, id, this);
	}

	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}

}
