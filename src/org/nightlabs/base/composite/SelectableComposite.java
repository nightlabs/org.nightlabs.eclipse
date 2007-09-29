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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class SelectableComposite extends XComposite {

	private boolean selected = false;
	private Color normalBGColor;
	private Color selectedBGColor;
	
	private MouseListener mouseListener = new MouseAdapter() 
	{
		public void mouseDoubleClick(MouseEvent evt) {
			setSelected(true,evt.stateMask);
		}

		public void mouseUp(MouseEvent evt) {
			setSelected(true,evt.stateMask);
		}
	};
	
	Listener shellMouseListener = new Listener() {
		public void handleEvent (Event event) {
			switch (event.type) {
				case SWT.MouseDown:
					break;
				case SWT.MouseMove:
					break;
				case SWT.MouseUp:
					System.out.println("mouse up with x"+event.x+", y"+event.y); //$NON-NLS-1$ //$NON-NLS-2$
					Rectangle rect = getBounds ();
					if (rect.contains (event.x, event.y)) {
						setSelected(true);
					}
					break;
			}
		}
	};	
		
	/**
	 * @param parent
	 * @param style
	 */
	public SelectableComposite(Composite parent, int style) {
		super(parent, style);
		normalBGColor = this.getBackground();
		selectedBGColor = new Color(Display.getDefault(),new RGB(0,0,255));
//		addMouseListener(mouseListener);
//		Button button = new Button(parent,style);
//		button.addSelectionListener()
//		Display.getDefault().getShells()[0].addListener(SWT.MouseUp, shellMouseListener);
		Shell[] shs = Display.getDefault().getShells();
		final Shell shell = shs[shs.length-1];
		shell.addListener(SWT.MouseUp, shellMouseListener);
		// Marco: I think we need to remove the listener on dispose. Added the listener on 2007-03-27. Is this correct?
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				if (!shell.isDisposed())
					shell.removeListener(SWT.MouseUp, shellMouseListener);
			}
		});
	}
	

	/**
	 * Checks if this Composite is selected or not.
	 * 
	 * @return <code>true</code> if this Composite is selected - <code>false</code> otherwise
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * Sets the selection state of this composite
	 * to the passed value and sets its 
	 * Backgroundcolor accordingly.
	 * 
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		notifySelectionListeners();
	}
	
	/**
	 * Sets the selection state of this composite
	 * to the passed value and sets its 
	 * Backgroundcolor accordingly.
	 * 
	 * @param selected
	 * @param stateMask
	 */
	public void setSelected(boolean selected, int stateMask) {
		this.selected = selected;
		notifySelectionListeners(stateMask);
	}

	/**
	 * Switches the selection state.
	 */
	protected void toggleSelected(){
		setSelected(!isSelected());
	}
	
	
	/**
	 * A package visible int to
	 * store the list idx of this 
	 * Composite within a CompositeList
	 * eg. {@link HorizontalMultiColumnCompositeList}.
	 */
	int compositeListIdx = -1;	
	/**
	 * Returns the compositeListIdx.
	 * This is and should only be used in connection
	 * with a {@link HorizontalMultiColumnCompositeList}.
	 */
	public int getCompositeListIdx() {
		return compositeListIdx;
	}
	
	private Set<SelectableCompositeListener> selectionListeners = new HashSet<SelectableCompositeListener>();
	
	protected void notifySelectionListeners() {
		notifySelectionListeners(0);
	}
	
	/**
	 * Nofify all selectionChangeListener
	 */
	protected void notifySelectionListeners(int stateMask) {
		CompositeSelectionEvent evt = new CompositeSelectionEvent();
		evt.setSource(this);
		evt.setSelected(isSelected());
		evt.setStateMask(stateMask);
		for (Iterator iter = selectionListeners.iterator(); iter.hasNext();) {
			SelectableCompositeListener listener = (SelectableCompositeListener) iter.next();			
			listener.selectionStateChanged(evt);
		}
	}
	
	private Object selectionObject;
	
	
	public Object getSelectionObject() {
		return selectionObject;
	}
	public void setSelectionObject(Object selectionObject) {
		this.selectionObject = selectionObject;
	}
	
	public void removeSelectionListener(SelectionListener listener) {
//		if (eventTable == null) return;
//		eventTable.unhook (SWT.Selection, listener);
//		eventTable.unhook (SWT.DefaultSelection,listener);	
	}
	
	public void addSelectionListener(SelectionListener listener) {
//		TypedListener typedListener = new TypedListener (listener);
//		addListener (SWT.Selection,typedListener);
//		addListener (SWT.DefaultSelection,typedListener);
	}
	
	/**
	 * Add a listener to this selectable Composite.
	 * @param listener
	 */
	public void addSelectionChangeListener(SelectableCompositeListener listener) {
		selectionListeners.add(listener);
	}
	/**
	 * Remove a listener to this this selectable Composite.
	 * @param selectionListener
	 */
	public void removeSelectionChangeListener(SelectableCompositeListener selectionListener) {
		selectionListeners.remove(selectionListener);
	}
	
}
