package org.nightlabs.eclipse.ui.pdfviewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfviewer.internal.PdfThumbnailNavigatorComposite;
import org.nightlabs.eclipse.ui.pdfviewer.resource.Messages;


public class PdfThumbnailNavigator implements ContextElement<PdfThumbnailNavigator>
{
	public static final ContextElementType<PdfThumbnailNavigator> CONTEXT_ELEMENT_TYPE = new ContextElementType<PdfThumbnailNavigator>(PdfThumbnailNavigator.class);
	private PdfThumbnailNavigatorComposite pdfThumbnailNavigatorComposite;
	private PdfViewer pdfViewer;
	private PdfDocument pdfDocument;
	private String contextElementId;


	public PdfThumbnailNavigator(PdfViewer pdfViewer) {
		this(pdfViewer, null);
	}

	public PdfThumbnailNavigator(PdfViewer pdfViewer, String contextElementId) {
		assertValidThread();

		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer must not be null!"); //$NON-NLS-1$

		this.pdfViewer = pdfViewer;
		this.contextElementId = contextElementId;
		pdfViewer.registerContextElement(this);

		// PDF thumbnail navigator will be notified in the case PDF simple navigator or PDF viewer itself has changed current page.
		// The event is not fired again (see below).
		pdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_CURRENT_PAGE, propertyChangeListenerCurrentPage);
		// PDF thumbnail navigator will be notified in the case PDF viewer has loaded a PDF document.
		pdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_PDF_DOCUMENT, propertyChangeListenerPdfDocument);
		pdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_COMPONENT_RESIZED, propertyChangeListenerComponentResized);
		setPdfDocument(pdfViewer.getPdfDocument());
	}

	public Control createControl(Composite parent, int style) {
		assertValidThread();

		if (this.pdfThumbnailNavigatorComposite != null)
			this.pdfThumbnailNavigatorComposite.dispose();

		this.pdfThumbnailNavigatorComposite = new PdfThumbnailNavigatorComposite(parent, style, this);


		this.pdfThumbnailNavigatorComposite.getThumbnailPdfViewer().addPropertyChangeListener(PdfViewer.PROPERTY_CURRENT_PAGE, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				PdfThumbnailNavigator.this.pdfViewer.setCurrentPage((Integer) evt.getNewValue());
			}
		});
		this.pdfThumbnailNavigatorComposite.getThumbnailPdfViewer().addPropertyChangeListener(PdfViewer.PROPERTY_COMPONENT_RESIZED, propertyChangeListenerComponentResized);

		return this.pdfThumbnailNavigatorComposite;
	}

	private PropertyChangeListener propertyChangeListenerComponentResized = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			pdfThumbnailNavigatorComposite.zoomToPageWidth();
		}
	};

	private PropertyChangeListener propertyChangeListenerCurrentPage = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			int newCurrentPage = (Integer)event.getNewValue();
			if (pdfThumbnailNavigatorComposite != null && !pdfThumbnailNavigatorComposite.isDisposed())
				pdfThumbnailNavigatorComposite.setCurrentPage(newCurrentPage);
		}
	};

	private PropertyChangeListener propertyChangeListenerPdfDocument = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			setPdfDocument((PdfDocument)event.getNewValue());
		}
	};

	private static void assertValidThread()	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!"); //$NON-NLS-1$
	}

//	public PdfViewer getThumbnailPdfViewer() {
//		if (pdfThumbnailNavigatorComposite != null)
//			return pdfThumbnailNavigatorComposite.getThumbnailPdfViewer();
//		else
//			return null;
//	}

	@Override
	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}

	@Override
	public ContextElementType<PdfThumbnailNavigator> getContextElementType() {
		return CONTEXT_ELEMENT_TYPE;
	}
	@Override
	public String getContextElementId() {
		return contextElementId;
	}

	@Override
	public void onUnregisterContextElement() {
		pdfViewer.removePropertyChangeListener(PdfViewer.PROPERTY_PDF_DOCUMENT, propertyChangeListenerPdfDocument);
	    pdfViewer.removePropertyChangeListener(PdfViewer.PROPERTY_CURRENT_PAGE, propertyChangeListenerCurrentPage);
	    pdfViewer.removePropertyChangeListener(PdfViewer.PROPERTY_COMPONENT_RESIZED, propertyChangeListenerComponentResized);
	    pdfViewer = null; // ensure we can't do anything with it anymore - the pdfViewer forgot this instance already - so we forget it, too.
	}

	public PdfThumbnailNavigatorComposite getPdfThumbnailNavigatorComposite() {
    	return pdfThumbnailNavigatorComposite;
    }

	public void setPdfThumbnailNavigatorComposite(PdfThumbnailNavigatorComposite pdfThumbnailNavigatorComposite) {
    	this.pdfThumbnailNavigatorComposite = pdfThumbnailNavigatorComposite;
    }

	protected void setPdfDocument(PdfDocument pdfDocument) {
	    this.pdfDocument = pdfDocument;
	    if (pdfThumbnailNavigatorComposite != null && !pdfThumbnailNavigatorComposite.isDisposed())
	    	pdfThumbnailNavigatorComposite.setPdfDocument(pdfDocument);
    }

	public PdfDocument getPdfDocument() {
	    return pdfDocument;
    }
}
