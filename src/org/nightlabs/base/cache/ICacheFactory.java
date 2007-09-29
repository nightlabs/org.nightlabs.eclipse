/**
 * 
 */
package org.nightlabs.base.cache;

import org.eclipse.core.runtime.IExecutableExtension;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface ICacheFactory extends IExecutableExtension {

	/**
	 * Should return an unique identifyer for this factory.
	 * @return An unique identifyer for this factory.
	 */
	public String getID();

	/**
	 * Should return an implementation of {@link ICache}.
	 * This can be accessed by {@link CacheRegistry}.
	 * 
	 * @return An implementation of {@link ICache}.
	 */
	public ICache createCache();
}
