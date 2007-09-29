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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

public abstract class QuantitySelector extends XComposite
{
	private Button[] quickQtyButtons = new Button[9];
	private XComposite spacer;
	private Spinner varQtySpinner;
	private Button varQtyButton;

	public QuantitySelector(Composite parent)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		getGridData().grabExcessHorizontalSpace = false;
		getGridData().grabExcessVerticalSpace = false;
		getGridLayout().verticalSpacing = 0;
		getGridLayout().horizontalSpacing = 0;

		for (int i = 0; i < quickQtyButtons.length; ++i) {
			quickQtyButtons[i] = new Button(this, SWT.FLAT);
			quickQtyButtons[i].setText(Integer.toString(i + 1));
			quickQtyButtons[i].setData(new Integer(i + 1));
			quickQtyButtons[i].addSelectionListener(buttonSelectionListener);
		}
		spacer = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		spacer.setLayoutData(new GridData(4, 1));
		varQtySpinner = new Spinner(this, SWT.BORDER);
		varQtySpinner.setMinimum(1);
		varQtySpinner.setMaximum(Integer.MAX_VALUE);
		varQtySpinner.setSelection(10);
		varQtySpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int qty = varQtySpinner.getSelection();
				varQtyButton.setData(new Integer(qty));
				varQtyButton.setText(Integer.toString(qty));
				relayout();
			}
		});
		varQtyButton = new Button(this, SWT.FLAT);
		varQtyButton.setData(new Integer(varQtySpinner.getSelection()));
		varQtyButton.setText(Integer.toString(varQtySpinner.getSelection()));
		varQtyButton.addSelectionListener(buttonSelectionListener);

		this.getGridLayout().numColumns = this.getChildren().length;
	}

	private SelectionListener buttonSelectionListener = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e)
		{
			int qty = ((Integer)((Button)e.getSource()).getData()).intValue();
			quantitySelected(qty);
		}
	};

	/**
	 * This method is called after the text in the variable quantity
	 * button/spinner has changed. Because the number might have changed
	 * length, a new layout of the whole composite (including the parent)
	 * might be necessary.
	 * <p>
	 * Usually, a call to <code>MainComposite.this.layout(true, true);</code>
	 * is all you need to do.
	 * </p>
	 */
	protected abstract void relayout();

	/**
	 * This method is called when the user has selected a quantity.
	 *
	 * @param qty The quantity the user has chosen.
	 */
	protected abstract void quantitySelected(int qty);
}
