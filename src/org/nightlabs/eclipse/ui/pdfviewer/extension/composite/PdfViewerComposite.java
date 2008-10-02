package org.nightlabs.eclipse.ui.pdfviewer.extension.composite;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.eclipse.ui.pdfviewer.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfSimpleNavigator;
import org.nightlabs.eclipse.ui.pdfviewer.PdfThumbnailNavigator;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;

/**
 * Off-the-shelf composite for displaying PDF files. This comprises the raw PDF viewing area (see {@link PdfViewer}) as well
 * as a simple page-based navigator (see {@link PdfSimpleNavigator}) and a thumbnail navigator (see {@link PdfThumbnailNavigator}).
 * If you don't like the way this composite looks like, please compose your own out of the various parts
 * provided by the projects <code>org.nightlabs.eclipse.ui.pdfviewer</code> and <code>org.nightlabs.eclipse.ui.pdfviewer.extension</code>.
 *
 * @author frederik löser - frederik at nightlabs dot de
 */
public class PdfViewerComposite extends SashForm {

	private static final Logger logger = Logger.getLogger(PdfViewerComposite.class);
	private PdfViewer pdfViewer;
	private Control pdfViewerControl;
	private PdfSimpleNavigator pdfSimpleNavigator;
	private Control pdfSimpleNavigatorControl;
	private PdfThumbnailNavigator pdfThumbnailNavigator;
	private Control pdfThumbnailNavigatorControl;

	public PdfViewerComposite(Composite parent, int style) {
		this(parent, style, null);
	}

	public PdfViewerComposite(final Composite parent, int style, PdfDocument pdfDocument) {
		super(parent, SWT.HORIZONTAL);

		pdfViewer = new PdfViewer();

		// PDF viewer fires a property change event concerning the property "PROPERTY_PDF_DOCUMENT"
		// when setting the given PDF document here. PDF thumbnail navigator will receive this event (when created)
		// and will call setDocument for its corresponding composite to load the given PDF document in its composite.
		if (pdfDocument != null)
			pdfViewer.setPdfDocument(pdfDocument);

		pdfThumbnailNavigator = new PdfThumbnailNavigator(pdfViewer);

		Composite leftComp = new Composite(this, SWT.NONE);
		leftComp.setLayout(new GridLayout());

		// creating the control of PDF thumbnail navigator (the composite of this control creates a new instance of PDF viewer!)
		pdfThumbnailNavigatorControl = pdfThumbnailNavigator.createControl(leftComp, SWT.BORDER);
		pdfThumbnailNavigatorControl.setLayoutData(new GridData(GridData.FILL_BOTH));

		// creating the control of PDF viewer (a new PDF viewer composite will be created)
		pdfViewerControl = pdfViewer.createControl(this, SWT.BORDER);
		pdfViewerControl.setLayoutData(new GridData(GridData.FILL_BOTH));

		// creating PDF simple navigator and its corresponding control (a new PDF simple navigator composite will be created)
		pdfSimpleNavigator = new PdfSimpleNavigator(pdfViewer);
		pdfSimpleNavigatorControl = pdfSimpleNavigator.createControl(leftComp, SWT.BORDER);
		pdfSimpleNavigatorControl.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
		setWeights(new int[] {1, 3});
		layout(true, true);

		// TODO in the case the PDF viewer component has been resized weights have to be computed again (not optimal yet)
		parent.getDisplay().asyncExec(new Runnable() {
			@Override
            public void run() {
				if (logger.isDebugEnabled()) {
					logger.debug("simple navigator control width (computed) " + pdfSimpleNavigatorControl.getSize().x); //$NON-NLS-1$
					logger.debug("simple navigator control width (actual) " + pdfSimpleNavigatorControl.getBounds().width); //$NON-NLS-1$
					logger.debug("thumbnail navigator control width " + pdfThumbnailNavigatorControl.getBounds().width); //$NON-NLS-1$
					logger.debug("thumbnail navigator control border width " + pdfThumbnailNavigatorControl.getBorderWidth()); //$NON-NLS-1$
					logger.debug("pdf viewer control border width " + pdfViewerControl.getBorderWidth()); //$NON-NLS-1$
					logger.debug("PdfViewerComposite.this width " + PdfViewerComposite.this.getBounds().width); //$NON-NLS-1$
				}

				int absoluteWidth = pdfSimpleNavigatorControl.getBounds().width +
									pdfThumbnailNavigatorControl.getBorderWidth() * 2 +
									pdfViewerControl.getBorderWidth();

				int relativeWidth = (int) Math.ceil(100f * absoluteWidth / /*PdfViewerComposite.this.*/parent.getBounds().width);
				logger.debug("relativeWeight " + relativeWidth); //$NON-NLS-1$
				logger.info(absoluteWidth + " " + relativeWidth + " " + parent.getBounds().width);
				PdfViewerComposite.this.setWeights(new int[]{relativeWidth, 100 - relativeWidth});
				/*PdfViewerComposite.this.*/parent.layout(true, true);
            }
		});
	}

	public PdfViewer getPdfViewer() {
    	return pdfViewer;
    }

}
