package org.nightlabs.eclipse.ui.pdfviewer;

public class PdfSimpleNavigator
{
	private PdfViewer pdfViewer;

	/**
	 * Get the <code>PdfSimpleNavigator</code> that is assigned to the given <code>pdfViewer</code>.
	 *
	 * @param pdfViewer the {@link PdfViewer} for which to get the <code>PdfSimpleNavigator</code>.
	 * @return the <code>PdfSimpleNavigator</code> or <code>null</code>, if none has been created for the given <code>pdfViewer</code>.
	 */
	public static PdfSimpleNavigator getPdfSimpleNavigator(PdfViewer pdfViewer)
	{
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		return (PdfSimpleNavigator) pdfViewer.getContextElement(PdfSimpleNavigator.class.getName());
	}

	public PdfSimpleNavigator(PdfViewer pdfViewer) {
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		this.pdfViewer = pdfViewer;
		pdfViewer.setContextElement(PdfSimpleNavigator.class.getName(), this);
	}

	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}
}
