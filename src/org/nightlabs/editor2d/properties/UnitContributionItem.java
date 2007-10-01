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
package org.nightlabs.editor2d.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.nightlabs.base.ui.action.XContributionItem;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.i18n.unit.IUnit;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class UnitContributionItem 
extends XContributionItem 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(UnitContributionItem.class);
	
	public UnitContributionItem(UnitManager unitManager) 
	{
		super();
		this.unitManager = unitManager;
		units = new ArrayList<IUnit>(unitManager.getUnits());
		unitManager.addPropertyChangeListener(currentUnitListener);
		unitManager.addPropertyChangeListener(unitAddedListener);
	}

	private UnitManager unitManager = null;
	private Combo combo = null;
	public Combo getCombo() {
		return combo;
	}
	private List<IUnit> units = null;
	
//  /**
//   * Creates and returns the control for this contribution item
//   * under the given parent composite.
//   *
//   * @param parent the parent composite
//   * @return the new control
//   */
//  protected Control createControl(Composite parent) 
//  {
//  	Composite comp = new XComposite(parent, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER);
//  	combo = new Combo(comp, SWT.BORDER | SWT.READ_ONLY);
//  	combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//  	combo.addSelectionListener(comboSelectionListener);  		  	
////  	for (IUnit unit : units) {
////			combo.add(unit.getUnitSymbol());
////		}
////  	selectUnit(unitManager.getCurrentUnit());
//  	refresh();
//  	
//  	if (toolitem != null)
//  		toolitem.setWidth(computeWidth(comp));  	
//  	return comp;
//  }

  /**
   * Creates and returns the control for this contribution item
   * under the given parent composite.
   *
   * @param parent the parent composite
   * @return the new control
   */
  protected Control createControl(Composite parent) 
  {
  	Composite comp = new XComposite(parent, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER);
  	combo = new Combo(comp, SWT.BORDER | SWT.READ_ONLY);
  	combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
  	combo.addSelectionListener(comboSelectionListener);  		  	
  	refresh();  	
  	if (toolitem != null)
  		toolitem.setWidth(computeWidth(comp));  	
  	return comp;
  }
	
	protected ToolItem toolitem = null;	  
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
  public void fill(ToolBar parent, int index) 
  {
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
   * 
   * @param parent The menu
   * @param index Menu index
   */
  public final void fill(Menu parent, int index) 
  {
  	Assert.isTrue(false, "Can't add a control to a menu");//$NON-NLS-1$  	
  }
  
	protected SelectionListener comboSelectionListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			int index = combo.getSelectionIndex();
			if (index != -1) {
				IUnit selectedUnit = units.get(index);
				unitManager.setCurrentUnit(selectedUnit);
			}
		}	
	};  
	
	protected PropertyChangeListener unitAddedListener = new PropertyChangeListener()
	{	
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(UnitManager.PROP_UNIT_ADDED)) 
				refresh();
		}	
	};

	protected PropertyChangeListener currentUnitListener = new PropertyChangeListener()
	{	
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(UnitManager.PROP_CURRENT_UNIT_CHANGED)) 
		  	selectUnit(unitManager.getCurrentUnit());
		}	
	};
	
	public void selectUnit(IUnit unit) 
	{
		int index = units.indexOf(unit);
		if (index != -1) {
			if (combo != null)
				combo.select(index);
			else
				logger.debug("combo == null!"); //$NON-NLS-1$
		}
		else
			logger.debug("units does not contain IUnit "+unit); //$NON-NLS-1$
	}
	
	public IUnit getSelectedUnit() 
	{
		int index = combo.getSelectionIndex();
		if (index != -1)
			return units.get(index);
		return null;
	}
		
	protected void refresh() 
	{
		units = new LinkedList<IUnit>(unitManager.getUnits());
		combo.removeAll();
  	for (IUnit unit : units) {
			combo.add(unit.getUnitSymbol());
		}
  	selectUnit(unitManager.getCurrentUnit());		
	}
}
