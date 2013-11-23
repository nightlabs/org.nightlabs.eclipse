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

package org.nightlabs.base.ui.property;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.celleditor.AWTColorCellEditor;
import org.nightlabs.base.ui.labelprovider.AWTColorLabelProvider;

/**
 * @author unascribed (probably Daniel Mazurek)
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class AWTColorPropertyDescriptor
extends XPropertyDescriptor
{
	private static final Logger logger = Logger.getLogger(AWTColorPropertyDescriptor.class);

	/**
	 * @param id
	 * @param displayName
	 */
	public AWTColorPropertyDescriptor(Object id, String displayName)
	{
		super(id, displayName);
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent)
	{
		CellEditor editor = new AWTColorCellEditor(parent);
		if (getValidator() != null)
			editor.setValidator(getValidator());

		return editor;
	}

	@Override
	public ILabelProvider getLabelProvider() {
		return super.getLabelProvider();
	}

	@Override
	public void onActivate() {
		if (logger.isDebugEnabled())
			logger.debug("onActivate: this=" + this); //$NON-NLS-1$

		setLabelProvider(new AWTColorLabelProvider());

		super.onActivate();
	}

	@Override
	public void onDeactivate() {
		if (logger.isDebugEnabled())
			logger.debug("onDeactivate: this=" + this); //$NON-NLS-1$

//		((AWTColorLabelProvider)getLabelProvider()).onDeactivate();
		getLabelProvider().dispose();
		setLabelProvider(null);
		super.onDeactivate();
	}
}
