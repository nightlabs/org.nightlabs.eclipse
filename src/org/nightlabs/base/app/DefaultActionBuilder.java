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

package org.nightlabs.base.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.internal.handlers.ShowKeyAssistHandler;
import org.nightlabs.base.action.ContributionItemSetRegistry;
import org.nightlabs.base.action.INewFileAction;
import org.nightlabs.base.action.NewFileRegistry;
import org.nightlabs.base.action.OpenFileAction;
import org.nightlabs.base.action.ReOpenFileAction;
import org.nightlabs.base.action.UpdateAction;
import org.nightlabs.base.config.RecentFileCfMod;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;

/**
 * 
 * @author Daniel Mazurek Daniel.Mazurek[AT]NightLabs[DOT]de
 * @author Alexander Bieber Alex[AT]NightLabs[DOT]de
 */
public class DefaultActionBuilder 
extends ActionBarAdvisor 
{
	private static final Logger logger = Logger.getLogger(DefaultActionBuilder.class);

	public static enum ActionBarItem 
	{
		About,
		Close,
		CloseAll,		
		Export,		
		Help,
		Import,
		Intro,
		KeyAssist,
		New,
		Open,
		Print,		
		Save,
		SaveAs,
		Properties,
		Preferences,
		Quit,
		Perspectives,
		RecentFiles,		
		Update,
		Views;
	}
	
//	public static enum ActionGroup
//	{
//		Save,
//		Print,
//		Close;
//	}
	
	protected IActionBarConfigurer configurer;
	
	// Actions
	protected Map<ActionBarItem, IWorkbenchAction> actions;
	protected Map<IContributionItem, String> groupNames;
	
	// File-Menu
	protected IMenuManager newMenu;
	protected IMenuManager recentFilesMenu;
	protected ActionFactory.IWorkbenchAction saveAction;
	protected ActionFactory.IWorkbenchAction saveAsAction;
	protected ActionFactory.IWorkbenchAction quitAction;
	protected OpenFileAction openAction;
	protected ActionFactory.IWorkbenchAction printAction;
	protected ActionFactory.IWorkbenchAction importAction;
	protected ActionFactory.IWorkbenchAction exportAction;
	protected ActionFactory.IWorkbenchAction propertiesAction;
//	private ActionFactory.IWorkbenchAction newAction;
	protected ActionFactory.IWorkbenchAction closeAction;
	protected ActionFactory.IWorkbenchAction closeAllAction;
		
	// Help-Menu
	protected ActionFactory.IWorkbenchAction introAction; 
	protected ActionFactory.IWorkbenchAction helpAction;
//	protected ActionFactory.IWorkbenchAction updateAction;
	protected IAction updateAction;
	protected ActionFactory.IWorkbenchAction aboutAction;
	
	protected ShowKeyAssistHandler keyAssistHandler;
	
	// Window-Menu
	protected IContributionItem openPerspectiveMenu;
	protected IContributionItem showViewMenu;	
	protected ActionFactory.IWorkbenchAction preferencesAction;
	
	protected Collection<ActionBarItem> menuBarItems;
	protected Collection<ActionBarItem> coolBarItems;

	public DefaultActionBuilder(IActionBarConfigurer configurer,
				Collection<ActionBarItem> showInMenuBar, Collection<ActionBarItem> showInCoolBar)	
	{
		super(configurer);
		menuBarItems = showInMenuBar == null ? new HashSet<ActionBarItem>() : showInMenuBar;
		coolBarItems = showInCoolBar == null ? new HashSet<ActionBarItem>() : showInCoolBar;		
		
		actions = new HashMap<ActionBarItem, IWorkbenchAction>();
		groupNames = new HashMap<IContributionItem, String>();
		
		if (menuBarItems.contains(ActionBarItem.RecentFiles))
			initRecentFileConfig();
	}
	
	public DefaultActionBuilder(IActionBarConfigurer configurer)
	{
		this(configurer, Arrays.asList(ActionBarItem.values()), null);
	}
		
	protected void initRecentFileConfig() 
	{
		try {
			fileHistory = (RecentFileCfMod) Config.sharedInstance().createConfigModule(RecentFileCfMod.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}				
	}
	
	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#makeActions(org.eclipse.ui.IWorkbenchWindow)
	 */
	protected void makeActions(IWorkbenchWindow window) 
	{
		if (menuBarItems.contains(ActionBarItem.KeyAssist))			
			keyAssistHandler = new ShowKeyAssistHandler();
		if (menuBarItems.contains(ActionBarItem.New))
		{			
			newMenu = new MenuManager(Messages.getString("org.nightlabs.base.app.DefaultActionBuilder.newMenu.text"), ActionFactory.NEW.getId()); //$NON-NLS-1$
//			newMenu.add((ActionFactory.NEW.create(window)));
			newMenu.add(new GroupMarker(ActionFactory.NEW.getId()));			
		}
		if (menuBarItems.contains(ActionBarItem.Open))			
			openAction = new OpenFileAction();
		if (menuBarItems.contains(ActionBarItem.RecentFiles))
		{
			if (openAction != null)
				openAction.addPropertyChangeListener(historyFileListener);
			recentFilesMenu = new MenuManager(Messages.getString("org.nightlabs.base.app.DefaultActionBuilder.recentFilesMenu.text"), NLWorkbenchActionConstants.M_RECENT_FILES); //$NON-NLS-1$
			recentFilesMenu.add(new GroupMarker(IWorkbenchActionConstants.HISTORY_GROUP));			
		}
		if (menuBarItems.contains(ActionBarItem.Close)) {
			closeAction = ActionFactory.CLOSE.create(window);
			actions.put(ActionBarItem.Close, closeAction);
		}			
		if (menuBarItems.contains(ActionBarItem.CloseAll)) {
			closeAllAction = ActionFactory.CLOSE_ALL.create(window);
			actions.put(ActionBarItem.CloseAll, closeAllAction);
		}
		if (menuBarItems.contains(ActionBarItem.Save)) {
			saveAction = ActionFactory.SAVE.create(window);
			actions.put(ActionBarItem.Save, saveAction);
			saveAsAction = ActionFactory.SAVE_AS.create(window);
			actions.put(ActionBarItem.SaveAs, saveAsAction);
		}
		if (menuBarItems.contains(ActionBarItem.Print)) {
			printAction = ActionFactory.PRINT.create(window);
			actions.put(ActionBarItem.Print, printAction);
		}
		if (menuBarItems.contains(ActionBarItem.Import)) {
			importAction = ActionFactory.IMPORT.create(window);
			actions.put(ActionBarItem.Import, importAction);
		}
		if (menuBarItems.contains(ActionBarItem.Export)) {
			exportAction = ActionFactory.EXPORT.create(window);
			actions.put(ActionBarItem.Export, exportAction);
		}
		if (menuBarItems.contains(ActionBarItem.Properties)) {
			propertiesAction = ActionFactory.PROPERTIES.create(window);
			actions.put(ActionBarItem.Properties, propertiesAction);
		}
				 
		quitAction = ActionFactory.QUIT.create(window);
		actions.put(ActionBarItem.Quit, quitAction);
		
		// Window-Menu
		if (menuBarItems.contains(ActionBarItem.Perspectives))
			openPerspectiveMenu = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);
		if (menuBarItems.contains(ActionBarItem.Views))
			showViewMenu = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
		if (menuBarItems.contains(ActionBarItem.Preferences)) {
			preferencesAction = ActionFactory.PREFERENCES.create(window);
			actions.put(ActionBarItem.Preferences, preferencesAction);
		}
				
		// Help-Menu		
		if (menuBarItems.contains(ActionBarItem.Help)) {
			helpAction = ActionFactory.HELP_CONTENTS.create(window);
			actions.put(ActionBarItem.Help, helpAction);
		}
		if (menuBarItems.contains(ActionBarItem.Intro)) {
			try {
				introAction = ActionFactory.INTRO.create(window);
				actions.put(ActionBarItem.Intro, introAction);
			} catch (Exception x) {
				introAction = null;
				logger.error("Could not create intro action!", x); //$NON-NLS-1$
			}
		}
		if (menuBarItems.contains(ActionBarItem.Update)) {
			// TODO: find out how to hook updateAction
		}

		// Commented to avoid "ordinary" update action
//		updateAction = new UpdateAction();
		aboutAction = ActionFactory.ABOUT.create(window);
		actions.put(ActionBarItem.About, aboutAction);
		
		for(IWorkbenchAction action : actions.values()) 
			getActionBarConfigurer().registerGlobalAction(action);
	}

	protected IMenuManager fileMenu = null;
	public IMenuManager getFileMenu() {
		return fileMenu;
	}
	
	protected IMenuManager windowMenu = null;
	public IMenuManager getWindowMenu() {
		return windowMenu;
	}
	
	protected IMenuManager helpMenu = null;
	public IMenuManager getHelpMenu() {
		return helpMenu;
	}
	
	public void addToMenuGroup(IMenuManager menu, IContributionItem contribItem, String groupName) 
	{
		if (groupName != null)
		{
			if (menu.find(groupName) == null)
				menu.add(new GroupMarker(groupName));
			
			menu.appendToGroup(groupName, contribItem);
			groupNames.put(contribItem, groupName);
		}
		else
		{
			menu.add(contribItem);
		}
	}
	
	public void addToMenuGroup(IMenuManager menu, IAction action, String groupName)
	{
		this.addToMenuGroup(menu, new ActionContributionItem(action), groupName);
	}
	
	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillMenuBar(IMenuManager menuBar) 
	{		
	  // File-Menu
		fileMenu = new MenuManager(Messages.getString("org.nightlabs.base.app.DefaultActionBuilder.fileMenu.text"),  //$NON-NLS-1$
				IWorkbenchActionConstants.M_FILE);
		
		menuBar.add(fileMenu);

		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		
		// New
		if (menuBarItems.contains(ActionBarItem.New)) {
			addToMenuGroup(fileMenu, newMenu, IWorkbenchActionConstants.NEW_EXT);
			createNewEntries(newMenu);
			fileMenu.add(new Separator());			
		}
		
		// Open
		if (menuBarItems.contains(ActionBarItem.Open) && menuBarItems.contains(ActionBarItem.RecentFiles)) {
			addToMenuGroup(fileMenu, openAction, IWorkbenchActionConstants.OPEN_EXT);
			addToMenuGroup(fileMenu, recentFilesMenu, IWorkbenchActionConstants.OPEN_EXT);
			historyFileMenuManager = recentFilesMenu;
			createHistoryEntries(historyFileMenuManager);
			fileMenu.add(new Separator());						
		}
		else if (menuBarItems.contains(ActionBarItem.Open)) {
			addToMenuGroup(fileMenu, openAction, IWorkbenchActionConstants.OPEN_EXT);
			fileMenu.add(new Separator());								
		}		
		else if (menuBarItems.contains(ActionBarItem.RecentFiles)) {
			addToMenuGroup(fileMenu, recentFilesMenu, IWorkbenchActionConstants.OPEN_EXT);
			historyFileMenuManager = recentFilesMenu;
			createHistoryEntries(historyFileMenuManager);
			fileMenu.add(new Separator());			
		}
		
		// Close		
		if (menuBarItems.contains(ActionBarItem.Close) && menuBarItems.contains(ActionBarItem.CloseAll)) {
			addToMenuGroup(fileMenu, closeAction, IWorkbenchActionConstants.CLOSE_EXT);
			addToMenuGroup(fileMenu, closeAllAction, IWorkbenchActionConstants.CLOSE_EXT);
			fileMenu.add(new Separator());
		}			
		else if (menuBarItems.contains(ActionBarItem.Close)) {
			addToMenuGroup(fileMenu, closeAction, IWorkbenchActionConstants.CLOSE_EXT);
			fileMenu.add(new Separator());			
		}
		else if (menuBarItems.contains(ActionBarItem.CloseAll)) {
			addToMenuGroup(fileMenu, closeAllAction, IWorkbenchActionConstants.CLOSE_EXT);
			fileMenu.add(new Separator());						
		}			    
			
		// Save
		if (menuBarItems.contains(ActionBarItem.Save)) {
			addToMenuGroup(fileMenu, saveAction, IWorkbenchActionConstants.SAVE_EXT);
			addToMenuGroup(fileMenu, saveAsAction, IWorkbenchActionConstants.SAVE_EXT);
			fileMenu.add(new Separator());			
		}
		
		// Print
		if (menuBarItems.contains(ActionBarItem.Print)) {
			addToMenuGroup(fileMenu, printAction, IWorkbenchActionConstants.PRINT_EXT);
			fileMenu.add(new Separator());			
		}
		
		// Import / Export
		if (menuBarItems.contains(ActionBarItem.Import) && menuBarItems.contains(ActionBarItem.Export)) {
			addToMenuGroup(fileMenu, importAction, IWorkbenchActionConstants.IMPORT_EXT);
			addToMenuGroup(fileMenu, exportAction, IWorkbenchActionConstants.IMPORT_EXT);
			fileMenu.add(new Separator());
		}		
		else if (menuBarItems.contains(ActionBarItem.Import)) {
			addToMenuGroup(fileMenu, importAction, IWorkbenchActionConstants.IMPORT_EXT);
			fileMenu.add(new Separator());			
		}
		else if (menuBarItems.contains(ActionBarItem.Export)) {
			addToMenuGroup(fileMenu, exportAction, IWorkbenchActionConstants.IMPORT_EXT);
			fileMenu.add(new Separator());			
		}
		
		// Properties		
		if (menuBarItems.contains(ActionBarItem.Properties)) {
			addToMenuGroup(fileMenu, propertiesAction, null);
			fileMenu.add(new Separator());
		}
		
		addToMenuGroup(fileMenu, quitAction, null);
		
    fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));
    menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    
//    // Menus form contributionItemSet-ExtensionPoint
//		try {
//			ContributionItemSetRegistry.sharedInstance().contributeToMenuBar(menuBar);
//		} catch (EPProcessorException e) {
//			logger.error("There occured an error while processing the ContributionItemSetRegistry!", e);
//			e.printStackTrace();
//		}
    
    // Window-Menu
		windowMenu = new MenuManager(Messages.getString("org.nightlabs.base.app.DefaultActionBuilder.windowMenu.text"),  //$NON-NLS-1$
				IWorkbenchActionConstants.M_WINDOW);
		menuBar.add(windowMenu);		

		// Perspective-SubMenu
		if (menuBarItems.contains(ActionBarItem.Perspectives)) {		
			MenuManager openPerspectiveMenuMgr = new MenuManager(Messages.getString("org.nightlabs.base.app.DefaultActionBuilder.openPerspectiveMenu.text"),  //$NON-NLS-1$
					NLWorkbenchActionConstants.M_PERSPECTIVES);
			openPerspectiveMenuMgr.add(openPerspectiveMenu);
			windowMenu.add(openPerspectiveMenuMgr);					
		}

		// View-SubMenu
		if (menuBarItems.contains(ActionBarItem.Views)) {
			MenuManager showViewMenuMgr = new MenuManager(Messages.getString("org.nightlabs.base.app.DefaultActionBuilder.showViewMenu.text"),  //$NON-NLS-1$
					NLWorkbenchActionConstants.M_VIEWS);
			showViewMenuMgr.add(showViewMenu);
			windowMenu.add(showViewMenuMgr);		
			windowMenu.add(new Separator());			
		}

		if (menuBarItems.contains(ActionBarItem.Preferences))
			windowMenu.add(preferencesAction);

		// Help-Menu
		helpMenu = new MenuManager(Messages.getString("org.nightlabs.base.app.DefaultActionBuilder.helpMenu.text"),  //$NON-NLS-1$
				IWorkbenchActionConstants.M_HELP);
		menuBar.add(helpMenu);
		if (menuBarItems.contains(ActionBarItem.Help)) {
			helpMenu.add(helpAction);
			helpMenu.add(new Separator());
		}
		if (menuBarItems.contains(ActionBarItem.Intro)) {
			if (introAction != null)
				helpMenu.add(introAction);

			helpMenu.add(new Separator());			
		}
		
		if (updateAction != null)
			helpMenu.add(updateAction);
		helpMenu.add(aboutAction);
		
		try {
			ContributionItemSetRegistry.sharedInstance().contributeToMenuBar(menuBar);
		} catch (EPProcessorException e) {
			logger.error("There occured an error while processing the ContributionItemSetRegistry!", e); //$NON-NLS-1$
			e.printStackTrace();
		}		
	}

	public void fillCoolBar(ICoolBarManager coolBar) 
	{
		try {
			ContributionItemSetRegistry.sharedInstance().contributeToCoolBar(coolBar);
		} catch (EPProcessorException e) {
			logger.error("There occured an error while processing the ContributionItemSetRegistry!", e); //$NON-NLS-1$
			e.printStackTrace();
		}
	}
	
	public void dispose() 
	{
	  	aboutAction.dispose();
	    quitAction.dispose();
	    
	  for (IWorkbenchAction action : actions.values())
	  		action.dispose();
	}
	
	protected RecentFileCfMod fileHistory;
	protected IMenuManager historyFileMenuManager;	
	protected int historyEntries = 0;
	protected int maxHistoryLength = 0;
	protected String firstHistoryID = null; 
	protected String lastHistoryID = null;

	// is notifyed if a file has been opend or created
	protected PropertyChangeListener historyFileListener = new PropertyChangeListener() {	
		public void propertyChange(PropertyChangeEvent arg0) {
			if (arg0.getPropertyName().equals(OpenFileAction.HISTORY_FILE_ADDED)) {
				String fileName = (String) arg0.getNewValue();
				addHistoryFile(historyFileMenuManager, fileName, false);
			}
		}	
	}; 
		
	protected void addHistoryFile(IMenuManager menuMan, String fileName, boolean append) 
	{
		ReOpenFileAction action = new ReOpenFileAction(fileName);
		if (firstHistoryID == null) {
			firstHistoryID = action.getId();
			menuMan.add(action);				
		}
		else 
		{
			if (!append) {
				menuMan.insertBefore(firstHistoryID, action);
				firstHistoryID = action.getId();				
			}
			else
				menuMan.add(action);
		}
		
		historyEntries++;
		
		if (maxHistoryLength == historyEntries)
			lastHistoryID = action.getId();
		
		if (maxHistoryLength < historyEntries) 
		{
			menuMan.remove(lastHistoryID);
			if (!fileHistory.getRecentFileNames().contains(fileName))
				fileHistory.getRecentFileNames().add(fileName);
			
			for (int i=0; i<fileHistory.getRecentFileNames().size()-maxHistoryLength; i++) {
				fileHistory.getRecentFileNames().remove(i);				
			}				
		}
	}
	
	/**
	 * creates the MenuEntries of all previous opened files
	 * @param menuMan The IMenuManager to which the entries should be added
	 */
	protected void createHistoryEntries(IMenuManager menuMan) 
	{
		if (fileHistory != null) {
			List fileNames = fileHistory.getRecentFileNames();
			maxHistoryLength = fileHistory.getMaxHistoryLength();
			if (fileNames.size() != 0) {
				for (int i=fileNames.size()-1; i!=0; i--) {
					String fileName = (String) fileNames.get(i);
					addHistoryFile(menuMan, fileName, true);
				}											
			}
		}			
	}	
		
	/**
	 * adds entries registered in the {@link NewFileRegistry}-ExtensionPoint
	 * to the given {@link MenuManager}
	 * 
	 * @param menuMan the IMenuManager to add new entries to
	 */
	protected void createNewEntries(IMenuManager menuMan)
	{
		NewFileRegistry newFileRegistry = NewFileRegistry.sharedInstance(); 
		Map categoryID2Actions = newFileRegistry.getCategory2Actions();
		List<INewFileAction> defaultActions = new ArrayList<INewFileAction>();
		for (Iterator it = categoryID2Actions.keySet().iterator(); it.hasNext(); ) 
		{
			String categoryID = (String) it.next();
			List actions = (List) categoryID2Actions.get(categoryID);
			for (Iterator itActions = actions.iterator(); itActions.hasNext(); ) {
				INewFileAction action = (INewFileAction) itActions.next();			
				if (categoryID.equals(NewFileRegistry.DEFAULT_CATEGORY_ID)) {
					defaultActions.add(action);
				}
				else {
					String categoryName = newFileRegistry.getCategoryName(categoryID);
					if (categoryName != null && !categoryName.equals("")) {					 //$NON-NLS-1$
						IMenuManager categoryMenu = new MenuManager(categoryName);
						categoryMenu.add(action);
						menuMan.add(categoryMenu);
					}
				}							
			}
		}
		for (Iterator itDefault = defaultActions.iterator(); itDefault.hasNext(); ) {
			menuMan.add((IAction)itDefault.next());
		}
	}
	
}
