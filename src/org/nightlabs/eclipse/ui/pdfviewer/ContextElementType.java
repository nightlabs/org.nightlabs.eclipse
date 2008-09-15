package org.nightlabs.eclipse.ui.pdfviewer;

import org.nightlabs.eclipse.ui.pdfviewer.internal.Util;

public class ContextElementType<T extends ContextElement<?>>
{
	private Class<T> contextElementBaseClass;

	public ContextElementType(Class<T> contextElementBaseClass) {
		if (contextElementBaseClass == null)
			throw new IllegalArgumentException("contextElementBaseClass must not be null!");

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
			throw new IllegalArgumentException("contextElement is not a valid implementation: " + contextElement);
	}
}
