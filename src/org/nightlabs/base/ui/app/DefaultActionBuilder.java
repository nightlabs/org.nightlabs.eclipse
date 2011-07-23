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

package org.nightlabs.base.ui.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.nightlabs.base.ui.action.ContributionItemSetRegistry;
import org.nightlabs.base.ui.action.NewWizardAction;
import org.nightlabs.base.ui.action.OpenFileAction;
import org.nightlabs.base.ui.action.ReOpenFileAction;
import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.base.ui.action.registry.ActionVisibilityContext;
import org.nightlabs.base.ui.action.registry.ActionVisibilityDecider;
import org.nightlabs.base.ui.config.RecentFileCfMod;
import org.nightlabs.base.ui.perspective.PerspectiveExtensionRegistry;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.jfire.compatibility.CompatibleActionFactory;
import org.nightlabs.util.CollectionUtil;

/**
 * This class is a subclass of {@link ActionBarAdvisor} which provides some functionality for defining,
 * which actions should be available by default in the main menu and coolbar of an RCP application.
 * It is recommended to use this class instead of an own subclass of {@link ActionBarAdvisor},
 * because it integrates some features, like the {@link NewWizardAction} and the recent file menu entries
 * by default.
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
//		NewWizard,
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
		Views,
		Back_History,
		Forward_History
	}

//	public static enum ActionGroup
//	{
//		Save,
//		Print,
//		Close;
//	}

	protected IActionBarConfigurer configurer;

	// Actions
	protected Map<ActionBarItem, IAction> actions;
	protected Map<IContributionItem, String> groupNames;

	// File-Menu
//	protected IMenuManager newMenu;
//	protected ActionFactory.IWorkbenchAction newWizardAction;
	protected IContributionItem newWizardAction;
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

	// Window-Menu
	protected IContributionItem openPerspectiveMenu;
	protected IContributionItem showViewMenu;
	protected ActionFactory.IWorkbenchAction preferencesAction;

	protected IAction backAction;
	protected IAction forwardAction;

	protected Collection<ActionBarItem> menuBarItems;
	protected Collection<ActionBarItem> coolBarItems;

//	private boolean useEclipseNavigationHistory = false;

	public DefaultActionBuilder(IActionBarConfigurer configurer,
				Collection<ActionBarItem> showInMenuBar, Collection<ActionBarItem> showInCoolBar)
	{
		super(configurer);
		menuBarItems = showInMenuBar == null ? new HashSet<ActionBarItem>() : showInMenuBar;
		coolBarItems = showInCoolBar == null ? new HashSet<ActionBarItem>() : showInCoolBar;

		actions = new HashMap<ActionBarItem, IAction>();
		groupNames = new HashMap<IContributionItem, String>();

		if (menuBarItems.contains(ActionBarItem.RecentFiles))
			initRecentFileConfig();
	}

	public DefaultActionBuilder(IActionBarConfigurer configurer)
	{
//		this(configurer, Arrays.asList(ActionBarItem.values()), null);
		// changed this to make it possible to remove something from the list
		this(configurer, CollectionUtil.array2ArrayList(ActionBarItem.values()), null);
	}

//	public void setUseEclipseNavigationHistory(boolean useEclipseNavigationHistory) {
//		this.useEclipseNavigationHistory = useEclipseNavigationHistory;
//	}

	protected void initRecentFileConfig()
	{
		try {
			fileHistory = Config.sharedInstance().createConfigModule(RecentFileCfMod.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}

	protected boolean isContained(ActionBarItem item) {
		if (menuBarItems.contains(item) || coolBarItems.contains(item)) {
			return true;
		}
		return false;
	}

	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#makeActions(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	protected void makeActions(IWorkbenchWindow window)
	{
		// File
		if (isContained(ActionBarItem.KeyAssist)) {
			//FIXME doesn't exist in RAP
			//keyAssistHandler = new ShowKeyAssistHandler();
		}
		if (isContained(ActionBarItem.New)) {
			IWorkbenchAction newAction = new NewWizardAction(window);
			newAction.setText(Messages.getString("org.nightlabs.base.ui.app.DefaultActionBuilder.action.new.name")); //$NON-NLS-1$
			newWizardAction = new ActionContributionItem(newAction);
			actions.put(ActionBarItem.New, newAction);
		}
		if (isContained(ActionBarItem.Open)) {
			openAction = new OpenFileAction();
		}
		if (isContained(ActionBarItem.RecentFiles)) {
			if (openAction != null)
				openAction.addPropertyChangeListener(historyFileListener);
			recentFilesMenu = new MenuManager(Messages.getString("org.nightlabs.base.ui.app.DefaultActionBuilder.recentFilesMenu.text"), NLWorkbenchActionConstants.M_RECENT_FILES); //$NON-NLS-1$
			recentFilesMenu.add(new GroupMarker(IWorkbenchActionConstants.HISTORY_GROUP));
		}
		if (isContained(ActionBarItem.Close)) {
			closeAction = ActionFactory.CLOSE.create(window);
			actions.put(ActionBarItem.Close, closeAction);
		}
		if (isContained(ActionBarItem.CloseAll)) {
			closeAllAction = ActionFactory.CLOSE_ALL.create(window);
			actions.put(ActionBarItem.CloseAll, closeAllAction);
		}
		if (isContained(ActionBarItem.Save)) {
			saveAction = ActionFactory.SAVE.create(window);
			actions.put(ActionBarItem.Save, saveAction);
			saveAsAction = ActionFactory.SAVE_AS.create(window);
			actions.put(ActionBarItem.SaveAs, saveAsAction);
		}
		if (isContained(ActionBarItem.Print)) {
			printAction = ActionFactory.PRINT.create(window);
			actions.put(ActionBarItem.Print, printAction);
		}
		if (isContained(ActionBarItem.Import)) {
			importAction = ActionFactory.IMPORT.create(window);
			actions.put(ActionBarItem.Import, importAction);
		}
		if (isContained(ActionBarItem.Export)) {
			exportAction = ActionFactory.EXPORT.create(window);
			actions.put(ActionBarItem.Export, exportAction);
		}
		if (isContained(ActionBarItem.Properties)) {
			propertiesAction = ActionFactory.PROPERTIES.create(window);
			actions.put(ActionBarItem.Properties, propertiesAction);
		}
		if (isContained(ActionBarItem.Quit)) {
			quitAction = ActionFactory.QUIT.create(window);
			actions.put(ActionBarItem.Quit, quitAction);
		}

		// Navigation
		if (isContained(ActionBarItem.Back_History)) {
//			if (useEclipseNavigationHistory) {
				backAction = ActionFactory.BACKWARD_HISTORY.create(window);
//			} else {
//				backAction = EditorHistory.sharedInstance().getBackAction();
//				// reset text + tooltip because original text of action is ignored (english text of corresponding command is displayed)
//				backAction.setText(Messages.getString("org.nightlabs.base.ui.app.DefaultActionBuilder.action.historyBack.text")); //$NON-NLS-1$
//				backAction.setToolTipText(Messages.getString("org.nightlabs.base.ui.app.DefaultActionBuilder.action.historyBack.tooltip")); //$NON-NLS-1$
//			}
//			if (logger.isDebugEnabled()) {
//				logger.debug("back action text = "+backAction.getText()); //$NON-NLS-1$
//			}
			actions.put(ActionBarItem.Back_History, backAction);
		}
		if (isContained(ActionBarItem.Forward_History)) {
//			if (useEclipseNavigationHistory) {
				forwardAction = ActionFactory.FORWARD_HISTORY.create(window);
//			}
//			else {
//				forwardAction = EditorHistory.sharedInstance().getForwardAction();
//				// reset text + tooltip because original text of action is ignored (english text of corresponding command is displayed)
//				forwardAction.setText(Messages.getString("org.nightlabs.base.ui.app.DefaultActionBuilder.action.historyForward.text")); //$NON-NLS-1$
//				forwardAction.setToolTipText(Messages.getString("org.nightlabs.base.ui.app.DefaultActionBuilder.action.historyForward.tooltip")); //$NON-NLS-1$
//			}
//			if (logger.isDebugEnabled()) {
//				logger.debug("forward action text = "+forwardAction.getText()); //$NON-NLS-1$
//			}
			actions.put(ActionBarItem.Forward_History, forwardAction);
		}

		// Window-Menu
		if (isContained(ActionBarItem.Perspectives))
			openPerspectiveMenu = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);
		if (isContained(ActionBarItem.Views))
			showViewMenu = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
		if (isContained(ActionBarItem.Preferences)) {
			preferencesAction = ActionFactory.PREFERENCES.create(window);
			actions.put(ActionBarItem.Preferences, preferencesAction);
		}

		// Help-Menu
		if (isContained(ActionBarItem.Help)) {
			helpAction = ActionFactory.HELP_CONTENTS.create(window);
			actions.put(ActionBarItem.Help, helpAction);
		}
		if (isContained(ActionBarItem.Intro) && window.getWorkbench().getIntroManager().getIntro() != null) {
			try {
				introAction = ActionFactory.INTRO.create(window);
				actions.put(ActionBarItem.Intro, introAction);
			} catch (Exception x) {
				introAction = null;
				logger.error("Could not create intro action!", x); //$NON-NLS-1$
			}
		}
		if (isContained(ActionBarItem.Update)) {
			// TODO: find out how to hook updateAction
			// Commented to avoid "ordinary" update action
//			updateAction = new UpdateAction();
//			actions.put(ActionBarItem.Update, updateAction);
		}
		if (isContained(ActionBarItem.About) && CompatibleActionFactory.ABOUT != null) {
			aboutAction = CompatibleActionFactory.ABOUT.create(window);
			actions.put(ActionBarItem.About, aboutAction);
		}

		for(IAction action : actions.values())
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

	protected IMenuManager navigateMenu = null;
	public IMenuManager getNavigateMenu() {
		return navigateMenu;
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

	private IMenuManager menuBar;

	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillMenuBar(IMenuManager menuBar)
	{
		this.menuBar = menuBar;

		// File-Menu
		fileMenu = new MenuManager(Messages.getString("org.nightlabs.base.ui.app.DefaultActionBuilder.fileMenu.text"),  //$NON-NLS-1$
				IWorkbenchActionConstants.M_FILE);

		menuBar.add(fileMenu);

		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

//		// New Wizard
//		if (menuBarItems.contains(ActionBarItem.NewWizard)) {
//			addToMenuGroup(fileMenu, newMenu, IWorkbenchActionConstants.NEW);
//			fileMenu.add(newWizardAction);
//			fileMenu.add(new Separator());
//		}
//
//		// New (NightLabs New File Extension Point)
//		if (menuBarItems.contains(ActionBarItem.New))
//		{
//			addToMenuGroup(fileMenu, newMenu, IWorkbenchActionConstants.NEW_EXT);
//			fileMenu.add(newWizardAction);
//			createNewEntries(newMenu);
//			if (!menuBarItems.contains(ActionBarItem.NewWizard)) {
//				fileMenu.add(new Separator());
//			}
//		}
		// New Wizard
		if (menuBarItems.contains(ActionBarItem.New)) {
			addToMenuGroup(fileMenu, newWizardAction, IWorkbenchActionConstants.NEW_EXT);
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

		if (menuBarItems.contains(ActionBarItem.Quit)) {
			addToMenuGroup(fileMenu, quitAction, null);
		}

		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		createContributionItemSetRegistry();

		// Navigate-Menu
		if (menuBarItems.contains(ActionBarItem.Back_History) || menuBarItems.contains(ActionBarItem.Forward_History)) {
			navigateMenu = new MenuManager(Messages.getString("org.nightlabs.base.ui.app.DefaultActionBuilder.menu.navigate.text"), IWorkbenchActionConstants.M_NAVIGATE); //$NON-NLS-1$
			menuBar.add(navigateMenu);

			// Back Navigation History
			if (menuBarItems.contains(ActionBarItem.Back_History)) {
				navigateMenu.add(backAction);
			}
			// Forward Navigation History
			if (menuBarItems.contains(ActionBarItem.Forward_History)) {
				navigateMenu.add(forwardAction);
			}
		}

		// Window-Menu
		windowMenu = new MenuManager(Messages.getString("org.nightlabs.base.ui.app.DefaultActionBuilder.windowMenu.text"),  //$NON-NLS-1$
				IWorkbenchActionConstants.M_WINDOW);
		menuBar.add(windowMenu);

		// Perspective-SubMenu
		if (menuBarItems.contains(ActionBarItem.Perspectives)) {
			MenuManager openPerspectiveMenuMgr = new MenuManager(Messages.getString("org.nightlabs.base.ui.app.DefaultActionBuilder.openPerspectiveMenu.text"),  //$NON-NLS-1$
					NLWorkbenchActionConstants.M_PERSPECTIVES);
			openPerspectiveMenuMgr.add(openPerspectiveMenu);
			windowMenu.add(openPerspectiveMenuMgr);
		}

		// View-SubMenu
		if (menuBarItems.contains(ActionBarItem.Views)) {
			MenuManager showViewMenuMgr = new MenuManager(Messages.getString("org.nightlabs.base.ui.app.DefaultActionBuilder.showViewMenu.text"),  //$NON-NLS-1$
					NLWorkbenchActionConstants.M_VIEWS);
			showViewMenuMgr.add(showViewMenu);
			windowMenu.add(showViewMenuMgr);
			windowMenu.add(new Separator());
		}

		if (menuBarItems.contains(ActionBarItem.Preferences))
			windowMenu.add(preferencesAction);

		// Help-Menu
		helpMenu = new MenuManager(Messages.getString("org.nightlabs.base.ui.app.DefaultActionBuilder.helpMenu.text"),  //$NON-NLS-1$
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

		if (menuBarItems.contains(ActionBarItem.About) && aboutAction != null) {
			helpMenu.add(aboutAction);	
		}
	}

	private ContributionItemSetRegistry contributionItemSetRegistry;
	private ICoolBarManager coolBar;
	private void createContributionItemSetRegistry() {
		if (contributionItemSetRegistry == null) {
			contributionItemSetRegistry = new ContributionItemSetRegistry(new ActionVisibilityDecider() {
				@Override
				public boolean isVisible(ActionVisibilityContext actionVisibilityContext, ActionDescriptor actionDescriptor) {
					if (!super.isVisible(actionVisibilityContext, actionDescriptor))
						return false;

					Map<String, Set<String>> perspectiveID2ExtensionIDs = PerspectiveExtensionRegistry.sharedInstance().getPerspectiveID2ExtensionIDs(
							contributionItemSetRegistry.getExtensionPointID());
					if (perspectiveID2ExtensionIDs != null) {
						String perspectiveID = RCPUtil.getActivePerspectiveID();
						Set<String> extensionIDs = perspectiveID2ExtensionIDs.get(perspectiveID);
						if (extensionIDs != null) {
							return extensionIDs.contains(actionDescriptor.getID());
						}
						else {
							return false;
						}
					}

					return true;
				}
			});
			contributionItemSetRegistry.process();
		}

		if (RCPUtil.getActiveWorkbenchWindow() == null) {
			// This Thread is necessary in order to prevent blocking the application from shutdown.
			// It might happen, that the application never has an active perspective, because it is shut down before
			// completely starting (e.g. when the classloader-config changed and the login occurs very early
			// and decides to restart the application.
			
			final Display display = Display.getDefault();
			
			Thread thread = new Thread() {
				@Override
				public void run() {
					logger.info("activeWorkbenchWindow is null. Will re-enqueue this method into the event dispatcher and exit."); //$NON-NLS-1$
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// ignore
					}
					display.asyncExec(new Runnable() {
						public void run() {
							try {
							createContributionItemSetRegistry();
							}catch(Exception aEx) {
								aEx.printStackTrace();
							}
						}
					});
				}
			};
			thread.setDaemon(true);
			thread.start();

			return;
		} // if (RCPUtil.getActiveWorkbenchWindow() == null) {

		RCPUtil.getActiveWorkbenchWindow().addPerspectiveListener(new PerspectiveAdapter() {
			@Override
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
				contribute();
			}
		});

		contribute();
	}

	private void contribute()
	{
		if (menuBar != null) {
			contributeToMenuBar(menuBar);
		}

		if (coolBar != null) {
			contributeToCoolBar(coolBar);
		}
	}

	private void contributeToMenuBar(IMenuManager menu) {
		contributionItemSetRegistry.removeAllFromMenuBar(menu);
		contributionItemSetRegistry.contributeToMenuBar(menu);
	}

	private void contributeToCoolBar(ICoolBarManager coolBarManager) {
		contributionItemSetRegistry.removeAllFromCoolBar(coolBarManager);
		contributionItemSetRegistry.contributeToCoolBar(coolBarManager);
	}

	@Override
	public void fillCoolBar(ICoolBarManager coolBar)
	{
		this.coolBar = coolBar;
		createContributionItemSetRegistry();

		// New Wizard
		if (coolBarItems.contains(ActionBarItem.New)) {
			addCoolBarEntry(coolBar, newWizardAction);
		}

		// Open
		if (coolBarItems.contains(ActionBarItem.Open)) {
			addCoolBarEntry(coolBar, openAction);
		}

		// Close
		if (coolBarItems.contains(ActionBarItem.Close)) {
			addCoolBarEntry(coolBar, closeAction);
		}
		if (coolBarItems.contains(ActionBarItem.CloseAll)) {
			addCoolBarEntry(coolBar, closeAllAction);
		}

		// Save
		if (coolBarItems.contains(ActionBarItem.Save)) {
			addCoolBarEntry(coolBar, saveAction);
		}

		// Print
		if (coolBarItems.contains(ActionBarItem.Print)) {
			addCoolBarEntry(coolBar, printAction);
		}

		// Import / Export
		if (coolBarItems.contains(ActionBarItem.Import)) {
			addCoolBarEntry(coolBar, importAction);
		}
		if (coolBarItems.contains(ActionBarItem.Export)) {
			addCoolBarEntry(coolBar, exportAction);
		}

		// Properties
		if (coolBarItems.contains(ActionBarItem.Properties)) {
			addCoolBarEntry(coolBar, propertiesAction);
		}

		if (coolBarItems.contains(ActionBarItem.Preferences))
			addCoolBarEntry(coolBar, preferencesAction);

		// Help
		if (coolBarItems.contains(ActionBarItem.Help)) {
			addCoolBarEntry(coolBar, helpAction);
		}
		if (coolBarItems.contains(ActionBarItem.Intro)) {
			addCoolBarEntry(coolBar, introAction);
		}

		if (coolBarItems.contains(ActionBarItem.Update)) {
//			addCoolBarEntry(coolBar, updateAction);
		}

		if (coolBarItems.contains(ActionBarItem.About)) {
			addCoolBarEntry(coolBar, aboutAction);
		}

		if (coolBarItems.contains(ActionBarItem.Back_History)) {
			addCoolBarEntry(coolBar, backAction);
		}

		if (coolBarItems.contains(ActionBarItem.Forward_History)) {
			addCoolBarEntry(coolBar, forwardAction);
		}
	}

	protected void addCoolBarEntry(ICoolBarManager coolBar, IAction action) {
		IToolBarManager toolBarManager = new ToolBarManager();
		toolBarManager.add(action);
		coolBar.add(toolBarManager);
	}

	protected void addCoolBarEntry(ICoolBarManager coolBar, IContributionItem item) {
		IToolBarManager toolBarManager = new ToolBarManager();
		toolBarManager.add(item);
		coolBar.add(toolBarManager);
	}

	@Override
	public void dispose()
	{
//	  aboutAction.dispose();
//	  quitAction.dispose();

	  for (IAction action : actions.values()) {
		  if (action instanceof IWorkbenchAction) {
			  IWorkbenchAction workbenchAction = (IWorkbenchAction) action;
			  workbenchAction.dispose();
		  }
	  }
	}

	protected RecentFileCfMod fileHistory;
	protected IMenuManager historyFileMenuManager;
	protected int historyEntries = 0;
	protected int maxHistoryLength = 0;
	protected String firstHistoryID = null;
	protected String lastHistoryID = null;

	// is notified if a file has been opened or created
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
			List<String> fileNames = fileHistory.getRecentFileNames();
			maxHistoryLength = fileHistory.getMaxHistoryLength();
			if (fileNames.size() != 0) {
				for (int i=fileNames.size()-1; i!=0; i--) {
					String fileName = fileNames.get(i);
					addHistoryFile(menuMan, fileName, true);
				}
			}
		}
	}

//	/**
//	 * adds entries registered in the {@link NewFileRegistry}-ExtensionPoint
//	 * to the given {@link MenuManager}
//	 *
//	 * @param menuMan the IMenuManager to add new entries to
//	 */
//	@SuppressWarnings("deprecation")
//	protected void createNewEntries(IContributionManager menuMan)
//	{
//		NewFileRegistry newFileRegistry = NewFileRegistry.sharedInstance();
//		Map<String, List<INewFileAction>> categoryID2Actions = newFileRegistry.getCategory2Actions();
//		List<INewFileAction> defaultActions = new ArrayList<INewFileAction>();
//		for (Iterator<String> it = categoryID2Actions.keySet().iterator(); it.hasNext(); )
//		{
//			String categoryID = it.next();
//			List<INewFileAction> actions = categoryID2Actions.get(categoryID);
//			for (Iterator<INewFileAction> itActions = actions.iterator(); itActions.hasNext(); ) {
//				INewFileAction action = itActions.next();
//				if (categoryID.equals(NewFileRegistry.DEFAULT_CATEGORY_ID)) {
//					defaultActions.add(action);
//				}
//				else {
//					String categoryName = newFileRegistry.getCategoryName(categoryID);
//					if (categoryName != null && !categoryName.equals("")) {					 //$NON-NLS-1$
//						IMenuManager categoryMenu = new MenuManager(categoryName);
//						categoryMenu.add(action);
//						menuMan.add(categoryMenu);
//					}
//				}
//			}
//		}
//		for (Iterator<INewFileAction> itDefault = defaultActions.iterator(); itDefault.hasNext(); ) {
//			menuMan.add(itDefault.next());
//		}
//	}

}
