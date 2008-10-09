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
 * <code>CONTEXT_ELEMENT_TYPE</code> (and return it in your implementation of {@link #getContextElementType()}),
 * if you don't subclass another <code>ContextElement</code> (that already
 * defines this constant). However, if you subclass one and your new implementation is doing something completely
 * different, you are encouraged to redeclare this <code>CONTEXT_ELEMENT_TYPE</code> (with your new class)
 * in order to indicate that it has semantically nothing in common with the superclass.
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
public interface ContextElement<T extends ContextElement<T>>
{
	/**
	 * Get the type of the context element. This type defines the semantic functionality
	 * of your <code>ContextElement</code>. Therefore, if you subclass an existing implementation,
	 * you have to think about whether your implementation is semantically the same (just better of course ;-)
	 * or whether it's sth. different.
	 * <p>
	 * In other words: If an instance of your subclass would usually be used together with an instance
	 * of the superclass in the same viewer (because they provide a complement to each other), then your
	 * subclass is semantically different and should therefore declare its own <code>CONTEXT_ELEMENT_TYPE</code>.
	 * If an instance of your subclass would instead replace the instance of the superclass, because they provide
	 * the same functionality, you should not override this method and not declare your own <code>CONTEXT_ELEMENT_TYPE</code>.
	 * </p>
	 *
	 * @return the constant <code>CONTEXT_ELEMENT_TYPE</code> declared by your implementation.
	 */
	ContextElementType<T> getContextElementType();

	/**
	 * Get the unique identifier of this <code>ContextElement</code> instance or <code>null</code>.
	 * <p>
	 * This is only required, if multiple instances of the same {@link ContextElementType} are
	 * used with one {@link PdfViewer} instance. For example, if you put one simple navigator beneath
	 * the thumbnails in an external view (e.g. the outline) and a second one beneath your main viewer
	 * area inside an editor.
	 * </p>
	 *
	 * @return the identifier of this <code>ContextElement</code> instance (within the scope of the current {@link PdfViewer}
	 *		and the {@link ContextElementType} returned by {@link #getContextElementType()}).
	 */
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
