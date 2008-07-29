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

package org.nightlabs.base.ui.composite.groupedcontent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;

/**
 * A {@link GroupedContentComposite} consists of a table ({@link GroupedContentSwitcherTable}) on the left
 * that shows an entry for each {@link GroupedContentProvider} added to this Composite and a dynamic area 
 * on the right that shows the content of the currently selected {@link GroupedContentProvider}. 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class GroupedContentComposite extends XComposite {
	
	private XComposite tableWrapper;
	private GroupedContentSwitcherTable switcherTable;
	
	private XComposite contentWrapper;
	private StackLayout contentStackLayout;
	
	private List<GroupedContentProvider> groupedContentProviders = new ArrayList<GroupedContentProvider>();
	private Map<GroupedContentProvider, Composite> providerComposites = new HashMap<GroupedContentProvider, Composite>();
	
	private ISelectionChangedListener switcherListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection)switcherTable.getSelection();
			if (selection.size() != 1)
				return;
			GroupedContentProvider contentProvider = (GroupedContentProvider)selection.getFirstElement();
			selectContentProvider(contentProvider);
		}
	};
	
	/**
	 * Create a new {@link GroupedContentComposite}.
	 * 
	 * @param parent The parent for the new {@link GroupedContentComposite}.
	 * @param style The style to apply to the Composite.
	 * @param setLayoutData Whether to set a {@link GridData} that will fill in both directions 
	 *                      for the newly created {@link GroupedContentComposite}. 
	 */
	public GroupedContentComposite(Composite parent, int style, boolean setLayoutData) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER,
				setLayoutData ? LayoutDataMode.GRID_DATA : LayoutDataMode.NONE);
		getGridLayout().numColumns = 2;

		tableWrapper = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		GridData tableGD = new GridData(GridData.FILL_BOTH);
		tableGD.grabExcessHorizontalSpace = false;
		tableGD.widthHint = 150;
		tableWrapper.setLayoutData(tableGD);

		switcherTable = new GroupedContentSwitcherTable(tableWrapper, SWT.NONE);
		switcherTable.addSelectionChangedListener(switcherListener);
		
		contentWrapper = new XComposite(this, SWT.NONE);
		contentWrapper.setLayoutData(new GridData(GridData.FILL_BOTH));
		contentStackLayout = new StackLayout();
		contentWrapper.setLayout(contentStackLayout);
	}

	/**
	 * Add the given {@link GroupedContentProvider} to the list of providers of this {@link GroupedContentComposite}.
	 * The provider will be added to the end of the list.
	 * 
	 * @param groupedContentProvider The provider to add.
	 */
	public void addGroupedContentProvider(GroupedContentProvider groupedContentProvider) {
		this.groupedContentProviders.add(groupedContentProvider);
		switcherTable.setInput(this.groupedContentProviders);
		preSelect();
//		layout(true, true);
	}

	/**
	 * Add the given {@link GroupedContentProvider} to the list of providers of this {@link GroupedContentComposite}.
	 * The provider will be added at the given index, but index must be valid for the actual list of providers, otherwise
	 * an {@link IndexOutOfBoundsException} will be thrown.
	 * 
	 * @param groupedContentProvider The provider to add.
	 * @param index The index at which the given provider should be added.
	 */
	public void addGroupedContentProvider(GroupedContentProvider groupedContentProvider, int index) {
		this.groupedContentProviders.add(index, groupedContentProvider);
		switcherTable.setInput(this.groupedContentProviders);
		preSelect();
		layout(true, true);
	}

	protected void selectContentProvider(GroupedContentProvider contentProvider) {
		Composite providerComp = providerComposites.get(contentProvider);
		if (providerComp == null) {
			providerComp = contentProvider.createGroupContent(contentWrapper);
			providerComposites.put(contentProvider, providerComp);
		}
		contentStackLayout.topControl = providerComp;
		contentWrapper.layout(true, true); // TODO true, true necessary?
	}
	
	/**
	 * Sets the title for this {@link GroupedContentComposite}.
	 * @param title The title to set.
	 */
	public void setGroupTitle(String title) {
		switcherTable.setGroupTitle(title);
	}
	
	private void preSelect() {
		if (switcherTable.getItemCount() > 0 && switcherTable.getSelectionCount() == 0) {
			switcherTable.select(0);
			selectContentProvider(groupedContentProviders.get(0));
		}
	}
}
