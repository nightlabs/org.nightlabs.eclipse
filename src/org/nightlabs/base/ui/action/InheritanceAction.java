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
package org.nightlabs.base.ui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.nightlabs.base.ui.NLBasePlugin;
import org.nightlabs.base.ui.composite.InheritanceToggleButton;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.resource.SharedImages;

/**
 * Abstract base class for all Inheritance actions, which are used to visualize the inheritance.
 * It automatically changes the icon, dependent on the checked state.
 * Subclasses must implement {@link IAction#run()}.
 *
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public abstract class InheritanceAction
extends Action implements IUpdateActionOrContributionItem
{
	public static final String ID = InheritanceAction.class.getName();

	public InheritanceAction() {
		super("", IAction.AS_CHECK_BOX); //$NON-NLS-1$
		setId(ID);
		setToolTipText(Messages.getString("org.nightlabs.base.ui.action.InheritanceAction.toolTipText")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(
				NLBasePlugin.getDefault(), InheritanceToggleButton.class, InheritanceToggleButton.IMAGE_SUFFIX_UNLINKED));
	}

	@Override
	public void setChecked(boolean checked)
	{
		super.setChecked(checked);
		if (checked)
			setImageDescriptor(SharedImages.getSharedImageDescriptor(NLBasePlugin.getDefault(), InheritanceToggleButton.class, InheritanceToggleButton.IMAGE_SUFFIX_LINKED));
		else
			setImageDescriptor(SharedImages.getSharedImageDescriptor(NLBasePlugin.getDefault(), InheritanceToggleButton.class, InheritanceToggleButton.IMAGE_SUFFIX_UNLINKED));
	}

	/**
	 * {@inheritDoc}
	 * <p>Returns <code>true</code>.</p>
	 * @see org.nightlabs.base.ui.action.IUpdateActionOrContributionItem#calculateEnabled()
	 */
	@Override
	public boolean calculateEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>Returns <code>true</code>.</p>
	 * @see org.nightlabs.base.ui.action.IUpdateActionOrContributionItem#calculateVisible()
	 */
	@Override
	public boolean calculateVisible() {
		return true;
	}
}
