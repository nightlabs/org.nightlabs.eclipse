package org.nightlabs.eclipse.ui.pdfviewer.editor;

import java.io.File;

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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.io.FileEditorInput;
import org.nightlabs.eclipse.ui.pdfviewer.composite.PdfViewerComposite;
import org.nightlabs.eclipse.ui.pdfviewer.composite.internal.PdfDocument;
import org.nightlabs.eclipse.ui.pdfviewer.composite.internal.PdfFileLoader;

import com.sun.pdfview.PDFFile;


public class PdfViewerEditor extends EditorPart {

	public static final String ID = PdfViewerEditor.class.getName();
	private volatile PdfDocument pdfDocument;
	private PdfViewerComposite pdfViewerComposite;
	private PdfFileLoader pdfFileLoader;
	private PDFFile pdfFile;
	private File file = null;

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
//		pdfViewerComposite = new PdfViewerCompositeOld(parent, SWT.NONE);
		final IEditorInput input = getEditorInput();

		if (input instanceof FileEditorInput) {
			loadingMessageLabel = new Label(parent, SWT.NONE);
			loadingMessageLabel.setText("Loading PDF file...");

			Job job = new Job("Loading PDF file") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("Loading PDF file", 100);
					try {
						file = ((FileEditorInput)input).getFile();
						pdfFileLoader = new PdfFileLoader(file);
						pdfFile = pdfFileLoader.loadPdf();
						monitor.worked(20);

						pdfDocument = new PdfDocument(pdfFile, new SubProgressMonitor(monitor, 80));
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


//			pdfViewerComposite = new PdfViewerCompositeOld(parent, SWT.H_SCROLL | SWT.V_SCROLL, file);
//			ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL |SWT.BORDER);
//			pdfViewerComposite = new PdfViewerCompositeOld(scrolledComposite, SWT.NONE, file);
//			scrolledComposite.setContent(pdfViewerComposite);
//			scrolledComposite.setExpandHorizontal(true);
//			scrolledComposite.setExpandVertical(true);

//			pdfViewerComposite.loadPdf(((FileEditorInput)input).getFile());
		}


//		Job job = new Job("loading the chosen pdf-file...") {
//			protected IStatus run(IProgressMonitor monitor) {
//				// TODO support other kinds of EditorInput
//				if (input instanceof FileEditorInput) {
//					file = ((FileEditorInput)input).getFile();
//					System.out.println("now creating instance of PdfViewerCompositeOld...");
//					pdfViewerComposite = new PdfViewerCompositeOld(parent, SWT.NONE, file);
////					pdfViewerComposite.loadPdf(((FileEditorInput)input).getFile());
//				}
//				return Status.OK_STATUS;
//			}
//		};
//		job.setPriority(Job.SHORT);
//		job.schedule();

	}

	@Override
	public void setFocus() {
		if (pdfViewerComposite != null)
			pdfViewerComposite.setFocus();
	}

}
