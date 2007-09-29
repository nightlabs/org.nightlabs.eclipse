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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;


/**
 * A wizard page to be used within a wizard hop.
 * @see IWizardHop
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class WizardHopPage extends DynamicPathWizardPage
implements IWizardHopPage
{
	private IWizardHop wizardHop;
	
	/**
	 * Create a new WizardHopPage.
	 * @param pageName The identifier used for the page
	 */
	public WizardHopPage(String pageName)
	{
		super(pageName);
	}

	/**
	 * Create a new WizardHopPage.
	 * @param pageName The identifier used for the page
	 * @param title The title for the page
	 */
	public WizardHopPage(String pageName, String title)
	{
		super(pageName, title);
	}

	/**
	 * Create a new WizardHopPage.
	 * @param pageName The identifier used for the page
	 * @param title The title for the page
	 * @param titleImage The title image for the page
	 */
	public WizardHopPage(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.wizard.IWizardHopPage#setWizardHop(org.nightlabs.base.wizard.IWizardHop)
	 */
	public void setWizardHop(IWizardHop wizardHop)
	{
		this.wizardHop = wizardHop;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.wizard.IWizardHopPage#getWizardHop()
	 */
	public IWizardHop getWizardHop()
	{
		return wizardHop;
	}

	/**
	 * If this WizardHopPage has a wizardHop set, the hop will be asked for the next
	 * page, otherwise the wizard of this page (if set) will be asked.
	 *   
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	public IWizardPage getNextPage()
	{
		if (wizardHop != null)
			return wizardHop.getNextPage(this);
		else {
			if (getWizard() != null)
				return getWizard().getNextPage(this);
			else
				throw new IllegalStateException(
						"wizardHop AND wizard are not assigned! If this is the entry-page of a WizardHop, no WizardHop has been created in the constructor! Call 'new WizardHop(this);' in the constructor of " + //$NON-NLS-1$
						this.getClass().getName()+". If this should be used as normal WizardPage, add it to the wizard."); //$NON-NLS-1$		
		}
	}

//	/**
//	 * @see org.eclipse.jface.wizard.WizardPage#getPreviousPage()
//	 */
//	public IWizardPage getPreviousPage()
//	{
//		return wizardHop.getPreviousPage(this);
//	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#setWizard(org.eclipse.jface.wizard.IWizard)
	 */
	@Override
	public void setWizard(IWizard newWizard) {
		super.setWizard(newWizard);
		if (wizardHop == null)
			return;
		for (IWizardHopPage hopPage : wizardHop.getHopPages()) {
			if (hopPage != this && hopPage.getWizard() != newWizard)
				hopPage.setWizard(newWizard);
		}
	}
}
