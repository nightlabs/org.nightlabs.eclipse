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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * An implementation of {@link IProgressMonitor} that takes
 * a list of monitors and dipatches all calls to all of them.
 * 
 * A compound progress monitor remembers all operations invoked to it
 * and is able to integrate a new monitor by invoking all past actions
 * on it see ({@link #addProgressMonitor(IProgressMonitor)}).
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class CompoundProgressMonitor implements IProgressMonitor {

	/**
	 * Logger used by this class.
	 */
	private static final Logger logger = Logger.getLogger(CompoundProgressMonitor.class);
	
	private Collection<IProgressMonitor> monitors = Collections.synchronizedList(new LinkedList<IProgressMonitor>());
	private List<MonitorInvocation> invocations = Collections.synchronizedList(new LinkedList<MonitorInvocation>()); 

	/**
	 * Invokations are stored as methodName and params
	 * and invoked per reflection.
	 */
	private static class MonitorInvocation {
		public String methodName;
		public Object[] params;
		public MonitorInvocation(String methodName, Object... params) {
			this.methodName = methodName;
			this.params = params;
		}
		public void invoke(IProgressMonitor monitor) {
			Class[] paramTypes = new Class[params.length];
			for (int i = 0; i < params.length; i++) {
				paramTypes[i] = params[i].getClass();
				if (paramTypes[i] == Integer.class)
					paramTypes[i] = int.class;
				else if (paramTypes[i] == Boolean.class)
					paramTypes[i] = boolean.class;
				else if (paramTypes[i] == Double.class)
					paramTypes[i] = double.class;
			}
			try {
				Method method = monitor.getClass().getMethod(methodName, paramTypes);
				method.invoke(monitor, params);
			} catch (Exception e) {
				logger.warn("Could not invoke "+methodName+" on "+monitor, e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	/**
	 * Create a new compound progress monitor for the given
	 * list of monitors.
	 * 
	 * @param monitors The list of monitors to dispatch to.
	 */
	public CompoundProgressMonitor(IProgressMonitor... monitors) {
		for (int i = 0; i < monitors.length; i++) {
			this.monitors.add(monitors[i]);
		}
	}

	/**
	 * Adds an invokation.
	 */
	private void addInvocation(String methodName, Object... params) {
		invocations.add(new MonitorInvocation(methodName, params));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String, int)
	 */
	public void beginTask(String name, int totalWork) {
		for (IProgressMonitor monitor : monitors) {
			monitor.beginTask(name, totalWork);
		}
		addInvocation("beginTask", name, totalWork); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#done()
	 */
	public void done() {
		for (IProgressMonitor monitor : monitors) {
			monitor.done();
		}
		addInvocation("done"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
	 */
	public void internalWorked(double work) {
		for (IProgressMonitor monitor : monitors) {
			monitor.internalWorked(work);
		}
		addInvocation("internalWorked", work); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Will combine the cancelled flag of all monitors with logical or.
	 * 
	 * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
	 */
	public boolean isCanceled() {
		boolean result = false;
		for (IProgressMonitor monitor : monitors) {
			result = result | monitor.isCanceled();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
	 */
	public void setCanceled(boolean value) {
		for (IProgressMonitor monitor : monitors) {
			monitor.setCanceled(value);
		}
		addInvocation("setCanceled", value); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
	 */
	public void setTaskName(String name) {
		for (IProgressMonitor monitor : monitors) {
			monitor.setTaskName(name);
		}
		addInvocation("setTaskName", name); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
	 */
	public void subTask(String name) {
		for (IProgressMonitor monitor : monitors) {
			monitor.subTask(name);
		}
		addInvocation("subTask", name); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
	 */
	public void worked(int work) {
		for (IProgressMonitor monitor : monitors) {
			monitor.worked(work);
		}
		addInvocation("worked", work); //$NON-NLS-1$
	}

	/**
	 * Adds the given progress monitor the of monitors known to this instance,
	 * so that further calls to this monitor will be dispatched to the new one, too.
	 * 
	 * Prior to adding all former invokations performed on this monitor will be
	 * invoked for the new one as well, so it will (in most cases) be in the same
	 * state as all others managed by this one. 
	 * 
	 * @param monitor The monitor to integrate.
	 */
	public void addProgressMonitor(IProgressMonitor monitor) {
		for (MonitorInvocation invocation : new LinkedList<MonitorInvocation>(invocations)) {
			invocation.invoke(monitor);
		}
		monitors.add(monitor);
	}
}

