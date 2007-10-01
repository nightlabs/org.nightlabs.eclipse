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

package org.nightlabs.editor2d.request;

import org.eclipse.draw2d.Shape;
import org.nightlabs.editor2d.j2d.GeneralShape;


public class EditorCreateShapeRequest  
extends EditorCreateRequest
implements EditorShapeRequest
{
  public static final int BOUNDS_FIX_MODE = 1;
  public static final int BOUNDS_UNFIX_MODE = 2;
  
  public EditorCreateShapeRequest() {
    super();
  }

  public EditorCreateShapeRequest(Object type) {
    super(type);
  }  
  
  protected int mode = BOUNDS_UNFIX_MODE;    
  public int getMode() {
    return mode;
  }
  public void setMode(int mode) {
    this.mode = mode;
  }
  
  protected GeneralShape gp;
  public GeneralShape getGeneralShape() {
    return gp;
  }  
  public void setGeneralShape(GeneralShape gp) {
    this.gp = gp;
  }
  
  protected Shape shape;  
  public Shape getShape() {
    return shape;
  }
  public void setShape(Shape shape) {
    this.shape = shape;
  }  
  
  protected boolean useShape = false;    
  public boolean isUseShape() {
    return useShape;
  }
  public void setUseShape(boolean useShape) {
    this.useShape = useShape;
  }
      
//  public IModelCreationFactory getModelCreationFactory() {
//  	return (IModelCreationFactory) getFactory();
//  }
}
