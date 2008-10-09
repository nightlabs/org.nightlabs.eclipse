package org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElement;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElementType;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.action.PdfViewerActionRegistry;
import org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar.internal.PdfCoolBarComposite;

/**
 * API for creating a cool bar (i.e. a bar holding tools like actions and other contributions).
 * Instantiate an instance of this class and use the {@link #createControl(Composite, int)} method
 * to place a cool bar into a composite.
 * 
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
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

		this.pdfViewer = pdfViewerActionRegistry.getPdfViewer();
		this.pdfViewerActionRegistry = pdfViewerActionRegistry;
		this.contextElementId = contextElementId;
		pdfViewer.registerContextElement(this);
	}

	public Control createControl(Composite parent, int style)
	{
		assertValidThread();

		if (this.pdfCoolBarComposite != null) {
			this.pdfCoolBarComposite.dispose();
			this.pdfCoolBarComposite = null;
		}

		pdfCoolBarComposite = new PdfCoolBarComposite(parent, style, this);

		return pdfCoolBarComposite;
	}

	public PdfViewerActionRegistry getPdfViewerActionRegistry() {
		return pdfViewerActionRegistry;
	}

	@Override
	public void onUnregisterContextElement() {
		// nothing to do
	}

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
