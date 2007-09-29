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

package org.nightlabs.base.action;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

public class NewFileRegistry 
extends AbstractEPProcessor
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.newfileaction";	 //$NON-NLS-1$
//	public static final String DEFAULT_CATEGORY = "Default category";
	public static final String DEFAULT_CATEGORY_ID = "DefaultCategory"; //$NON-NLS-1$

	private static NewFileRegistry sharedInstance;
	
	public static NewFileRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new NewFileRegistry();
		return sharedInstance;
	}
	
	protected Map<String, List<INewFileAction>> category2Actions = new HashMap<String, List<INewFileAction>>();
	
	/**
	 * 
	 * @return a {@link Map} containing categoryIDs {@link String} as key and
	 * a {@link List} of {@link INewFileAction} as value 
	 */
	public Map getCategory2Actions() {
		checkProcessing();
		return category2Actions;
	}
	
	/**
	 * 
	 * @param categoryID the ID of the Category
	 * @return the name of the category for the corresponding ID
	 */
	public String getCategoryName(String categoryID) {
		return categoryRegistry.getCategoryName(categoryID);
	}
	
	protected CategoryRegistry categoryRegistry = new CategoryRegistry();
	
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase("action"))  //$NON-NLS-1$
		{
			String className = element.getAttribute("class"); //$NON-NLS-1$
			if (!checkString(className))
				throw new EPProcessorException("Element action has to define attribute class."); //$NON-NLS-1$

			String categoryID = element.getAttribute("categoryID"); //$NON-NLS-1$
			if (!checkString(categoryID) || categoryRegistry.getCategoryName(categoryID) == null)
				categoryID = DEFAULT_CATEGORY_ID;

			String fileExtension = element.getAttribute("fileExtension"); //$NON-NLS-1$
			if (!checkString(fileExtension))
				throw new EPProcessorException("Element action has to define attribute fileExtension."); //$NON-NLS-1$

			String title = element.getAttribute("title"); //$NON-NLS-1$
			if (!checkString(title))
				throw new EPProcessorException("Element action has to define attribute title."); //$NON-NLS-1$

			String tooltip = element.getAttribute("tooltip");			 //$NON-NLS-1$
			String iconName = element.getAttribute("icon"); //$NON-NLS-1$
							
			Object o;
			try {
				o = element.createExecutableExtension("class"); //$NON-NLS-1$
				if (o instanceof INewFileAction) {
					INewFileAction action = (INewFileAction) o;
					action.setFileExtension(fileExtension);
					action.setText(title);
					if (checkString(tooltip))
						action.setToolTipText(tooltip);					
					if (checkString(iconName))
						action.setImageDescriptor(getImageDescriptor(iconName));
					
//					category2Actions.put(categoryID, action);
					if (category2Actions.containsKey(categoryID)) {
						List<INewFileAction> actions = category2Actions.get(categoryID);
						actions.add(action);
					}
					else {
						List<INewFileAction> actions = new LinkedList<INewFileAction>();
						actions.add(action);
						category2Actions.put(categoryID, actions);						
					}					
				}					
			} catch (CoreException e) {
				throw new EPProcessorException(e);
			}			
		}
	}

	protected ImageDescriptor getImageDescriptor(String iconName) 
	{
		if (iconName != null) {			
			return ImageDescriptor.createFromURL(NLBasePlugin.getDefault().getBundle().getEntry(iconName));
		}
		return null;
	}
		
	protected class CategoryRegistry 
	extends AbstractEPProcessor 
	{ 
		protected Map<String, String> categoryID2name = new HashMap<String, String>();
		
		public String getExtensionPointID() {
			return EXTENSION_POINT_ID;
		}
		
		public String getCategoryName(String categoryID) 
		{
			checkProcessing();
			return categoryID2name.get(categoryID);
		}

		public void processElement(IExtension extension, IConfigurationElement element) 
		throws Exception 
		{
			if (element.getName().equalsIgnoreCase("category"))  //$NON-NLS-1$
			{
				String id = element.getAttribute("id"); //$NON-NLS-1$
				if (!checkString(id))
					throw new EPProcessorException("Element category has to define attribute id."); //$NON-NLS-1$
 				
				String name = element.getAttribute("name"); //$NON-NLS-1$
				if (!checkString(name))
					throw new EPProcessorException("Element category has to define attribute name."); //$NON-NLS-1$
				
				categoryID2name.put(id, name);
			}
		}
				
	}
	
}
