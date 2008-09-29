package org.nightlabs.eclipse.ui.pdfviewer.extension.editor;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.io.FileEditorInput;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.pdfviewer.AutoZoom;
import org.nightlabs.eclipse.ui.pdfviewer.OneDimensionalPdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfFileLoader;
import org.nightlabs.eclipse.ui.pdfviewer.PdfSimpleNavigator;
import org.nightlabs.eclipse.ui.pdfviewer.PdfThumbnailNavigator;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;

import com.sun.pdfview.PDFFile;

/**
 * This editor displays a PDF file. It can be opened with the following implementations of
 * {@link IEditorInput}:
 * <ul>
 *	<li>{@link PdfViewerEditorInput}</li>: Supports {@link File} and byte array as data source.
 *	<li>{@link FileEditorInput}</li>: Supports {@link File} as data source.
 *	<li>{@link IPathEditorInput}</li>
 * </ul>
 *
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfViewerEditor extends EditorPart
{
	private static final Logger logger = Logger.getLogger(PdfViewerEditor.class);
	public static final String ID = PdfViewerEditor.class.getName();
	private volatile PdfDocument pdfDocument;
	private PdfViewer pdfViewer;
	private Control pdfViewerControl;
	private PdfSimpleNavigator pdfSimpleNavigator;
	private Control pdfSimpleNavigatorControl;
	private PdfThumbnailNavigator pdfThumbnailNavigator;
	private Control pdfThumbnailNavigatorControl;
	private PDFFile pdfFile;
//	private SashForm page;

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO implement later
	}

	@Override
	public void doSaveAs() {
/*		IProgressMonitor monitor = new NullProgressMonitor();
		String pathName = "abc.pdf";
		try {
			PdfFileSaver.savePdfAs(pathName, monitor);
		}
		catch (final Exception x) {
			logger.error("Error while saving PDF file!", x);
		}*/
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInput(input);

		// strange that the following lines are necessary - the tool tip pops up automatically - why does the part name not?
		if (input.getName() != null)
			setPartName(input.getName());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false; // TODO implement later
	}

	private Label loadingMessageLabel = null;

	@Override
	public void createPartControl(final Composite parent) {
//		final SashForm page = new SashForm(parent, SWT.VERTICAL);  //new Composite(parent, SWT.NONE);
//		final Composite page = new Composite(parent, SWT.NONE);
//		page.setLayout(new GridLayout(2, false));

		final Composite loadingMessagePage = new Composite(parent, SWT.NONE);
		loadingMessagePage.setLayout(new GridLayout());
		loadingMessageLabel = new Label(loadingMessagePage, SWT.NONE);
		GridData gdLoadingMessageLabel = new GridData(GridData.FILL_HORIZONTAL);
		gdLoadingMessageLabel.verticalIndent = 16;
		gdLoadingMessageLabel.horizontalIndent = 16;
		loadingMessageLabel.setLayoutData(gdLoadingMessageLabel);
		loadingMessageLabel.setText("Loading PDF file...");

		final Display display = loadingMessageLabel.getDisplay();

		final IEditorInput input = getEditorInput();
		Job job = new Job("Loading PDF file") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Loading PDF file", 100);
				try {
					if (input instanceof PdfViewerEditorInput) {
						pdfFile = ((PdfViewerEditorInput)input).getPDFFile();
						if (pdfFile == null)
							pdfFile = ((PdfViewerEditorInput)input).createPDFFile(new SubProgressMonitor(monitor, 20));
					}
					else if (input instanceof FileEditorInput) {
						File file = ((FileEditorInput)input).getFile();
						pdfFile = PdfFileLoader.loadPdf(file, new SubProgressMonitor(monitor, 20));
					}
					else if (input instanceof IPathEditorInput) {
						File file = ((IPathEditorInput)input).getPath().toFile();
						pdfFile = PdfFileLoader.loadPdf(file, new SubProgressMonitor(monitor, 20));
					}
				// I have no idea, in which plugin this IURIEditorInput can be found - thus we don't support it
				// (maybe it's not worth another dependency anyway - or maybe solve it by an optional dependency, later).
				// Marco.
//					else if (input instanceof IURIEditorInput) {
//						URI uri = ((IURIEditorInput)input).getURI();
//						InputStream in = uri.toURL().openStream();
//						try {
//							pdfFile = PdfFileLoader.loadPdf(in);
//						} finally {
//							in.close();
//						}
//					}
					else
						throw new IllegalArgumentException("Editor input is an unsupported type! class=" + input.getClass() + " instance=" + input);

					pdfDocument = new OneDimensionalPdfDocument(pdfFile, new SubProgressMonitor(monitor, 80));
				} catch (final Exception x) {
					logger.error("Error while reading PDF file!", x);

					display.asyncExec(new Runnable() {
						public void run() {
							if (parent.isDisposed())
								return;

							RCPUtil.closeEditor(input, false);
						}
					});

					throw new RuntimeException(x);
				} finally {
					monitor.done();
				}

				display.asyncExec(new Runnable() {
					public void run() {
						if (parent.isDisposed())
							return;

						loadingMessagePage.dispose();
						SashForm page = new SashForm(parent, SWT.HORIZONTAL);

						pdfViewer = new PdfViewer();

						// PDF viewer fires a property change event concerning the property "PROPERTY_PDF_DOCUMENT"
						// when setting the given PDF document here. PDF thumb-nail navigator will receive this event (when created)
						// and will call setDocument for its corresponding composite to load the given PDF document in its composite.
						pdfViewer.setPdfDocument(pdfDocument);

						pdfThumbnailNavigator = new PdfThumbnailNavigator(pdfViewer);

						Composite leftComp = new Composite(page, SWT.NONE);
						leftComp.setLayout(new GridLayout());

						// creating the control of PDF thumb-nail navigator (the composite of this control creates a new instance of PDF viewer!)
						pdfThumbnailNavigatorControl = pdfThumbnailNavigator.createControl(leftComp, SWT.BORDER);
						pdfThumbnailNavigatorControl.setLayoutData(new GridData(GridData.FILL_BOTH));

						// creating the control of PDF viewer (a new PDF viewer composite will be created)
						pdfViewerControl = pdfViewer.createControl(page, SWT.BORDER);
						pdfViewerControl.setLayoutData(new GridData(GridData.FILL_BOTH));

						// creating PDF simple navigator and its corresponding control (a new PDF simple navigator composite will be created)
						pdfSimpleNavigator = new PdfSimpleNavigator(pdfViewer);
						pdfSimpleNavigatorControl = pdfSimpleNavigator.createControl(leftComp, SWT.NONE);
						pdfSimpleNavigatorControl.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));

						pdfThumbnailNavigator.getThumbnailPdfViewer().setAutoZoom(AutoZoom.pageWidth);
						pdfThumbnailNavigator.getThumbnailPdfViewer().setZoomIsAllowed(false);
						pdfThumbnailNavigator.getThumbnailPdfViewer().setDrawShadowAroundChosenPageOnMouseClick(true);

						if (logger.isDebugEnabled()) {
							logger.info("simple navigator control width " + pdfSimpleNavigatorControl.getBounds().width);
							logger.info("thumbnail navigator control width " + pdfThumbnailNavigatorControl.getBounds().width);
							logger.info("thumbnail navigator control border width " + pdfThumbnailNavigatorControl.getBorderWidth());
							logger.info("pdf viewer control border width " + pdfViewerControl.getBorderWidth());
							logger.info("parent width " + parent.getBounds().width);
						}

						// TODO in the case the PDF viewer component has been resized weights have to be computed again (not optimal yet)
						int absoluteWidth = pdfSimpleNavigatorControl.getBounds().width +
											pdfThumbnailNavigatorControl.getBorderWidth() * 2 +
											pdfViewerControl.getBorderWidth();

						int relativeWidth = (int) Math.ceil(100f * absoluteWidth / parent.getBounds().width);
						page.setWeights(new int[]{relativeWidth, 100 - relativeWidth});

						parent.layout(true, true);

//						pdfThumbnailNavigator.setPdfDocumentFactory(new PdfThumbnailNavigator.PdfDocumentFactory() {
//							public PdfDocument createPdfDocument(PdfDocument pdfDocument)
//							{
//								return new OneDimensionalPdfDocument(pdfDocument.getPdfFile(), OneDimensionalPdfDocument.Layout.vertical, new NullProgressMonitor());
//							}
//						});
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	@Override
	public void setFocus() {
		if (pdfViewerControl != null) {
			pdfViewerControl.setFocus();
		}
	}
}
