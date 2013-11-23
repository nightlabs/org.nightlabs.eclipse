/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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

package org.nightlabs.editor2d.viewer.ui.app;

import java.util.Arrays;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.nightlabs.base.ui.app.DefaultActionBuilder;
import org.nightlabs.editor2d.viewer.ui.action.AbstractOpenAction;
import org.nightlabs.editor2d.viewer.ui.action.EditorOpenAction;

public class ViewerActionBuilder
extends DefaultActionBuilder
{
	public ViewerActionBuilder(IActionBarConfigurer configurer)
	{
		super(configurer, Arrays.asList(new ActionBarItem[] {ActionBarItem.Preferences}), null);
	}
	
	private AbstractOpenAction openAction = null;

	@Override
	public void fillMenuBar(IMenuManager menuBar)
	{
		super.fillMenuBar(menuBar);
		getFileMenu().insertBefore(ActionFactory.QUIT.getId(), openAction);
		getFileMenu().insertAfter(openAction.getId(), new Separator());
	}

	@Override
	protected void makeActions(IWorkbenchWindow window)
	{
		super.makeActions(window);
		openAction = new EditorOpenAction();
	}
		
}
