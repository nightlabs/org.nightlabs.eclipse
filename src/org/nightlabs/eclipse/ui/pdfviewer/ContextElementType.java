package org.nightlabs.eclipse.ui.pdfviewer;

import org.nightlabs.eclipse.ui.pdfviewer.internal.Util;

public class ContextElementType {
	private String contextElementTypeID;

	public ContextElementType(Class<?> contextElementTypeBaseClass) {
		this(contextElementTypeBaseClass.getName());
	}

	public ContextElementType(String contextElementTypeID) {
		if (contextElementTypeID == null)
			throw new IllegalArgumentException("contextElementTypeID must not be null!");

		this.contextElementTypeID = contextElementTypeID;
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
				+ ((contextElementTypeID == null) ? 0 : contextElementTypeID.hashCode());

		_hashCode = result;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ContextElementType other = (ContextElementType) obj;
		return Util.equals(this.contextElementTypeID, other.contextElementTypeID);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + '[' + contextElementTypeID +']';
	}
}
