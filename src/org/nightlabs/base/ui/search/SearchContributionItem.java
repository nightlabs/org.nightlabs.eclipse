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
package org.nightlabs.base.ui.search;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.nightlabs.base.ui.NLBasePlugin;
import org.nightlabs.base.ui.action.XContributionItem;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SearchContributionItem
//extends AbstractSearchContributionItem
extends XContributionItem
{
	private static final Logger logger = Logger.getLogger(SearchContributionItem.class);
	
	private ISearchResultProviderFactory selectedFactory = null;
	private Map<MenuItem, ISearchResultProviderFactory> menuItem2Factory = new HashMap<MenuItem, ISearchResultProviderFactory>();	
	private Text searchText = null;
	private ToolItem selectedItem = null;
	
	public SearchContributionItem() {
		super(SearchContributionItem.class.getName());
	}

	protected Control createText(Composite parent)
	{
		searchText = new Text(parent, SWT.BORDER);
		searchText.addSelectionListener(buttonSelectionListener);
		return searchText;
	}

	private SelectionListener buttonSelectionListener = new SelectionListener()
	{
		public void widgetSelected(SelectionEvent e) {
			searchPressed();
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
	
	private ToolItem createSearchItem(final ToolBar toolBar, final Menu menu) {
		ToolItem searchItem = new ToolItem(toolBar, SWT.DROP_DOWN);
		searchItem.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(),
				SearchContributionItem.class));
		searchItem.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event)
			{
				logger.info("event.detail = "+event.detail); //$NON-NLS-1$
				if (event.detail == SWT.ARROW) {
						Rectangle rect = getSelectedItem().getBounds();
						Point p = new Point(rect.x, rect.y + rect.height);
						p = toolBar.toDisplay(p);
						menu.setLocation(p.x, p.y);
						menu.setVisible(true);						
				}
				if (event.detail == SWT.NONE) {
					searchPressed();
				}
			}
		});
		searchItem.setImage(getSelectedFactory().getComposedDecoratorImage());
		return searchItem;
	}
	
	@Override
	public void fill(ToolBar parent, int index)
	{
//		fillToolBar(parent, index);
		IContributionManager toolBarManager = getParent();

		String id1 = ToolBarContributionItemText.class.getName();
		toolBarManager.remove(id1);
		toolBarManager.add(new ToolBarContributionItemText(id1));

		String id2 = ToolBarContributionItemButton.class.getName();		
		toolBarManager.remove(id2);
		toolBarManager.add(new ToolBarContributionItemButton(id2));
	}
	
	private void fillToolBar(ToolBar parent, int index)
	{
		final ToolBar toolBar = parent;

		final ToolItem toolItem = new ToolItem(toolBar, SWT.SEPARATOR);
		toolItem.setControl(createText(toolBar));
		toolItem.setWidth(100);
//		toolItem.setData(new SubContributionItem(this));
		
		final Menu menu = createMenu(new Menu(RCPUtil.getActiveShell(), SWT.POP_UP));		
		setSelectedItem(createSearchItem(toolBar, menu));
//		selectedItem.setData(new SubContributionItem(this));
		getSelectedItem().addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent e) {
				logger.debug("selectedItem DISPOSE!!!"); //$NON-NLS-1$
			}
		});		

		toolBar.pack();
	}

	@Override
	public void fill(CoolBar parent, int index)
	{
		final CoolBar coolBar = parent;
		ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP);

		fillToolBar(toolBar, index);

		CoolItem coolItem = new CoolItem(coolBar, SWT.SEPARATOR);
		coolItem.setControl(toolBar);

//		// FIXME: set size for contributionItem leads to strange layout problems when resetting perspective
		Point size = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point coolSize = coolItem.computeSize(size.x, size.y);
		int diffX = 0;
		coolItem.setSize(coolSize.x - diffX, coolSize.y);
		coolItem.setMinimumSize(coolSize.x - diffX, coolSize.y);
		coolItem.setPreferredSize(coolSize.x - diffX, coolSize.y);
//		toolBar.layout(true, true);
//		coolBar.layout(true, true);
		
		coolBar.pack();
	}
	
	class ToolBarContributionItemText extends ContributionItem
	{				
		public ToolBarContributionItemText(String id) {
			super(id);
		}

		@Override
		public void fill(ToolBar parent, int index) {
			final ToolItem toolItem = new ToolItem(parent, SWT.SEPARATOR);
			toolItem.setControl(createText(parent));
			toolItem.setWidth(100);
		}
	}
	
	class ToolBarContributionItemButton extends ContributionItem
	{
		public ToolBarContributionItemButton(String id) {
			super(id);
		}

		@Override
		public void fill(ToolBar parent, int index) {
			final Menu menu = createMenu(new Menu(RCPUtil.getActiveShell(), SWT.POP_UP));		
			setSelectedItem(createSearchItem(parent, menu));
			getSelectedItem().addDisposeListener(new DisposeListener(){
				@Override
				public void widgetDisposed(DisposeEvent e) {
					logger.debug("selectedItem DISPOSE!!!"); //$NON-NLS-1$
				}
			});
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.search.AbstractSearchContributionItem#getSearchText()
	 */
	protected String getSearchText() 
	{
		if (searchText != null && !searchText.isDisposed())
			return searchText.getText();
		
		return null;
	}

	@Override
	public void fill(Menu menu, int index)
	{ 
		String id = SearchContributionItem.class.getName();
		getParent().remove(id);
		IMenuManager menuManager = new MenuManager(Messages.getString("org.nightlabs.base.ui.search.SearchContributionItem.menu.search.title"), id); //$NON-NLS-1$
		
		Map<ISearchResultProviderFactory, ISearchResultProvider> factory2Instance = getUseCase().getFactory2Instance();
		for (Map.Entry<ISearchResultProviderFactory, ISearchResultProvider> entry : factory2Instance.entrySet()) {
			ISearchResultProviderFactory factory = entry.getKey();
			MenuContributionItem item = new MenuContributionItem(factory);
			menuManager.add(item);
		}
		
		if (getParent() != null)
			getParent().add(menuManager);
		
//		createMenu(menu);
	}
	
	class MenuContributionItem extends ContributionItem
	{
		private ISearchResultProviderFactory factory;
		public MenuContributionItem(ISearchResultProviderFactory factory) {
			this.factory = factory;
			setId(factory.getClass().getName());
		}

		@Override
		public void fill(Menu menu, int index) {
			createMenuItem(menu, factory);
		}
	}	
	
	private SelectionListener menuSelectionListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) 
		{
			selectedFactory = menuItem2Factory.get((MenuItem) e.getSource());
			if (selectedItem != null && !selectedItem.isDisposed())
				selectedItem.setImage(getSelectedFactory().getComposedDecoratorImage());
			searchPressed();
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};

	protected MenuItem createMenuItem(Menu menu, ISearchResultProviderFactory factory) {
		MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText(factory.getName().getText());
		menuItem.setImage(factory.getImage());
		menuItem2Factory.put(menuItem, factory);
		menuItem.addSelectionListener(menuSelectionListener);
		menuItem.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent e) {
				MenuItem menuItem = (MenuItem) e.getSource();
				menuItem2Factory.remove(menuItem);
			}
		});
				
		return menuItem;
	}

	protected ISearchResultProviderFactory getSelectedFactory() {
		if (selectedFactory == null) {
			selectedFactory = getUseCase().getCurrentSearchResultProviderFactory();
		}
		return selectedFactory;
	}

	protected SearchResultProviderRegistryUseCase getUseCase() {
		SearchResultProviderRegistryUseCase useCase = SearchResultProviderRegistry.sharedInstance().getUseCase(getUseCaseKey());
		if (useCase == null) {
			useCase = new SearchResultProviderRegistryUseCase();
			ISearchResultProviderFactory factory = useCase.getFactory2Instance().keySet().iterator().next();
			useCase.setCurrentSearchResultProviderFactory(factory);
		}
		return useCase;
	}

	protected void updateUseCase()
	{
		SearchResultProviderRegistryUseCase useCase = getUseCase();
		if (getSearchText() != null) {
			useCase.setCurrentSearchText(getSearchText());
		}
		useCase.setCurrentSearchResultProviderFactory(getSelectedFactory());
	}

	protected String getUseCaseKey() {
		return SearchContributionItem.class.getName() + RCPUtil.getActivePerspectiveID();
	}	
	
	protected void searchPressed()
	{
		if (getSelectedFactory() != null) {
			ISearchResultProvider searchResultProvider = getUseCase().getFactory2Instance().get(getSelectedFactory());
			updateUseCase();
			if (getSearchText() != null)
				searchResultProvider.setSearchText(getSearchText());
			ISearchResultActionHandler actionHandler = getSelectedFactory().getActionHandler();
			if (actionHandler != null) {
				actionHandler.setSearchResultProvider(searchResultProvider);
				actionHandler.run();
			}
		}
	}
	
	protected Menu createMenu(Menu menu)
	{
		Map<ISearchResultProviderFactory, ISearchResultProvider> factory2Instance = getUseCase().getFactory2Instance();
		for (Map.Entry<ISearchResultProviderFactory, ISearchResultProvider> entry : factory2Instance.entrySet()) {
			ISearchResultProviderFactory factory = entry.getKey();
			createMenuItem(menu, factory);
		}
		return menu;
	}

	/**
	 * Return the selectedItem.
	 * @return the selectedItem
	 */
	protected ToolItem getSelectedItem() {
		return selectedItem;
	}

	/**
	 * Sets the selectedItem.
	 * @param selectedItem the selectedItem to set
	 */
	protected void setSelectedItem(ToolItem selectedItem) {
		this.selectedItem = selectedItem;
	}	
}
