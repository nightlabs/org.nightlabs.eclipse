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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.custom.XCombo;

/**
 * <code>XComboComposite</code> wraps an {@link XCombo} and provides a high-level viewer-API for it.
 * An <code>XCombo</code> is a non-native combo-box implementation with supports additional features
 * like an icon.
 * <p>
 * If you don't need the additional features, you might instead use {@link ComboComposite} which
 * wraps a native {@link org.eclipse.swt.widgets.Combo}. It is, however, recommended to use the
 * same combo everywhere for aesthetic reasons.
 * </p>
 *
 * @author Daniel.Mazurek at Nightlabs dot de
 * @author Marius Heinzmann <marius[AT]NightLabs[DOT]de>
 */
public class XComboComposite<T> 
	extends AbstractListComposite<T> 
{
	
	/**
	 * @see AbstractListComposite#AbstractListComposite(Composite, int, boolean)
	 */
	public XComboComposite(Composite parent, int comboStyle) {
		super(parent, comboStyle, true);
	}
	
	/**
	 * @see AbstractListComposite#AbstractListComposite(Composite, int, String, boolean)
	 */
	public XComboComposite(Composite parent, int comboStyle, String caption) {
		super(parent, comboStyle, caption, true);
	}

	public XComboComposite(Composite parent, int comboStyle, ILabelProvider labelProvider) {
		super(parent, comboStyle, (String)null, true, labelProvider);
	}

	/**
	 * @see AbstractListComposite#AbstractListComposite(Composite, int, String, boolean, ILabelProvider)
	 */
	public XComboComposite(Composite parent, int comboStyle, String caption, ILabelProvider labelProvider) {
		super(parent, comboStyle, caption, true, labelProvider);
	}

	/**
	 * @see AbstractListComposite#AbstractListComposite(Composite, int, String, boolean, ILabelProvider, LayoutMode)
	 */
	public XComboComposite(Composite parent, int comboStyle, String caption, ILabelProvider labelProvider,
			LayoutMode layoutMode) {
		super(parent, comboStyle, caption, true, labelProvider, layoutMode);
	}

	/**
	 * @see AbstractListComposite#AbstractListComposite(Composite, int, String, boolean, ILabelProvider, LayoutMode, LayoutDataMode)  
	 */
	public XComboComposite(Composite parent, int comboStyle, String caption, ILabelProvider labelProvider,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode)	{
		super(parent, comboStyle, caption, true, labelProvider, layoutMode, layoutDataMode);
	}

	/**
	 * @see AbstractListComposite#AbstractListComposite(Composite, int, String, boolean, ILabelProvider, LayoutMode, LayoutDataMode, int)
	 */
	public XComboComposite(Composite parent, int comboStyle, String caption, 
			ILabelProvider labelProvider, LayoutMode layoutMode, LayoutDataMode layoutDataMode, int compositeStyle)
	{
		super(parent, comboStyle, caption, true, labelProvider, layoutMode, layoutDataMode, compositeStyle);
	}
	
//	/**
//	 * @return the backend XCombo widget.
//	 */
//	This shouldn't be needed. All methods of the Combo should be completely hidden! (marius)
//	public XCombo getCombo() {
//		return imageCombo;
//	}

	/**
	 * @param listener
	 * @see org.nightlabs.base.custom.XCombo#addModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void addModifyListener(ModifyListener listener) {
		imageCombo.addModifyListener(listener);
	}

	/**
	 * @param listener
	 * @see org.nightlabs.base.custom.XCombo#addSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void addSelectionListener(SelectionListener listener) {
		imageCombo.addSelectionListener(listener);
	}

	/**
	 * @param listener
	 * @see org.nightlabs.base.custom.XCombo#removeModifyListener(org.eclipse.swt.events.ModifyListener)
	 */
	public void removeModifyListener(ModifyListener listener) {
		imageCombo.removeModifyListener(listener);
	}

	/**
	 * @param listener
	 * @see org.nightlabs.base.custom.XCombo#removeSelectionListener(org.eclipse.swt.events.SelectionListener)
	 */
	public void removeSelectionListener(SelectionListener listener) {
		imageCombo.removeSelectionListener(listener);
	}

	@Override
	protected void addElementToGui(int index, T element) {
		imageCombo.add(labelProvider.getImage(element), labelProvider.getText(element));
	}

	
	// Either initialise here, pass false to all superconstructors, create a constructor pyramid for 
	// this class (smallest constructor calls next bigger one), and call createGUIControl in biggest 
	// constructor, or do NOT initialise additional fields but only declare them here and initialise  
	// them in createGUIControl!
	/**
	 * The backend Combo used by this implementation. 
	 */
	private XCombo imageCombo;

	@Override
	protected void createGuiControl(Composite parent, int widgetStyle, String caption) 
	{
		getGridData().grabExcessVerticalSpace = false;
		if ( caption != null && ! "".equals(caption) ) { //$NON-NLS-1$
			XComposite composite = new XComposite(parent, SWT.NONE, LayoutDataMode.GRID_DATA);
			label = new Label(composite, SWT.NONE);
			label.setText(caption);
			imageCombo = new XCombo(parent, widgetStyle);
			imageCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		} else {
			imageCombo = new XCombo(parent, widgetStyle);
			if ( parent.getLayout() instanceof GridLayout )
				imageCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		imageCombo.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selection = new StructuredSelection(
						imageCombo.getItem(imageCombo.getSelectionIndex()) );
				fireSelectionChangedEvent(selection);
			}
		
		});
	}

	@Override
	protected int internal_getSelectionCount() {
		if (imageCombo.getSelectionIndex() < 0 || imageCombo.getItemCount() < 1)
			return 0;
		else
			return 1;
	}

	@Override
	protected int internal_getSelectionIndex() {
		return imageCombo.getSelectionIndex();
	}

	@Override
	protected int[] internal_getSelectionIndices() {
		return new int[] { imageCombo.getSelectionIndex() };
	}

	@Override
	protected void internal_setSelection(int index) {
		imageCombo.select(index);
	}

	@Override
	protected void internal_setSelection(int[] indices) {
		if (indices.length > 1 )
			throw new IllegalArgumentException("Multiple selections are not supported by Combos!"); //$NON-NLS-1$
		
		internal_setSelection(indices[0]);
	}

	@Override
	protected void refreshElement(T elem) {
		int index = getElementIndex(elem);
		if (index < 0)
			return;
		
		imageCombo.setItem(index, labelProvider.getImage(elem), labelProvider.getText(elem));
	}

	@Override
	protected void removeAllElementsFromGui() {
		imageCombo.removeAll();
	}

	@Override
	protected void removeElementFromGui(int index) {
		imageCombo.remove(index);
	}

	@Override
	public Control getControl() {
		return imageCombo;
	}

}
