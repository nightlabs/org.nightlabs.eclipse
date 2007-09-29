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
package org.nightlabs.base.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.entity.editor.EntityEditorPageSettings;
import org.nightlabs.base.entity.tree.EntityTreeCategoryViewBinding;
import org.nightlabs.base.entity.tree.IEntityTreeCategory;
import org.nightlabs.base.entity.tree.IEntityTreeCategoryBinding;
import org.nightlabs.base.entity.tree.IEntityTreeCategoryViewBinding;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;

/**
 * An extension point registry for entity tree categories
 * and editor pages.
 * 
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class EntityEditorRegistry extends AbstractEPProcessor
{
	private static final Logger logger = Logger.getLogger(EntityEditorRegistry.class);

	/**
	 * The extension point id this registry is for.
	 */
	public static final String EXTENSION_POINT_ID = "org.nightlabs.base.entityEditor"; //$NON-NLS-1$

	/**
	 * The shared instance
	 */
	private static EntityEditorRegistry sharedInstance = null;

	/**
	 * Get the lazily created shared instance.
	 * @return The shared instance
	 */
	public static EntityEditorRegistry sharedInstance()
	{
		if (sharedInstance == null)
			sharedInstance = new EntityEditorRegistry();
		return sharedInstance;
	}

	/**
	 * BindingCarrier used to store and sort references/uses
	 * of {@link IEntityTreeCategory}s. 
	 */
	private class BindingCarrier implements Comparable<BindingCarrier> {
		private String categoryID;
		private IEntityTreeCategoryBinding binding;

		public BindingCarrier(String categoryID, IEntityTreeCategoryBinding binding) {
			this.categoryID = categoryID;
			this.binding = binding;
		}

		public int compareTo(BindingCarrier o) {
			return this.getBinding().compareTo(o.getBinding());
		}

		public IEntityTreeCategoryBinding getBinding() {
			return binding;
		}

		public void resolve() {
			IEntityTreeCategory category = categories.get(categoryID);
			if (category == null)
				logger.error("A category binding could not be resolved. Bound to "+categoryID); //$NON-NLS-1$
			binding.setEntityTreeCategory(category);
		}
	}
	

	/**
	 * All registered categories by their id.
	 */
	private Map<String, IEntityTreeCategory> categories = new HashMap<String, IEntityTreeCategory>();
	
	/**
	 * Category extensions.
	 */
	private Map<String, List<BindingCarrier>> categoriesByViewID = new HashMap<String, List<BindingCarrier>>();
	
	private Map<IEntityTreeCategory, Collection<IEntityTreeCategoryBinding>> category2Bindings;

	/**
	 * Editor page extensions.
	 */
	private Map<String, Set<EntityEditorPageSettings>> pageSettings;
	
	
	public void addPage(String editorID, EntityEditorPageSettings settings)
	{
		if(pageSettings == null)
			pageSettings = new HashMap<String, Set<EntityEditorPageSettings>>();
		Set<EntityEditorPageSettings> editorPages = pageSettings.get(editorID);
		if(editorPages == null) {
			editorPages = new HashSet<EntityEditorPageSettings>();
			pageSettings.put(editorID, editorPages);
		}
		editorPages.add(settings);
	}
	
	public Set<EntityEditorPageSettings> getPageSettings(String editorID)
	{
		checkProcessing();
		return pageSettings==null ? null : pageSettings.get(editorID);
	}
	
	public List<EntityEditorPageSettings> getPageSettingsOrdered(String editorID)
	{
		checkProcessing();
		Set<EntityEditorPageSettings> pagesUnordered = getPageSettings(editorID);
		if(pagesUnordered == null)
			return new ArrayList<EntityEditorPageSettings>(0);
		List<EntityEditorPageSettings> pagesOrdered = new ArrayList<EntityEditorPageSettings>(pagesUnordered);
		Collections.sort(pagesOrdered, new Comparator<EntityEditorPageSettings>() {
			public int compare(EntityEditorPageSettings o1, EntityEditorPageSettings o2)
			{
				return o1.getIndexHint() - o2.getIndexHint();
			}
		});
		return pagesOrdered;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID()
	{
		return EXTENSION_POINT_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception
	{
		try {
			if("category".equalsIgnoreCase(element.getName())) { //$NON-NLS-1$
				if(categoriesByViewID == null)
					categoriesByViewID = new HashMap<String, List<BindingCarrier>>();

				IEntityTreeCategory category = (IEntityTreeCategory)element.createExecutableExtension("class"); //$NON-NLS-1$
				
				categories.put(category.getId(), category);

				IConfigurationElement[] children = element.getChildren();System.out.println(extension.getNamespaceIdentifier());
				for (IConfigurationElement child : children) {
					if ("viewBinding".equalsIgnoreCase(child.getName())) { //$NON-NLS-1$
						processViewBinding(extension, child, category.getId(), category);
					}
				}
			}
			else if("pageFactory".equalsIgnoreCase(element.getName())) { //$NON-NLS-1$
				String editorID = element.getAttribute("editorID"); //$NON-NLS-1$
				addPage(editorID, new EntityEditorPageSettings(extension, element));
			}
			else if ("viewBinding".equalsIgnoreCase(element.getName())) { //$NON-NLS-1$
				String categoryID = element.getAttribute("category"); //$NON-NLS-1$
				if (!checkString(categoryID))
					throw new EPProcessorException("Category attribute must be defined for viewBinding when not wrapped in category-element", extension); //$NON-NLS-1$
				processViewBinding(extension, element, categoryID, null);
			}
		} catch (CoreException e) {
			throw new EPProcessorException("processElement failed", extension, e); //$NON-NLS-1$
		}
	}
	
	protected void processViewBinding(
			IExtension extension, IConfigurationElement element,
			String categoryID, IEntityTreeCategory category
		) 
	throws EPProcessorException, CoreException 
	{
		String clazz = element.getAttribute("class"); //$NON-NLS-1$
		IEntityTreeCategoryViewBinding viewBinding = null;
		if (checkString(clazz)) {
			// if class defined creat the executable extension
			viewBinding = (IEntityTreeCategoryViewBinding) element.createExecutableExtension("class"); //$NON-NLS-1$
		}
		else {
			// if not, instantiate default
			viewBinding = new EntityTreeCategoryViewBinding();
			// and let it initialize
			viewBinding.setInitializationData(element, "class", null); //$NON-NLS-1$
		}
				
		List<BindingCarrier> categories = categoriesByViewID.get(viewBinding.getViewID());
		if (categories == null) {
			categories = new ArrayList<BindingCarrier>();
			categoriesByViewID.put(viewBinding.getViewID(), categories);
		}
		if (category != null)
			categories.add(new BindingCarrier(category.getId(), viewBinding));
		else
			categories.add(new BindingCarrier(categoryID, viewBinding));
	}

	/**
	 * Overrides to resolve and sort the category bindings 
	 * after processing is done.
	 */
	@Override
	public synchronized void process() 
	{
		super.process();
		category2Bindings = new HashMap<IEntityTreeCategory, Collection<IEntityTreeCategoryBinding>>();
		for (List<BindingCarrier> bindings : categoriesByViewID.values()) {
			for (BindingCarrier binding : bindings) {
				binding.resolve();
				
				Collection<IEntityTreeCategoryBinding> catBindings = category2Bindings.get(binding.getBinding().getEntityTreeCategory());
				if (catBindings == null) {
					catBindings = new HashSet<IEntityTreeCategoryBinding>(); 
					category2Bindings.put(binding.getBinding().getEntityTreeCategory(), catBindings);
				}
				catBindings.add(binding.getBinding());
			}
			Collections.sort(bindings);
		}
	}

	/**
	 * Get the category bindings to the given viewID.
	 * 
	 * @param viewID The id of the view registered categories should be searched for. 
	 * @return The view bindings for the given viewID.
	 */
	public IEntityTreeCategoryBinding[] getViewBindings(String viewID)
	{
		checkProcessing();
		List<BindingCarrier> list = categoriesByViewID.get(viewID);
		IEntityTreeCategoryBinding[] emptyArray = new IEntityTreeCategoryBinding[0];
		if (list != null) {
			IEntityTreeCategoryBinding[] cats = new IEntityTreeCategoryBinding[list.size()];
			for (int i = 0; i < cats.length; i++) {
				cats[i] = list.get(i).getBinding();
			}
			return cats;
		}
		else
			return emptyArray;
	}

	/**
	 * Returns all bindings of the given category in a new Set 
	 * (which can be modified, without changing the registration)
	 * 
	 * @param category The category all bindings should be searched for. 
	 * @return All bindings of the given category.
	 */
	public Set<IEntityTreeCategoryBinding> getCategoryBindings(IEntityTreeCategory category) {
		return new HashSet<IEntityTreeCategoryBinding>(category2Bindings.get(category));
	}
	
}
