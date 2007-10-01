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
package org.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;

import org.apache.log4j.Logger;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.GroupDrawComponent;
import org.nightlabs.editor2d.model.GroupPropertySource;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class GroupEditPart 
extends AbstractDrawComponentContainerEditPart 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(GroupEditPart.class);
	
	public GroupEditPart(GroupDrawComponent group) {
		super(group);
		
    group.clearBounds();
    group.getBounds();
	}

	public GroupDrawComponent getGroupDrawComponent() {
		return (GroupDrawComponent) getModel();
	}
	
  public IPropertySource getPropertySource()
  {
    if (propertySource == null) {
      propertySource = new GroupPropertySource(getGroupDrawComponent());
    }
    return propertySource;
  }

//	@Override
//	protected void propertyChanged(PropertyChangeEvent evt) 
//	{
//		super.propertyChanged(evt);
//		String propertyName = evt.getPropertyName();
//		if (propertyName.equals(DrawComponentContainer.CHILD_ADDED)) 
//		{
//			logger.debug(propertyName);
//			Collection<DrawComponent> children = (Collection<DrawComponent>) evt.getNewValue();
//			setContains(false, children);
//			return;
//		}
//		else if (propertyName.equals(DrawComponentContainer.CHILD_REMOVED)) 
//		{
//			logger.debug(propertyName);
//			Collection<DrawComponent> newChildren = (Collection<DrawComponent>) evt.getNewValue();
//			Collection<DrawComponent> oldChildren = (Collection<DrawComponent>) evt.getOldValue();
//			if (newChildren != null && oldChildren != null) 
//			{
//				oldChildren.removeAll(newChildren);
//				setContains(true, oldChildren);
//			}
//			return;
//		}		
//	} 	
//    
//	protected void setContains(boolean contains, Collection<DrawComponent> drawComponents) 
//	{
//		if (drawComponents != null) 
//		{
//			for (Iterator<DrawComponent> it = drawComponents.iterator(); it.hasNext(); ) 
//			{
//				Object o = getViewer().getEditPartRegistry().get(it.next());
//				if (o != null && o instanceof GraphicalEditPart) 
//				{
//					GraphicalEditPart gep = (GraphicalEditPart) o;
//					IFigure figure = gep.getFigure();
//					if (figure instanceof DrawComponentFigure) {
//						DrawComponentFigure dcFigure = (DrawComponentFigure) figure;
//						dcFigure.setContains(contains);
////						dcFigure.setEnabled(contains);
////						dcFigure.setOpaque(contains);
//						dcFigure.setVisible(contains);
//						logger.debug("DrawComponentFigure found and set contains to "+contains);
//					}					
//				}
//			}			
//		}
//		refresh();
//	}
//
//	@Override
//	public void activate() {
//		super.activate();
//		setContains(false, getGroupDrawComponent().getDrawComponents());
//	}
	
	@Override
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(DrawComponentContainer.CHILD_ADDED)) 
		{
			logger.debug(propertyName);
//			Collection<DrawComponent> children = (Collection<DrawComponent>) evt.getNewValue();
			setContains(false);
			return;
		}
		else if (propertyName.equals(DrawComponentContainer.CHILD_REMOVED)) 
		{
			logger.debug(propertyName);
			setContains(false);
//			Collection<DrawComponent> newChildren = (Collection<DrawComponent>) evt.getNewValue();
//			Collection<DrawComponent> oldChildren = (Collection<DrawComponent>) evt.getOldValue();
//			if (newChildren != null && oldChildren != null) 
//			{
//				oldChildren.removeAll(newChildren);
//				setContains(true, oldChildren);
//			}
			return;
		}		
	}
	
	@Override
	public void activate() {
		super.activate();
		setContains(false);
	}	
}
