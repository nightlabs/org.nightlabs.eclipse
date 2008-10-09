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
import java.util.EventObject;

/**
 * Event propagated to {@link PaintListener}s. It provides access to a {@link Graphics2D}
 * allowing listeners to draw.
 * 
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PaintEvent extends EventObject
{
    private static final long serialVersionUID = 1L;

	private Graphics2D graphics2D;

	public PaintEvent(PdfViewer source, Graphics2D graphics2D) {
		super(source);
		this.graphics2D = graphics2D;
	}

	/**
	 * Get access to the drawing area.
	 *
	 * @return the AWT API object to the drawing area.
	 */
	public Graphics2D getGraphics2D() {
	    return graphics2D;
    }

	@Override
	public PdfViewer getSource() {
	    return (PdfViewer) super.getSource();
	}
}
