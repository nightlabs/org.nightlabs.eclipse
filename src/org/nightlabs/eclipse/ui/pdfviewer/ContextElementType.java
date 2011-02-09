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

import org.nightlabs.eclipse.ui.pdfrenderer.internal.Util;

/**
 * An instance of this class specifies a type a {@link ContextElement}.
 * 
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class ContextElementType<T extends ContextElement<?>>
{
	private Class<T> contextElementBaseClass;

	public ContextElementType(Class<T> contextElementBaseClass) {
		if (contextElementBaseClass == null)
			throw new IllegalArgumentException("contextElementBaseClass must not be null!"); //$NON-NLS-1$

		this.contextElementBaseClass = contextElementBaseClass;
	}

	private int _hashCode = 0;

	@Override
	public int hashCode() {
		if (_hashCode != 0)
			return _hashCode;

		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((contextElementBaseClass == null) ? 0 : contextElementBaseClass.hashCode());

		_hashCode = result;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ContextElementType<?> other = (ContextElementType<?>) obj;
		return Util.equals(this.contextElementBaseClass, other.contextElementBaseClass);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + '[' + contextElementBaseClass.getName() +']';
	}

	public boolean isValidContextElementImplementation(ContextElement<?> contextElement)
	{
		return contextElementBaseClass.isInstance(contextElement);
	}
	public void assertValidContextElementImplementation(ContextElement<?> contextElement)
	{
		if (!isValidContextElementImplementation(contextElement))
			throw new IllegalArgumentException("contextElement is not a valid implementation: " + contextElement); //$NON-NLS-1$
	}
}
