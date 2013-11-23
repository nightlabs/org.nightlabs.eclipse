/**
 *
 */
package org.nightlabs.base.ui.context;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.swt.widgets.Display;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [dOt] de -->
 *
 */
public class UIContext {

	// Switched to WeakHashMap (instead of ConcurrentHashMap), because it is hard to guarantee that
	// Threads really call unregister(...).
	private Map<Thread, IUIContextRunner> contextRunners = new WeakHashMap<Thread, IUIContextRunner>();
	private Map<Thread, Object> contextObjects = new WeakHashMap<Thread, Object>();

	/**
	 *
	 */
	protected UIContext() {
	}

	public synchronized void registerRunner(Thread thread, IUIContextRunner runner) {
		contextRunners.put(thread, runner);
	}

	public synchronized void registerObject(Thread thread, Object object) {
		contextObjects.put(thread, object);
	}

	public synchronized void unregister(Thread thread) {
		contextRunners.remove(thread);
		contextObjects.remove(thread);
	}

	public synchronized IUIContextRunner getRunner(Thread thread) {
		return contextRunners.get(thread);
	}

	public synchronized IUIContextRunner getSaveRunner(Thread thread) {
		IUIContextRunner runner = contextRunners.get(thread);
		if (runner == null) {
			runner = new DefaultUIContextRunner();
		}
		return runner;
	}

	public Display getDisplay(Thread thread) {
		return getSaveRunner(thread).getDisplay();
	}

	public static Display getDisplay() {
		return sharedInstance().getDisplay(Thread.currentThread());
	}

	public static IUIContextRunner getRunner() {
		return sharedInstance().getSaveRunner(Thread.currentThread());
	}

	private static UIContext sharedInstance;

	public static UIContext sharedInstance() {
		if (sharedInstance == null) {
			synchronized (UIContext.class) {
				if (sharedInstance == null) {
					sharedInstance = new UIContext();
				}
			}
		}
		return sharedInstance;
	}

}
