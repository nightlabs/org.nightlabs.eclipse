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

package org.nightlabs.base.ui.composite.groupedcontent;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * {@link GroupedContentProvider}s are the entries shown in a {@link GroupedContentComposite}.
 * They provide the text and image for the table as well as the actual content of the group.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public interface GroupedContentProvider {
	/**
	 * @return The icon of this provider.
	 */
	Image getGroupIcon();
	/**
	 * @return The title of this provider.
	 */
	String getGroupTitle();
	/**
	 * Create the {@link Composite} of this provider that is the actual content
	 * of the provider. This method will be called only once per {@link GroupedContentComposite}
	 * and {@link GroupedContentProvider}.
	 * 
	 * @param parent The parent to create the {@link Composite} for.
	 * @return The newly created {@link Composite} that contains the contents to show for this provider.
	 */
	Composite createGroupContent(Composite parent);
}
