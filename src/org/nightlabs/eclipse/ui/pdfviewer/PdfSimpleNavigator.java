package org.nightlabs.eclipse.ui.pdfviewer;

import java.util.Collection;

public class PdfSimpleNavigator
implements ContextElement
{
	public static final ContextElementType CONTEXT_ELEMENT_TYPE = new ContextElementType(PdfSimpleNavigator.class);

	private PdfViewer pdfViewer;

	/**
	 * Get the <code>PdfSimpleNavigator</code> that is assigned to the given <code>pdfViewer</code>.
	 *
	 * @param pdfViewer the {@link PdfViewer} for which to get the <code>PdfSimpleNavigator</code>.
	 * @param id the identifier that was passed to {@link #PdfSimpleNavigator(PdfViewer, String)} (<code>null</code> allowed).
	 * @return the <code>PdfSimpleNavigator</code> or <code>null</code>, if none has been created for the given <code>pdfViewer</code>.
	 */
	public static PdfSimpleNavigator getPdfSimpleNavigator(PdfViewer pdfViewer, String id)
	{
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		return (PdfSimpleNavigator) pdfViewer.getContextElement(CONTEXT_ELEMENT_TYPE, id);
	}

	@SuppressWarnings("unchecked")
	public static Collection<? extends PdfSimpleNavigator> getPdfSimpleNavigators(PdfViewer pdfViewer)
	{
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		return (Collection<? extends PdfSimpleNavigator>) pdfViewer.getContextElements(CONTEXT_ELEMENT_TYPE);
	}

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
	 * @param id the identifier, if multiple instances shall be used, or <code>null</code>.
	 */
	public PdfSimpleNavigator(PdfViewer pdfViewer, String id) {
		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!");

		this.pdfViewer = pdfViewer;
		pdfViewer.setContextElement(CONTEXT_ELEMENT_TYPE, id, this);
	}

	@Override
	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}

	@Override
	public ContextElementType getContextElementType() {
		return CONTEXT_ELEMENT_TYPE;
	}
}
