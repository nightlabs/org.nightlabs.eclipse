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

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.config.DialogCf;
import org.nightlabs.base.config.DialogCfMod;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.config.Config;


/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class DynamicPathWizardDialog extends WizardDialog 
{
	private DynamicPathWizard dynamicWizard;

	/**
	 * Create a new DynamicPathWizardDialog.
	 * @param wizard The wizard to show
	 */
	public DynamicPathWizardDialog(DynamicPathWizard wizard)
	{
		this(RCPUtil.getActiveWorkbenchShell(), wizard);
	}

	/**
	 * Create a new DynamicPathWizardDialog.
	 * @param shell The parent shell
	 * @param wizard The wizard to show
	 */
	public DynamicPathWizardDialog(Shell shell, DynamicPathWizard wizard)
	{
		super(shell, wizard);
		dynamicWizard = wizard;
		dynamicWizard.setDynamicWizardDialog(this);
	}

	/**
	 * Overrides and makes it public so the wizard can
	 * trigger the update of the dialog buttons.
	 * 
	 * @see org.eclipse.jface.wizard.WizardDialog#update()
	 */
	public void update() {
		super.update();
	}
	
	/**
	 * Overrides and makes it public.
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#getButton(int)
	 */
	public Button getButton(int id) {
		return super.getButton(id);
	}
	
	protected void backPressed() {
		buttonBar.setFocus(); // to trigger all GUI-element-to-backend-object-store-methods
		super.backPressed();
	}
	
	protected void nextPressed() {
		buttonBar.setFocus(); // to trigger all GUI-element-to-backend-object-store-methods
//		if (getCurrentPage() == dynamicWizard.getWizardEntryPage()) {
//			if (dynamicWizard.getPopulator() != null)
//				dynamicWizard.getPopulator().addDynamicWizardPages(dynamicWizard);
//		}
		super.nextPressed();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardDialog#finishPressed()
	 */
	protected void finishPressed()
	{
		buttonBar.setFocus(); // to trigger all GUI-element-to-backend-object-store-methods
		storeDialogSize();
		super.finishPressed();
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardDialog#cancelPressed()
	 */
	protected void cancelPressed()
	{
		buttonBar.setFocus(); // to trigger all GUI-element-to-backend-object-store-methods
		super.cancelPressed();
	}


	protected Control createContents(Composite parent) {
		Control result = super.createContents(parent);
		if (dynamicWizard.getFirstPage() instanceof IDynamicPathWizardPage)
			((IDynamicPathWizardPage)dynamicWizard.getFirstPage()).onShow();
		return result;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.FINISH_ID) {
			IWizardPage currPage = getCurrentPage();
			if (currPage instanceof IDynamicPathWizardPage)
				((IDynamicPathWizardPage)currPage).onHide();
		}
		super.buttonPressed(buttonId);
		if (getReturnCode() != Window.OK) {
			IWizardPage currPage = getCurrentPage();
			if (currPage instanceof IDynamicPathWizardPage)
				((IDynamicPathWizardPage)currPage).onShow();
		}		
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardDialog#showPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	public void showPage(IWizardPage page)
	{
		IWizardPage currPage = getCurrentPage();

		if (currPage == page)
			return;

		if (currPage instanceof IDynamicPathWizardPage)
			((IDynamicPathWizardPage)currPage).onHide();

		super.showPage(page);

		if (page instanceof IDynamicPathWizardPage)
			((IDynamicPathWizardPage)page).onShow();
	}
	
	@Override
	public void create() 
	{
		super.create();

		DialogCf cf = getDialogCfMod().getDialogCf(getWizardIdentifier(getWizard()));
		if (cf == null) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Point shellSize = getShell().getSize();
			int diffWidth = screenSize.width - shellSize.x;
			int diffHeight = screenSize.height - shellSize.y;
			getShell().setLocation(diffWidth/2, diffHeight/2);
		}
		else {
			getShell().setLocation(cf.getX(), cf.getY());
			getShell().setSize(cf.getWidth(), cf.getHeight());
		}
	}

	private static DialogCfMod getDialogCfMod()
	{
		return (DialogCfMod) Config.sharedInstance().createConfigModule(DialogCfMod.class);
	}

	private static String getWizardIdentifier(IWizard wizard)
	{
		String wizardIdentifier = wizard instanceof IDynamicPathWizard ? ((IDynamicPathWizard)wizard).getIdentifier() : wizard.getClass().getName();
		if (wizardIdentifier == null)
			throw new IllegalStateException("identifier is null! Check the class " + wizard.getClass().getName()); //$NON-NLS-1$

		return wizardIdentifier;
	}

	@Override
	public boolean close()
	{
		storeDialogSize();
		return super.close();
	}
	
	protected void storeDialogSize() {
		getDialogCfMod().createDialogCf(
				getWizardIdentifier(getWizard()),
				getShell().getLocation().x,
				getShell().getLocation().y,
				getShell().getSize().x,
				getShell().getSize().y);
	}
}
