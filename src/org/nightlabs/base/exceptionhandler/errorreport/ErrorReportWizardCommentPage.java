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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.wizard.DynamicPathWizard;
import org.nightlabs.base.wizard.DynamicPathWizardPage;

/**
 * @author Simon Lehmann - simon@nightlabs.de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ErrorReportWizardCommentPage extends DynamicPathWizardPage
{
	private Text textUserComment;
	
	public ErrorReportWizardCommentPage()
	{
		super(ErrorReportWizardCommentPage.class.getName(), Messages.getString("org.nightlabs.base.exceptionhandler.errorreport.ErrorReportWizardCommentPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.base.exceptionhandler.errorreport.ErrorReportWizardCommentPage.description")); //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.base.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createPageContents(Composite parent)
	{	
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
//    GridLayout layout = new GridLayout();
//    //layout.
//    page.setLayout(layout);
    Label commentLabel = new Label(page, SWT.WRAP);
    commentLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    commentLabel.setText(Messages.getString("org.nightlabs.base.exceptionhandler.errorreport.ErrorReportWizardCommentPage.commentLabel.text")); //$NON-NLS-1$
		textUserComment = new Text(page, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
		textUserComment.setToolTipText(Messages.getString("org.nightlabs.base.exceptionhandler.errorreport.ErrorReportWizardCommentPage.textUserComment.toolTipText")); //$NON-NLS-1$
		textUserComment.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				((DynamicPathWizard)getWizard()).updateDialog();
				ErrorReportWizard wizard = (ErrorReportWizard) getWizard();
				ErrorReport errorReport = wizard.getErrorReport();
				errorReport.setUserComment(textUserComment.getText());
			}
		});
		textUserComment.setLayoutData(new GridData(GridData.FILL_BOTH));
		return page;
	}
	
	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	public boolean isPageComplete()
	{
		return true;
	}
}
