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
package org.nightlabs.base.ui.search;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
@SuppressWarnings("unchecked")
public class SearchResultProviderRegistryUseCase {

	public SearchResultProviderRegistryUseCase() {}

	private ISearchResultProviderFactory currentSearchResultProviderFactory = null;
	/**
	 * @return the currentSearchResultProviderFactory
	 */
	public ISearchResultProviderFactory getCurrentSearchResultProviderFactory() {
		return currentSearchResultProviderFactory;
	}
	/**
	 * @param currentSearchResultProviderFactoryID the currentSearchResultProviderFactoryID to set
	 */
	public void setCurrentSearchResultProviderFactory(
			ISearchResultProviderFactory currentSearchResultProviderFactory) {
		this.currentSearchResultProviderFactory = currentSearchResultProviderFactory;
	}

	private String currentSearchText = null;
	/**
	 * @return the currentSearchText
	 */
	public String getCurrentSearchText() {
		return currentSearchText;
	}
	/**
	 * @param currentSearchText the currentSearchText to set
	 */
	public void setCurrentSearchText(String currentSearchText) {
		this.currentSearchText = currentSearchText;
	}

	private String useCase = null;
	/**
	 * @return the useCase
	 */
	public String getUseCase() {
		return useCase;
	}
	/**
	 * @param useCase the useCase to set
	 */
	public void setUseCase(String useCase) {
		this.useCase = useCase;
	}

	private SortedMap<ISearchResultProviderFactory, ISearchResultProvider> factory2Instance = null;
	public SortedMap<ISearchResultProviderFactory, ISearchResultProvider> getFactory2Instance() {
		if (factory2Instance == null) {
			factory2Instance = new TreeMap<ISearchResultProviderFactory, ISearchResultProvider>(
					SearchResultProviderRegistry.FactoryComparator);
			Set<ISearchResultProviderFactory> factories = SearchResultProviderRegistry.sharedInstance().getFactories();
			for (ISearchResultProviderFactory factory : factories) {
				factory2Instance.put(factory, factory.createSearchResultProvider());
			}
		}
		return factory2Instance;
	}


}
