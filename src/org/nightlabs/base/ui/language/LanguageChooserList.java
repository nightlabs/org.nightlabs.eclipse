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

package org.nightlabs.base.ui.language;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.language.LanguageCf;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class LanguageChooserList
	extends AbstractLanguageChooser
{
	/**
	 * LOG4J logger used by this class.
	 */
	private static final Logger logger = Logger.getLogger(LanguageChooserList.class);

	private LanguageTableContentProvider contentProvider;
	private LanguageTableLabelProvider labelProvider;
	private TableViewer viewer;

	public LanguageChooserList(Composite parent) {
		this(parent, true);
	}

	public LanguageChooserList(Composite parent, boolean grabExcessHorizontalSpace) {
		this(parent, true, false);
	}

	public LanguageChooserList(Composite parent, boolean grabExcessHorizontalSpace, boolean showHeader)
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
		new TableColumn(t, SWT.LEFT).setText(Messages.getString("org.nightlabs.base.ui.language.LanguageChooserList.column.language")); //$NON-NLS-1$
		t.setHeaderVisible(showHeader);

		// must be called AFTER all columns are added
		viewer.setInput(contentProvider);

		StructuredSelection selection = new StructuredSelection(
				LanguageManager.sharedInstance().getCurrentLanguage());
		viewer.setSelection(selection, true);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.language.LanguageChooser#getLanguage()
	 */
	public LanguageCf getLanguage()
	{
		StructuredSelection selection = (StructuredSelection) viewer.getSelection();
		return (LanguageCf) selection.getFirstElement();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.language.LanguageChooser#getLanguages()
	 */
	@Override
	public Collection<LanguageCf> getLanguages()
	{
		return LanguageManager.sharedInstance().getLanguages();
	}

	/**
	 * @param listener
	 * @see org.eclipse.jface.viewers.StructuredViewer#addDoubleClickListener(org.eclipse.jface.viewers.IDoubleClickListener)
	 */
	public void addDoubleClickListener(IDoubleClickListener listener) {
		viewer.addDoubleClickListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.jface.viewers.StructuredViewer#removeDoubleClickListener(org.eclipse.jface.viewers.IDoubleClickListener)
	 */
	public void removeDoubleClickListener(IDoubleClickListener listener) {
		viewer.removeDoubleClickListener(listener);
	}
}
