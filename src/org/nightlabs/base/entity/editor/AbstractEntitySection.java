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
package org.nightlabs.base.entity.editor;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.editor.RestorableSectionPart;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @deprecated is never used
 */
public abstract class AbstractEntitySection<T> 
extends RestorableSectionPart 
{
	public AbstractEntitySection(IFormPage page, Composite parent, int style, String sectionText) {
		super(parent, page.getEditor().getToolkit(), style);
		init(page, sectionText);
	}

	public AbstractEntitySection(IFormPage page, Composite parent, String sectionText) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		init(page, sectionText);		
	}
	
	protected void init(IFormPage page, String text) 
	{
		FormToolkit toolkit = page.getEditor().getToolkit();
		Section section = getSection();
		section.setText(text);
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite container = EntityEditorUtil.createCompositeClient(toolkit, section, 1);
		((GridLayout)container.getLayout()).numColumns = 1;
		createComposite(container);
	}
	
	public abstract void copyFromObjectToUI(T object);	
	public abstract void copyFromUIToObject(T object);
	
	protected abstract void createComposite(Composite parent);
}
