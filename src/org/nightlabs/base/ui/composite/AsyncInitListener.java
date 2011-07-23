package org.nightlabs.base.ui.composite;

/**
 * <p>
 * Listener to be notified when the UI was completely initialised.
 * </p><p>
 * <b>Important:</b> This listener must be triggered on the SWT/RWT UI thread!
 * </p><p>
 * It is fired by composites (usually implementing {@link AsyncInitComposite}) which load
 * data asynchronously in the background before they finish creating their UI. In this case, the surrounding
 * UI can only access the composite's contents after the data is loaded (by a Job) - when this listener triggered.
 * </p>
 * 
 * @author marco
 */
public interface AsyncInitListener {
	
	/**
	 * <p>
	 * Event notification method reporting the completion of the UI initialisation.
	 * </p><p>
	 * This method is fired on the UI thread. It should not be fired anymore, if the composite
	 * firing this event (the event's <code>source</code>) is
	 * already disposed. This is because the semantics of this event is to notify an observer
	 * that UI is ready. If the data is ready but the UI already disposed, it is not ready in the
	 * sense of this event (and never will become ready!).
	 * </p><p>
	 * The contract is explicitely not ensuring this method will ever be called, because even
	 * if it didn't take the disposed state into account, there might be other reasons preventing
	 * the callback (e.g. an exception when fetching the data).
	 * </p><p>
	 * However, this method is always called for every listener when the UI is initialised as planned.
	 * If the UI was already initialised before the listener was added (e.g. because the data retrieval was very fast due to a cache),
	 * this method should be immediately called while it is added. 
	 * </p>
	 * @param event the event.
	 */
	void initialised(AsyncInitEvent event);

}
