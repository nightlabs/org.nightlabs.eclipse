/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.ui.i18n.UnitRegistryEP;
import org.nightlabs.base.ui.property.CheckboxPropertyDescriptor;
import org.nightlabs.base.ui.property.DoublePropertyDescriptor;
import org.nightlabs.base.ui.property.IntPropertyDescriptor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.properties.NamePropertyDescriptor;
import org.nightlabs.editor2d.properties.RotationPropertyDescriptor;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.unit.DotUnit;
import org.nightlabs.editor2d.util.UnitUtil;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.unit.IUnit;

public class DrawComponentPropertySource 
implements IPropertySource
{	
	private static final Logger logger = Logger.getLogger(DrawComponentPropertySource.class);
	
	public static final String CATEGORY_NAME = EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.category.name")); //$NON-NLS-1$
	public static final String CATEGORY_GEOM = EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.category.geometry"));		 //$NON-NLS-1$
	public static final String CATEGORY_ROTATION = EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.category.rotation")); //$NON-NLS-1$
	
	public DrawComponentPropertySource(DrawComponent element) 
	{
		this.drawComponent = element;				
		descriptors = createPropertyDescriptors();
	}
	
	protected DrawComponent drawComponent;	
	public DrawComponent getDrawComponent() {
		return drawComponent;
	}
	
	public void clean() {
	}
	
	private IUnit unit = null;
	public IUnit getUnit() {
		if (unit == null)
			unit = UnitRegistryEP.sharedInstance().getUnitRegistry().getUnit(DotUnit.UNIT_ID);
		return unit;
	}
	public void setUnit(IUnit unit) {
		this.unit = unit;
	}
	
	private DotUnit dotUnit = null;
	protected DotUnit getDotUnit() {
		if (dotUnit == null)
			dotUnit = getDrawComponent().getRoot().getModelUnit();
		return dotUnit;
	}
			
	// TODO: format value so that only 3 digits after the comma are visible
	public double getValue(int modelValue, IUnit unit) {
		return UnitUtil.getUnitValue(modelValue, getDotUnit(), unit);
	}
	
	// TODO: format value so that only 3 digits after the comma are visible	
	public int getSetValue(double value, IUnit unit) {
		return UnitUtil.getModelValue(value, getDotUnit(), unit);
	}	
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return drawComponent;
	}
	
	protected List<IPropertyDescriptor> descriptors = null;
	protected List<IPropertyDescriptor> getDescriptors() {
		if (descriptors == null)
			descriptors = new ArrayList<IPropertyDescriptor>();
		return descriptors;
	}
		
	protected List<IPropertyDescriptor> createPropertyDescriptors() 
	{
		List<IPropertyDescriptor> descriptors = getDescriptors();
		
		// Name
		descriptors.add(createNamePD());				
		// X
		descriptors.add(createXPD());		
		// Y		
		descriptors.add(createYPD());		
		// Width		
		descriptors.add(createWidthPD());		
		// Height		
		descriptors.add(createHeightPD());		
		// Rotation		
		descriptors.add(createRotationPD());		
		// RotationX
		descriptors.add(createRotationXPD());		
		// RotationY
		descriptors.add(createRotationYPD());
		// Visible
		descriptors.add(createVisiblePD());
		// Template
		descriptors.add(createTemplatePD());
		
		// PropertyDescriptors from extension point
		List<IPropertyDescriptor> extensionPointProperties = getExtensionPointProperties(); 
		if (!extensionPointProperties.isEmpty())
			descriptors.addAll(extensionPointProperties);
		
		return descriptors;
	}

	protected List<IPropertyDescriptor> getExtensionPointProperties() 
	{		
		List<DrawComponentProperty> properties = 
			DrawComponentPropertyRegistry.sharedInstance().getDrawComponentProperty(
					getDrawComponent().getRoot().getClass().getName(),
					getDrawComponent().getClass().getName());
		if (properties != null) 
		{
			List<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>(properties.size());			
			for (DrawComponentProperty property : properties) {
				property.setDrawComponent(getDrawComponent());
				descriptors.add(property.getPropertyDescriptor());
				id2DrawComponentProperty.put(property.getID(), property);
			}
			
			if (logger.isDebugEnabled())
				logger.debug(properties.size()+" extension point properties registered for "+getDrawComponent().getClass()); //$NON-NLS-1$
			
			return descriptors;
		}
		return Collections.EMPTY_LIST;
	}
	
	private Map<String, DrawComponentProperty> id2DrawComponentProperty = 
		new HashMap<String, DrawComponentProperty>();
	
	protected PropertyDescriptor createNamePD() 
	{
		PropertyDescriptor desc = new NamePropertyDescriptor(drawComponent,
				DrawComponent.PROP_NAME,
				Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.name"));		 //$NON-NLS-1$
		desc.setCategory(CATEGORY_NAME);
		return desc;
	}
	
	protected PropertyDescriptor createXPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(DrawComponent.PROP_X,
				Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.x")); //$NON-NLS-1$
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}
	
	protected PropertyDescriptor createYPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(DrawComponent.PROP_Y,
				Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.y")); //$NON-NLS-1$
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}

	protected PropertyDescriptor createWidthPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(DrawComponent.PROP_WIDTH,
				Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.width")); //$NON-NLS-1$
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}

	protected PropertyDescriptor createHeightPD() 
	{
		PropertyDescriptor desc = new DoublePropertyDescriptor(DrawComponent.PROP_HEIGHT,
				Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.height")); //$NON-NLS-1$
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}
	
	protected PropertyDescriptor createRotationPD() 
	{
		PropertyDescriptor desc = new RotationPropertyDescriptor(DrawComponent.PROP_ROTATION,
				Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.rotation"), drawComponent); //$NON-NLS-1$
		desc.setCategory(CATEGORY_ROTATION);		
		return desc;
	}
	
	protected PropertyDescriptor createRotationXPD() 
	{
		PropertyDescriptor desc = new IntPropertyDescriptor(DrawComponent.PROP_ROTATION_X,
				Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.rotationX"),  //$NON-NLS-1$
				!drawComponent.isChangedRotationCenterAllowed());
		desc.setCategory(CATEGORY_ROTATION);		
		return desc;
	}

	protected PropertyDescriptor createRotationYPD() 
	{
		PropertyDescriptor desc = new IntPropertyDescriptor(DrawComponent.PROP_ROTATION_Y,
				Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.rotationY"), //$NON-NLS-1$
				!drawComponent.isChangedRotationCenterAllowed());
		desc.setCategory(CATEGORY_ROTATION);		
		return desc;
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() 
	{
		if (drawComponent == null)
			return new IPropertyDescriptor[0];
		
		List<IPropertyDescriptor> descriptors = getDescriptors();		
		return (IPropertyDescriptor[])descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) 
	{
		if (id.equals(DrawComponent.PROP_X)) {
			return new Double(getValue(drawComponent.getX(), getUnit()));
		}
		else if (id.equals(DrawComponent.PROP_Y)) {
			return new Double(getValue(drawComponent.getY(), getUnit()));
		}
		else if (id.equals(DrawComponent.PROP_WIDTH)) {
			return new Double(getValue(drawComponent.getWidth(), getUnit()));
		}
		else if (id.equals(DrawComponent.PROP_HEIGHT)) {
			return new Double(getValue(drawComponent.getHeight(), getUnit()));
		}
		else if (id.equals(DrawComponent.PROP_ROTATION)) {
			return new Double(drawComponent.getRotation());
//			return new Double(drawComponent.getRotationAbsolute());
		}		
		else if (id.equals(DrawComponent.PROP_ROTATION_X)) {
			return new Integer(drawComponent.getRotationX());
		}		
		else if (id.equals(DrawComponent.PROP_ROTATION_Y)) {
			return new Integer(drawComponent.getRotationY());
		}		
		else if (id.equals(DrawComponent.PROP_NAME)) {
			return drawComponent.getI18nText();
		}		
		else if (id.equals(DrawComponent.PROP_VISIBLE)) {
			return new Boolean(drawComponent.isVisible());
		}
		else if (id.equals(DrawComponent.PROP_TEMPLATE)) {
			return new Boolean(drawComponent.isTemplate());
		}		
		
		// properties from extension point
		DrawComponentProperty property = id2DrawComponentProperty.get(id);
		if (property != null) {
			property.setDrawComponent(drawComponent);
			return property.getPropertyValue();
		}
		
		return null;
	}
	
	/* 
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		return false;
	}

	/*
	 * @see org.eclipse.ui.views.properties.IPropertySouce#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) 
	{
		if (id.equals(DrawComponent.PROP_X)) {
			double x = ((Double)value).doubleValue();
			drawComponent.setX(getSetValue(x, getUnit()));
			return;
		}
		else if (id.equals(DrawComponent.PROP_Y)) {
			double y = ((Double)value).doubleValue();
			drawComponent.setY(getSetValue(y, getUnit()));
			return;
		}
		else if (id.equals(DrawComponent.PROP_WIDTH)) {
			double width = ((Double)value).doubleValue();
			drawComponent.setWidth(getSetValue(width, getUnit()));
			return;
		}
		else if (id.equals(DrawComponent.PROP_HEIGHT)) {
			double height = ((Double)value).doubleValue();
			drawComponent.setHeight(getSetValue(height, getUnit()));
			return;
		}
		else if (id.equals(DrawComponent.PROP_ROTATION)) {
			drawComponent.setRotation(((Double)value).doubleValue());
//			drawComponent.setRotationAbsolute(((Double)value).doubleValue());			
			return;
		}		
		else if (id.equals(DrawComponent.PROP_ROTATION_X)) {
			drawComponent.setRotationX(((Integer)value).intValue());
			return;
		}		
		else if (id.equals(DrawComponent.PROP_ROTATION_Y)) {
			drawComponent.setRotationY(((Integer)value).intValue());
			return;
		}
		else if (id.equals(DrawComponent.PROP_NAME)) {
			drawComponent.setI18nText((I18nText)value);
			return;
		}
		else if (id.equals(DrawComponent.PROP_VISIBLE)) {
			drawComponent.setVisible(((Boolean)value).booleanValue());
			return;
		}
		else if (id.equals(DrawComponent.PROP_TEMPLATE)) {
			drawComponent.setTemplate(((Boolean)value).booleanValue());
			return;
		}
		
		// properties from extension point
		DrawComponentProperty property = id2DrawComponentProperty.get(id);
		if (property != null) {
			property.setDrawComponent(drawComponent);
			property.setPropertyValue(value);
		}
	}
			
	protected PropertyDescriptor createVisiblePD() 
	{
		PropertyDescriptor pd = new CheckboxPropertyDescriptor(DrawComponent.PROP_VISIBLE, 
				Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.visible"), false); //$NON-NLS-1$
		return pd;
	}
	
	protected PropertyDescriptor createTemplatePD() 
	{
		PropertyDescriptor pd = new CheckboxPropertyDescriptor(DrawComponent.PROP_TEMPLATE, 
				Messages.getString("org.nightlabs.editor2d.model.DrawComponentPropertySource.template"), true); //$NON-NLS-1$
		return pd;
	}
}

