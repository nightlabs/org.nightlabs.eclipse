/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.ui.exceptionhandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;

/**
 * Maintains a Map of {@link IExceptionHandler} and is able
 * to search the right handler for an exception.
 * <p>
 * This class staticly holds a default registry as singleton static member and
 * provides some static convenience methods statically which
 * work with the default shared instance.
 * </p>
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author marco schulze - marco at nightlabs dot de
 */
public class ExceptionHandlerRegistry extends AbstractEPProcessor {
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ExceptionHandlerRegistry.class);
	
	/**
	 * The registry can be set to different modes.
	 */
	public static enum Mode {
		/**
		 * Mode processByHandler indicates that
		 * exceptions passed to the registry
		 * will be processed by the appropriate
		 * registered {@link IExceptionHandler}.
		 */
		processByHandler,
		/**
		 * Mode bypass indicates that
		 * exceptions passed to the registry
		 * will only be logged and not passed
		 * to {@link IExceptionHandler}s.
		 */
		bypass
	}
	
	/**
	 * The current mode of the registry, will be initialised with {@link Mode#processByHandler}.
	 */
	private Mode mode = Mode.processByHandler;

//	private Map<String, IExceptionHandler> exceptionHandlers = new HashMap<String, IExceptionHandler>();
	private Map<String, ExceptionHandlerRegistryItem> targetTypeName2registryItem = new HashMap<String, ExceptionHandlerRegistryItem>();

	// Dummy object to provide thread safety
	// IMPROVE other synchronization strategy
	// Comment by Marco: why? what's the problem with this mutex?
	private Object targetTypeName2registryItemMutex = new Object();

	public void addExceptionHandler(String targetType, IExceptionHandler handler)
	{
		addExceptionHandler(targetType, handler, -1);
	}

	public void addExceptionHandler(String targetType, IExceptionHandler handler, int priority)
	{
		synchronized(targetTypeName2registryItemMutex) {
			targetTypeName2registryItem.put(targetType, new ExceptionHandlerRegistryItem(targetType, handler, priority));
		}
	}

	public void removeExceptionHandler(String targetType)
	{
		synchronized(targetTypeName2registryItemMutex) {
			targetTypeName2registryItem.remove(targetType);
		}
	}

	protected ExceptionHandlerRegistryItem getExceptionHandlerRegistryItem(Class<?> targetType) {
		return getExceptionHandlerRegistryItem(targetType.getName());
	}

	protected ExceptionHandlerRegistryItem getExceptionHandlerRegistryItem(String targetTypeName) {
		synchronized(targetTypeName2registryItemMutex) {
			return targetTypeName2registryItem.get(targetTypeName);
		}
	}

	/**
	 * Finds registered ExceptionHandlers. Moves up the class hierarchy for the
	 * passed exception itself and all its nested cause exceptions to find
	 * a handler for the specific class.
	 * <p>
	 * Note, that it starts at the root-cause and works its way up the wrapped exceptions.
	 * </p>
	 *
	 * @param exception the exception thrown and in need to be handled.
	 * @param skipItems all those items that should be ignored (usually, because they already have their handlers being triggered and their handlers returned false indicating that they don't want to handle the exception)
	 * @return <code>null</code> if no handler could be found or an instance of {@link ExceptionHandlerSearchResult} containing the required information for triggering the handler.
	 */
	protected ExceptionHandlerSearchResult searchHandler(Throwable exception, Set<ExceptionHandlerRegistryItem> skipItems) {
		// make sure the registrations where made
		checkProcessing();

		// Build a stack of causes with the root cause being the first and the wrappers following in order of wrapping.
		// This is important, because if the priority of 2 handlers is the same (or it's the same handler), then
		// the handler closest to the root-cause is used and the triggerException is the one closest to the root.
		LinkedList<Throwable> causeStack = new LinkedList<Throwable>();
		{
			Throwable x = exception;
			while (x != null) {
				causeStack.addFirst(x);
				x = ExceptionUtils.getCause(x);
			}
		}

		// now iterate the causes and search for handler with highest logical (lowest numerical) priority
		ExceptionHandlerSearchResult result = null;
		ExceptionHandlerRegistryItem bestItem = null;
		for (Iterator<Throwable> itCause = causeStack.iterator(); itCause.hasNext(); ) {
			Throwable cause = itCause.next();
			Class<?> searchClass = cause.getClass();

			ExceptionHandlerRegistryItem item = getExceptionHandlerRegistryItem(searchClass);
			if (item != null && skipItems.contains(item))
				item = null;

			// If there is no item found or the item is skipped, we step up the class hierarchy of the 'cause' to find
			// a suitable item.
			while (item == null && !Throwable.class.equals(searchClass)) {
				searchClass = searchClass.getSuperclass();
				item = getExceptionHandlerRegistryItem(searchClass);

				// If there is no more wrapper-exception (i.e. itCause has no next element) and the found item is to be skipped,
				// we continue going up the class hierarchy. This should end in the worst case with the default exception handler
				// being used. But theoretically (if there is no default), this method might return null.
				// If we have further wrapper-exceptions, we do not go up the hierarchy, because the skipped handler probably was the right
				// one already and it obviously decided to skip this cause. I'm not completely sure yet, though, whether this strategy is the
				// best. Marco.
//				if (item != null && !itCause.hasNext() && skipItems.contains(item)) 
//					item = null;
				// hmmm... if a handler decided to ignore, we should probably better first stay at the same cause, climbing up the class
				// hierarchy, because this behaviour seems to make more sense. If the default handler is found, it has a very low
				// logical priority anyway, so that a more specific handler (having a higher logical priority) for a wrapping exception
				// will be chosen instead anyway. Marco.
				if (item != null && skipItems.contains(item))
					item = null;
			}

			if (item == null)
				continue;

			if (skipItems.contains(item))
				continue;

			if (bestItem == null) {
				bestItem = item;
				result = new ExceptionHandlerSearchResult();
				result.setExceptionHandlerRegistryItem(bestItem);
				result.setTriggerException(cause);
			}
			else if (bestItem.getPriority() > item.getPriority()) {
				bestItem = item;
				result.setExceptionHandlerRegistryItem(bestItem);
				result.setTriggerException(cause);
			}
		}

		return result;
	}
	
	/**
	 * This method executes an ExceptionHandler on the GUI thread
	 * and does not wait for it. If this method is executed on the GUI
	 * thread, it the actual exception handling is performed in the next iteration
	 * of the event loop and this method returns before the handling was done.
	 *
	 * @param exception the exception that has been thrown and should be handled.
	 */
	public static void asyncHandleException(Throwable exception)
	{
		sharedInstance().handleException(Thread.currentThread(), exception, true);
	}

	/**
	 * This method executes an ExceptionHandler on the GUI thread
	 * and does not wait for it. If this method is executed on the GUI
	 * thread, it the actual exception handling is performed in the next iteration
	 * of the event loop and this method returns before the handling was done.
	 *
	 * @param exception the exception that has been thrown and should be handled.
	 */
	public static void asyncHandleException(Thread thread, Throwable exception)
	{
		sharedInstance().handleException(thread, exception, true);
	}

	/**
	 * This method can be executed on every thread. It executes an
	 * ExceptionHandler on the GUI thread and waits for it to return.
	 *
	 * @param exception the exception that has been thrown and should be handled.
	 */
	public static boolean syncHandleException(Throwable exception)
	{
		return sharedInstance().handleException(Thread.currentThread(), exception, false);
	}

	/**
	 * This method can be executed on every thread. It executes an
	 * ExceptionHandler on the GUI thread and waits for it to return.
	 *
	 * @param exception the exception that has been thrown and should be handled.
	 */
	public static boolean syncHandleException(Thread thread, Throwable exception)
	{
		return sharedInstance().handleException(thread, exception, false);
	}

	/**
	 * Sets the mode of the registry.
	 * See the {@link Mode} description on what they mean.
	 * <p>
	 * Important: This is a pretty internal method and you should well understand what you are doing when you decide to use it!
	 * It is very unlikely that you really need this method. Think twice!
	 * </p>
	 *
	 * @param mode The mode to set.
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	/**
	 * Searches the {@link IExceptionHandler} for the given exception and invokes
	 * its {@link IExceptionHandler#handleException(Thread, Throwable, Throwable)} method.
	 * <p>
	 * Note, that if the {@link #mode} of the registry is {@link Mode#bypass} this
	 * method will simply log the error and return.
	 * </p>
	 * @param thread The thread the exception occured on.
	 * @param exception The exception to handle.
	 * @param async Whether to handle asynchronously (value = <code>true</code>) or synchronously (value <code>true</code>)
	 * @return Whether a appropriate handler could be found and invoked. This is only a useful result, if <code>async</code> is <code>false</code>! If <code>async</code> is true, this is <code>null</code>.
	 */
	private Boolean handleException(final Thread thread, final Throwable exception, final boolean async)
	{
		if (mode == Mode.bypass) {
			logger.error("ExceptionHandlerRegistry bypassing (Mode.bypass) Exception: " + exception, exception); //$NON-NLS-1$
			return async ? null : Boolean.TRUE;
		}
		else {
			logger.error(String.valueOf(exception), exception);
		}

		final Set<ExceptionHandlerRegistryItem> skipItems = new HashSet<ExceptionHandlerRegistryItem>();

		// The first handler is searched on the calling thread. This is very likely the right handler.
		// If not, we search further handlers on the UI thread (within the Runnable).
		final ExceptionHandlerSearchResult finalSearchResult = searchHandler(exception, skipItems);

		if (finalSearchResult != null) {

			final Boolean[] result = new Boolean[] { null };

			try {
				Runnable runnable = new Runnable(){
					public void run() {
						if (!async)
							result[0] = Boolean.FALSE;

						try {
							ExceptionHandlerSearchResult searchResult = finalSearchResult;

							boolean handled = false;
							while (true) {
								skipItems.add(searchResult.getExceptionHandlerRegistryItem());
								IExceptionHandler handler = searchResult.getExceptionHandlerRegistryItem().getExceptionHandler();
							
								ExceptionHandlerParam exceptionParam = new ExceptionHandlerParam(thread,exception, searchResult.getTriggerException()); 
								exceptionParam.setErrorScreenShot(RCPUtil.takeApplicationScreenShot());
								handled = handler.handleException(exceptionParam);
								if (handled)
									break;

								searchResult = searchHandler(exception, skipItems);
								if (searchResult == null) {
									StringBuilder foundHandlers = new StringBuilder();
									for (ExceptionHandlerRegistryItem exceptionHandlerRegistryItem : skipItems) {
										if (foundHandlers.length() != 0)
											foundHandlers.append(", "); //$NON-NLS-1$

										foundHandlers.append(exceptionHandlerRegistryItem.getExceptionHandler().getClass().getName());
									}
									logger.fatal("None of the found ExceptionHandlers (" + foundHandlers + ") handled this Throwable!", exception); //$NON-NLS-1$ //$NON-NLS-2$
									return;
								}
							}

							if (handled && !async)
								result[0] = Boolean.TRUE;

						} catch (Throwable x) {
							logger.fatal("Exception occured while handling exception on GUI thread!", x); //$NON-NLS-1$
						}
			
					}
				};

				if (async)
					Display.getDefault().asyncExec(runnable);
				else
					Display.getDefault().syncExec(runnable);
				
			} catch (Throwable ex) {
				logger.fatal("Exception occured while handling exception on causing thread!", ex); //$NON-NLS-1$
		  }

			if (result[0] == null && !async)
				result[0] = Boolean.FALSE;

			return result[0];
		}
		else {
			logger.fatal("Did not find an ExceptionHandler for this Throwable!", exception); //$NON-NLS-1$
			return async ? null : Boolean.FALSE;
		}
	}
	
	private static final String EXTENSION_POINT_ID = "org.nightlabs.base.ui.exceptionhandler"; //$NON-NLS-1$

	/**
	 * Processes exceptionHandler extension-point elements.
	 * For each element one instance of exceptionHandler.class is registered
	 * in the {@link ExceptionHandlerRegistry}.
	 * @param element
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception
	{
		if (element.getName().toLowerCase().equals("exceptionhandler")) { //$NON-NLS-1$
			String targetType = element.getAttribute("targetType"); //$NON-NLS-1$

			IExceptionHandler handler = (IExceptionHandler) element.createExecutableExtension("class"); //$NON-NLS-1$
			if (!IExceptionHandler.class.isAssignableFrom(handler.getClass()))
				throw new IllegalArgumentException("Specified class for element exceptionHandler must implement "+IExceptionHandler.class.getName()+". "+handler.getClass().getName()+" does not."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			int priority = -1; // default => will be changed into 500
			String priorityString = element.getAttribute("priority"); //$NON-NLS-1$
			if (priorityString != null && !"".equals(priorityString)) { //$NON-NLS-1$
				try {
					priority = Integer.parseInt(priorityString);
					if (priority < 0 || priority > 1000)
						throw new NumberFormatException("Out of range!"); //$NON-NLS-1$
				} catch (NumberFormatException x) {
					NumberFormatException y = new NumberFormatException("priority=\"" + priorityString + "\" is not a valid integer in the range between 0 and 1000!"); //$NON-NLS-1$ //$NON-NLS-2$
					y.initCause(x);
					throw y;
				}
			}

			addExceptionHandler(targetType, handler, priority);
		}
		else {
			// wrong element according to schema, probably checked earlier
			throw new IllegalArgumentException("Element "+element.getName()+" is not supported by extension-point " + EXTENSION_POINT_ID); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}
	
	
	
	private static ExceptionHandlerRegistry sharedInstance;
	
	public static ExceptionHandlerRegistry sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new ExceptionHandlerRegistry();
		}
		return sharedInstance;
	}
}
