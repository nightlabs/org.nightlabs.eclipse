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
package org.nightlabs.eclipse.ui.pdfviewer.extension.composite;

/**
 * Options that can be passed to the {@link PDFViewerComposite}'s constructor.
 *
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public enum PDFViewerCompositeOption {
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
