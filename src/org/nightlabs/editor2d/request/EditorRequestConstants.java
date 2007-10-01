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

import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

/**
 * The set of constants used to identify <code>Requests</code> by their {@link
 * Request#getType() type}. Applications can extend this set of constants with their own.
 */
public interface EditorRequestConstants 
extends RequestConstants
{
  /**
   * Indicates the creation of a new Shape. 
   */
  String REQ_CREATE_SHAPE  = "create Shape";//$NON-NLS-1$

  /**
   * Indicates the Editing of a Shape. 
   */  
  String REQ_EDIT_SHAPE = "edit Shape";//$NON-NLS-2$ //$NON-NLS-1$

  /**
   * Indicates the Zooming of the View (e.g. with a ZoomTool) 
   */    
  String REQ_ZOOM_RECT = "zoom Rectangle";//$NON-NLS-3$ //$NON-NLS-1$
  
  /**
   * Indicates the Rotation of a DrawComponent
   */
  String REQ_ROTATE = "rotate";//$NON-NLS-4$ //$NON-NLS-1$
  
  /**
   * Indicates the Editing of the Rotation Center
   */  
  String REQ_EDIT_ROTATE_CENTER = "edit Rotation Center";//$NON-NLS-5$ //$NON-NLS-1$
  
  /**
   * Indicates the Shearing of a DrawComponent
   */
  String REQ_SHEAR = "shear"; //$NON-NLS-1$
  
//  /*
//   * Indicates the creation of a Text
//   */
//  String REQ_CREATE_TEXT = "create Text";//$NON-NLS-5$
}
