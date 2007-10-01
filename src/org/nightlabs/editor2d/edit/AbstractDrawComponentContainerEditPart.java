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

package org.nightlabs.editor2d.edit;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.editpolicy.DrawComponentContainerXYLayoutPolicy;
import org.nightlabs.editor2d.editpolicy.DrawComponentEditPolicy;
import org.nightlabs.editor2d.editpolicy.EditorEditPolicy;
import org.nightlabs.editor2d.figures.ContainerDrawComponentFigure;


public abstract class AbstractDrawComponentContainerEditPart 
extends AbstractDrawComponentEditPart 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(AbstractDrawComponentContainerEditPart.class);
  
  /**
   * @param drawComponentContainer
   */
  public AbstractDrawComponentContainerEditPart(DrawComponentContainer drawComponentContainer) {
    super(drawComponentContainer);
  }

  @Override
  protected IFigure createFigure() 
  {
    ContainerDrawComponentFigure figure = new ContainerDrawComponentFigure();
    figure.setLayoutManager(new FreeformLayout());    
    figure.setDrawComponent(getDrawComponent());    
    addRenderer(figure);
//    addZoomListener(figure);
    return figure;  	
  }
  
  public DrawComponentContainer getDrawComponentContainer() {
  	return (DrawComponentContainer) getModel();
  }
  
  @Override  
  protected void createEditPolicies() 
  {
  	// generic delete policy
    installEditPolicy(EditPolicy.COMPONENT_ROLE, new DrawComponentEditPolicy());
//		installEditPolicy(EditPolicy.CONTAINER_ROLE, new DrawComponentContainerEditPolicy());
    
		// handles constraint changes (e.g. moving and/or resizing) of model elements
		// and creation of new model elements		
		XYLayout layout = (XYLayout) getContentPane().getLayoutManager();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DrawComponentContainerXYLayoutPolicy(layout));
		// disable selection feedback for this edit part		
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);	
		// show snap feedback 		
		installEditPolicy(EditorEditPolicy.SNAP_FEEDBACK_ROLE, new SnapFeedbackPolicy()); //$NON-NLS-1$
		
//		installEditPolicy(EditorEditPolicy.ROTATE_ROLE, new EditorRotateEditPolicy());			
  }  
    
  @Override  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		super.propertyChanged(evt);
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(DrawComponentContainer.CHILD_ADDED)) {
//			logger.debug(propertyName);
			refresh();
			return;
		}
		else if (propertyName.equals(DrawComponentContainer.CHILD_REMOVED)) {
//			logger.debug(propertyName);
			refresh();
			return;
		}		
	}
	
  @Override  
  protected List getModelChildren()
  {
    return getDrawComponentContainer().getDrawComponents();
  }   
}
