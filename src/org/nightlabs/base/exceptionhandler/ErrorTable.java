/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
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
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.exceptionhandler;


import java.util.Collection;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.layout.WeightedTableLayout;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.table.AbstractTableComposite;
import org.nightlabs.base.table.TableContentProvider;
import org.nightlabs.base.table.TableLabelProvider;

/**
 * This class is supposed to display a Collection of <code>ErrorItem</code>s
 * using two columns.
 * 
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public class ErrorTable extends AbstractTableComposite
{
	private static class ContentProvider extends TableContentProvider
	{
		@Override
		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof Collection)
				return ((Collection)inputElement).toArray();			
			return super.getElements(inputElement);
		}		
	}
	
	
	private static class LabelProvider extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof ErrorItem)
			{
				ErrorItem error = (ErrorItem)element;
				switch(columnIndex)
				{
				case 0:
					return error.getThrownException().getClass().getName();
				case 1:
					return error.getMessage();
				}
			}
			
			return ""; //$NON-NLS-1$
		}		
	}

	public ErrorTable(Composite parent, int style)
	{
		super(parent, style);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		(new TableColumn(table, SWT.LEFT)).setText(Messages.getString("org.nightlabs.base.exceptionhandler.ErrorTable.tableColumnExceptionClass")); //$NON-NLS-1$
		(new TableColumn(table, SWT.LEFT)).setText(Messages.getString("org.nightlabs.base.exceptionhandler.ErrorTable.tableColumnMessage")); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[] {3, 4}));
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}
	
	public void setInput(Collection input) 
	{
		getTableViewer().setInput(input);
		if (input.size() == 1)
			getTable().setSelection(0);
	}
	
	public ErrorItem getSelectedItem() {
		TableItem[] selection = getTable().getSelection();
		if (selection.length > 0)
			return (ErrorItem)selection[0].getData();
		return null;
	}
	public void setSelectedItem(ErrorItem toBeSelected)
	{
		TableItem[] tableItems = getTable().getItems();
//		ErrorItem current;
		for(int i = 0; i < tableItems.length; i++)
		{
			if(((ErrorItem)tableItems[i].getData()) == toBeSelected)
			{
				getTable().setSelection(i);
				return;
			}			
		}
	}
}

