/**
 * 
 */
package org.nightlabs.base.cache;


/**
 * Interface used by the {@link CacheRegistry} to access a simple
 * object cache.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface ICache {

	/**
	 * Put the given object into the cache and associate it to the given key.
	 * Like in Maps the object is looked up later via the {@link Object#hashCode()}
	 * and {@link Object#equals(Object)} methods.
	 *  
	 * @param scope The scope under wich to put the given object into the cache.
	 * @param key The key to use for the given object.
	 * @param object The object to put into the cache
	 */
	public void put(String scope, Object key, Object object);
	/**
	 * Put the given object into the cache under the <code>null</code> scope.
	 * @see #put(String, Object, Object);
	 * 
	 * @param key The key to use for the given object.
	 * @param object The object to put into the cache
	 */
	public void put(Object key, Object object);

	
	/**
	 * Get the object associated to the given key out of the cache.
	 * If not corresponding object is found return <code>null</code>.
	 * 
	 * @param scope The scope under wich to search the object.
	 * @param key The key to search the object for.
	 * @return The object out of the cache that is associated to the given key, or <code>null</code>.
	 */
	public Object get(String scope, Object key);	
	/**
	 * Get the object associated to the given key out of the cache
	 * and use the <code>null</code> scope.
	 * @see #get(String, Object)
	 * 
	 * @param key The key to search the object for.
	 * @return The object out of the cache that is associated to the given key, or <code>null</code>.
	 */
	public Object get(Object key);

	/**
	 * Remove all objects from the cache that are
	 * associated to the given key.
	 * 
	 * @param key The key for wich all objects should be removed. 
	 */
	public void remove(Object key);

	/**
	 * Remove all objects from the cache that are
	 * associated to a key of the given class.
	 * 
	 * @param keyClass The class of keys all objects should be removed from cache.
	 */
	public void removeByKeyClass(Class keyClass);
	
	/**
	 * Remove all objects from the cache.
	 */
	public void removeAll();
	
}
