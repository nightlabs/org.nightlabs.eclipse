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

package org.nightlabs.base.entity.editor;

import java.beans.PropertyChangeSupport;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.notification.IDirtyStateManager;
import org.nightlabs.util.bean.IPropertyChangeSupport;

/**
 * <p>A controller that can be associated to a page that is displayed
 * by an {@link EntityEditor}. Page controllers are created by
 * {@link IEntityEditorPageFactory}s registered by the "pageFactory" extension point.</p>
 * 
 * <p>The default implementation of {@link EntityEditor} will make use of an
 * {@link EntityEditorController} and delegate all work concerning
 * loading and saving of data to the page controllers (implementations of this interface).</p>
 * 
 * <p>Also some base classes of the entity-editor-framework with extra background-loading 
 * functionality use {@link IEntityEditorPageController}s to have a standardised access to
 * the data a page needs</p>
 * 
 * <p>The controller extends {@link IPropertyChangeSupport} hence it accepts listeners to
 * property changes. Pages should use this listeners to reflect the changes in their UI.
 * Implementors should try to subclass {@link PropertyChangeSupport} wherever possible.</p>
 * 
 * This interface should not be implemented but instead extend {@link EntityEditorController} 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IEntityEditorPageController
extends IDirtyStateManager
// extends IPropertyChangeSupport, IDirtyStateManager 
{
//	/**
//	 * Set the page this controller is associated with.
//	 * This will be called immediately after the controller is created.
//	 * @param page the page this controller is associated with.
//	 * @deprecated see {@link EntityEditorPageController#getPage()}
//	 * @see #getPages()
//	 */
//	public void setPage(IFormPage page);

// seems not be needed. commented it out. Marco. 2007-08-29
//	public static final String PROPERTY_CONTROLLER_OBJECT = IEntityEditorPageController.class.getName()+"#controllerObject"; //$NON-NLS-1$

	/**
	 * Get the identifier of the controller.
	 *
	 * @return the identifier of this controller.
	 */
	public String getControllerID();
	
	/**
	 * Adds a page to this controller which is associated with it.
	 * This will be called immediately after the controller is created.
	 * @param page the page this controller is associated with. 
	 * @see #getPages()
	 */
	public void addPage(IFormPage page);
	
	/**
	 * Returns a Set of all pages for which the controller is responsible 
	 */
	public Set<IFormPage> getPages();

	/**
	 * Set the {@link EntityEditorController} this page controller is
	 * registered to. This will also be called right after creation.
	 * 
	 * @param editorController the {@link EntityEditorController} this page controller is
	 * registered to.
	 */
	public void setEntityEditorController(EntityEditorController editorController);
	
	/**
	 * Load the data special to the implementation of a page controller
	 * and write status to the given monitor. This is very likely to be called
	 * on a non-gui thread. This Method is invoked asynchronously by the abstract EntityEditorPageController,
	 * so its better to extend the abstract Controller than to write job management yourself. 
	 * @param monitor The monitor to write status to.
	 */
	public void doLoad(IProgressMonitor monitor);
	
	/**
	 * Save the data special to the implementation of a page controller
	 * and write status to the given monitor. This is very likely to be called
	 * on a non-gui thread. The Method will be called by EntityEditor after packaging into an 
	 * asynchronous callback job.
	 * @param monitor The monitor to write status to.
	 */
	public void doSave(IProgressMonitor monitor);
	
	/**
	 * Performs cleanups when the editor is closed.
	 */
	public void dispose();
	
	/**
	 * Called when the editor of this controller gets the focus.
	 */
	public void editorFocussed();
	
	
	/**
	 * Adds a new {@link IEntityEditorPageControllerModifyListener} to this controller.
	 *  
	 * @param listener The listener to be added.
	 */
	public void addModifyListener(IEntityEditorPageControllerModifyListener listener);
	
	/**
	 * Remove the given {@link IEntityEditorPageControllerModifyListener} from this controller.
	 * 
	 * @param listener The listener to remove.
	 */
	public void removeModifyListener(IEntityEditorPageControllerModifyListener listener); 	
}
