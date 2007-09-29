/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.action;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public abstract class AbstractContributionItem 
extends XContributionItem 
{
	public AbstractContributionItem() {
		super();
	}
	
	public AbstractContributionItem(String id, String name) 
	{
		super();
		setId(id);
		this.name = name;
	}

	public AbstractContributionItem(String id, String name, boolean fillToolBar, 
			boolean fillCoolBar, boolean fillMenuBar, boolean fillComposite) 
	{
		super();
		setId(id);
		this.name = name;		
		this.fillCoolBar = fillCoolBar;
		this.fillToolBar = fillToolBar;
		this.fillMenuBar = fillMenuBar;
		this.fillComposite = fillComposite;
	}
		
	protected abstract Control createControl(Composite parent);
	
	protected boolean fillToolBar = true;
	protected boolean fillCoolBar = true;
	protected boolean fillMenuBar = true;
	protected boolean fillComposite = true;
	
	private boolean toolBarFilled = false;
	private boolean coolBarFilled = false;
	private boolean compositeFilled = false;
	
	private String name = ""; //$NON-NLS-1$
	public String getName() {
		return name;
	}			
	public void setName(String name) {
		this.name = name;
	}
	
	protected void setSize() 
	{
		if (fillToolBar && toolBarFilled) 
			getToolItem().setWidth(computeWidth(getControl()));
		
		if (fillCoolBar && coolBarFilled)
			getCoolItem().setSize(computeWidth(getControl()), computeHeight(getControl()));
		
		if (fillComposite && compositeFilled)
			getControl().setSize(computeWidth(getControl()), computeHeight(getControl()));
	}
	  
	public void fill(Composite parent) 
	{
		if (fillComposite) {
			control = createControl(parent);
			compositeFilled = true;
			setSize();
		}		
	}

	protected Control control = null;
	public Control getControl() {
		return control;
	}
	
	public void fill(CoolBar parent, int index) 
	{
		if (fillCoolBar) {
			coolItem = new CoolItem(parent, SWT.SEPARATOR, index);
			control = createControl(parent);
			coolItem.setControl(control);
			coolBarFilled = true;
			setSize();
		}
	}

	protected CoolItem coolItem = null;
	public CoolItem getCoolItem() {
		return coolItem;
	}
	
	public void fill(Menu menu, int index) 
	{
		if (fillMenuBar) {
			menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText(name);			
		}
	}

	private MenuItem menuItem = null;
	public MenuItem getMenuItem() {
		return menuItem;
	}
	
	public void fill(ToolBar parent, int index) 
	{
		if (fillToolBar) {
			toolItem = new ToolItem(parent, SWT.SEPARATOR, index);
			control = createControl(parent);
	  	toolItem.setControl(control);
	  	toolBarFilled = true;
	  	setSize();
		}
	}
	
	protected ToolItem toolItem = null;
	public ToolItem getToolItem() {
		return toolItem;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (control != null)
			control.dispose();
		if (coolItem != null)
			coolItem.dispose();
		if (toolItem != null)
			toolItem.dispose();
		if (menuItem != null)
			menuItem.dispose();
	}

}
