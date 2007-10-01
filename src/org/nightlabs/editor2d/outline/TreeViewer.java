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
package org.nightlabs.editor2d.outline;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.editor2d.edit.tree.DrawComponentTreeEditPart;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class TreeViewer 
extends org.eclipse.gef.ui.parts.TreeViewer 
{
	private org.eclipse.jface.viewers.TreeViewer treeViewer;
	public org.eclipse.jface.viewers.TreeViewer getTreeViewer() {
		return treeViewer;
	}
	
	public Control createControl(Composite parent) 
	{
		Tree tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer = new org.eclipse.jface.viewers.TreeViewer(tree);
		treeViewer.setContentProvider(new ContentProvider());
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
	  treeViewer.setLabelProvider(new DecoratingLabelProvider(new LabelProvider(), decorator));
		
		treeViewer.setInput(getRootEditPart());
//		setControl(tree);
		setControl(treeViewer.getControl());	  
//		return tree;
	  return treeViewer.getControl();
	}
	
	class ContentProvider 
	implements ITreeContentProvider 
	{
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof EditPart) {
				EditPart ep = (EditPart) parentElement;
				return ep.getChildren().toArray();
			}
			return null;
		}

		public Object getParent(Object element) {
			if (element instanceof EditPart) {
				EditPart ep = (EditPart) element;
				return ep.getParent();
			}
			return null;
		}

		public boolean hasChildren(Object element) 
		{
			if (element instanceof EditPart) {
				EditPart ep = (EditPart) element;
				return !ep.getChildren().isEmpty();
			}
			return false;
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof EditPart) {
				EditPart ep = (EditPart) inputElement;
				return ep.getChildren().toArray();
			}
			return null;
		}

		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}		
	}
	
	class LabelProvider
	extends org.eclipse.jface.viewers.LabelProvider
	{
		@Override
		public Image getImage(Object element) {
			if (element instanceof DrawComponentTreeEditPart) {
				DrawComponentTreeEditPart tep = (DrawComponentTreeEditPart) element;
				return tep.getTreeImage();
			}			
			return super.getImage(element);
		}

		@Override
		public String getText(Object element) {
			if (element instanceof DrawComponentTreeEditPart) {
				DrawComponentTreeEditPart tep = (DrawComponentTreeEditPart) element;
				return tep.getTreeText();
			}			
			return super.getText(element);
		}		
	}
	
}
