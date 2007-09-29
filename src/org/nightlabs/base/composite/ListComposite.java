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


import java.util.ArrayList;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

/**
 * This class provides a composite able to display a list of objects of the same type as graphical representation.
 * The labels of the objects are provided by a {@link ILabelProvider}.
 * 
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 * @author Marius Heinzmann <marius[AT]NightLabs[DOT]de>
 * 
 * @param <T> The type of the elements that should be displayed inside this list.
 */
public class ListComposite<T> 
	extends AbstractListComposite<T> 
{
	// Either initialise here, pass false to all superconstructors, create a constructor pyramid for 
	// this class (smallest constructor calls next bigger one), and call createGUIControl in biggest 
	// constructor, or do NOT initialise additional fields but only declare them here and initialise  
	// them in createGUIControl!
	private List list;

	/*
	 * see {@link AbstractListComposite#AbstractListComposite(Composite, int)}
	 */
	public ListComposite(Composite parent, int style) {
		super(parent, style, true);
	}

	/* 
	 * see {@link AbstractListComposite#AbstractListComposite(Composite, int, String)}
	 */
	public ListComposite(Composite parent, int listStyle, String caption) {
		super(parent, listStyle, caption, true);
	}

	/*
	 * see {@link AbstractListComposite#AbstractListComposite(Composite, int, String, ILabelProvider)}
	 */
	public ListComposite(Composite parent, int listStyle, String caption, ILabelProvider labelProvider) {
		super(parent, listStyle, caption, true, labelProvider);
	}

	/*
	 * see {@link AbstractListComposite#AbstractListComposite(Composite, int, String, ILabelProvider, LayoutMode)}
	 */
	public ListComposite(Composite parent, int listStyle, String caption, ILabelProvider labelProvider,
			LayoutMode layoutMode) {
		super(parent, listStyle, caption, true, labelProvider, layoutMode);
	}

	/*
	 * see {@link AbstractListComposite#AbstractListComposite(Composite, int, String, ILabelProvider, LayoutMode, LayoutDataMode)}
	 */
	public ListComposite(Composite parent, int listStyle, String caption, ILabelProvider labelProvider,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, listStyle, caption, true, labelProvider, layoutMode, layoutDataMode);
	}

	/*
	 * see {@link AbstractListComposite#AbstractListComposite(Composite, int, String, ILabelProvider, LayoutMode, LayoutDataMode, int)}
	 */
	public ListComposite(Composite parent, int listStyle, String caption, ILabelProvider labelProvider, 
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, int compositeStyle) {
		super(parent, listStyle, caption, true, labelProvider, layoutMode, layoutDataMode, compositeStyle);
	}

	@Override
	protected void addElementToGui(int index, T element)
	{
		list.add(labelProvider.getText(element), index);
	}

	@Override
	protected void createGuiControl(Composite parent, int widgetStyle, String caption)
	{
		widgetStyle |= getBorderStyle(); // forces border around List according to the context it is in
		Composite wrapper = parent;
		if (caption != null) {
			// TODO: I keep the two columns, but why do we want it like that? (marius)
			wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE, 2);
			label = new Label(wrapper, SWT.NONE);
			label.setText(caption);
		}
		
		list = new List(wrapper, widgetStyle | SWT.V_SCROLL | SWT.H_SCROLL);
		list.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (wrapper.getLayout() instanceof GridLayout)
				list.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int[] selectedIndices = list.getSelectionIndices();
				java.util.List<T> selectedElements = new ArrayList<T>(selectedIndices.length);
				for (int index : selectedIndices) {
					selectedElements.add( elements.get(index) );
				}
				
				fireSelectionChangedEvent( new StructuredSelection(selectedElements) );
			}
		});
	}
	
	public Label getLabel() {
		if (label != null)
			return label;
		else
			throw new IllegalStateException("Cannot access the label if its creation hasn't been requested earlier."); //$NON-NLS-1$
	}

	@Override
	protected int internal_getSelectionIndex()
	{
		return list.getSelectionIndex();
	}

	@Override
	protected int[] internal_getSelectionIndices()
	{
		return list.getSelectionIndices();
	}

	@Override
	protected void removeAllElementsFromGui()
	{
		list.removeAll();
	}

	@Override
	protected void removeElementFromGui(int index)
	{
		list.remove(index);
	}

	@Override
	protected void internal_setSelection(int index)
	{
		list.select(index);
		list.showSelection();
	}

	@Override
	protected int internal_getSelectionCount()
	{
		return list.getSelectionCount();
	}

	@Override
	protected void internal_setSelection(int[] indices)
	{
		list.select(indices);
		list.showSelection();
	}

	public List getList()
	{
		return list;
	}
	
	/**
	 * @see AbstractListComposite#refreshElement(T)
	 */
	public void refreshElement(T elem)
	{
		int index = getElementIndex(elem);
		list.setItem(index, labelProvider.getText(elem));
	}

	@Override
	public Control getControl() {
		return list;
	}

}