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
package org.nightlabs.eclipse.ui.pdfviewer.extension.editor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.nightlabs.eclipse.ui.pdfrenderer.PDFFileLoader;
import org.nightlabs.eclipse.ui.pdfviewer.OneDimensionalPDFDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PDFDocument;
import org.nightlabs.eclipse.ui.pdfviewer.PDFProgressMontitorWrapper;
import org.nightlabs.eclipse.ui.pdfviewer.PDFViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PDFViewerActionRegistry;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.save.SaveAsActionHandler;
import org.nightlabs.eclipse.ui.pdfviewer.extension.composite.PDFViewerComposite;
import org.nightlabs.eclipse.ui.pdfviewer.extension.composite.PDFViewerCompositeOption;
import org.nightlabs.eclipse.ui.pdfviewer.extension.resource.Messages;
import org.nightlabs.util.IOUtil;

import com.sun.pdfview.PDFFile;

/**
 * This editor displays a PDF file. It can be opened with the following implementations of
 * {@link IEditorInput}:
 * <ul>
 *	<li>{@link PDFViewerEditorInput}</li>: Supports {@link File} and byte array as data source.
 *	<li>{@link FileEditorInput}</li>: Supports {@link File} as data source.
 *	<li>{@link IPathEditorInput}</li>
 * </ul>
 *
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PDFViewerEditor extends EditorPart
{
	private static final Logger logger = Logger.getLogger(PDFViewerEditor.class);
	public static final String ID = PDFViewerEditor.class.getName();
	private volatile PDFDocument pdfDocument;
	private PDFFile pdfFile;
	private PDFViewerComposite pdfViewerComposite;
//	private SashForm page;

	@Override
	public void doSave(final IProgressMonitor monitor) {
		// TODO implement later
	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException
	{
		setSite(site);
		setInput(input);

		// strange that the following lines are necessary - the tool tip pops up automatically - why does the part name not?
		if (input.getName() != null) {
			setPartName(input.getName());
		}
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
		final GridData gdLoadingMessageLabel = new GridData(GridData.FILL_HORIZONTAL);
		gdLoadingMessageLabel.verticalIndent = 16;
		gdLoadingMessageLabel.horizontalIndent = 16;
		loadingMessageLabel.setLayoutData(gdLoadingMessageLabel);
		loadingMessageLabel.setText(Messages.getString("org.nightlabs.eclipse.ui.pdfviewer.extension.editor.PdfViewerEditor.loadingMessageLabel.text")); //$NON-NLS-1$

		final Display display = loadingMessageLabel.getDisplay();

		final IEditorInput input = getEditorInput();
		final Job job = new Job(Messages.getString("org.nightlabs.eclipse.ui.pdfviewer.extension.editor.PdfViewerEditor.monitor.task.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.pdfviewer.extension.editor.PdfViewerEditor.monitor.task.name"), 100); //$NON-NLS-1$
				try {
					if (input instanceof PDFViewerEditorInput) {
						final PDFViewerEditorInput i = (PDFViewerEditorInput)input;
						pdfFile = i.createPDFFile(new SubProgressMonitor(monitor, 20));
						datasourceURL = i.getUrl();
						datasourceByteArray = i.getByteArray();
						datasourceFile = i.getFile();
					}
					else if (input instanceof FileEditorInput) {
						final File file = ((FileEditorInput)input).getFile();
						pdfFile = PDFFileLoader.loadPDF(file, new PDFProgressMontitorWrapper(new SubProgressMonitor(monitor, 20)));
						datasourceFile = file;
					}
					else if (input instanceof IPathEditorInput) {
						final File file = ((IPathEditorInput)input).getPath().toFile();
						pdfFile = PDFFileLoader.loadPDF(file, new PDFProgressMontitorWrapper(new SubProgressMonitor(monitor, 20)));
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
					else {
						throw new IllegalArgumentException("Editor input is an unsupported type! class=" + input.getClass() + " instance=" + input); //$NON-NLS-1$ //$NON-NLS-2$
					}

					pdfDocument = new OneDimensionalPDFDocument(pdfFile, new SubProgressMonitor(monitor, 80));
				} catch (final Exception x) {
					logger.error("Error while reading PDF file!", x); //$NON-NLS-1$

					display.asyncExec(new Runnable() {
						public void run() {
							if (parent.isDisposed()) {
								return;
							}

							RCPUtil.closeEditor(input, false);
						}
					});

					throw new RuntimeException(x);
				} finally {
					monitor.done();
				}

				display.asyncExec(new Runnable() {

					public void run() {
						if (parent.isDisposed()) {
							return;
						}

						loadingMessagePage.dispose();

						pdfViewerComposite = new PDFViewerComposite(
								parent,
								SWT.NONE, pdfDocument,
//								PdfViewerCompositeOption.NO_THUMBNAIL_NAVIGATOR,
//								PdfViewerCompositeOption.NO_SIMPLE_NAVIGATOR,
								PDFViewerCompositeOption.NO_COOL_BAR
						);
						pdfViewerComposite.addDisposeListener(disposeListener);
						createSaveAsActionHandler();

						if (pdfViewerEditorActionBarContributor != null) {
							pdfViewerEditorActionBarContributor.contribute();
						}

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
		public void widgetDisposed(final DisposeEvent event) {
			if (pdfViewerEditorActionBarContributor != null) {
				if (PDFViewerEditor.this == pdfViewerEditorActionBarContributor.getActiveEditor()) {
					pdfViewerEditorActionBarContributor.setActiveEditor(null);
				}
			}
		}
	};

	private PDFViewerEditorActionBarContributor pdfViewerEditorActionBarContributor;
	protected PDFViewerEditorActionBarContributor getPdfViewerEditorActionBarContributor() {
		return pdfViewerEditorActionBarContributor;
	}
	protected void setPdfViewerEditorActionBarContributor(final PDFViewerEditorActionBarContributor pdfViewerEditorActionBarContributor) {
		this.pdfViewerEditorActionBarContributor = pdfViewerEditorActionBarContributor;
	}

	private static File lastSaveDirectory;

	private void createSaveAsActionHandler()
	{
		final PDFViewer pdfViewer = getPDFViewer();

		new PDFViewerActionRegistry(pdfViewer, PDFViewerComposite.USE_CASE_DEFAULT, PDFViewerEditorActionBarContributor.class.getName());

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
					if (lastSaveDirectory == null) {
						lastSaveDirectory = IOUtil.getUserHome();
					}
					suggestedFileName = new File(datasourceURL.getPath()).getName();
					final File f = new File(lastSaveDirectory, suggestedFileName);
					suggestedDirectory = lastSaveDirectory.getAbsolutePath();
					suggestedFilePath = f.getAbsolutePath();
				}
				else {
					suggestedDirectory = ""; //$NON-NLS-1$
					suggestedFileName = ""; //$NON-NLS-1$
					suggestedFilePath = ""; //$NON-NLS-1$
				}

				final FileDialog fileDialog = new FileDialog(RCPUtil.getActiveShell(), SWT.SAVE);
				fileDialog.setFileName(suggestedFileName);

				if (suggestedFilePath != null && !"".equals(suggestedFilePath)) { //$NON-NLS-1$
					fileDialog.setFilterPath(suggestedDirectory);
				}

				fileDialog.setText(String.format("Save PDF file %s", suggestedFileName, suggestedFilePath)); //$NON-NLS-1$
				final String fileName = fileDialog.open();
				if (fileName != null) {
					final File file = new File(fileName);
					if (file.exists()) {
						if (!MessageDialog.openQuestion(RCPUtil.getActiveShell(), String.format("Overwrite?", file.getName(), file.getAbsolutePath()), String.format("The file \"%s\" already exists. Do you want to overwrite it?", file.getName(), file.getAbsolutePath()))) { //$NON-NLS-1$ //$NON-NLS-2$
							return;
						}
					}

					lastSaveDirectory = file.getParentFile();

					final Job job = new Job(String.format("Saving PDF file %s", file.getName())) { //$NON-NLS-1$
						@Override
						protected IStatus run(final IProgressMonitor monitor) {
							monitor.beginTask(String.format("Saving PDF file %s", file.getName()), 100); //$NON-NLS-1$
							try {
								monitor.worked(10);

								try {
									if (datasourceByteArray != null) {
										final ByteArrayInputStream in = new ByteArrayInputStream(datasourceByteArray);
										final FileOutputStream out = new FileOutputStream(file);
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
									else if (datasourceURL != null) {
										final InputStream in = datasourceURL.openStream();
										try {
											final FileOutputStream out = new FileOutputStream(file);
											try {
												IOUtil.transferStreamData(
														in, out
												);
											} finally {
												out.close();
											}
										} finally {
											in.close();
										}
										monitor.worked(90);
									} else {
										throw new IllegalStateException("No datasource! Cannot save!"); //$NON-NLS-1$
									}

								} catch (final IOException x) {
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

	public PDFViewer getPDFViewer() {
		return pdfViewerComposite == null ? null : pdfViewerComposite.getPdfViewer();
	}
}
