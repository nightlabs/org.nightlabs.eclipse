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

package org.nightlabs.editor2d.edit.tree;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.editpolicy.tree.DrawComponentTreeContainerEditPolicy;

public abstract class DrawComponentContainerTreeEditPart 
extends DrawComponentTreeEditPart 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(DrawComponentContainerTreeEditPart.class);
	
  /**
   * Constructor initializes this with the given model.
   *
   * @param model  Model for this.
   */
  public DrawComponentContainerTreeEditPart(DrawComponentContainer model) {
  	super(model);
  }
    
  /**
   * Creates and installs pertinent EditPolicies.
   */
  protected void createEditPolicies() 
  {
  	super.createEditPolicies();
//  	installEditPolicy(EditPolicy.CONTAINER_ROLE, new DrawComponentContainerEditPolicy());
  	// generic Edit Policy for moving, deleting and adding children in a Tree
  	installEditPolicy(EditPolicy.TREE_CONTAINER_ROLE, new DrawComponentTreeContainerEditPolicy());
  	if (getParent() instanceof RootEditPart)
  		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
  }

  /**
   * Returns the model of this as a LogicDiagram.
   *
   * @return  Model of this.
   */
  protected DrawComponentContainer getDrawComponentContainer() {
  	return (DrawComponentContainer)getModel();
  }

  /**
   * Returns the children of this from the model,
   * as this is capable enough of holding EditParts.
   *
   * @return  List of children.
   */
  protected List getModelChildren() 
  {
  	return getDrawComponentContainer().getDrawComponents();
  }
  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(DrawComponentContainer.CHILD_ADDED)) {
//			logger.debug(propertyName);
			refreshChildren();			
		}
		else if (propertyName.equals(DrawComponentContainer.CHILD_REMOVED)) {
//			logger.debug(propertyName);
			refreshChildren();			
		}		
	}  
    
}
