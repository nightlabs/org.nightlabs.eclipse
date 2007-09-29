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

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface IDynamicPathWizard extends IWizard
{
	/**
	 * @return an identifier used for managing settings of this wizard. The {@link DynamicPathWizardDialog} uses this
	 *		for managing sizes and positions. It must not be <code>null</code>. The default implementation in
	 *		{@link DynamicPathWizard} returns the class name of the concrete implementation.
	 */
	String getIdentifier();

	/**
	 * <strong>Important API change:</strong> Since this method exists, you
	 * MUST NOT overwrite {@link #getWizardEntryPage()} anymore!!!
	 * <p>
	 * This method is called exactly once and you should create the first page of
	 * your wizard here. Note, that it is already called in the constructor
	 * if <tt>init</tt> is <tt>true</tt>.
	 * <p>
	 * Note: You might want to extend the {@link DynamicPathWizardPage} instead of manually
	 * implementing an {@link IDynamicPathWizardPage}.
	 *
	 * @return Returns the first page of the wizard.
	 *
	 * @see DynamicPathWizardPage
	 * @see IDynamicPathWizardPage
	 */
	IDynamicPathWizardPage createWizardEntryPage();

	/**
	 * <strong>Important API change: Do not overwrite this method anymore!!!</strong>
	 * Overwrite {@link #createWizardEntryPage()} instead!
	 * 
	 * @return Returns the first page of the wizard. If this page does not yet exist
	 * (means it's the first call to this method), {@link #createWizardEntryPage()} is
	 * called.
	 */
	IDynamicPathWizardPage getWizardEntryPage();

	/**
	 * With this method you can insert a page. Once the wizard is visible, you
	 * can only add pages in the path AFTER the current page!
	 *
	 * @param index The 0-based position of the page.
	 * @param page The page to be added.
	 */
	void addDynamicWizardPage(int index, IWizardPage page);

	/**
	 * This method adds a page to the end of the dynamic path.
	 *
	 * @param page The page to be added. 
	 */
	void addDynamicWizardPage(IWizardPage page);

	/**
	 * @param page The page for which to find out the index.
	 * @return Either -1, if the page is not in the <tt>List</tt>, or the 0-based
	 *		index within the <tt>List</tt>.
	 */
	int getDynamicWizardPageIndex(IWizardPage page);

	/**
	 * @param index The 0-based index of the desired page.
	 * @return Returns always an instance of <tt>IDynamicPathWizardPage</tt>.
	 *		If the index is invalid, the used <tt>List</tt> throws an exception.
	 */
	IDynamicPathWizardPage getDynamicWizardPage(int index);

	int getDynamicWizardPageCount();

	/**
	 * Removes the page with the given index. If the index is invalid, nothing
	 * happens. Note, that (if the wizard is already visible) you can only remove
	 * a page that appears AFTER the
	 * current page - means NOT being in the
	 * path from the entry-page to the current page.
	 *
	 * @param index Index of the dynamic page.
	 */
	void removeDynamicWizardPage(int index);

	/**
	 * Removes the given page. If the page is unknown, nothing
	 * happens. Note, that (if the wizard is already visible) you can only remove
	 * a page that appears AFTER the
	 * current page - means NOT being in the path from the entry-page to the
	 * current page.
	 *
	 * @param page The page to be removed from this wizard.
	 */
	void removeDynamicWizardPage(IWizardPage page);

	void removeAllDynamicWizardPages();

//	DynamicWizardPopulator getPopulator();

//	void setPopulator(DynamicWizardPopulator populator);

	DynamicPathWizardDialog getDynamicWizardDialog();

	void setDynamicWizardDialog(DynamicPathWizardDialog dynamicWizardDialog);

	IWizardPage getNextPage(IWizardPage page);

	void updateDialog();

}
