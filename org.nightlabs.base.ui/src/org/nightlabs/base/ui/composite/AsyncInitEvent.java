package org.nightlabs.base.ui.composite;

import java.util.EventObject;

/**
 * Event information given to an {@link AsyncInitListener}. 
 * 
 * @author marco
 */
public class AsyncInitEvent extends EventObject
{
	private static final long serialVersionUID = 1L;

	/**
	 * Create an <code>AsyncInitEvent</code>.
	 * 
	 * @param source the composite or other UI element which finished its initialisation (the one which provides an <code>addInitListener(...)</code> method).
	 */
	public AsyncInitEvent(Object source) {
		super(source);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * In <code>AsyncInitEvent</code>, this method always returns the composite or other UI element which finished its
	 * initialisation (the one which provides an <code>addInitListener(...)</code> method).
	 * </p>
	 */
	@Override
	public Object getSource() {
		return super.getSource();
	}
}
