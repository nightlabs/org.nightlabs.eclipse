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
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.nightlabs.base.composite.FadeableComposite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.util.RCPUtil;

/**
 * A PartController handles the visibility of a set of WorkbenchParts. It accepts 
 * {@link org.nightlabs.base.part.ControllablePart}s whose real content
 * can be hidden by the Controller. When hidden the real contents can 
 * either be sent to background or disposed 
 * (see {@link #updateParts()}, {@link #disposePartContents()}).
 * <p>
 * The visibility of a part id determined by their 
 * {@link org.nightlabs.base.part.ControllablePart#canDisplayPart()} method.
 * <p>
 * Implementors have to provide a Composite that will be displayed when
 * the real contents are hidden.
 * <p>
 * Parts are managed when they register themselves by {@link #registerPart(ControllablePart)}.
 * The registratin of a Part should be done in its constructor. See {@link org.nightlabs.base.part.ControllablePart}
 * for more information on how to use them with a PartController. 
 * <p>
 * A PartController is responsible for somehow creating or listening to event 
 * that might cause the visibility of its registered parts to change and 
 * use the PartController API like {@link #updateParts()} to update the registered views.
 * <p>
 * Note that PartController is also linked to the {@link PartVisibilityTracker}. Whenever the registered
 * View's real content was created it will call {@link PartVisibilityListener#partVisible(org.eclipse.ui.IWorkbenchPartReference)}
 * on all registrations that implement {@link PartVisibilityListener}. {@link PartVisibilityListener#partHidden(org.eclipse.ui.IWorkbenchPartReference)}
 * is called accordingly when the "ConditionUnsatisfiedComposite" is created. Both times the 
 * partReference parameter for the listeners callback method might be null.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @see #createNewConditionUnsatisfiedComposite(Composite)
 * @see org.nightlabs.base.part.ControllablePart
 */
public abstract class PartController {

	protected class ControlledPart {
		
		private ControllablePart part;
		
		private StackLayout stackLayout;
		private XComposite wrapper;
		private FadeableComposite fadeableWrapper;
		private XComposite conditionWrapper;
		
		private boolean contentsCreated = false;
		private boolean internalPartsCreated = false;
		
		public ControlledPart(ControllablePart part) {
			this.part = part;
		}

		private Layout fadableLayout = XComposite.getLayout(XComposite.LayoutMode.TIGHT_WRAPPER);
		public ControlledPart(ControllablePart part, Layout layout) {
			this.part = part;
			this.fadableLayout = layout;
		}
				
		/**
		 * Creates the internal Controls hosting the real part
		 * content.
		 *  
		 * @param parent
		 */
		public void createPartControl(Composite parent) {
			wrapper = new XComposite(parent, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
			stackLayout = new StackLayout();
			wrapper.setLayout(stackLayout);
			conditionWrapper = new XComposite(wrapper, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
			createNewConditionUnsatisfiedComposite(conditionWrapper);
			createFadeableWrapper();
			internalPartsCreated = true;			
		}
		
		public XComposite getWrapper() {
			return wrapper;
		}
		
		protected void createFadeableWrapper() {
			fadeableWrapper = new FadeableComposite(wrapper, SWT.NONE);
//			fadeableWrapper.setLayout(new GridLayout());
			fadeableWrapper.setLayout(fadableLayout);			
		}
		
		/**
		 * Updates the part according to the current login-state.
		 */
		protected void updatePart() {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (!internalPartsCreated)
						return;
					doCreateContents();
					if (part.canDisplayPart()) { 
						stackLayout.topControl = fadeableWrapper;
						setViewActionsVisible(true);
						callPartVisibilityListener(part, PartVisibilityListenerMethod.partVisible);
					}
					else { 
						stackLayout.topControl = conditionWrapper;
						setViewActionsVisible(false);
						callPartVisibilityListener(part, PartVisibilityListenerMethod.partHidden);
					}
					wrapper.layout(true, true);
					
				}
			});
		}
		
		/**
		 * Creates the View contents if possible
		 * and neccesary
		 */
		protected void doCreateContents() {
			if (!contentsCreated) {
				if (part.canDisplayPart()) {
					part.createPartContents(fadeableWrapper);
					contentsCreated = true;
				}
			}
		}
		
		/**
		 * Disposes and recreates the fadeable
		 * wrapper for the real part content. 
		 */
		protected void disposePartContents() {
			if (!internalPartsCreated)
				return;
			fadeableWrapper.dispose();
			createFadeableWrapper();
			contentsCreated = false;
		}
		
		/**
		 * Show/Hide all Viewactions of a View if the {@link ControllablePart} is a {@link IViewPart}
		 * @param visible
		 */
		protected void setViewActionsVisible(boolean visible) 
		{
			if (part instanceof IViewPart) {
				RCPUtil.setViewActionsVisible((IViewPart)part, visible);				
			}
		}
		
		
	}
	
	/**
	 * Sublcasses should create a new Composite indicating that the conditions
	 * neccessary to show a View were not satisfied.
	 * 
	 * @param parent The parent the Composite shoulb be created for
	 * @return A new Composite
	 */
	protected abstract Composite createNewConditionUnsatisfiedComposite(Composite parent);
	
	/**
	 * key: ControllablePart part
	 * value: ControllePart controlledView
	 */
	private Map<ControllablePart, ControlledPart> controlledParts = new HashMap<ControllablePart, ControlledPart>();
	
	/**
	 * Update all registered Views according to their satus returned by
	 * {@link ControllablePart#canDisplayPart()}.
	 * 
	 * @param part
	 */
	public void updatePart(ControllablePart part) {
		ControlledPart controlledPart = (ControlledPart)controlledParts.get(part);
		controlledPart.updatePart();
	}

	/**
	 * Get the ControllePart for the given ControllablePart if registrered.
	 * Will return null otherwise
	 */
	protected ControlledPart getControlledPart(ControllablePart part) {
		return (ControlledPart)controlledParts.get(part);
	}

	/**
	 * Update all registered WorkbenchParts according to their satus returned by
	 * {@link ControllablePart#canDisplayPart()}
	 */
	public void updateParts() {
		for (Iterator iter = controlledParts.keySet().iterator(); iter.hasNext();) {
			ControllablePart part = (ControllablePart) iter.next();
			updatePart(part);
		}
	}
	
	/**
	 * Dispose the real contents of the given WorkbenchPart.
	 */
	public void disposePartContents(ControllablePart part) {
		ControlledPart controlledPart = getControlledPart(part);
		if (controlledPart != null)
			controlledPart.disposePartContents();
	}
	
	/**
	 * Dispose the real contents of all registered WorkbenchParts.
	 */
	protected void disposePartContents() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				for (Iterator iter = controlledParts.keySet().iterator(); iter.hasNext();) {
					ControllablePart part = (ControllablePart) iter.next();
					disposePartContents(part);
				}
			}
		});
	}
	
	/**
	 * Register a part to this controller.
	 * When done so make sure that the createPartControl(Composite)
	 * method of the passed WorkbenchPart is delegated to 
	 * this Controllers {@link #createPartControl(ControllablePart, Composite)} method.
	 * 
	 * @param part The part to be controlled by this Controller.
	 */
	public void registerPart(ControllablePart part) {
		ControlledPart controlledPart = new ControlledPart(part);
		controlledParts.put(part, controlledPart);
	}

	/**
	 * Register a part to this controller and determines which layout is used
	 * When done so make sure that the createPartControl(Composite)
	 * method of the passed WorkbenchPart is delegated to 
	 * this Controllers {@link #createPartControl(ControllablePart, Composite)} method.
	 * 
	 * @param part The part to be controlled by this Controller.
	 */
	public void registerPart(ControllablePart part, Layout layout) {
		ControlledPart controlledPart = new ControlledPart(part, layout);
		controlledParts.put(part, controlledPart);
	}
	
	/**
	 * Unregister a controlled WorkbenchPart from the Controller.
	 * 
	 * @param part The WorkbenchPart to unregister.
	 */
	public void unregisterPart(ControllablePart part) {
		controlledParts.remove(part);
	}
	
	/**
	 * Should be called by controlled WorkbenchPart in their 
	 * createPartControl(Composite) method.
	 * 
	 * @param part The controlled part.
	 * @param parent The parent passed to the createPartControl() method.
	 */
	public void createPartControl(final ControllablePart part, Composite parent) {
		ControlledPart controlledPart = getControlledPart(part);
		if (controlledPart == null)
			throw new IllegalStateException("The ControlledPart instance of part "+part+"("+part.getClass().getName()+") could not be found. Maybe it was not registered. Use PartController#registerPart() prior to this method."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (part != null) {
			controlledPart.createPartControl(parent);
			controlledPart.getWrapper().addDisposeListener(
					new DisposeListener() {
						public void widgetDisposed(DisposeEvent e) {
							unregisterPart(part);
						}
					}
			);
			controlledPart.updatePart();
		}
	}
	
	private enum PartVisibilityListenerMethod {
		partVisible,
		partHidden
	}
	
	private void callPartVisibilityListener(ControllablePart listenerPart, PartVisibilityListenerMethod listenerMethod) {
		if (listenerPart instanceof IWorkbenchPart) {
			int partStatus = PartVisibilityTracker.sharedInstance().getPartVisibilityStatus((IWorkbenchPart)listenerPart);
			if (partStatus != PartVisibilityTracker.PART_STATUS_HIDDEN)
				if (listenerPart instanceof PartVisibilityListener) {
					IWorkbenchPartReference partRef = RCPUtil.searchPartReference((IWorkbenchPart)listenerPart);						
					if (listenerMethod == PartVisibilityListenerMethod.partVisible)
						((PartVisibilityListener)listenerPart).partVisible(partRef);
					if (listenerMethod == PartVisibilityListenerMethod.partHidden)
						((PartVisibilityListener)listenerPart).partHidden(partRef);
				}
		}
	}
}
