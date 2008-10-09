package org.nightlabs.eclipse.ui.pdfviewer.extension.action.zoom;

import org.nightlabs.l10n.NumberFormatter;

/**
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class ZoomLevel
{
	public static final ZoomLevel ZOOM_TO_PAGE_WIDTH = new ZoomLevel("Page width");
	public static final ZoomLevel ZOOM_TO_PAGE_HEIGHT = new ZoomLevel("Page height");
	public static final ZoomLevel ZOOM_TO_PAGE = new ZoomLevel("Page (complete)");

	public ZoomLevel(int zoomFactorPerMill) {
		this.zoomFactorPerMill = zoomFactorPerMill;
	}

	private String label = null;

	public ZoomLevel(String label) {
		this.label = label;
	}

	private int zoomFactorPerMill = 0;

	public int getZoomFactorPerMill() {
		return zoomFactorPerMill;
	}

	public String getLabel() {
		return label;
	}

	public String getLabel(boolean auto) {
		if (!auto)
			return label;

		if (label == null)
			return NumberFormatter.formatFloat((double) zoomFactorPerMill / 10, 1) + "%";
		else
			return label;
	}
}
