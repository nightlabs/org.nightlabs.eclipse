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

import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * <code>ComboComposite</code> wraps a native {@link Combo} and provides a high-level viewer-API
 * for it.
 * <p>
 * Instead of this class, you can use an {@link XComboComposite} which supports additional features
 * like e.g. an icon. Since both extend {@link AbstractListComposite}, the API is the same and
 * you can easily replace one by another.
 * </p>
 *
 * @deprecated Use {@link XComboComposite} instead: For aethetic reasons, it is recommended to use
 * the same combo everywhere and the native combo lacks some important features which
 * <code>XCombo</code> supports.
 *
 * @author Marius Heinzmann <marius[AT]nightlabs[DOT]de>
 * @param <T>
 */
public class ComboComposite<T> extends AbstractListComposite<T> {

	// Either initialise here, pass false to all superconstructors, create a constructor pyramid for 
	// this class (smallest constructor calls next bigger one), and call createGUIControl in biggest 
	// constructor, or do NOT initialise additional fields but only declare them here and initialise  
	// them in createGUIControl!
	private Combo combo;

	public ComboComposite(Composite parent, int comboStyle)
	{
		super(parent, comboStyle, true);
	}

	public ComboComposite(Composite parent, int comboStyle, List<T> elements) {
		this(parent, comboStyle, elements, (String)null);
	}

	public ComboComposite(Composite parent, int comboStyle, List<T> elements, String caption) {
		super(parent, comboStyle, caption, true, new LabelProvider());
		setInput(elements);
	}

//	public ComboComposite(Composite parent, int style, ILabelProvider labelProvider, LayoutMode layoutMode,
//			LayoutDataMode layoutDataMode) {
//		super(labelProvider, parent, style, layoutMode, layoutDataMode);
//	}
//
//	public ComboComposite(Composite parent, int style, ILabelProvider labelProvider) {
//		super(labelProvider, parent, style);
//	}

	public ComboComposite(Composite parent, int comboStyle, ILabelProvider labelProvider, String caption) {
		super(parent, comboStyle, caption, true, labelProvider);
	}

	public ComboComposite(Composite parent, int comboStyle, List<T> elements, ILabelProvider labelProvider) {
		this(parent, comboStyle, elements, labelProvider, (String)null);
	}

	public ComboComposite(Composite parent, int style, String caption) {
		super(parent, style, caption, true, new LabelProvider());
	}

	public ComboComposite(Composite parent, int comboStyle, List<T> elements, ILabelProvider labelProvider, String caption) {
		super(parent, comboStyle, caption, true, labelProvider);
		setInput(elements);
	}

	public ComboComposite(Composite parent, int comboStyle, ILabelProvider labelProvider) {
		super(parent, comboStyle, (String) null, true, labelProvider);
	}

	protected void addElementToGui(int index, T element) {
		combo.add(labelProvider.getText(element), index);
	}

	protected void createGuiControl(Composite parent, int style, String caption) {
		getGridData().grabExcessVerticalSpace = false;
		style |= getBorderStyle();
		if ((style & SWT.SIMPLE) == 0 && (style & SWT.DROP_DOWN) == 0)
			style |= SWT.DROP_DOWN;
		if (caption != null) {
			XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE, 2);
			wrapper.getGridData().grabExcessVerticalSpace = false;
			label = new Label(wrapper, SWT.NONE);
			label.setText(caption);
			combo = new Combo(wrapper, style);
			combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		} else {
			combo = new Combo(parent, style);
			if (parent.getLayout() instanceof GridLayout) {
				combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			}
		}
		
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection selection = new StructuredSelection(
						combo.getItem(combo.getSelectionIndex()) );
				fireSelectionChangedEvent(selection);
			}
		});		
	}

	public Label getLabel() {
		if (label != null)
			return label;
		else
			throw new IllegalStateException("Cannot access the label if its creation hasn't been requested earlier."); //$NON-NLS-1$
	}

	protected int internal_getSelectionIndex() {
		return combo.getSelectionIndex();
	}

	protected int[] internal_getSelectionIndices() {
		return new int[] { combo.getSelectionIndex() };
	}

	protected void removeAllElementsFromGui() {
		combo.removeAll();
	}

	protected void removeElementFromGui(int index) {
		combo.remove(index);
	}

	protected void internal_setSelection(int index) {
		combo.select(index);
	}

	protected void internal_setSelection(int[] indices) {
		if (indices.length < 1)
			combo.select(-1); // seems to be ignored - a combo seems to have always
		// one item selected - This is an SWT issue, because with Delphi + Windows, it was possible to deselect
		// and with SWT + Linux it works, too. Marco.
		else
			combo.select(indices[0]);
	}

	protected int internal_getSelectionCount() {
		if (combo.getSelectionIndex() < 0 || combo.getItemCount() < 1)
			return 0;
		else
			return 1;
	}

//	This should not be needed and should be completely hidden. (marius)
//	public Combo getCombo() {
//		return combo;
//	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Combo#addModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void addModifyListener(ModifyListener listener) {
		combo.addModifyListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Combo#addSelectionListener(org.eclipse.swt.events.SelectionListener)
	 * @deprecated The methods from combo should be completely hidden - there is already an ISelectionProvider implemented in this class
	 */
	public void addSelectionListener(SelectionListener listener) {
		combo.addSelectionListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Combo#removeModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void removeModifyListener(ModifyListener listener) {
		combo.removeModifyListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Combo#removeSelectionListener(org.eclipse.swt.events.SelectionListener)
	 * @deprecated The methods from combo should be completely hidden - there is already an ISelectionProvider implemented in this class
	 */
	public void removeSelectionListener(SelectionListener listener) {
		combo.removeSelectionListener(listener);
	}

	protected void refreshElement(T element) {
		int index = getElementIndex(element);
		combo.setItem(index, labelProvider.getText(element));
	}

	/**
	 * @return the result from the delegated {@link Combo#getText()}.
	 */
	public String getText()
	{
		return combo.getText();
	}
	/**
	 * This method delegates to {@link Combo#setText(String)}
	 * @param text The text.
	 */
	public void setText(String text)
	{
		combo.setText(text);
	}

	@Override
	public Control getControl() {
		return combo;
	}

}