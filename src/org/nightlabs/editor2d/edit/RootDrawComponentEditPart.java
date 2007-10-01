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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.config.PreferencesConfigModule;
import org.nightlabs.editor2d.figures.BufferedFreeformLayer;
import org.nightlabs.editor2d.figures.OversizedBufferFreeformLayer;
import org.nightlabs.editor2d.model.RootDrawComponentPropertySource;
import org.nightlabs.editor2d.viewer.ui.descriptor.DescriptorManager;

public class RootDrawComponentEditPart 
extends AbstractDrawComponentContainerEditPart
{  
	public RootDrawComponentEditPart(RootDrawComponent root) 
	{
	  super(root);
	  setModel(root);
	  initConfigModule();
	}

	protected DescriptorManager descriptorManager = new DescriptorManager();		
	public DescriptorManager getDescriptorManager() {
		return descriptorManager;
	}
	public void setDescriptorManager(DescriptorManager descriptorManager) {
		this.descriptorManager = descriptorManager;
	}

  protected void initConfigModule() 
  {
  	try {
    	prefConfMod = (PreferencesConfigModule) Config.sharedInstance().createConfigModule(PreferencesConfigModule.class);  		
  	} catch (ConfigException ce) {
  		throw new RuntimeException(ce);
  	}
  }
	
  protected PreferencesConfigModule prefConfMod = null;
  public PreferencesConfigModule getPreferencesConfigModule() {
  	return prefConfMod;
  }
  
//  public static UpdateManager updateManager = null;	
	protected IFigure createFigure()
	{
//	  SmartUpdateFreeformLayer f = new SmartUpdateFreeformLayer(this);
//	  f.setMldcEditPart(this);
//	  EditPartViewer viewer = getRoot().getViewer();
//	  if (viewer instanceof J2DScrollingGraphicalViewer) {
//	    J2DScrollingGraphicalViewer j2DViewer = (J2DScrollingGraphicalViewer) viewer;
//	    FigureCanvas figureCanvas = (FigureCanvas)j2DViewer.getControl();	    
//	    updateManager = figureCanvas.getLightweightSystem().getUpdateManager();
//	    f.registerOnDeferredUpdateManager(updateManager);
//	  }
		
	  Figure f = new OversizedBufferFreeformLayer();
	  ((BufferedFreeformLayer)f).init(this);
	  
//	  addScrollListener();	  
//		Figure f = new FreeformLayer();
		
		f.setLayoutManager(new FreeformLayout());				
		return f; 		
	}
	
	public BufferedFreeformLayer getBufferedFreeformLayer() 
	{
		if (getFigure() instanceof BufferedFreeformLayer)
			return (BufferedFreeformLayer) getFigure();
		
		return null;
	}
	
	protected void createEditPolicies()
	{
		super.createEditPolicies();
		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());		
	}

	public RootDrawComponent getRootDrawComponent() {
		return (RootDrawComponent) getModel();
	}
		
  /** 
   * @return a List of all Layers
   */
  protected List getModelChildren()
  {  
    return getRootDrawComponent().getDrawComponents();
  }	
	
  /**
   * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
   */
  public Object getAdapter(Class adapter) 
  {    
  	if (adapter == SnapToHelper.class) {
  		List snapStrategies = new ArrayList();
  		Boolean val = (Boolean)getViewer().getProperty(RulerProvider.PROPERTY_RULER_VISIBILITY);
  		if (val != null && val.booleanValue())
  			snapStrategies.add(new SnapToGuides(this));
  		val = (Boolean)getViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
  		if (val != null && val.booleanValue())
  			snapStrategies.add(new SnapToGeometry(this));
  		val = (Boolean)getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
  		if (val != null && val.booleanValue())
  			snapStrategies.add(new SnapToGrid(this));
  		
  		if (snapStrategies.size() == 0)
  			return null;
  		if (snapStrategies.size() == 1)
  			return (SnapToHelper)snapStrategies.get(0);

  		SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
  		for (int i = 0; i < snapStrategies.size(); i++)
  			ss[i] = (SnapToHelper)snapStrategies.get(i);
  		return new CompoundSnapToHelper(ss);
  	}
  	return super.getAdapter(adapter);
  }  
  
  public IPropertySource getPropertySource()
  {
    if (propertySource == null)
    {
      propertySource =
        new RootDrawComponentPropertySource(getRootDrawComponent());
    }
    return propertySource;
  }
  
	@Override
	protected void propertyChanged(PropertyChangeEvent evt) 
	{
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(RootDrawComponent.PROP_RESOLUTION)) {
			refresh();
			return;			
		}
		super.propertyChanged(evt);
	}
	
	@Override
	public void deactivate() 
	{
		super.deactivate();
		if (getBufferedFreeformLayer() != null)
			getBufferedFreeformLayer().dispose();		
	}   
    
	
}
