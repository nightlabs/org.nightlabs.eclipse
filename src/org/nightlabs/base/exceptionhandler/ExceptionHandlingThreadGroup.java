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

package org.nightlabs.base.exceptionhandler;



/**
 * ExceptionHandlingThreadGroup adds exception handling to 
 * a thread group. It is used internally to extend the mechanism of
 * exception handling in the NightLabs RCP framework. 
 * @author Alexander Bieber
 */
public class ExceptionHandlingThreadGroup extends ThreadGroup {

	/**
	 * @param name
	 */
	public ExceptionHandlingThreadGroup(String name) {
		super(name);
	}

	/**
	 * @param parent
	 * @param name
	 */
	public ExceptionHandlingThreadGroup(ThreadGroup parent, String name) {
		super(parent, name);
	}
	
	

	public void uncaughtException(Thread t, Throwable e) {
		if (!ExceptionHandlerRegistry.syncHandleException(t, e))
			super.uncaughtException(t, e);
//		final ExceptionHandlerRegistry.ExceptionHandlerSearchResult handlerSearch = ExceptionHandlerRegistry.searchHandler(e);
//		Throwable handlingException = null;
//		final Thread thread = t;
//		final Throwable exception = e;
//		if (handlerSearch.getHandler() != null){
//			try {
//				Display.getDefault().syncExec(new Runnable(){
//					public void run(){
//						try {
//							handlerSearch.getHandler().handleException(thread,exception,handlerSearch.getTriggerException());
//						} catch(Throwable x) {
//						}
//					}
//				});
//				
//			} catch (Throwable ex) {
//				handlingException = ex;
//				handlingException.initCause(e);
//		  }
//		  	if (handlingException != null)
//		  		super.uncaughtException(t,handlingException);
//		}
//		else
//			super.uncaughtException(t, e);
	}
}
