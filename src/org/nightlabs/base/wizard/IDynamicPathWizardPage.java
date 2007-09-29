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

package org.nightlabs.base.wizard;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface IDynamicPathWizardPage extends IWizardPage
{
	/**
	 * When the {@link DynamicPathWizard} evaluates whether it can finish
	 * (in the method {@link DynamicPathWizard#canFinish()}),
	 * it asks all pages of the current path, whether they are complete by calling
	 * {@link IWizardPage#isPageComplete()}. To find out the current path,
	 * it starts at the entry page and moves forward by {@link IWizardPage#getNextPage()}.
	 * In case {@link IWizardPage#getNextPage()} returns <tt>null</tt>, the wizard cannot
	 * know whether this page is really the last or whether the next page is just not yet
	 * known. To make clear that a new page must follow, overwrite this method to return
	 * <tt>false</tt>.
	 *
	 * @return Returns whether or not this wizard page can be the last one in a chain.
	 */
	boolean canBeLastPage();

	/**
	 * This method will be called, when this page becomes the current one (after it has
	 * been made visible).
	 */
	void onShow();

	/**
	 * This method will be called, after this page was the current one, when setVisible
	 * is called with false (after the page was hidden, before the other page becomes
	 * visible).
	 */
	void onHide();
	
	/**
	 * returns true if the method onShow() has been already called, otherwise false
	 * @return true if the method onShow() has been already called, otherwise false 
	 */
	boolean isShown();
}
