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

package org.nightlabs.base.ui.entity.editor;

import java.util.Set;

import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.progress.ProgressMonitor;

/**
 * <p>A controller that can be associated to a page that is displayed
 * by an {@link EntityEditor}. Page controllers are created by
 * {@link IEntityEditorPageFactory}s registered by the "pageFactory" extension point.</p>
 * 
 * <p>The default implementation of {@link EntityEditor} will make use of an
 * {@link EntityEditorController} which delegates all work concerning
 * loading and saving of data to the page controllers (implementations of this interface).</p>
 * 
 * <p>Also some base classes of the entity-editor-framework with extra background-loading
 * functionality use {@link IEntityEditorPageController}s to have a standardised access to
 * the data a page needs</p>
 * 
 * <p>The controller accepts {@link IEntityEditorPageControllerModifyListener}s that will 
 * be added by the framework to listen to changes in the data of the controller.
 * Implementations should notify these listeners and pages should use this listeners 
 * to reflect the changes in their UI.</p>EntityEditorController
 * 
 * This interface should not be implemented but instead extend {@link EntityEditorController}
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IEntityEditorPageController
extends IDirtyStateManager
{

	/**
	 * Get the identifier of the controller.
	 *
	 * @return the identifier of this controller.
	 */
	String getControllerID();
	
	/**
	 * Return a name for this controller.
	 * @return A name for this controller.
	 */
	String getName();
	
	/**
	 * Adds a page to this controller which is associated with it.
	 * This will be called immediately after the controller is created.
	 * @param page the page this controller is associated with.
	 * @see #getPages()
	 */
	void addPage(IFormPage page);
	
	/**
	 * Returns a Set of all pages for which the controller is responsible
	 */
	Set<IFormPage> getPages();

	/**
	 * Set the {@link EntityEditorController} this page controller is
	 * registered to. This will also be called right after creation.
	 * 
	 * @param editorController the {@link EntityEditorController} this page controller is
	 * registered to.
	 */
	void setEntityEditorController(EntityEditorController editorController);
	
	/**
	 * Load the data special to the implementation of a page controller
	 * and write status to the given monitor. This is very likely to be called
	 * on a non-gui thread. This Method is invoked asynchronously by the abstract EntityEditorPageController,
	 * so its better to extend the abstract Controller than to write job management yourself.
	 * <p>
	 * TODO: @Bieber: document whether this method is intended to be called from the outside or only to be implemented! I assume, this
	 * method should never be called from an API consumer directly, but I didn't write this framework. I assume, an API consumer
	 * should instead call a load method, right? Why is this load method not defined in this interface btw? Marco.
	 * </p>
	 * @param monitor The monitor to write status to.
	 */
	void doLoad(ProgressMonitor monitor);

	boolean isLoaded();
	
	/**
	 * Save the data special to the implementation of a page controller
	 * and write status to the given monitor. This is very likely to be called
	 * on a non-gui thread. The method will be called by EntityEditor after packaging into an
	 * asynchronous callback job.
	 * 
	 * @param monitor The monitor to write status to.
	 * @return Whether the save process succeeded respectively whether the controller actually 
	 *         did something. In some situations the controller might ask the user whether to
	 *         really save and if the user decides to cancel the save process this method should
	 *         return <code>false</code>.
	 */
	boolean doSave(ProgressMonitor monitor);
	
	/**
	 * Performs cleanups when the editor is closed.
	 */
	void dispose();
	
	/**
	 * Called when the editor of this controller gets the focus.
	 */
	void editorFocussed();
	
	
	/**
	 * Adds a new {@link IEntityEditorPageControllerModifyListener} to this controller.
	 * The new listener is immediately triggered with the last event (if there was already one).
	 *
	 * @param listener The listener to be added.
	 */
	void addModifyListener(IEntityEditorPageControllerModifyListener listener);
	
	/**
	 * Remove the given {@link IEntityEditorPageControllerModifyListener} from this controller.
	 * 
	 * @param listener The listener to remove.
	 */
	void removeModifyListener(IEntityEditorPageControllerModifyListener listener);
}
