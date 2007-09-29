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
package org.nightlabs.base.search;

import java.util.Map;

import org.apache.log4j.Logger;
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
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.action.AbstractContributionItem;
import org.nightlabs.base.resource.SharedImages;
import org.nightlabs.base.util.RCPUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SearchContributionItem 
extends AbstractContributionItem 
{
	private static final Logger logger = Logger.getLogger(SearchContributionItem.class);

	private ISearchResultProviderFactory selectedFactory = null;	
	private Item selectedItem = null;
	private Text searchText = null;		
	
	public SearchContributionItem() {
		super();
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
			// TODO get the factory with the highest priority
			ISearchResultProviderFactory factory = useCase.getFactory2Instance().keySet().iterator().next();
			useCase.setCurrentSearchResultProviderFactory(factory);
		}
		return useCase;
	}
	
	protected void updateUseCase() 
	{
		SearchResultProviderRegistryUseCase useCase = getUseCase();
		if (searchText != null && !searchText.isDisposed()) {
			useCase.setCurrentSearchText(searchText.getText());
		}
		useCase.setCurrentSearchResultProviderFactory(getSelectedFactory());
	}
	
	protected String getUseCaseKey() {
		return SearchContributionItem.class.getName() + RCPUtil.getActivePerspectiveID();
	}
		
	protected void createText(Composite parent) 
	{
		searchText = new Text(parent, SWT.BORDER);
//		searchText.setText("          ");
		searchText.addSelectionListener(buttonSelectionListener);		
	}
	
//	private Button searchButton = null;	
//	private Combo searchTypeCombo;
//	
//	private void fillSearchTypeCombo() 
//	{		
//		searchTypeCombo.setItems(getSearchTypes().toArray(new String[getSearchTypes().size()]));
//		if (getSearchTypes().indexOf(selectedType) != -1)
//			searchTypeCombo.select(getSearchTypes().indexOf(selectedType));
//	}	
//	
//	private SelectionListener comboSelectionListener = new SelectionListener(){	
//		public void widgetSelected(SelectionEvent e) {
//			selectedType = searchTypes.get(searchTypeCombo.getSelectionIndex());
//		}	
//		public void widgetDefaultSelected(SelectionEvent e) {
//			widgetSelected(e);
//		}	
//	};
		
//	private String selectedType = null;
//	protected String getSelectedType() {
//		return selectedType;
//	}

//	private ISearchResultProvider selectedSearchResultProvider = null;
//	protected ISearchResultProvider getSelectedSearchResultProvider() {
//		return selectedSearchResultProvider;
//	}
	
	protected Control createControl(Composite parent) {
		return null;
	}
	
//	@Override
//	protected Control createControl(Composite parent) 
//	{
//		checkSelectedType();
//		
//		Composite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
//		GridLayout layout = new GridLayout(1, false);
//		layout.verticalSpacing = 0;
//		layout.marginHeight = 0;
//		layout.marginWidth = 0;
//		layout.marginLeft = 0;
//		layout.marginRight = 0;
//		layout.marginTop = 0;
//		layout.marginBottom = 0;
//		comp.setLayout(layout);
//	
//		createText(comp);
//		
//		searchButton = new Button(comp, SWT.BOTTOM | SWT.PUSH);
//		searchButton.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
//				SearchContributionItem.class, "", ImageDimension._12x12, ImageFormat.png));		
//		searchButton.addSelectionListener(buttonSelectionListener);
//				
//		searchTypeCombo = new Combo(comp, SWT.NONE);
//		fillSearchTypeCombo();
//		searchTypeCombo.addSelectionListener(comboSelectionListener);
//		
//		return comp;
//	}	
		
	protected void searchPressed() 
	{
		if (getSelectedFactory() != null) {
			ISearchResultProvider searchResultProvider = getUseCase().getFactory2Instance().get(getSelectedFactory());
			updateUseCase();
			searchResultProvider.setSearchText(searchText.getText());
//			Collection selectedObjects = searchResultProvider.getSelectedObjects();			
			ISearchResultActionHandler actionHandler = getSelectedFactory().getActionHandler();
			if (actionHandler != null) {
				actionHandler.setSearchResultProvider(searchResultProvider);
				actionHandler.run();
			}
		}		
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

	protected Menu createMenu(Menu menu) 
	{
//		Menu menu = new Menu(RCPUtil.getActiveWorkbenchShell(), SWT.POP_UP);
		Map<ISearchResultProviderFactory, ISearchResultProvider> factory2Instance = getUseCase().getFactory2Instance();
		for (Map.Entry<ISearchResultProviderFactory, ISearchResultProvider> entry : factory2Instance.entrySet()) {
			ISearchResultProviderFactory factory = entry.getKey();
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);			
			menuItem.setText(factory.getName().getText());
			menuItem.setImage(factory.getImage());
			menuItem.setData(factory);
			menuItem.addSelectionListener(new SelectionListener(){			
				public void widgetSelected(SelectionEvent e) {
					selectedFactory = (ISearchResultProviderFactory) ((MenuItem) e.getSource()).getData();
					selectedItem.setImage(getSelectedFactory().getComposedDecoratorImage());
					searchPressed();
				}			
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}			
			});
		}		
		return menu;
	}
	
	public void fill(ToolBar parent, int index) 
	{
		if (fillToolBar) 
		{
			parent.addDisposeListener(new DisposeListener(){
				public void widgetDisposed(DisposeEvent e) {
					dispose();
				}
			});
			
			toolItem = new ToolItem(parent, SWT.SEPARATOR, index);			
	  	createText(parent);
	  	toolItem.setControl(searchText);
	  	toolItem.setWidth(100);			
	  	
			toolBar = parent;
	  	menu = createMenu(new Menu(RCPUtil.getActiveWorkbenchShell(), SWT.POP_UP));
	  	final ToolItem searchItem = new ToolItem(parent, SWT.DROP_DOWN);
	  	searchItem.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
					SearchContributionItem.class));
	  	searchItem.addListener(SWT.Selection, new Listener(){	
	  		public void handleEvent(Event event) {
	  			if (event.detail == SWT.ARROW) {
	  				Rectangle rect = searchItem.getBounds();
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
	  	selectedItem = searchItem;
	  	selectedItem.setImage(getSelectedFactory().getComposedDecoratorImage());
	  	toolBar.layout(true, true);	 	  	
		}
	}	
		
//	@Override
//	public void fill(CoolBar parent, int index) 
//	{		
//		if (fillCoolBar) 
//		{
//			parent.addDisposeListener(new DisposeListener(){
//				public void widgetDisposed(DisposeEvent e) {
//					dispose();
//				}
//			});
//
//			final CoolBar coolBar = parent;
//			toolBar = new ToolBar(coolBar, SWT.FLAT | SWT.WRAP);
//			toolBar.addDisposeListener(new DisposeListener(){
//				public void widgetDisposed(DisposeEvent e) {
//					dispose();
//				}
//			});
//	  	menu = createMenu();
//	  	
//	  	toolItem = new ToolItem(toolBar, SWT.SEPARATOR);			
//	  	createText(toolBar);
//	  	toolItem.setControl(searchText);
//	  	toolItem.setWidth(100);
//	  	
//	  	final ToolItem searchItem = new ToolItem(toolBar, SWT.DROP_DOWN);	  	
//	  	searchItem.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
//					SearchContributionItem.class));
//	  	searchItem.addListener(SWT.Selection, new Listener(){	
//	  		public void handleEvent(Event event) 
//	  		{
//	  			logger.info("event.detail = "+event.detail);
//	  			if (event.detail == SWT.ARROW) {
//	  				Rectangle rect = searchItem.getBounds();
//	  				Point p = new Point(rect.x, rect.y + rect.height);
//	  				p = toolBar.toDisplay(p);
//	  				menu.setLocation(p.x, p.y);
//	  				menu.setVisible(true);
//	  			}
//	  			if (event.detail == SWT.NONE) {
//	  				searchPressed();
//	  			}	  			
//	  		}
//	  	});	  	
//	  	selectedItem = searchItem;
//	  	selectedItem.setImage(getSelectedFactory().getComposedDecoratorImage());
//	  	toolBar.layout(true, true);
//	  	
//	  	coolItem = new CoolItem(coolBar, SWT.SEPARATOR);
//	  	coolItem.setControl(toolBar);
////	  	Point size = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);	  	
////	  	Point coolSize = coolItem.computeSize(size.x, size.y);
////	  	coolItem.setMinimumSize(coolSize.x - 10, coolSize.y);
////	  	coolItem.setSize(coolSize.x - 10, coolSize.y);
//	  	
//	  	toolBar.layout(true, true);
//	  	coolBar.layout(true, true);
//		}
//	}

	@Override
	public void fill(CoolBar parent, int index) 
	{		
		if (fillCoolBar) 
		{
			parent.addDisposeListener(new DisposeListener(){
				public void widgetDisposed(DisposeEvent e) {
					dispose();
				}
			});

			final CoolBar coolBar = parent;
//			Composite wrapper = new XComposite(coolBar, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
//			toolBar = new ToolBar(wrapper, SWT.FLAT | SWT.WRAP);
			toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP);
			toolBar.addDisposeListener(new DisposeListener(){
				public void widgetDisposed(DisposeEvent e) {
					dispose();
				}
			});
	  	menu = createMenu(new Menu(RCPUtil.getActiveWorkbenchShell(), SWT.POP_UP));
	  	
	  	toolItem = new ToolItem(toolBar, SWT.SEPARATOR);			
	  	createText(toolBar);
	  	toolItem.setControl(searchText);
	  	toolItem.setWidth(100);
	  	
	  	final ToolItem searchItem = new ToolItem(toolBar, SWT.DROP_DOWN);	  	
	  	searchItem.setImage(SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
					SearchContributionItem.class));
	  	searchItem.addListener(SWT.Selection, new Listener(){	
	  		public void handleEvent(Event event) 
	  		{
	  			logger.info("event.detail = "+event.detail); //$NON-NLS-1$
	  			if (event.detail == SWT.ARROW) {
	  				Rectangle rect = searchItem.getBounds();
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
	  	selectedItem = searchItem;
	  	selectedItem.setImage(getSelectedFactory().getComposedDecoratorImage());
	  	toolBar.layout(true, true);
	  	
	  	coolItem = new CoolItem(coolBar, SWT.SEPARATOR);
//	  	coolItem.setControl(wrapper);
	  	coolItem.setControl(toolBar);

	  	// FIXME: set size for contributionItem leads to strange layout problems when restting perspective
	  	Point size = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);	  	
	  	Point coolSize = coolItem.computeSize(size.x, size.y);
	  	coolItem.setSize(coolSize.x - 10, coolSize.y);
	  	coolItem.setMinimumSize(coolSize.x - 10, coolSize.y);
	  	coolItem.setPreferredSize(coolSize.x - 10, coolSize.y);	  	
	  	toolBar.layout(true, true);
	  	coolBar.layout(true, true);
	  	
//	  	coolItem.setSize(SWT.DEFAULT, SWT.DEFAULT);
//	  	coolItem.setMinimumSize(SWT.DEFAULT, SWT.DEFAULT);	 
//	  	coolItem.setPreferredSize(SWT.DEFAULT, SWT.DEFAULT);	 
		}
	}
	
	@Override
	public void fill(Menu menu, int index) 
	{
//		super.fill(menu, index);
		createMenu(menu);
	}

	private CoolItem coolItem = null;
	private ToolBar toolBar = null;
	private Menu menu = null;
	
	@Override
	public void dispose() 
	{
		super.dispose();
		if (searchText != null)
			searchText.dispose();
		if (coolItem != null)
			coolItem.dispose();
		if (menu != null)
			menu.dispose();
	}
	
}
