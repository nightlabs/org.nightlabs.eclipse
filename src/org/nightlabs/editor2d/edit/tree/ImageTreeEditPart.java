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

package org.nightlabs.editor2d.edit.tree;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.editor2d.AbstractPaletteFactory;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.model.ImagePropertySource;

public class ImageTreeEditPart 
extends DrawComponentTreeEditPart 
{
//	public static Image IMAGE_ICON = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
//			AbstractPaletteFactory.class, "Image").createImage();	
	public static Image IMAGE_ICON = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
			AbstractPaletteFactory.class, "Image", ImageFormat.gif).createImage();	 //$NON-NLS-1$
	
  public ImageTreeEditPart(ImageDrawComponent drawComponent) {
    super(drawComponent);
  }

//  protected Image getImage() {
//    return IMAGE_ICON;
//  }
  protected Image getOutlineImage() {
    return IMAGE_ICON;
  }
  
  public ImageDrawComponent getImageDrawComponent() {
  	return (ImageDrawComponent) getModel();
  }
  
  public IPropertySource getPropertySource()
  {
    if (propertySource == null) 
    {
      propertySource =
        new ImagePropertySource(getImageDrawComponent());
    }
    return propertySource;
  }
}
