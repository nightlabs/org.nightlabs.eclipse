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
package org.nightlabs.editor2d.render;

import java.util.Iterator;

import org.eclipse.draw2d.Graphics;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class Draw2DBaseRenderer 
implements Draw2DRenderContext 
{
	public Draw2DBaseRenderer() 
	{
		super();
		init();
	}

	/**
	 * @see org.nightlabs.editor2d.render.Draw2DRenderContext#paint(org.nightlabs.editor2d.DrawComponent, org.eclipse.draw2d.Graphics)
	 */
  /**
	 * The Standard Implementation of the paint-Method
	 * does by default nothing.
	 * It only checks if the given DrawComponent is a DrawComponentContainer
	 * and if so, it paints all its children. 
   */
  public void paint(DrawComponent dc, Graphics g) 
  {
  	if (dc instanceof DrawComponentContainer) {
  		DrawComponentContainer container = (DrawComponentContainer) dc;
  		if (container != null) {
  			for (Iterator<DrawComponent> it = container.getDrawComponents().iterator(); it.hasNext(); ) {
  				DrawComponent d = it.next();
  				Renderer r = d.getRenderer();
  				RenderContext rc = r.getRenderContext(Draw2DRenderContext.RENDER_CONTEXT_TYPE);
  				if (rc != null && rc instanceof Draw2DRenderContext) {
  					Draw2DRenderContext d2drc = (Draw2DRenderContext) rc; 
  					d2drc.paint(dc, g);
  				}
  			}
  		}
  	}
  }

  /**
   * Inheritans of this class can override this Method
   * in order to initialize things, needed for rendering.
   *
   * By Default this Method is empty.
   * This Method will be called in the Constructor 
   */
  protected void init() 
  {    
  };
  
  protected String renderContextType = Draw2DRenderContext.RENDER_CONTEXT_TYPE;
  public String getRenderContextType() {
  	return renderContextType;
  }
  public void setRenderContextType(String newRenderContextType) {
  	this.renderContextType = newRenderContextType;
  }  
}
