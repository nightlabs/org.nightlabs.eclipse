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

package org.nightlabs.base.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A Label above a Text wrapped in a Composite. 
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class LabeledText extends XComposite {
	private Label labelCaption;
	private Text textControl;

	/**
	 * @param parent The composite into which the new <tt>LabeledText</tt> will be added as child.
	 * @param caption The title that will be shown by the wrapped <tt>Label</tt>.
	 * @param textStyle The style that will be applied to the wrapped <tt>Text</tt>.
	 * @param setLayoutData Whether or not to create and assign a {@link GridData} object.
	 */
	public LabeledText(
			Composite parent,
			String caption,
			int textStyle,
			int labelStyle,
			int wrapperStyle,
			boolean setLayoutData
	) {
		super(parent, wrapperStyle,
				LayoutMode.TIGHT_WRAPPER, 
				setLayoutData ? LayoutDataMode.GRID_DATA : LayoutDataMode.NONE);
		getGridLayout().verticalSpacing = 5;
		this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		if (caption != null) {
			labelCaption = new Label(this, labelStyle);
			labelCaption.setText(caption);
			GridData labelCaptionLData = new GridData();
			labelCaptionLData.grabExcessHorizontalSpace = true;
			labelCaptionLData.horizontalAlignment = GridData.FILL;
			labelCaption.setLayoutData(labelCaptionLData);
		}

		textControl = new Text(this, textStyle);
		GridData textControlLData = new GridData();
		textControlLData.grabExcessHorizontalSpace = true;
		textControlLData.horizontalAlignment = GridData.FILL;
		textControl.setLayoutData(textControlLData);
		textControl.setText(""); //$NON-NLS-1$
		this.layout();
	}

	/**
	 * This calls {@link #LabeledText(Composite, String, int, int, int, boolean)} with
	 * <tt>labelStyle = SWT.NONE</tt> and <tt>wrapperStyle = SWT.NONE</tt>.
	 *
	 * @param parent The composite into which the new <tt>LabeledText</tt> will be added as child.
	 * @param caption The title that will be shown by the wrapped <tt>Label</tt>.
	 * @param textStyle The style that will be applied to the wrapped <tt>Text</tt>.
	 * @param setLayoutData Whether or not to create and assign a {@link GridData} object.
	 */
	public LabeledText(
			Composite parent,
			String caption,
			int textStyle,
			boolean setLayoutData
	) {
		this(parent, caption, textStyle, SWT.NONE, SWT.NONE, setLayoutData);
	}

	/**
	 * This calls {@link #LabeledText(Composite, String, int, boolean)} with <tt>setLayoutData = true</tt>.
	 *
	 * @param parent The composite into which the new <tt>LabeledText</tt> will be added as child.
	 * @param caption The title that will be shown by the wrapped <tt>Label</tt>.
	 * @param textStyle The style that will be applied to the wrapped <tt>Text</tt>.
	 */
	public LabeledText(
			Composite parent,
			String caption,
			int textStyle
	) {
		this(parent, caption, textStyle, false);
	}

	/**
	 * This calls {@link #LabeledText(Composite, String, int)} with <tt>textStyle = SWT.BORDER</tt>.
	 *
	 * @param parent The composite into which the new <tt>LabeledText</tt> will be added as child.
	 * @param caption The title that will be shown by the wrapped <tt>Label</tt>.
	 */
	public LabeledText(
			Composite parent,
			String caption
	) {
		this(parent, caption, XComposite.getBorderStyle(parent));
	}

	public Text getTextControl() {
		return textControl;
	}
	
	/**
	 * Returns the caption control. 
	 * Note that this will be <code>null</code> if the LabeledText was
	 * created with a caption of <code>null</code>.
	 */
	public Label getCaptionControl() {
		return labelCaption;
	}
	
	/**
	 * Set the caption. 
	 * Will have no affect, if the caption was not created, is already disposed
	 * or if the given caption is null.
	 * 
	 * @param caption The caption to set.
	 */
	public void setCaption(String caption) {
		if (labelCaption != null && !labelCaption.isDisposed() && caption != null)
			labelCaption.setText(caption);
	}

	/**
	 * Returns the text of this LabeledText's 
	 * {@link #getTextControl()}.
	 * 
	 * @return The text currently shown in the text control.
	 */
	public String getText() {
		return getTextControl().getText();
	}
	
	/**
	 * Set the text that is displayed in the {@link #getTextControl()}.
	 * 
	 * @param text The text to set.
	 */
	public void setText(String text) {		
		getTextControl().setText(text);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Text#addModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void addModifyListener(ModifyListener listener) {
		textControl.addModifyListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Text#removeModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void removeModifyListener(ModifyListener listener) {
		textControl.removeModifyListener(listener);
	}
	
}
