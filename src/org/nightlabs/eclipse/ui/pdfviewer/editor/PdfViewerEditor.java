package org.nightlabs.eclipse.ui.pdfviewer.editor;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.io.FileEditorInput;
import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;
import org.nightlabs.eclipse.ui.pdfviewer.composite.internal.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.composite.internal.PdfFileLoader;

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
	private PdfViewerComposite pdfViewerComposite;
	private PDFFile pdfFile;

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO implement later
	}

	@Override
	public void doSaveAs() {
		// TODO implement later
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
		loadingMessageLabel = new Label(parent, SWT.NONE);
		loadingMessageLabel.setText("Loading PDF file...");

		final IEditorInput input = getEditorInput();
		Job job = new Job("Loading PDF file") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Loading PDF file", 100);
				try {
					if (input instanceof PdfViewerEditorInput) {
						pdfFile = ((PdfViewerEditorInput)input).getPDFFile();
					}
					else if (input instanceof FileEditorInput) {
						File file = ((FileEditorInput)input).getFile();
						pdfFile = PdfFileLoader.loadPdf(file);
					}
					else if (input instanceof IPathEditorInput) {
						File file = ((IPathEditorInput)input).getPath().toFile();
						pdfFile = PdfFileLoader.loadPdf(file);
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

					monitor.worked(20);

					pdfDocument = new PdfDocument(pdfFile, new SubProgressMonitor(monitor, 80));
				} catch (IOException x) {
					logger.error("Error while reading PDF file!", x);
					throw new RuntimeException(x);
				} finally {
					monitor.done();
				}

				loadingMessageLabel.getDisplay().asyncExec(new Runnable() {
					public void run() {
						loadingMessageLabel.dispose();
						pdfViewerComposite = new PdfViewerComposite(parent, pdfDocument);
						parent.layout(true, true);
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
		if (pdfViewerComposite != null)
			pdfViewerComposite.setFocus();
	}

}
