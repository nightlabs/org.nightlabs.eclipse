/**
 * 
 */
package org.nightlabs.base.cache;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * A factory providing a cache that isn't able to cache anything yet.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DummyCacheFactory implements ICacheFactory {

	public static class Cache implements ICache {
		public Object get(String scope, Object key) {
			return null;
		}
		public Object get(Object key) {
			return null;
		}
		public void put(String scope, Object key, Object object) {
		}
		public void put(Object key, Object object) {
		}
		public void remove(Object key) {
		}
		public void removeAll() {
		}
		public void removeByKeyClass(Class keyClass) {
		}
	}
	
	/**
	 * 
	 */
	public DummyCacheFactory() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.cache.ICacheFactory#createCache()
	 */
	public ICache createCache() {
		return new Cache();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.cache.ICacheFactory#getID()
	 */
	public String getID() {
		return DummyCacheFactory.class.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
	}


}
