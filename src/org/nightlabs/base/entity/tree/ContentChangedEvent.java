package org.nightlabs.base.entity.tree;

import java.util.EventObject;

public class ContentChangedEvent
		extends EventObject
{
	private static final long serialVersionUID = 1L;

	public ContentChangedEvent(IEntityTreeCategoryBinding categoryBinding)
	{
		super(categoryBinding);
	}

	/**
	 * This method is a convenience method preventing unnecessary casts.
	 * It simply calls {@link EventObject#getSource()}.
	 * @return Returns the same as {@link EventObject#getSource()} which is the
	 *		category binding whose content changed.
	 */
	public IEntityTreeCategoryBinding getEntityTreeCategoryBinding()
	{
		return (IEntityTreeCategoryBinding) getSource();
	}
}
