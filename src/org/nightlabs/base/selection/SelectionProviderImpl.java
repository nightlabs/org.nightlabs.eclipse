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

package org.nightlabs.base.selection;


/**
 * @author Daniel Mazurek - daniel dot mazurek at nightlabs dot de
 * @deprecated Use {@link SelectionProvider} instead!
 */
public class SelectionProviderImpl
extends SelectionProvider
{
}
//implements ISelectionProvider
//{
//
//	public SelectionProviderImpl() {
//		super();
//	}
//
//	private ListenerList selectionListeners = new ListenerList();
//	
////	/**
////	 * 
////	 * @return a Collection which contains all added selectionChangedListeners
////	 */
////	protected Collection getSelectionListeners() 
////	{
////		return selectionListeners;
////	}
////	
//	/**
//	 * fires a SelectionChangedEvent to all added SelectionChangedListeners
//	 *
//	 */
//	protected void fireSelectionChanged() 
//	{
//		Object[] listeners = selectionListeners.getListeners();
//		for (Object listener : listeners) {
//			ISelectionChangedListener l = (ISelectionChangedListener) listener;
//			l.selectionChanged(new SelectionChangedEvent(this, getSelection()));
//		}
//	}
//
//	/**
//	 * 
//	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
//	 */
//	public void addSelectionChangedListener(ISelectionChangedListener listener) {
//		selectionListeners.add(listener);
//	}
//
//	/**
//	 * 
//	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
//	 */
//	public ISelection getSelection() {
//		return getSelectedObjects();
//	}
//
//	/**
//	 * 
//	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
//	 */
//	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
//		selectionListeners.remove(listener);
//	}
//
//	/**
//	 * only accepts StructuredSelections
//	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
//	 */
//	public void setSelection(ISelection newSelection) 
//	{
//		if (newSelection instanceof StructuredSelection) {
//			selection = (StructuredSelection) newSelection;
//			selectedObjects = selection.toList();
//			fireSelectionChanged();
//		}		
//	}
//
//	protected StructuredSelection selection;
//	private List selectedObjects;
//	
//	protected List getSelectedObjectsList() 
//	{
//		if (selectedObjects == null)
//			selectedObjects = new ArrayList();
//
//		return selectedObjects;
//	}
//	
//	public StructuredSelection getSelectedObjects() 
//	{
//		if (selectedObjects == null || selectedObjects.isEmpty())
//			return StructuredSelection.EMPTY;
//		
//		return new StructuredSelection(selectedObjects);
//	}
//}
