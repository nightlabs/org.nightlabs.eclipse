/* ********************************************************************
 * NightLabs PDF Viewer - http://www.nightlabs.org/projects/pdfviewer *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar.internal;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerActionRegistry;
import org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar.PdfCoolBar;

/**
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfCoolBarComposite extends Composite
{
	private PdfCoolBar pdfCoolBar;
	private PdfViewerActionRegistry pdfViewerActionRegistry;
	private CoolBar coolBar;
	private CoolBarManager coolBarManager;

	public PdfCoolBarComposite(Composite parent, int style, PdfCoolBar pdfCoolBar) {
		super(parent, style);
		this.setLayout(new FillLayout());

		this.pdfCoolBar = pdfCoolBar;
		this.pdfViewerActionRegistry = this.pdfCoolBar.getPdfViewerActionRegistry();

		coolBar = new CoolBar(this, SWT.NONE);
		coolBarManager = new CoolBarManager(coolBar);
		pdfViewerActionRegistry.contributeToCoolBar(coolBarManager);
		coolBar.setVisible(true);
		coolBarManager.update(true);
	}
}
