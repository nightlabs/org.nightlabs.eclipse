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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class NotificationManager
extends org.nightlabs.notification.NotificationManager
{
	protected NotificationManager()
	{
		new NotificationManagerInterceptorEPProcessor(this).process();
	}

	/**
	 * @see org.nightlabs.notification.NotificationManager#getNotificationModeForListener(org.nightlabs.notification.NotificationListener)
	 */
	protected String getNotificationModeForListener(NotificationListener listener)
	{
		if (listener instanceof NotificationListenerJob)
			return NotificationListenerJob.class.getName();

		if (listener instanceof NotificationListenerSWTThreadSync)
			return NotificationListenerSWTThreadSync.class.getName();

		if (listener instanceof NotificationListenerSWTThreadAsync)
			return NotificationListenerSWTThreadAsync.class.getName();

		return super.getNotificationModeForListener(listener);
	}

	/**
	 * @see org.nightlabs.notification.NotificationManager#performNotification(java.lang.String, org.nightlabs.notification.NotificationListener, org.nightlabs.notification.NotificationEvent)
	 */
	protected void performNotification(String notificationMode, final NotificationListener listener,
			final NotificationEvent event)
	{
		listener.setActiveNotificationEvent(event);
		if (NotificationListenerJob.class.getName().equals(notificationMode)) {
			NotificationListenerJob l = (NotificationListenerJob) listener;

			Job job = l.createJob(event);
			if (job == null) {
				String jobName = l.getJobName();
				if (jobName == null)
					jobName = Messages.getString("org.nightlabs.base.notification.NotificationManager.jobName"); //$NON-NLS-1$

				job = new Job(jobName) {
					protected IStatus run(IProgressMonitor monitor)
					{
						((NotificationListenerJob)listener).setProgressMonitor(monitor);
						((NotificationListenerJob)listener).notify(event);

						return Status.OK_STATUS;
					}
				};
				job.setRule(l.getRule());
				job.setPriority(l.getPriority());
				job.setUser(l.isUser());
				job.setSystem(l.isSystem());
			}
			job.schedule(l.getDelay());
		}
		else if (NotificationListenerSWTThreadAsync.class.getName().equals(notificationMode)) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run()
				{
					listener.notify(event);
				}
			});
		}
		else if (NotificationListenerSWTThreadSync.class.getName().equals(notificationMode)) {
			Display.getDefault().syncExec(new Runnable() {
				public void run()
				{
					listener.notify(event);
				}
			});
		}
		else
			super.performNotification(notificationMode, listener, event);
	}
}
