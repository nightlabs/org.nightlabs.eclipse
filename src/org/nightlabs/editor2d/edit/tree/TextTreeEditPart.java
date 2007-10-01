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

import java.beans.PropertyChangeEvent;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.editor2d.AbstractPaletteFactory;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.model.TextPropertySource;

public class TextTreeEditPart 
extends DrawComponentTreeEditPart 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(TextTreeEditPart.class);
	
//	public static final Image TEXT_ICON = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
//			AbstractPaletteFactory.class, "Text").createImage();		
	public static final Image TEXT_ICON = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
			AbstractPaletteFactory.class, "Text", ImageFormat.gif).createImage();   //$NON-NLS-1$
	
  public TextTreeEditPart(TextDrawComponent drawComponent) {
    super(drawComponent);
  }

//  protected Image getImage() {
//    return TEXT_ICON;
//  }
  protected Image getOutlineImage() {
    return TEXT_ICON;
  }  

  public TextDrawComponent getTextDrawComponent() {
    return (TextDrawComponent) getModel();
  }
  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(TextDrawComponent.PROP_FONT)) {
			logger.debug(propertyName +" changed!"); //$NON-NLS-1$
			refreshVisuals();			
		}
//		else if (propertyName.equals(TextDrawComponent.PROP_FONT_NAME)) {
//			LOGGER.debug(propertyName +" changed!");
//			refreshVisuals();			
//		}
//		else if (propertyName.equals(TextDrawComponent.PROP_FONT_SIZE)) {
//			LOGGER.debug(propertyName +" changed!");
//			refreshVisuals();			
//		}
		else if (propertyName.equals(TextDrawComponent.PROP_TEXT)) {
			logger.debug(propertyName +" changed!"); //$NON-NLS-1$
			refreshVisuals();			
		}
//		else if (propertyName.equals(TextDrawComponent.PROP_BOLD)) {
//			LOGGER.debug(propertyName +" changed!");
//			refreshVisuals();			
//		}
//		else if (propertyName.equals(TextDrawComponent.PROP_ITALIC)) {
//			LOGGER.debug(propertyName +" changed!");
//			refreshVisuals();			
//		}		
	}
	
	public IPropertySource getPropertySource()
  {
    if (propertySource == null)
    {
      propertySource =
        new TextPropertySource(getTextDrawComponent());
    }
    return propertySource;
  }   
}
