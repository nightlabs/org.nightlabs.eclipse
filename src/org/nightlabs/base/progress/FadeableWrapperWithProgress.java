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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.composite.Fadeable;
import org.nightlabs.base.composite.FadeableComposite;
import org.nightlabs.base.composite.XComposite;

/**
 * {@link Fadeable} that can switch between a view of an {@link IProgressMonitor}
 * and custom content of the composite.
 * 
 * You can use this as parent for your WorkbenchParts or even in custom Composites.
 * 
 * When using the {@link FadeableWrapperWithProgress} remember not to pass the actual
 * instance to child Controls, but {@link #getContent()}. 
 * 
 * If you subclass {@link FadeableWrapperWithProgress} ther exist several 
 * callback-methods to configure the look of the wrapper. 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class FadeableWrapperWithProgress extends FadeableComposite {

	/**
	 * Enum for the possible view modes of an
	 * {@link FadeableWrapperWithProgress}.
	 */
	public enum ViewMode {
		/**
		 * See the contents of the Control
		 */
		CONTENT,
		/**
		 * See the progess monitor
		 */
		PROGESS
	}
	
	/**
	 * The stack layout to switch views between progress and content 
	 */
	private StackLayout stackLayout;
	/**
	 * Wrapper for the progress monitor
	 */	
	protected XComposite progressWrapper;
	/**
	 * The progress monitor (used implementation {@link SaveProgressMonitorPart}).
	 */
	protected IProgressMonitor progressMonitorPart;
	/**
	 * The composite used to wrap the real contents
	 */
	protected XComposite contentWrapper;
	
	/**
	 * Create a new fadeable wrapper (witch {@link LayoutMode#TIGHT_WRAPPER})
	 * and the default initial {@link ViewMode} (= {@link ViewMode#PROGESS}).
	 * 
	 * @param parent The parent to use.
	 * @param style The style to use for the wrapper itself.
	 */
	public FadeableWrapperWithProgress(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		initGUI(null);
	}

	/**
	 * Create a new fadeable wrapper with the given {@link ViewMode}
	 * as initial view.
	 * 
	 * @param parent The parent to use.
	 * @param style The style for the wrapper itself.
	 * @param layoutMode The LayoutMode of the Wrapper itself.
	 * @param initialViewMode The initial {@link ViewMode} for this wrapper.
	 */
	public FadeableWrapperWithProgress(Composite parent, int style,
			LayoutMode layoutMode, ViewMode initialViewMode) {
		super(parent, style, layoutMode);
		initGUI(initialViewMode);
	}

	/**
	 * Creates the StackLayout and calls the different callback methods.
	 * 
	 * @param initViewMode The initial {@link ViewMode}
	 */
	protected void initGUI(ViewMode initViewMode) {
		stackLayout = new StackLayout();
		stackLayout.marginHeight = 0;
		stackLayout.marginWidth = 0;
		setLayout(stackLayout);
		
		progressWrapper = new XComposite(this, SWT.NONE, XComposite.LayoutMode.ORDINARY_WRAPPER);
		configureProgressWrapper(progressWrapper);		
		progressMonitorPart = createProgressMonitorPart(progressWrapper);
		
		contentWrapper = createContentWrapper(this);
		
		updateStackLayout(initViewMode == null ? ViewMode.PROGESS : initViewMode);		
	}

	/**
	 * Callback method to configure the Composite wrapping the progress monitor.
	 * 
	 * @param progressWrapper the Composite wrapping the progress monitor.
	 */
	protected void configureProgressWrapper(XComposite progressWrapper) {
		progressWrapper.getGridLayout().marginLeft = 5;
	}

	/**
	 * Callback method to create the progress monitor.
	 * The default implementation will return an {@link SaveProgressMonitorPart}.
	 * 
	 * @param parent The parent to use for the progress monitor
	 * @return A new implementation of {@link IProgressMonitor} that should have its visual representation as children of the given parent parameter.
	 */
	protected IProgressMonitor createProgressMonitorPart(Composite parent) {
		return new SaveProgressMonitorPart(parent, null);
	}
	
	/**
	 * Callback method to create the Composite wrapping the actual content.
	 * This is what will be returned as {@link #getContent()}.
	 * The defauld implementation will create an ordinary wrapping XComposite.
	 *
	 * @param parent The contens wrapper parent (Most likely the fadeable itself)
	 * @return A new {@link XComposite} that will be used as wrapper for the real content.
	 */
	protected XComposite createContentWrapper(Composite parent) {
		return new XComposite(parent, SWT.NONE, XComposite.LayoutMode.ORDINARY_WRAPPER);
	}

	/**
	 * Returns the Composite wrapping the real contents of this fadeable wrapper.
	 * Use this when creating as parameter for child composites.
	 *  
	 * @return the Composite wrapping the real contents of this fadeable wrapper.
	 */
	public XComposite getContent() {
		return contentWrapper;
	}

	/**
	 * Updates the stack layout according tho the given view mode.
	 * @param viewMode The view mode to switch to
	 */
	private void updateStackLayout(ViewMode viewMode) {
		switch (viewMode) {
			case PROGESS:
				stackLayout.topControl = progressWrapper; break;
			case CONTENT:
				stackLayout.topControl = progressWrapper; break;
		}
	}

	/**
	 * Class used internally to switch the stack.
	 */
	private static class SwitchViewRunnable implements Runnable {
		
		ViewMode viewMode;
		FadeableWrapperWithProgress wrapper;
		
		public SwitchViewRunnable(FadeableWrapperWithProgress wrapper, ViewMode viewMode) {
			this.wrapper = wrapper;
			this.viewMode = viewMode;
		}
		
		public void run() {
			wrapper.updateStackLayout(viewMode);
			wrapper.layout(true, true);
		}
		
	}
	
	/**
	 * Get a runnable that will switch this wrapper
	 * to view the given view mode.
	 * 
	 * @param viewMode The view mode to switch to
	 */
	public Runnable getSwitchViewRunnable(ViewMode viewMode) {
		return new SwitchViewRunnable(this, viewMode);
	};
	
	/**
	 * Switch to view the page's content.
	 * Note that this will be called asynchronously on the {@link Display} thread, so 
	 * it will have to wait if the {@link Display} thread is busy.
	 */
	public void switchView(final ViewMode viewMode) {
		Display.getDefault().syncExec(getSwitchViewRunnable(viewMode));
	}
	
	/**
	 * Switch the stack layout to view the specified view mode.
	 * Note that this will be called asynchronously on the {@link Display} thread.
	 */
	public void asyncSwitchView(final ViewMode viewMode) {
		Display.getDefault().asyncExec(getSwitchViewRunnable(viewMode));
	}
	
}
