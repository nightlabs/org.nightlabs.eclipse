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
 ******************************************************************************/

package org.nightlabs.base.composite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.selection.SelectionProvider;

/**
 * This class provides abstract logic for managing a list of objects and displaying them in
 * list-style gui controls as e.g. combo boxes or lists.
 * 
 * The labels of the objects are provided by a {@link ILabelProvider}.
 * 
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 * @author Marius Heinzmann <marius[AT]NightLabs[DOT]de>
 * 
 * @param <T> The type of the elements that should be displayed inside this combo.
 */
public abstract class AbstractListComposite<T> extends XComposite
implements ISelectionProvider
{	
	/**
	 * The (optional) label, which all subclasses may use as they wish.
	 */
	protected Label label = null;
	/**
	 * List of the objects that should be managed.
	 */
	protected List<T> elements = null;
	
	/**
	 * Label provider for the managed elements.
	 */
	protected ILabelProvider labelProvider;
	
	/**
	 * Creates a fully configurable ListComposite. If a subclass defines own members (declares and defines)
	 * or initialises own members in its own constructors, then this subclass needs to set 
	 * <code>createGUI = false</code>, otherwise uninitialised members might be accessed in 
	 * {@link #createGuiControl(Composite, int, String)}!  
	 * 
	 * @param parent the Composite to place this one in.
	 * @param widgetStyle the style of the widget which displays the managed List of elements.
	 * @param createGUI boolean to indicate whether this class should call 
	 * 		{@link #createGuiControl(Composite, int, String)} or the subclass will in its own constructors.
	 */
	public AbstractListComposite(Composite parent, int widgetStyle, boolean createGUI)
	{
		this(parent, widgetStyle, (String) null, createGUI);		
	}
	
	/**
	 * Creates a fully configurable ListComposite. If a subclass defines own members (declares and defines)
	 * or initialises own members in its own constructors, then this subclass needs to set 
	 * <code>createGUI = false</code>, otherwise uninitialised members might be accessed in 
	 * {@link #createGuiControl(Composite, int, String)}!  
	 * 
	 * @param parent the Composite to place this one in.
	 * @param widgetStyle the style of the widget which displays the managed List of elements.
	 * @param caption the String to use as a caption, if none given no caption is created.
	 * @param createGUI boolean to indicate whether this class should call 
	 * 		{@link #createGuiControl(Composite, int, String)} or the subclass will in its own constructors.
	 */
	public AbstractListComposite(Composite parent, int widgetStyle, String caption, boolean createGUI) {
		this(parent, widgetStyle, caption, createGUI, new LabelProvider());
	}
	
	/**
	 * Creates a fully configurable ListComposite. If a subclass defines own members (declares and defines)
	 * or initialises own members in its own constructors, then this subclass needs to set 
	 * <code>createGUI = false</code>, otherwise uninitialised members might be accessed in 
	 * {@link #createGuiControl(Composite, int, String)}!  
	 * 
	 * @param parent the Composite to place this one in.
	 * @param widgetStyle the style of the widget which displays the managed List of elements.
	 * @param caption the String to use as a caption, if none given no caption is created.
	 * @param createGUI boolean to indicate whether this class should call 
	 * 		{@link #createGuiControl(Composite, int, String)} or the subclass will in its own constructors.
	 * @param labelProvider the {@link ILabelProvider}, which will generate the text as well as image 
	 * 		labels for the stored elements.
	 */
	public AbstractListComposite(Composite parent, int widgetStyle, String caption, boolean createGUI, 
			ILabelProvider labelProvider) 
	{
		this(parent, widgetStyle, caption, createGUI, labelProvider, LayoutMode.TIGHT_WRAPPER);
	}
	
	/**
	 * Creates a fully configurable ListComposite. If a subclass defines own members (declares and defines)
	 * or initialises own members in its own constructors, then this subclass needs to set 
	 * <code>createGUI = false</code>, otherwise uninitialised members might be accessed in 
	 * {@link #createGuiControl(Composite, int, String)}!  
	 * 
	 * @param parent the Composite to place this one in.
	 * @param widgetStyle the style of the widget which displays the managed List of elements.
	 * @param caption the String to use as a caption, if none given no caption is created.
	 * @param createGUI boolean to indicate whether this class should call 
	 * 		{@link #createGuiControl(Composite, int, String)} or the subclass will in its own constructors.
	 * @param labelProvider the {@link ILabelProvider}, which will generate the text as well as image 
	 * 		labels for the stored elements.
	 * @param layoutMode the {@link LayoutMode} to use for this XComposite.
	 */
	public AbstractListComposite(Composite parent, int widgetStyle, String caption, boolean createGUI, 
			ILabelProvider labelProvider,	LayoutMode layoutMode) 
	{
		this(parent, widgetStyle, caption, createGUI, labelProvider, layoutMode, LayoutDataMode.GRID_DATA);
	}
	
	/**
	 * Creates a fully configurable ListComposite. If a subclass defines own members (declares and defines)
	 * or initialises own members in its own constructors, then this subclass needs to set 
	 * <code>createGUI = false</code>, otherwise uninitialised members might be accessed in 
	 * {@link #createGuiControl(Composite, int, String)}!  
	 * 
	 * @param parent the Composite to place this one in.
	 * @param widgetStyle the style of the widget which displays the managed List of elements.
	 * @param caption the String to use as a caption, if none given no caption is created.
	 * @param createGUI boolean to indicate whether this class should call 
	 * 		{@link #createGuiControl(Composite, int, String)} or the subclass will in its own constructors.
	 * @param labelProvider the {@link ILabelProvider}, which will generate the text as well as image 
	 * 		labels for the stored elements.
	 * @param layoutMode the {@link LayoutMode} to use for this XComposite.
	 * @param layoutDataMode the {@link LayoutDataMode} to use for this XComposite.
	 */
	public AbstractListComposite(Composite parent, int widgetStyle, String caption, boolean createGUI, 
			ILabelProvider labelProvider,	LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		this(parent, widgetStyle, caption, createGUI, labelProvider, layoutMode, layoutDataMode, SWT.NONE);
	}				

	/**
	 * Creates a fully configurable ListComposite. If a subclass defines own members (declares and defines)
	 * or initialises own members in its own constructors, then this subclass needs to set 
	 * <code>createGUI = false</code>, otherwise uninitialised members might be accessed in 
	 * {@link #createGuiControl(Composite, int, String)}!  
	 * 
	 * @param parent the Composite to place this one in.
	 * @param widgetStyle the style of the widget which displays the managed List of elements.
	 * @param caption the String to use as a caption, if none given no caption is created.
	 * @param createGUI boolean to indicate whether this class should call 
	 * 		{@link #createGuiControl(Composite, int, String)} or the subclass will in its own constructors.
	 * @param labelProvider the {@link ILabelProvider}, which will generate the text as well as image 
	 * 		labels for the stored elements.
	 * @param layoutMode the {@link LayoutMode} to use for this XComposite.
	 * @param layoutDataMode the {@link LayoutDataMode} to use for this XComposite.
	 * @param compositeStyle the style of this composite.
	 */
	public AbstractListComposite(Composite parent, int widgetStyle, String caption, boolean createGUI, 
			ILabelProvider labelProvider, LayoutMode layoutMode, LayoutDataMode layoutDataMode, int compositeStyle)
	{
		super(parent, compositeStyle, layoutMode, layoutDataMode);
		
		if (labelProvider == null)
			throw new IllegalArgumentException("labelProvider must not be null!"); //$NON-NLS-1$

		this.labelProvider = labelProvider;

		elements = new LinkedList<T>();

		if (createGUI)
			createGuiControl(this, widgetStyle, caption);
	}

	public void setLabelProvider(ILabelProvider labelProvider)
	{
		this.labelProvider = labelProvider;
	}

	/**
	 * Subclasses should create the GUI and register an SelectionListener in the widget, which calls
	 * {@link #fireSelectionChangedEvent(IStructuredSelection)} by implementing this method. <br> 
	 * 
	 * <p><b>Important</b>:<br>
	 * 	Ensure that a SelectionListener is registered within the widget and that it calls
	 * 	{@link #fireSelectionChangedEvent(IStructuredSelection)}! 
	 * </p>
	 *  
	 * @param widgetStyle the style with which to create the widget representing the List of elements.
	 * @param caption the text used as a caption if it is not <code>null</code>.
	 */
	protected abstract void createGuiControl(Composite parent, int widgetStyle, String caption);

  /**
   * Returns the primary control associated with this viewer.
   *
   * @return the SWT control which displays this viewer's content
   */
	public abstract Control getControl();
	
	/**
	 * This is convenience method to retrieve the default widget style.
	 * <!--
	 * Tobias: commented the following remark, static methods cannot be overwritten anyway
	 * 
	 * It is not intended to be overridden by subclasses!
	 * -->
	 * 
	 * @param comp some composite 
	 * @return SWT.READ_ONLY | XComposite.getBorderStyle(comp)
	 */
	public static int getDefaultWidgetStyle(Composite comp) {
		return SWT.READ_ONLY | XComposite.getBorderStyle(comp);
	}
	
	/**
	 * Populates the graphical list with the elements provided and labels them using the label provider.
	 */
	protected void populateList()
	{
		int index = 0;
		for (T elem : elements)
			addElementToGui(index++, elem);
	}

	/**
	 * The given element is added to the graphical representation of this list.
	 * @param index the index at which to insert the given <code>element</code>.
	 * @param element The element to be added.
	 */
	protected abstract void addElementToGui(int index, T element);

	/**
	 * The given element is removed from the graphical representation of this list.
	 * @param index The element to be removed.
	 */
	protected abstract void removeElementFromGui(int index);

	/**
	 * All elements are removed from the graphical representation of this list.
	 */
	protected abstract void removeAllElementsFromGui();

	/**
	 * Returns the index of the element currently selected in the graphical representation of this list.
	 */
	protected abstract int internal_getSelectionIndex();

	protected abstract int[] internal_getSelectionIndices();

	/**
	 * Selects the element with the given index in the graphical representation of this list. If 
	 * <code>index == -1</code> deselect all previously selected elements.
	 */
	protected abstract void internal_setSelection(int index);

	protected abstract void internal_setSelection(int[] indices);

	protected abstract int internal_getSelectionCount();

	/**
	 * Returns the index of the currently selected element.
	 * @return the index of the currently selected element.
	 */
	public int getSelectionIndex()
	{
		return internal_getSelectionIndex();
	}

	/**
	 * Returns an array of indices of all selected elements. 
	 * @return an array of indices of all selected elements.
	 */
	public int[] getSelectionIndices()
	{
		return internal_getSelectionIndices();
	}

	/**
	 * Returns the number of selected elements.
	 * @return the number of selected elements.
	 */
	public int getSelectionCount()
	{
		return internal_getSelectionCount();
	}

	/**
	 * Sets the selection to the element with the given index. If no such index exists, it does nothing.
	 * @param index the index of an element, which shall be selected.
	 */
	public void setSelection(int index)
	{
		internal_setSelection(index);
	}
	
	/**
	 * Selects all elements corresponding to the given array of indices.
	 * @param indices an array with all indices of the elements that shall be selected.
	 */
	public void setSelection(int[] indices)
	{
		internal_setSelection(indices);
	}

	/**
	 * Refreshes the specified element and updates its label.
	 * @param The element to be refreshed.
	 */
	protected abstract void refreshElement(T elem);	
	
	/**
	 * Adds the specified element to the end of the list.
	 * @param element The element to be added.
	 */
	public void addElement(T element)
	{
		elements.add(element);
		addElementToGui(elements.size() - 1, element);
	}

	public void addElement(int index, T element)
	{
		elements.add(index, element);
		addElementToGui(index, element);
	}
	
	/**
	 * Removes the specified element from the list.
	 * @param element The element to be removed.
	 */
	public int removeElement(T element)
	{
		int index = elements.indexOf(element);
		removeElementFromGui(index);
		elements.remove(index);
		return index;
	}

	/**
	 * Removes all elements from the list.
	 */
	public void removeAll()
	{
		removeAllElementsFromGui();
		elements.clear();
	}
	
	/**
	 * Adds all specified elements to the list.
	 * @param elements The elements to be added.
	 */
	public void addElements(Collection<T> elements)
	{
		for (T elem : elements)
			addElement(elem);
	}
	
	/**
	 * Resets the list to the specified elements.
	 * @param elements The elements to be set.
	 */
	public void setInput(Collection<T> elements)
	{
		if (elements == null)
			elements = Collections.EMPTY_LIST;
		
		removeAll();
		addElements(elements);
	}
	
	/**
	 * Returns the object that is currently selected in the list.
	 * @return the currently selected object.
	 */
	public T getSelectedElement()
	{		
		int selIndex = internal_getSelectionIndex();
		if (selIndex != -1)
			return elements.get(selIndex);
		else
			return null;
	}

	/**
	 * Returns a {@link List} of all selected Elements;
	 * @return a {@link List} of all selected Elements;
	 */
	public List<T> getSelectedElements()
	{
		ArrayList<T> selectedElements = new ArrayList<T>(internal_getSelectionCount());
		int[] selIndices = internal_getSelectionIndices();
		for (int i = 0; i < selIndices.length; ++i) {
			selectedElements.add(elements.get(selIndices[i]));
		}
		return selectedElements;
	}

	/**
	 * Returns the index of the given element.
	 * @param element The element whose index is to be returned.
	 * @return The index of the element.
	 */
	protected int getElementIndex(T element)
	{
		return elements.indexOf(element);
	}

	/**
	 * Returns the current list of elements.
	 * @return The current list of elements.
	 */
	public List<T> getElements() {
		return elements;
	}
	
	/**
	 * Returns the <code>label</code>'s text if any has been created, <code>null</code> otherwise.
	 * @return the <code>label</code>'s text if any has been created, <code>null</code> otherwise.
	 */
	public String getTitle() {
		return label == null ? null : label.getText(); 
	}

	/**
	 * Sets the Caption of the this List if a label has been created.
	 * @param text the text to set as Caption. If it is <code>null</code> the caption will be cleared.
	 */
	public void setCaption(String text) {
		if (label == null)
			return;
		
		if (text == null)
			text = ""; //$NON-NLS-1$
		
		label.setText(text);
	}
	
	/**
	 * Checks whether the given element is within the current element list
	 * 
	 * @param element The element to check
	 * @return Whether or not the given element is within the current element list
	 */
	public boolean contains(T element) {
		return elements.contains(element);
	}

	public boolean selectElementByIndex(int index) {
		if (index < -1)
			return false;
		
		internal_setSelection(index);
		return true;
	}

	/**
	 * Selects the given element in the list.
	 * 
	 * @param element The element to be selected.
	 * @return <code>True</code> if the element to be selected was in the list and <code>false</code>
	 *  if not. If <code>element</code> is <code>null</code>, this method always returns 
	 *  <code>true</code> and deselects	all previously selected elements.
	 */
	public boolean selectElement(T element)
	{
		int index = elements.indexOf(element);
		return selectElementByIndex(index);
	}
	
	/**
	 * Refreshes the list.
	 */
	protected void reset()
	{
		removeAllElementsFromGui();
		populateList();
	}
	
	/**
	 * <p>
	 * Removes the currently selected element in the list and returns the removed element.
	 * If there is no element selected, this method does nothing and returns null. If
	 * there are multiple elements selected, this method removes and returns the first
	 * element.
	 * </p>
	 * <p>
	 * If there is, after the element has been removed, no selection existing, the element
	 * which is now at the selection-index will be selected. If the selection-index became
	 * out-of-range, the last element will be selected.
	 * </p>
	 */
	public T removeSelected()
	{
		int index = internal_getSelectionIndex();
		if (index < 0)
			return null;

		T toReturn = getSelectedElement();
		elements.remove(index);
		removeElementFromGui(index);
		if (internal_getSelectionIndex() < 0)
			internal_setSelection(Math.min(index, elements.size()-1));

		return toReturn;
	}

	/**
	 * In contrast to {@link #removeSelected()}, this method does not select any other element,
	 * after all previously selected elements have been removed.
	 */
	public List<T> removeAllSelected()
	{
		int[] selectionIndices = internal_getSelectionIndices();
		Arrays.sort(selectionIndices);
		ArrayList<T> res = new ArrayList<T>(selectionIndices.length);
		for (int i = selectionIndices.length -1; i >= 0; --i) {
			res.add(elements.remove(selectionIndices[i]));
			removeElementFromGui(selectionIndices[i]);
		}
		return res;
	}
	
	public void refresh()
	{
		for (T elem : elements)
			refreshElement(elem);
	}

	/**
	 * This method has to be called by the subclass when a {@link SelectionChangedEvent} happened 
	 * in the widget. The Listener to trigger this method should be registered in 
	 * {@link #createGuiControl(Composite, int, String)}.
	 * 
	 * @param selection the selection containing all selected elements. 
	 */
	protected void fireSelectionChangedEvent(IStructuredSelection selection)
	{
		selectionProvider.setSelection(selection);
	}

//	/**
//	 * All subclasses have to register an SelectionChangedListener in the widget and call 
//	 * {@link #fireSelectionChangedEvent()} when they get notified of selection changes in the widget.
//	 */
//	abstract protected void registerSelectionListenerInWidget(); 
//	
	/**
	 * SelectionProvider used to notify {@link ISelectionChangedListener}. 
	 */
	private SelectionProvider selectionProvider = new SelectionProvider();

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionProvider.addSelectionChangedListener(listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionProvider.removeSelectionChangedListener(listener);
	}

	/**
	 * @return an instance of {@link IStructuredSelection} containing all selected elements.
	 *
	 * @see ISelectionProvider#getSelection()
	 */
	public ISelection getSelection()
	{
		return new StructuredSelection(getSelectedElements());
	}

	/**
	 * @param selection This must be an instance of {@link IStructuredSelection} containing the elements
	 *		you want to select (all others will be deselected. If the concrete implementation of {@link AbstractListComposite}
	 *		does not support multi-selections, the first element of <code>selection</code> will be selected.
	 *
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	public void setSelection(ISelection selection)
	{
		IStructuredSelection sel = (IStructuredSelection) selection;
		LinkedList<Integer> selectionIndices = new LinkedList<Integer>();
		for (Iterator it = sel.iterator(); it.hasNext(); ) {
			Object element = it.next();
			int index = elements.indexOf(element);
			if (index < 0)
				throw new IllegalArgumentException("The object in the selection is not known in this list: " + element); //$NON-NLS-1$

			selectionIndices.add(index);
		}

		int[] si = new int[selectionIndices.size()]; int i = 0;
		for (Integer selIdx : selectionIndices) {
			si[i++] = selIdx.intValue();
		}

		internal_setSelection(si);
	}
	
	/**
	 * Selects the given element.
	 * @param element The element to be selected.
	 */
	public void setSelection(final T element) {
		IStructuredSelection sel = new StructuredSelection(element);
		
		setSelection(sel);
	}
}