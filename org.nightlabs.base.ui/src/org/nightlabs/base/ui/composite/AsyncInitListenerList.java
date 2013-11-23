package org.nightlabs.base.ui.composite;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.widgets.Display;

/**
 * @author marco
 */
public class AsyncInitListenerList extends ListenerList
{
	private AsyncInitComposite asyncInitComposite;
	private Display display;
	private boolean alreadyFired = false;

	/**
	 * Create an <code>AsyncInitListenerList</code>.
	 * @param asyncInitComposite the composite firing {@link AsyncInitEvent}s.
	 */
	public AsyncInitListenerList(AsyncInitComposite asyncInitComposite) {
		this.asyncInitComposite = asyncInitComposite;
		this.display = asyncInitComposite.getDisplay();
	}

	/**
	 * <p>
	 * Fire the {@link AsyncInitEvent}.
	 * </p><p>
	 * This method is thread-safe. It makes sure that the {@link AsyncInitListener#initialised(AsyncInitEvent)}
	 * method is triggered on the SWT/RWT UI thread.
	 * </p>
	 * @param sync if <code>true</code>, this method blocks until all listeners were notified.
	 * If <code>false</code>, this method immediately returns and the event is propagated via {@link Display#asyncExec(Runnable)}.
	 */
	public void fireAsyncInitEvent(boolean sync)
	{
		fireAsyncInitEvent(sync, null);
	}
	
	private void fireAsyncInitEvent(boolean sync, final Object[] listeners)
	{
		if (display == null || asyncInitComposite.isDisposed()) // maybe not UI thread
			return;

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Object[] ll = listeners;
				if (ll == null) {
					synchronized (AsyncInitListenerList.this) {
						ll = getListeners();
						alreadyFired = true;
					}
				}

				if (ll.length == 0 || asyncInitComposite.isDisposed()) // now definitely UI thread
					return;

				// We fire outside the synchronized block to prevent dead-locks!
				AsyncInitEvent event = new AsyncInitEvent(asyncInitComposite);
				for (int i = 0; i < ll.length; ++i) {
					((AsyncInitListener) ll[i]).initialised(event);
				}
			}
		};

		if (sync && display.getThread() == Thread.currentThread())
			runnable.run();
		else if (sync)
			display.syncExec(runnable);
		else
			display.asyncExec(runnable);
	}
	
	@Override
	public synchronized void add(Object listener) {
		super.add(listener);
		if (alreadyFired)
			fireAsyncInitEvent(true, new Object[] { listener });
	}
}
