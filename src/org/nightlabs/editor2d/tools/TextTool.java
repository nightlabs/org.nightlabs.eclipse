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

import org.eclipse.gef.Request;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.jface.dialogs.Dialog;
import org.nightlabs.editor2d.dialog.CreateTextDialog;
import org.nightlabs.editor2d.model.IModelCreationFactory;
import org.nightlabs.editor2d.request.TextCreateRequest;

public class TextTool 
extends CreationTool
{
	public TextTool(IModelCreationFactory factory) {
		super(factory);
	}
	
  /**
   * Creates a {@link TextCreateRequest} and sets this tool's factory on the request.
   * @see org.eclipse.gef.tools.TargetingTool#createTargetRequest()
   */
  protected Request createTargetRequest() 
  {
    TextCreateRequest request = new TextCreateRequest();
    request.setFactory(getFactory());
    return request;
  }  
  
  public TextCreateRequest getTextCreateRequest() 
  {
    return (TextCreateRequest) getTargetRequest();
  }
  
  protected boolean handleButtonDown(int button) 
  {
    CreateTextDialog dialog = new CreateTextDialog(
        getCurrentViewer().getControl().getShell(), 
        getTextCreateRequest()
      );
    dialog.open();
        
    if (dialog.getReturnCode() == Dialog.OK) 
    {
      performCreation(1);
      return true;
    }
    return false;
  } 
    
}
