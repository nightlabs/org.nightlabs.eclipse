package org.nightlabs.eclipse.ui.pdfviewer;

import java.awt.geom.Point2D;

public class MouseEvent {
	private java.awt.Point pointRelativeToComponent;
	private Point2D pointInRealCoordinate;
	private int zoomFactorPerMill;

	public MouseEvent(java.awt.Point pointRelativeToPanel, Point2D pointInRealCoordinate, int zoomFactorPerMill) {
		this.pointRelativeToComponent = pointRelativeToPanel;
		this.pointInRealCoordinate = pointInRealCoordinate;
		this.zoomFactorPerMill = zoomFactorPerMill;
	}

	public java.awt.Point getPointRelativeToComponent() {
		return pointRelativeToComponent;
	}

	public Point2D getPointInRealCoordinate() {
		return pointInRealCoordinate;
	}

	public int getZoomFactorPerMill() {
    	return zoomFactorPerMill;
    }
}
