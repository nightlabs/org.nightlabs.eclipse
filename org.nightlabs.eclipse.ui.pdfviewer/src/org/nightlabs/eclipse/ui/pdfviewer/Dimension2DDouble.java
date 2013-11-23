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

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

/**
 * An implementation of {@link Dimension2D} using <code>double</code> values and
 * supporting read-only mode.
 * <p>
 * Since there is unfortunately no subclass-implementation like
 * {@link Point2D.Double}, but only {@link Dimension} which uses <code>int</code>
 * values, this implementation was required.
 * </p>
 *
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 */
public class Dimension2DDouble extends Dimension2D
{
	private volatile boolean readOnly;
	private double width;
	private double height;

	public Dimension2DDouble() { }

	public Dimension2DDouble(final double width, final double height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public double getWidth() {
		return width;
	}

	private void assertNotReadOnly()
	{
		if (readOnly) {
			throw new UnsupportedOperationException("This instance of Dimension2DDouble is read-only!"); //$NON-NLS-1$
		}
	}

	public void setWidth(final double width)
	{
		assertNotReadOnly();

		this.width = width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	public void setHeight(final double height) {
		assertNotReadOnly();

		this.height = height;
	}

	@Override
	public void setSize(final double width, final double height)
	{
		assertNotReadOnly();

		this.width = width;
		this.height = height;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != this.getClass() && !(obj instanceof Dimension2D)) {
			return false;
		}
		final Dimension2D o = (Dimension2D) obj;
		return this.width == o.getWidth() && this.height == o.getHeight();
	}

	@Override
	public int hashCode() {
		final long wbits = Double.doubleToLongBits(width);
		final long hbits = Double.doubleToLongBits(height);
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
