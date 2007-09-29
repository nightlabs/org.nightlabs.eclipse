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

package org.nightlabs.base.io;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.io.IOFilter;
import org.nightlabs.io.IOFilterInformationProvider;
import org.nightlabs.io.IOFilterMan;

public class IOFilterRegistry 
extends AbstractEPProcessor 
{
	public static final Logger logger = Logger.getLogger(IOFilterRegistry.class);
	
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.iofilter"; //$NON-NLS-1$
	
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	protected static IOFilterRegistry _sharedInstance;
	public static IOFilterRegistry sharedInstance() 
	{		
		if (_sharedInstance == null) {
			_sharedInstance = new IOFilterRegistry();
//			_sharedInstance.process();
		}					
		return _sharedInstance;
	}
	
	protected IOFilterRegistry() {
		super();
	}	
	
	public static final String ELEMENT_IOFILTER = "ioFilter"; //$NON-NLS-1$
	public static final String ELEMENT_FILE_EXTENSION = "fileExtension"; //$NON-NLS-1$
	public static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	public static final String ATTRIBUTE_DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
	public static final String ATTRIBUTE_INFORMATION_PROVIDER = "informationProvider"; //$NON-NLS-1$
	public static final String ATTRIBUTE_FILE_EXTENSION = "fileExtension"; //$NON-NLS-1$
	
	
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (ioFilterMan == null)
			ioFilterMan = new IOFilterMan();
		
		if (element.getName().equalsIgnoreCase(ELEMENT_IOFILTER)) 
		{
			String name = element.getAttribute(ATTRIBUTE_NAME);
			String filterDesciption = element.getAttribute(ATTRIBUTE_DESCRIPTION);	
				
			IOFilterInformationProvider informationProvider = null;			
			try {
				if (element.getAttribute(ATTRIBUTE_INFORMATION_PROVIDER) != null) {
					Object object = element.createExecutableExtension(ATTRIBUTE_INFORMATION_PROVIDER);
					if (object instanceof IOFilterInformationProvider)
						informationProvider = (IOFilterInformationProvider) object;
				}
			} catch (CoreException e) {
				logger.warn("InformationProvider Class for ioFilter "+name+" could not be instanciated!", e); //$NON-NLS-1$ //$NON-NLS-2$
			}					
			
			IOFilter filter = null;
			try 
			{
				Object object = element.createExecutableExtension(ATTRIBUTE_CLASS);
				if (object instanceof IOFilter) 
				{
					filter = (IOFilter) object;
					if (checkString(name)) 
					{
						I18nText filterName = new I18nTextBuffer();
						filterName.setText(Locale.getDefault().getLanguage(), name);
						filter.setName(filterName);
					}
					if (checkString(filterDesciption)) 
					{
						I18nText filterDesc = new I18nTextBuffer();
						filterDesc.setText(Locale.getDefault().getLanguage(), filterDesciption);
						filter.setDescription(filterDesc);
					}
					
					IConfigurationElement[] children = element.getChildren(ELEMENT_FILE_EXTENSION);
					if (children.length == 0) {
						logger.warn("There is no fileExtension registered for ioFilter "+name+"!"); //$NON-NLS-1$ //$NON-NLS-2$
						return;					
					}
						
					String[] fileExtensions = new String[children.length];
					for (int i=0; i<children.length; i++) 
					{
						IConfigurationElement child = children[i];					
						String description = child.getAttribute(ATTRIBUTE_DESCRIPTION);
						String fileExtension = child.getAttribute(ATTRIBUTE_FILE_EXTENSION);					
						if (checkString(fileExtension)) 
						{
							fileExtensions[i] = fileExtension;
							if (checkString(description)) {
								I18nText desc = new I18nTextBuffer();
								desc.setText(Locale.getDefault().getLanguage(), description);
								filter.setFileExtensionDescription(fileExtension, desc);							
							}
						}
					}
					filter.setFileExtensions(fileExtensions);
					if (informationProvider != null)
						filter.setInformationProvider(informationProvider);
					ioFilterMan.addIOFilter(filter);									
				}					 
			} catch (CoreException e) {
				logger.warn("ioFilter Class for ioFilter "+name+" could not be instanciated!", e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	protected IOFilterMan ioFilterMan = null;
	public IOFilterMan getIOFilterMan() 
	{
		checkProcessing();
		
		if (ioFilterMan == null) 
			ioFilterMan = new IOFilterMan();
		
		return ioFilterMan;
	}

}
