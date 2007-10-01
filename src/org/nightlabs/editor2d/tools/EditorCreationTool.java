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

package org.nightlabs.editor2d.tools;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.nightlabs.editor2d.model.IModelCreationFactory;
import org.nightlabs.editor2d.request.EditorCreateShapeRequest;
import org.nightlabs.editor2d.request.EditorRequestConstants;


public abstract class EditorCreationTool 
extends CreationTool 
implements EditorRequestConstants
{
  protected SnapToHelper helper;
  
//  /**
//   * @param aFactory
//   */
//  public EditorCreationTool(CreationFactory aFactory) {
//    super(aFactory);
//  }
  public EditorCreationTool(IModelCreationFactory factory) {
    super(factory);
  }

  public IModelCreationFactory getModelCreationFactory() {
  	return (IModelCreationFactory) getFactory();
  }
  
  /**
   * Creates a {@link EditorCreateShapeRequest} and sets this tool's factory on the request.
   * @see org.eclipse.gef.tools.TargetingTool#createTargetRequest()
   */
  protected Request createTargetRequest() 
  {
  	EditorCreateShapeRequest request = new EditorCreateShapeRequest();
  	request.setFactory(getFactory());
  	return request;
  }
  
  protected Point getRealLocation() 
  {
    Point p = getLocation();
    Point realLocation;
    
    EditPartViewer view = getCurrentViewer();
    if (view instanceof ScrollingGraphicalViewer) 
    {
      ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer) view;
      FigureCanvas canvas = (FigureCanvas) viewer.getControl();
      Viewport viewport = canvas.getViewport();
      Point viewLocation = viewport.getViewLocation();
      realLocation = p.getTranslated(viewLocation);                    
      return realLocation;      
    }
    return p;
  }
  
  protected EditorCreateShapeRequest getEditorCreateRequest() {
  	return (EditorCreateShapeRequest)getTargetRequest();
  }  
    
  /**
   * @see org.eclipse.gef.tools.AbstractTool#getDebugName()
   */
  protected String getDebugName() {
  	return "EditorCreation Tool";//$NON-NLS-1$
  }   
}


