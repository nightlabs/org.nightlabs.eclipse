package org.nightlabs.eclipse.ui.pdfviewer;

/**
 * A context-element is an UI element that interacts with a {@link PdfViewer}.
 * <p>
 * When implementing a <code>ContextElement</code>, you should register in the {@link PdfViewer}
 * via {@link PdfViewer#setContextElement(ContextElementType, String, ContextElement)} as soon
 * as you know the <code>PdfViewer</code> (usually in the constructor of your implementation).
 * </p>
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public interface ContextElement<T extends ContextElement<T>> {
	ContextElementType<T> getContextElementType();
	String getContextElementId();
	PdfViewer getPdfViewer();
}
