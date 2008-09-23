package org.nightlabs.eclipse.ui.pdfviewer;

/**
 * A context-element is a UI element that interacts with a {@link PdfViewer}.
 * <p>
 * When implementing a <code>ContextElement</code>, you should register in the {@link PdfViewer}
 * via {@link PdfViewer#registerContextElement(ContextElement)} as soon
 * as you know the <code>PdfViewer</code> (usually in the constructor of your implementation).
 * </p>
 * <p>
 * Additionally, you <b>must</b> declare a <code>public static final</code> constant named
 * <code>CONTEXT_ELEMENT_TYPE</code>, if you don't subclass another <code>ContextElement</code> (that already
 * defines this constant). However, if you subclass one and your new implementation is doing something completely
 * different, you are encouraged to redeclare this <code>CONTEXT_ELEMENT_TYPE</code> (with your new class)
 * in order to indicate that it has nothing in common with your superclass.
 * </p>
 * <p>
 * You can obtain a certain <code>ContextElement</code> for a PdfViewer by
 * {@link PdfViewer#getContextElement(ContextElementType, String)} or all of one type by
 * {@link PdfViewer#getContextElements(ContextElementType)}.
 * </p>
 *
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public interface ContextElement<T extends ContextElement<T>> {
	ContextElementType<T> getContextElementType();
	String getContextElementId();

	/**
	 * Get the {@link PdfViewer}.
	 *
	 * @return the {@link PdfViewer} which is assigned to this <code>ContextElement</code>.
	 */
	PdfViewer getPdfViewer();

	/**
	 * Call-back method that is triggered when a {@link ContextElement} is unregistered from its {@link PdfViewer}.
	 * In your implementation of <code>ContextElement</code> you should perform clean-up operations, for example
	 * remove all listeners that you registered in the <code>PdfViewer</code> before.
	 */
	void onUnregisterContextElement();
}
