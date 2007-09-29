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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.internal.progress.ErrorInfo;

public class JobErrorNotificationManager 
// implements IJobErrorNotificationManager 
{

	public JobErrorNotificationManager() {
		super();
	}

	public boolean showErrorFor(Job job, String title, String msg) {
		ExceptionHandlerRegistry.asyncHandleException(job.getResult().getException());
		return true;
	}

	public boolean hasErrors() {
		return errors.size() > 0;
	}

	private List errors = new ArrayList();
	
	public void addError(IStatus status, Job job) {
		errors.add(new ErrorInfo(status, job));
		ExceptionHandlerRegistry.asyncHandleException(status.getException());
//		clearAllErrors();
	}
	
	public Collection getErrors() {
		return errors;
	}

	public void clearAllErrors() {
//		ErrorNotificationManager.removeFromFinishedJobs(errors);
		errors.clear();
	}

}
