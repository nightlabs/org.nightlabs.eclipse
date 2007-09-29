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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/* OLD JAVADOC:
 * 
 * Used for Wizards with dynamic WizardPages.
 * Inteded to be used as follows.<br/>
 * Subclass DynamicPathWizardPage and implement
 * <ul>
 *   <li>{@link org.eclipse.jface.wizard.Wizard#performFinish()}, where you perform your
 *   wizards action as you are used with normal
 *   Wizards.</li>
 * </ul> 
 * From now on you have two possibilities.
 * <p>
 *   You can use the add/removeDynamicWizardPage methods to add the
 *   pages as you need them. If you don't override the getNextPage-behavior
 *   of the added pages they will appear in the same order as added.
 * </p>
 * <p>
 *   FIXME!!! WRONG JAVADOC INFORMATION
 *   The second possibility is to set a DynamicWizardPopulator
 *   ({@link #setPopulator(DynamicWizardPopulator)}). After removing
 *   the old dynamic pages the populator will be asked to add its pages by 
 *   {@link org.nightlabs.base.wizard.DynamicWizardPopulator#addDynamicWizardPages(DynamicPathWizard)}.
 *   This is done every time Next is pressed and the wizards first page
 *   is showing (and your WizardDialog is a subclass of DynamicPathWizardDialog). 
 *   This lets you easily decide on user input wich path to go 
 *   by simply switching populators and the user can go back and
 *   even take an other way.
 * </p>
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Marco Schulze
 */

/**
 * 
 * Used for Wizards with dynamic WizardPages.
 * Intended to be used as follows.<br/>
 * Subclass DynamicPathWizardPage and implement
 * <ul>
 *   <li>{@link org.eclipse.jface.wizard.Wizard#performFinish()}, where you perform your
 *   wizards action as you are used with normal
 *   Wizards.</li>
 * </ul> 
 * <p>
 *   You can use the add/removeDynamicWizardPage methods to add the
 *   pages as you need them. If you don't override the getNextPage-behavior
 *   of the added pages they will appear in the same order as added.
 * </p>
 * 
 * FIXME: must be rewritten as from Eclipse 3.2, Wizard works different then in 3.1
 * Wizard.createPageControls() does not work properly when in addPages() only addDynamicPage() is called
 * instead of addPage()   
 *  
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Marco Schulze
 */
public abstract class DynamicPathWizard extends Wizard implements IDynamicPathWizard {

	private List<IWizardPage> dynamicWizardPages = new ArrayList<IWizardPage>();
	private DynamicPathWizardDialog dynamicWizardDialog;

	/**
	 * Calls {@link #DynamicPathWizard(boolean)} with <tt>init = true</tt>.
	 */
	public DynamicPathWizard() {
		this(true);
	}

	/**
	 * @param init Whether or not to call the method {@link #init()}. If you are not ready
	 * to implicitely call {@link #init()} in the super constructor, you can call
	 * <tt>super(false)</tt>, but you must call <tt>init()</tt> manually then!
	 *
	 * @deprecated Because {@link #init()} is not necessary anymore, this constructor
	 *		isn't either.
	 */
	public DynamicPathWizard(boolean init) {
		setForcePreviousAndNextButtons(true);
		if (init)
			init();
	}

	/**
	 * Does some initialization. This method is called automatically by the constructor
	 * if <tt>init = true</tt> is passed to {@link #DynamicPathWizard(boolean)} or
	 * if {@link #DynamicPathWizard()} is used. If you decide to use {@link #DynamicPathWizard(boolean)}
	 * with <tt>init = false</tt>, you must call this method manually!
	 *
	 * @deprecated
	 */
	protected void init() {
//		addPage(getWizardEntryPage());
	}

	/**
	 * {@inheritDoc}
	 *
	 * This implementation (in <code>DynamicPathWizard</code>) returns: <code>this.getClass().getName()</code>
	 */
	public String getIdentifier()
	{
		return this.getClass().getName();
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages()
	{
		super.addPages(); // empty super method.
		IWizardPage entryPage = getWizardEntryPage();
		if(entryPage != null)
			addPage(entryPage);
	}

	/**
	 * <strong>More Important API change:</strong> The entry page system is deprecated.
	 * From now on, handle entry pages manually!
	 * <p>
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
	 * 
	 * @deprecated
	 */
	public IDynamicPathWizardPage createWizardEntryPage() 
	{
		return null;
	}

	/**
	 * @deprecated
	 */
	private IDynamicPathWizardPage wizardEntryPage = null;

	/**
	 * <strong>More Important API change:</strong> The entry page system is deprecated.
	 * From now on, handle entry pages manually!
	 * <p>
	 * <strong>Important API change: Do not overwrite this method anymore!!!</strong>
	 * Overwrite {@link #createWizardEntryPage()} instead!
	 * 
	 * @return Returns the first page of the wizard. If this page does not yet exist
	 * (means it's the first call to this method), {@link #createWizardEntryPage()} is
	 * called.
	 * 
	 * @deprecated
	 */
	public IDynamicPathWizardPage getWizardEntryPage()
	{
		if (wizardEntryPage == null)
			wizardEntryPage = createWizardEntryPage();

//		if (wizardEntryPage == null)
//		throw new NullPointerException("createWizardEntryPage() must not return null!");

		return wizardEntryPage;
	}

	public List<IWizardPage> getDynamicWizardPages()
	{
		return Collections.unmodifiableList(dynamicWizardPages);
	}

	/**
	 * @see org.nightlabs.base.wizard.IDynamicPathWizard#getDynamicWizardPageCount()
	 */
	public int getDynamicWizardPageCount()
	{
		return dynamicWizardPages.size();
	}


	/**
	 * @return The first dynamic wizard page or null if no dynamic pages exist 
	 */
	protected IDynamicPathWizardPage getFirstDynamicPage() {
		if (dynamicWizardPages.size() > 0)
			return (IDynamicPathWizardPage)dynamicWizardPages.get(0);
		return null;
	}


	/**
	 * @return The first static wizard page or null if no static pages exist 
	 */
	protected IWizardPage getFirstStaticPage() {
		if(getPageCount() > 0)
			return getPages()[0];
		return null;
	}

	/**
	 * Get the first page in this wizard existing right now.
	 * This can either be a dynamic or static page or <code>null</code>
	 * if the wizard is empty.
	 * @return The first wizard page
	 */
	protected IWizardPage getFirstPage()
	{
		IWizardPage page = getFirstStaticPage();
		if(page == null)
			page = getFirstDynamicPage();
		return page;
	}

	/**
	 * Test whether a page can be added at the given index.
	 * @param index The index to test
	 * @throws IllegalStateException if the value of index is illegal
	 */
	protected void assertCanInsertDynamicWizardPage(int index)
	{
		int lastReadOnlyIndex = -1;
		IWizardPage currentPage = dynamicWizardDialog.getCurrentPage();
		if (currentPage == null)
			return;

		if(getPageCount() == 0 && getDynamicWizardPageCount() == 0) {
			if(index == 0)
				return;
			else
				throw new IllegalStateException("Cannot add a wizard page at index " + index + ", it is the first page for this wizard!"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		IWizardPage page = getFirstPage();
		while (page != null && page != currentPage) {
			if (page instanceof IDynamicPathWizardPage) {
				int i = getDynamicWizardPageIndex((IDynamicPathWizardPage)page);
				if (i > lastReadOnlyIndex)
					lastReadOnlyIndex = i;
			}
			page = page.getNextPage();
		}

		if (index <= lastReadOnlyIndex)
			throw new IllegalStateException("Cannot add a wizard page at index " + index + ", because the current page forces all indices <=" + lastReadOnlyIndex + " to be unchangeable!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Add a dynamic wizard page to this wizard.
	 * Overridden method.
	 * @param index At which index to put the page.
	 * @param page The page to add
	 * @throws IllegalStateException if the value of index is illegal
	 * @see org.nightlabs.base.wizard.IDynamicPathWizard#addDynamicWizardPage(int, org.nightlabs.base.wizard.IDynamicPathWizardPage)
	 */
	public void addDynamicWizardPage(int index, IWizardPage page) {
		assertCanInsertDynamicWizardPage(index);
		dynamicWizardPages.add(index, page);
		page.setWizard(this);
	}

	/**
	 * Add a dynamic wizard page at the end of the wizard.
	 * 
	 * @param page The page to add
	 * @see org.nightlabs.base.wizard.IDynamicPathWizard#addDynamicWizardPage(org.nightlabs.base.wizard.IDynamicPathWizardPage)
	 */
	public void addDynamicWizardPage(IWizardPage page) {
		dynamicWizardPages.add(page);
		page.setWizard(this);
	}

	/**
	 * Get the index of a dynamic wizard page.
	 * Overridden method.
	 * @return the index of the page or -1 if the page was not found.
	 * @see org.nightlabs.base.wizard.IDynamicPathWizard#getDynamicWizardPageIndex(org.nightlabs.base.wizard.IDynamicPathWizardPage)
	 */
	public int getDynamicWizardPageIndex(IWizardPage page) {
		return dynamicWizardPages.indexOf(page);
	}

	/**
	 * @see org.nightlabs.base.wizard.IDynamicPathWizard#getDynamicWizardPage(int)
	 */
	public IDynamicPathWizardPage getDynamicWizardPage(int index)
	{
		return (IDynamicPathWizardPage) dynamicWizardPages.get(index);
	}

	protected void assertCanRemoveDynamicWizardPage(IWizardPage page)
	{
		if (dynamicWizardDialog == null)
			return;

		IWizardPage currentPage = dynamicWizardDialog.getCurrentPage();
		if (currentPage == null)
			return;

		if(getPageCount() == 0 && getDynamicWizardPageCount() == 0) {
			throw new IllegalStateException("Cannot remove wizard page! Nothing to remove!"); //$NON-NLS-1$
		}

		IWizardPage pageToCheck = getFirstPage();
		while (pageToCheck != null && pageToCheck != currentPage) {
			if (pageToCheck == page)
				throw new IllegalStateException("Cannot remove page \"" + page.getName() + "\", because it is part of the path BEFORE the current page! Can only remove pages that are following the current page!"); //$NON-NLS-1$ //$NON-NLS-2$
			pageToCheck = pageToCheck.getNextPage();
		}

		if (!dynamicWizardPages.contains(page))
			throw new IllegalStateException("Cannot remove page \"" + page.getName() + "\", because it is not registered as dynamic page! If it is static, you cannot remove it and if it is part of a WizardHop, you must remove it there!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see org.nightlabs.base.wizard.IDynamicPathWizard#removeDynamicWizardPage(int)
	 */
	public void removeDynamicWizardPage(int index) {
		if (index < 0 || index >= dynamicWizardPages.size())
			return;

		assertCanRemoveDynamicWizardPage(
				(IDynamicPathWizardPage) dynamicWizardPages.get(index));
		dynamicWizardPages.remove(index);
	}

	/**
	 * @see org.nightlabs.base.wizard.IDynamicPathWizard#removeDynamicWizardPage(org.nightlabs.base.wizard.IDynamicPathWizardPage)
	 */
	public void removeDynamicWizardPage(IWizardPage page) {
		assertCanRemoveDynamicWizardPage(page);
		dynamicWizardPages.remove(page);
	}

	public void removeAllDynamicWizardPages() {
		dynamicWizardPages.clear();
	}

	public boolean canFinish() {
		IWizardPage page = getFirstPage();
		IWizardPage lastPage = null;
		while (page != null) {
			if (!page.isPageComplete())
				return false;
			lastPage = page;
			page = page.getNextPage();
		}
		if (!(lastPage instanceof IDynamicPathWizardPage))
			return true;
		if (lastPage == null || (lastPage instanceof IDynamicPathWizardPage && ((IDynamicPathWizardPage)lastPage).canBeLastPage()))
			return true;
		else
			return false;

//		if (!getWizardEntryPage().isPageComplete())
//		return false;

//		if (dynamicWizardPages.size() > 0) {
//		for (int i = 0; i < dynamicWizardPages.size(); i++) {
//		if (!((IWizardPage) dynamicWizardPages.get(i)).isPageComplete())
//		return false;
//		}
//		return true; // no page is incomplete
//		}
//		else {
//		return true; // only wizardEntryPage matters
//		}
	}	

	public DynamicPathWizardDialog getDynamicWizardDialog() {
		return dynamicWizardDialog;
	}
	public void setDynamicWizardDialog(DynamicPathWizardDialog dynamicWizardDialog) {
		this.dynamicWizardDialog = dynamicWizardDialog;
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	public IWizardPage getPreviousPage(IWizardPage page)
	{
		return super.getPreviousPage(page);
	}

	public IWizardPage getNextPage(IWizardPage page) {

		IWizardPage[] pages = getPages();
		boolean isStaticPage = false;
		for (int i = 0; i < pages.length; ++i) {
			if (pages[i] == page) {
				isStaticPage = true;
				break;
			}
		}

		if (isStaticPage && page != pages[pages.length - 1])
			return super.getNextPage(page);

//		if (page == getWizardEntryPage()) {
		if (page == pages[pages.length - 1]) {
			IWizardPage populatorPage = getFirstDynamicPage();
			if (populatorPage != null)
				return populatorPage;
//			return null;
		}
		int index = dynamicWizardPages.indexOf(page);
		if (index == dynamicWizardPages.size() - 1) {
			return null;
//			if (page == getFirstDynamicPage() && dynamicWizardPages.size() > 0)
//			return (IWizardPage)dynamicWizardPages.get(0);
			// last page or page not found
//			return null;
		}
		else if (index >= 0)
			return (IWizardPage)dynamicWizardPages.get(index + 1);
		else
			return null;
	}

	public void updateDialog() {
		if (dynamicWizardDialog != null)
			dynamicWizardDialog.update();
	}

	/**
	 * This method does (if the wizard is currently shown) the same as
	 * if the finish-button has been pressed.
	 */
	public void finish() {
		if (dynamicWizardDialog != null)
			dynamicWizardDialog.finishPressed();
	}

	/**
	 * This method does (if the wizard is currently shown) the same as
	 * if the finish-button has been pressed.
	 */
	public void cancel() {
		if (dynamicWizardDialog != null)
			dynamicWizardDialog.cancelPressed();
	}

}
