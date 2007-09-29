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

package org.nightlabs.base.celleditor;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.nightlabs.base.util.ColorUtil;
import org.nightlabs.base.util.ImageUtil;


public class AWTColorCellEditor 
extends DialogCellEditor
{
  /*
	 * The default extent in pixels.
	 */
//	private static final int DEFAULT_EXTENT = 16;
	
	/**
	 * Gap between between image and text in pixels.
	 */	 	
	private static final int GAP = 6;

	/**
	 * The composite widget containing the color and RGB label widgets
	 */
	private Composite composite;

	/**
	 * The label widget showing the current color.
	 */
	private Label colorLabel;

	/**
	 * The label widget showing the RGB values.
	 */
	private Label rgbLabel;

	/**
	 * The image.
	 */
	private Image image;
	
	/**
	 * Internal class for laying out this cell editor.
	 */
	private class ColorCellLayout extends Layout {
		public Point computeSize(
			Composite editor, 
			int wHint, 
			int hHint, 
			boolean force) {
			if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT)
				return new Point(wHint, hHint);
			
			if (showColor) {
				Point colorSize = colorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				Point rgbSize = rgbLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				return new Point(
					colorSize.x + GAP + rgbSize.x, 
					Math.max(colorSize.y, rgbSize.y));				
			}
			else {
				return rgbLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
			}
		}
		public void layout(Composite editor, boolean force) 
		{
			if (showColor) {
				Rectangle bounds = editor.getClientArea();
				Point colorSize = colorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				Point rgbSize = rgbLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				int ty = (bounds.height - rgbSize.y) / 2;
				if (ty < 0)
					ty = 0;
				colorLabel.setBounds(-1, 0, colorSize.x, colorSize.y);
				rgbLabel.setBounds(
					colorSize.x + GAP - 1, 
					ty, 
					bounds.width - colorSize.x - GAP, 
					bounds.height); 				
			}
			else {
				Rectangle bounds = editor.getClientArea();
				Point rgbSize = rgbLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				int ty = (bounds.height - rgbSize.y) / 2;
				if (ty < 0)
					ty = 0;				
				rgbLabel.setBounds(
						GAP - 1, 
						ty, 
						bounds.width - GAP, 
						bounds.height); 								
			}
		}
	}
	
	/**
	 * Creates a new color cell editor parented under the given control.
	 * The cell editor value is black (<code>RGB(0,0,0)</code>) initially, and has no 
	 * validator.
	 *
	 * @param parent the parent control
	 */
	public AWTColorCellEditor(Composite parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Creates a new color cell editor parented under the given control.
	 * The cell editor value is black (<code>RGB(0,0,0)</code>) initially, and has no 
	 * validator.
	 *
	 * @param parent the parent control
	 * @param style the style bits
	 * 
	 */
	public AWTColorCellEditor(Composite parent, int style) {
		super(parent, style);	  
	}

	/**
	 * Creates a new color cell editor parented under the given control.
	 * The cell editor value is black (<code>RGB(0,0,0)</code>) initially, and has no 
	 * validator.
	 *
	 * @param parent the parent control
	 * @param style the style bits
	 * @param showColor determines if the color should be visible in an additional separate label
	 * 
	 */
	public AWTColorCellEditor(Composite parent, int style, boolean showColor) {
		super(parent, style);	  
		this.showColor = showColor;
	}
	
	private boolean showColor = false;
	
	/* 
	 * Method declared on DialogCellEditor.
	 */
	protected Control createContents(Composite cell) 
	{
		Color bg = cell.getBackground();
		composite = new Composite(cell, getStyle());
		composite.setBackground(bg);
		composite.setLayout(new ColorCellLayout());
		if (showColor) {
			colorLabel = new Label(composite, SWT.LEFT);
			colorLabel.setBackground(bg);			
		}
		rgbLabel = new Label(composite, SWT.LEFT);
		rgbLabel.setBackground(bg);
		rgbLabel.setFont(cell.getFont());
		return composite;
	}

	/* 
	 * Method declared on CellEditor.
	 */
	public void dispose() 
	{
		if (image != null) {
			image.dispose();
			image = null;
		}
		super.dispose();
	}

	/*
	 * Method declared on DialogCellEditor.
	 */
	protected Object openDialogBox(Control cellEditorWindow) 
	{
		ColorDialog dialog = new ColorDialog(cellEditorWindow.getShell());
		Object value = getValue();
		if (value != null) {
		  if (value instanceof String) {
		    value = new java.awt.Color(0,0,0);
		  }
		  if (value instanceof java.awt.Color) {
		    value = ColorUtil.toRGB((java.awt.Color)value);
		  }
		  dialog.setRGB((RGB) value);	  
		}		
		value = dialog.open(); 
		RGB rgb = dialog.getRGB();
		java.awt.Color awtColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
		return awtColor;
	}

	/*
	 * Method declared on DialogCellEditor.
	 */
	protected void updateContents(Object value) 
	{
	  if (value != null) {
		  Logger.getLogger(AWTColorCellEditor.class).debug("value instanceof "+value.getClass()); //$NON-NLS-1$
	  }

	  if (value instanceof String) {
	    value = new java.awt.Color(0,0,0);
	  }
	  
	  if (value == null) {
	    value = new java.awt.Color(0,0,0);
	  }
	  	  
	  RGB rgb = ColorUtil.toRGB((java.awt.Color)value);	
	  if (showColor) {
			image = ImageUtil.createColorImage((java.awt.Color)value);
			colorLabel.setImage(image);	  	
	  }
	
		rgbLabel.setText("(" + rgb.red + "," + rgb.green + "," + rgb.blue + ")");//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	}
}

