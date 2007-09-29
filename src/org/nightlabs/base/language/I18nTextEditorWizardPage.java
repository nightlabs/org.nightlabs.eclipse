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

package org.nightlabs.base.language;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;

/**
 * Simple extendable Wizardpage showing one I18nTextEditor.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class I18nTextEditorWizardPage extends WizardHopPage {

	private I18nTextBuffer buffer = new I18nTextBuffer();
	
	/**
	 * @param pageName
	 */
	public I18nTextEditorWizardPage(String pageName, String editorCaption) {
		super(pageName);
		setEditorCaption(editorCaption);
	}

	/**
	 * @param pageName
	 * @param title
	 */
	public I18nTextEditorWizardPage(String pageName, String title, String editorCaption) {
		super(pageName, title);
		setEditorCaption(editorCaption);
	}

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public I18nTextEditorWizardPage(String pageName, String title,
			ImageDescriptor titleImage, String editorCaption) {
		super(pageName, title, titleImage);
		setEditorCaption(editorCaption);
	}
	
	private XComposite wrapper;
	private I18nTextEditor textEditor;
	private String editorCaption;
	

	public void setEditorCaption(String editorCaption) {
		this.editorCaption = (editorCaption != null) ? this.editorCaption: ""; //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		textEditor = new I18nTextEditor(wrapper, editorCaption);
		textEditor.setI18nText(buffer);
		textEditor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING));
		createAdditionalContents(wrapper);
		return wrapper;
	}
	
	/**
	 * This is called after the I18TextEditor is created to allow 
	 * subclasses to add additional Controls to the page.
	 * 
	 * @param wrapper
	 */
	protected void createAdditionalContents(Composite wrapper) {}
	
	
	public void setI18nText(I18nText text) {
		buffer.copyFrom(text);
		textEditor.setI18nText(buffer);
	}
	
	public I18nText getI18nText() {
		return buffer;
	}
	
	public I18nTextEditor getTextEditor() {
		return textEditor;
	}

}
