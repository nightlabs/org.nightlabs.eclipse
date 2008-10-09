package org.nightlabs.eclipse.ui.pdfviewer;

/**
 * This enumeration will be used for checking if, given a certain composite, auto-zooming shall be used.
 * Automatic zooming to page width will be used in PDF thumbnail navigator, e.g.. Additionally, the
 * user might have UI (like a zoom combo box) that allows setting an auto-zoom for the main viewing area.
 *
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public enum AutoZoom {
	pageWidth,
	pageHeight,
	page,
	none
}