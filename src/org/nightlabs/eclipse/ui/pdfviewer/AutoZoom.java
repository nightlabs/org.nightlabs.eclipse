package org.nightlabs.eclipse.ui.pdfviewer;

/**
 * This enumeration will be used for checking if, given a certain composite, auto-zooming shall be used.
 * Automatic zooming to page width will be used in PDF thumbnail navigator, e.g.
 */
public enum AutoZoom {
	pageWidth,
	pageHeight,
	none
}