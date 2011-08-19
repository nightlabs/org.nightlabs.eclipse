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
package org.nightlabs.eclipse.ui.pdfviewer.extension.action.zoom;

import org.nightlabs.l10n.NumberFormatter;

/**
 * Declaration of different zoom level constants that can be chosen for zooming.
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class ZoomLevel
{
	public static final ZoomLevel ZOOM_TO_PAGE_WIDTH = new ZoomLevel("Page width"); //$NON-NLS-1$
	public static final ZoomLevel ZOOM_TO_PAGE_HEIGHT = new ZoomLevel("Page height"); //$NON-NLS-1$
	public static final ZoomLevel ZOOM_TO_PAGE = new ZoomLevel("Page (complete)"); //$NON-NLS-1$
	private int zoomFactorPerMill = 0;
	private String label = null;


	/**
	 * Constructor of {@link ZoomLevel}.
	 * Sets the zoom factor per mill of the {@link ZoomLevel} given as argument.
	 * @param zoomFactorPerMill the zoom factor per mill to set.
	 */
	public ZoomLevel(final int zoomFactorPerMill) {
		this.zoomFactorPerMill = zoomFactorPerMill;
	}

	/**
	 * Constructor of {@link ZoomLevel}.
	 * Sets the label of the {@link ZoomLevel} given as argument.
	 * @param label the label to set.
	 */
	public ZoomLevel(final String label) {
		this.label = label;
	}

	/**
	 * Gets the zoom factor per mill of a chosen {@link ZoomLevel} instance.
	 * @return the zoom factor per mill.
	 */
	public int getZoomFactorPerMill() {
		return zoomFactorPerMill;
	}

	/**
	 * Gets the label of a chosen {@link ZoomLevel} instance.
	 * @return the label.
	 */
	public String getLabel() {
		return label;
	}

	public String getLabel(final boolean auto) {
		if (!auto) {
			return label;
		}

		if (label == null) {
			return NumberFormatter.formatFloat((double) zoomFactorPerMill / 10, 1) + "%"; //$NON-NLS-1$
		} else {
			return label;
		}
	}
}
