package org.nightlabs.eclipse.ui.pdfviewer.extension.composite;

import org.nightlabs.eclipse.ui.pdfviewer.PdfSimpleNavigator;
import org.nightlabs.eclipse.ui.pdfviewer.PdfThumbnailNavigator;
import org.nightlabs.eclipse.ui.pdfviewer.PdfViewer;
import org.nightlabs.eclipse.ui.pdfviewer.extension.coolbar.PdfCoolBar;

/**
 * Options that can be passed to the {@link PdfViewerComposite}'s constructor.
 *
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public enum PdfViewerCompositeOption {
	/**
	 * Do not create a cool bar. Without this option, a {@link PdfCoolBar} will be created above the
	 * {@link PdfViewer} (and above the thumbnail navigator, if it is created).
	 */
	NO_COOL_BAR,

	/**
	 * Do not create a thumbnail navigator. Without this option, a {@link PdfThumbnailNavigator} will
	 * be created on the left side of the {@link PdfViewer}.
	 */
	NO_THUMBNAIL_NAVIGATOR,
	
	/**
	 * Do not create a simple navigator. Without this option, a {@link PdfSimpleNavigator} will be created
	 * below the {@link PdfThumbnailNavigator} (or below the {@link PdfViewer}, if no thumbnail navigator is shown).
	 */
	NO_SIMPLE_NAVIGATOR
}
