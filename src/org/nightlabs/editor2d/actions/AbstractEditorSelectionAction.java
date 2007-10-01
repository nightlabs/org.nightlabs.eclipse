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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.GroupDrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.LineDrawComponent;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RectangleDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.edit.EllipseEditPart;
import org.nightlabs.editor2d.edit.GroupEditPart;
import org.nightlabs.editor2d.edit.ImageEditPart;
import org.nightlabs.editor2d.edit.LayerEditPart;
import org.nightlabs.editor2d.edit.LineEditPart;
import org.nightlabs.editor2d.edit.RootDrawComponentEditPart;
import org.nightlabs.editor2d.edit.PageEditPart;
import org.nightlabs.editor2d.edit.RectangleEditPart;
import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.edit.TextEditPart;

public abstract class AbstractEditorSelectionAction 
extends SelectionAction
{
	public AbstractEditorSelectionAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	public AbstractEditorSelectionAction(AbstractEditor editor) {
		super(editor);
	}

	protected abstract boolean calculateEnabled(); 
	protected abstract void init();	
	public abstract void run();	

	public boolean isActiveEditor()
	{
		if (getWorkbenchPart().getSite().getWorkbenchWindow().getActivePage().getActiveEditor().equals(getEditor())) {
			return true;
		}
		return false;
	}
	
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
	public RootDrawComponent getRootDrawComponent() 
	{
		return getEditor().getRootDrawComponent();
	}	
	
	/**
	 * checks if the selected objects contain minimum so many EditPart or EditPart-Models
	 * as the given amount of the given class
	 * 
	 * @param clazz the Class to search for 
	 * @param amount the minimum amount of occurences of the given class 
	 * in the selected objects
	 * @param model determines if the Class-Check should be performed on the
	 * selected EditParts or the model (DrawComponent) of the EditParts
	 * @return true if the selected objects contain minimum so many EditPart
	 * as the given amount of the given class
	 */
	public boolean selectionContains(Class clazz, int amount, boolean model) 
	{
		return selectionContains(new Class[] {clazz}, amount, model);
	}

	/**
	 * checks if the selected objects contain minimum so many EditPart or EditPart-Models
	 * as the given amount of the given classes
	 * 
	 * @param clazzes an Array of classes to search for
	 * @param amount the minimum amount of occurences of the given class 
	 * in the selected objects
	 * @param model determines if the Class-Check should be performed on the
	 * selected EditParts or the model (DrawComponent) of the EditParts
	 * @return true if the selected objects contain minimum so many EditPart
	 * as the given amount of the given class
	 */
	public boolean selectionContains(Class[] clazzes, int amount, boolean model) 
	{
		if (!getSelectedObjects().isEmpty()) 
		{
			int counter = 0;
			for (Iterator it = getSelectedObjects().iterator(); it.hasNext(); ) 
			{
				EditPart editPart = (EditPart) it.next();
				Class c = null;
				if (!model) 
					c = editPart.getClass();
				else
					c = editPart.getModel().getClass();
				
				for (int i=0; i<clazzes.length; i++) 
				{
					Class clazz = clazzes[i];
					if (clazz.isAssignableFrom(c)) {
						counter++;
						if (amount == counter)
							return true;
					}										
				}
			}
		}
		return false;
	}
		
	/**
	 * A Convenice Method which calls selectionContains with the amount 1
	 * @param clazz the Class to search for
	 * @param model determines if the search is performed on the selected EditParts or
	 * the Model of the EditParts
	 * @see #selectionContains(Class clazz, int amount, boolean model)
	 */
	public boolean selectionContains(Class clazz, boolean model) {
		return selectionContains(clazz, 1, model);
	}
	
	private static List EMPTY_LIST = Collections.EMPTY_LIST; 
			
	/**
	 * 
	 * @param clazz the Class to search for
	 * @param model determines if the Class-Check should be performed on the
	 * selected EditParts or the model (DrawComponent) of the EditParts
	 * @return a Collection of all objects from the selection which are assignable 
	 * from the given class
	 */
	public Collection getSelection(Class clazz, boolean model) 
	{
		return getSelection(new Class[] {clazz}, model);
	}

	/**
	 * 
	 * @param clazzes the Class[] to search for
	 * @param model determines if the Class-Check should be performed on the
	 * selected EditParts or the model (DrawComponent) of the EditParts
	 * @return a List of all objects from the selection which are assignable 
	 * from the given classes
	 */
	public Collection getSelection(Class[] clazzes, boolean model) 
	{
		if (!getSelectedObjects().isEmpty()) 
		{
			Collection selection = new HashSet();			
			for (Iterator it = getSelectedObjects().iterator(); it.hasNext(); ) 
			{
				EditPart editPart = (EditPart) it.next();
				Class c = null;
				if (!model)
					c = editPart.getClass();
				else
					c = editPart.getModel().getClass();
				
				for (int i=0; i<clazzes.length; i++) 
				{
					Class clazz = clazzes[i];
					if (clazz.isAssignableFrom(c)) 
					{
						if (!model) 
							selection.add(clazz.cast(editPart));							
						else
							selection.add(clazz.cast(editPart.getModel()));							
					}					
				}
			}
			return selection;
		}
		return EMPTY_LIST;
	}
			
	/**
	 * 
	 * @param clazz the Class to search for
	 * @param model determines if the Class-Check should be performed on the
	 * selected EditParts or the model (DrawComponent) of the EditParts
	 * @return a Collection of all objects from the selection which are assignable 
	 * from the given class
	 */
	public List getSelectionAsList(Class clazz, boolean model) 
	{
		return getSelectionAsList(new Class[] {clazz}, model);
	}
	
	/**
	 * 
	 * @param clazzes the Class[] to search for
	 * @param model determines if the Class-Check should be performed on the
	 * selected EditParts or the model (DrawComponent) of the EditParts
	 * @return a List of all objects from the selection which are assignable 
	 * from the given classes
	 */
	public List getSelectionAsList(Class[] clazzes, boolean model) 
	{
		if (!getSelectedObjects().isEmpty()) 
		{
			List selection = new LinkedList();			
			for (Iterator it = getSelectedObjects().iterator(); it.hasNext(); ) 
			{
				EditPart editPart = (EditPart) it.next();
				Class c = null;
				if (!model)
					c = editPart.getClass();
				else
					c = editPart.getModel().getClass();
				
				for (int i=0; i<clazzes.length; i++) 
				{
					Class clazz = clazzes[i];
					if (clazz.isAssignableFrom(c)) 
					{
						Object o = null;
						if (!model)
							o = clazz.cast(editPart);
						else
							o = clazz.cast(editPart.getModel());
							
						if (!selection.contains(o))
							selection.add(o);													
					}					
				}
			}
			return selection;
		}
		return EMPTY_LIST;
	}
	
	private Class[] defaultEditPartExcludes = null; 
	private Class[] defaultModelExcludes = null;
	
	public Class[] getDefaultExcludes(boolean model) 
	{
		if (!model) {
			if (defaultEditPartExcludes == null) {
				defaultEditPartExcludes = new Class[4];
				defaultEditPartExcludes[0] = RootEditPart.class;							
				defaultEditPartExcludes[1] = LayerEditPart.class;
				defaultEditPartExcludes[2] = PageEditPart.class;
				defaultEditPartExcludes[3] = RootDrawComponentEditPart.class;				
			}
			return defaultEditPartExcludes;
		}
		else {
			if (defaultModelExcludes == null) {
				defaultModelExcludes = new Class[3];
				defaultModelExcludes[0] = RootDrawComponent.class;
				defaultModelExcludes[1] = Layer.class;
				defaultModelExcludes[2] = PageDrawComponent.class;
			}
			return defaultModelExcludes;
		}
	}
		
	private Collection<Class> defaultExcludeList = null;
	public Collection<Class> getDefaultExcludeList(boolean model) 
	{
		if (defaultExcludeList == null) 
		{
			Class[] excludes = getDefaultExcludes(model);
			defaultExcludeList = new ArrayList<Class>(excludes.length);		
			for (int i=0; i<excludes.length; i++) {
				defaultExcludeList.add(excludes[i]);
			}			
		}
		return defaultExcludeList;
	}
	
	private Class[] defaultEditPartIncludes = null; 
	private Class[] defaultModelIncludes = null;	
	public Class[] getDefaultIncludes(boolean model) 
	{
		if (!model) {
			if (defaultEditPartIncludes == null) {
				defaultEditPartIncludes = new Class[7];
				defaultEditPartIncludes[0] = RectangleEditPart.class;
				defaultEditPartIncludes[1] = EllipseEditPart.class;
				defaultEditPartIncludes[2] = LineEditPart.class;
				defaultEditPartIncludes[3] = ImageEditPart.class;
				defaultEditPartIncludes[4] = TextEditPart.class;			
				defaultEditPartIncludes[5] = GroupEditPart.class;
				defaultEditPartIncludes[6] = ShapeDrawComponentEditPart.class;				
			}
			return defaultEditPartIncludes;
		}
		else {
			if (defaultModelIncludes == null) {
				defaultModelIncludes = new Class[7];
				defaultModelIncludes[0] = RectangleDrawComponent.class;
				defaultModelIncludes[1] = EllipseDrawComponent.class;
				defaultModelIncludes[2] = LineDrawComponent.class;
				defaultModelIncludes[3] = ImageDrawComponent.class;
				defaultModelIncludes[4] = TextDrawComponent.class;
				defaultModelIncludes[5] = GroupDrawComponent.class;
				defaultModelIncludes[6] = ShapeDrawComponent.class;				
			}
			return defaultModelIncludes;
		}
	}	
	
	private Collection<Class> defaultIncludeList = null;
	public Collection<Class> getDefaultIncludeList(boolean model) 
	{
		if (defaultIncludeList == null) {
			Class[] includes = getDefaultIncludes(model);
			defaultIncludeList = new ArrayList<Class>(includes.length);		
			for (int i=0; i<includes.length; i++) {
				defaultIncludeList.add(includes[i]);
			}			
		}
		return defaultIncludeList;		
	}
	
//	public List getDefaultSelection(boolean model) 
//	{
//		return getSelection(getDefaultIncludes(model), model);		
//	}
	public Collection getDefaultSelection(boolean model) 
	{
		return getSelection(getDefaultIncludes(model), model);		
	}
	
	public IStructuredSelection getStructuredSelection() {
		return (IStructuredSelection) getSelection();
	}
	
	public EditPart getPrimarySelected() {
		return (EditPart) getStructuredSelection().getFirstElement();
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
	public List getEditParts(List drawComponents) 
	{
		List editParts = new ArrayList();
		for (Iterator it = drawComponents.iterator(); it.hasNext(); ) 
		{
			DrawComponent dc = (DrawComponent) it.next();
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
	public void selectEditParts(List drawComponents) 
	{
		List editParts = getEditParts(drawComponents);
		getGraphicalViewer().setSelection(new StructuredSelection(editParts));
	}

	/**
	 * selects the EditParts in the GraphicalViewer for the the given List
	 * of drawComponents  
	 * @param dc the DrawComponent to selected its EditPart
	 */
	public void selectEditPart(DrawComponent dc) 
	{
		EditPart ep = getEditPart(dc);
		getGraphicalViewer().setSelection(new StructuredSelection(ep));
	}
	
	/**
	 * 
	 * @return the Shell of the Site for the AbstractEditor
	 */
	public Shell getShell() {
		return getEditor().getSite().getShell();
	}
		
	public void clearSelection() {
		getGraphicalViewer().setSelection(new StructuredSelection());
	}
}
