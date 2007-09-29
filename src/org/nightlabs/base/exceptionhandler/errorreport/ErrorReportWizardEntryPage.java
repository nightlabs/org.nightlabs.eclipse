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

package org.nightlabs.base.exceptionhandler.errorreport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.wizard.DynamicPathWizardPage;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReportWizardEntryPage extends DynamicPathWizardPage
{
	public ErrorReportWizardEntryPage()
	{
		super(ErrorReportWizardEntryPage.class.getName(), Messages.getString("org.nightlabs.base.exceptionhandler.errorreport.ErrorReportWizardEntryPage.title")); //$NON-NLS-1$
	}
  
	/**
	 * @see org.nightlabs.base.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createPageContents(Composite parent)
	{
    setDescription(Messages.getString("org.nightlabs.base.exceptionhandler.errorreport.ErrorReportWizardEntryPage.description")); //$NON-NLS-1$
    Label welcomeText = new Label(parent, SWT.WRAP);
    welcomeText.setText(Messages.getString("org.nightlabs.base.exceptionhandler.errorreport.ErrorReportWizardEntryPage.welcomeText.text")); //$NON-NLS-1$
		return welcomeText;
	}
  
  public void onShow()
  {
    super.onShow();
    getShell().setSize(new Point(600, 500));
  }
}
