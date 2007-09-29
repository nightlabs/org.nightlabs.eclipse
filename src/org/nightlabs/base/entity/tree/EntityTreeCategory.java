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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * An abstract default implementation for an entity tree category.
 * To create a new entity tree category, one should inherit this
 * class.
 * 
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author marco schulze - marco at nightlabs dot de
 */
public abstract class EntityTreeCategory implements IEntityTreeCategory
{
	/**
	 * The id of this category.
	 */
	String id;

	/**
	 * The name of this category.
	 */
	String name;

	/**
	 * The icon for this category and the default
	 * icon for entries in this category.
	 */
	Image icon;

	/**
	 * Default constructor.
	 */
	public EntityTreeCategory()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement element, String propertyName, Object data) throws CoreException
	{
		id = element.getAttribute("id"); //$NON-NLS-1$
		name = element.getAttribute("name"); //$NON-NLS-1$
		String iconStr = element.getAttribute("icon"); //$NON-NLS-1$
		if(iconStr != null)
			icon = AbstractUIPlugin.imageDescriptorFromPlugin(element.getDeclaringExtension().getNamespaceIdentifier(), iconStr).createImage();
//			icon = ImageDescriptor.createFromURL(BaseAdminPlugin.getDefault().getBundle().getEntry(iconStr)).createImage();
		else
			icon = null;
	}

	/**
	 * Map for storing the consumers of the category along with the binding they use.
	 * The binding is used to notify the consumer of changes with the right binding.
	 */
	private Map<IEntityTreeCategoryContentConsumer, IEntityTreeCategoryBinding> contentConsumers = new HashMap<IEntityTreeCategoryContentConsumer, IEntityTreeCategoryBinding>(); 

	public ITreeContentProvider createContentProvider(final IEntityTreeCategoryContentConsumer contentConsumer, IEntityTreeCategoryBinding categoryBinding)
	{
		contentConsumers.put(contentConsumer, categoryBinding);
		contentConsumer.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				contentConsumers.remove(contentConsumer);
				onContentConsumerDisposed(e, contentConsumer, contentConsumers.keySet());
			}
		});
		return _createContentProvider(contentConsumer);
	}

	protected abstract ITreeContentProvider _createContentProvider(IEntityTreeCategoryContentConsumer contentConsumer);

	/**
	 * This method is triggered whenever an {@link IEntityTreeCategoryContentConsumer} is disposed.
	 *
	 * @param e the event
	 * @param contentConsumer The currently disposed consumer - it is not contained in <code>contentConsumers</code> anymore.
	 * @param contentConsumers The consumers that are still left (<code>contentConsumer</code> is already removed). If
	 *		this set is empty, you should release all data and listeners you kept.
	 */
	protected void onContentConsumerDisposed(DisposeEvent e, IEntityTreeCategoryContentConsumer contentConsumer, Set<IEntityTreeCategoryContentConsumer> contentConsumers)
	{
		// this implementation is empty
	}

	/**
	 * Fire a category change event. This will notify all consumers 
	 * of changes of the category binding the use.
	 */
	protected void fireEntityTreeCategoryChange() 
	{
		for (Entry<IEntityTreeCategoryContentConsumer, IEntityTreeCategoryBinding> consumerEntry : new HashSet<Entry<IEntityTreeCategoryContentConsumer, IEntityTreeCategoryBinding>>(contentConsumers.entrySet())) {
			consumerEntry.getKey().contentChanged(new ContentChangedEvent(consumerEntry.getValue()));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.IEntityTreeCategory#getId()
	 */
	public String getId()
	{
		return id;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.IEntityTreeCategory#getIcon()
	 */
	public Image getImage()
	{
		return icon;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.IEntityTreeCategory#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.entityeditor.IEntityTreeCategory#getIcon(java.lang.Object)
	 */
	public Image getImage(Object o)
	{
		return getImage();
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.entityeditor.IEntityTreeCategory#getText(java.lang.Object)
	 */
	public String getText(Object o)
	{
		return o.toString();
	}
}
