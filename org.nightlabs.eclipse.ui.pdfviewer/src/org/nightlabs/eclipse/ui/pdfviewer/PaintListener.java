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
package org.nightlabs.eclipse.ui.pdfviewer;

import java.awt.Graphics2D;

/**
 * Implementations of this interface can draw into the PDF viewer's drawing area or into the buffer,
 * depending on which way they were registered. See these methods:
 * <ul>
 * <li>{@link PDFViewer#addPaintToBufferListener(PaintListener)}</li>
 * <li>{@link PDFViewer#removePaintToBufferListener(PaintListener)}</li>
 * <li>{@link PDFViewer#addPaintToViewListener(PaintListener)}</li>
 * <li>{@link PDFViewer#removePaintToViewListener(PaintListener)}</li>
 * </ul>
 *
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public interface PaintListener {
	/**
	 * Paint before the {@link PDFViewer} paints.
	 *
	 * @param event provides information about the paint situation - especially access to a {@link Graphics2D}.
	 */
	void prePaint(PaintEvent event);
	/**
	 * Paint after the {@link PDFViewer} painted.
	 *
	 * @param event provides information about the paint situation - especially access to a {@link Graphics2D}.
	 */
	void postPaint(PaintEvent event);
}
