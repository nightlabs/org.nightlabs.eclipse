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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class I18nTextEditorMultiLine 
extends I18nTextEditor 
{
	/**
	 * @param parent
	 */
	public I18nTextEditorMultiLine(Composite parent) {
		this(parent, (String) null);
	}

	/**
	 * @param parent
	 * @param caption
	 */
	public I18nTextEditorMultiLine(Composite parent, String caption) {
		this(parent, (LanguageChooser) null, caption);
	}

	/**
	 * @param parent
	 * @param languageChooser
	 */
	public I18nTextEditorMultiLine(Composite parent, LanguageChooser languageChooser) {
		this(parent, languageChooser, (String) null);
	}

	/**
	 * @param parent
	 * @param chooser
	 * @param caption
	 */
	public I18nTextEditorMultiLine(Composite parent, LanguageChooser chooser, String caption) {
		this(parent, chooser, caption, DEFAULT_LINECOUNT);
	}

	/**
	 * @param parent
	 * @param chooser
	 * @param caption
	 */
	public I18nTextEditorMultiLine(Composite parent, LanguageChooser languageChooser, String caption,
			int lineCount) 
	{
		super(parent, languageChooser, caption, false);
		this.lineCount = lineCount;
		createContext(parent, languageChooser, caption);
	}

	// in order to provide a nice editor area without a vertical scrollbar that is properly displayed,
	// the line_count should be at least 3.
	public static final int DEFAULT_LINECOUNT = 3;
	private int singleLineHeight;
	private int lineCount = DEFAULT_LINECOUNT;

	@Override
	protected void createLanguageChooser(Composite parent)
	{
		int oldNumColumns = getGridLayout().numColumns;
		super.createLanguageChooser(parent);
		getGridLayout().numColumns = oldNumColumns;
	}
	
	@Override
	protected Text createText(Composite parent) 
	{
		Text text = new Text(parent, getBorderStyle() | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		singleLineHeight = text.getLineHeight();
		GridData gridData = new GridData(GridData.FILL_BOTH);
		int actualLineCount = Math.max(lineCount, DEFAULT_LINECOUNT);
		gridData.heightHint = actualLineCount * singleLineHeight;
		text.setLayoutData(gridData);		
		return text;
	}
	
}
