package org.nightlabs.eclipse.ui.pdfviewer;

/**
 * A context-element is an UI element that interacts with a {@link PdfViewer}.
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public interface ContextElement<T extends ContextElement<T>> {
	ContextElementType<T> getContextElementType();
	PdfViewer getPdfViewer();
}
