/**
 * 
 */
package org.nightlabs.base.cache;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * Use this registry to access intances of {@link ICache}.
 * 
 * The static method {@link #getCache()} provides access to 
 * the last {@link ICacheFactory} registered via the extension-point
 * {@value #EXTENSION_POINT_ID}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class CacheRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.cache"; //$NON-NLS-1$
	
	private static class CacheCarrier {
		private ICache cache;
		private ICacheFactory cacheFactory;
		
		public ICache getCache() {
			if (cache == null) {
				if (cacheFactory == null)
					throw new IllegalStateException("ICacheFactory was not created using extension-point "+EXTENSION_POINT_ID); //$NON-NLS-1$
				cache = cacheFactory.createCache();
				if (cache == null)
					throw new IllegalStateException("Registered ICacheFactory "+cacheFactory.getClass().getName()+" returned null cache!"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return cache; 
		}
	}
	
	private Map<String, CacheCarrier> cacheCarriers = new HashMap<String, CacheCarrier>();
	
	/**
	 * 
	 */
	public CacheRegistry() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element) 
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase("cacheFactory")) { //$NON-NLS-1$
			try {
				CacheCarrier carrier = new CacheCarrier();
				carrier.cacheFactory = (ICacheFactory)element.createExecutableExtension("class"); //$NON-NLS-1$
				cacheCarriers.put(carrier.cacheFactory.getID(), carrier);
				lastRegisteredFactoryID = carrier.cacheFactory.getID();
			} catch (CoreException e) {
				throw new EPProcessorException("Could not create ICacheFactory ", extension, e); //$NON-NLS-1$
			}
		}
	}
	
	public ICache getCache(String factoryID) {
		checkProcessing();
		CacheCarrier carrier = cacheCarriers.get(factoryID);
		if (carrier == null)
			return null;
		return carrier.getCache();
	}
	
	public ICache getLastRegisteredCache() {
		checkProcessing();
		return getCache(lastRegisteredFactoryID);
	}
	
	private static String lastRegisteredFactoryID;
	
	/**
	 * Can be used to overwrite the factory that is used
	 * by the static accessors for an instance
	 * of {@link ICache}.
	 * 
	 * @param staticFactoryID The id of the factory to be used from now. 
	 */
	public static void setStaticFactoryID(String staticFactoryID) {
		lastRegisteredFactoryID = staticFactoryID;
	}
	
	private static CacheRegistry sharedInstance;

	/**
	 * Access a static instance of {@link CacheRegistry}.
	 */
	public static CacheRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new CacheRegistry();
		return sharedInstance;
	}


	/**
	 * Uses the {@link #sharedInstance()} to access an instance
	 * of {@link ICache} either via the last registered {@link ICacheFactory}
	 * or the factory referenced by the last call to {@link #setStaticFactoryID(String)}
	 * 
	 * @return An instance of {@link ICache}.
	 * @throws IllegalStateException If something fails when accessing the 'last registered' cache
	 */
	public static ICache getCache() {
		return sharedInstance().getLastRegisteredCache();
	}
	
}
