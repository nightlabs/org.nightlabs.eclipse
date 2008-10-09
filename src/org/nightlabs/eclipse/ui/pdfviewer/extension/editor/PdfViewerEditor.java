package org.nightlabs.eclipse.ui.pdfviewer.extension.editor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.io.FileEditorInput;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.pdfviewer.OneDimensionalPdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PdfFileLoader;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerActionRegistry;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.save.SaveAsActionHandler;
import org.nightlabs.eclipse.ui.pdfviewer.extension.composite.PdfViewerComposite;
import org.nightlabs.eclipse.ui.pdfviewer.extension.composite.PdfViewerCompositeOption;
import org.nightlabs.eclipse.ui.pdfviewer.extension.resource.Messages;
import org.nightlabs.util.IOUtil;

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
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfViewerEditor extends EditorPart
{
	private static final Logger logger = Logger.getLogger(PdfViewerEditor.class);
	public static final String ID = PdfViewerEditor.class.getName();
	private volatile PdfDocument pdfDocument;
	private PDFFile pdfFile;
	private PdfViewerComposite pdfViewerComposite;
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

	private URL datasourceURL;
	private File datasourceFile;
	private byte[] datasourceByteArray;

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
		loadingMessageLabel.setText(Messages.getString("org.nightlabs.eclipse.ui.pdfviewer.extension.editor.PdfViewerEditor.loadingMessageLabel.text")); //$NON-NLS-1$

		final Display display = loadingMessageLabel.getDisplay();

		final IEditorInput input = getEditorInput();
		Job job = new Job(Messages.getString("org.nightlabs.eclipse.ui.pdfviewer.extension.editor.PdfViewerEditor.monitor.task.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.pdfviewer.extension.editor.PdfViewerEditor.monitor.task.name"), 100); //$NON-NLS-1$
				try {
					if (input instanceof PdfViewerEditorInput) {
						PdfViewerEditorInput i = (PdfViewerEditorInput)input;
						pdfFile = i.getPDFFile();
						if (pdfFile == null)
							pdfFile = i.createPDFFile(new SubProgressMonitor(monitor, 20));
						datasourceURL = i.getUrl();
						datasourceByteArray = i.getByteArray();
						datasourceFile = i.getFile();
					}
					else if (input instanceof FileEditorInput) {
						File file = ((FileEditorInput)input).getFile();
						pdfFile = PdfFileLoader.loadPdf(file, new SubProgressMonitor(monitor, 20));
						datasourceFile = file;
					}
					else if (input instanceof IPathEditorInput) {
						File file = ((IPathEditorInput)input).getPath().toFile();
						pdfFile = PdfFileLoader.loadPdf(file, new SubProgressMonitor(monitor, 20));
						datasourceFile = file;
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
						throw new IllegalArgumentException("Editor input is an unsupported type! class=" + input.getClass() + " instance=" + input); //$NON-NLS-1$ //$NON-NLS-2$

					pdfDocument = new OneDimensionalPdfDocument(pdfFile, new SubProgressMonitor(monitor, 80));
				} catch (final Exception x) {
					logger.error("Error while reading PDF file!", x); //$NON-NLS-1$

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

						pdfViewerComposite = new PdfViewerComposite(
								parent,
								SWT.NONE, pdfDocument,
//								PdfViewerCompositeOption.NO_THUMBNAIL_NAVIGATOR,
//								PdfViewerCompositeOption.NO_SIMPLE_NAVIGATOR,
								PdfViewerCompositeOption.NO_COOL_BAR
						);
						pdfViewerComposite.addDisposeListener(disposeListener);
						createSaveAsActionHandler();

						if (pdfViewerEditorActionBarContributor != null)
							pdfViewerEditorActionBarContributor.contribute();

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

	private DisposeListener disposeListener = new DisposeListener() {
		@Override
		public void widgetDisposed(DisposeEvent event) {
			if (pdfViewerEditorActionBarContributor != null) {
				if (PdfViewerEditor.this == pdfViewerEditorActionBarContributor.getActiveEditor())
					pdfViewerEditorActionBarContributor.setActiveEditor(null);
			}
		}
	};

	private PdfViewerEditorActionBarContributor pdfViewerEditorActionBarContributor;
	protected PdfViewerEditorActionBarContributor getPdfViewerEditorActionBarContributor() {
		return pdfViewerEditorActionBarContributor;
	}
	protected void setPdfViewerEditorActionBarContributor(PdfViewerEditorActionBarContributor pdfViewerEditorActionBarContributor) {
		this.pdfViewerEditorActionBarContributor = pdfViewerEditorActionBarContributor;
	}

	private static File lastSaveDirectory;

	private void createSaveAsActionHandler()
	{
		PdfViewer pdfViewer = getPdfViewer();

		new PdfViewerActionRegistry(pdfViewer, PdfViewerComposite.USE_CASE_DEFAULT, PdfViewerEditorActionBarContributor.class.getName());

		new SaveAsActionHandler(pdfViewer) {
			@Override
			public void saveAs() {
				String suggestedDirectory;
				String suggestedFilePath;
				String suggestedFileName;

				if (datasourceFile != null) {
					suggestedFilePath = datasourceFile.getAbsolutePath();
					suggestedDirectory = datasourceFile.getParent();
					suggestedFileName = datasourceFile.getName();
				}
				else if (datasourceURL != null) {
					if (lastSaveDirectory == null)
						lastSaveDirectory = IOUtil.getUserHome();
					suggestedFileName = new File(datasourceURL.getPath()).getName();
					File f = new File(lastSaveDirectory, suggestedFileName);
					suggestedDirectory = lastSaveDirectory.getAbsolutePath();
					suggestedFilePath = f.getAbsolutePath();
				}
				else {
					suggestedDirectory = "";
					suggestedFileName = "";
					suggestedFilePath = "";
				}

				FileDialog fileDialog = new FileDialog(RCPUtil.getActiveShell(), SWT.SAVE);
				fileDialog.setFileName(suggestedFileName);

				if (suggestedFilePath != null && !"".equals(suggestedFilePath))
					fileDialog.setFilterPath(suggestedDirectory);

				fileDialog.setText(String.format("Save PDF file %s", suggestedFileName, suggestedFilePath));
				String fileName = fileDialog.open();
				if (fileName != null) {
					final File file = new File(fileName);
					if (file.exists()) {
						if (!MessageDialog.openQuestion(RCPUtil.getActiveShell(), String.format("Overwrite?", file.getName(), file.getAbsolutePath()), String.format("The file \"%s\" already exists. Do you want to overwrite it?", file.getName(), file.getAbsolutePath())))
							return;
					}

					lastSaveDirectory = file.getParentFile();

					Job job = new Job(String.format("Saving PDF file %s", file.getName())) {
						@Override
						protected IStatus run(IProgressMonitor monitor) {
							monitor.beginTask(String.format("Saving PDF file %s", file.getName()), 100);
							try {
								monitor.worked(10);

								try {
									if (datasourceByteArray != null) {
										ByteArrayInputStream in = new ByteArrayInputStream(datasourceByteArray);
										FileOutputStream out = new FileOutputStream(file);
										try {
											IOUtil.transferStreamData(
													in, out, 0, datasourceByteArray.length,
													new org.nightlabs.progress.SubProgressMonitor(new ProgressMonitorWrapper(monitor), 90)
											);
										} finally {
											out.close();
											in.close();
										}
									}
									else if (datasourceFile != null) {
										if (!datasourceFile.equals(file)) {
											IOUtil.copyFile(
													datasourceFile, file,
													new org.nightlabs.progress.SubProgressMonitor(new ProgressMonitorWrapper(monitor), 90)
											);
										}
									}
//									else if (datasourceURL != null) {
//										// NO NEED to handle this, because the datasourceByteArray will be loaded in this case.
//									}
									else
										throw new IllegalStateException("No datasource! Cannot save!");

								} catch (IOException x) {
									throw new RuntimeException(x);
								}
							} finally {
								monitor.done();
							}
							return Status.OK_STATUS;
						}
					};
					job.schedule();
				}
			}
		};
	}

	@Override
	public void setFocus() {
		if (pdfViewerComposite != null) {
			pdfViewerComposite.setFocus();
		}
	}

	public PdfViewer getPdfViewer() {
		return pdfViewerComposite == null ? null : pdfViewerComposite.getPdfViewer();
	}
}
