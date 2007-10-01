/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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
package org.nightlabs.editor2d.model;

import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.ui.property.CheckboxPropertyDescriptor;
import org.nightlabs.base.ui.property.ComboBoxPropertyDescriptor;
import org.nightlabs.base.ui.property.DoublePropertyDescriptor;
import org.nightlabs.base.ui.property.XTextPropertyDescriptor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.i18n.I18nText;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PagePropertySource 
extends DrawComponentPropertySource 
{
	public PagePropertySource(PageDrawComponent element) {
		super(element);
	}

	protected PageDrawComponent getPageDrawComponent() {
		return (PageDrawComponent) drawComponent;
	}
	
	protected List<IPropertyDescriptor> createPropertyDescriptors() 
	{
		List<IPropertyDescriptor> descriptors = getDescriptors();
		
		// Name
		descriptors.add(createNamePD());			
		// Orientation
		descriptors.add(createOrientationPD());
		// Width
		descriptors.add(createPageWidthPD());
		// Height
		descriptors.add(createPageHeightPD());
		// Resolution Unit
		descriptors.add(createResolutionUnitPD());		
		// Resolution Value
		descriptors.add(createResolutionValuePD());
		// Page Size
//		descriptors.add(createPageSizePD());
		// Show Page Bounds
		descriptors.add(createShowPageBoundsPD());
		// Visible
		descriptors.add(createVisiblePD());		
		
		// PropertyDescriptors from extension point
		List<IPropertyDescriptor> extensionPointProperties = getExtensionPointProperties(); 
		if (!extensionPointProperties.isEmpty())
			descriptors.addAll(extensionPointProperties);
		
		return descriptors;
	}
	
	protected PropertyDescriptor createPageWidthPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(PageDrawComponent.PROP_WIDTH,
				Messages.getString("org.nightlabs.editor2d.model.PagePropertySource.width"), true); //$NON-NLS-1$
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}
	
	protected PropertyDescriptor createPageHeightPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(DrawComponent.PROP_HEIGHT,
				Messages.getString("org.nightlabs.editor2d.model.PagePropertySource.height"), true); //$NON-NLS-1$
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}
		
	public static String getOrientationString(int orientation) 
	{
		if (orientation == PageDrawComponent.ORIENTATION_HORIZONTAL)
			return Messages.getString("org.nightlabs.editor2d.model.PagePropertySource.horizontal"); //$NON-NLS-1$
		if (orientation == PageDrawComponent.ORIENTATION_VERTICAL)
			return Messages.getString("org.nightlabs.editor2d.model.PagePropertySource.vertical"); //$NON-NLS-1$
		
		return null;
	}
	
	public static int getOrientation(String orientationString) 
	{
		if (orientationString.equals(Messages.getString("org.nightlabs.editor2d.model.PagePropertySource.horizontal"))) //$NON-NLS-1$
			return PageDrawComponent.ORIENTATION_HORIZONTAL;
		if (orientationString.equals(Messages.getString("org.nightlabs.editor2d.model.PagePropertySource.vertical"))) //$NON-NLS-1$
			return PageDrawComponent.ORIENTATION_VERTICAL;
		
		return -1;
	}
		
	protected PropertyDescriptor createOrientationPD()
	{
		String horizontal = getOrientationString(PageDrawComponent.ORIENTATION_HORIZONTAL);
		String vertical = getOrientationString(PageDrawComponent.ORIENTATION_VERTICAL);		
		String[] values = new String[] {horizontal, vertical};
		PropertyDescriptor desc = new ComboBoxPropertyDescriptor(PageDrawComponent.PROP_ORIENTATION,
				Messages.getString("org.nightlabs.editor2d.model.PagePropertySource.orientation"), values); //$NON-NLS-1$
		return desc;
	}	

	protected PropertyDescriptor createShowPageBoundsPD()
	{
		PropertyDescriptor desc = new CheckboxPropertyDescriptor(PageDrawComponent.PROP_SHOW_PAGE_BOUNDS,
				Messages.getString("org.nightlabs.editor2d.model.PagePropertySource.showPageBounds")); //$NON-NLS-1$
		desc.setCategory(CATEGORY_PAGE);
		return desc;
	}	
		
	protected PropertyDescriptor createResolutionValuePD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(RootDrawComponent.PROP_RESOLUTION,
				Messages.getString("org.nightlabs.editor2d.model.PagePropertySource.resolution"), true); //$NON-NLS-1$
		desc.setCategory(CATEGORY_RESOLUTION);
		return desc;
	}

	public static final String ID_RESOLUTION_UNIT = "ResolutionUnit"; //$NON-NLS-1$
	public static final String CATEGORY_RESOLUTION = Messages.getString("org.nightlabs.editor2d.model.PagePropertySource.category.resolution");	 //$NON-NLS-1$
	public static final String CATEGORY_PAGE = Messages.getString("org.nightlabs.editor2d.model.PagePropertySource.category.page");	 //$NON-NLS-1$
	
	protected PropertyDescriptor createResolutionUnitPD() 
	{
		PropertyDescriptor desc = new XTextPropertyDescriptor(ID_RESOLUTION_UNIT,
				Messages.getString("org.nightlabs.editor2d.model.PagePropertySource.resolutionUnit"), true); //$NON-NLS-1$
		desc.setCategory(CATEGORY_RESOLUTION);
		return desc;
	}
		
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) 
	{
		if (id.equals(PageDrawComponent.PROP_ORIENTATION)) {
			return getOrientationString(getPageDrawComponent().getOrientation());
		}
		else if (id.equals(DrawComponent.PROP_NAME)) {
			return drawComponent.getI18nText();
		}
		else if (id.equals(DrawComponent.PROP_WIDTH)) {
			return new Double(getValue(getPageDrawComponent().getPageBounds().width, getUnit()));
		}
		else if (id.equals(DrawComponent.PROP_HEIGHT)) {
			return new Double(getValue(getPageDrawComponent().getPageBounds().height, getUnit()));
		}		
		else if (id.equals(RootDrawComponent.PROP_RESOLUTION)) {
			return getPageDrawComponent().getRoot().getResolution().getResolutionX();
		}
		else if (id.equals(ID_RESOLUTION_UNIT)) {
			return getPageDrawComponent().getRoot().getResolution().getResolutionUnit().getResolutionID();
		}
		else if (id.equals(PageDrawComponent.PROP_SHOW_PAGE_BOUNDS)) {
			return getPageDrawComponent().isShowPageBounds();
		}						
//		else if (id.equals(PageDrawComponent.PROP_PAGE_BOUNDS)) {
//			PageSize pageSize = new PageSize();
//			pageSize.setPageHeight(getPageDrawComponent().getPageBounds().width);
//			pageSize.setPageWidth(getPageDrawComponent().getPageBounds().height);
//			pageSize.setResolution(getPageDrawComponent().getResolution());
//			return pageSize;
//		}
		
//		return null;
		return super.getPropertyValue(id);
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */	
	public void setPropertyValue(Object id, Object value) 
	{
		if (id.equals(PageDrawComponent.PROP_ORIENTATION)) {
			int newOrientation = getOrientation((String)value);
			getPageDrawComponent().setOrientation(newOrientation);
		}
		else if (id.equals(DrawComponent.PROP_NAME)) {
			drawComponent.setI18nText((I18nText)value);
		}
		else if (id.equals(PageDrawComponent.PROP_SHOW_PAGE_BOUNDS)) {
			getPageDrawComponent().setShowPageBounds((Boolean)value);
		}		
//		else if (id.equals(RootDrawComponent.PROP_RESOLUTION)) 
//		{
//			double resolution = ((Double)value).doubleValue();
//			getPageDrawComponent().getRoot().getResolution().setResolutionX(resolution);
//			getPageDrawComponent().getRoot().getResolution().setResolutionY(resolution);
//		}		
//		else if (id.equals(PageDrawComponent.PROP_PAGE_BOUNDS)) 
//		{
//			PageSize pageSize = (PageSize) value;
//			int defaultX = 25;
//			int defaultY = 25;
//			Rectangle pageBounds = new Rectangle(defaultX, defaultY, 
//					(int)pageSize.getPageWidth(), (int)pageSize.getPageHeight());
//			getPageDrawComponent().setPageBounds(pageBounds);
//			getPageDrawComponent().setResolution(pageSize.getResolution());
//		}	
		super.setPropertyValue(id, value);
	}
	
}
