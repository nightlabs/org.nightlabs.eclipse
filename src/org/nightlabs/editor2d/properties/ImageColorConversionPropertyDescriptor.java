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
package org.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.property.XPropertyDescriptor;
import org.nightlabs.editor2d.ImageDrawComponent;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ImageColorConversionPropertyDescriptor 
extends XPropertyDescriptor 
{

	/**
	 * @param imageDC the {@link ImageDrawComponent} to edit
	 * @param id the id of the property to edit
	 * @param displayName the name of the property
	 */
	public ImageColorConversionPropertyDescriptor(ImageDrawComponent imageDC, Object id, String displayName) {
		super(id, displayName);
		this.imageDC = imageDC;
	}

//	/**
//	* @param imageDC the {@link ImageDrawComponent} to edit
//	* @param id the id of the property to edit
//	* @param displayName the name of the property
//	* @param readOnly determines if the property is readonly or not
//	*/
//	public ImageColorConversionPropertyDescriptor(ImageDrawComponent imageDC, Object id, String displayName, boolean readOnly) {
//		super(id, displayName, readOnly);
//		this.imageDC = imageDC;
//	}
	
	protected ImageDrawComponent imageDC = null;

	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		return new ImageColorConversionPropertyCellEditor(imageDC, parent);
	}
	
}
