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
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.figures.DrawComponentFigure;
import org.nightlabs.editor2d.figures.RendererFigure;
import org.nightlabs.editor2d.model.DrawComponentPropertySource;
import org.nightlabs.editor2d.request.EditorRequestConstants;
import org.nightlabs.editor2d.util.EditorUtil;
import org.nightlabs.editor2d.util.J2DUtil;
import org.nightlabs.editor2d.viewer.ui.descriptor.DescriptorManager;

public abstract class AbstractDrawComponentEditPart 
extends AbstractGraphicalEditPart
implements EditorRequestConstants
{
	private static final Logger logger = Logger.getLogger(AbstractDrawComponentEditPart.class);
	
  public AbstractDrawComponentEditPart(DrawComponent drawComponent) {
    setModel(drawComponent);   
//    drawComponent.clearBounds();
//    drawComponent.getBounds();
  }
  
  private Label tooltip = new Label();
  protected Label getTooltip() 
  {
  	tooltip.setText(getTooltipText(getDrawComponent()));
  	return tooltip; 
  }
  
  protected String getTooltipText(DrawComponent dc) 
  {
  	DescriptorManager descMan = getModelRoot().getDescriptorManager();
  	descMan.setDrawComponent(getDrawComponent());
  	return descMan.getEntriesAsString(true);
  }
   
  public void updateTooltip() 
  {
  	if (getModelRoot().getPreferencesConfigModule().isShowToolTips())
  		getFigure().setToolTip(getTooltip());
  	else
  		getFigure().setToolTip(null);
  }
  
  @Override
  protected IFigure createFigure() 
  {
  	RendererFigure figure = new DrawComponentFigure();  
//  	figure.setDescriptorManager(getModelRoot().getDescriptorManager());
    figure.setDrawComponent(getDrawComponent());    
    addRenderer(figure);
    addZoomListener((DrawComponentFigure)figure);
    figure.setToolTip(getTooltip());
    return figure;
  }
      
  public DrawComponentFigure getDrawComponentFigure() 
  {
  	if (getFigure() instanceof DrawComponentFigure) {
  		return (DrawComponentFigure) getFigure();
  	}
  	return null;
  }
  
  protected void addRenderer(RendererFigure figure) 
  {
    // add Renderer
    if (getDrawComponent().getRenderer() != null) {
    	figure.setRenderer(getDrawComponent().getRenderer());  
    }
    else {
      if (getModelRoot() != null) {
        getDrawComponent().setRenderModeManager(getModelRoot().getRootDrawComponent().getRenderModeManager());
      }
    }  	
  }
  
  public RootDrawComponentEditPart getModelRoot() 
  {
  	if (getRoot() instanceof RootDrawComponentEditPart) {
  		return (RootDrawComponentEditPart) getRoot();
  	}
  	else {
  		for (Iterator it = getRoot().getChildren().iterator(); it.hasNext(); ) {
  			Object o = it.next();
  			if (o instanceof RootDrawComponentEditPart) {
  				return (RootDrawComponentEditPart) o;
  			}
  		}
  	}
  	return null;
  }
  
  protected void addZoomListener(DrawComponentFigure figure) 
  {
    ZoomManager zoomManager = EditorUtil.getZoomManager(this);
    if (zoomManager != null) {
      zoomManager.addZoomListener(figure.getZoomListener());
    }  	
  }

  protected void removeZoomListener(DrawComponentFigure figure) 
  {
    ZoomManager zoomManager = EditorUtil.getZoomManager(this);
    if (zoomManager != null) {
      zoomManager.removeZoomListener(figure.getZoomListener());
    }  	
  }

  protected RendererFigure getRendererFigure() {
    return (RendererFigure) getFigure();
  }
  
  /** 
   * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
   */
  protected abstract void createEditPolicies();
  
  /** 
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
   */
  @Override  
  public void activate()
  {
    if (isActive())
      return;
    
    // start listening for changes in the model
    hookIntoDrawComponent(getDrawComponent());
            
    super.activate();
    
    if (getDrawComponent().isTemplate())
    	setContains(false);
  }
  
  /**
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
   */
  @Override  
  public void deactivate()
  {
    if (!isActive())
      return;
    
    // stop listening for changes in the model
    unhookFromDrawComponent(getDrawComponent());
    
    if (getFigure() instanceof DrawComponentFigure) {
    	DrawComponentFigure dcf = (DrawComponentFigure) figure; 
    	removeZoomListener(dcf);
    	dcf.dispose();
    }
    
    if (getPropertySource() instanceof DrawComponentPropertySource) {
    	DrawComponentPropertySource dcps = (DrawComponentPropertySource) getPropertySource();
    	dcps.clean();
    }
    
    if (logger.isDebugEnabled())
    	logger.debug("deactivate called"); //$NON-NLS-1$
    
    super.deactivate();
  }
  
  public DrawComponent getDrawComponent() {
    return (DrawComponent) getModel();
  }
    
  @Override  
  protected void refreshVisuals()
  {
    Rectangle r = new Rectangle(J2DUtil.toDraw2D(getDrawComponent().getBounds()));
    
    ((GraphicalEditPart) getParent()).setLayoutConstraint(
        this,
        getFigure(),
        r);  
    
    if (getFigure() instanceof RendererFigure) {
      getRendererFigure().setRenderer(getDrawComponent().getRenderer());
      getRendererFigure().setDrawComponent(getDrawComponent());      
    }
    
    getFigure().repaint();    
    updateRoot(getFigure());       
    updateTooltip();
//    LOGGER.debug("refreshVisuals!");
  }

  public void updateRoot(IFigure figure) 
  {
    RootDrawComponentEditPart rootEditPart = getModelRoot();
    if (rootEditPart != null) {
    	if (rootEditPart.getBufferedFreeformLayer() != null)
    		rootEditPart.getBufferedFreeformLayer().refresh(figure);    	
    }
  }
      
  /**
   * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
   */
  @Override  
  public Object getAdapter(Class key)
  {
    /* override the default behavior defined in AbstractEditPart
     *  which would expect the model to be a property sourced. 
     *  instead the editpart can provide a property source
     */
    if (IPropertySource.class == key)
    {
      return getPropertySource();
    }
    return super.getAdapter(key);
  }
  
  protected IPropertySource propertySource = null;
  public IPropertySource getPropertySource() 
  {
    if (propertySource == null) {
      propertySource = new DrawComponentPropertySource(getDrawComponent());
    }
    return propertySource;
  }
  
  private PropertyChangeListener listener = new PropertyChangeListener(){	
		public void propertyChange(PropertyChangeEvent evt) {
			propertyChanged(evt);
		}	
	};
  
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		String propertyName = evt.getPropertyName();
		
		if (propertyName.equals(DrawComponent.PROP_BOUNDS)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(DrawComponent.PROP_NAME)) {
			refreshVisuals();
			return;
		}		
		else if (propertyName.equals(DrawComponent.PROP_HEIGHT)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(DrawComponent.PROP_WIDTH)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(DrawComponent.PROP_X)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(DrawComponent.PROP_Y)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(DrawComponent.PROP_ROTATION)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(DrawComponent.PROP_ROTATION_X)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(DrawComponent.PROP_ROTATION_Y)) {
			refreshVisuals();
			return;
		}						
		else if (propertyName.equals(DrawComponent.PROP_RENDER_MODE)) {
			refreshVisuals();
			return;
		}		
		else if (propertyName.equals(DrawComponent.TRANSFORM_CHANGED)) {
			refreshVisuals();
			return;
		}
		else if (propertyName.equals(DrawComponent.PROP_TEMPLATE)) {
			boolean template = ((Boolean)evt.getNewValue());
			setContains(!template);
			return;
		}
		else if (propertyName.equals(DrawComponent.PROP_VISIBLE)) {
			refreshVisuals();
			return;
		} 		
	}
	  
  /**
   * Registers this edit part as a listener for change notifications
   * to the specified DrawComponent element.
   * 
   * @param element the DrawComponent element that should be observed
   * for change notifications
   */
  protected void hookIntoDrawComponent(DrawComponent element)
  {
    if (element != null)
      element.addPropertyChangeListener(listener);
  }
  
  /**
   * Removes this edit part from the specified DrawComponent element.
   * Thus, it will no longe receive change notifications.
   * 
   * @param element the DrawComponent element that should not be observed
   * any more
   */
  protected void unhookFromDrawComponent(DrawComponent element)
  {
    if (element != null)
      element.removePropertyChangeListener(listener);
  }
            
//  public boolean understandsRequest(Request req) 
//  {
//    if (req.getType().equals(REQ_ROTATE))
//      return true;
//
//    else if (req.getType().equals(REQ_EDIT_ROTATE_CENTER))
//      return true;
//
////    else if (req.getType().equals(REQ_SHEAR))
////      return true;
//    
//    return super.understandsRequest(req);
//  }  
  
	protected void setContains(boolean contains) 
	{
		Collection<DrawComponent> drawComponents = new ArrayList<DrawComponent>(1);
		if (getDrawComponent() instanceof DrawComponentContainer) {
			drawComponents = ((DrawComponentContainer)getDrawComponent()).getDrawComponents();
		} else {
			drawComponents.add(getDrawComponent());
		}
		
		for (Iterator<DrawComponent> it = drawComponents.iterator(); it.hasNext(); ) 
		{
			Object o = getViewer().getEditPartRegistry().get(it.next());
			if (o != null && o instanceof GraphicalEditPart) 
			{
				GraphicalEditPart gep = (GraphicalEditPart) o;
				IFigure figure = gep.getFigure();
				if (figure instanceof DrawComponentFigure) {
					DrawComponentFigure dcFigure = (DrawComponentFigure) figure;
					dcFigure.setContains(contains);
					dcFigure.setVisible(contains);
					logger.info("DrawComponentFigure found and set contains to "+contains); //$NON-NLS-1$
				}					
			}
		}			
		
		refresh();
	}
	
}
