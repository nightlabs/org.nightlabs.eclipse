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

package org.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;

import org.apache.log4j.Logger;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.model.EllipsePropertySource;


public class EllipseEditPart 
extends ShapeDrawComponentEditPart 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(EllipseEditPart.class);
	
  public EllipseEditPart(EllipseDrawComponent drawComponent) {
    super(drawComponent);
  }

	protected EllipseDrawComponent getEllipseDrawComponent() {
		return (EllipseDrawComponent) getModel();
	}
	
  public IPropertySource getPropertySource()
  {
    if (propertySource == null)
    {
      propertySource =
        new EllipsePropertySource(getEllipseDrawComponent());
    }
    return propertySource;
  } 
  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(EllipseDrawComponent.PROP_END_ANGLE)) {
			refreshVisuals();	
			return;
		}
		else if (propertyName.equals(EllipseDrawComponent.PROP_START_ANGLE)) {
			refreshVisuals();
			return;
		}		
	}
       
}
