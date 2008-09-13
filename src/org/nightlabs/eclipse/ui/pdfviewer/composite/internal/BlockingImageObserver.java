/**
 * 
 */
package org.nightlabs.eclipse.ui.pdfviewer.composite.internal;

import java.awt.Image;
import java.awt.image.ImageObserver;

class BlockingImageObserver implements ImageObserver {
	private boolean renderingFinished = false;
	private boolean renderingAborted = false;
	private int lastInfoflags = 0;

	public BlockingImageObserver() { }

	private static final long timeoutMSec = 60000;

	@Override
	public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		lastInfoflags = infoflags;

		if ((infoflags & ImageObserver.ALLBITS) != 0)
			renderingFinished = true;

		if ((infoflags & ImageObserver.ABORT) != 0)
			renderingAborted = true;

		this.notifyAll();
		return !renderingFinished;
	}

	public synchronized void waitForRendering()
	{
		long start = System.currentTimeMillis();
		while (!renderingFinished && !renderingAborted) {
			if (System.currentTimeMillis() - start > timeoutMSec)
				throw new WaitForRenderingException("Timeout waiting for rendering to finish or abort!");

			try {
				this.wait(10000);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		if (renderingAborted)
			throw new WaitForRenderingException("Rendering was aborted! lastInfoflags=" + lastInfoflags);
	}
}