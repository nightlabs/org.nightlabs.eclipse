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
package org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElement;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElementType;
import org.nightlabs.eclipse.ui.pdfviewer.PDFViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PDFViewerActionRegistry;
import org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar.internal.PDFCoolBarComposite;

/**
 * API for creating a cool bar (i.e. a bar holding tools like actions and other contributions).
 * Instantiate an instance of this class and use the {@link #createControl(Composite, int)} method
 * to place a cool bar into a composite.
 *
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class PDFCoolBar implements ContextElement<PDFCoolBar>
{
	public static final ContextElementType<PDFCoolBar> CONTEXT_ELEMENT_TYPE = new ContextElementType<PDFCoolBar>(PDFCoolBar.class);
	private PDFViewer pdfViewer;
	private String contextElementId;
	private PDFCoolBarComposite pdfCoolBarComposite;
	private PDFViewerActionRegistry pdfViewerActionRegistry;

	/**
	 * Checks if a given method is called on the SWT UI thread.
	 */
	private static void assertValidThread()
	{
		if (Display.getCurrent() == null) {
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!"); //$NON-NLS-1$
		}
	}

	public PDFCoolBar(final PDFViewerActionRegistry pdfViewerActionRegistry) {
		this(pdfViewerActionRegistry, null);
	}

	public PDFCoolBar(final PDFViewerActionRegistry pdfViewerActionRegistry, final String contextElementId) {
		assertValidThread();
		if (pdfViewerActionRegistry == null) {
			throw new IllegalArgumentException("pdfViewerActionRegistry must not be null!"); //$NON-NLS-1$
		}

		this.pdfViewer = pdfViewerActionRegistry.getPDFViewer();
		this.pdfViewerActionRegistry = pdfViewerActionRegistry;
		this.contextElementId = contextElementId;
		pdfViewer.registerContextElement(this);
	}

	public Control createControl(final Composite parent, final int style)
	{
		assertValidThread();

		if (this.pdfCoolBarComposite != null) {
			this.pdfCoolBarComposite.dispose();
			this.pdfCoolBarComposite = null;
		}

		pdfCoolBarComposite = new PDFCoolBarComposite(parent, style, this);

		return pdfCoolBarComposite;
	}

	public PDFViewerActionRegistry getPdfViewerActionRegistry() {
		return pdfViewerActionRegistry;
	}

	@Override
	public void onUnregisterContextElement() {
		// nothing to do
	}

	@Override
	public String getContextElementId() {
		return contextElementId;
	}

	@Override
	public ContextElementType<PDFCoolBar> getContextElementType() {
		return CONTEXT_ELEMENT_TYPE;
	}

	@Override
	public PDFViewer getPDFViewer() {
		return pdfViewer;
	}

}
