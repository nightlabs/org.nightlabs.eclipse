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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;

import org.nightlabs.base.action.IXContributionItem;

public class ActionDescriptor extends ItemDescriptor
{
	private IAction action;
	private IXContributionItem contributionItem;
	private String menubarPath;
	private String toolbarPath;
	private String contextmenuPath;
//	private String groupMarkerName;
	
	/**
	 * Used to determine whether to show the Action or not.
	 * @see #setVisible(boolean)
	 */
	private boolean visible;

	/**
	 * If <code>visible == true</code> and <code>visibleInMenubar == true</code>, then the Action
	 * will be shown in the menu bar.
	 */
	private boolean visibleInMenubar;
	/**
	 * If <code>visible == true</code> and <code>visibleInToolbar == true</code>, then the Action
	 * will be shown in the tool bar.
	 */
	private boolean visibleInToolbar;
	/**
	 * If <code>visible == true</code> and <code>visibleInContextmenu == true</code>, then the Action
	 * will be shown in the context menu.
	 */
	private boolean visibleInContextmenu;

	/**
	 * the id of the {@link IAction} or the {@link IContributionItem}
	 */
	private String id;
	
	public ActionDescriptor() { }

	public void init(
			IAction action, IXContributionItem contributionItem, String menubarPath, String toolbarPath,
			String contextmenuPath, boolean visible,
			boolean visibleInMenubar, boolean visibleInToolbar, boolean visibleInContextmenu, String id)
	{
		this.action = action;
		this.contributionItem = contributionItem;
		this.menubarPath = menubarPath;
		this.toolbarPath = toolbarPath;
		this.contextmenuPath = contextmenuPath;
		this.visible = visible;
		this.visibleInMenubar = visibleInMenubar;
		this.visibleInToolbar = visibleInToolbar;
		this.visibleInContextmenu = visibleInContextmenu;
		this.id = id;
	}

	public IAction getAction()
	{
		return action;
	}
	public IXContributionItem getContributionItem()
	{
		return contributionItem;
	}
	public String getMenubarPath()
	{
		return menubarPath;
	}
	public String getToolbarPath()
	{
		return toolbarPath;
	}
	public String getContextmenuPath()
	{
		return contextmenuPath;
	}
	public String getID() 
	{
		return id;
	}
//	public String getGroupMarkerName() 
//	{
//		return groupMarkerName;
//	}
	
	/**
	 * Important: Not only <code>visible</code> alone decides whether to show an Action or not.
	 * Additionally, the result of {@link #isVisibleInMenubar()}, {@link #isVisibleInToolbar()}
	 * or {@link #isVisibleInContextmenu()} is relevant.
	 *
	 * @return Returns whether or not the Action shall be added to
	 *		the {@link IContributionManager} in {@link AbstractActionRegistry#contribute(IContributionManager, String)}.
	 */
	public boolean isVisible()
	{
		return visible;
	}
	/**
	 * If an <code>ActionDescriptor</code> is marked as not visible, it will not be added to
	 * the {@link IContributionManager} in {@link AbstractActionRegistry#contribute(IContributionManager, String)}.
	 *
	 * @param visible Whether or not the action shall be visible.
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	public boolean isVisibleInMenubar()
	{
		return visibleInMenubar;
	}
	public boolean isVisibleInToolbar()
	{
		return visibleInToolbar;
	}
	public boolean isVisibleInContextmenu()
	{
		return visibleInContextmenu;
	}
	public void setVisibleInMenubar(boolean visibleInMenubar)
	{
		this.visibleInMenubar = visibleInMenubar;
	}
	public void setVisibleInToolbar(boolean visibleInToolbar)
	{
		this.visibleInToolbar = visibleInToolbar;
	}
	public void setVisibleInContextmenu(boolean visibleInContextmenu)
	{
		this.visibleInContextmenu = visibleInContextmenu;
	}
	public void setID(String id) 
	{
		this.id = id;
	}
//	public void setGroupMarkerName(String groupMarkerName) 
//	{
//		this.groupMarkerName = groupMarkerName;
//	}
}
