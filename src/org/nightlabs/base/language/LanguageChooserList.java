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

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.layout.WeightedTableLayout;
import org.nightlabs.language.LanguageCf;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class LanguageChooserList
	extends AbstractLanguageChooser
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(LanguageChooserList.class);
	
	private LanguageTableContentProvider contentProvider;
	private LanguageTableLabelProvider labelProvider;
	private TableViewer viewer;
	
	public LanguageChooserList(Composite parent)
	{
		this(parent, true);
	}
	public LanguageChooserList(Composite parent, boolean grabExcessHorizontalSpace)
	{
		super(parent, SWT.NONE, true);
		((GridData)getLayoutData()).grabExcessHorizontalSpace = grabExcessHorizontalSpace;

		contentProvider = new LanguageTableContentProvider();
		labelProvider = new LanguageTableLabelProvider();
		viewer = new TableViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				logger.debug("new language: "+getLanguage().getLanguageID()); //$NON-NLS-1$
				fireLanguageChangeEvent();
			}
		});

		Table t = viewer.getTable();
//		t.setHeaderVisible(true);
		t.setLinesVisible(true);

		GridData tgd = new GridData(GridData.FILL_BOTH);
		tgd.horizontalSpan = 1;
		tgd.verticalSpan = 1;

		t.setLayoutData(tgd);
		t.setLayout(new WeightedTableLayout(new int[] {1}));

		// Add the columns to the table
		new TableColumn(t, SWT.LEFT).setText("language"); //$NON-NLS-1$ // Because the header is not visible, we don't need to localise this String.
		
		// must be called AFTER all columns are added
		viewer.setInput(contentProvider);
		
		StructuredSelection selection = new StructuredSelection(
				LanguageManager.sharedInstance().getCurrentLanguage());
		viewer.setSelection(selection, true);
	}

	/**
	 * @see org.nightlabs.base.language.LanguageChooser#getLanguage()
	 */
	public LanguageCf getLanguage()
	{
		StructuredSelection selection = (StructuredSelection) viewer.getSelection();
		return (LanguageCf) selection.getFirstElement();
	}

}
