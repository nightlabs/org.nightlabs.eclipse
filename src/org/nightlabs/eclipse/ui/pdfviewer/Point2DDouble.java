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
