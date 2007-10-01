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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.RootDrawComponent;

public abstract class AbstractEditorAction 
extends EditorPartAction
{
	public AbstractEditorAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	public AbstractEditorAction(AbstractEditor editor) {
		super(editor);
	}

//	protected abstract boolean calculateEnabled();
//	protected abstract void init();
	
	/**
	 * 
	 * @return the AbstractEditor
	 * @see org.nightlabs.editor2d.AbstractEditor
	 */
	public AbstractEditor getEditor() {
		return (AbstractEditor) getWorkbenchPart();
	}
	
	/**
	 * 
	 * @return the RootDrawComponent of the AbstractEditor
	 * @see org.nightlabs.editor2d.RootDrawComponent
	 */
	public RootDrawComponent getRootDrawComponent() {
		return getEditor().getRootDrawComponent();
	}	
	
	/**
	 * 
	 * @return the currentLayer of the RootDrawComponent
	 */
	public Layer getCurrentLayer() {
		return getRootDrawComponent().getCurrentLayer();
	}
	
	/**
	 * 
	 * @return the GraphicalViewer of the AbstractEditor
	 * @see org.eclipse.gef.GraphicalViewer
	 */
	public GraphicalViewer getGraphicalViewer() {
		return getEditor().getOutlineGraphicalViewer();		
	}
	
	/**
	 * 
	 * @param model the DrawComponent to find its EditPart
	 * @return the corresponding EditPart
	 * 
	 */
	public EditPart getEditPart(DrawComponent model) {
		return (EditPart) getGraphicalViewer().getEditPartRegistry().get(model);
	}
	
	/**
	 * 
	 * @param drawComponents a List of DrawComponents to find a EditParts for
	 * @return a List of the corresponding EditParts
	 */
	public List<EditPart> getEditParts(List<DrawComponent> drawComponents) 
	{
		List<EditPart> editParts = new ArrayList<EditPart>();
		for (Iterator<DrawComponent> it = drawComponents.iterator(); it.hasNext(); ) 
		{
			DrawComponent dc = it.next();
			EditPart ep = getEditPart(dc);
			editParts.add(ep);
		}
		return editParts;
	}
	
	/**
	 * selects the EditParts in the GraphicalViewer for the the given List
	 * of drawComponents  
	 * @param drawComponents
	 */
	public void selectEditPart(List drawComponents) 
	{
		List editParts = getEditParts(drawComponents);
		getGraphicalViewer().setSelection(new StructuredSelection(editParts));
	}	
	
	/**
	 * 
	 * @return the Shell of the Site for the AbstractEditor
	 */
	public Shell getShell() {
		return getEditor().getSite().getShell();
	}
	
	/** 
	 * @return true if the AbstractEditor is the active Editor of the Workbench 
	 */
	public boolean isActiveEditor()
	{
		// TODO: find out why getActivePage() always return null
		if (getWorkbenchPart().getSite().getWorkbenchWindow().getActivePage() != null) 
		{
			if (getWorkbenchPart().getSite().getWorkbenchWindow().getActivePage().getActiveEditor().equals(getEditor())) {
				return true;
			}			
		}
		return false;
	}	
}
