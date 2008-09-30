package org.nightlabs.eclipse.ui.pdfviewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfviewer.internal.PdfSimpleNavigatorComposite;


public class PdfSimpleNavigator implements ContextElement<PdfSimpleNavigator> {

	public static final ContextElementType<PdfSimpleNavigator> CONTEXT_ELEMENT_TYPE = new ContextElementType<PdfSimpleNavigator>(PdfSimpleNavigator.class);
//	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private PdfViewer pdfViewer;
	private PdfDocument pdfDocument;
	private String contextElementId;
	private PdfSimpleNavigatorComposite pdfSimpleNavigatorComposite;

	/**
	 * Create a <code>PdfSimpleNavigator</code>. This constructor delegates to {@link #PdfSimpleNavigator(PdfViewer, String)}
	 * with <code>id = null</code>.
	 * @param pdfViewer the {@link PdfViewer} for which to create a <code>PdfSimpleNavigator</code>.
	 */
	public PdfSimpleNavigator(PdfViewer pdfViewer) {
		this(pdfViewer, null);
	}

	/**
	 * Create a <code>PdfSimpleNavigator</code>.
	 *
	 * @param pdfViewer the {@link PdfViewer} for which to create a <code>PdfSimpleNavigator</code>.
	 * @param contextElementId the identifier, if multiple instances shall be used, or <code>null</code>.
	 */
	public PdfSimpleNavigator(PdfViewer pdfViewer, String contextElementId) {
		assertValidThread();

		if (pdfViewer == null) {
			throw new IllegalArgumentException("pdfViewer must not be null!");
		}

		this.pdfViewer = pdfViewer;
		this.contextElementId = contextElementId;
		pdfViewer.registerContextElement(this);
		this.setPdfDocument(pdfViewer.getPdfDocument()); // pdfViewer.getPdfDocument() can return null!

		pdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_PDF_DOCUMENT, propertyChangeListenerPdfDocument);
		// this navigator will be notified here in the case PDF viewer has changed current page
		pdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_CURRENT_PAGE, propertyChangeListenerCurrentPage);

		// is this the best way?
		// TODO find and consider only context elements that are of type PDF thumb-nail navigator
		// TODO do not use PDF simple navigator as hop for firing between PDF thumb-nail navigator and PDF viewer
/*		Collection<? extends ContextElement<?>> result = pdfViewer.getContextElements();
		for (Iterator<? extends ContextElement<?>> iterator = result.iterator(); iterator.hasNext();) {
			ContextElement<?> registeredContextElement = iterator.next();
			if (registeredContextElement.getPdfViewer() != null) {
				// this navigator will be notified here in the case PDF thumb-nail navigator has changed current page
				registeredContextElement.getPdfViewer().addPropertyChangeListener(
//						registeredContextElement.getPdfViewer().PROPERTY_CURRENT_PAGE,
						PdfViewer.PROPERTY_CURRENT_PAGE,
						propertyChangeListenerCurrentPageModifiedByTN
						);
			}
		}*/
	}

	private PropertyChangeListener propertyChangeListenerCurrentPage = new PropertyChangeListener() {
		@Override
        public void propertyChange(PropertyChangeEvent event) {
			pdfSimpleNavigatorComposite.getCurrentPageNumberText().setText(String.valueOf(event.getNewValue()));
			// check if one or more buttons in PDF simple navigator have to be en-/disabled
			pdfSimpleNavigatorComposite.setControlEnabledStatus((Integer)event.getNewValue());
        }
	};

//	private PropertyChangeListener propertyChangeListenerCurrentPageModifiedByTN = new PropertyChangeListener() {
//		@Override
//        public void propertyChange(PropertyChangeEvent event) {
//			pdfSimpleNavigatorComposite.getCurrentPageNumberText().setText(String.valueOf(event.getNewValue()));
//			// check if one or more buttons in PDF simple navigator have to be en-/disabled
//			pdfSimpleNavigatorComposite.setControlEnabledStatus((Integer)event.getNewValue());
//			getPdfViewer().setCurrentPage((Integer)event.getNewValue(), false);		// only set the new page in PDF viewer, do not fire again
//        }
//	};

	private PropertyChangeListener propertyChangeListenerPdfDocument = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			setPdfDocument((PdfDocument) event.getNewValue());
		}
	};

	@Override
	public void onUnregisterContextElement() {
	    pdfViewer.removePropertyChangeListener(PdfViewer.PROPERTY_PDF_DOCUMENT, propertyChangeListenerPdfDocument);
	    pdfViewer.removePropertyChangeListener(PdfViewer.PROPERTY_CURRENT_PAGE, propertyChangeListenerCurrentPage);
	    pdfViewer = null; // ensure we can't do anything with it anymore - the pdfViewer forgot this instance already - so we forget it, too.
	}

	private static void assertValidThread()
	{
		if (Display.getCurrent() == null) {
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!");
		}
	}

	public Control createControl(Composite parent, int style) {
		assertValidThread();

		if (this.pdfSimpleNavigatorComposite != null) {
			this.pdfSimpleNavigatorComposite.dispose();
		}
		this.pdfSimpleNavigatorComposite = new PdfSimpleNavigatorComposite(parent, style, this);
		pdfSimpleNavigatorComposite.setPdfDocument(pdfDocument);

		this.pdfSimpleNavigatorComposite.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				getPdfViewer().setCurrentPage((Integer)event.getNewValue());
			}
		});

		return this.pdfSimpleNavigatorComposite;
	}

	public PdfDocument getPdfDocument() {
		assertValidThread();
		return pdfDocument;
	}

	protected void setPdfDocument(PdfDocument pdfDocument) {
		assertValidThread();

		this.pdfDocument = pdfDocument;
		if (pdfSimpleNavigatorComposite != null) {
			pdfSimpleNavigatorComposite.setPdfDocument(pdfDocument);
		}
	}

//	public PDFFile getPdfFile() {
//		assertValidThread();
//
//		return pdfFile;
//	}

//	public void setPdfFile(PDFFile pdfFile) {
//		assertValidThread();
//
//		this.pdfFile = pdfFile;
//		if (pdfSimpleNavigatorComposite != null)
//			pdfSimpleNavigatorComposite.setPdfFile(pdfFile);
//	}

	@Override
	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}

	@Override
	public ContextElementType<PdfSimpleNavigator> getContextElementType() {
		return CONTEXT_ELEMENT_TYPE;
	}

	@Override
	public String getContextElementId() {
		return contextElementId;
	}
}
