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
package org.nightlabs.base.search;

import java.util.Collection;

/**
 * Interface which describes a contract for providing search results
 * Implementations get a search string and must return a Collection of
 * objects
 * Instances of ISearchResultProvider are created by an {@link ISearchResultProviderFactory}
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public interface ISearchResultProvider<T> 
{	
	/**
	 * returns a Collection which contains the result of the search
	 * @return a Collection which contains the result of the search 
	 */
	Collection<T> getSelectedObjects();
		
	/**
	 * sets the search text
	 * @param text the searchText to set
	 */
	void setSearchText(String text);
	
	/**
	 * returns the factory
	 * @return the factory
	 */
	ISearchResultProviderFactory getFactory(); 
}
