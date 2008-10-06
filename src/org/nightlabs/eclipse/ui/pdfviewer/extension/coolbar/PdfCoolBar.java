package org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElement;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElementType;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerActionRegistry;
import org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar.internal.PdfCoolBarComposite;

public class PdfCoolBar implements ContextElement<PdfCoolBar>
{
	public static final ContextElementType<PdfCoolBar> CONTEXT_ELEMENT_TYPE = new ContextElementType<PdfCoolBar>(PdfCoolBar.class);
	private PdfViewer pdfViewer;
	private String contextElementId;
	private PdfCoolBarComposite pdfCoolBarComposite;
	private PdfViewerActionRegistry pdfViewerActionRegistry;

	private static void assertValidThread()
	{
		if (Display.getCurrent() == null) {
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!"); //$NON-NLS-1$
		}
	}

	public PdfCoolBar(PdfViewerActionRegistry pdfViewerActionRegistry) {
		this(pdfViewerActionRegistry, null);
	}

	public PdfCoolBar(PdfViewerActionRegistry pdfViewerActionRegistry, String contextElementId) {
		assertValidThread();
		if (pdfViewerActionRegistry == null)
			throw new IllegalArgumentException("pdfViewerActionRegistry must not be null!");

		this.pdfViewerActionRegistry = pdfViewerActionRegistry;
		this.pdfViewer = pdfViewerActionRegistry.getPdfViewer();
		pdfViewer.addPropertyChangeListener(PdfViewer.PROPERTY_PDF_DOCUMENT, propertyChangeListenerPdfDocument);
	}

	public Control createControl(Composite parent, int style)
	{
		assertValidThread();

		if (this.pdfCoolBarComposite != null) {
			this.pdfCoolBarComposite.dispose();
			this.pdfCoolBarComposite = null;
		}

		pdfCoolBarComposite = new PdfCoolBarComposite(parent, style, this);
//		pdfCoolBarComposite.setPdfDocument(pdfDocument);

		return pdfCoolBarComposite;
	}

	public PdfViewerActionRegistry getPdfViewerActionRegistry() {
		return pdfViewerActionRegistry;
	}

	@Override
	public void onUnregisterContextElement() {
		pdfViewer.removePropertyChangeListener(PdfViewer.PROPERTY_PDF_DOCUMENT, propertyChangeListenerPdfDocument);
//		pdfViewer.removePropertyChangeListener(PdfViewer.PROPERTY_REGISTER_CONTEXT_ELEMENT, propertyChangeListenerRegisterContextElement);
	}

//	private PropertyChangeListener propertyChangeListenerRegisterContextElement = new PropertyChangeListener() {
//		@Override
//		public void propertyChange(PropertyChangeEvent evt) {
//			ContextElement<?> contextElement = (ContextElement<?>) evt.getNewValue();
//			if (PdfViewerActionRegistry.CONTEXT_ELEMENT_TYPE.equals(contextElement.getContextElementType()))
//				pdfViewerActionRegistry = (PdfViewerActionRegistry) contextElement;
//		}
//	};

	private PropertyChangeListener propertyChangeListenerPdfDocument = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			pdfCoolBarComposite.refresh();
		}
	};

	@Override
	public String getContextElementId() {
		return contextElementId;
	}

	@Override
	public ContextElementType<PdfCoolBar> getContextElementType() {
		return CONTEXT_ELEMENT_TYPE;
	}

	@Override
	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}

}
