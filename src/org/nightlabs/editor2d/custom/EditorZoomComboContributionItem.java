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

package org.nightlabs.editor2d.custom;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.base.ui.action.XContributionItem;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.i18n.unit.resolution.DPIResolutionUnit;
import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.i18n.unit.resolution.Resolution;
import org.nightlabs.i18n.unit.resolution.ResolutionImpl;


public class EditorZoomComboContributionItem 
extends XContributionItem
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(EditorZoomComboContributionItem.class);
	
	private Combo combo = null;
  private String[] initStrings = null;
  private ToolItem toolitem = null;
  private ZoomManager zoomManager = null;
  private IPartService service = null;	
        
  /**
   * Constructor for ComboToolItem.
   * @param partService used to add a PartListener
   */
  public EditorZoomComboContributionItem(IPartService partService) 
  {
  	this(partService, "8888%");//$NON-NLS-1$
  }

  /**
   * Constructor for ComboToolItem.
   * @param partService used to add a PartListener
   * @param initString the initial string displayed in the combo
   */
  public EditorZoomComboContributionItem(IPartService partService, String initString) 
  {
  	this(partService, new String[] {initString});
  }
  
  /**
   * Constructor for ComboToolItem.
   * @param partService used to add a PartListener
   * @param initStrings the initial string displayed in the combo
   */
  public EditorZoomComboContributionItem(IPartService partService, String[] initStrings) 
  {
  	super(GEFActionConstants.ZOOM_TOOLBAR_WIDGET);
//    super(partService, initStrings);
  	this.initStrings = initStrings;
  	service = partService;
  	Assert.isNotNull(partService);
  	partService.addPartListener(partListener);
  }
  
  protected void refresh(boolean repopulateCombo) 
  {
  	if (combo == null || combo.isDisposed())
  		return;
		if (zoomManager == null) {
			combo.setEnabled(false);
			combo.setText(""); //$NON-NLS-1$
		} 
		else {
			if (repopulateCombo) {
				combo.setItems(getZoomManager().getZoomLevelsAsText());
			}
			String zoom = getZoomManager().getZoomAsText();
			logger.debug("zoomText = "+zoom); //$NON-NLS-1$
			int index = combo.indexOf(zoom);
			if (index != -1)
				combo.select(index);
			else
				combo.setText(zoom); 				
			combo.setEnabled(true);
		}
  }
  
  private DecimalFormat format = new DecimalFormat("####%"); //$NON-NLS-1$
  public String getZoomAsText(double zoom, double factor) {
  	return format.format(zoom * factor);
  }
  
  /**
   * Returns the zoomManager.
   * @return ZoomManager
   */
  public ZoomManager getZoomManager() 
  {
  	return zoomManager;
  }
  
  private ZoomListener zoomListener = new ZoomListener()
  {	
		public void zoomChanged(double zoom) 
		{
	  	logger.debug("zoom = "+zoom);			 //$NON-NLS-1$
			refresh(false);
		}	
	};
		
  private SelectionListener comboSelectionListener = new SelectionListener() 
  {
		public void widgetSelected(SelectionEvent e) 
		{
	  	if (zoomManager != null) 
	  	{
	  		if (combo.getSelectionIndex() >= 0) {
	  			zoomManager.setZoomAsText(combo.getItem(combo.getSelectionIndex()));
	  		}
	  		else {
	  			zoomManager.setZoomAsText(combo.getText());
	  		}
	  	}
	  	refresh(false);
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
  
	private FocusListener comboFocusListener = new FocusListener() 
	{
		public void focusGained(FocusEvent e) {
			// do nothing
		}
		public void focusLost(FocusEvent e) {
			refresh(false);
		}
	};
	
  /**
   * Creates and returns the control for this contribution item
   * under the given parent composite.
   *
   * @param parent the parent composite
   * @return the new control
   */
  protected Control createControl(Composite parent) 
  {
  	combo = new Combo(parent, SWT.DROP_DOWN);
  	combo.addSelectionListener(comboSelectionListener);
  	combo.addFocusListener(comboFocusListener);
  	
  	// Initialize width of combo
  	combo.setItems(initStrings);
  	toolitem.setWidth(computeWidth(combo));
  	refresh(true);
  	return combo;
  }

  /**
   * @see org.eclipse.jface.action.ContributionItem#dispose()
   */
  public void dispose() {
  	if (partListener == null)
  		return;
  	service.removePartListener(partListener);
  	if (zoomManager != null) {
  		zoomManager.removeZoomListener(zoomListener);
  		zoomManager = null;
  	}
  	combo = null;
  	partListener = null;
  }

  /**
   * The control item implementation of this <code>IContributionItem</code>
   * method calls the <code>createControl</code> framework method to
   * create a control under the given parent, and then creates
   * a new tool item to hold it.
   * Subclasses must implement <code>createControl</code> rather than
   * overriding this method.
   * 
   * @param parent The ToolBar to add the new control to
   * @param index Index
   */
  public void fill(ToolBar parent, int index) {
  	toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
  	Control control = createControl(parent);
  	toolitem.setControl(control);	
  }  
  
  /**
   * The control item implementation of this <code>IContributionItem</code>
   * method calls the <code>createControl</code> framework method.
   * Subclasses must implement <code>createControl</code> rather than
   * overriding this method.
   * 
   * @param parent The parent of the control to fill
   */
  public final void fill(Composite parent) {
  	createControl(parent);
  }

  /**
   * The control item implementation of this <code>IContributionItem</code>
   * method throws an exception since controls cannot be added to menus.
   * 
   * @param parent The menu
   * @param index Menu index
   */
  public final void fill(Menu parent, int index) {
  	Assert.isTrue(false, "Can't add a control to a menu");//$NON-NLS-1$
  }  
  
  /*************************** BEGIN resolution recalculation ******************************/    
  private RootDrawComponent root = null;
      
  private IPartListener partListener = new IPartListener() 
  {
		public void partActivated(IWorkbenchPart part) 
		{
			Object rootAdapter = part.getAdapter(RootDrawComponent.class);
			if (rootAdapter != null && rootAdapter instanceof RootDrawComponent) {
				root = (RootDrawComponent) rootAdapter;
			}
		  Object zoomAdapter = part.getAdapter(ZoomManager.class);
		  if (zoomAdapter != null && zoomAdapter instanceof ZoomManager) {
  			setZoomManager((ZoomManager)zoomAdapter);  
		  }
		}
		public void partBroughtToTop(IWorkbenchPart p) { }
		public void partClosed(IWorkbenchPart p) { }
		public void partDeactivated(IWorkbenchPart p) { }
		public void partOpened(IWorkbenchPart p) { }
	};

	private double[] initalZoomValues = new double[] {0.001, 0.01, 0.05, 0.1, 0.25, 0.5, 1.0, 2.0, 3.0, 5.0, 10.0};
	public double[] getZoomLevels(double factor) 
	{
		if (factor == 1)
			return initalZoomValues;
		else {
			double[] newValues = new double[initalZoomValues.length];
			for (int i = 0; i < initalZoomValues.length; i++) {
				newValues[i] = initalZoomValues[i] * factor;
			}
			return newValues;
		}
	}
	
  /**
   * Sets the ZoomManager
   * @param zm The ZoomManager
   */
  public void setZoomManager(ZoomManager zm) 
  {
  	if (zoomManager == zm)
  		return;
  	if (zoomManager != null)
  		zoomManager.removeZoomListener(zoomListener);

  	zoomManager = zm;

  	double factor = getResolutionFactor();  	
//  	logger.debug("factor = "+factor);
  	
		zoomManager.setZoomLevels(getZoomLevels(factor));
		zoomManager.setUIMultiplier(1/factor);		
//  	zoomManager.setZoom(factor);
		if (zm.getZoom() == 1.0)
			zoomManager.setZoom(factor);
		else
			zoomManager.setZoom(zm.getZoom());
  	
  	refresh(true);

  	if (zoomManager != null)
  		zoomManager.addZoomListener(zoomListener);
  }	
	
	private Resolution deviceResolution = null;
	public Resolution getDeviceResolution() 
	{
		if (deviceResolution == null) {
//			int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
			int dpi = 72;
			deviceResolution = new ResolutionImpl(new DPIResolutionUnit(), dpi, dpi);
//			logger.debug("ScreenResolution (DPI) = "+dpi);			
		}
		return deviceResolution;		
	}
	
	public double getDeviceResolutionX(IResolutionUnit unit) {
		return getDeviceResolution().getResolutionX(unit);
	}

	public double getDeviceResolutionY(IResolutionUnit unit) {
		return getDeviceResolution().getResolutionY(unit);
	}
	
	protected double getDocumentResolutionX(IResolutionUnit unit) 
  {
  	if (root != null) 
  		return root.getResolution().getResolutionX(unit);
  	return 1.0;
  }

	protected double getDocumentResolutionY(IResolutionUnit unit) 
  {
  	if (root != null) 
  		return root.getResolution().getResolutionY(unit);
  	return 1.0;
  }
  
  public Resolution getDocumentResolution() 
  {
  	if (root != null) {
//  		logger.debug("DocumentResolution = "+root.getResolution().getResolutionX()+" "+root.getResolution().getResolutionUnit().getResolutionID());  	  		  		
  		return root.getResolution();
  	}
  	return new ResolutionImpl();
  }	
    
  public double getResolutionFactor() {
  	return Math.max(getResolutionFactorX(), getResolutionFactorY());
  }
  
  private IResolutionUnit defaultResolutionUnit = new DPIResolutionUnit();  
  protected double getResolutionFactorX() {
  	return getDeviceResolutionX(defaultResolutionUnit) / getDocumentResolutionX(defaultResolutionUnit);  	
  }
  
  protected double getResolutionFactorY() {
  	return getDeviceResolutionY(defaultResolutionUnit) / getDocumentResolutionY(defaultResolutionUnit);  	
  }
  
}
