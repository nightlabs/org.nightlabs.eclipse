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

package org.nightlabs.base.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.table.CheckboxCellEditorHelper;


public class CheckboxPropertyDescriptor 
extends XPropertyDescriptor
{
	/**
	 * @param id
	 * @param displayName
	 */
	public CheckboxPropertyDescriptor(Object id, String displayName) 
	{
		super(id, displayName);
		init();
	}
	
	/**
	 * @param id
	 * @param displayName
	 * @param readOnly
	 */
	public CheckboxPropertyDescriptor(Object id, String displayName, boolean readOnly) 
	{
		super(id, displayName, readOnly);
		init();
	}
	
	protected void init() {
		setLabelProvider(new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				if (element instanceof Boolean) {
					boolean b = (Boolean) element;
					return CheckboxCellEditorHelper.getCellEditorImage(b, isReadOnly());
				}
				return super.getImage(element);		
			}
			@Override
			public String getText(Object element) {
				return ""; //$NON-NLS-1$
			}
		});
	}
	
	/*
	 * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
	 */
	public CellEditor createPropertyEditor(Composite parent) 
	{
		CellEditor editor = new CheckboxCellEditor(parent);
		return editor;
	}	
	
	private class CheckboxCellEditor
	extends org.eclipse.jface.viewers.CheckboxCellEditor 
	{
		public CheckboxCellEditor(Composite parent) {
			super(parent);			
		}
		
		@Override
		protected void doSetValue(Object value) {
			if (isReadOnly())
				return;
			super.doSetValue(value);
		}

		@Override
		public void activate() {
			if (isReadOnly())
				return;
			super.activate();
		}
	}
	
//	private void init() 
//	{
//		setValidator(new BooleanValidator());
//		setLabelProvider(new LabelProvider() {
//			@Override
//			public Image getImage(Object element) {
//				if (element instanceof Boolean) {
//					boolean b = (Boolean) element;
//					if (b)
//						return SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
//								CheckboxPropertyDescriptor.class, "True", "15x15", ImageFormat.png);
//					else
//						return SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
//								CheckboxPropertyDescriptor.class, "False", "15x15", ImageFormat.png);					
//				}
//				return super.getImage(element);
//			}
//			@Override
//			public String getText(Object element) {
//				return "";
//			}
//		});
//	}
//	
//	/*
//	 * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
//	 */
//	public CellEditor createPropertyEditor(Composite parent) 
//	{
//		CellEditor editor = new CheckboxCellEditor(parent, SWT.NONE, readOnly);
//		if (getValidator() != null)
//			editor.setValidator(getValidator());
//		return editor;
//	}

}

