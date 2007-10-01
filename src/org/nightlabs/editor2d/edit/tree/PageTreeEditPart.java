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
package org.nightlabs.editor2d.edit.tree;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.model.PagePropertySource;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PageTreeEditPart 
extends DrawComponentContainerTreeEditPart 
{ 
//	public static Image PAGE_ICON = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
//			PageTreeEditPart.class).createImage();	
	public static Image PAGE_ICON = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
			PageTreeEditPart.class, "", ImageFormat.gif).createImage();	 //$NON-NLS-1$
	
	/**
	 * @param model the PageDrawComponent to initialze this PageTreeEditPart with 
	 */
	public PageTreeEditPart(PageDrawComponent model) {
		super(model);
	}

//	@Override
//	protected Image getImage() {
//		return PAGE_ICON;
//	}
	@Override
	protected Image getOutlineImage() {
		return PAGE_ICON;
	}

	protected PageDrawComponent getPageDrawComponent() {
		return (PageDrawComponent) getDrawComponent();
	}
	
	@Override	
	public IPropertySource getPropertySource()
  {
    if (propertySource == null) {
      propertySource = new PagePropertySource(getPageDrawComponent());
    }
    return propertySource;
  }
}
