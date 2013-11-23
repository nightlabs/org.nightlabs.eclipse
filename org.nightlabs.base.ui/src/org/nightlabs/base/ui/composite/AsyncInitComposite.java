package org.nightlabs.base.ui.composite;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

/**
 * UI element which loads data asynchronously in the background before finishing UI creation.
 * It allows the surrounding UI to register
 * listeners in order to be notified when the data was loaded and the UI is thus fully accessible.
 * 
 * @author marco
 */
public interface AsyncInitComposite {

	/**
	 * <p>
	 * Add an {@link AsyncInitListener}. This method must be thread-safe.
	 * </p><p>
	 * It is recommended to use a {@link ListenerList} for the implementation.
	 * </p><p>
	 * If the <code>AsyncInitComposite</code> implementation already finished initialisation, this method
	 * must immediately fire the event to the new listener. It is recommended to use code like this to
	 * prevent the listener from being fired on a non-UI-thread:
	 * </p>
	 * <pre>
	 * </pre>
	 * 
	 * @param listener the listener to be added. Must not be <code>null</code>.
	 */
	void addAsyncInitListener(AsyncInitListener listener);

	/**
	 * <p>
	 * Remove an {@link AsyncInitListener} that was previously added by {@link #addAsyncInitListener(AsyncInitListener)}.
	 * This method must be thread-safe.
	 * </p>
	 * @param listener the listener to be removed. Must not be <code>null</code>.
	 * @see #addAsyncInitListener(AsyncInitListener)
	 */
	void removeAsyncInitListener(AsyncInitListener listener);

	/**
	 * <p>
	 * Get the display.
	 * </p><p>
	 * This method must be thread-safe. As usually a {@link Composite}-subclass implements this interface,
	 * the implementation of this method is normally {@link Widget#getDisplay()}.
	 * </p>
	 * @return the display (see {@link Widget#getDisplay()}).
	 */
	Display getDisplay();
	
	/**
	 * <p>
	 * Is the composite already disposed?
	 * </p><p>
	 * This method must be thread-safe. As usually a {@link Composite}-subclass implements this interface,
	 * the implementation of this method is normally {@link Widget#isDisposed()}.
	 * </p>
	 * @return the disposed state, i.e. <code>true</code> when this composite was already disposed.
	 */
	boolean isDisposed();

}
