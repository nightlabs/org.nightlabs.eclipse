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

package org.nightlabs.base.progress;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressIndicator;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;

/**
 * An implementation of {@link IProgressMonitor} that
 * will do its work on the SWT Display thread.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SaveProgressMonitorPart extends ProgressMonitorPart {

	/**
	 * @param parent
	 * @param layout
	 */
	public SaveProgressMonitorPart(Composite parent, Layout layout) {
		super(parent, layout);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public SaveProgressMonitorPart(Composite parent, Layout layout, int arg2) {
		super(parent, layout, arg2);
	}

	private boolean checkGUIexists() {
		ProgressIndicator indicator = SaveProgressMonitorPart.super.fProgressIndicator;
		if (indicator == null || indicator.isDisposed())
			return false;
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.ProgressMonitorPart#beginTask(java.lang.String, int)
	 */
	public void beginTask(final String title, final int work) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (! checkGUIexists())
					return;

				SaveProgressMonitorPart.super.beginTask(title, work);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.ProgressMonitorPart#done()
	 */
	public void done() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (! checkGUIexists())
					return;

				SaveProgressMonitorPart.super.done();
			}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.ProgressMonitorPart#internalWorked(double)
	 */
	public void internalWorked(final double worked) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (! checkGUIexists())
					return;
					
				SaveProgressMonitorPart.super.internalWorked(worked);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.ProgressMonitorPart#isCanceled()
	 */
	public boolean isCanceled() {
		class BooleanHolder {
			public boolean bool = false;
		}
		final BooleanHolder holder = new BooleanHolder();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (! checkGUIexists()) {
					holder.bool = true;
					return;
				}

				holder.bool = SaveProgressMonitorPart.super.isCanceled();
			}
		});
		return holder.bool;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.ProgressMonitorPart#setCanceled(boolean)
	 */
	public void setCanceled(final boolean canceled) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (! checkGUIexists())
					return;

				SaveProgressMonitorPart.super.setCanceled(canceled);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.ProgressMonitorPart#setTaskName(java.lang.String)
	 */
	public void setTaskName(final String taskName) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (! checkGUIexists())
					return;
				
				SaveProgressMonitorPart.super.setTaskName(taskName);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.ProgressMonitorPart#subTask(java.lang.String)
	 */
	public void subTask(final String name) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (! checkGUIexists())
					return;
				
				SaveProgressMonitorPart.super.subTask(name);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.ProgressMonitorPart#worked(int)
	 */
	public void worked(final int worked) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (! checkGUIexists())
					return;

				SaveProgressMonitorPart.super.worked(worked);
			}
		});
	}
	
}
