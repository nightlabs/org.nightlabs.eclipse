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

package org.nightlabs.editor2d.ui;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartFactory;
import org.nightlabs.editor2d.Editor2DFactory;
import org.nightlabs.editor2d.NameProvider;
import org.nightlabs.editor2d.impl.Editor2DFactoryImpl;
import org.nightlabs.editor2d.ui.edit.GraphicalEditPartFactory;
import org.nightlabs.editor2d.ui.edit.tree.TreePartFactory;
import org.nightlabs.editor2d.ui.util.EditorNameProvider;


public class Editor  
extends AbstractEditor
{
	/**
	 * @see org.nightlabs.editor2d.ui.AbstractEditor#createEditPartFactory()
	 */	
  public EditPartFactory createEditPartFactory() {
  	return new GraphicalEditPartFactory();
  }  
    
	/**
	 * @see org.nightlabs.editor2d.ui.AbstractEditor#createOutlineEditPartFactory()
	 */  
  public EditPartFactory createOutlineEditPartFactory() {
  	return new TreePartFactory(getFilterManager());
  }   

	/**
	 * @see org.nightlabs.editor2d.ui.AbstractEditor#createContextMenuProvider()
	 */    
  public ContextMenuProvider createContextMenuProvider() {
    return new EditorContextMenuProvider(getGraphicalViewer(), getActionRegistry());
  }

	/**
	 * @see org.nightlabs.editor2d.ui.AbstractEditor#createNameProvider()
	 */    
  public NameProvider createNameProvider() {
		return new EditorNameProvider();
	}

	/**
	 * @see org.nightlabs.editor2d.ui.AbstractEditor#createModelFactory()
	 */  		
	public Editor2DFactory createModelFactory() {
		return new Editor2DFactoryImpl();
	}
	
//	/**
//	 * @see org.nightlabs.editor2d.ui.AbstractEditor#createRootDrawComponent()
//	 */    
//	public RootDrawComponent createRootDrawComponent() {
//		RootDrawComponent root = getModelFactory().createRootDrawComponent();		
//    return root;
//  }
	
	/**
	 * @see org.nightlabs.editor2d.ui.AbstractEditor#createPaletteFactory()
	 */  	
	public AbstractPaletteFactory createPaletteFactory() {
		return new EditorPaletteFactory(getModelFactory());
	}
			
}
  
