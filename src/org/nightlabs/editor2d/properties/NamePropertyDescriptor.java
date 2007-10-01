/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.celleditor.XI18nTextCellEditor;
import org.nightlabs.base.ui.labelprovider.I18nTextLabelProvider;
import org.nightlabs.base.ui.property.XPropertyDescriptor;
import org.nightlabs.editor2d.DrawComponent;

public class NamePropertyDescriptor 
extends XPropertyDescriptor
{
	public NamePropertyDescriptor(DrawComponent dc, Object id, String displayName) {
		this(dc, id, displayName, false);
	}

	public NamePropertyDescriptor(DrawComponent dc, Object id, String displayName, 
			boolean readOnly) {
		super(id, displayName, readOnly);
		if (dc != null)
			setLabelProvider(new I18nTextLabelProvider(dc.getRoot().getLanguageID(), false));
		else
			setLabelProvider(new I18nTextLabelProvider(false));
	}	

//	public NamePropertyDescriptor(Object id, String displayName) {
//		this(id, displayName, false);
//	}
//
//	public NamePropertyDescriptor(Object id, String displayName, boolean readOnly) {
//		super(id, displayName, readOnly);
//		setLabelProvider(new I18nTextLabelProvider(false));
//	}	
	
  @Override
  public CellEditor createPropertyEditor(Composite parent) {
  	return new XI18nTextCellEditor(parent);
  }
  
}
