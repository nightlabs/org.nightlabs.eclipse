/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.app;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;

/**
 * Basic {@link WorkbenchAdvisor} that can be used as basis when developing custom applications.
 * This Advisor initializes Log4J and the NightLabs {@link Config} for you.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Daniel Mazurek - Daniel.Mazurek [AT] NightLabs [DOT] de
 */
public abstract class AbstractWorkbenchAdvisor 
extends WorkbenchAdvisor 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(AbstractWorkbenchAdvisor.class);

	public AbstractWorkbenchAdvisor() 
	{
		super();
		try {
			application = initApplication();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

	/**
	 * Checks the {@link ExceptionHandlerRegistry} for registered handlers by invoking
	 * {@link ExceptionHandlerRegistry#searchHandler(Throwable)}. For the found item the 
	 * {@link org.eclipse.jface.window.Window.IExceptionHandler#handleException(java.lang.Throwable)} 
	 * method is invoked on a unique instance of the eventHandler class (plugin.xml). 
	 */
	public void eventLoopException(Throwable exception) {
		if (!ExceptionHandlerRegistry.syncHandleException(exception))
			super.eventLoopException(exception);
	}  	

	/**
	 * Initializes the workbench 
	 */
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
	}	

	/**
	 * saves the Config before the Application is shutDown
	 */
	@Override
	public boolean preShutdown() 
	{
		boolean superResult = super.preShutdown();
		try {
			org.nightlabs.config.Config.sharedInstance().save();
		} catch (ConfigException e) {
			logger.error("Saving config failed!", e); //$NON-NLS-1$
		}
		
		Set<IWorkbenchListener> listener = WorkbenchListenerRegistry.sharedInstance().getListener();
		for (IWorkbenchListener workbenchListener : listener) {
			workbenchListener.preShutdown();
		}
		
		return superResult;
	}

	protected AbstractApplication application = null;
	/**
	 * return the {@link AbstractApplication} associated with this
	 * AbstractWorkbenchAdvisor
	 * 
	 * @return the AbstractApplication associated with this 
	 * AbstractWorkbenchAdvisor
	 */
	public AbstractApplication getApplication() 
	{
		if (application == null)
			application = initApplication();

		return application;
	}

	/**
	 * Initializes the application.
	 * <p>
	 * The default implementation returns 
	 * {@link AbstractApplication#sharedInstance()}
	 * which should be the application that is
	 * currently running.
	 * <p>
	 * Extendors might call <code>super.initApplication()</code>
	 * and then modify the result.
	 */
	protected AbstractApplication initApplication() {
		return AbstractApplication.sharedInstance();
	}

	/**
	 * creates the DefaultWorkbenchWindowAdvisor by default.
	 * if you want to specify your own WorkbenchWindowAdvisor, you have to override
	 * this Method
	 * 
	 * @param configurer the IWorkbenchWindowConfigurer
	 * @return the WorkbenchWindowAdvisor for this WorkbenchAdvisor
	 */
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new DefaultWorkbenchWindowAdvisor(configurer, AbstractApplication.getApplicationName());
	}

	@Override
	public void postShutdown() {
		super.postShutdown();
		checkClearWorkspace();
		
		Set<IWorkbenchListener> listener = WorkbenchListenerRegistry.sharedInstance().getListener();
		for (IWorkbenchListener workbenchListener : listener) {
			workbenchListener.postShutdown();
		}
	}		
	
	/**
	 * checks if the -clearWorkspace programm argument is set and if
	 * so clears the workspace directory of the application
	 */
	protected void checkClearWorkspace() 
	{
		String[] args = NLBasePlugin.getDefault().getApplication().getArguments();
		boolean doClearWorkspace = false;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			String val = i + 1 < args.length ? args[i + 1] : null;
			if ("--clearWorkspace".equals(arg)) //$NON-NLS-1$
				doClearWorkspace = Boolean.parseBoolean(val);
		}
		if (doClearWorkspace) {
			logger.info("************ clearing workspace! **************"); //$NON-NLS-1$
			RCPUtil.clearWorkspace(false);
		}
	}

	@Override
	public boolean openWindows() 
	{
		boolean openWindows = super.openWindows();
		Set<IWorkbenchListener> listener = WorkbenchListenerRegistry.sharedInstance().getListener();
		for (IWorkbenchListener workbenchListener : listener) {
			workbenchListener.openWindows();
		}
		return openWindows;
	}

	@Override
	public void postStartup() 
	{
		super.postStartup();
		Set<IWorkbenchListener> listener = WorkbenchListenerRegistry.sharedInstance().getListener();
		for (IWorkbenchListener workbenchListener : listener) {
			workbenchListener.postStartup();
		}		
	}

	@Override
	public void preStartup() {
		super.preStartup();
		Set<IWorkbenchListener> listener = WorkbenchListenerRegistry.sharedInstance().getListener();
		for (IWorkbenchListener workbenchListener : listener) {
			workbenchListener.preStartup();
		}		
	}	
		
}
