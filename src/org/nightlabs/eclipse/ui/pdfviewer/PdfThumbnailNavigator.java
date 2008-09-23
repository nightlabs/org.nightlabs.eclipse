package org.nightlabs.eclipse.ui.pdfviewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfviewer.internal.PdfThumbnailNavigatorComposite;


public class PdfThumbnailNavigator implements ContextElement<PdfThumbnailNavigator>
{
	public static final ContextElementType<PdfThumbnailNavigator> CONTEXT_ELEMENT_TYPE = new ContextElementType<PdfThumbnailNavigator>(PdfThumbnailNavigator.class);
	private PdfThumbnailNavigatorComposite pdfThumbnailNavigatorComposite;
	private PdfViewer pdfViewer;
	private String contextElementId;


	public PdfThumbnailNavigator(PdfViewer pdfViewer) {
		this(pdfViewer, null);
	}

	public PdfThumbnailNavigator(PdfViewer pdfViewer, String contextElementId) {
		assertValidThread();

		if (pdfViewer == null) {
			throw new IllegalArgumentException("pdfViewer must not be null!");
		}

		this.pdfViewer = pdfViewer;
		this.contextElementId = contextElementId;
		pdfViewer.registerContextElement(this);
		// this navigator will be notified here in the case PDF simple navigator or PDF viewer itself has changed current page
		// the event is not fired again (see below)
		pdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_CURRENT_PAGE, propertyChangeListenerCurrentPage);
	}

	public Control createControl(Composite parent, int style) {
		assertValidThread();

		if (this.pdfThumbnailNavigatorComposite != null) {
			this.pdfThumbnailNavigatorComposite.dispose();
		}
		this.pdfThumbnailNavigatorComposite = new PdfThumbnailNavigatorComposite(parent, style, this);
//		pdfThumbnailNavigatorComposite.setPdfDocument(pdfDocument);

		this.pdfThumbnailNavigatorComposite.getThumbnailPdfViewer().addPropertyChangeListener(PdfViewer.PROPERTY_CURRENT_PAGE, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				PdfThumbnailNavigator.this.pdfViewer.setCurrentPage((Integer) evt.getNewValue(), true);
			}
		});

		return this.pdfThumbnailNavigatorComposite;
	}

	private PropertyChangeListener propertyChangeListenerCurrentPage = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			// TODO draw some kind of shadow to the page with page number event.getNewValue()
			if (pdfThumbnailNavigatorComposite != null && !pdfThumbnailNavigatorComposite.isDisposed())
				pdfThumbnailNavigatorComposite.setCurrentPage((Integer)event.getNewValue(), false);	// do not fire again
		}
	};

	private static void assertValidThread()
	{
		if (Display.getCurrent() == null) {
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!");
		}
	}

	public PdfViewer getThumbnailPdfViewer() {
		if (pdfThumbnailNavigatorComposite != null)
			return pdfThumbnailNavigatorComposite.getThumbnailPdfViewer();
		else
			return null;
	}

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
	    pdfViewer.removePropertyChangeListener(PdfViewer.PROPERTY_CURRENT_PAGE, propertyChangeListenerCurrentPage);
	    pdfViewer = null; // ensure we can't do anything with it anymore - the pdfViewer forgot this instance already - so we forget it, too.
	}




}
