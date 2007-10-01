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
package org.nightlabs.editor2d.editpolicy;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.nightlabs.editor2d.figures.ShapeFigure;
import org.nightlabs.editor2d.util.feedback.FeedbackUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorFeedbackPolicy 
extends EditorConstrainedEditPolicy 
{

	public EditorFeedbackPolicy() {

	}

  protected IFigure feedback;
  
  /**
   * Lazily creates and returns the feedback figure used during drags.
   * @return the feedback figure
   */
  protected IFigure getDragSourceFeedbackFigure() 
  {
  	if (feedback == null)
  		feedback = createDragSourceFeedbackFigure();
  	return feedback;
  }
      
  /**
   * @see org.eclipse.gef.EditPolicy#deactivate()
   */
  public void deactivate() {
  	if (feedback != null) {
  		removeFeedback(feedback);
  		feedback = null;
  	}
  	super.deactivate();
  }
  
  /**
   * Creates the figure used for feedback.
   * @return the new feedback figure
   */
  protected IFigure createDragSourceFeedbackFigure() 
  {       	
    IFigure figure = getCustomFeedbackFigure(getHost().getModel());
  	figure.setBounds(getInitialFeedbackBounds());
  	addFeedback(figure);
  	return figure;
  }
  
  protected ShapeFigure getCustomFeedbackFigure(Object modelPart) 
  {
  	return FeedbackUtil.getCustomFeedbackFigure(modelPart);
  }  
  
  protected Rectangle getInitialFeedbackBounds() 
  {
  	return getHostFigure().getBounds();
  }  
}
