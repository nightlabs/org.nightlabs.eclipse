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
package org.nightlabs.base.editor;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.entity.editor.EntityEditorUtil;
import org.nightlabs.base.message.IMessageDisplayer;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class MessageSectionPart 
extends RestorableSectionPart
implements IMessageDisplayer
{
	public MessageSectionPart(IFormPage page, Composite parent, int style, String title) {
		super(parent, page.getEditor().getToolkit(), style);
		FormToolkit toolkit = page.getEditor().getToolkit();
		Section section = getSection();
		section.setText(title);
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		container = EntityEditorUtil.createCompositeClient(toolkit, section, 1);
		((GridLayout)container.getLayout()).numColumns = 1;		
		container.setLayoutData(new GridData(GridData.FILL_BOTH));		
	}

	private Composite container;
	public Composite getContainer() {
		return container;
	}
	
	/**
	 * sets the message to display
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		setMessage(message, IMessageProvider.NONE);
	}

	/**
	 * sets the message to display
	 * 
	 * @param message the message to display
	 * @param style the style of the message
	 * The valid message styles are one of <code>IMessageProvider.NONE</code>,
	 * <code>IMessageProvider.INFORMATION</code>,<code>IMessageProvider.WARNING</code>, or
	 * <code>IMessageProvider.ERROR</code>.
	 */
	public void setMessage(String message, int style) {
		getManagedForm().getForm().getForm().setMessage(message, style);
	}

}
