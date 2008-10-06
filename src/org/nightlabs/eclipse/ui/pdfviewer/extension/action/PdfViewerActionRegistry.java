package org.nightlabs.eclipse.ui.pdfviewer.extension.action;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.action.IXContributionItem;
import org.nightlabs.base.ui.action.registry.AbstractActionRegistry;
import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElement;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElementType;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;

public class PdfViewerActionRegistry
extends AbstractActionRegistry
implements ContextElement<PdfViewerActionRegistry>
{
	private static final Logger logger = Logger.getLogger(PdfViewerActionRegistry.class);
	public static final ContextElementType<PdfViewerActionRegistry> CONTEXT_ELEMENT_TYPE = new ContextElementType<PdfViewerActionRegistry>(PdfViewerActionRegistry.class);

	private PdfViewer pdfViewer;
	private String contextElementId;
	private UseCase useCase;

	private static void assertValidThread()
	{
		if (Display.getCurrent() == null) {
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!"); //$NON-NLS-1$
		}
	}

	public PdfViewerActionRegistry(PdfViewer pdfViewer, UseCase useCase) {
		this(pdfViewer, useCase, null);
	}

	public PdfViewerActionRegistry(PdfViewer pdfViewer, UseCase useCase, String contextElementId) {
		assertValidThread();

		if (pdfViewer == null)
			throw new IllegalArgumentException("pdfViewer == null");

		if (useCase == null)
			throw new IllegalArgumentException("useCase == null");

		this.pdfViewer = pdfViewer;
		this.useCase = useCase;
		this.contextElementId = contextElementId;

		long start = 0;
		if (logger.isDebugEnabled())
			start = System.currentTimeMillis();

		process();

		if (logger.isDebugEnabled())
			logger.debug("process() took " + (System.currentTimeMillis() - start) + " msec!");
	}

	@Override
	public void onUnregisterContextElement() {
		// nothing to clean up
	}

	@Override
	protected boolean initActionDescriptor(ActionDescriptor actionDescriptor, IExtension extension, IConfigurationElement element)
	throws EPProcessorException
	{
		String useCaseId = element.getAttribute("useCase");
		if (!this.useCase.getUseCaseId().equals(useCaseId))
			return false; // ignore this actionDescriptor since it's not included in our use case.

		return super.initActionDescriptor(actionDescriptor, extension, element);
	}

	@Override
	protected void initAction(IAction action, IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
		if (action instanceof IPdfViewerActionOrContributionItem)
			((IPdfViewerActionOrContributionItem)action).init(this);
	}

	@Override
	protected void initContributionItem(IXContributionItem contributionItem, IExtension extension, IConfigurationElement element)
	throws EPProcessorException
	{
		if (contributionItem instanceof IPdfViewerActionOrContributionItem)
			((IPdfViewerActionOrContributionItem)contributionItem).init(this);
	}

	@Override
	protected Object createActionOrContributionItem(IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
		try {
			return element.createExecutableExtension("class"); //$NON-NLS-1$
		} catch (CoreException e) {
			throw new EPProcessorException(e.getMessage(), extension, e);
		}
	}

	@Override
	public String getExtensionPointID() {
		return "org.nightlabs.eclipse.ui.pdfviewer.extension.pdfViewerAction";
	}

	@Override
	public String getContextElementId() {
		return contextElementId;
	}

	@Override
	public ContextElementType<PdfViewerActionRegistry> getContextElementType() {
		return CONTEXT_ELEMENT_TYPE;
	}

	@Override
	public PdfViewer getPdfViewer() {
		return pdfViewer;
	}

	public UseCase getUseCase() {
		return useCase;
	}
}
