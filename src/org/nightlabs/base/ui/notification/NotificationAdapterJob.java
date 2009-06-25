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

package org.nightlabs.base.ui.notification;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.nightlabs.base.ui.progress.RCPProgressMonitor;
import org.nightlabs.notification.NotificationAdapter;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.progress.ProgressMonitor;


public abstract class NotificationAdapterJob
extends NotificationAdapter
implements NotificationListenerJob
{
	private String jobName = null;

	public NotificationAdapterJob() { }

	public NotificationAdapterJob(String jobName)
	{
		this.jobName = jobName;
	}

	/**
	 * @see org.nightlabs.base.ui.notification.NotificationListenerJob#createJob(NotificationEvent)
	 */
	public org.nightlabs.base.ui.job.Job createJob(NotificationEvent event)
	{
		return null;
	}

	public String getJobName()
	{
		return jobName;
	}

	private ProgressMonitor progressMonitor;
//	private ProgressMonitor progressMonitorWrapper;
	private IProgressMonitor rcpProgressMonitor;

	@Override
	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	@Override
	public ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	public IProgressMonitor getRCPProgressMonitor() {
		if (rcpProgressMonitor == null) {
			if (progressMonitor == null)
				throw new IllegalStateException("getRCPProgressMonitor() must not be called before setProgressMonitor(ProgressMonitor)."); //$NON-NLS-1$
			rcpProgressMonitor = new RCPProgressMonitor(progressMonitor);
		}
		return rcpProgressMonitor;
	}

	/**
	 * @deprecated Use {@link #getProgressMonitor()} instead! This method exists only for downward compatibility!
	 */
	@Deprecated
	public ProgressMonitor getProgressMonitorWrapper() {
		return getProgressMonitor();
	}
//	public ProgressMonitor getProgressMonitorWrapper() {
//		if (progressMonitorWrapper == null) {
//			if (progressMonitor == null)
//				throw new IllegalStateException("getProgressMonitorWrapper() must not be called before setProgressMonitor(IProgressMonitor)."); //$NON-NLS-1$
//			progressMonitorWrapper = new ProgressMonitorWrapper(progressMonitor);
//		}
//		return progressMonitorWrapper;
//	}

	/**
	 * @see org.nightlabs.base.ui.notification.NotificationListenerJob#getRule()
	 */
	public ISchedulingRule getRule()
	{
		return null;
	}

	/**
	 * The default implementation of this method returns {@link Job#SHORT}.
	 *
	 * @see org.nightlabs.base.ui.notification.NotificationListenerJob#getPriority()
	 */
	public int getPriority()
	{
		return Job.SHORT;
	}

	/**
	 * The default implementation of this method returns 0.
	 *
	 * @see org.nightlabs.base.ui.notification.NotificationListenerJob#getDelay()
	 */
	public long getDelay()
	{
		return 0;
	}

	/**
	 * The default implementation of this method returns false.
	 *
	 * @see org.nightlabs.base.ui.notification.NotificationListenerJob#isUser()
	 */
	public boolean isUser()
	{
		return false;
	}

	/**
	 * The default implementation of this method returns false.
	 *
	 * @see org.nightlabs.base.ui.notification.NotificationListenerJob#isSystem()
	 */
	public boolean isSystem()
	{
		return false;
	}
}
