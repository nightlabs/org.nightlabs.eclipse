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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SearchResultProviderRegistry 
extends AbstractEPProcessor 
{
	private static final Logger logger = Logger.getLogger(SearchResultProviderRegistry.class);
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.searchResultProvider"; //$NON-NLS-1$
	public static final String ELEMENT_SEARCH_RESULT_PROVIDER_FACTORY = "searchResultProviderFactory"; //$NON-NLS-1$
	public static final String ELEMENT_SEARCH_RESULT_HANDLER = "searchResultActionHandler"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
	public static final String ATTRIBUTE_PRIORITY = "priority"; //$NON-NLS-1$
	public static final String ATTRIBUTE_DECORATOR = "decorator"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ICON = "icon"; //$NON-NLS-1$
	public static final String ATTRIBUTE_PERSPECTIVE_ID = "perspectiveID"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	public static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	public static final String ATTRIBUTE_SEARCH_RESULT_PROVIDER_FACTORY_ID = "searchResultProviderFactoryID"; //$NON-NLS-1$

	private static SearchResultProviderRegistry sharedInstance;
	public static SearchResultProviderRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (SearchResultProviderRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new SearchResultProviderRegistry();
				}
			}
		}
		return sharedInstance;
	}
	
	protected SearchResultProviderRegistry() {}
	
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equals(ELEMENT_SEARCH_RESULT_PROVIDER_FACTORY)) {
			if (checkString(element.getAttribute(ATTRIBUTE_CLASS))) {
				try {
					ISearchResultProviderFactory factory = (ISearchResultProviderFactory) element.createExecutableExtension(ATTRIBUTE_CLASS);
					factories.add(factory);
				} catch (Exception e) {
					logger.error("There occured an error during initalizing the class "+element.getAttribute(ATTRIBUTE_CLASS), e); //$NON-NLS-1$
				}				
			}
		}
		if (element.getName().equals(ELEMENT_SEARCH_RESULT_HANDLER)) {
			String factoryID = element.getAttribute(ATTRIBUTE_SEARCH_RESULT_PROVIDER_FACTORY_ID);
			String className = element.getAttribute(ATTRIBUTE_CLASS);
			String perspectiveID = element.getAttribute(ATTRIBUTE_PERSPECTIVE_ID);
			if (checkString(className) && checkString(factoryID)) {
				try {
					ISearchResultActionHandler actionHandler = (ISearchResultActionHandler) element.createExecutableExtension(ATTRIBUTE_CLASS);
					Map<String, ISearchResultActionHandler> perspectiveID2ActionHandler = factoryID2PerspectiveID2ActionHandler.get(factoryID); 
					if (perspectiveID2ActionHandler == null) {
						perspectiveID2ActionHandler = new HashMap<String, ISearchResultActionHandler>();
						factoryID2PerspectiveID2ActionHandler.put(factoryID, perspectiveID2ActionHandler);
					}

					if (!checkString(perspectiveID))
						perspectiveID = ISearchResultProviderFactory.WILDCARD_PERSPECTIVE_ID;
					
					if (perspectiveID2ActionHandler.get(perspectiveID) != null) {
							ISearchResultActionHandler oldActionHandler = perspectiveID2ActionHandler.get(perspectiveID);
							logger.warn("There already exists an actionHandler for the perspectiveID "+perspectiveID+" and the factoryID "+factoryID); //$NON-NLS-1$ //$NON-NLS-2$
							logger.warn("actionHandler "+oldActionHandler+" has been replaced by actionHandler "+actionHandler); //$NON-NLS-1$ //$NON-NLS-2$
					}

					perspectiveID2ActionHandler.put(perspectiveID, actionHandler);
				} catch (Exception e) {
					logger.error("There occured an error during initalizing the class "+element.getAttribute(ATTRIBUTE_CLASS), e); //$NON-NLS-1$
				}				
			}
		}		
	}
	
	private Comparator<ISearchResultProviderFactory> factoryComparator = new Comparator<ISearchResultProviderFactory>(){
		public int compare(ISearchResultProviderFactory o1, ISearchResultProviderFactory o2) {
			return o1.getPriority() - o2.getPriority();
		}
	};
	
	private SortedSet<ISearchResultProviderFactory> factories = new TreeSet<ISearchResultProviderFactory>(factoryComparator);	
	public Set<ISearchResultProviderFactory> getFactories() {
		check();
		return factories;
	}
	
//	private Map<String, Set<ISearchResultActionHandler>> factoryID2ActionHandlers = new HashMap<String, Set<ISearchResultActionHandler>>();
	private Map<String, Map<String, ISearchResultActionHandler>> factoryID2PerspectiveID2ActionHandler = new HashMap<String, Map<String, ISearchResultActionHandler>>();	
	private Map<String, SearchResultProviderRegistryUseCase> useCase2RegistryUse = new HashMap<String, SearchResultProviderRegistryUseCase>();
	public SearchResultProviderRegistryUseCase getUseCase(String useCase) {
		check();
		return useCase2RegistryUse.get(useCase);
	}	
	public void addUseCase(String useCaseString, SearchResultProviderRegistryUseCase useCase) {
		useCase2RegistryUse.put(useCaseString, useCase);
	}
	
	private boolean checked = false;
	protected void check()
	{
		if (!checked) {
			checkProcessing();
			for (ISearchResultProviderFactory factory : factories) {
				Map<String, ISearchResultActionHandler> perspectiveID2ActionHandler = factoryID2PerspectiveID2ActionHandler.get(factory.getID());
				if (perspectiveID2ActionHandler != null) {
					for (Map.Entry<String, ISearchResultActionHandler> entry : perspectiveID2ActionHandler.entrySet()) {
						factory.addActionHandler(entry.getValue(), entry.getKey());
					}					
				}
			}
			checked = true;
		}
	}
	
}
