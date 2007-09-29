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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class EntityEditorUtil
{
	/**
	 * Create a composite client for a section.
	 * @param toolkit The toolkit to use
	 * @param section The section for which to create the client
	 * @param numColumns How many columns the layout should have.
	 * @return The client Composite
	 */
	public static Composite createCompositeClient(FormToolkit toolkit, Section section, int numColumns)
	{
		Composite client = toolkit.createComposite(section);
//		XComposite client = new XComposite(section, SWT.NONE);
//		client.setToolkit(toolkit);
		GridLayout layout = new GridLayout();
		layout.marginRight = layout.marginLeft = toolkit.getBorderStyle() != SWT.NULL ? 0 : 2;
		layout.numColumns = numColumns;
		client.setLayout(layout);
		client.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.setClient(client);
		return client;
	}

}
