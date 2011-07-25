package org.nightlabs.eclipse.compatibility;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Transform;

public class CompatibleGC {
	public static GC newGC(Image image) {
		return new GC(image);
	}

	public static void setAntialias(GC gc, int on) {
		gc.setAntialias(on);
	}

	public static void setInterpolation(GC gc, int high) {
		gc.setInterpolation(high);
	}

	public static void copyArea(GC gc, Image image, int i, int j) {
		gc.copyArea(image, i, j);
	}

	public static void setLineStyle(GC gc, int lineSolid) {
		gc.setLineStyle(lineSolid);
	}

	public static void setTransform(GC gc, Transform transform) {
		gc.setTransform(transform);
	}
}
