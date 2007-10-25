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

package org.nightlabs.editor2d.viewer.ui.action;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.action.AbstractContributionItem;
import org.nightlabs.base.ui.util.ColorUtil;
import org.nightlabs.editor2d.viewer.ui.IViewer;
import org.nightlabs.editor2d.viewer.ui.event.IColorChangedListener;
import org.nightlabs.editor2d.viewer.ui.resource.Messages;

public class BackgroundContributionItem 
extends AbstractContributionItem 
{
	public static String ID = BackgroundContributionItem.class.getName();
	
	public BackgroundContributionItem(IViewer viewer) 
	{
		super(ID, Messages.getString("org.nightlabs.editor2d.viewer.ui.action.BackgroundContributionItem.name")); //$NON-NLS-1$
		this.viewer = viewer;
		bgColor = viewer.getBgColor();
	}
	
	protected IViewer viewer = null;	
	protected Button colorButton = null;
	protected Color bgColor = null;
	@Override
	protected Control createControl(Composite parent) 
	{
		colorButton = new Button(parent, SWT.NONE);
		colorButton.setText(getName());
		colorButton.setBackground(bgColor);
		colorButton.addSelectionListener(selectionListener);
		colorButton.addDisposeListener(disposeListener);
		return colorButton;
	}
	
	protected SelectionListener selectionListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			ColorDialog colorDialog = new ColorDialog(Display.getCurrent().getActiveShell());
			RGB rgb = colorDialog.open();
			if (rgb != null) {
				bgColor.dispose();
				java.awt.Color oldColor = ColorUtil.toAWTColor(bgColor);
				bgColor = new Color(Display.getCurrent(), rgb);
				colorButton.setBackground(bgColor);				
				viewer.setBgColor(bgColor);
				fireColorChanged(oldColor, ColorUtil.toAWTColor(bgColor));
				viewer.updateCanvas();
			}
		}	
	};
	
	protected DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) {
			bgColor.dispose();
			colorButton.removeSelectionListener(selectionListener);
		}	
	};
			
	protected Collection<IColorChangedListener> colorListener = null;
	protected Collection<IColorChangedListener> getColorListener() {
		if (colorListener == null)
			colorListener = new LinkedList<IColorChangedListener>();
		return colorListener;
	}
	public void addColorChangedListener(IColorChangedListener colorListener) {
		getColorListener().add(colorListener);
	}
	public void removeColorChangedListener(IColorChangedListener colorListener) {
		getColorListener().remove(colorListener);
	}
	
	protected void fireColorChanged(java.awt.Color oldColor, java.awt.Color newColor) 
	{
		for (IColorChangedListener cl : getColorListener()) {
			cl.colorChanged(oldColor, newColor);
		}
	}
}
