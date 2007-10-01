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

package org.nightlabs.editor2d.model;

import org.nightlabs.editor2d.Editor2DFactory;
import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.EditorRuler;
import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.GroupDrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.LineDrawComponent;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RectangleDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.TextDrawComponent;

public class ModelCreationFactory 
//implements CreationFactory
implements IModelCreationFactory
{
	protected Class targetClass;
	protected Editor2DFactory factory;
	
	public ModelCreationFactory(Class targetClass, Editor2DFactory factory) {
		this.targetClass = targetClass;
		this.factory = factory;
	}
	
	public Editor2DFactory getFactory() {
		return factory;
	}
	
	/* 
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public Object getNewObject()
	{	  	  	  
		Object result = null;
			
		if( targetClass.equals(RectangleDrawComponent.class)) {
			result = factory.createRectangleDrawComponent();
		}
		else if( targetClass.equals(Layer.class)) {
			result = factory.createLayer();
		}
		else if( targetClass.equals(EllipseDrawComponent.class)) {
			result = factory.createEllipseDrawComponent();
		}
//		else if( targetClass.equals(RootDrawComponent.class)) {
//		  result = factory.createRootDrawComponent();
//		}
		else if( targetClass.equals(EditorGuide.class)) {
		  result = factory.createEditorGuide();
		}
		else if ( targetClass.equals(EditorRuler.class)) {
		  result = factory.createEditorRuler();
		}
		else if ( targetClass.equals(LineDrawComponent.class)) {
		  result = factory.createLineDrawComponent();
		}
    else if ( targetClass.equals(TextDrawComponent.class)) {
      result = factory.createTextDrawComponent();
    }
    else if ( targetClass.equals(ImageDrawComponent.class)) {
      result = factory.createImageDrawComponent();
    }
    else if ( targetClass.equals(PageDrawComponent.class)) {
      result = factory.createPageDrawComponent();
    }
    else if ( targetClass.equals(GroupDrawComponent.class)) {
      result = factory.createGroupDrawComponent();
    }
			
		return result;
	}

	/* 
	 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
	 */
	public Object getObjectType()
	{
		return targetClass;
	}
}

