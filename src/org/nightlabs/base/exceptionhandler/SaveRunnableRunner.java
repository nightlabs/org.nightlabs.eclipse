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

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.util.ISafeRunnableRunner;

/**
 * Runner for <tt>ISafeRunnable</tt>s that uses
 * the {@link org.nightlabs.base.exceptionhandler.ExceptionHandlerRegistry}.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class SaveRunnableRunner implements ISafeRunnableRunner {

	public SaveRunnableRunner() {
		super();
	}

	/**
	 * Runs the code and handles exceptions with
	 * the {@link ExceptionHandlerRegistry}.
	 * 
	 * @see org.eclipse.jface.util.ISafeRunnableRunner#run(org.eclipse.core.runtime.ISafeRunnable)
	 */
	public void run(ISafeRunnable code) {
		try {
			code.run();
		} catch (Throwable t) {
			ExceptionHandlerRegistry.asyncHandleException(t);
		}
	}

}
