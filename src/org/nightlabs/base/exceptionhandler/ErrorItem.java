/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
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
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/


package org.nightlabs.base.exceptionhandler;

/** 
 * Helper class for displaying errors in <code>ErrorTable</code>s
 * 
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public class ErrorItem
{
	private Throwable thrownException, triggerException;
	private String message;
	
	public ErrorItem(String message, Throwable thrownException, Throwable triggerException)
	{
		if (message == null) {
			if (triggerException != null) {
				this.message = triggerException.getLocalizedMessage();
			} else if (thrownException != null) {
				this.message = thrownException.getLocalizedMessage();
			}
		} else {
			this.message = message;
		}
		this.thrownException = thrownException;
		this.triggerException = triggerException;
	}
	
	public String getMessage()
	{
		return message;
	}
	public Throwable getThrownException()
	{
		return thrownException;
	}
	public Throwable getTriggerException()
	{
		return triggerException;
	}
}
