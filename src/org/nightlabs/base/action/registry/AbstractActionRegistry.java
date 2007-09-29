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

package org.nightlabs.base.action.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.jface.action.SubContributionManager;
import org.eclipse.jface.action.SubCoolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener3;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.nightlabs.base.action.IXContributionItem;
import org.nightlabs.base.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.extensionpoint.EPProcessorException;
import org.nightlabs.base.perspective.PerspectiveExtensionRegistry;
import org.nightlabs.base.util.RCPUtil;


/**
 * This class can be used to easily create an own extension-point for the management of actions.
 * To implement it, you not only need to create a class extending <code>AbstractActionRegistry</code>,
 * but as well define the extension-point in your plugin.xml. You must adhere to the following structure:
 * <ul>
 *  <li>groupMarker (0 - *) {name, path}</li>
 *  <li>separator (0 - *) {name, path}</li>
 *  <li>action (0 - *) {id, name, tooltip, icon, disabledIcon, hoverIcon, menubarPath, toolbarPath, contextmenuPath, visible, visibleInMenubar, visibleInToolbar, visibleInContextmenu}</li>
 *  <li>menu (0 - *) {id, label, path}
 *   <ul>
 *    <li>groupMarker (0 - *) {name}</li>
 *    <li>separator (0 - *) {name}</li>
 *   </ul>
 *  </li>
 * </ul>
 * As you see, the <i>path</i> property of <i>groupMarker</i> and <i>separator</i> is only used
 * when they are defined on the top-level; it is ignored when these elements
 * are declared within a menu.
 * </p>
 * <p>
 * Of course, you can decide to omit some of the properties in the extension point and control them
 * programatically. For example. this is very often the case with the properties visible,
 * visibleInMenubar, visibleInToolbar and visibleInContextmenu. 
 * </p>
 * <p>
 * Instead of <i>action</i>, you might want to use another element name. This can easily be done via
 * {@link #getActionElementName()}.
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Daniel Mazurek - daniel at nightlabs dot de
 */
public abstract class AbstractActionRegistry 
extends AbstractEPProcessor
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(AbstractActionRegistry.class);

	/**
	 * key: String articleEditActionID<br/>
	 * value: ActionDescriptor actionDescriptor
	 */
	private Map<String, ActionDescriptor> actionDescriptorsByID = new HashMap<String, ActionDescriptor>();

	/**
	 * Contains all ActionDescriptors and MenuDescriptors in the order in which they have been read.
	 */
	private List<ItemDescriptor> menuRaw = new ArrayList<ItemDescriptor>();

	private List<ItemDescriptor> menuSortedForMenubar = null;
	private List<ItemDescriptor> menuSortedForContextmenu = null;
	private List<ItemDescriptor> menuSortedForToolbar = null;

	protected static final String KIND_MENUBAR = "menubar"; //$NON-NLS-1$
	protected static final String KIND_CONTEXTMENU = "contextmenu"; //$NON-NLS-1$
	protected static final String KIND_TOOLBAR = "toolbar"; //$NON-NLS-1$
	protected static final String KIND_COOLBAR = "coolbar"; //$NON-NLS-1$

	/**
	 * This method populates the passed <code>menuManager</code> with all visible
	 * {@link IAction}s and other {@link IXContributionItem}s (e.g. separators or group-markers).
	 * <p>
	 * This method is meant to be used in an {@link org.eclipse.ui.IEditorActionBarContributor}
	 * or another action-bar-contributor in order to contribute to the main menu or better a
	 * sub-menu (which needs then to be created by the contributor before).
	 * </p>
	 * <p>
	 * This method contributes WITHOUT removing items before. If you want to remove items, you must
	 * do it manually before calling this method.
	 * </p>
	 *
	 * @param menuManager The menu-manager which is responsible for the menu (or more likely sub-menu).
	 * @return Returns the number of visible items (i.e. actions) that have been added (because some
	 *		might be invisible and therefore not added).
	 */
	public int contributeToMenuBar(IMenuManager menuManager)
	{
		return contribute(menuManager, KIND_MENUBAR);
	}

	/**
	 * This method populates the passed <code>menuManager</code> with all visible
	 * {@link IAction}s and other {@link IXContributionItem}s (e.g. separators or group-markers).
	 * <p>
	 * This method is meant to be used in an {@link org.eclipse.ui.IEditorActionBarContributor}
	 * or another action-bar-contributor (or wherever you prefer to handle the context menu) in
	 * order to contribute to the context-menu.
	 * </p>
	 * <p>
	 * This method contributes WITHOUT removing items before. If you want to remove items, you must
	 * do it manually before calling this method.
	 * </p>
	 *
	 * @param menuManager The menu-manager which is responsible for the context-menu (or one of its sub-menus).
	 * @return Returns the number of visible items (i.e. actions) that have been added (because some
	 *		might be invisible and therefore not added).
	 */
	public int contributeToContextMenu(IMenuManager menuManager)
	{
		return contribute(menuManager, KIND_CONTEXTMENU);
	}

	/**
	 * This method populates the passed <code>toolBarManager</code> with all visible
	 * {@link IAction}s and other {@link IXContributionItem}s (e.g. separators or group-markers).
	 * <p>
	 * This method is meant to be used in an {@link org.eclipse.ui.IEditorActionBarContributor}
	 * or another action-bar-contributor in order to contribute to the toolbar. Note, that
	 * sub-menu-structures are flattened automatically because a toolbar doesn't support sub-menus.
	 * </p>
	 * <p>
	 * This method contributes WITHOUT removing items before. If you want to remove items, you must
	 * do it manually before calling this method.
	 * </p>
	 *
	 * @param toolBarManager The tool-bar-manager which is responsible for your tool-bar.
	 * @return Returns the number of visible items (i.e. actions) that have been added (because some
	 *		might be invisible and therefore not added).
	 */
	public int contributeToToolBar(IToolBarManager toolBarManager)
	{
		return contribute(toolBarManager, KIND_TOOLBAR);
	}

	private static boolean useRemoveInsteadOfUnvisibleWorkaround = true;
	
	/**
	 * Removes all contributions of this registry from the CoolBar of the
	 * given coolBarManager. Ideally this is done by making the contributions
	 * invisible, so Eclipse can remember their positions. However
	 * this is currently not possible and thats why this method 
	 * removes the affected contributions from the CoolBar.
	 * This behaviour can is configured via {@link #useRemoveInsteadOfUnvisibleWorkaround}.
	 * 
	 * @param coolBarManager The {@link ICoolBarManager} where the contributions of this registry should be removed from.
	 */
	public void removeAllFromCoolBar(ICoolBarManager coolBarManager) {
		IContributionManager coolBarContributionManager = ((SubCoolBarManager)coolBarManager).getParent();
		if (!useRemoveInsteadOfUnvisibleWorkaround)
			((SubCoolBarManager)coolBarManager).setVisible(false);
		
		String baseID = this.getClass().getName();
		String orphanageToolbarID = baseID + '.' + ORPHANAGE_TOOLBAR_ID;
		// We use a temporary MenuManager which will be translated into the real
		// coolbar afterwards.
		MenuManager tmpMenu = new MenuManager();
		contribute(tmpMenu, KIND_COOLBAR);

		// convert the existing items of the real coolbar-manager into a Map - the new items might
		// already exist because of Eclipse's workspace memory (and then the old ones need to be
		// manipulated - new ones would be ignored because of a bug/feature in the EclipseRCP)
		IContributionItem[] coolBarItems = ((SubCoolBarManager)coolBarManager).getParent().getItems();
	
		// key: String itemId
		// value: IXContributionItem
		Map<String, IContributionItem> coolBarItemMap = new HashMap<String, IContributionItem>(coolBarItems.length);
		for (int i = 0; i < coolBarItems.length; ++i) {
			IContributionItem coolBarItem = coolBarItems[i];
			coolBarItemMap.put(coolBarItem.getId(), coolBarItem);
			System.out.println("Having "+coolBarItem.getId()+" in CoolBar"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		ToolBarContributionItem orphanageToolBarContributionItem = getToolBarContributionItem(coolBarItemMap.get(orphanageToolbarID));
		if (orphanageToolBarContributionItem != null) {
			IContributionItem item = coolBarContributionManager.find(orphanageToolBarContributionItem.getId());
			if (item != null) {
				if (useRemoveInsteadOfUnvisibleWorkaround) {
					coolBarContributionManager.remove(orphanageToolBarContributionItem.getId());
				}
				else {
					orphanageToolBarContributionItem.setVisible(false);
					item.setVisible(false);
				}
			}
		}
		
		// Now, we iterate all the "precompiled" items and contribute them to the coolbar
		IContributionItem[] tmpItems = tmpMenu.getItems();
		for (int i = 0; i < tmpItems.length; ++i) {
			IContributionItem tmpItem = tmpItems[i];

			// Test for items that are already in the parent
			if (tmpItem instanceof IMenuManager) {
				IMenuManager tmpSubMenu = (IMenuManager) tmpItem;
				String tmpSubMenuID = baseID + '.' + tmpSubMenu.getId();

				ToolBarContributionItem toolBarContributionItem = getToolBarContributionItem(coolBarItemMap.get(tmpSubMenuID));
				if (toolBarContributionItem != null) {
					IContributionItem item = coolBarContributionManager.find(toolBarContributionItem.getId());
					if (item != null) {
						if (useRemoveInsteadOfUnvisibleWorkaround) {
							coolBarContributionManager.remove(tmpSubMenuID);					
						}
						else {
							toolBarContributionItem.setVisible(false);
							item.setVisible(false);
						}
					}
				}
			}
		} 
		coolBarContributionManager.update(true);
	}
	
	/**
	 * @param coolBarManager
	 * @return Returns the number of visible items (i.e. actions) that have been added (because some
	 *		might be invisible and therefore not added).
	 */
	public int contributeToCoolBar(ICoolBarManager coolBarManager)
	{
		if (!perspectiveListenerAdded)
			earlyContributed = true;
		
		if (coolBarManager instanceof SubContributionManager) 
			((SubCoolBarManager)coolBarManager).setVisible(true);
			
		String baseID = this.getClass().getName();
		String orphanageToolbarID = baseID + '.' + ORPHANAGE_TOOLBAR_ID;

		// We use a temporary MenuManager which will be translated into the real
		// coolbar afterwards.
		MenuManager tmpMenu = new MenuManager();
		int res = contribute(tmpMenu, KIND_COOLBAR);

		// convert the existing items of the real coolbar-manager into a Map - the new items might
		// already exist because of Eclipse's workspace memory (and then the old ones need to be
		// manipulated - new ones would be ignored because of a bug/feature in the EclipseRCP)
		IContributionItem[] coolBarItems = null;
		if (coolBarManager instanceof SubCoolBarManager) {
			coolBarItems = ((SubCoolBarManager)coolBarManager).getParent().getItems();
		}
		else if (coolBarManager instanceof IContributionManager) {
			coolBarItems = ((IContributionManager)coolBarManager).getItems();
		}
//			IContributionItem[] coolBarItems = ((SubCoolBarManager)coolBarManager).getParent().getItems();

		// key: String itemId
		// value: IXContributionItem
		Map<String, IContributionItem> coolBarItemMap = new HashMap<String, IContributionItem>(coolBarItems.length);
		for (int i = 0; i < coolBarItems.length; ++i) {
			IContributionItem coolBarItem = coolBarItems[i];
			coolBarItemMap.put(coolBarItem.getId(), coolBarItem);
		}

		// Clean the "orphanage" toolbar which holds all those actions, that are not wrapped by a menu.
		// This toolbar does only exist, if it is needed.
		ToolBarContributionItem orphanageToolBarContributionItem = getToolBarContributionItem(coolBarItemMap.get(orphanageToolbarID));
		if (orphanageToolBarContributionItem!= null)
			orphanageToolBarContributionItem.getToolBarManager().removeAll();

		// We need to collect all the "orphaned" actions in a menu first and at them after all the other menus.
		MenuManager orphanageMenu = new MenuManager();

		// Now, we iterate all the "precompiled" items and contribute them to the coolbar
		IContributionItem[] tmpItems = tmpMenu.getItems();
		for (int i = 0; i < tmpItems.length; ++i) {
			IContributionItem tmpItem = tmpItems[i];

			// Either, we've hit a menu or an "orphaned" item
			if (tmpItem instanceof IMenuManager) {
				IMenuManager tmpSubMenu = (IMenuManager) tmpItem;
				String tmpSubMenuID = baseID + '.' + tmpSubMenu.getId();

				// find the previously existing ToolBarManager or create a new one.
				IToolBarManager toolBarManager;
				ToolBarContributionItem toolBarContributionItem = getToolBarContributionItem(coolBarItemMap.get(tmpSubMenuID));
				if (toolBarContributionItem != null)
					toolBarManager = toolBarContributionItem.getToolBarManager();
				else {
					toolBarManager = new ToolBarManager();
					toolBarContributionItem = new ToolBarContributionItem(toolBarManager, tmpSubMenuID);
					coolBarManager.add(toolBarContributionItem);
				}

				toolBarManager.removeAll();
				addFlattenedMenu(toolBarManager, tmpSubMenuID + ".separator", tmpSubMenu); //$NON-NLS-1$
			}
			else {
				orphanageMenu.add(tmpItem);
			}
		} // for (int i = 0; i < tmpItems.length; ++i) {

		// If we have actions without menus, populate a toolbar with them.
		if (!orphanageMenu.isEmpty()) {
			IToolBarManager toolBarManager;
			orphanageToolBarContributionItem = getToolBarContributionItem(coolBarItemMap.get(orphanageToolbarID));
			if (orphanageToolBarContributionItem != null)
				toolBarManager = orphanageToolBarContributionItem.getToolBarManager();
			else {
				toolBarManager = new ToolBarManager();
				orphanageToolBarContributionItem = new ToolBarContributionItem(toolBarManager, orphanageToolbarID);
				coolBarManager.add(orphanageToolBarContributionItem);
			}

			toolBarManager.removeAll();
			addFlattenedMenu(toolBarManager, orphanageToolbarID + ".separator", orphanageMenu); //$NON-NLS-1$
		}

		coolBarManager.update(true);

		return res;			
//		} 
//		else {
//			return contribute(coolBarManager, KIND_COOLBAR);
//		}
	}

	protected static ToolBarContributionItem getToolBarContributionItem(Object item)
	{
		if (item == null)
			return null;

		if (item instanceof ToolBarContributionItem)
			return (ToolBarContributionItem) item;
		else if (item instanceof SubContributionItem)
			return (ToolBarContributionItem) ((SubContributionItem)item).getInnerItem();
		else {
			String itemID;
			if (item instanceof IContributionItem)
				itemID = ((IContributionItem)item).getId();
			else
				itemID = "{unknown}"; //$NON-NLS-1$

			throw new IllegalStateException("Item with id=" + itemID + " should be an instance of ToolBarContributionItem or Sub, but is of type " + item.getClass().getName() + "! Item: " + item); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	private static final String ORPHANAGE_TOOLBAR_ID = "orphanage"; //$NON-NLS-1$

	/**
	 * @param manager The manager in which to search.
	 * @param path A slash ('/') separated path or <code>null</code>. If <code>path</code> is
	 *		<code>null</code>, this method will always return <code>null</code>.
	 * @return Returns the contribution item specified by path or <code>null</code>, if nothing
	 *		could be found.
	 */
	protected static IContributionItem findUsingPath(IContributionManager manager, String path)
	{
		if (path == null)
			return null;

		if (manager instanceof IMenuManager)
			return ((IMenuManager)manager).findUsingPath(path);

		String[] parts = path.split("/"); //$NON-NLS-1$
		IContributionManager mgr = manager;
		for (int i = 0; i < parts.length; ++i) {
			String id = parts[i];
			if (mgr != null) 
			{
				IContributionItem item = mgr.find(id);
				if (i == parts.length - 1)
					return item;
				
				if (item instanceof IMenuManager) {
					StringBuffer sbSubPath = new StringBuffer();
					for (int n = i+1; n < parts.length; ++n) {
						if (sbSubPath.length() != 0)
							sbSubPath.append('/');
						sbSubPath.append(parts[n]);
					}
					return ((IMenuManager)item).findUsingPath(sbSubPath.toString());
				}

				if (item instanceof IContributionManager)
					mgr = (IContributionManager) item;
				else
					return null;				
			}
		}

		return null;
	}

	protected static void addFlattenedMenu(IContributionManager dest, String parentID, IMenuManager menu)
	{
		String id = menu.getId();
		String sepName = (parentID == null || "".equals(parentID)) ? id : (parentID + '.' + id); //$NON-NLS-1$
		dest.add(new Separator(sepName));
		IContributionItem[] items = menu.getItems();
		for (int i = 0; i < items.length; ++i) {
			IContributionItem item = items[i];
			if (item instanceof IMenuManager) {
				String itemId = item.getId();
				String newParentID = (parentID == null || "".equals(parentID)) ? itemId : (parentID + '.' + itemId); //$NON-NLS-1$
				addFlattenedMenu(dest, newParentID, (IMenuManager)item);
			}
			else
				dest.add(item);
		}
	}

	public AbstractActionRegistry() 
	{
		super();
//		if (isAffectedOfPerspectiveExtension()) {
//			RCPUtil.getActiveWorkbenchWindow().addPerspectiveListener(perspectiveListener);
//		}
	}
	
//	/**
//	 * This method contributes WITHOUT removing items before.
//	 * 
//	 * @param contributionManager
//	 * @param kind
//	 * @return Returns the number of visible contribution items (i.e. actions) that have been added.
//	 */
//	protected int contribute(IContributionManager contributionManager, String kind)
//	{
//			int visibleContributionItemCount = 0;
//
//			List menuSorted;
//			if (KIND_MENUBAR.equals(kind))
//				menuSorted = menuSortedForMenubar;
//			else if (KIND_CONTEXTMENU.equals(kind))
//				menuSorted = menuSortedForContextmenu;
//			else if (KIND_TOOLBAR.equals(kind))
//				menuSorted = menuSortedForToolbar;
//			else if (KIND_COOLBAR.equals(kind))
//				menuSorted = menuSortedForToolbar;
//			else
//				throw new IllegalArgumentException("kind \"" + kind + "\" invalid!");
//
//			boolean firstRun = menuSorted == null;
//			LinkedList menuRaw = null;
//			int lastMenuRawSize = 0;
//			if (firstRun) {
//				menuRaw = new LinkedList(this.menuRaw);
//				lastMenuRawSize = menuRaw.size();
//				menuSorted = new LinkedList();
//			}
//			
////			contributionManager.removeAll();
//			
//			while ((firstRun && !menuRaw.isEmpty()) || !firstRun) {
//				for (Iterator itTopLevel = (firstRun ? menuRaw : menuSorted).iterator(); itTopLevel.hasNext(); ) {
//					ItemDescriptor item = (ItemDescriptor) itTopLevel.next();
//					if (item instanceof ActionDescriptor) 
//					{
//						ActionDescriptor ad = (ActionDescriptor) item;
//						String path;
//						if (KIND_MENUBAR.equals(kind))
//							path = ad.getMenubarPath();
//						else if (KIND_CONTEXTMENU.equals(kind))
//							path = ad.getContextmenuPath();
//						else if (KIND_TOOLBAR.equals(kind))
//							path = ad.getToolbarPath();
//						else if (KIND_COOLBAR.equals(kind))
//							path = ad.getToolbarPath();
//						else
//							throw new IllegalArgumentException("kind \"" + kind + "\" invalid!");
//
//						IContributionItem anchor = path == null ? null : findUsingPath(contributionManager, path);
//						if (anchor != null) {
//							boolean visible = ad.isVisible();
//							if (visible) {
//								if (KIND_MENUBAR.equals(kind))
//									visible = ad.isVisibleInMenubar();
//								else if (KIND_CONTEXTMENU.equals(kind))
//									visible = ad.isVisibleInContextmenu();
//								else if (KIND_TOOLBAR.equals(kind))
//									visible = ad.isVisibleInToolbar();
//								else if (KIND_COOLBAR.equals(kind))
//									visible = ad.isVisibleInToolbar();
//								else
//									throw new IllegalArgumentException("kind \"" + kind + "\" invalid!");
//							}
//
//							if (visible) {
//								++visibleContributionItemCount;
//								if (ad.getAction() != null)
//									((ContributionItem)anchor).getParent().insertAfter(anchor.getId(), ad.getAction());
//								else if (ad.getContributionItem() != null)
//									((ContributionItem)anchor).getParent().insertAfter(anchor.getId(), ad.getContributionItem());
//							}
//
//							if (firstRun) {
//								menuSorted.add(item);
//								itTopLevel.remove();
//							}
//						}
//					}
//					else if (item instanceof SeparatorDescriptor) {
//						SeparatorDescriptor s = (SeparatorDescriptor) item;
//						String path = s.getPath();
//						IContributionItem anchor = path == null ? null : findUsingPath(contributionManager, path);
//						if (path == null || anchor != null) {
//							Separator separator = new Separator(s.getName());
//							if (anchor == null)
//								contributionManager.add(separator);
//							else
//								((ContributionItem)anchor).getParent().insertAfter(anchor.getId(), separator);
//
//							if (firstRun) {
//								menuSorted.add(item);
//								itTopLevel.remove();
//							}
//						}
//					}
//					else if (item instanceof GroupMarkerDescriptor) {
//						GroupMarkerDescriptor gm = (GroupMarkerDescriptor) item;
//						String path = gm.getPath();
//						IContributionItem anchor = path == null ? null : findUsingPath(contributionManager, path);
//						if (path == null || anchor != null) {
//							GroupMarker groupMarker = new GroupMarker(gm.getName());
//							if (anchor == null)
//								contributionManager.add(groupMarker);
//							else
//								((ContributionItem)anchor).getParent().insertAfter(anchor.getId(), groupMarker);
//
//							if (firstRun) {
//								menuSorted.add(item);
//								itTopLevel.remove();
//							}
//						}
//					}
//					else if (item instanceof MenuDescriptor) {
//						MenuDescriptor md = (MenuDescriptor) item;
//						String path = md.getPath();
//						IContributionItem anchor = path == null ? null : findUsingPath(contributionManager, path);
//						if (path == null || anchor != null) {
//							MenuManager subMenu = new MenuManager(md.getLabel(), md.getId());
//
//							for (Iterator itSub = md.getSubItems().iterator(); itSub.hasNext(); ) {
//								Object obj = itSub.next();
//								if (obj instanceof SeparatorDescriptor) {
//									SeparatorDescriptor separator = (SeparatorDescriptor) obj;
//									subMenu.add(new Separator(separator.getName()));
//								}
//								else if (obj instanceof GroupMarkerDescriptor) {
//									GroupMarkerDescriptor groupMarker = (GroupMarkerDescriptor) obj;
//									subMenu.add(new GroupMarker(groupMarker.getName()));
//								}
//								else
//									throw new IllegalStateException("SubItem of menu is neither a SeparatorDescriptor nor a GroupMarkerDescriptor but " + obj.getClass().getName());
//							}
//
//							if (anchor == null)
//								contributionManager.add(subMenu);
//							else
//								((ContributionItem)anchor).getParent().insertAfter(anchor.getId(), subMenu);
//
//							if (firstRun) {
//								menuSorted.add(item);
//								itTopLevel.remove();
//							}
//						}
//					}
//					else
//						throw new IllegalStateException("Item in menuRaw of type " + item.getClass() + " is an instance of an unknown class!");
//				}
//
//				if (firstRun && (lastMenuRawSize == menuRaw.size())) {
//					if (logger.isDebugEnabled()) {
//						logger.debug("Could not add the following contributions to the menu (kind " + kind + "):");
//						for (Iterator it = menuRaw.iterator(); it.hasNext(); ) {
//							ItemDescriptor item = (ItemDescriptor) it.next();
//							if (item instanceof ActionDescriptor) {
//								logger.debug("    Action with id=" + ((ActionDescriptor)item).getAction().getId());
//							}
//							else if (item instanceof MenuDescriptor) {
//								logger.debug("    Menu with id=" + ((MenuDescriptor)item).getId());
//							}
//							else if (item instanceof SeparatorDescriptor) {
//								logger.debug("    Separator with name=" + ((SeparatorDescriptor)item).getName());
//							}
//							else if (item instanceof GroupMarkerDescriptor) {
//								logger.debug("    GroupMarker with name=" + ((GroupMarkerDescriptor)item).getName());
//							}
//							else
//								logger.debug("    " + item);
//						}
//					}
//					break;
//				}
//
//				if (!firstRun)
//					break;
//
//				lastMenuRawSize = menuRaw.size();
//			} // while ((firstRun && !menuRaw.isEmpty()) || !firstRun) {
//
//			if (firstRun) {
//				if (KIND_MENUBAR.equals(kind))
//					menuSortedForMenubar = menuSorted;
//				else if (KIND_CONTEXTMENU.equals(kind))
//					menuSortedForContextmenu = menuSorted;
//				else if (KIND_TOOLBAR.equals(kind))
//					menuSortedForToolbar = menuSorted;
//				else if (KIND_COOLBAR.equals(kind))
//					menuSortedForToolbar = menuSorted;
//				else
//					throw new IllegalArgumentException("kind \"" + kind + "\" invalid!");
//			}
//
//			// flatten the menus if we're contributing to the toolbar (which doesn't understand sub-menus)
//			// the coolbar is handled by contributeToCoolBar(...) directly
//			if (KIND_TOOLBAR.equals(kind)) {
//				IContributionItem[] items = contributionManager.getItems();
//				contributionManager.removeAll();
//				for (int i = 0; i < items.length; ++i) {
//					IContributionItem item = items[i];
//					if (item instanceof IMenuManager)
//						addFlattenedMenu(contributionManager, null, (IMenuManager)item);
//					else
//						contributionManager.add(item);
//				}
//			}
//			contributionManager.update(true);
//			return visibleContributionItemCount;			
//	}

	/**
	 * This method contributes WITHOUT removing items before.
	 * 
	 * @param contributionManager
	 * @param kind
	 * @return Returns the number of visible contribution items (i.e. actions) that have been added.
	 */
	protected int contribute(IContributionManager contributionManager, String kind)
	{
		if (!perspectiveListenerAdded)
			earlyContributed = true;
		
			int visibleContributionItemCount = 0;

			List<ItemDescriptor> menuSorted;
			if (KIND_MENUBAR.equals(kind))
				menuSorted = menuSortedForMenubar;
			else if (KIND_CONTEXTMENU.equals(kind))
				menuSorted = menuSortedForContextmenu;
			else if (KIND_TOOLBAR.equals(kind))
				menuSorted = menuSortedForToolbar;
			else if (KIND_COOLBAR.equals(kind))
				menuSorted = menuSortedForToolbar;
			else
				throw new IllegalArgumentException("kind \"" + kind + "\" invalid!"); //$NON-NLS-1$ //$NON-NLS-2$

			boolean firstRun = menuSorted == null;
			LinkedList<ItemDescriptor> menuRaw = null;
			int lastMenuRawSize = 0;
			if (firstRun) {
				menuRaw = new LinkedList<ItemDescriptor>(this.menuRaw);
				lastMenuRawSize = menuRaw.size();
				menuSorted = new LinkedList<ItemDescriptor>();
			}
			
//			contributionManager.removeAll();
			
			while ((firstRun && !menuRaw.isEmpty()) || !firstRun) {
				for (Iterator itTopLevel = (firstRun ? menuRaw : menuSorted).iterator(); itTopLevel.hasNext(); ) {
					ItemDescriptor item = (ItemDescriptor) itTopLevel.next();
					if (item instanceof ActionDescriptor) 
					{
						ActionDescriptor ad = (ActionDescriptor) item;
						
						if (getActiveExtensionIDs().contains(ad.getID())) 
						{
							String path;
							if (KIND_MENUBAR.equals(kind))
								path = ad.getMenubarPath();
							else if (KIND_CONTEXTMENU.equals(kind))
								path = ad.getContextmenuPath();
							else if (KIND_TOOLBAR.equals(kind))
								path = ad.getToolbarPath();
							else if (KIND_COOLBAR.equals(kind))
								path = ad.getToolbarPath();
							else
								throw new IllegalArgumentException("kind \"" + kind + "\" invalid!"); //$NON-NLS-1$ //$NON-NLS-2$

							IContributionItem anchor = path == null ? null : findUsingPath(contributionManager, path);
							if (anchor != null) {
								boolean visible = ad.isVisible();
								if (visible) {
									if (KIND_MENUBAR.equals(kind))
										visible = ad.isVisibleInMenubar();
									else if (KIND_CONTEXTMENU.equals(kind))
										visible = ad.isVisibleInContextmenu();
									else if (KIND_TOOLBAR.equals(kind))
										visible = ad.isVisibleInToolbar();
									else if (KIND_COOLBAR.equals(kind))
										visible = ad.isVisibleInToolbar();
									else
										throw new IllegalArgumentException("kind \"" + kind + "\" invalid!"); //$NON-NLS-1$ //$NON-NLS-2$
								}

								if (visible) {
									++visibleContributionItemCount;
									if (anchor instanceof MenuManager) {
										if (ad.getAction() != null)
											((MenuManager)anchor).add(ad.getAction());
										else if (ad.getContributionItem() != null)
											((MenuManager)anchor).add(ad.getContributionItem());
									} else {
										if (ad.getAction() != null)
											((ContributionItem)anchor).getParent().insertAfter(anchor.getId(), ad.getAction());
										else if (ad.getContributionItem() != null)
											((ContributionItem)anchor).getParent().insertAfter(anchor.getId(), ad.getContributionItem());
									}
								}

								if (firstRun) {
									menuSorted.add(item);
									itTopLevel.remove();
								}
							}
						} // if (getActiveExtensionIDs().contains(ad.getID()))
					}
					else if (item instanceof SeparatorDescriptor) {
						SeparatorDescriptor s = (SeparatorDescriptor) item;
						String path = s.getPath();
						IContributionItem anchor = path == null ? null : findUsingPath(contributionManager, path);
						if (path == null || anchor != null) {
							Separator separator = new Separator(s.getName());
							if (anchor == null)
								contributionManager.add(separator);
							else
								((ContributionItem)anchor).getParent().insertAfter(anchor.getId(), separator);

							if (firstRun) {
								menuSorted.add(item);
								itTopLevel.remove();
							}
						}
					}
					else if (item instanceof GroupMarkerDescriptor) {
						GroupMarkerDescriptor gm = (GroupMarkerDescriptor) item;
						String path = gm.getPath();
						IContributionItem anchor = path == null ? null : findUsingPath(contributionManager, path);
						if (path == null || anchor != null) {
							GroupMarker groupMarker = new GroupMarker(gm.getName());
							if (anchor == null)
								contributionManager.add(groupMarker);
							else
								((ContributionItem)anchor).getParent().insertAfter(anchor.getId(), groupMarker);

							if (firstRun) {
								menuSorted.add(item);
								itTopLevel.remove();
							}
						}
					}
					else if (item instanceof MenuDescriptor) {
						MenuDescriptor md = (MenuDescriptor) item;
						String path = md.getPath();
						IContributionItem anchor = path == null ? null : findUsingPath(contributionManager, path);
						if (path == null || anchor != null) {
							MenuManager subMenu = new MenuManager(md.getLabel(), md.getId());

							for (Iterator itSub = md.getSubItems().iterator(); itSub.hasNext(); ) {
								Object obj = itSub.next();
								if (obj instanceof SeparatorDescriptor) {
									SeparatorDescriptor separator = (SeparatorDescriptor) obj;
									subMenu.add(new Separator(separator.getName()));
								}
								else if (obj instanceof GroupMarkerDescriptor) {
									GroupMarkerDescriptor groupMarker = (GroupMarkerDescriptor) obj;
									subMenu.add(new GroupMarker(groupMarker.getName()));
								}
								else
									throw new IllegalStateException("SubItem of menu is neither a SeparatorDescriptor nor a GroupMarkerDescriptor but " + obj.getClass().getName()); //$NON-NLS-1$
							}

							if (anchor == null)
								contributionManager.add(subMenu);
							else
								((ContributionItem)anchor).getParent().insertAfter(anchor.getId(), subMenu);

							if (firstRun) {
								menuSorted.add(item);
								itTopLevel.remove();
							}
						}
					}
					else
						throw new IllegalStateException("Item in menuRaw of type " + item.getClass() + " is an instance of an unknown class!"); //$NON-NLS-1$ //$NON-NLS-2$
				}

				if (firstRun && (lastMenuRawSize == menuRaw.size())) {
					if (logger.isDebugEnabled()) {
						logger.debug("Could not add the following contributions to the menu (kind " + kind + "):"); //$NON-NLS-1$ //$NON-NLS-2$
						for (Iterator it = menuRaw.iterator(); it.hasNext(); ) {
							ItemDescriptor item = (ItemDescriptor) it.next();
							if (item instanceof ActionDescriptor) {
								logger.debug("    Action with id=" + ((ActionDescriptor)item).getID()); //$NON-NLS-1$
							}
							else if (item instanceof MenuDescriptor) {
								logger.debug("    Menu with id=" + ((MenuDescriptor)item).getId()); //$NON-NLS-1$
							}
							else if (item instanceof SeparatorDescriptor) {
								logger.debug("    Separator with name=" + ((SeparatorDescriptor)item).getName()); //$NON-NLS-1$
							}
							else if (item instanceof GroupMarkerDescriptor) {
								logger.debug("    GroupMarker with name=" + ((GroupMarkerDescriptor)item).getName()); //$NON-NLS-1$
							}
							else
								logger.debug("    " + item); //$NON-NLS-1$
						}
					}
					break;
				}

				if (!firstRun)
					break;

				lastMenuRawSize = menuRaw.size();
			} // while ((firstRun && !menuRaw.isEmpty()) || !firstRun) {

			if (firstRun) {
				if (KIND_MENUBAR.equals(kind))
					menuSortedForMenubar = menuSorted;
				else if (KIND_CONTEXTMENU.equals(kind))
					menuSortedForContextmenu = menuSorted;
				else if (KIND_TOOLBAR.equals(kind))
					menuSortedForToolbar = menuSorted;
				else if (KIND_COOLBAR.equals(kind))
					menuSortedForToolbar = menuSorted;
				else
					throw new IllegalArgumentException("kind \"" + kind + "\" invalid!"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			// flatten the menus if we're contributing to the toolbar (which doesn't understand sub-menus)
			// the coolbar is handled by contributeToCoolBar(...) directly
			if (KIND_TOOLBAR.equals(kind)) {
				IContributionItem[] items = contributionManager.getItems();
				contributionManager.removeAll();
				for (int i = 0; i < items.length; ++i) {
					IContributionItem item = items[i];
					if (item instanceof IMenuManager)
						addFlattenedMenu(contributionManager, null, (IMenuManager)item);
					else
						contributionManager.add(item);
				}
			}
			contributionManager.update(true);
			return visibleContributionItemCount;			
	}
	
	/**
	 * You are free to name the action element as you desire. It's a good idea though, to name
	 * it somewhat ending on "Action" (e.g. mySpecialAction) - or simply "action".
	 * <p>
	 * The default implementation of this method returns "action".
	 * </p>
	 *
	 * @return Returns the name of the action element. Default is "action".
	 */
	protected String getActionElementName()
	{
		return "action"; //$NON-NLS-1$
	}

	/**
	 * In your extension of <code>AbstractActionRegistry</code>, you must create
	 * your actions yourself in order to define what shall be done in the {@link IAction#run()}
	 * method.
	 * <p>
	 * You should not do any initialization here, but use {@link #initAction(IAction, IExtension, IConfigurationElement)}
	 * or {@link #initContributionItem(IXContributionItem, IExtension, IConfigurationElement)}
	 * instead.
	 * </p>
	 *
	 * @param extension The extension which contributes the action.
	 * @param element The current element (which matches {@link #getActionElementName()}).
	 * @return Returns a new instance of {@link IAction} or {@link IXContributionItem}.
	 * @throws EPProcessorException If the element's attributes are not correct or another problem occurs.
	 */
	protected abstract Object createActionOrContributionItem(IExtension extension, IConfigurationElement element) throws EPProcessorException;

	/**
	 * This method is called after all the general properties of the new action have been set. It is NOT called,
	 * if {@link #createActionOrContributionItem(IExtension, IConfigurationElement)} returned an
	 * {@link IXContributionItem}. Override it, if you want to initialize your action.
	 *
	 * @param action The action as it has been created before by {@link #createActionOrContributionItem(IExtension, IConfigurationElement)}.
	 * @param extension The extension which contributes the action.
	 * @param element The current element (which matches {@link #getActionElementName()}).
	 * @throws EPProcessorException If the element's attributes are not correct or another problem occurs.
	 */
	protected void initAction(IAction action, IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
	}

	/**
	 * This method is called after all the general properties of the new ContributionItem have been set.
	 * It is NOT called, if {@link #createActionOrContributionItem(IExtension, IConfigurationElement)}
	 * returned an {@link IAction}. Override it, if you want to initialize your contribution item.
	 *
	 * @param contributionItem The contributionItem as it has been created before by {@link #createActionOrContributionItem(IExtension, IConfigurationElement)}.
	 * @param extension The extension which contributes the action.
	 * @param element The current element (which matches {@link #getActionElementName()}).
	 * @throws EPProcessorException If the element's attributes are not correct or another problem occurs.
	 */
	protected void initContributionItem(IXContributionItem contributionItem, IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
	}

	private String elementNameAction = null;
	protected static final String ELEMENT_NAME_SEPARATOR = "separator"; //$NON-NLS-1$
	protected static final String ELEMENT_NAME_GROUP_MARKER = "groupMarker"; //$NON-NLS-1$
	protected static final String ELEMENT_NAME_MENU = "menu"; //$NON-NLS-1$

	protected static final String ATTRIBUTE_NAME_SEPARATOR_NAME = "name"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_SEPARATOR_PATH = "path"; //$NON-NLS-1$

	protected static final String ATTRIBUTE_NAME_GROUP_MARKER_NAME = "name"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_GROUP_MARKER_PATH = "path"; //$NON-NLS-1$

	protected static final String ATTRIBUTE_NAME_MENU_ID = "id"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_MENU_LABEL = "label"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_MENU_PATH = "path"; //$NON-NLS-1$

	protected static final String ATTRIBUTE_NAME_ACTION_ID = "id"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_ACTION_NAME = "name"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_ACTION_TOOLTIP = "tooltip"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_ACTION_ICON = "icon"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_ACTION_DISABLED_ICON = "disabledIcon"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_ACTION_HOVER_ICON = "hoverIcon"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_ACTION_MENUBAR_PATH = "menubarPath"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_ACTION_TOOLBAR_PATH = "toolbarPath"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_ACTION_CONTEXTMENU_PATH = "contextmenuPath"; //$NON-NLS-1$
	
	protected static final String ATTRIBUTE_NAME_ACTION_VISIBLE = "visible"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_ACTION_VISIBLE_IN_MENUBAR = "visibleInMenubar"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_ACTION_VISIBLE_IN_TOOLBAR = "visibleInToolbar"; //$NON-NLS-1$
	protected static final String ATTRIBUTE_NAME_ACTION_VISIBLE_IN_CONTEXTMENU = "visibleInContextmenu"; //$NON-NLS-1$

	public synchronized void process()
	{
//		if (elementNameAction == null)
//			elementNameAction = getActionElementName();
		super.process();

		if (isAffectedOfPerspectiveExtension()) {
			checkPerspectiveListenerAdded();
		}
	}

	public boolean checkPerspectiveListenerAdded() 
	{
		if (!perspectiveListenerAdded) {			
			if (RCPUtil.getActiveWorkbenchWindow() != null) {
				RCPUtil.getActiveWorkbenchWindow().addPerspectiveListener(perspectiveListener);
				perspectiveListenerAdded = true;
				// if there were early contributions, before a perspective listener could be added
				// also display the necessary items
				if (earlyContributed) {
					if (RCPUtil.getActiveWorkbenchWindow().getActivePage() != null)
						perspectiveChange(RCPUtil.getActiveWorkbenchWindow().getActivePage().getPerspective());
				}
			}
		}
		return perspectiveListenerAdded;
	}
	
//	protected boolean checkEarlyContribution() {
//		if (!perspectiveListenerAdded)
//			earlyContributed = true;			
//	}
	
	/**
	 * Override this method if you need an extended <code>ActionDescriptor</code>. Note, that you
	 * cannot initialize it here, but you should then additionally extend the method
	 * {@link #initActionDescriptor(ActionDescriptor, IExtension, IConfigurationElement)}.
	 *
	 * @return Returns a newly created instance of <code>ActionDescriptor</code>.
	 */
	protected ActionDescriptor createActionDescriptor()
	{
		return new ActionDescriptor();
	}

	/**
	 * Extend this method to set additional properties of your <code>ActionDescriptor</code>.
	 * This method is called, after the basic properties of {@link ActionDescriptor} have already
	 * been initialized.
	 * <p>
	 * Though the basic implementation of this method is empty, it is still a good idea, to
	 * call <code>super.initActionDescriptor(...)</code> before doing your
	 * own initialization.
	 * </p>
	 *
	 * @param actionDescriptor The <code>ActionDescriptor</code> that was previously created by {@link #createActionDescriptor()}.
	 * @param extension The extension that is contributing the current action.
	 * @param element The current element (which is matching {@link #getActionElementName()}).
	 * @throws EPProcessorException If the element's attributes are not correct or another problem occurs.
	 */
	protected void initActionDescriptor(ActionDescriptor actionDescriptor, IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
	}

	public void processElement(IExtension extension, IConfigurationElement element)
			throws Exception
	{
		if (elementNameAction == null)
			elementNameAction = getActionElementName();
		
		String elementName = element.getName();
		if (ELEMENT_NAME_SEPARATOR.equals(elementName)) {
			String name = element.getAttribute(ATTRIBUTE_NAME_SEPARATOR_NAME);
			String path = element.getAttribute(ATTRIBUTE_NAME_SEPARATOR_PATH);
			menuRaw.add(new SeparatorDescriptor(name, path));
		}
		else if (ELEMENT_NAME_GROUP_MARKER.equals(elementName)) {
			String name = element.getAttribute(ATTRIBUTE_NAME_GROUP_MARKER_NAME);
			String path = element.getAttribute(ATTRIBUTE_NAME_GROUP_MARKER_PATH);
			menuRaw.add(new GroupMarkerDescriptor(name, path));
		}
		else if (ELEMENT_NAME_MENU.equals(elementName)) {
			String id = element.getAttribute(ATTRIBUTE_NAME_MENU_ID);
			String label = element.getAttribute(ATTRIBUTE_NAME_MENU_LABEL);
			String path = element.getAttribute(ATTRIBUTE_NAME_MENU_PATH);
			if ("".equals(path)) //$NON-NLS-1$
				path = null;

			MenuDescriptor menuDescriptor = new MenuDescriptor(id, label, path);
			menuRaw.add(menuDescriptor);

//			String separatorName = "";
//			String groupMarkerName = "";			
			IConfigurationElement[] children = element.getChildren();
			for (int i = 0; i < children.length; ++i) {
				IConfigurationElement child = children[i];
				if (ELEMENT_NAME_SEPARATOR.equals(child.getName())) {
					String name = child.getAttribute(ATTRIBUTE_NAME_SEPARATOR_NAME);
					if (child.getAttribute(ATTRIBUTE_NAME_SEPARATOR_PATH) != null)
						logger.warn("There is a separator's path specified within a menu. This path will be ignored! You should not specify a path when using a separator inside of a menu! plugin=" + extension.getNamespaceIdentifier()+ " extension-point=" + getExtensionPointID()); //$NON-NLS-1$ //$NON-NLS-2$
					menuDescriptor.addSubItem(new SeparatorDescriptor(name));
				}
				else if (ELEMENT_NAME_GROUP_MARKER.equals(child.getName())) {
					String name = child.getAttribute(ATTRIBUTE_NAME_GROUP_MARKER_NAME);
					if (child.getAttribute(ATTRIBUTE_NAME_GROUP_MARKER_PATH) != null)
						logger.warn("There is a group-marker's path specified within a menu. This path will be ignored! You should not specify a path when using a group-marker inside of a menu! plugin=" + extension.getNamespaceIdentifier()+ " extension-point=" + getExtensionPointID()); //$NON-NLS-1$ //$NON-NLS-2$
					menuDescriptor.addSubItem(new GroupMarkerDescriptor(name));
				}
				else
					throw new IllegalArgumentException("childElement.name=\"" + child.getName() + "\" of menu \"" + id + "\" is unknown!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		else if (elementNameAction.equals(elementName)) {
			String id = element.getAttribute(ATTRIBUTE_NAME_ACTION_ID);
			String name = element.getAttribute(ATTRIBUTE_NAME_ACTION_NAME);
			String tooltip = element.getAttribute(ATTRIBUTE_NAME_ACTION_TOOLTIP);
			String icon = element.getAttribute(ATTRIBUTE_NAME_ACTION_ICON);
			String disabledIcon = element.getAttribute(ATTRIBUTE_NAME_ACTION_DISABLED_ICON);
			String hoverIcon = element.getAttribute(ATTRIBUTE_NAME_ACTION_HOVER_ICON);
			String menubarPath = element.getAttribute(ATTRIBUTE_NAME_ACTION_MENUBAR_PATH);
			String toolbarPath = element.getAttribute(ATTRIBUTE_NAME_ACTION_TOOLBAR_PATH);
			String contextmenuPath = element.getAttribute(ATTRIBUTE_NAME_ACTION_CONTEXTMENU_PATH);
			String visible = element.getAttribute(ATTRIBUTE_NAME_ACTION_VISIBLE);
			String visibleInMenubar = element.getAttribute(ATTRIBUTE_NAME_ACTION_VISIBLE_IN_MENUBAR);
			String visibleInToolbar = element.getAttribute(ATTRIBUTE_NAME_ACTION_VISIBLE_IN_TOOLBAR);
			String visibleInContextmenu = element.getAttribute(ATTRIBUTE_NAME_ACTION_VISIBLE_IN_CONTEXTMENU);

			Object actionOrContribution = createActionOrContributionItem(extension, element);

			IAction action = null;
			IXContributionItem contributionItem = null;
			if (actionOrContribution instanceof IAction) {
				action = (IAction) actionOrContribution;

				action.setId(id);
				action.setText(name);
				action.setToolTipText(tooltip);
				action.setEnabled(false);

				if (icon != null && !"".equals(icon)) //$NON-NLS-1$
					action.setImageDescriptor(
							ImageDescriptor.createFromURL(Platform.getBundle(extension.getNamespace()).getEntry(icon)));

				if (disabledIcon != null && !"".equals(disabledIcon)) //$NON-NLS-1$
					action.setDisabledImageDescriptor(
							ImageDescriptor.createFromURL(Platform.getBundle(extension.getNamespace()).getEntry(disabledIcon)));

				if (hoverIcon != null && !"".equals(hoverIcon)) //$NON-NLS-1$
					action.setHoverImageDescriptor(
							ImageDescriptor.createFromURL(Platform.getBundle(extension.getNamespace()).getEntry(hoverIcon)));

				initAction(action, extension, element);
			}
			else if (actionOrContribution instanceof IXContributionItem) {
				contributionItem = (IXContributionItem) actionOrContribution;
				contributionItem.setId(id);
				initContributionItem(contributionItem, extension, element);
//				if (!id.equals(contributionItem.getId()))
//					throw new EPProcessorException("Your IContribution, which you created for the ActionDescriptor defined in extension " + extension.getNamespace() + " with id=" + id + " does have the wrong id (\"" + contributionItem.getId() + "\") set! Check your method " + this.getClass().getName() + ".createActionOrContributionItem(...)!");
			}
			else
				throw new EPProcessorException("Action class defined in extension " + extension.getNamespace() + " with id=" + id + " does neither implement IAction nor IXContributionItem! It must implement ONE of them!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			ActionDescriptor actionDescriptor = createActionDescriptor();
			actionDescriptor.init(
					action, contributionItem,
					menubarPath, toolbarPath, contextmenuPath,
					parseBooleanAcceptingNull(visible, true),
					parseBooleanAcceptingNull(visibleInMenubar, true),
					parseBooleanAcceptingNull(visibleInToolbar, true), parseBooleanAcceptingNull(visibleInContextmenu, true),
					id);
//			actionDescriptor.setGroupMarkerName(groupMarkerName);
			initActionDescriptor(actionDescriptor, extension, element);
			actionDescriptorsByID.put(id, actionDescriptor);
			menuRaw.add(actionDescriptor);
		}
		else
			throw new IllegalArgumentException("element.name=\"" + elementName + "\" unknown!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected static boolean parseBooleanAcceptingNull(String s, boolean defaultVal)
	{
		return s == null || "".equals(s) ? defaultVal : Boolean.parseBoolean(s); //$NON-NLS-1$
	}

	public Collection<ActionDescriptor> getActionDescriptors()
	{
		return actionDescriptorsByID.values();
	}

	public ActionDescriptor getActionDescriptor(String actionID, boolean throwExceptionIfNotFound)
	{
		ActionDescriptor descriptor = (ActionDescriptor) actionDescriptorsByID.get(actionID);

		if (throwExceptionIfNotFound && descriptor == null)
			throw new IllegalArgumentException("No ActionDescriptor known for actionID=" + actionID); //$NON-NLS-1$

		return descriptor;
	}
	
//	private Map<IContributionManager, Collection<String>> contributionManager2ExtensionIDs = 
//		new HashMap<IContributionManager, Collection<String>>();
	
	protected boolean earlyContributed = false;
	protected boolean perspectiveListenerAdded = false;
	protected boolean isAffectedOfPerspectiveExtension() 
	{			
		boolean isAffected = PerspectiveExtensionRegistry.sharedInstance().
			getRegisteredExtensionPointIDs().contains(getExtensionPointID());
//		if (isAffected)
//			logger.info("ExtensionPoint "+getExtensionPointID()+" affected by perspectiveExtension!");
//		else
//			logger.info("ExtensionPoint "+getExtensionPointID()+" NOT affected by perspectiveExtension!");
		
		return isAffected;
	}
	
	protected void updateActivePerspectiveExtensions() 
	{
		Map<String, Collection<String>> perspectiveID2ExtensionIDs = 
			PerspectiveExtensionRegistry.sharedInstance().getPerspectiveID2ExtensionIDs(
					getExtensionPointID());
		activeExtensionIDs = perspectiveID2ExtensionIDs.get(activePerspectiveID);		
	}
	
	private String activePerspectiveID = ""; //$NON-NLS-1$
	protected String getActivePerspectiveID() {
		return activePerspectiveID;
	}
	
	private Collection<String> activeExtensionIDs = Collections.emptyList();
	public Collection<String> getActiveExtensionIDs() 
	{
		if (!isAffectedOfPerspectiveExtension()) {
			return actionDescriptorsByID.keySet();
		}
		return activeExtensionIDs;
	}
	
	protected void perspectiveChange(IPerspectiveDescriptor perspective) 
	{		
//		logger.debug("perspectiveChange for perspective "+perspective.getId());		
		activePerspectiveID = perspective.getId();
		if (isAffectedOfPerspectiveExtension()) 
		{
			Collection<String> oldActiveExtensionIDs = Collections.emptyList();
			if (getActiveExtensionIDs() != null)
				oldActiveExtensionIDs = new ArrayList<String>(getActiveExtensionIDs()); 
			updateActivePerspectiveExtensions();
			if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() instanceof WorkbenchWindow) 
			{
				WorkbenchWindow workbenchWindow = (WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				ICoolBarManager coolBarManager = workbenchWindow.getCoolBarManager();
				if (coolBarManager != null) {
					removeContributionItems(coolBarManager, oldActiveExtensionIDs);
					contributeToCoolBar(coolBarManager);					
				}
				IToolBarManager toolBarManager = workbenchWindow.getToolBarManager();
				if (toolBarManager != null) {
					removeContributionItems(toolBarManager, oldActiveExtensionIDs);					
					contributeToToolBar(toolBarManager);					
				}
				IMenuManager menuManager = workbenchWindow.getMenuManager();
				if (menuManager != null) {
					removeContributionItems(menuManager, oldActiveExtensionIDs);
					contributeToMenuBar(menuManager);					
				}				
			}
		}		
	}
	
	protected void removeContributionItems(IContributionManager manager, Collection<String> ids) {
		for (String id : ids) {
			manager.remove(id);
		}
	}
	
	private IPerspectiveListener3 perspectiveListener = new IPerspectiveListener3()
	{
		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
			perspectiveChange(perspective);
		}			
		public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
			perspectiveChange(perspective);
		}	
		public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId) {
		}	
		public void perspectiveSavedAs(IWorkbenchPage page, IPerspectiveDescriptor oldPerspective, IPerspectiveDescriptor newPerspective) {			
		}	
		public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
			perspectiveChange(perspective);
		}	
		public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {			
		}	
		public void perspectiveClosed(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		}	
	};
	
}
