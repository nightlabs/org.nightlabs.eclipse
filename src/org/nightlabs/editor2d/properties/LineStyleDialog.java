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

package org.nightlabs.editor2d.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.nightlabs.editor2d.resource.Messages;

public class LineStyleDialog 
extends ListDialog
{
  public LineStyleDialog(Shell parent) 
  {
    super(parent);
    setTitle(Messages.getString("org.nightlabs.editor2d.properties.LineStyleDialog.title")); //$NON-NLS-1$
    setMessage(Messages.getString("org.nightlabs.editor2d.properties.LineStyleDialog.message")); //$NON-NLS-1$
    setContentProvider(contentProvider);
    setLabelProvider(new LineStyleLabelProvider());
    setInput(null);
  }
    
  protected IStructuredContentProvider contentProvider = new IStructuredContentProvider() 
  {  
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
  
    public void dispose() {
    }
  
    public Object[] getElements(Object inputElement) 
    {
      List<Integer> lineStyles = new ArrayList<Integer>();
      lineStyles.add(new Integer(SWT.LINE_SOLID));
      lineStyles.add(new Integer(SWT.LINE_DOT));
      lineStyles.add(new Integer(SWT.LINE_DASH));
      lineStyles.add(new Integer(SWT.LINE_DASHDOT));
      lineStyles.add(new Integer(SWT.LINE_DASHDOTDOT));
      return lineStyles.toArray();
    }
  }; 
  
}
