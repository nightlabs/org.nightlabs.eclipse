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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractSearchResultProviderFactory 
implements ISearchResultProviderFactory 
{
	public AbstractSearchResultProviderFactory() {
		super();
	}

	private Image decoratorImage = null;
	public Image getDecoratorImage() {
		return decoratorImage;
	}

	private String id = null;
	public String getID() {
		return id;
	}

	private Image image = null;
	public Image getImage() {
		return image;
	}

	private I18nText name = new I18nTextBuffer();
	public I18nText getName() {
		return name;
	}

	public ISearchResultActionHandler getActionHandler() 
	{
		String perspectiveID = RCPUtil.getActivePerspectiveID();			
		ISearchResultActionHandler actionHandler = perspectiveID2ActionHandler.get(perspectiveID);
		if (actionHandler == null) {
			actionHandler = perspectiveID2ActionHandler.get(WILDCARD_PERSPECTIVE_ID);
		}
		return actionHandler;
	}
	
	private Map<String, ISearchResultActionHandler> perspectiveID2ActionHandler = new HashMap<String, ISearchResultActionHandler>();
	public void addActionHandler(ISearchResultActionHandler actionHandler, String perspectiveID) {
		perspectiveID2ActionHandler.put(perspectiveID, actionHandler);
	}
	
	private int priority = DEFAULT_PRIORITY;
	public int getPriority() {
		return priority;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) 
	throws CoreException 
	{
		if (config.getName().equals(SearchResultProviderRegistry.ELEMENT_SEARCH_RESULT_PROVIDER_FACTORY)) {
			String decoratorString = config.getAttribute(SearchResultProviderRegistry.ATTRIBUTE_DECORATOR);
			String iconString = config.getAttribute(SearchResultProviderRegistry.ATTRIBUTE_ICON);
			String name = config.getAttribute(SearchResultProviderRegistry.ATTRIBUTE_NAME);
			String priority = config.getAttribute(SearchResultProviderRegistry.ATTRIBUTE_PRIORITY);
			String idString = config.getAttribute(SearchResultProviderRegistry.ATTRIBUTE_ID);
			if (AbstractEPProcessor.checkString(name)) {
				this.name.setText(Locale.getDefault().getLanguage(), name);
			}
			if (AbstractEPProcessor.checkString(iconString)) {
				ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
						config.getNamespaceIdentifier(), iconString);
				if (imageDescriptor != null)
					image = imageDescriptor.createImage();										
			}
			if (AbstractEPProcessor.checkString(decoratorString)) {
				ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
						config.getNamespaceIdentifier(), decoratorString);
				if (imageDescriptor != null)
					decoratorImage = imageDescriptor.createImage();										
			}
			if (AbstractEPProcessor.checkString(priority)) {
				try {
					this.priority = Integer.valueOf(priority);
				} catch (NumberFormatException e) {
					
				}
			}
			if (AbstractEPProcessor.checkString(idString)) {
				id = idString;
			}
		}		
	}

	private Image composedDecoratorImage = null;
	public Image getComposedDecoratorImage() 
	{
		if (composedDecoratorImage == null && decoratorImage != null) {
			composedDecoratorImage = new SearchCompositeImage(decoratorImage).createImage();
		}
		return composedDecoratorImage;
	}
	
}
