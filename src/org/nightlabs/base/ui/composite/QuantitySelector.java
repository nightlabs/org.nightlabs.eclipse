/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public abstract class QuantitySelector extends XComposite
{
	private Button[] quickQtyButtons = new Button[9];
	private XComposite spacer;
	private Spinner varQtySpinner;
	private Button varQtyButton;

	public QuantitySelector(Composite parent) {
		this(parent, SWT.NONE);
	}

	public QuantitySelector(Composite parent, int style)
	{
		this(parent, style, LayoutDataMode.GRID_DATA);
	}

	public QuantitySelector(Composite parent, int style, LayoutDataMode layoutDataMode)
	{
		super(parent, style, LayoutMode.TOTAL_WRAPPER, layoutDataMode);
//		setLayoutData(new GridData());
//		getGridData().horizontalAlignment = SWT.BEGINNING;
//		getGridData().verticalAlignment = SWT.BEGINNING;
//		getGridData().grabExcessHorizontalSpace = false;
//		getGridData().grabExcessVerticalSpace = false;
//		getGridLayout().verticalSpacing = 0;
//		getGridLayout().horizontalSpacing = 0;
//		getGridLayout().marginTop = 0;
//		getGridLayout().marginBottom = 0;

		for (int i = 0; i < quickQtyButtons.length; ++i) {
			quickQtyButtons[i] = new Button(this, SWT.FLAT);
			quickQtyButtons[i].setText(Integer.toString(i + 1));
			quickQtyButtons[i].setData(new Integer(i + 1));
			quickQtyButtons[i].addSelectionListener(buttonSelectionListener);
			quickQtyButtons[i].setLayoutData(createGridData());
		}
		Label spacer = new Label(this, SWT.NONE);
		spacer.setLayoutData(createGridData());

		varQtySpinner = new Spinner(this, getBorderStyle());
		varQtySpinner.setMinimum(1);
		varQtySpinner.setMaximum(Integer.MAX_VALUE);
		varQtySpinner.setSelection(10);
		varQtySpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int qty = varQtySpinner.getSelection();
				varQtyButton.setData(new Integer(qty));
				varQtyButton.setText(Integer.toString(qty));
				relayout();
			}
		});
		varQtySpinner.setLayoutData(createGridData());

		Label spacer2 = new Label(this, SWT.NONE);
		spacer2.setLayoutData(createGridData());

		varQtyButton = new Button(this, SWT.FLAT);
		varQtyButton.setData(new Integer(varQtySpinner.getSelection()));
		varQtyButton.setText(Integer.toString(varQtySpinner.getSelection()));
		varQtyButton.addSelectionListener(buttonSelectionListener);
		varQtyButton.setLayoutData(createGridData());

		this.getGridLayout().numColumns = this.getChildren().length;
	}

	private GridData createGridData() {
		GridData gd = new GridData();
//		gd.verticalAlignment = SWT.BEGINNING;
//		gd.verticalIndent = 0;
//		gd.grabExcessVerticalSpace = false;
		return gd;
	}

	private SelectionListener buttonSelectionListener = new SelectionAdapter() {
		@Override
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
