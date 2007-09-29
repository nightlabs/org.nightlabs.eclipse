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

package org.nightlabs.base.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.composite.ChildStatusController;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.util.IOUtil;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class RCPUtil
{
	static {
		Display.getDefault().addFilter(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event)
			{
				switch (event.keyCode) {
					case SWT.CTRL:
						keyDown_Ctrl = true;
						break;
					case SWT.SHIFT:
						keyDown_Shift = true;
						break;
				}
			}
		});
		Display.getDefault().addFilter(SWT.KeyUp, new Listener() {
			public void handleEvent(Event event)
			{
				switch (event.keyCode) {
					case SWT.CTRL:
						keyDown_Ctrl = false;
						break;
					case SWT.SHIFT:
						keyDown_Shift = false;
						break;
				}
			}
		});
	}

	public static final int KEY_CTRL = SWT.CTRL;
	public static final int KEY_SHIFT = SWT.CTRL;

	private static boolean keyDown_Ctrl = false;
	private static boolean keyDown_Shift = false;

	public static boolean isKeyDown(int key)
	{
		switch (key) {
			case SWT.CTRL:
				return keyDown_Ctrl;
			case SWT.SHIFT:
				return keyDown_Shift;
			default:
				throw new IllegalArgumentException("Unknown key! Use one of the KEY_* constants!"); //$NON-NLS-1$
		}
	}

	/**
	 * Recursively sets the enabled flag for the given Composite and all its children.
	 * <p>
	 * <b>Important:</b> It is highly recommended to extend your containers in a way that they
	 * automatically do this and when re-enabling the container do <b>not</b> enable child-elements
	 * that where disabled before. The {@link XComposite} already implements this behaviour (just
	 * like other NightLabs-UI elements as well). In order to implement this behaviour yourself,
	 * you should use a {@link ChildStatusController}.
	 * </p>
	 * 
	 * @param comp The parent control
	 * @param enabled The enabled flag to set
	 */
	public static void setControlEnabledRecursive(Composite comp, boolean enabled) {
		comp.setEnabled(enabled);
		Control[] children = comp.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Composite)
				setControlEnabledRecursive((Composite)children[i], enabled);
		}
	}

	/**
	 * Sets the given button selected if it is contained in the given parent Composite,
	 * and deselects all other buttons in this Composite
	 * 
	 * @param parent the parent Composite
	 * @param button a Button with style Param SWT.TOGGLE or SWT.RADIO which should be selected
	 */
	public static void setButtonSelected(Composite parent, Button button) 
	{
	  button.setSelection(true);
	  Control[] children = parent.getChildren();
	  for (int i = 0; i < children.length; i++) {
	    if (!children[i].equals(button)) {
	      if ( (((children[i].getStyle() & SWT.TOGGLE) != 0) || ((children[i].getStyle() & SWT.RADIO) != 0))  
	          && (children[i] instanceof Button) ) 
	      {
	        ((Button)children[i]).setSelection(false);
	      }
	    } 
	  }
	}
	
	/**
	 * Returns wether the ViewPart with the given id is currently visble in
	 * one of the pages of the active Workbench window. Will also return 
	 * true when the page-book containing this view is minimized.
	 * 
	 * @param viewID The id of the view to be queried
	 * @return Wether the view is visible
	 */
	public static boolean isViewVisible(String viewID) {
//		IWorkbenchPage[] pages = Workbench.getInstance().getActiveWorkbenchWindow().getPages();
		IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
		for (int i = 0; i < pages.length; i++) {
			IWorkbenchPart part = pages[i].findView(viewID);
			if (part != null) {
				return isPartVisible(part);
			}
		}
		return false;
	}
	
	/**
	 * Show/Hide all ViewActions of the given View.
	 * 
	 * @param view The View which ViewActions should be shown/hidden
	 * @param visible true to show all Actions, fals to hide them 
	 */
	public static void setViewActionsVisible(IViewPart view, boolean visible) {
		IToolBarManager toolBarManager = view.getViewSite().getActionBars().getToolBarManager();
		IMenuManager menuManager = view.getViewSite().getActionBars().getMenuManager();
		IContributionItem[] tItems = toolBarManager.getItems();
		for (int i = 0; i < tItems.length; i++) {
			tItems[i].setVisible(visible);
			tItems[i].update();
		}
		IContributionItem[] mItems = menuManager.getItems();
		for (int i = 0; i < mItems.length; i++) {
			mItems[i].setVisible(visible);
			mItems[i].update();
		}
		toolBarManager.update(true);
		menuManager.update(true);
	}

	/**
	 * Returns wether the given IWorkbenchPart is currently visble in
	 * one of the pages of the active Workbench window. Will also return 
	 * true when the page-book containing this view is minimized.
	 * 
	 * @param part The part to check
	 * @return Wether the view is visible
	 */
	public static boolean isPartVisible(IWorkbenchPart part) {
		boolean visible = false;
		IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
		for (int i = 0; i < pages.length; i++) {
			if (part != null)
				if (pages[i].isPartVisible(part)){
					visible = true;
				}
		}		
		return visible;
	}
	
	/**
	 * Shows the view with the given viewID in all workbench-pages
	 * 
	 * @param viewID The id of the view to be queried
	 * @return Wether the view is visible
	 */
	public static IWorkbenchPart showView(String viewID) {
		IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
		for (int i = 0; i < pages.length; i++) {
			IWorkbenchPart view = null;
			try { view = pages[0].showView(viewID); } catch (PartInitException e) { throw new RuntimeException(e); }
			if (view != null) 
				return view;
		}
		return null;
	}
	
	/**
	 * Shows the view with the given viewID and 
	 * gives it focus.
	 * 
	 * @param viewID The id of the view to be queried
	 * @return Wether the view is visible
	 */
	public static IWorkbenchPart activateView(String viewID) {
		IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
		for (int i = 0; i < pages.length; i++) {
			IWorkbenchPart view = null;
			try { view = pages[0].showView(viewID); } catch (PartInitException e) { throw new RuntimeException(e); }
			if (view != null) {
				pages[0].activate(view);
				return view;
			}
		}
		return null;
	}

	/**
	 * @deprecated Use {@link #getActiveWorkbenchShell()} instead!
	 */
	public static Shell getWorkbenchShell() {
		return getActiveWorkbenchShell();
	}

	/**
	 * Returns the active WorkbenchWindow's shell.
	 * @return The active WorkbenchWindow's shell.
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		return window == null ? null : window.getShell();
	}

	/**
	 * Returns the active WorkbenchWindow's active page.
	 * @return The active WorkbenchWindow's active page.
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * @deprecated Use {@link #getActiveWorkbenchPage()} instead!
	 */
	public static IWorkbenchPage getWorkbenchPage() {
		return getActiveWorkbenchPage();
	}

	/**
	 * Returns the active WorkbenchWindow's active page.
	 * @return The active WorkbenchWindow's active page.
	 */
	public static IWorkbenchPage getActiveWorkbenchPage() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		return window == null ? null : window.getActivePage();
	}

	/**
	 * returns the id of the current perspective
	 * @return the id of the current perspective
	 */
	public static String getActivePerspectiveID() {
		IWorkbenchPage page = getActiveWorkbenchPage();
		return page == null ? null : page.getPerspective().getId();		
	}

	/**
	 * opens a ErrorDialog with the given message 
	 * 
	 * @param message the message to display
	 * @param buttonStyle the buttonStyle
	 * @return the returnCode of the Dialog
	 */
	public static void showErrorDialog(String message) 
	{
		MessageDialog.openError(getActiveWorkbenchShell(), Messages.getString("org.nightlabs.base.util.RCPUtil.showErrorDialog.title"), message); //$NON-NLS-1$
	}

	/**
	 * opens a MessageBox which asks the user if he want to overwrite the file,
	 * with the given fileName
	 * 
	 * @param fileName the name of the file
	 * @return the returnCode of the Dialog
	 */
	public static boolean showConfirmOverwriteDialog(String fileName) 
	{
		return MessageDialog.openConfirm(
				getActiveWorkbenchShell(), 
				Messages.getString("org.nightlabs.base.util.RCPUtil.showConfirmOverwriteDialog.title"), //$NON-NLS-1$
				String.format(Messages.getString("org.nightlabs.base.util.RCPUtil.showConfirmOverwriteDialog.message"), new Object[] { fileName }) //$NON-NLS-1$
			);
	}
	
	/**
	 * disposes the given Composite with all of its children
	 * 
	 * @param comp the Composite to dispose with all of its children
	 */
	public static void disposeAllChildren(Composite comp) 
	{
		if (comp != null) 
		{
			if (!comp.isDisposed()) {
				Control[] children = comp.getChildren();
				for (int i=0; i<children.length; i++) {
					Control c = children[i];
					c.dispose();
				}				
			}
		}
	}
  
	/**
	 * Opens an editor with the given input and editorID and returns it.
	 * 
	 * @param input The editors input
	 * @param editorID The editors id 
	 * @return The editor opened
	 * @throws PartInitException
	 */
	public static IEditorPart openEditor(IEditorInput input, String editorID) 
	throws PartInitException 
	{
		return getActiveWorkbenchPage().openEditor(input, editorID);
	}
  
	/**
	 * Finds the editor for the given input in the workbench's 
	 * active workbenchpage. Returns null if no editor for
	 * the given input was found.
	 *
	 * @param input The input for which to search a currently open editor. 
	 */
	public static IEditorPart findEditor(IEditorInput input) {
		return getActiveWorkbenchPage().findEditor(input);
	}

	/**
	 * If there is an open editor for the given <code>input</code>,
	 * it will be closed. If no editor can be found, this
	 * method is a no-op.
	 *
	 * @param input The input specifying the editor to close.
	 * @param save Whether or not to save - this is passed to {@link IWorkbenchPage#closeEditor(IEditorPart, boolean)}.
	 * @return <code>true</code>, if no open editor was found or if it has been successfully closed.
	 *		<code>false</code>, if the open editor for the given <code>input</code> was not closed (e.g. because
	 *		the user cancelled closing in case <code>save == true</code>).
	 */
	public static boolean closeEditor(IEditorInput input, boolean save) {
		IWorkbenchPage page = getActiveWorkbenchPage();
		IEditorPart editor = page.findEditor(input);
		if (editor == null)
			return true;

		return page.closeEditor(editor, save);
	}

	/**
	 * Finds the view with the given id in the workbench's
	 * active workbenchpage. Returns null if no view for the
	 * given viewID was found.
	 *
	 * @param viewID The id of the view.
	 */
	public static IViewPart findView(String viewID)
	{
		return getActiveWorkbenchPage().findView(viewID);
	}

	/**
	 * 
	 * @param bounds the bounds of a Control
	 * @return the Point which determines the Location so that the given Bounds are
	 * centered on the screen 
	 */
	public static Point getCenterPosition(Rectangle bounds) 
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = 0;
		int y = 0;
		if (bounds.width < screenSize.getWidth())
			x = (((int)screenSize.getWidth()) - bounds.width) / 2; 
		if (bounds.height < screenSize.getHeight())
			y = (((int)screenSize.getHeight()) - bounds.height) / 2;
		
		return new Point(x,y);
	}

	/**
	 * Adds all known perspectives as perspective shortcuts to the given
	 * layout.
	 */
	public static void addAllPerspectiveShortcuts(IPageLayout layout)
	{
		IPerspectiveDescriptor[] perspectives = PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives();
		for (int i = 0; i < perspectives.length; i++)
			layout.addPerspectiveShortcut(perspectives[i].getId());
//		IConfigurationElement[] configPerspectives =  Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.ui.perspectives");
//		for(int i = 0; i < configPerspectives.length; i++)
//			layout.addPerspectiveShortcut(configPerspectives[i].getAttribute("id"));
	}
	
	/**
	 *  
	 * @param comp the Composite to set the Form Border for
	 * @see FormToolkit#paintBordersFor(Composite)
	 */
	public static void setFormBorder(Composite comp) 
	{
		comp.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
	}
	
	/**
	 * sets the location of a dialog so that it apperas in the center of the screen 
	 * @param d the Dialog to center
	 */
	public static void centerDialog(Dialog d) 
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point shellSize = d.getShell().getSize();
		int diffWidth = screenSize.width - shellSize.x;
		int diffHeight = screenSize.height - shellSize.y;
		d.getShell().setLocation(diffWidth/2, diffHeight/2);
	}
	
  /**
   * checks if a IMenuManager with the given ID is contained in 
   * the given IMenuManager and returns it. 
   * 
   * @param id the ID of the ContributionItem
   * @param menuMan the MenuManager to search in
   * @return the ContributionItem with the given ID or null if not contained
   */
  public static IContributionItem getMenuItem(String id, IMenuManager menuMan) 
  {
  	if (menuMan != null) {
    	IContributionItem[] menuItems = menuMan.getItems();
    	for (int i=0; i<menuItems.length; i++) {
    		IContributionItem menuItem = menuItems[i];
    		if (menuItem != null && menuItem.getId() != null) {
      		if (menuItem.getId().equals(id)) 
      			return menuItem;  			
    		}
    	}  		
  	}
  	return null;
  }	
  
  /**
   * Returns the either the active {@link IWorkbenchPage}
   * or the first found. If none can be found <code>null</code>
   * will be returned.
   * 
   * @return An {@link IWorkbenchPage} or null
   */
  public static IWorkbenchPage searchWorkbenchPage() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window == null)
			return null;
		IWorkbenchPage[] pages = window.getPages();
		if (pages.length > 0)
			return pages[0];
		else
			return null;			             
  }
  
  /**
   * Tries to find a reference for the given part somewhere in the Workbench and returns it.
   * If a reference can not be found <code>null</code> will be returned.
   * 
   * @param part The part to search a reference for
   */
  public static IWorkbenchPartReference searchPartReference(IWorkbenchPart part) {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window == null)
			return null;
		IWorkbenchPage[] pages = window.getPages();
		for (int i = 0; i < pages.length; i++) {
			IWorkbenchPartReference ref = pages[i].getReference(part);
			if (ref != null)
				return ref;
		}
		return null;
  }
	
  /**
   * logs all parents and its layoutData of the given control to the given logger
   * 
   * @param control the {@link Control} to log its parents
   * @param logger the logger to log
   * @param logLevel the logLevel to use
   */
	public static void logControlParents(Control control, Logger logger, Level logLevel) 
	{
		Composite parent = control.getParent();
		if (parent != null) 
		{
			logger.log(logLevel, "control = "+control); //$NON-NLS-1$
			logger.log(logLevel, "control.getLayoutData() = "+control.getLayoutData());			 //$NON-NLS-1$
			logger.log(logLevel, "parent = "+parent);			 //$NON-NLS-1$
			logger.log(logLevel, "parent.getLayout() = "+parent.getLayout());			 //$NON-NLS-1$
			logControlParents(parent, logger, logLevel);
		}
	}  
	
	
	private static IProgressMonitor nullMonitor = new NullProgressMonitor();

	/**
	 * Checks the given monitor and returns it if not <code>null</code>. If
	 * the given monitor is null an instance of {@link NullProgressMonitor}
	 * will be returned.
	 * 
	 * @param monitor The monitor to check
	 */
	public static IProgressMonitor getSaveProgressMonitor(IProgressMonitor monitor) {
		if (monitor != null)
			return monitor;
		return nullMonitor;		
	}
	
	public static boolean isDisplayThread() {
		return Display.getDefault().getThread().equals(Thread.currentThread());
	}

	/**
	 * This method is a convenience method calling
	 * <code>ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString()</code>
	 *
	 * @return Returns a {@link File} instance pointing to the workspace root directory.
	 */
	public File getResourcesWorkspace()
	{
		return new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());
	}
	
	/**
	 * Returns a {@link File} representation of the given {@link IResource}.
	 * 
	 * @param resource The resource to get the {@link File} from.
	 * @return A {@link File} representation of the given {@link IResource}.
	 */
	public static File getResourceAsFile(IResource resource) {
		return new File(resource.getWorkspace().getRoot().getLocation().toFile(), resource.getFullPath().toOSString());
	}
	
	/**
	 * Sets the font of the given Control to its old font adding/removing the given styles.
	 * So, for example, to maket the text bold do:
	 * <pre>
	 * RCPUtil.setControlFontStyle(myControl, SWT.BOLD, 0);
	 * </pre>
	 * Styles are first added then removed.
	 * 
	 * @param control The {@link Control} to change the font of.
	 * @param addStyle The style flag(s that should be added to the controls actual font.
	 * @param removeStyle The style flag(s) that should be removed from the contros actual font.
	 */
	public static void setControlFontStyle(Control control, int addStyle, int removeStyle) {
		Font oldFont = control.getFont();
		int newStyle = oldFont.getFontData()[0].getStyle() | addStyle;
		newStyle = newStyle & (~removeStyle);
		final Font newFont = new Font(
				oldFont.getDevice(), 
				oldFont.getFontData()[0].getName(), 
				oldFont.getFontData()[0].getHeight(), 
				newStyle
			);
		control.setFont(newFont);
		control.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				newFont.dispose();
			}			
		});
	}
	
	/**
	 * clears the workspace folder
	 * @param ask determines if the user should be asked before
	 */
	public static void clearWorkspace(boolean ask) 
	{
		if (ask) {
			boolean ok = MessageDialog.openConfirm(RCPUtil.getActiveWorkbenchShell(), 
					Messages.getString("org.nightlabs.base.util.RCPUtil.clearWorkspace.title"), Messages.getString("org.nightlabs.base.util.RCPUtil.clearWorkspace.message")); //$NON-NLS-1$ //$NON-NLS-2$
			if (!ok)
				return;
		}
		File workspace = Platform.getLocation().toFile();
		IOUtil.deleteDirectoryRecursively(workspace);		
	}
	
	/**
	 * Adds a new {@link org.eclipse.ui.internal.layout.IWindowTrim} 
	 * to the {@link org.eclipse.ui.internal.layout.TrimLayout} of the given shell
	 * and prepends it to the the trim with the given id (pependTo). 
	 * The new trim  will be filled with the contents of the given contributionItem.
	 * 
	 * @param shell The shell to add the trim to.
	 * @param contributionItem The contributionItem to fill the trim with.
	 * @param prependTo The id of the trim the new trim should be prepended to.
	 */
	@SuppressWarnings("restriction") //$NON-NLS-1$
	public static void addContributionItemTrim(
			Shell shell, 
			IContributionItem contributionItem, 
			String prependTo
	) {
		if (shell != null && (shell.getLayout() instanceof org.eclipse.ui.internal.layout.TrimLayout)) {
			// This is how the WorkbenchWindow add the progress and heapstatus controls
			// can't be that wrong :-)
			org.eclipse.ui.internal.layout.TrimLayout layout = (org.eclipse.ui.internal.layout.TrimLayout) shell.getLayout();
			Composite comp = new Composite(shell, SWT.NONE);
			contributionItem.fill(comp);
			org.eclipse.ui.internal.WindowTrimProxy trimProxy = new org.eclipse.ui.internal.WindowTrimProxy(
					comp,
					contributionItem.getId(), //$NON-NLS-1$
					contributionItem.getClass().getSimpleName(), SWT.BOTTOM | SWT.TOP
			) {

				public void handleClose() {
					getControl().dispose();
				}

				public boolean isCloseable() {
					return true;
				}
			};
			org.eclipse.ui.internal.layout.IWindowTrim prependTrim = layout.getTrim(prependTo);
			trimProxy.setWidthHint(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
			trimProxy.setHeightHint(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			layout.addTrim(SWT.BOTTOM, trimProxy, prependTrim);

			comp.setVisible(true);
		}
	}

	
}
