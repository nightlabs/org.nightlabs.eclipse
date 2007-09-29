/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
 ******************************************************************************/
package org.nightlabs.base.entity.tree;

import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.entity.EntityEditorRegistry;

/**
 * {@link IEntityTreeCategoryBinding}s are used to bind {@link IEntityTreeCategory}s
 * to certain use cases (see {@link IEntityTreeCategoryViewBinding}).
 * <p>
 * A binding can be defined as extension of the entityEditor extension-point
 * and is used to display the contents of a {@link IEntityTreeCategory}.
 * <p>
 * Developers can use the default implementation {@link EntityTreeCategoryViewBinding}
 * by leaving the "class" attribute blank in their extension. Doing so
 * will cause the binding to delegate all its methods to the category it is bound
 * to. The bindings were introduced to give developers the freedom to
 * intercept this behaviour and do things specific for a certain binding
 * of a category without the need of changing the category itself. 
 * <p>
 * Consumers of an {@link IEntityTreeCategory} should use the bindings
 * rather than the categories themselves to access a category's content,
 * see {@link EntityTree} for a default implementation.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IEntityTreeCategoryBinding extends IExecutableExtension, Comparable<IEntityTreeCategoryBinding> {

	/**
	 * Used internally by {@link EntityEditorRegistry}
	 * when the bindings are resolved.
	 * 
	 * @param category The category this binding is bound to.
	 */
	void setEntityTreeCategory(IEntityTreeCategory category);
	/**
	 * Should return the category set by {@link #setEntityTreeCategory(IEntityTreeCategory)}
	 * which represents the category this binding is bound to.
	 * 
	 * @return The category set by {@link #setEntityTreeCategory(IEntityTreeCategory)}.
	 */
	IEntityTreeCategory getEntityTreeCategory();

	/**
	 * Returns the name of the binding. This will be displayed
	 * as the category name (most likely the category root-node in a tree).
	 * @see IEntityTreeCategory#getName()
	 * 
	 * @return The binding name
	 */
	String getName();
	
	/**
	 * Returns the icon of the binding. This will be displayed
	 * as the category icon (most likely for the category root-node in a tree).
	 * @see IEntityTreeCategory#getImage()
	 * 
	 * @return The binding image
	 */
	Image getImage();
	
	/**
	 * A hint for positioning this binding along with
	 * bindings of other categories in a tree list etc.
	 * 
	 * @return A hint for positioning this binding.
	 */
	int getIndexHint();
	
	/**
	 * The id of the editor that should be opened for an
	 * entity of the category this binding is for.
	 * 
	 * @return The editorID of the editor to open for this binding.
	 */
	String getEditorID();
	
	/**
	 * Create an {@link IEditorInput} for the editor that
	 * is opened for the given element.
	 * 
	 * @param element The element (out of the category this is bound to) a {@link IEditorInput} should be created for.
	 * @return An {@link IEditorInput} for the editor that
	 * 	is opened for the given element.
	 */
	IEditorInput createEditorInput(Object element);

	/**
	 * Create the content provider for elements of the category this is bound to.
	 * See {@link IEntityTreeCategory#createContentProvider(IEntityTreeCategoryContentConsumer, IEntityTreeCategoryBinding)}
	 * for more information on the contract of this providers.
	 * 
	 * @see IEntityTreeCategory#createContentProvider(IEntityTreeCategoryContentConsumer, IEntityTreeCategoryBinding)
	 * 
	 * @param contentConsumer The consumer the content provider should be created for
	 * @return The content provider for elements of the category this is bound to.
	 */
	ITreeContentProvider createContentProvider(IEntityTreeCategoryContentConsumer contentConsumer);

	/**
	 * Create a label provider for elements of the category this is bound to.
	 * 
	 * @see IEntityTreeCategory#createLabelProvider()
	 * @return A label provider for elements of the category this is bound to.
	 */
	ITableLabelProvider createLabelProvider();
}
