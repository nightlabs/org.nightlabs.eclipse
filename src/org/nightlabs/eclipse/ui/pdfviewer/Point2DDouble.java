package org.nightlabs.eclipse.ui.pdfviewer;

import java.awt.geom.Point2D;

/**
 * A {@link Point2D} implementation using <code>double</code> values and supporting read-only mode.
 *
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 * @author frederik loeser - frederik at nightlabs dot de
 */
public class Point2DDouble extends Point2D
{
	private volatile boolean readOnly;

	private double x;
	private double y;

	public Point2DDouble() { }

	public Point2DDouble(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Point2DDouble(Point2D other)
	{
		this.x = other.getX();
		this.y = other.getY();
	}

	private void assertNotReadOnly()
	{
		if (readOnly)
			throw new UnsupportedOperationException("This instance of Dimension2DDouble is read-only!"); //$NON-NLS-1$
	}

	@Override
	public double getX() {
		return x;
	}

	public void setX(double x) {
		assertNotReadOnly();
	    this.x = x;
	}

	@Override
	public double getY() {
		return y;
	}

	public void setY(double y) {
		assertNotReadOnly();
	    this.y = y;
    }

	@Override
	public void setLocation(double x, double y)
	{
		assertNotReadOnly();

		this.x = x;
		this.y = y;
	}

	public void setReadOnly() {
	    this.readOnly = true;
    }
	public boolean isReadOnly() {
	    return readOnly;
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = super.hashCode();
	    long temp;
	    temp = java.lang.Double.doubleToLongBits(x);
	    result = prime * result + (int) (temp ^ (temp >>> 32));
	    temp = java.lang.Double.doubleToLongBits(y);
	    result = prime * result + (int) (temp ^ (temp >>> 32));
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (!super.equals(obj)) return false;
	    if (obj.getClass() != this.getClass() && !(obj instanceof Point2D)) return false;
	    final Point2D other = (Point2D) obj;
	    return this.x == other.getX() && this.y == other.getY();
    }

	@Override
	public String toString() {
	    return getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + '[' + x + ',' + y + ']';
	}
}
