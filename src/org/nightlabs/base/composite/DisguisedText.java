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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Provides a Text Widget with no border
 * and the same Background as
 * its parent. As subclassing of {@link org.eclipse.swt.widgets.Widget}
 * is not allowed a static method will 
 * create and return a Text.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class DisguisedText {

	/**
	 * Creates a disguised Text.
	 * 
	 * @param parent
	 */
	public static Text createText(Composite parent) {
		Text text = new Text(parent, XComposite.getBorderStyle(parent));
//		text.setBackground(parent.getBackground());
		return text;
	}
	
	/**
	 * Creates a Label and a disguised Text and adds
	 * GridDatas to them assuming that the parent has
	 * a GridLayout with two free columns in the current row. 
	 * 
	 * @param title The title
	 * @param parent The parent composite
	 * @return the newly created {@link LabeledDisguisedText}
	 */
	public static LabeledDisguisedText createLabeledText(String title, Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(title);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Text text = createText(parent);
		text.setText(""); //$NON-NLS-1$
		text.setEditable(true);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.grabExcessHorizontalSpace = true;
//		gd.horizontalAlignment = GridData.FILL;
		text.setLayoutData(gd);
		LabeledDisguisedText result = new LabeledDisguisedText();
		result.setLabelControl(label);
		result.setTextControl(text);
		return result;
	}
	
	/**
	 * Helper class for {@link DisguisedText#createLabeledText(String, Composite)}
	 *
	 */
	public static class LabeledDisguisedText {

		/**
		 * 
		 */
		public LabeledDisguisedText() {
			super();
		}
		
		private Label labelControl;
		private Text textControl;
		
		public Label getLabelControl() {
			return labelControl;
		}
		public void setLabelControl(Label labelControl) {
			this.labelControl = labelControl;
		}
		public Text getTextControl() {
			return textControl;
		}
		public void setTextControl(Text textControl) {
			this.textControl = textControl;
		}

	}	
}
