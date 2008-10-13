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
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.eclipse.ui.pdfviewer.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfSimpleNavigator;
import org.nightlabs.eclipse.ui.pdfviewer.PdfThumbnailNavigator;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerActionRegistry;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.UseCase;
import org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar.PdfCoolBar;
import org.nightlabs.util.CollectionUtil;

/**
 * Off-the-shelf composite for displaying PDF files. This comprises the raw PDF viewing area (see {@link PdfViewer}) as well
 * as a simple page-based navigator (see {@link PdfSimpleNavigator}) and a thumbnail navigator (see {@link PdfThumbnailNavigator}).
 * If you don't like the way this composite looks like, please compose your own out of the various parts
 * provided by the projects <code>org.nightlabs.eclipse.ui.pdfviewer</code> and <code>org.nightlabs.eclipse.ui.pdfviewer.extension</code>.
 *
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfViewerComposite extends Composite
{
//	private static final Logger logger = Logger.getLogger(PdfViewerComposite.class);

	public static final UseCase USE_CASE_DEFAULT = new UseCase("default");

	private SashForm sashForm;
	private PdfViewer pdfViewer;
	private Control pdfViewerControl;
	private PdfSimpleNavigator pdfSimpleNavigator;
	private Control pdfSimpleNavigatorControl;
	private PdfThumbnailNavigator pdfThumbnailNavigator;
	private Control pdfThumbnailNavigatorControl;
	private PdfViewerActionRegistry pdfViewerActionRegistry;
	private PdfCoolBar pdfCoolBar;
	private Control pdfCoolBarControl;
	private EnumSet<PdfViewerCompositeOption> options = EnumSet.noneOf(PdfViewerCompositeOption.class);


	public PdfViewerComposite(Composite parent, int style) {
		this(parent, style, null);
	}

	public PdfViewerComposite(final Composite parent, int style, PdfDocument pdfDocument) {
		this(parent, style, pdfDocument, (PdfViewerCompositeOption[])null);
	}
	public PdfViewerComposite(final Composite parent, int style, PdfDocument pdfDocument, PdfViewerCompositeOption ... options)
	{
		super(parent, style);
		if (options != null)
			this.options.addAll(CollectionUtil.array2ArrayList(options));

		GridLayout gl = new GridLayout();
		gl.marginLeft = 0;
		gl.marginRight = 0;
		gl.marginTop = 0;
		gl.marginBottom = 0;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		this.setLayout(gl);

		pdfViewer = new PdfViewer();

		if (!this.options.contains(PdfViewerCompositeOption.NO_COOL_BAR)) {
			pdfViewerActionRegistry = new PdfViewerActionRegistry(pdfViewer, USE_CASE_DEFAULT, PdfCoolBar.class.getName());
			pdfCoolBar = new PdfCoolBar(pdfViewerActionRegistry);
			pdfCoolBarControl = pdfCoolBar.createControl(this, SWT.BORDER);
			pdfCoolBarControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		if (!this.options.contains(PdfViewerCompositeOption.NO_THUMBNAIL_NAVIGATOR)) {
			sashForm = new SashForm(this, SWT.NONE);
			sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		}

		// PDF viewer fires a property change event concerning the property "PROPERTY_PDF_DOCUMENT"
		// when setting the given PDF document here. PDF thumbnail navigator will receive this event (when created)
		// and will call setDocument for its corresponding composite to load the given PDF document in its composite.
		pdfViewer.setPdfDocument(pdfDocument);

		final Composite leftComp;
		if (sashForm == null)
			leftComp = null;
		else{
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

		if (!this.options.contains(PdfViewerCompositeOption.NO_THUMBNAIL_NAVIGATOR)) {
			pdfThumbnailNavigator = new PdfThumbnailNavigator(pdfViewer);

			// creating the control of PDF thumbnail navigator (the composite of this control creates a new instance of PDF viewer!)
			pdfThumbnailNavigatorControl = pdfThumbnailNavigator.createControl(leftComp, SWT.BORDER);
//			pdfThumbnailNavigatorControl = new Label(leftComp, SWT.BORDER);
			pdfThumbnailNavigatorControl.setLayoutData(new GridData(GridData.FILL_BOTH));
		}

		// creating the control of PDF viewer (a new PDF viewer composite will be created)
		if (sashForm == null)
			pdfViewerControl = pdfViewer.createControl(this, SWT.BORDER);
		else
			pdfViewerControl = pdfViewer.createControl(sashForm, SWT.BORDER);

		pdfViewerControl.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (!this.options.contains(PdfViewerCompositeOption.NO_SIMPLE_NAVIGATOR)) {
			// creating PDF simple navigator and its corresponding control (a new PDF simple navigator composite will be created)
			pdfSimpleNavigator = new PdfSimpleNavigator(pdfViewer);
			if (leftComp == null)
				pdfSimpleNavigatorControl = pdfSimpleNavigator.createControl(this, SWT.BORDER);
			else
				pdfSimpleNavigatorControl = pdfSimpleNavigator.createControl(leftComp, SWT.BORDER);

			pdfSimpleNavigatorControl.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
		}

		if (leftComp != null && sashForm != null) {
			leftComp.addControlListener(new ControlListener() {
				@Override
				public void controlMoved(ControlEvent event) {
				}

				@Override
				public void controlResized(ControlEvent event) {
					final int[] weights = new int[2];
					int minWidth = pdfSimpleNavigatorControl == null ? leftComp.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 10 : pdfSimpleNavigatorControl.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
					if (leftComp.getClientArea().width < minWidth) {
						int total = 10000;
						weights[0] = minWidth * total / sashForm.getClientArea().width;
						weights[1] = total - weights[0];
						leftComp.getDisplay().asyncExec(new Runnable() {
							public void run() {
								sashForm.setWeights(weights);
							}
						});
					}
				}

			});
		}

//		if (sashForm != null)
//			sashForm.setWeights(new int[] {1, 3});
//
//		layout(true, true);

//		parent.getDisplay().asyncExec(new Runnable() {
//			@Override
//			public void run() {
				if (sashForm != null)
					sashForm.setWeights(new int[] {1, 100});

				parent.layout(true, true);
//			}
//		});

//		// TODO in the case the PDF viewer component has been resized weights have to be computed again (not optimal yet)
//		parent.getDisplay().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				if (sashForm == null)
//					return;
//
//				if (logger.isDebugEnabled()) {
//					if (pdfSimpleNavigatorControl != null) {
//						logger.debug("simple navigator control width (computed) " + pdfSimpleNavigatorControl.getSize().x); //$NON-NLS-1$
//						logger.debug("simple navigator control width (actual) " + pdfSimpleNavigatorControl.getBounds().width); //$NON-NLS-1$
//					}
//					if (pdfThumbnailNavigatorControl != null) {
//						logger.debug("thumbnail navigator control width " + pdfThumbnailNavigatorControl.getBounds().width); //$NON-NLS-1$
//						logger.debug("thumbnail navigator control border width " + pdfThumbnailNavigatorControl.getBorderWidth()); //$NON-NLS-1$
//					}
//					logger.debug("pdf viewer control border width " + pdfViewerControl.getBorderWidth()); //$NON-NLS-1$
//					logger.debug("PdfViewerComposite.this width " + PdfViewerComposite.this.getBounds().width); //$NON-NLS-1$
//				}
//
////				int absoluteWidth = (
////						(pdfSimpleNavigatorControl == null ? WIDTH_WITHOUT_SIMPLE_NAVIGATOR : pdfSimpleNavigatorControl.getBounds().width) +
////						pdfThumbnailNavigatorControl.getBorderWidth() * 2 +
////						pdfViewerControl.getBorderWidth()
////				);
//				int absoluteWidth = getBounds().width;
//
//				int relativeWidth = (int) Math.ceil(100f * absoluteWidth / parent.getBounds().width);
//
//				if (logger.isDebugEnabled()) {
//					logger.debug("relativeWeight=" + relativeWidth); //$NON-NLS-1$
//					logger.debug("absoluteWidth=" + absoluteWidth + " relativeWidth=" + relativeWidth + " " + parent.getBounds().width);
//				}
//
////				sashForm.setWeights(new int[]{relativeWidth, 100 - relativeWidth});
//				sashForm.setWeights(new int[] {1, 3});
//
//				parent.layout(true, true);
//
////				// For a reason which I do not understand, the horizontal scroll bar become suddenly visible. So, we simply hide it here.
////				getHorizontalBar().setVisible(false);
////				getVerticalBar().setVisible(false);
//			}
//		});
	}

	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}

}
