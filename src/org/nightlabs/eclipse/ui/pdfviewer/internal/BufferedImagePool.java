package org.nightlabs.eclipse.ui.pdfviewer.internal;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nightlabs.eclipse.ui.pdfviewer.resource.Messages;

public class BufferedImagePool
{
	private static class BufferedImageCarrier
	{
		public boolean acquired;
		public BufferedImage bufferedImage;
	}

	private List<BufferedImageCarrier> pool = new LinkedList<BufferedImageCarrier>();

	public synchronized BufferedImage acquire(int width, int height)
	{
		BufferedImage result = null;
		for (Iterator<BufferedImageCarrier> it = pool.iterator(); it.hasNext(); ) {
			BufferedImageCarrier carrier = it.next();
			if (carrier.acquired)
				continue;

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
			BufferedImageCarrier carrier = new BufferedImageCarrier();
			carrier.bufferedImage = result;
			carrier.acquired = true;
			pool.add(carrier);
		}

		return result;
	}

	public synchronized void release(BufferedImage bufferedImage)
	{
		// We have only about 2 entries in the pool, so we can do this by iteration and don't need additional
		// indexing (would make it even slower due to the overhead).
		for (BufferedImageCarrier carrier : pool) {
			if (carrier.bufferedImage == bufferedImage) {
				if (!carrier.acquired)
					throw new IllegalArgumentException("This BufferedImage is known to this pool, but not acquired! " + bufferedImage); //$NON-NLS-1$

				carrier.acquired = false;
				return;
			}
        }
		throw new IllegalArgumentException("This BufferedImage is not know to this pool! " + bufferedImage); //$NON-NLS-1$
	}
}
