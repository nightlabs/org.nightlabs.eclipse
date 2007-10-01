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

package org.nightlabs.editor2d.actions;

import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.ui.IEditorPart;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.RootDrawComponent;


public abstract class AbstractRendererModeAction 
extends EditorPartAction 
{  
  protected RootDrawComponent root;
  
  /**
   * @param editor
   */
  public AbstractRendererModeAction(IEditorPart editor) {
    super(editor);
  }

  // TODO: check currentMode in RenderModeManager and if != getRenderMode() return true else false
  protected boolean calculateEnabled() 
  {
    return true;
  }
    
  public void run() 
  {
    if (getEditorPart() instanceof AbstractEditor) {
      AbstractEditor editor = ((AbstractEditor)getEditorPart());
      root = editor.getRootDrawComponent();
//      root.setRenderMode(getRenderMode());
      root.getRenderModeManager().setCurrentRenderMode(getRenderMode());
      editor.updateViewer();      
    }    
  }
    
  public abstract String getRenderMode();
  protected abstract void init();
}
