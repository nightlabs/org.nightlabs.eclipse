package org.nightlabs.eclipse.compatibility;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Transform;

public class CompatibleGC {
	public static GC newGC(Image image) {
		throw new NotAvailableInRAPException();
		//was: new GC(Image)
	}

	public static void setAntialias(GC gc, int on) {
		throw new NotAvailableInRAPException();
		//was: gc.setAntialias(on)
	}

	public static void setInterpolation(GC gc, int high) {
		throw new NotAvailableInRAPException();
		//was: gc.setInterpolation(on)
	}

	public static void copyArea(GC gc, Image image, int i, int j) {
		throw new NotAvailableInRAPException();
		//was: gc.copyArea(image, i, j)
	}

	public static void setLineStyle(GC gc, int lineSolid) {
		throw new NotAvailableInRAPException();
		//was: gc.setLineStyle(image, lineSolid)
	}

	public static void setTransform(GC gc, Transform transform) {
		throw new NotAvailableInRAPException();
		//was: setTransform(image, transform)
	}
}
