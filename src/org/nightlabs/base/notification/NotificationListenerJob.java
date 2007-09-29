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

package org.nightlabs.base.notification;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * Notification will be done by unsing an Eclipse {@link org.eclipse.core.runtime.jobs.Job}.
 * It is a good idea, NOT to use this interface directly, but to extend
 * {@link org.nightlabs.base.notification.NotificationAdapterJob}.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface NotificationListenerJob extends NotificationListener
{
	/**
	 * Important: If you implement this method (and do not return <tt>null</tt>), all other methods of this
	 * interface - except {@link #getDelay()} - will be ignored! Additionally,
	 * {@link NotificationListener#notify(org.nightlabs.notification.NotificationEvent)} must be called by
	 * your <tt>Job</tt> or will not be called at all!
	 *
	 * @param event The same notification event that will be passed to {@link NotificationListener#notify(org.nightlabs.notification.NotificationEvent)}.
	 *
	 * @return Return <tt>null</tt> or an instance of <tt>Job</tt> that will be used for this notification.
	 *		If you return <tt>null</tt>, a new <tt>Job</tt> will be created which will be configured using the other
	 *		methods.
	 */
	Job createJob(NotificationEvent event);

	/**
	 * This method is called directly before
	 * {@link NotificationListener#notify(org.nightlabs.notification.NotificationEvent)}.
	 *
	 * @param progressMonitor
	 */
	void setProgressMonitor(IProgressMonitor progressMonitor);

	/**
	 * @return The progressMonitor that was passed to {@link #setProgressMonitor(IProgressMonitor)}
	 */
	IProgressMonitor getProgressMonitor();
	
	/**
	 * @return Returns the name of the job.
	 */
	String getJobName();

	/**
	 * @return Returns the delay in milliseconds that the job will be started after the notification event
	 *		has occured. It probably doesn't make much sense to return another value than 0L.
	 */
	long getDelay();

	/**
	 * @return Returns the priority which will be set via {@link org.eclipse.core.runtime.jobs.Job#setPriority(int)}.
	 */
	int getPriority();

	/**
	 * @return Returns <tt>null</tt> or a rule that will be set via {@link org.eclipse.core.runtime.jobs.Job#setRule(org.eclipse.core.runtime.jobs.ISchedulingRule)}.
	 */
	ISchedulingRule getRule();

	/**
	 * @see Job#setUser(boolean)
	 */
	boolean isUser();

	/**
	 * @see Job#setSystem(boolean)
	 */
	boolean isSystem();
}
