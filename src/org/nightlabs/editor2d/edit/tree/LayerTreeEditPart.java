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
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.model.LayerPropertySource;
import org.nightlabs.editor2d.views.LayerView;


public class LayerTreeEditPart 
extends DrawComponentContainerTreeEditPart 
{
//	public static Image LAYER_ICON = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
//			LayerView.class).createImage();	
	public static Image LAYER_ICON = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
			LayerView.class, "", ImageFormat.gif).createImage();	 //$NON-NLS-1$
	
//	@Override  
//  protected Image getImage() {
//    return LAYER_ICON;
//  }  
	@Override  
  protected Image getOutlineImage() {
    return LAYER_ICON;
  }
	
  public LayerTreeEditPart(Layer layer) {
    super(layer);
  }
		
  public Layer getLayer() {
  	return (Layer) getModel();
  }
  
	@Override
  public IPropertySource getPropertySource()
  {
    if (propertySource == null)
    {
      propertySource =
        new LayerPropertySource(getLayer());
    }
    return propertySource;
  } 
  
  /**
   * Creates and installs pertinent EditPolicies.
   */
  protected void createEditPolicies() 
  {
    // TODO: Must override EditPolicy.TREE_CONTAINER_ROLE with a EditPolicy which does
    // support dragging of layers   	
  	super.createEditPolicies();
  }
}
