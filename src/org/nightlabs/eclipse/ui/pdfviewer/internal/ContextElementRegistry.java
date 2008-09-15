package org.nightlabs.eclipse.ui.pdfviewer.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElement;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElementType;
import org.nightlabs.eclipse.ui.pdfviewer.PdfSimpleNavigator;

/**
 * Holds all {@link ContextElement}s for one {@link org.nightlabs.eclipse.ui.pdfviewer.PdfViewer} instance.
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public class ContextElementRegistry
{
	private Map<ContextElementType<?>, Map<String, ContextElement<?>>> contextElementType2id2contextElement = new HashMap<ContextElementType<?>, Map<String,ContextElement<?>>>();

	private static void assertValidThread()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!");
	}

	/**
	 * Assign a context-element. This method should be called by the context-element itself
	 * when it is created/assigned a <code>PdfViewer</code>.
	 *
	 * @param contextElementType the type of the <code>contextElement</code>. This should be the base class - e.g. when subclassing {@link PdfSimpleNavigator}, you should still pass <code>PdfSimpleNavigator.class</code> and not the subclass' type. Must <b>not</b> be <code>null</code>!
	 * @param id the identifier of the context-element. Can be <code>null</code>.
	 * @param contextElement the context-element. Can be <code>null</code> to remove a previous entry.
	 */
	public void setContextElement(ContextElementType<?>contextElementType, String id, ContextElement<?> contextElement)
	{
		assertValidThread();

		if (contextElementType == null)
			throw new IllegalArgumentException("contextElementType must not be null!");

		if (contextElement != null)
			contextElementType.assertValidContextElementImplementation(contextElement);

		Map<String, ContextElement<?>> id2contextElement = contextElementType2id2contextElement.get(contextElementType);
		if (id2contextElement == null && contextElement != null) {
			id2contextElement = new HashMap<String, ContextElement<?>>();
			contextElementType2id2contextElement.put(contextElementType, id2contextElement);
		}

		if (contextElement == null) {
			if (id2contextElement != null)
				id2contextElement.remove(id);
		}
		else
			id2contextElement.put(id, contextElement);

		if (id2contextElement != null && id2contextElement.isEmpty())
			contextElementType2id2contextElement.remove(contextElementType);

		allContextElementsCache = null;
	}

	/**
	 * Get a context-element that was registered before via {@link #setContextElement(ContextElementType, String, ContextElement)}
	 * or <code>null</code> if none is known for the given <code>contextElementType</code> and <code>id</code>.
	 *
	 * @param contextElementType the type of the <code>contextElement</code> as passed to {@link #setContextElement(ContextElementType, String, ContextElement)} before.
	 * @param id the identifier of the context-element as specified in {@link #setContextElement(ContextElementType, String, ContextElement)} - can be <code>null</code>.
	 * @return the appropriate context-element or <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public <T extends ContextElement<T>> T  getContextElement(ContextElementType<T> contextElementType, String id) {
		assertValidThread();

		if (contextElementType == null)
			throw new IllegalArgumentException("contextElementType must not be null!");

		Map<String, ContextElement<?>> id2contextElement = contextElementType2id2contextElement.get(contextElementType);
		if (id2contextElement == null)
			return null;

		return (T) id2contextElement.get(id);
	}

	/**
	 * @param contextElementType the type of the <code>contextElement</code> as passed to {@link #setContextElement(ContextElementType, String, ContextElement)} before.
	 * @return an immutable <code>Collection</code> containing the previously registered context-elements; never <code>null</code> (instead, an empty <code>Collection</code> is returned).
	 */
	@SuppressWarnings("unchecked")
	public <T extends ContextElement<T>> Collection<T> getContextElements(ContextElementType<T> contextElementType)
	{
		assertValidThread();

		if (contextElementType == null)
			throw new IllegalArgumentException("contextElementType must not be null!");

		Map<String, ContextElement<?>> id2contextElement = contextElementType2id2contextElement.get(contextElementType);
		if (id2contextElement == null)
			return Collections.emptySet();

		return (Collection<T>) Collections.unmodifiableCollection(id2contextElement.values());
	}

	private Collection<ContextElement<?>> allContextElementsCache = null;

	/**
	 * Get all {@link ContextElement}s that are registered.
	 *
	 * @return an immutable <code>Collection</code> containing all {@link ContextElement}s.
	 */
	public Collection<? extends ContextElement<?>> getContextElements()
	{
		assertValidThread();

		if (allContextElementsCache == null) {
			Set<ContextElement<?>> result = new HashSet<ContextElement<?>>();
			for (Map<String, ContextElement<?>> id2contextElement : contextElementType2id2contextElement.values()) {
				result.addAll(id2contextElement.values());
			}
			allContextElementsCache = Collections.unmodifiableCollection(result);
		}
		return allContextElementsCache;
	}
}
