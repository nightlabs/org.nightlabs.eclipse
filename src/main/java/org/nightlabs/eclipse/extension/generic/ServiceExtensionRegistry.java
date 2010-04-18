package org.nightlabs.eclipse.extension.generic;

import org.nightlabs.eclipse.extension.ExtensionPlugin;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ServiceExtensionRegistry extends GenericExtensionRegistry<IdExtension>
{
	private static final String EXTENSION_POINT_ID = "org.nightlabs.eclipse.extension.service";
	private static final String EXTENSION_ELEMENT_NAME = "service";

	/**
	 * Create a new ServiceExtensionRegistry instance.
	 */
	public ServiceExtensionRegistry()
	{
		super(EXTENSION_POINT_ID, EXTENSION_ELEMENT_NAME, true, ExtensionPlugin.getDefault());
	}
}
