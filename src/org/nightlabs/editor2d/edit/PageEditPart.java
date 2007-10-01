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
package org.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.figures.PageFreeformFigure;
import org.nightlabs.editor2d.figures.RendererFigure;
import org.nightlabs.editor2d.model.PagePropertySource;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PageEditPart 
extends AbstractDrawComponentContainerEditPart 
{
	/**
	 * @param page the PageDrawComponent to initalize this PageEditPart with
	 */
	public PageEditPart(PageDrawComponent page) {
		super(page);
	}

	protected PageDrawComponent getPageDrawComponent() {
		return (PageDrawComponent) getDrawComponent();
	}
	
  protected IFigure createFigure() 
  {    
  	RendererFigure f = new PageFreeformFigure();
    f.setDrawComponent(getPageDrawComponent());    
    addRenderer(f);
    
		f.setLayoutManager(new FreeformLayout());    
		return f;  
  }	
  
  public IPropertySource getPropertySource()
  {
    if (propertySource == null) {
      propertySource = new PagePropertySource(getPageDrawComponent());
    }
    return propertySource;
  }  
  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		String propertyName = evt.getPropertyName();
//		if (propertyName.equals(PageDrawComponent.PROP_RESOLUTION)) {
//			refresh();
//			return;
//		}
		if (propertyName.equals(PageDrawComponent.PROP_ORIENTATION)) {
			refreshVisuals();
			return;
		}
		if (propertyName.equals(PageDrawComponent.PROP_PAGE_BOUNDS)) {
			refreshVisuals();
			return;
		}
		if (propertyName.equals(PageDrawComponent.PROP_PAGE_BOUNDS)) {
			refreshVisuals();
			return;
		}				
		super.propertyChanged(evt);		
	}
}
