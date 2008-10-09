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

import java.awt.Image;
import java.awt.image.ImageObserver;

/**
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 * @author frederik loeser - frederik at nightlabs dot de
 */
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
				throw new WaitForRenderingException("Timeout waiting for rendering to finish or abort!"); //$NON-NLS-1$

			try {
				this.wait(10000);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		if (renderingAborted)
			throw new WaitForRenderingException("Rendering was aborted! lastInfoflags=" + lastInfoflags); //$NON-NLS-1$
	}
}