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
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.DrillDownAdapter;

/**
 * Implementations of this interface can appear in
 * an implementation of an entity tree.
 * 
 * @see EntityTreeCategory
 * 
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author marco schulze - marco at nightlabs dot de
 */
public interface IEntityTreeCategory extends IExecutableExtension
{
	/**
	 * Get the ID of this category.
	 * @return The ID of this category
	 */
	String getId();
	
	/**
	 * Get the name of this category. This will be
	 * used as label in the entity tree.
	 * @return The name of this category
	 */
	String getName();
	
	/**
	 * Get an image for for this category. This will be
	 * used as icon in the entity tree. Entries in
	 * this category will also use this icon as default.
	 * @return An image for for this category
	 */
	Image getImage();
	
	/**
	 * Return a <em>new</em> {@link ITreeContentProvider} to use with this category.
	 * <p>
	 * The top level entries of an entity tree will always be the categories themselves.
	 * The {@link ITreeContentProvider} returned here is used to determine 
	 * the elements of a category and their children. 
	 * </p>
	 * <p>
	 * The returned provider's {@link IStructuredContentProvider#getElements(Object)} 
	 * method can be called with the parent {@link IEntityTreeCategory} 
	 * as inputElement. This is when to obtain the categories top-level children.
	 * </p>
	 * <p>
	 * Note that {@link IStructuredContentProvider#getElements(Object)} 
	 * can also be called with an inputElement equal to an object returned 
	 * in the getElements() or getChildren() methods. This is  when a {@link DrillDownAdapter}
	 * is used. The implementation of getElements will have to check this 
	 * in order to support such adapters. Best practise will be to return 
	 * a list of node-elements when the input is a category and
	 * delegate to the node-objects in other cases.
	 * </p>
	 * <p>
	 * The {@link ITreeContentProvider#getParent(Object)} is also used when {@link DrillDownAdapter}s
	 * are used and should return the category when called with an element
	 * returned in {@link IStructuredContentProvider#getElements(Object)}.
	 * </p>
	 * <p>
	 * Note, that the category is responsible for retrieving the data (<b>not</b> the
	 * content providers). That means,
	 * even though every tree has its own content provider, the data should be
	 * loaded from the server only once per category. Additionally, the category
	 * must track changes on this data, while there is someone interested in it.
	 * </p>
	 * <p>
	 * In order to fulfill this contract efficiently, an {@link IEntityTreeCategoryContentConsumer}
	 * is passed to this method, every time a new content provider is requested.
	 * Knowing who actually uses the generated content provider allows for lazy
	 * data retrieval and discarding of all data and remote listeners when the last
	 * consumer has been disposed. That means:
	 * <ul>
	 *	<li>
	 *	A category should not retrieve any data, before the first content provider asks
	 *	the category for it.
	 *	</li>
	 *	<li>
	 *	The category should not register any modification/lifecycle listeners, before
	 *	it has loaded the data.
	 *	</li>
	 *	<li>
	 *	The category should discard all data and remove all listeners after the last
	 *	consumer is disposed. Therefore, the category should use
	 *	{@link IEntityTreeCategoryContentConsumer#addDisposeListener(org.eclipse.swt.events.DisposeListener)}
	 *	and track by means of a reference counter whether data and listeners are still
	 *	needed.
	 *	</li>
	 *	<li>
	 *	It is fine, if the category notifies all content consumers about changes
	 *	(via {@link IEntityTreeCategoryContentConsumer#contentChanged(ContentChangedEvent)},
	 *	after the first content provider required the data to be loaded. In other words,
	 *	the category does not need to track, which content provider already asked for data
	 *	in order to filter the {@link ContentChangedEvent} distribution.
	 *	</li>
	 * </ul> 
	 * </p>
	 *
	 * @param contentConsumer The content consumer for whom the new content provider is created. This
	 *		consumer will be disposed some time later in its lifecycle and this category needs to be notified
	 *		about it via a {@link DisposeListener}. When there is no consumer existing anymore, the category
	 *		must release all resources (including listeners in the server) until a new consumer requires the
	 *		data to be reloaded.
	 *
	 * @param categoryBinding The binding the consumer uses to access this category. This will be
	 * 		used to notify the consumer of changes in the category.
	 *
	 * @return A <em>new</em> {@link ITreeContentProvider} to use with this category.
	 */
	ITreeContentProvider createContentProvider(IEntityTreeCategoryContentConsumer contentConsumer, IEntityTreeCategoryBinding categoryBinding);

	/**
	 * Return a <em>new</em> {@link ITableLabelProvider} to use with this category.
	 * 
	 * The icons and labels of an entity tree will be determined by the category itself
	 * (see {@link #getImage()}) and {@link #getName()}. The {@link ITableLabelProvider}
	 * returned here is used to determine labels and icons for the elements of a 
	 * category and their children.
	 * 
	 * @return A <em>new</em> {@link ITableLabelProvider} to use with this category.
	 */
	ITableLabelProvider createLabelProvider();

	/**
	 * Get an editor input object for an entry in this category.
	 * 
	 * @param o An entry in this category.
	 * @return An editor input object for an entry in this category
	 */
	IEditorInput createEditorInput(Object o);
}
