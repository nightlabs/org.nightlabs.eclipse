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
package org.nightlabs.eclipse.ui.pdfviewer.extension.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.action.IXContributionItem;
import org.nightlabs.base.ui.action.registry.AbstractActionRegistry;
import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.eclipse.extension.EPProcessorException;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElement;
import org.nightlabs.eclipse.ui.pdfviewer.ContextElementType;
import org.nightlabs.eclipse.ui.pdfviewer.PDFViewer;

/**
 * This class is the main entry point for the registration of PDF viewer actions.
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class PDFViewerActionRegistry
extends AbstractActionRegistry
implements ContextElement<PDFViewerActionRegistry>
{
	private static final Logger logger = Logger.getLogger(PDFViewerActionRegistry.class);
	public static final ContextElementType<PDFViewerActionRegistry> CONTEXT_ELEMENT_TYPE = new ContextElementType<PDFViewerActionRegistry>(PDFViewerActionRegistry.class);

	private PDFViewer pdfViewer;
	private String contextElementId;
	private UseCase useCase;

	/**
	 * Checks if a given method is called on the SWT UI thread.
	 */
	private static void assertValidThread()
	{
		if (Display.getCurrent() == null) {
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!"); //$NON-NLS-1$
		}
	}

	/**
	 * The constructor of {@link PDFViewerActionRegistry}.
	 * @param pdfViewer the {@link PDFViewer} instance.
	 * @param useCase the chosen {@link UseCase}.
	 * @param contextElementId the id of the chosen {@link ContextElement}.
	 */
	public PDFViewerActionRegistry(final PDFViewer pdfViewer, final UseCase useCase, final String contextElementId) {
		assertValidThread();

		if (pdfViewer == null) {
			throw new IllegalArgumentException("pdfViewer == null"); //$NON-NLS-1$
		}

		if (useCase == null) {
			throw new IllegalArgumentException("useCase == null"); //$NON-NLS-1$
		}

		this.pdfViewer = pdfViewer;
		this.useCase = useCase;
		this.contextElementId = contextElementId;
		pdfViewer.registerContextElement(this);
		pdfViewer.addPropertyChangeListener(PDFViewer.PROPERTY_PDF_DOCUMENT, propertyChangeListenerCalculateEnabled);
		pdfViewer.addPropertyChangeListener(PDFViewer.PROPERTY_REGISTER_CONTEXT_ELEMENT, propertyChangeListenerCalculateEnabled);
		pdfViewer.addPropertyChangeListener(PDFViewer.PROPERTY_UNREGISTER_CONTEXT_ELEMENT, propertyChangeListenerCalculateEnabled);

		long start = 0;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}

		process();

		if (logger.isDebugEnabled()) {
			logger.debug("process() took " + (System.currentTimeMillis() - start) + " msec!"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private PropertyChangeListener propertyChangeListenerCalculateEnabled = new PropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			calculateEnabled();
		}
	};

	@Override
	public void onUnregisterContextElement() {
		pdfViewer.removePropertyChangeListener(PDFViewer.PROPERTY_PDF_DOCUMENT, propertyChangeListenerCalculateEnabled);
		pdfViewer.removePropertyChangeListener(PDFViewer.PROPERTY_REGISTER_CONTEXT_ELEMENT, propertyChangeListenerCalculateEnabled);
		pdfViewer.removePropertyChangeListener(PDFViewer.PROPERTY_UNREGISTER_CONTEXT_ELEMENT, propertyChangeListenerCalculateEnabled);
	}

	@Override
	protected boolean initActionDescriptor(final ActionDescriptor actionDescriptor, final IExtension extension, final IConfigurationElement element)
	throws EPProcessorException
	{
		final String useCaseId = element.getAttribute("useCase"); //$NON-NLS-1$
		if (!this.useCase.getUseCaseId().equals(useCaseId)) {
			return false; // ignore this actionDescriptor since it's not included in our use case.
		}

		return super.initActionDescriptor(actionDescriptor, extension, element);
	}

	@Override
	protected void initAction(final IAction action, final IExtension extension, final IConfigurationElement element) throws EPProcessorException
	{
		if (action instanceof IPDFViewerActionOrContributionItem) {
			((IPDFViewerActionOrContributionItem)action).init(this);
		}
	}

	@Override
	protected void initContributionItem(final IXContributionItem contributionItem, final IExtension extension, final IConfigurationElement element)
	throws EPProcessorException
	{
		if (contributionItem instanceof IPDFViewerActionOrContributionItem) {
			((IPDFViewerActionOrContributionItem)contributionItem).init(this);
		}
	}

	@Override
	protected Object createActionOrContributionItem(final IExtension extension, final IConfigurationElement element) throws EPProcessorException
	{
		try {
			return element.createExecutableExtension("class"); //$NON-NLS-1$
		} catch (final CoreException e) {
			throw new EPProcessorException(e.getMessage(), extension, e);
		}
	}

	@Override
	public String getExtensionPointID() {
		return "org.nightlabs.eclipse.ui.pdfviewer.extension.pdfViewerAction"; //$NON-NLS-1$
	}

	@Override
	public String getContextElementId() {
		return contextElementId;
	}

	@Override
	public ContextElementType<PDFViewerActionRegistry> getContextElementType() {
		return CONTEXT_ELEMENT_TYPE;
	}

	@Override
	public PDFViewer getPDFViewer() {
		return pdfViewer;
	}

	/**
	 * Get the {@link UseCase}.
	 * @return the {@link UseCase}.
 	 */
	public UseCase getUseCase() {
		return useCase;
	}

	/**
	 * Calculates if a property change concerning the property ENABLED has occurred.
	 * This decision depends on whether a {@link PdfDocument} instance is available.
	 */
	public void calculateEnabled() {
		for (final ActionDescriptor actionDescriptor : getActionDescriptors()) {
			final IAction action = actionDescriptor.getAction();
			if (action instanceof IPDFViewerActionOrContributionItem) {
				((IPDFViewerActionOrContributionItem)action).calculateEnabled();
			}

			final IContributionItem contributionItem = actionDescriptor.getContributionItem();
			if (contributionItem instanceof IPDFViewerActionOrContributionItem) {
				((IPDFViewerActionOrContributionItem)contributionItem).calculateEnabled();
			}
		}
	}
}
