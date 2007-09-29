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

package org.nightlabs.base.action.registry;

import java.util.ArrayList;
import java.util.List;

public class MenuDescriptor extends ItemDescriptor
{
	private String id;
	private String label;
	private String path;

	/**
	 * A List with instances of either SeparatorDescriptor or GroupMarkerDescriptor
	 */
	private List<ItemDescriptor> subItems = new ArrayList<ItemDescriptor>();

	public void addSubItem(ItemDescriptor item)
	{
		subItems.add(item);
	}

	/**
	 * @return Returns instances of {@link ItemDescriptor}.
	 */
	public List getSubItems()
	{
		return subItems;
	}

	public MenuDescriptor(String id, String label, String path)
	{
		this.id = id;
		this.label = label;
		this.path = path;
	}
	public String getId()
	{
		return id;
	}
	public String getLabel()
	{
		return label;
	}
	public String getPath()
	{
		return path;
	}
}
