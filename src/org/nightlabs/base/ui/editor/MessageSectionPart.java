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
package org.nightlabs.base.ui.editor;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.base.ui.message.IMessageDisplayer;
import org.nightlabs.base.ui.message.MessageType;
import org.nightlabs.base.ui.toolkit.IToolkit;

/**
 * {@link MessageSectionPart} creates a {@link Section} with a container for the section contents.
 * The section will have get a {@link GridData} and therefore can be used inside {@link GridLayout}
 * as is. The container for the sections contents will have a {@link GridLayout} with one column by default.
 * <p>
 *
 * </p>
 *
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class MessageSectionPart
extends RestorableSectionPart
implements IMessageDisplayer
{
	private Composite container;
	/**
	 * We maintain our own description control because there was no
	 * way of preventing the original description control of a
	 * Section to have borders
	 * (Section constructor causes reflow
	 * -> reflow causes XComposite to give all Texts a border painter)
	 */
	private Text descriptionControl;

	/**
	 * Create a new {@link MessageSectionPart} with a Section and a container
	 * {@link Composite} for the contents of this section.
	 *
	 * @param page The page the new part should be in.
	 * @param parent The parent for the new Section.
	 * @param style The style of the new Section, use constants of {@link ExpandableComposite} and {@link Section} for that.
	 * 		Note that using {@link Section#DESCRIPTION} will have no affect as it is stripped from the style, the description
	 * 		control is managed by this class.
	 * @param title The title for the new section.
	 */
	public MessageSectionPart(IFormPage page, Composite parent, int style, String title) {
		this(page.getEditor().getToolkit(), parent, style, title);
	}

	/**
	 * Create a new {@link MessageSectionPart} with a Section and a container
	 * {@link Composite} for the contents of this section.
	 *
	 * @param toolkit The toolkit to use.
	 * @param parent The parent for the new Section.
	 * @param style The style of the new Section, use constants of {@link ExpandableComposite} and {@link Section} for that.
	 * 		Note that using {@link Section#DESCRIPTION} will have no affect as it is stripped from the style, the description
	 * 		control is managed by this class.
	 * @param title The title for the new section.
	 */
	public MessageSectionPart(FormToolkit toolkit, Composite parent, int style, String title) {
		super(parent, toolkit, (style & ~Section.DESCRIPTION));
		Section section = getSection();
		section.setText(title);
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		container = EntityEditorUtil.createCompositeClient(toolkit, section, 1);
		((GridLayout)container.getLayout()).numColumns = 1;
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
	}


	/**
	 * Returns the container that was created for the contents of the underlying section
	 * and that was set as the sections client.
	 *
	 * @return The container for the contents of the underlying section.
	 */
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

	/**
	 * Returns the description text of the underlying section.
	 * Note that this method returns <code>null</code> as
	 * long as no description was set.
	 *
	 * @return the description text of the underlying section.
	 */
	public String getDescription() {
		return getSection().getDescription();
	}

	/**
	 * Set the description text for the underlying section.
	 * Note that this might cause the description control to
	 * be added to the section dynamically as it is added
	 * when it is needed first.
	 *
	 * @param description The description text to set.
	 */
	public void setDescription(String description) {
		if (getSection().isDisposed())
			return;
		if (descriptionControl == null) {
			descriptionControl = new Text(getSection(), SWT.READ_ONLY | SWT.WRAP);
			getSection().setDescriptionControl(descriptionControl);
			getSection().getDescriptionControl().setData(IToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
		}
		getSection().setDescription(description);
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.message.IMessageDisplayer#setMessage(java.lang.String, org.nightlabs.base.ui.composite.MessageComposite.MessageType)
	 */
	public void setMessage(String message, MessageType type)
	{
		setMessage(message, type.ordinal());
	}
}
