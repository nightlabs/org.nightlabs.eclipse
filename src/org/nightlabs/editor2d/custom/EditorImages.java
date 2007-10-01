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

package org.nightlabs.editor2d.custom;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.editor2d.EditorPlugin;

public class EditorImages 
{
  public static final ImageDescriptor ROTATE_NW_16;
  public static final ImageDescriptor ROTATE_SW_16;
  public static final ImageDescriptor ROTATE_NE_16;
  public static final ImageDescriptor ROTATE_SE_16;
     
  static 
  {
    ROTATE_NW_16 = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
    		EditorImages.class, "RotateNW"); //$NON-NLS-1$
    ROTATE_NE_16 = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
    		EditorImages.class, "RotateNE"); //$NON-NLS-1$
    ROTATE_SE_16 = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
    		EditorImages.class, "RotateSE"); //$NON-NLS-1$
    ROTATE_SW_16 = SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), 
    		EditorImages.class, "RotateSW");  	 //$NON-NLS-1$
  }
  
  /**
   * Returns the RotationImage or ShearImage corresponding to the given direction, defined in 
   * {@link PositionConstants}.
   * @param direction The relative direction of the desired Rotate/Shear Image
   * @return The appropriate directional Rotate/Shear Image
   */
  public static Image getDirectionalRotationImage(int direction) 
  {
    switch (direction) 
    {
      case PositionConstants.SOUTH_EAST:
        return ROTATE_SE_16.createImage();
      case PositionConstants.SOUTH_WEST:
        return ROTATE_SW_16.createImage();
      case PositionConstants.NORTH_EAST:
        return ROTATE_NE_16.createImage();
      case PositionConstants.NORTH_WEST:
        return ROTATE_NW_16.createImage();
      default:
        break;
    }
    return null;
  }  
}
