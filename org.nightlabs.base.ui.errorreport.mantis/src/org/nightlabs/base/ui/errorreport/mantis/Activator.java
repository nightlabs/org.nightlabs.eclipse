package org.nightlabs.base.ui.errorreport.mantis;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin
{
	private static Activator plugin;

	/**
	 * The constructor.
	 */
	public Activator()
	{
		super();
		plugin = this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception 
	{
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception 
	{
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static Activator getDefault() 
	{
		return plugin;
	}
}
