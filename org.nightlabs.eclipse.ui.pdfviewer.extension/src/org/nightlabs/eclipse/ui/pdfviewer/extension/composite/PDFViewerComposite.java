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
package org.nightlabs.eclipse.ui.pdfviewer.extension.composite;

import java.util.EnumSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.eclipse.ui.pdfviewer.PDFDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PDFSimpleNavigator;
import org.nightlabs.eclipse.ui.pdfviewer.PDFThumbnailNavigator;
import org.nightlabs.eclipse.ui.pdfviewer.PDFViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PDFViewerActionRegistry;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.UseCase;
import org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar.PDFCoolBar;
import org.nightlabs.util.CollectionUtil;

/**
 * Off-the-shelf composite for displaying PDF files. This comprises the raw PDF viewing area (see {@link PDFViewer}) as well
 * as a simple page-based navigator (see {@link PDFSimpleNavigator}) and a thumbnail navigator (see {@link PDFThumbnailNavigator}).
 * If you don't like the way this composite looks like, please compose your own out of the various parts
 * provided by the projects <code>org.nightlabs.eclipse.ui.pdfviewer</code> and <code>org.nightlabs.eclipse.ui.pdfviewer.extension</code>.
 *
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PDFViewerComposite extends Composite
{
//	private static final Logger logger = Logger.getLogger(PdfViewerComposite.class);

	public static final UseCase USE_CASE_DEFAULT = new UseCase("default"); //$NON-NLS-1$

	private SashForm sashForm;
	private PDFViewer pdfViewer;
	private Control pdfViewerControl;
	private PDFSimpleNavigator pdfSimpleNavigator;
	private Control pdfSimpleNavigatorControl;
	private PDFThumbnailNavigator pdfThumbnailNavigator;
	private Control pdfThumbnailNavigatorControl;
	private PDFViewerActionRegistry pdfViewerActionRegistry;
	private PDFCoolBar pdfCoolBar;
	private Control pdfCoolBarControl;
	private EnumSet<PDFViewerCompositeOption> options = EnumSet.noneOf(PDFViewerCompositeOption.class);

	private Composite leftComp;

	public PDFViewerComposite(final Composite parent, final int style) {
		this(parent, style, null);
	}

	public PDFViewerComposite(final Composite parent, final int style, final PDFDocument pdfDocument) {
		this(parent, style, pdfDocument, (PDFViewerCompositeOption[])null);
	}
	public PDFViewerComposite(final Composite parent, final int style, final PDFDocument pdfDocument, final PDFViewerCompositeOption ... options)
	{
		super(parent, style);
		if (options != null) {
			this.options.addAll(CollectionUtil.array2ArrayList(options));
		}

		GridLayout gl = new GridLayout();
		gl.marginLeft = 0;
		gl.marginRight = 0;
		gl.marginTop = 0;
		gl.marginBottom = 0;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		this.setLayout(gl);

		pdfViewer = new PDFViewer();

		if (!this.options.contains(PDFViewerCompositeOption.NO_COOL_BAR)) {
			pdfViewerActionRegistry = new PDFViewerActionRegistry(pdfViewer, USE_CASE_DEFAULT, PDFCoolBar.class.getName());
			pdfCoolBar = new PDFCoolBar(pdfViewerActionRegistry);
			pdfCoolBarControl = pdfCoolBar.createControl(this, SWT.BORDER);
			pdfCoolBarControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		if (!this.options.contains(PDFViewerCompositeOption.NO_THUMBNAIL_NAVIGATOR)) {
			sashForm = new SashForm(this, SWT.NONE);
			sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		}

		// PDF viewer fires a property change event concerning the property "PROPERTY_PDF_DOCUMENT"
		// when setting the given PDF document here. PDF thumbnail navigator will receive this event (when created)
		// and will call setDocument for its corresponding composite to load the given PDF document in its composite.
		pdfViewer.setPDFDocument(pdfDocument);

		if (sashForm == null) {
			leftComp = null;
		} else{
			leftComp = new Composite(sashForm, SWT.NONE);
			gl = new GridLayout();
			gl.marginLeft = 0;
			gl.marginRight = 0;
			gl.marginTop = 0;
			gl.marginBottom = 0;
			gl.marginWidth = 0;
			gl.marginHeight = 0;
			leftComp.setLayout(gl);
		}

		if (!this.options.contains(PDFViewerCompositeOption.NO_THUMBNAIL_NAVIGATOR)) {
			pdfThumbnailNavigator = new PDFThumbnailNavigator(pdfViewer);

			// creating the control of PDF thumbnail navigator (the composite of this control creates a new instance of PDF viewer!)
			pdfThumbnailNavigatorControl = pdfThumbnailNavigator.createControl(leftComp, SWT.BORDER);
//			pdfThumbnailNavigatorControl = new Label(leftComp, SWT.BORDER);
			pdfThumbnailNavigatorControl.setLayoutData(new GridData(GridData.FILL_BOTH));
		}

		// creating the control of PDF viewer (a new PDF viewer composite will be created)
		if (sashForm == null) {
			pdfViewerControl = pdfViewer.createControl(this, SWT.BORDER);
		} else {
			pdfViewerControl = pdfViewer.createControl(sashForm, SWT.BORDER);
		}

		pdfViewerControl.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (!this.options.contains(PDFViewerCompositeOption.NO_SIMPLE_NAVIGATOR)) {
			// creating PDF simple navigator and its corresponding control (a new PDF simple navigator composite will be created)
			pdfSimpleNavigator = new PDFSimpleNavigator(pdfViewer);
			if (leftComp == null) {
				pdfSimpleNavigatorControl = pdfSimpleNavigator.createControl(this, SWT.BORDER);
			} else {
				pdfSimpleNavigatorControl = pdfSimpleNavigator.createControl(leftComp, SWT.BORDER);
			}

			pdfSimpleNavigatorControl.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));

			pdfSimpleNavigatorControl.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(final ControlEvent e) {
					ensureSizeConstraints();
				}
			});
		}

		if (leftComp != null && sashForm != null) {
			leftComp.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(final ControlEvent event) {
					ensureSizeConstraints();
				}
			});
		}

		if (sashForm != null) {
			sashForm.setWeights(new int[] {1, 100});
		}

		parent.layout(true, true);
	}

	public PDFViewer getPdfViewer() {
		return pdfViewer;
	}

	private void ensureSizeConstraints() {
		final int[] weights = new int[2];
		final int minWidth = pdfSimpleNavigatorControl == null ? leftComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 10 : pdfSimpleNavigatorControl.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		if (leftComp.getClientArea().width < minWidth) {
			final int sashWidth = sashForm.getClientArea().width;
			if (sashWidth < 1) {
				return;
			}

			final int total = 10000;
			weights[0] = minWidth * total / sashWidth;
			weights[1] = total - weights[0];
			if (weights[1] < 1) {
				return;
			}

			leftComp.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (!sashForm.isDisposed()) {
						sashForm.setWeights(weights);
					}
				}
			});
		}
	}
}
