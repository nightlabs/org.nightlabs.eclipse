/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.labelprovider;

import java.awt.Color;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.util.ImageUtil;

public class AWTColorLabelProvider 
//implements ILabelProvider
extends LabelProvider
{  
  public AWTColorLabelProvider() 
  {
    super();    
  }

  public Image getImage(Object element) 
  {
    if (element instanceof Color)
      return ImageUtil.createColorImage((Color)element);
    
    return null;
  }

  public String getText(Object element) 
  {
    if (element instanceof Color)
    {
      Color color = (Color) element;
      return
      	"("+color.getRed()+","+ //$NON-NLS-1$ //$NON-NLS-2$
      	color.getGreen()+","+ //$NON-NLS-1$
      	color.getBlue()+")"; //$NON-NLS-1$
    }
    return element == null ? "" : element.toString(); //$NON-NLS-1$
  }

  public void dispose() 
  {
  }
}
