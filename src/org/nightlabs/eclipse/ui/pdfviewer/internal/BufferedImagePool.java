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
package org.nightlabs.eclipse.ui.pdfviewer.internal;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @version $Revision$ - $Date$
 * @author frederik loeser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class BufferedImagePool
{
	private static class BufferedImageCarrier
	{
		public boolean acquired;
		public BufferedImage bufferedImage;
	}

	private List<BufferedImageCarrier> pool = new LinkedList<BufferedImageCarrier>();

	public synchronized BufferedImage acquire(final int width, final int height)
	{
		BufferedImage result = null;
		for (final Iterator<BufferedImageCarrier> it = pool.iterator(); it.hasNext(); ) {
			final BufferedImageCarrier carrier = it.next();
			if (carrier.acquired) {
				continue;
			}

			if (carrier.bufferedImage.getWidth() == width && carrier.bufferedImage.getHeight() == height) {
				if (result == null) {
					result = carrier.bufferedImage;
					carrier.acquired = true;
				}
			}
			else {
				carrier.bufferedImage.flush();
				it.remove();
			}
        }

		if (result == null) {
			result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			final BufferedImageCarrier carrier = new BufferedImageCarrier();
			carrier.bufferedImage = result;
			carrier.acquired = true;
			pool.add(carrier);
		}

		return result;
	}

	public synchronized void release(final BufferedImage bufferedImage)
	{
		// We have only about 2 entries in the pool, so we can do this by iteration and don't need additional
		// indexing (would make it even slower due to the overhead).
		for (final BufferedImageCarrier carrier : pool) {
			if (carrier.bufferedImage == bufferedImage) {
				if (!carrier.acquired) {
					throw new IllegalArgumentException("This BufferedImage is known to this pool, but not acquired! " + bufferedImage); //$NON-NLS-1$
				}

				carrier.acquired = false;
				return;
			}
        }
		throw new IllegalArgumentException("This BufferedImage is not know to this pool! " + bufferedImage); //$NON-NLS-1$
	}
}
