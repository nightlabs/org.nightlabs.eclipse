package org.nightlabs.eclipse.ui.pdfviewer;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import org.nightlabs.eclipse.ui.pdfviewer.resource.Messages;

/**
 * An implementation of {@link Dimension2D} using <code>double</code> values.
 * <p>
 * Since there is unfortunately no subclass-implementation like
 * {@link Point2D.Double}, but only {@link Dimension} which uses <code>int</code>
 * values, this implementation was required.
 * </p>
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public class Dimension2DDouble extends Dimension2D
{
	private volatile boolean readOnly;
	private double width;
	private double height;

	public Dimension2DDouble() { }

	public Dimension2DDouble(double width, double height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public double getWidth() {
		return width;
	}

	private void assertNotReadOnly()
	{
		if (readOnly)
			throw new UnsupportedOperationException("This instance of Dimension2DDouble is read-only!"); //$NON-NLS-1$
	}

	public void setWidth(double width)
	{
		assertNotReadOnly();

		this.width = width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		assertNotReadOnly();

		this.height = height;
	}

	@Override
	public void setSize(double width, double height)
	{
		assertNotReadOnly();

		this.width = width;
		this.height = height;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (obj.getClass() != this.getClass() && !(obj instanceof Dimension2D)) return false;
		Dimension2D o = (Dimension2D) obj;
		return this.width == o.getWidth() && this.height == o.getHeight();
	}

	@Override
	public int hashCode() {
		long wbits = Double.doubleToLongBits(width);
		long hbits = Double.doubleToLongBits(height);
		return (int)(wbits ^ (wbits >>> 32)) * 31 ^ (int)(hbits ^ (hbits >>> 32));
	}

	@Override
	public String toString() {
	    return getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + '[' + width + ',' + height + ']';
	}

	public void setReadOnly() {
	    this.readOnly = true;
    }
	public boolean isReadOnly() {
	    return readOnly;
    }
}
