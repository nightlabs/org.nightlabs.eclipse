/* ********************************************************************
 * NightLabs PDF Viewer - http://www.nightlabs.org/projects/pdfviewer *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
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
import org.nightlabs.eclipse.ui.pdfviewer.PDFSimpleNavigator;

/**
 * Holds all {@link ContextElement}s for one {@link org.nightlabs.eclipse.ui.pdfviewer.PDFViewer} instance.
 *
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class ContextElementRegistry
{
	private Map<ContextElementType<?>, Map<String, ContextElement<?>>> contextElementType2id2contextElement = new HashMap<ContextElementType<?>, Map<String,ContextElement<?>>>();
	private Map<ContextElementType<?>, Map<String, ContextElement<?>>> contextElementType2id2contextElementCache = new HashMap<ContextElementType<?>, Map<String,ContextElement<?>>>();

	/**
	 * Checks if a given method is called on the SWT UI thread.
	 */
	private static void assertValidThread()
	{
		if (Display.getCurrent() == null) {
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!"); //$NON-NLS-1$
		}
	}

	/**
	 * Assign a context-element.
	 *
	 * @param contextElement the context-element. Must not be <code>null</code>.
	 */
	public void registerContextElement(final ContextElement<?> contextElement)
	{
		setContextElement(contextElement.getContextElementType(), contextElement.getContextElementId(), contextElement);
	}

	/**
	 * Remove a context-element's registration.
	 *
	 * @param contextElementType the type of the <code>contextElement</code> as specified by {@link ContextElement#getContextElementType()} when it was added.
	 * @param contextElementId the identifier or <code>null</code> as specified by {@link ContextElement#getContextElementId()} when it was added.
	 */
	public void unregisterContextElement(final ContextElementType<?> contextElementType, final String contextElementId)
	{
		setContextElement(contextElementType, contextElementId, null);
	}

	/**
	 * Add or remove a context-element from this registry.
	 *
	 * @param contextElementType the type of the <code>contextElement</code>. This should be the base class - e.g. when subclassing {@link PDFSimpleNavigator}, you should still pass <code>PdfSimpleNavigator.class</code> and not the subclass' type. Must <b>not</b> be <code>null</code>!
	 * @param contextElementId the identifier of the context-element. Can be <code>null</code>.
	 * @param contextElement the context-element. Can be <code>null</code> to remove a previously added entry.
	 */
	protected void setContextElement(final ContextElementType<?> contextElementType, final String contextElementId, final ContextElement<?> contextElement)
	{
		assertValidThread();

		if (contextElementType == null) {
			throw new IllegalArgumentException("contextElementType must not be null!"); //$NON-NLS-1$
		}

		if (contextElement != null) {
			contextElementType.assertValidContextElementImplementation(contextElement);
		}

		Map<String, ContextElement<?>> id2contextElement = contextElementType2id2contextElement.get(contextElementType);
		if (id2contextElement == null && contextElement != null) {
			id2contextElement = new HashMap<String, ContextElement<?>>();
			contextElementType2id2contextElement.put(contextElementType, id2contextElement);
		}

		if (contextElement == null) {
			if (id2contextElement != null) {
				final ContextElement<?> oldContextElement = id2contextElement.remove(contextElementId);
				if (oldContextElement != null) {
					oldContextElement.onUnregisterContextElement();
				}
			}
		} else {
			id2contextElement.put(contextElementId, contextElement);
		}

		if (id2contextElement != null && id2contextElement.isEmpty()) {
			contextElementType2id2contextElement.remove(contextElementType);
		}

		contextElementType2id2contextElementCache.remove(contextElementType);
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
	public <T extends ContextElement<T>> T getContextElement(final ContextElementType<T> contextElementType, final String id) {
		assertValidThread();

		if (contextElementType == null) {
			throw new IllegalArgumentException("contextElementType must not be null!"); //$NON-NLS-1$
		}

		final Map<String, ContextElement<?>> id2contextElement = contextElementType2id2contextElement.get(contextElementType);
		if (id2contextElement == null) {
			return null;
		}

		return (T) id2contextElement.get(id);
	}

	/**
	 * @param contextElementType
	 *          the type of the <code>contextElement</code> as passed to
	 *          {@link #setContextElement(ContextElementType, String, ContextElement)} before.
	 * @return an immutable <code>Collection</code> containing the previously registered context-elements; never <code>null</code>
	 *         (instead, an empty <code>Collection</code> is returned). This <code>Collection</code> is not backed by the registry and can
	 *         be safely iterated while the registry is modified.
	 */
	@SuppressWarnings("unchecked")
	public <T extends ContextElement<?>> Collection<T> getContextElements(final ContextElementType<?> contextElementType)
	{
		assertValidThread();

		if (contextElementType == null) {
			throw new IllegalArgumentException("contextElementType must not be null!"); //$NON-NLS-1$
		}

		Map<String, ContextElement<?>> id2contextElement = contextElementType2id2contextElementCache.get(contextElementType);
		if (id2contextElement != null) {
			return (Collection<T>) id2contextElement.values();
		}

		id2contextElement = contextElementType2id2contextElement.get(contextElementType);
		if (id2contextElement == null) {
			id2contextElement = Collections.emptyMap();
		} else {
			id2contextElement = Collections.unmodifiableMap(new HashMap<String, ContextElement<?>>(id2contextElement));
		}

		contextElementType2id2contextElementCache.put(contextElementType, id2contextElement);

		return (Collection<T>) id2contextElement.values();
	}

	private Collection<ContextElement<?>> allContextElementsCache = null;

	/**
	 * Get all {@link ContextElement}s that are registered.
	 *
	 * @return an immutable <code>Collection</code> containing all {@link ContextElement}s. This <code>Collection</code> is not backed by
	 *         the registry and can be safely iterated while the registry is modified.
	 */
	public Collection<? extends ContextElement<?>> getContextElements()
	{
		assertValidThread();

		if (allContextElementsCache == null) {
			final Set<ContextElement<?>> result = new HashSet<ContextElement<?>>();
			for (final Map<String, ContextElement<?>> id2contextElement : contextElementType2id2contextElement.values()) {
				result.addAll(id2contextElement.values());
			}
			allContextElementsCache = Collections.unmodifiableCollection(result);
		}
		return allContextElementsCache;
	}
}
