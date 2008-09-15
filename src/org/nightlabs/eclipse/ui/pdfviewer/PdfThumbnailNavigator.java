package org.nightlabs.eclipse.ui.pdfviewer;

public class PdfThumbnailNavigator
{
	private PdfViewer pdfViewer;

	/**
	 * Get the <code>PdfThumbnailNavigator</code> that is assigned to the given <code>pdfViewer</code>.
	 *
	 * @param pdfViewer the {@link PdfViewer} for which to get the <code>PdfThumbnailNavigator</code>.
	 * @return the <code>PdfThumbnailNavigator</code> or <code>null</code>, if none has been created for the given <code>pdfViewer</code>.
	 */
	public static PdfThumbnailNavigator getPdfThumbnailNavigator(PdfViewer pdfViewer)
	{
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		return (PdfThumbnailNavigator) pdfViewer.getContextElement(PdfThumbnailNavigator.class.getName());
	}

	public PdfThumbnailNavigator(PdfViewer pdfViewer) {
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		this.pdfViewer = pdfViewer;
		pdfViewer.setContextElement(PdfThumbnailNavigator.class.getName(), this);
	}

	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}

}
