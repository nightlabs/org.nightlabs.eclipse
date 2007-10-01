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

import java.awt.Color;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.ui.property.AWTColorPropertyDescriptor;
import org.nightlabs.base.ui.property.CheckboxPropertyDescriptor;
import org.nightlabs.base.ui.property.GenericComboBoxPropertyDescriptor;
import org.nightlabs.base.ui.property.SpinnerPropertyDescriptor;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent.LineStyle;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.util.CollectionUtil;


public class ShapeDrawComponentPropertySource 
extends DrawComponentPropertySource 
{
	public static final String CATEGORY_COLORS = Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.category.color"); //$NON-NLS-1$
	public static final String CATEGORY_LINE = Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.category.line");	 //$NON-NLS-1$
	
  /**
   * @param element
   */
  public ShapeDrawComponentPropertySource(ShapeDrawComponent element) {
    super(element);
  }
   
  @Override
	protected List<IPropertyDescriptor> createPropertyDescriptors() 
	{
		List<IPropertyDescriptor> descriptors = super.createPropertyDescriptors();
		
		// Line Color		
		descriptors.add(createLineColorPD());		
		// Fill Color
		descriptors.add(createFillColorPD());		
		// Line Width
		descriptors.add(createLineWidthPD());		
		// Line Style
		descriptors.add(createLineStylePD());		
		// Fill
		descriptors.add(createFillPD());
		// Stroke
		descriptors.add(createStrokePD());
		
		return descriptors;
	}
			
	protected PropertyDescriptor createStrokePD() 
	{
		PropertyDescriptor desc = new CheckboxPropertyDescriptor(ShapeDrawComponent.PROP_SHOW_STROKE,
				Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.showOutline")); //$NON-NLS-1$
		desc.setCategory(CATEGORY_LINE);
		return desc;
	}
	
	protected PropertyDescriptor createLineColorPD() 
	{
		PropertyDescriptor desc = new AWTColorPropertyDescriptor(ShapeDrawComponent.PROP_LINE_COLOR,
				Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.lineColor")); //$NON-NLS-1$
		desc.setCategory(CATEGORY_COLORS);
		return desc;
	}
	
	protected PropertyDescriptor createFillColorPD() 
	{
		PropertyDescriptor desc = new AWTColorPropertyDescriptor(ShapeDrawComponent.PROP_FILL_COLOR,
				Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.fillColor")); //$NON-NLS-1$
		desc.setCategory(CATEGORY_COLORS);
		return desc;
	}
	
//	protected PropertyDescriptor createLineWidthPD() 
//	{
//		PropertyDescriptor desc = new IntPropertyDescriptor(ShapeDrawComponent.PROP_LINE_WIDTH,
//				EditorPlugin.getResourceString("property.linewidth.label"));
//		desc.setCategory(CATEGORY_LINE);
//		return desc;
//	}

	protected PropertyDescriptor createLineWidthPD() 
	{
		PropertyDescriptor desc = new SpinnerPropertyDescriptor(ShapeDrawComponent.PROP_LINE_WIDTH,
				Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.lineWidth")); //$NON-NLS-1$
		desc.setCategory(CATEGORY_LINE);
		return desc;
	}
		
//	protected PropertyDescriptor createLineStylePD() 
//	{
//		PropertyDescriptor desc = new IntPropertyDescriptor(ShapeDrawComponent.PROP_LINE_STYLE,
//				EditorPlugin.getResourceString("property.linestyle.label"));
//		desc.setCategory(CATEGORY_LINE);
//		return desc;		
//	}

	private static ILabelProvider lineStyleLabelProvider = new LabelProvider() 
	{
		@Override
		public String getText(Object element) 
		{
			LineStyle lineStyle = (LineStyle) element; 
			switch (lineStyle) 
			{
				case SOLID:
					return Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.solid"); //$NON-NLS-1$
				case DASHED_1:
					return Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.dashed1"); //$NON-NLS-1$
				case DASHED_2:
					return Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.dashed2"); //$NON-NLS-1$
				case DASHED_3:
					return Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.dashed3"); //$NON-NLS-1$
				case DASHED_4:
					return Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.dashed4"); //$NON-NLS-1$
			}
			return Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.solid");			 //$NON-NLS-1$
		}		
	};
	
	protected PropertyDescriptor createLineStylePD() 
	{
//		List<LineStyle> lineStyles = Utils.enum2List(Enum.valueOf(LineStyle.class, "SOLID")); 
		List<LineStyle> lineStyles = CollectionUtil.enum2List(LineStyle.SOLID);		
		PropertyDescriptor desc = new GenericComboBoxPropertyDescriptor<LineStyle>(
				ShapeDrawComponent.PROP_LINE_STYLE,
				Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.lineStyle"),  //$NON-NLS-1$
				lineStyles,
				lineStyleLabelProvider);
		desc.setCategory(CATEGORY_LINE);
		return desc;		
	}
	
	protected PropertyDescriptor createFillPD() 
	{
		PropertyDescriptor desc = new CheckboxPropertyDescriptor(ShapeDrawComponent.PROP_FILL,
				Messages.getString("org.nightlabs.editor2d.model.ShapeDrawComponentPropertySource.fill"));				 //$NON-NLS-1$
		return desc;		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) 
	{
		Object o = super.getPropertyValue(id);
		if (o != null) 
			return o;
		else 
		{
			if (id.equals(ShapeDrawComponent.PROP_FILL_COLOR)) {
				return getShapeDrawComponent().getFillColor();
			}
			else if (id.equals(ShapeDrawComponent.PROP_LINE_COLOR)) {
				return getShapeDrawComponent().getLineColor();
			}
			else if (id.equals(ShapeDrawComponent.PROP_LINE_WIDTH)) {
				return getShapeDrawComponent().getLineWidth();
			}
			else if (id.equals(ShapeDrawComponent.PROP_LINE_STYLE)) {
				return getShapeDrawComponent().getLineStyle();
			}
			else if (id.equals(ShapeDrawComponent.PROP_FILL)) {
				return getShapeDrawComponent().isFill();
			}
			else if (id.equals(ShapeDrawComponent.PROP_SHOW_STROKE)) {
				return getShapeDrawComponent().isShowStroke();
			}
			
			return null;			
		}
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) 
	{
		super.setPropertyValue(id, value);
		
		if (id.equals(ShapeDrawComponent.PROP_FILL_COLOR)) {
			getShapeDrawComponent().setFillColor((Color)value);
		}
		else if (id.equals(ShapeDrawComponent.PROP_LINE_COLOR)) {
			getShapeDrawComponent().setLineColor((Color)value);
		}
		else if (id.equals(ShapeDrawComponent.PROP_LINE_WIDTH)) {
			getShapeDrawComponent().setLineWidth((Integer)value);
		}
		else if (id.equals(ShapeDrawComponent.PROP_LINE_STYLE)) {
//			getShapeDrawComponent().setLineStyle((Integer)value);
			getShapeDrawComponent().setLineStyle((LineStyle)value);			
		}
		else if (id.equals(ShapeDrawComponent.PROP_FILL)) {
			getShapeDrawComponent().setFill((Boolean)value);
		}
		else if (id.equals(ShapeDrawComponent.PROP_SHOW_STROKE)) {
			getShapeDrawComponent().setShowStroke((Boolean)value);
		}				
	}	
	
	protected ShapeDrawComponent getShapeDrawComponent() {
		return (ShapeDrawComponent) drawComponent;
	}
}
