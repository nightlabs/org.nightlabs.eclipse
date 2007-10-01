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

package org.nightlabs.editor2d.viewer.action;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.action.AbstractContributionItem;
import org.nightlabs.base.ui.custom.XCombo;
import org.nightlabs.editor2d.viewer.IZoomListener;
import org.nightlabs.editor2d.viewer.IZoomSupport;
import org.nightlabs.editor2d.viewer.resource.Messages;

public class ZoomContributionItem  
extends AbstractContributionItem
{
	public static final String ID = ZoomContributionItem.class.getName();

	public ZoomContributionItem(IZoomSupport zoomSupport) 
	{
		super(ID, Messages.getString("org.nightlabs.editor2d.viewer.action.ZoomContributionItem.name")); //$NON-NLS-1$
		this.zoomSupport = zoomSupport;
		zoomSupport.addZoomListener(zoomListener);
	}
	
	protected IZoomSupport zoomSupport = null;
	public IZoomSupport getZoomSupport() {
		return zoomSupport;
	}
	public void setZoomSupport(IZoomSupport zoomSupport) {
		this.zoomSupport = zoomSupport;
	}

//	protected Text text = null;
//  protected Control createControl(Composite parent) 
//  {
//  	text = new Text(parent, SWT.BORDER);
//  	setText(getZoomSupport().getZoom());
//  	text.addDisposeListener(disposeListener);
//  	return text;
//  }	

	protected XCombo combo = null;
  protected Control createControl(Composite parent) 
  {
  	combo = new XCombo(parent, SWT.BORDER);
  	
  	// to set right width
  	String initString = "1000%"; //$NON-NLS-1$
  	combo.add(null, initString, 0);
  	if (getToolItem() != null) {
  		getToolItem().setWidth(computeWidth(combo));
  	}
  	combo.remove(0);
  	
  	initComboEntries(combo);
  	combo.addSelectionListener(selectionListener);
  	combo.addDisposeListener(disposeListener);
  	setText(zoomSupport.getZoom());
  	return combo;
  }	
	  
	protected void initComboEntries(XCombo c) 
	{   	
		addEntry(c, null, "  25%", 0.25); //$NON-NLS-1$
		addEntry(c, null, "  50%", 0.5); //$NON-NLS-1$
		addEntry(c, null, " 100%", 1.0); //$NON-NLS-1$
		addEntry(c, null, " 200%", 2.0);		 //$NON-NLS-1$
		addEntry(c, null, " 300%", 3.0); //$NON-NLS-1$
		addEntry(c, null, " 400%", 4.0); //$NON-NLS-1$
		addEntry(c, null, " 500%", 5.0); //$NON-NLS-1$
	}
  
	protected Map<String, Double> entry2ZoomValue = new HashMap<String, Double>();	
	protected void addEntry(XCombo c, Image img, String name, double zoomValue) {
		c.add(img, name);
		entry2ZoomValue.put(name, new Double(zoomValue));
	}
	
  protected DisposeListener disposeListener = new DisposeListener()
  {	
		public void widgetDisposed(DisposeEvent e) {
			zoomSupport.removeZoomListener(zoomListener);
			combo.removeSelectionListener(selectionListener);
		}	
	};
  
  protected IZoomListener zoomListener = new IZoomListener()
  {	
		public void zoomChanged(double zoom) {
			setText(zoom);
		}	
	};
	
	protected void setText(double zoom) 
	{
		int percentage = (int) Math.floor(zoom * 100);
		if (combo != null)
			combo.setText(""+percentage+" %"); //$NON-NLS-1$ //$NON-NLS-2$
	}
			
	protected SelectionListener selectionListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			XCombo c = (XCombo) e.getSource();
			String text = c.getText();
			if (text.contains("%")) { //$NON-NLS-1$
				StringBuffer sb = new StringBuffer(text);
				int index = sb.lastIndexOf("%"); //$NON-NLS-1$
				text = sb.substring(0, index); 
			}
			try {
				double newZoom = Double.parseDouble(text);
				double realZoom = newZoom / 100;
				getZoomSupport().setZoom(realZoom);
			} catch (NumberFormatException nfe) {
				// Do nothing
			}
		}	
	};
}
