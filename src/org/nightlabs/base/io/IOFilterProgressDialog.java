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

package org.nightlabs.base.io;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

import org.nightlabs.io.AbstractIOFilterWithProgress;
import org.nightlabs.io.IOFilter;
import org.nightlabs.io.IOFilterWithProgress;

public class IOFilterProgressDialog 
extends ProgressMonitorDialog
{
	protected IOFilter ioFilter;
	public IOFilterProgressDialog(Shell parent, IOFilter ioFilter) 
	{
		super(parent);
		this.ioFilter = ioFilter;
		if (ioFilter instanceof IOFilterWithProgress) {
			IOFilterWithProgress progressFilter = (IOFilterWithProgress) ioFilter;		
			progressFilter.addPropertyChangeListener(subProgressListener);
			getProgressMonitor().beginTask(ioFilter.getDescription().getText(Locale.getDefault().getLanguage()), progressFilter.getTotalWork());			
		}
		else {
			getProgressMonitor().beginTask(ioFilter.getDescription().getText(Locale.getDefault().getLanguage()), 2);			
		}
	}
	
	protected PropertyChangeListener subProgressListener = new PropertyChangeListener()
	{	
		public void propertyChange(PropertyChangeEvent evt) 
		{
			Object newValue = evt.getNewValue();
			if (newValue.equals(AbstractIOFilterWithProgress.PROGRESS_CHANGED)) {
				int work = ((Integer)newValue).intValue();
				getProgressMonitor().internalWorked(work); 
			}
			else if (newValue.equals(AbstractIOFilterWithProgress.SUBTASK_FINISHED)) {
				String subTaskName = (String) newValue;
				getProgressMonitor().subTask(subTaskName);
			}
		}	
	};
	
}
