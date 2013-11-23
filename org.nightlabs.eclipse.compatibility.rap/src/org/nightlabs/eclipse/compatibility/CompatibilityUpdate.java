/**
 * 
 */
package org.nightlabs.eclipse.compatibility;

import java.net.URL;

/**
 * @author daniel
 *
 */
public class CompatibilityUpdate 
{
	/**
	 * <p>
	 * Get all plug-ins' install locations.
	 * </p><p>
	 * This must not only include all running
	 * plug-ins, but all installed ones. It is used to find the original
	 * <code>org.nightlabs.jfire.base.j2ee</code> even if a runtime-copy of the
	 * plug-in is currently used.
	 * </p><p>
	 * Important: This method returns currently nothing (an empty array) in the RAP implementation.
	 * It needs to be modified to not work with the old Update-Manager-API, anymore (which does
	 * not exist in RAP at all and is deprecated in the RCP since 3.6).
	 * </p><p>
	 * TODO We need to find a way to determine the locations via pure OSGI-API (ideally without
	 * Update-Manager-API and without P2-API!).
	 * </p>
	 * 
	 * @return all plug-ins' install locations.
	 */
	public static URL[] getPlatformPluginPaths() 
	{
		return new URL[] {};
	}
}
