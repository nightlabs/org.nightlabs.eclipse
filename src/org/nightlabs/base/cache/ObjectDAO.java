/**
 * 
 */
package org.nightlabs.base.cache;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * This class springs from org.nightlabs.jfire.base.jdo.JDOObjectDAO
 * that is used to retrieve JDOObjects for a JFire Server.
 * This derivation of an ObjectDAO uses the {@link CacheRegistry}
 * instead and is intended to be used for non JDO objects.
 * <p>
 * Inherit this class with a key class and an object class 
 * as generic parameters to provide an accessor object for 
 * this kind of object.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class ObjectDAO<KeyType, ObjectType> {
	/**
	 * The cache shared instance.
	 */
	private ICache cache = CacheRegistry.getCache();

	/**
	 * Default constructor.
	 */
	public ObjectDAO()
	{
	}

	/**
	 * Retrieve an object when not found in the cache. 
	 * This method will be called by {@link #getObjects(String, List, IProgressMonitor)
	 * for all objects that are not already in the cache.
	 * <p>
	 * Subclassers may override this method to provide a specialized way to
	 * retrieve a single object somewhere :-). The given implementation
	 * works by calling {@link #retrieveObjects(List, IProgressMonitor)}
	 * for the single object.
	 * 
	 * @param key The key of the object to get
	 * @param monitor The progress monitor for this action. After retrieving the
	 * 					object, <code>monitor.worked(1)</code> should be called.
	 * @return All requested and existing objects.
	 * @throws Exception in case of an error
	 */
	protected ObjectType retrieveObject(KeyType key, IProgressMonitor monitor)
	throws Exception
	{
		List<KeyType> keys = new ArrayList<KeyType>(1);
		keys.add(key);
		List<ObjectType> objects = retrieveObjects(keys, monitor);
		if(objects == null || objects.isEmpty())
			return null;
		return objects.iterator().next();
	}
	
	/**
	 * Retrieve objects not found in the cache. 
	 * This method will be called by
	 * {@link #getObjects(String, List, IProgressMonitor)
	 * for all objects that are not already in the cache.
	 * 
	 * @param keys Wich objects to get
	 * @param monitor The progress monitor for this action. For every retrieved
	 * 					object, <code>monitor.worked(1)</code> should be called.
	 * @return All requested and existing objects.
	 * @throws Exception in case of an error
	 */
	protected abstract List<ObjectType> retrieveObjects(List<KeyType> keys, IProgressMonitor monitor)
	throws Exception;

	/**
	 * Get an object from the cache.
	 * Object not found in the cache will be retrieved by calling 
	 * {@link #retrieveObject(Object, IProgressMonitor).
	 * 
	 * 
	 * @param scope The cache scope to use
	 * @param key Wich object to get
	 * @param monitor The progress monitor for this action. For every cached
	 * 					object, <code>monitor.worked(1)</code> will be called.
	 * @return All requested and existing objects.
	 * @throws Exception in case of an error
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public synchronized ObjectType getObject(String scope, KeyType key, IProgressMonitor monitor)
	{
		try {
			ObjectType res = (ObjectType) cache.get(scope, key);
			if (res == null) {
				res = retrieveObject(key, monitor);
				cache.put(scope, key, res);
			}
			return res;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}
	
	/**
	 * Get objects from the cache.
	 * Objects not found in the cache will be retrieved by calling 
	 * {@link #retrieveObjects(List, IProgressMonitor).
	 * 
	 * @param scope The cache scope to use
	 * @param keys Wich objects to get
	 * @param monitor The progress monitor for this action. For every cached
	 * 					object, <code>monitor.worked(1)</code> will be called.
	 * @return All requested and existing objects.
	 * @throws Exception in case of an error
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public synchronized List<ObjectType> getObjects(String scope, List<KeyType> keys, IProgressMonitor monitor)
	{
		try	{
			ArrayList<ObjectType> objects = new ArrayList<ObjectType>();
			
			if(keys.size() == 1) {
				objects.add(getObject(scope, keys.iterator().next(), monitor));
				return objects;
			}
			
			List<KeyType> fetchKeys = new ArrayList<KeyType>();
			for (KeyType key : keys) {
				ObjectType res = (ObjectType) cache.get(scope, key);
				if(res != null) {
					objects.add(res);
					monitor.worked(1);
				}
				else
					fetchKeys.add(key);
			}

			if (fetchKeys.size() > 0) {
				if(fetchKeys.size() == 1) {
					KeyType key = fetchKeys.iterator().next();
					ObjectType fetchedObject = retrieveObject(key, monitor);
					cache.put(scope, key, fetchedObject);
					objects.add(fetchedObject);
					monitor.worked(1);
				} else {
					List<ObjectType> fetchedObjects = retrieveObjects(fetchKeys,  monitor);
					for (int i=0; i<fetchedObjects.size(); i++) {
						cache.put(fetchKeys.get(i), fetchedObjects.get(i));
					}
					objects.addAll(fetchedObjects);
					monitor.worked(fetchedObjects.size());
				}
			}
			return objects;
			
		} catch (Exception x)	{
			throw new RuntimeException(x);
		}
	}

	/**
	 * Get objects from the cache.
	 * Objects not found in the cache will be retrieved by calling 
	 * {@link #retrieveObjects(List, IProgressMonitor).
	 * <p>
	 * This is a convenience method that calls
	 * {@link #getObjects(String, List, IProgressMonitor)}
	 * with a <code>null</code> scope.
	 * 
	 * @param keys Wich objects to get
	 * @return All requested and existing objects.
	 */
	public synchronized List<ObjectType> getObjects(List<KeyType> keys, IProgressMonitor monitor)
	{
		return getObjects(null, keys, monitor);
	}

	/**
	 * Get objects from the cache.
	 * Objects not found in the cache will be retrieved by calling 
	 * {@link #retrieveObjects(List, IProgressMonitor).
	 * <p>
	 * This is a convenience method that calls 
	 * {@link #getObjects(String, List, IProgressMonitor)
	 * 
	 * @param scope The cache scope to use
	 * @param keys Wich objects to get
	 * @param monitor The progress monitor for this action. For every cached
	 * 					object, <code>monitor.worked(1)</code> will be called.
	 * @return All requested and existing objects.
	 * @throws Exception in case of an error
	 */
	public synchronized List<ObjectType> getObjects(String scope, KeyType[] keys, IProgressMonitor monitor)
	{
		return getObjects(
				scope,
				CollectionUtil.array2ArrayList(keys), 
				monitor
			);
	}	
	
	/**
	 * Get the cache instance used by this ObjecDAO.
	 * @return The cache instance used by this ObjecDAO.
	 */
	protected ICache getCache() 
	{
		return cache;
	}
}
