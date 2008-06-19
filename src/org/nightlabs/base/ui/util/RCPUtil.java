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

package org.nightlabs.base.ui.util;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

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
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
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
import org.eclipse.ui.internal.Workbench;
import org.nightlabs.base.ui.form.AbstractBaseFormPage;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.util.IOUtil;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Fitas Amine - fitas at nightlabs dot de
 */
public class RCPUtil
{
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


	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger( RCPUtil.class);


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
	 * @return buffered image of the current screen
	 */
	public static BufferedImage takeScreenShot() throws AWTException {
		BufferedImage screenShot = null;
//		take a screen shot and save a temp file on disk and then send it by email
		Robot robot;
		org.eclipse.swt.graphics.Rectangle rect;

		Vector xCollect = new Vector();
		Vector yCollect = new Vector();
		int minX = 0;
		int minY = 0;
		int maxX;
		int maxY;
		int addwidth = 0;
		int addheight = 0;

		Display display = Display.getCurrent();
		if (display == null)
			throw new IllegalStateException("This method must be called on the SWT UI thread!");

		/*
		 * find the area rectangle of the screen that contains all the shell
		 * */


		for (Shell shell : display.getShells())
		{
			shell.redraw();
			xCollect.add(new Integer(shell.getBounds().x));
			yCollect.add(new Integer(shell.getBounds().y));
		}

		if(!xCollect.isEmpty())
		{
			minX  = (Integer)(Collections.min(xCollect));
			minY  = (Integer)(Collections.min(yCollect));
			maxX  = (Integer)(Collections.max(xCollect));
			maxY  = (Integer)(Collections.max(yCollect));

			for (Shell shell : display.getShells())
			{
				if(shell.getBounds().x == maxX)
					if(shell.getBounds().width > addwidth)
						addwidth = shell.getBounds().width;

				if(shell.getBounds().y == maxY)
					if(shell.getBounds().height > addheight)
						addheight = shell.getBounds().height;

			}		 
			addheight = (maxY - minY)  + addheight;
			addwidth = (maxX - minX) +  addwidth;
		}
		else
		{
			addwidth =Toolkit.getDefaultToolkit().getScreenSize().width;
			addheight =Toolkit.getDefaultToolkit().getScreenSize().height;
		}

		display.readAndDispatch();		
		robot = new Robot();
		screenShot = robot.createScreenCapture(new java.awt.Rectangle(minX,minY,addwidth,addheight));									

		return screenShot;
	}

	/**
	 *this method converts AWT bufferedImage into an SWT Image data
	 * 
	 * @return ImageData of the converted Image
	 */
	public static ImageData convertToSWT(BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel) bufferedImage
			.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(),
					colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(),
					bufferedImage.getHeight(), colorModel.getPixelSize(),
					palette);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					int pixel = palette.getPixel(new RGB(pixelArray[0],
							pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel) bufferedImage
			.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF,
						blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(),
					bufferedImage.getHeight(), colorModel.getPixelSize(),
					palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}


	/**
	 *this method Resizes an Image SWT 
	 * @param image to resize
	 * @param width, height new desired Image Size
	 * @param highQuality if the resize should be high quality thus slower
	 * @return Image of the new resized Image
	 */	
	public static Image resize(Image image, int width, int height,Boolean highQuality) {
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		if(highQuality)
		{
			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);
		}
		gc.drawImage(image, 0, 0,
				image.getBounds().width, image.getBounds().height,
				0, 0, width, height);
		gc.dispose();
		image.dispose(); // don't forget about me!
		return scaled;
	}



	/**
	 * Returns whether the ViewPart with the given id is currently visible in
	 * one of the pages of the active Workbench window. Will also return
	 * true when the page-book containing this view is minimized.
	 * 
	 * @param viewID The id of the view to be queried
	 * @return Whether the view is visible
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
	@Deprecated
	public static Shell getWorkbenchShell() {
		return getActiveWorkbenchShell();
	}

	/**
	 * Returns the active WorkbenchWindow's shell. In most use cases (e.g. opening a dialog), you should instead use {@link #getActiveShell()}.
	 * @return The active WorkbenchWindow's shell or <code>null</code>, if there is no active workbench window (or if its {@link IWorkbenchWindow#getShell()} method returns <code>null</code>).
	 * @see #getActiveShell()
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		return window == null ? null : window.getShell();
	}

	/**
	 * Returns the active shell. This method first calls {@link Display#getActiveShell()}. If this returns <code>null</code>,
	 * it calls {@link #getActiveWorkbenchShell()}.
	 *
	 * @return The active shell or <code>null</code>, if there is neither a global active shell nor an active workbench shell.
	 */
	public static Shell getActiveShell()
	{
		Shell shell = null;

		if (Display.getCurrent() != null)
			shell = Display.getCurrent().getActiveShell();

		if (shell == null)
			shell = Display.getDefault().getActiveShell();

		if (shell == null)
			shell = getActiveWorkbenchShell();

		return shell;
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
	@Deprecated
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
		IPerspectiveDescriptor perspectiveDescriptor = page == null ? null : page.getPerspective();
		return perspectiveDescriptor == null ? null : perspectiveDescriptor.getId();
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
		MessageDialog.openError(getActiveShell(), Messages.getString("org.nightlabs.base.ui.util.RCPUtil.showErrorDialog.title"), message); //$NON-NLS-1$
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
				getActiveShell(),
				Messages.getString("org.nightlabs.base.ui.util.RCPUtil.showConfirmOverwriteDialog.title"), //$NON-NLS-1$
				String.format(Messages.getString("org.nightlabs.base.ui.util.RCPUtil.showConfirmOverwriteDialog.message"), new Object[] { fileName }) //$NON-NLS-1$
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
	 * Opens an editor with the given input and editorID and returns it.
	 * 
	 * @param input The editors input
	 * @param editorID The editors id
	 * @return The editor opened
	 * @throws PartInitException
	 */
	public static IEditorPart openEditor(IEditorInput input, String editorID, boolean activate)
	throws PartInitException
	{
		return getActiveWorkbenchPage().openEditor(input, editorID, activate);
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
//		layout.addPerspectiveShortcut(configPerspectives[i].getAttribute("id"));
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
			boolean ok = MessageDialog.openConfirm(RCPUtil.getActiveShell(),
					Messages.getString("org.nightlabs.base.ui.util.RCPUtil.clearWorkspace.title"), Messages.getString("org.nightlabs.base.ui.util.RCPUtil.clearWorkspace.message")); //$NON-NLS-1$ //$NON-NLS-2$
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
					contributionItem.getId(),
					contributionItem.getClass().getSimpleName(), SWT.BOTTOM | SWT.TOP
			) {

				@Override
				public void handleClose() {
					getControl().dispose();
				}

				@Override
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

	/**
	 * Used internally for the TableLayout workaround.
	 */
	private static class WorkaroundTableLayout
	extends TableLayout
	{
		private List<ColumnLayoutData> originalData;
		private List<ColumnPixelData> pixelData = null;

		/**
		 * @param originalData
		 */
		public WorkaroundTableLayout(List<ColumnLayoutData> originalData)
		{
			assert originalData != null;
			this.originalData = originalData;
			this.pixelData = new ArrayList<ColumnPixelData>(originalData.size());
			for (int i=0; i < originalData.size(); i++)
			{
				// set min size of 10 pixels per column
				final ColumnPixelData pData = new ColumnPixelData(10);
				pixelData.add(pData);
				addColumnData(pData);
			}				
		}

		public List<ColumnLayoutData> getOriginalData()
		{
			return originalData;
		}

		@Override
		public void layout(Composite c, boolean flush)
		{
			// check if there is currently a vertical scroll bar visible in the composite tree where
			// the given tree of table is used in. If so reduce the available width.
			// Note: The weird thing is, that the ScrollBar is always set and the width is always > 0,
			//       BUT with this fix, the table width always matches perfectly. (marius) 
			final int verticalScrollBarWidth = c.getVerticalBar().getSize().y;
			final int width = c.getClientArea().width - verticalScrollBarWidth; 

			if (width > 1)
			{
				setPixelData(originalData, pixelData, width);
			}
			super.layout(c, flush);
		}
	}

	/**
	 * Performs {@link RCPUtil#workaroundFormTableLayout(Table, boolean)} for every Table found
	 * in the Composite graph of the given parent.
	 * 
	 * @param parent The parent to replace layouts for.
	 * @param doLayout Whether the tables should be re-layouted.
	 * @deprecated this shouldn't be needed anymore see {@link AbstractBaseFormPage}.
	 */
	@Deprecated
	public static void workaroundFormPageTableLayouts(Control parent, boolean doLayout) {
		if (parent instanceof Table) {
			workaroundFormTableLayout((Table) parent, doLayout);
		} else if (parent instanceof Composite) {
			Composite comp = (Composite) parent;
			Control[] children = comp.getChildren();
			for (Control child : children) {
				workaroundFormPageTableLayouts(child, doLayout);
			}
		}
	}

	/**
	 * Delegates to {@link #workaroundFormTableLayout(Table, boolean)} with 
	 * <code>doLayout = false</code>.
	 * 
	 * @param table The table to layout that has already a TableLayout set.
	 * @deprecated this shouldn't be needed anymore see {@link AbstractBaseFormPage}.
	 */
	@Deprecated
	public static void workaroundFormTableLayout(final Table table)
	{
		workaroundFormTableLayout(table, false);
	}

	/**
	 * Workaround method to apply normal {@link ColumnLayoutData}s to a table used in a {@link org.eclipse.ui.forms.widgets.Form} with GridLayout.
	 * This prevents the table to calculate a wrong size in a {@link org.eclipse.ui.forms.widgets.Form} and to let the Section grow on every resize.
	 * 
	 * @param table The table to layout that has already a TableLayout set.
	 * @param layoutData The layout data to apply to the table.
	 * @deprecated this shouldn't be needed anymore see {@link AbstractBaseFormPage}.
	 */
	@Deprecated
	public static void workaroundFormTableLayout(final Table table, final boolean doLayout) {
		// TODO: WORKAROUND: FIXME: XXX: This is a workaround for wrong size calculation within a form
		if (
				!(table.getLayout() instanceof TableLayout) &&
				!(table.getLayout() instanceof WorkaroundTableLayout) &&
				!(table.getLayout() instanceof WeightedTableLayout)
		)
			return; // The table does not have a TableLayout set.

		List<ColumnLayoutData> lData = null;
		if (table.getLayout() instanceof WorkaroundTableLayout) {
			lData = ((WorkaroundTableLayout)table.getLayout()).getOriginalData();
		} else if (table.getLayout() instanceof WeightedTableLayout) {
			lData = ((WeightedTableLayout) table.getLayout()).translateToColumnLayoutData();
		} else if (table.getLayout() instanceof TableLayout) {
			final TableLayout oldLayout = (TableLayout) table.getLayout();

//			tableLayout.addColumnData(new ColumnWeightData(10));
			Field columnsField = null;
			try {
				columnsField = TableLayout.class.getDeclaredField("columns"); //$NON-NLS-1$
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
			columnsField.setAccessible(true);

			try {
				lData = (List<ColumnLayoutData>) columnsField.get(oldLayout);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		final WorkaroundTableLayout tableLayout =
			new WorkaroundTableLayout(new ArrayList<ColumnLayoutData>(lData));

		table.setLayout(tableLayout);
//		final List<ColumnLayoutData> layoutData = lData;
//		final List<ColumnPixelData> pixelData = new ArrayList<ColumnPixelData>(layoutData.size());
//		for (int i=0; i < layoutData.size(); i++)
//		{
//		// set min size of 10 pixels per column
//		final ColumnPixelData pData = new ColumnPixelData(10);
//		pixelData.add(pData);
//		tableLayout.addColumnData(pData);
//		}
//		// in case the table hasn't been layouted yet, this width is 0 and the WorkaroundTableLayout 
//		// has to set correct pixel datas on layout call. 
//		final int clientWidth = table.getClientArea().width;
//		if (clientWidth != 0)
//		{
//		setPixelData(layoutData, pixelData, clientWidth);			
//		}
//		table.addControlListener(new ControlAdapter() {
//		public void controlResized(final ControlEvent e) {
//		setPixelData(layoutData, pixelData, table.getClientArea().width);
//		try {
//		final Field firstTimeField = TableLayout.class.getDeclaredField("firstTime");
//		firstTimeField.setAccessible(true);
//		firstTimeField.set(tableLayout, true);
//		} catch (final Exception ex) {
//		table.layout(true, true);
//		return; // well, the workaround broke.
//		}
//		table.layout(true, true);
//		}
//		});
		if (doLayout) {
			table.layout(true, true);
		}
	}

	private static void setPixelData(
			List<ColumnLayoutData> layoutData,
			List<ColumnPixelData> pixelDatas,
			int clientWidth
	) {
		int clientRest = clientWidth - 25;
		int weightSum = 0;
		for (int i = 0; i < layoutData.size(); i++) {
			final ColumnLayoutData columnData = layoutData.get(i);
			if (columnData instanceof ColumnPixelData) {
				clientRest -= ((ColumnPixelData) columnData).width;
				pixelDatas.get(i).width = ((ColumnPixelData) columnData).width;
			} else {
				weightSum += ((ColumnWeightData) columnData).weight;
			}
		}
		for (int i = 0; i < layoutData.size(); i++) {
			final ColumnLayoutData columnData = layoutData.get(i);
			final ColumnPixelData pixelData = pixelDatas.get(i);
			if (columnData instanceof ColumnWeightData) {
				pixelData.width = clientRest * ((ColumnWeightData) columnData).weight / weightSum;
			}
		}
	}
}
