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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A Label above a Text wrapped in a Composite.
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @deprecated Use {@link org.nightlabs.base.composite.LabeledText} instead!
 */
public class LabeledTextComposite extends TightWrapperComposite {
	private Label labelCaption;
	private Text textControl;

	public LabeledTextComposite(
			Composite parent,
			String caption,
			int style,
			boolean setLayoutData
	) {
		super(parent, style,false);
		
		this.setLayout(new GridLayout());
		this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		labelCaption = new Label(this, SWT.NONE);
		labelCaption.setText(caption);
		GridData labelCaptionLData = new GridData();
		labelCaptionLData.grabExcessHorizontalSpace = true;
		labelCaptionLData.horizontalAlignment = GridData.FILL;
		labelCaption.setLayoutData(labelCaptionLData);

		textControl = new Text(this, SWT.BORDER);
		GridData textControlLData = new GridData();
		textControlLData.grabExcessHorizontalSpace = true;
		textControlLData.horizontalAlignment = GridData.FILL;
		textControl.setLayoutData(textControlLData);
		textControl.setText(""); //$NON-NLS-1$

		this.layout();
	}
	
	public LabeledTextComposite(
			Composite parent,
			String caption,
			int style
	) {
		this(parent, caption, style, false);
	}
	
	public Text getTextControl() {
		return textControl;
	}
	
	public Label getCaptionControl() {
		return labelCaption;
	}

}
