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

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import org.nightlabs.base.NLBasePlugin;

/**
 * An ExpandableComposite with an ExpansionListener that
 * re-layouts the parent composite on expansion state 
 * changes. ExpandableWrapperComposite stores its expansion
 * state to the Eclipse PreferenceStore.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ExpandableWrapperComposite extends ExpandableComposite {

	private static final String EXPANDED_SUFFIX = ".expanded"; //$NON-NLS-1$
	
	private IExpansionListener expansionListener = new IExpansionListener(){
		public void expansionStateChanging(ExpansionEvent e) {
		}
		public void expansionStateChanged(ExpansionEvent e) {
			getParent().layout(true);
			getParent().redraw();
		}
	};
	
	public ExpandableWrapperComposite(Composite parent, int style) {
		this(parent, style, ExpandableComposite.TWISTIE);
		setExpanded(restoreExpansionState());
	}

	public ExpandableWrapperComposite(Composite parent, int style, int expansionStyle) {
		super(parent, style, expansionStyle);
		setBackground(parent.getBackground());
//	setForeground(parent.getForeground());
		addExpansionListener();
		setExpanded(restoreExpansionState());
	}
	
	public ExpandableWrapperComposite(Composite parent, int style, int expansionStyle, String identifier) {
		super(parent, style, expansionStyle);
		setBackground(parent.getBackground());
//	setForeground(parent.getForeground());
		addExpansionListener();
		this.identifier = identifier;
		setExpanded(restoreExpansionState());
		addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				storeExpansionState();
			}
		});
	}
	
	public void addExpansionListener() {
		addExpansionListener(expansionListener);
	}

	public void removeExpansionListener() {
		removeExpansionListener(expansionListener);
	}
	
	private String identifier;
	private boolean restoreExpansionState() {
		if (identifier == null)
			return false;
		return NLBasePlugin.getDefault().getPreferenceStore().getBoolean(identifier+EXPANDED_SUFFIX);
	}
	
	private void storeExpansionState() {
		if (identifier == null)
			return;
		NLBasePlugin.getDefault().getPreferenceStore().setValue(identifier+EXPANDED_SUFFIX, isExpanded());
	}
}
