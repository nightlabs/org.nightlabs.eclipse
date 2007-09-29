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

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * <p>Implementations of this class are registered to the <b>entityEditor</b>
 * extension point with the <b>pageFactory</b> element.</p>
 * 
 * <p>They are responsible for creating the GUI (IFormPage) for an entity editor page
 * as well as its controller that is responsible for loading and saving
 * the page's model.</p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IEntityEditorPageFactory {
	
	/**
	 * Create an new IFormPage that will display the part
	 * of the entity linked to this entity editor page. 
	 * For your convenience you might subclass {@link FormPage},
	 * or any other default implementation. Also take a look
	 * at {@link EntityEditorPageWithProgress} that provides
	 * support for loading (large or slow) data within
	 * asynchronous jobs.
	 *
	 * @param formEditor The editor to create the page for.
	 * @return A new entity editor page.
	 */
	public IFormPage createPage(FormEditor formEditor);
	
	/**
	 * <p>Create a new page controller for the pages of this
	 * factory. The page controller will be added to the editor's
	 * {@link EntityEditorController}. An {@link IEntityEditorPageFactory}
	 * might choose to return <code>null</code> here as well, then, however
	 * the associated editor or the page itself somehow have to handle
	 * data loading.</p>
	 * <p>
	 * Additionally, multiple pages can share the same page-controller. Therefore,
	 * this method can return an already-registered {@link IEntityEditorPageController}
	 * instead of creating a new one. The page-controller might be obtained from the editor's
	 * <code>EntityEditorController</code>
	 * (for example via {@link EntityEditorController#getPageControllerByControllerID(String)} or
	 * {@link EntityEditorController#getPageControllers(Class)}).
	 * Use {@link EntityEditor#getController()} to get the <code>EntityEditorController</code> from the given
	 * editor.
	 * </p>
	 * <p>Also for the page-controller there exists a base class with 
	 * extra functionality concerning background loading of data, see {@link EntityEditorPageController}</p>  
	 * 
	 * @param editor The editor for which a controller should be created
	 * @return A new page controller appropriate to pages created by this factory.
	 */
	public IEntityEditorPageController createPageController(EntityEditor editor);

}
