package org.nightlabs.eclipse.ui.pdfviewer;


public class PdfSimpleNavigator
implements ContextElement<PdfSimpleNavigator>
{
	public static final ContextElementType<PdfSimpleNavigator> CONTEXT_ELEMENT_TYPE = new ContextElementType<PdfSimpleNavigator>(PdfSimpleNavigator.class);

	private PdfViewer pdfViewer;
	private String contextElementId;

	/**
	 * Create a <code>PdfSimpleNavigator</code>. This constructor delegates to {@link #PdfSimpleNavigator(PdfViewer, String)}
	 * with <code>id = null</code>.
	 * @param pdfViewer the {@link PdfViewer} for which to create a <code>PdfSimpleNavigator</code>.
	 */
	public PdfSimpleNavigator(PdfViewer pdfViewer) {
		this(pdfViewer, null);
	}

	/**
	 * Create a <code>PdfSimpleNavigator</code>.
	 *
	 * @param pdfViewer the {@link PdfViewer} for which to create a <code>PdfSimpleNavigator</code>.
	 * @param contextElementId the identifier, if multiple instances shall be used, or <code>null</code>.
	 */
	public PdfSimpleNavigator(PdfViewer pdfViewer, String contextElementId) {
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		this.pdfViewer = pdfViewer;
		this.contextElementId = contextElementId;
		pdfViewer.registerContextElement(this);
	}

	@Override
	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}

	@Override
	public ContextElementType<PdfSimpleNavigator> getContextElementType() {
		return CONTEXT_ELEMENT_TYPE;
	}

	@Override
	public String getContextElementId() {
		return contextElementId;
	}
}
