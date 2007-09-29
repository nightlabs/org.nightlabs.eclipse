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

package org.nightlabs.base.part;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.nightlabs.base.util.RCPUtil;

/**
 * PartVisibilityTracker is used to determine the visibility sate of {@link org.eclipse.ui.IWorkbenchPart}s.
 * It can be asked whether a Part is visble (see. {@link #isPartVisible(IWorkbenchPart)}).
 * Addtionally {@link org.nightlabs.base.part.PartVisibilityListener} can be 
 * registered that will be notified when the visibility status of a part changes
 * (see {@link #addVisibilityListener(IWorkbenchPart, PartVisibilityListener)}).  
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class PartVisibilityTracker {

	public static final int PART_STATUS_NO_VISIBILITY_CHANGE = 1;
	public static final int PART_STATUS_VISIBLE = 2;
	public static final int PART_STATUS_HIDDEN = 4;

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(PartVisibilityTracker.class);
	
	/**
	 * An instance of this listener will to the active WorkbenchPage and tracks
	 * status changes of the parts within that page.
	 * @see PartVisibilityTracker#initialize() 
	 */
	protected static class Listener implements IPartListener2 {
	
		private PartVisibilityTracker tracker;
		
		public Listener(PartVisibilityTracker tracker) {
			this.tracker = tracker;
		}

		/**
		 * Set the status for the part referenced by the given partRef 
		 */
		private void setPartStatus(IWorkbenchPartReference partRef, int newStatus) {
			PartStatus status = tracker.getPartStatus(partRef);
			if (status != null)
				status.setStatus(newStatus);
		}		
		
		private PartStatus getPartStatus(IWorkbenchPartReference partRef) {
			return tracker.getPartStatus(partRef);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partActivated(IWorkbenchPartReference partRef) {
			if (logger.isDebugEnabled())
				logger.debug("partActivated  partRef: "+partRef.getPartName()); //$NON-NLS-1$

			if (getPartStatus(partRef).getStatus() == PART_STATUS_HIDDEN)
				setPartStatus(partRef, PART_STATUS_VISIBLE);
			else
				setPartStatus(partRef, PART_STATUS_NO_VISIBILITY_CHANGE);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			if (logger.isDebugEnabled())
				logger.debug("partBroughtToTop  partRef: "+partRef.getPartName()); //$NON-NLS-1$

			setPartStatus(partRef, PART_STATUS_VISIBLE);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partClosed(IWorkbenchPartReference partRef) {
			if (logger.isDebugEnabled())
				logger.debug("partClosed  partRef: "+partRef.getPartName()); //$NON-NLS-1$

			setPartStatus(partRef, PART_STATUS_HIDDEN);
			getPartStatus(partRef).removeAllListeners();
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partDeactivated(IWorkbenchPartReference partRef) {
			if (logger.isDebugEnabled())
				logger.debug("partDeactivated  partRef: "+partRef.getPartName()); //$NON-NLS-1$

			int status = partRef.getPage().isPartVisible(partRef.getPart(false)) ? PART_STATUS_NO_VISIBILITY_CHANGE : PART_STATUS_HIDDEN;
			if (getPartStatus(partRef).getStatus() == 0 && PART_STATUS_NO_VISIBILITY_CHANGE == status)
				status = PART_STATUS_VISIBLE;

			setPartStatus(partRef, status);
//					partRef.getPage().isPartVisible(partRef.getPart(false)) ? PART_STATUS_VISIBLE : PART_STATUS_HIDDEN); // PART_STATUS_NO_VISIBILITY_CHANGE: PART_STATUS_HIDDEN);
		}

		/**
		 * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partOpened(IWorkbenchPartReference partRef) {
			if (logger.isDebugEnabled())
				logger.debug("partOpened  partRef: "+partRef.getPartName()); //$NON-NLS-1$

			setPartStatus(partRef, PART_STATUS_VISIBLE);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partHidden(IWorkbenchPartReference partRef) {
			if (logger.isDebugEnabled())
				logger.debug("partHidden  partRef: "+partRef.getPartName()); //$NON-NLS-1$

			setPartStatus(partRef, PART_STATUS_HIDDEN);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partVisible(IWorkbenchPartReference partRef) {
			if (logger.isDebugEnabled())
				logger.debug("partVisible  partRef: "+partRef.getId()); //$NON-NLS-1$

			setPartStatus(partRef, PART_STATUS_VISIBLE);
		}
		
		/**
		 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
		 */
		public void partInputChanged(IWorkbenchPartReference partRef) {
			if (logger.isDebugEnabled())
				logger.debug("partInputChanged  partRef: "+partRef.getPartName()); //$NON-NLS-1$

			setPartStatus(partRef, PART_STATUS_NO_VISIBILITY_CHANGE);
		}
	}

//	public static final int PART_STATUS_CLOSED = 4;
	
	/**
	 * Class to hold the status of a IWorkbenchPart and notify visibility listners.
	 */
	protected static class PartStatus {
		private IWorkbenchPart part;
		private IWorkbenchPartReference partRef;
		private int status;
		private List<PartVisibilityListener> listeners = new LinkedList<PartVisibilityListener>();
		
		private boolean haveDelayedHideStatusChanges = false;
//		private boolean doNotification = false;
		
		public PartStatus(IWorkbenchPart part) {
			this.part = part;
			this.partRef = null;
		}
		
		public PartStatus(IWorkbenchPart part, IWorkbenchPartReference partRef) {
			this.partRef = partRef;
			this.part = part;
		}

		/**
		 * @return Returns the status.
		 */
		public int getStatus() {
			return status;
		}
		
		public IWorkbenchPartReference getPartRef() {
			return partRef;
		}
		
		public void setPartRef(IWorkbenchPartReference partRef) {
			this.partRef = partRef;
		}
		
		public Runnable delayedHiddenNotifyer = new Runnable() {
			public void run() {
				if (logger.isDebugEnabled())
					logger.debug("delayedHiddenNotifyer: have visible, set haveDelayedHideStatusChanges to false status: "+status+" part: "+part+" have..."+haveDelayedHideStatusChanges); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				if (haveDelayedHideStatusChanges) {
					status = PART_STATUS_HIDDEN;
					notifyListeners(status);
				}
			}
		};

		/**
		 * @param newStatus The status to set.
		 */
		public void setStatus(int newStatus) {
			if (newStatus == PART_STATUS_NO_VISIBILITY_CHANGE)
				return;

			if (logger.isDebugEnabled())
				logger.debug("setStatus: newStatus: "+newStatus+" part: "+part); //$NON-NLS-1$ //$NON-NLS-2$

			if (haveDelayedHideStatusChanges) {
				if (newStatus == PART_STATUS_VISIBLE) {
					haveDelayedHideStatusChanges = false;
				}
			}
			if (status != newStatus) {
				if (newStatus == PART_STATUS_HIDDEN) {
					haveDelayedHideStatusChanges = true;

					if (logger.isDebugEnabled())
						logger.debug("setStatus: have hidden schedule delayed notification newStatus: "+newStatus+" part: "+part); //$NON-NLS-1$ //$NON-NLS-2$

					Display.getDefault().timerExec(200, delayedHiddenNotifyer);
				}
				else {

					if (logger.isDebugEnabled())
						logger.debug("setStatus: have visible, set haveDelayedHideStatusChanges to false newStatus: "+newStatus+" part: "+part); //$NON-NLS-1$ //$NON-NLS-2$

					haveDelayedHideStatusChanges = false;
					status = newStatus;
					notifyListeners(status);
				}
			}
		}
		
		/**
		 * Notify all listeners of the new satus
		 *
		 */
		public void notifyListeners(int newStatus) {
			for (Iterator iter = new LinkedList<PartVisibilityListener>(listeners).iterator(); iter.hasNext();) {
				PartVisibilityListener listener = (PartVisibilityListener) iter.next();
				switch (newStatus) {
					case PART_STATUS_HIDDEN:
						listener.partHidden(partRef);
						break;
					case PART_STATUS_VISIBLE:
						listener.partVisible(partRef);
						break;
				}
				
			}
		}
		
		public void asyncInitStatus(int status) {
			this.status = status;
			notifyListeners(status);
		}

		public IWorkbenchPart getPart() {
			return part;
		}
		
		public void addListener(PartVisibilityListener listener) {
			listeners.add(listener);
		}
		
		public void removeListener(PartVisibilityListener listener) {
			listeners.remove(listener);
		}
		
		public void removeAllListeners() {
			listeners.clear();
		}
	}
	
	/**
	 * key: IWorkbenchPart part<br/>
	 * value: PartStatus satus
	 */
	private Map<IWorkbenchPart, PartStatus> partStati = new HashMap<IWorkbenchPart, PartStatus>();

	/**
	 * 
	 */
	public PartVisibilityTracker() {
		super();
	}
	
	/**
	 * Get the PartStatus for the part referenced by the given partRef.
	 * Calls {@link #getPartStatus(IWorkbenchPartReference, IWorkbenchPart)}
	 * with partRef and partRef.getPart()
	 */
	private PartStatus getPartStatus(IWorkbenchPartReference partRef) {
		return getPartStatus(partRef, partRef.getPart(false));
	}
	
	/**
	 * Get the PartStatus for the given part. If not found a new PartStatus
	 * will be created for the given part and partRef.
	 */
	private PartStatus getPartStatus(IWorkbenchPartReference partRef, IWorkbenchPart part) {
		PartStatus status = (PartStatus)partStati.get(part);
		if (status == null) {			
			status = new PartStatus(part, partRef);
			partStati.put(part, status);
		}
		else if (status.getPartRef() == null)
			status.setPartRef(partRef);
		return status;
	}

	/**
	 * Calls {@link #getPartStatus(IWorkbenchPartReference, IWorkbenchPart)}
	 * with null as partRef parameter.
	 */
	private PartStatus getPartStatus(IWorkbenchPart part) {
		return getPartStatus(null, part);
	}

	/**
	 * Add a visibility Listener to this tracker.
	 * The passed listener will be notified of visibility changes of the given 
	 * part. You can add a listeners at any time but only to a non null 
	 * {@link IWorkbenchPart}. If at listener registration the WorkbenchWindow
	 * was not already created this listener will be asyncronously triggered with 
	 * {@link PartVisibilityListener#partVisible(IWorkbenchPartReference)}.  
	 * Note that all listeners to a part will be removed when this part is 
	 * closed.
	 *  
	 * @param part The IWorkbenchPart which visibility changes should be notified for.
	 * @param listener The listener to be notified.
	 */
	public void addVisibilityListener(final IWorkbenchPart part, final PartVisibilityListener listener) {
		if (part == null)
			throw new IllegalArgumentException("Cannot add listener for null-part. Parameter part must not be null"); //$NON-NLS-1$
		if (listener == null)
			throw new IllegalArgumentException("Parameter listener must not be null"); //$NON-NLS-1$
		
		if (!initialized)
			initialize();
		final PartStatus status = getPartStatus(part);
		if (status == null) {
			if (logger.isDebugEnabled())
				logger.warn("addVisibilityListener: Autocreation of PartStatus failed status is null, can not add Listener "+listener); //$NON-NLS-1$

			return;
		}
		status.addListener(listener);

		if (logger.isDebugEnabled())
			logger.debug("addVisibilityListener: Added PartVisibiltyListener "+listener); //$NON-NLS-1$

		if (!initialized)
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					status.asyncInitStatus(PART_STATUS_VISIBLE);

					if (logger.isDebugEnabled())
						logger.debug("addVisibilityListener: Invoked asyncInitStatus()"); //$NON-NLS-1$
				}
			});
	}

	/**
	 * Remove a PartVisibilityListener listener for given part.
	 */
	public void removeVisibilityListener(IWorkbenchPart part, PartVisibilityListener listener) {
		PartStatus status = getPartStatus(part);
		if (status == null) {
			logger.warn("Autocreation of PartStatus failed status is null, can not remove Listener "+listener); //$NON-NLS-1$
			return;
		}
		status.removeListener(listener);
	}
	
	/**
	 * Get the visibility status of the given part. Will return one out of
	 * {@link #PART_STATUS_VISIBLE} or {@link #PART_STATUS_HIDDEN}.
	 *  
	 * @param part The part the visibilty should be returned
	 */
	public int getPartVisibilityStatus(IWorkbenchPart part) {
		int res;
		PartStatus status = getPartStatus(part);
		if (status != null)
			res = status.getStatus();
		else
			res = PART_STATUS_HIDDEN;

		if (logger.isDebugEnabled())
			logger.debug("getPartVisibilityStatus: part=" + part + " status=" + res); //$NON-NLS-1$ //$NON-NLS-2$

		return res;
	}
	
	private boolean initialized = false;
	public void initialize() {
		if (initialized) {
			if (logger.isDebugEnabled())
				logger.debug("initialize: already initialized. Exiting."); //$NON-NLS-1$

			return;
		}
		if (RCPUtil.getActiveWorkbenchPage() == null) {
			logger.warn("initialize: RCPUtil.getActiveWorkbenchPage() == null! Will defer initialization!"); //$NON-NLS-1$
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					initialize();
				}
			});
			return;
		}
		if (logger.isDebugEnabled())
			logger.debug("initialize: registering PartListener"); //$NON-NLS-1$

		Listener listener = new Listener(this);
		RCPUtil.getActiveWorkbenchPage().addPartListener(listener);
		initialized = true;
	}


	private static PartVisibilityTracker sharedInstance;
	
	/**
	 * Shared instance of PartVisibilityTracker. Should be initialized once.
	 * @see #initialize() 
	 */
	public static PartVisibilityTracker sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new PartVisibilityTracker();
		return sharedInstance;
	}
	
	/**
	 * Check whether the given part is visible.
	 * Be sure that at some point of time {@link #initialize()} was called before
	 * you call this method.  
	 * Calls {@link #getPartVisibilityStatus(IWorkbenchPart)} for the sharedInstance.
	 */
	public static boolean isPartVisible(IWorkbenchPart part) {
		if (RCPUtil.getActiveWorkbenchPage().isPartVisible(part))
			return true;
		
		return sharedInstance().getPartVisibilityStatus(part) == PART_STATUS_VISIBLE;
	}
	
	
	
}
