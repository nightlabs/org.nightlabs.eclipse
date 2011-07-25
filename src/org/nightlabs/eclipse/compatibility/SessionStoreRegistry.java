package org.nightlabs.eclipse.compatibility;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.rwt.service.ISessionStore;

public class SessionStoreRegistry {

private static Map<Thread, ISessionStore> stores = new WeakHashMap<Thread, ISessionStore>();
	
	/**
	 * 
	 */
	public SessionStoreRegistry() {
	}
	
	public static void associateThread(Thread thread, ISessionStore sessionStore) {
		stores.put(thread, sessionStore);
	}
	
	public static void disposeThread(Thread thread) {
		stores.remove(thread);
	}

	public static ISessionStore getSessionStore() {
		return getSessionStore(Thread.currentThread());
	}
	
	public static ISessionStore getSessionStore(Thread thread) {
		return stores.get(thread);
	}
}
