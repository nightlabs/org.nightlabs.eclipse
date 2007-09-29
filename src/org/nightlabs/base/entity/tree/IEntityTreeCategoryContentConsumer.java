package org.nightlabs.base.entity.tree;

import org.eclipse.swt.events.DisposeListener;

/**
 * An implementation of this interface is used to tell the {@link IEntityTreeCategory}
 * who is requesting the creation of a content provider. Most important, it tells
 * the category, when this content provider is not used anymore (i.e. after consumer
 * has been disposed).
 * <p>
 * {@link EntityTree} implements this interface and thus gets notified whenever the
 * content of a category changed and notifies the category, when the widget is disposed.
 * </p>
 * <p>
 * For more information, please read the documentation of
 * {@link IEntityTreeCategory#createContentProvider(IEntityTreeCategoryContentConsumer)}.
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface IEntityTreeCategoryContentConsumer
{
	/**
	 * This method adds a {@link DisposeListener} which must be
	 * triggered when this consumer is no more interested in the
	 * data (i.e. because it is disposed).
	 *
	 * @param listener The listener to be added.
	 */
	void addDisposeListener(DisposeListener listener);
	/**
	 * @param listener The listener to be removed
	 */
	void removeDisposeListener(DisposeListener listener);

	/**
	 * This method is called whenever the data managed by a category
	 * has changed (both, its structure - e.g. because a new element
	 * has been created - or its existing elements).
	 *
	 * @param event The event describing more details of the changes - e.g. the category which has changed.
	 */
	void contentChanged(ContentChangedEvent event);
}
