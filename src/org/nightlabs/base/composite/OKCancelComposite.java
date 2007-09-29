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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class OKCancelComposite extends XComposite {

	private Button okButton;
	private Button cancelButton;
	private Label label;

	public OKCancelComposite(Composite parent, int style, String text, String okTxt, String cancelTxt) {
		super(parent, style, LayoutMode.ORDINARY_WRAPPER);
		if ((text != null) && (!"".equals(text))) { //$NON-NLS-1$
			label = new Label(this, SWT.WRAP);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING));
			label.setText(text);
		}
		XComposite buttonWrapper = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		buttonWrapper.getGridLayout().numColumns = 2;
		okButton = new Button(buttonWrapper, SWT.PUSH);
		GridData okGD = new GridData();
		okGD.grabExcessHorizontalSpace = false;
		okGD.horizontalAlignment = GridData.END;
		okButton.setLayoutData(okGD);
		okButton.setText(okTxt);
		okButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				okPressed();
			}
		});

		cancelButton = new Button(buttonWrapper, SWT.PUSH);
		cancelButton.setLayoutData(new GridData());
		cancelButton.setText(cancelTxt);
		cancelButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				cancelPressed();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				cancelPressed();
			}
		});
	}
	
	protected abstract void okPressed();
	
	protected abstract void cancelPressed();
	
	

}
