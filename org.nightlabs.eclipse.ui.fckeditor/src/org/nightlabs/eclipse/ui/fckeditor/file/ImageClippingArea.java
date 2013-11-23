/* ************************************************************************
 * org.nightlabs.eclipse.ui.fckeditor - Eclipse RCP FCKeditor Integration *
 * Copyright (C) 2008 NightLabs - http://NightLabs.org                    *
 *                                                                        *
 * This library is free software; you can redistribute it and/or          *
 * modify it under the terms of the GNU Lesser General Public             *
 * License as published by the Free Software Foundation; either           *
 * version 2.1 of the License, or (at your option) any later version.     *
 *                                                                        *
 * This library is distributed in the hope that it will be useful,        *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of         *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU      *
 * Lesser General Public License for more details.                        *
 *                                                                        *
 * You should have received a copy of the GNU Lesser General Public       *
 * License along with this library; if not, write to the                  *
 *     Free Software Foundation, Inc.,                                    *
 *     51 Franklin St, Fifth Floor,                                       *
 *     Boston, MA  02110-1301  USA                                        *
 *                                                                        *
 * Or get it online:                                                      *
 *     http://www.gnu.org/copyleft/lesser.html                            *
 **************************************************************************/
package org.nightlabs.eclipse.ui.fckeditor.file;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.eclipse.ui.fckeditor.Activator;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
 */
public class ImageClippingArea extends Canvas implements PaintListener, ControlListener, MouseListener, MouseMoveListener, MouseTrackListener, DisposeListener
{
//	private Image sourceImage;
	private ImageData sourceImageData;
	private Image previewImage;
	private ImageData previewImageData;
	private Image previewImageDisabled;
	private Rectangle clippingArea;
	private Image bgEvenImage;
	private Pattern bgEven;
	private int imageX;
	private int imageY;
	private Cursor cursorDefault;
	private Cursor cursorSizeAll;

	private Cursor cursorSizeNW;
	private Cursor cursorSizeNE;
	private Cursor cursorSizeSW;
	private Cursor cursorSizeSE;

	private Cursor cursorSizeNS;
	private Cursor cursorSizeWE;

	private Collection<ClippingAreaListener> listeners;
	private static final int outerBorder = 0;
	private Anchor anchor = Anchor.NONE;
	private boolean dragging = false;
	private Point dragStart;
	private Rectangle dragClippingArea;
	private static final int anchorSnapTolerance = 5;
	private static final int clippingAreaMinimumSize = 2;

	/**
	 * Create a new ImageClippingArea instance.
	 * @param parent
	 * @param style
	 */
	public ImageClippingArea(Composite parent, int style)
	{
		super(parent, style);
		init();
		addPaintListener(this);
		addControlListener(this);
		addMouseMoveListener(this);
		addMouseListener(this);
		addMouseTrackListener(this);
		addDisposeListener(this);
	}

	public void addClippingAreaListener(ClippingAreaListener listener)
	{
		if(listeners == null)
			listeners = new LinkedList<ClippingAreaListener>();
		listeners.add(listener);
	}

	public void removeClippingAreaListener(ClippingAreaListener listener)
	{
		if(listeners != null) {
			listeners.remove(listener);
			if(listeners.isEmpty())
				listeners = null;
		}
	}

	protected void fireClippingAreaChanged()
	{
		if(listeners == null)
			return;
		Rectangle clippingAreaForSource = getClippingAreaForSource();
		for (ClippingAreaListener listener : listeners)
			listener.clippingAreaChanged(clippingAreaForSource);
	}

	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		gc.setAdvanced(true);

//		System.out.println("paint: "+e.x+"/"+e.y+" "+e.width+"/"+e.height);

		Point myBounds = getDimensions();

		if(sourceImageData == null) {
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			gc.drawLine(getClientArea().x, getClientArea().y, getClientArea().width - 1, getClientArea().height - 1);
			gc.drawLine(getClientArea().width - 1, getClientArea().y, getClientArea().x, getClientArea().height - 1);
			gc.drawRectangle(getClientArea().x, getClientArea().y, getClientArea().width-1, getClientArea().height-1);
			return;
		}

		if(previewImage == null) {
//				clippingArea = new Rectangle(0, 0, previewImageData.width, previewImageData.height);
			createPreviewImage();
		}
		int offsetX = Math.round((myBounds.x - previewImageData.width - outerBorder) / 2f);
		int offsetY = Math.round((myBounds.y - previewImageData.height - outerBorder) / 2f);
		imageX = getClientArea().x + offsetX + outerBorder;
		imageY = getClientArea().y + offsetY + outerBorder;

		boolean paintAll = false;

		if(!paintAll) {
			partialDrawImage(e, previewImageDisabled,
					0,
					0,
					imageX,
					imageY,
					previewImageData.width,
					previewImageData.height);
		} else {
			gc.drawImage(previewImageDisabled,
					0,
					0,
					previewImageData.width,
					previewImageData.height,
					imageX,
					imageY,
					previewImageData.width,
					previewImageData.height);
		}

		if(!paintAll) {
			partialDrawImage(e, previewImage,
					clippingArea.x,
					clippingArea.y,
					imageX + clippingArea.x,
					imageY + clippingArea.y,
					clippingArea.width,
					clippingArea.height);
		} else {
			gc.drawImage(previewImage,
					clippingArea.x,
					clippingArea.y,
					clippingArea.width,
					clippingArea.height,
					imageX + clippingArea.x,
					imageY + clippingArea.y,
					clippingArea.width,
					clippingArea.height);
		}

		/*
		gc.setBackgroundPattern(bgEven);
		// top
		gc.fillRectangle(
				imageX,
				imageY,
				previewImageData.width,
				clippingArea.y - 1);
		// bottom
		gc.fillRectangle(
				imageX,
				imageY + clippingArea.height + clippingArea.y + 1,
				previewImageData.width,
				previewImageData.height - clippingArea.y - clippingArea.height);

		// left
		gc.fillRectangle(
				imageX,
				imageY + clippingArea.y - 1,
				clippingArea.x - 1,
				clippingArea.height + 2);
		// right
		gc.fillRectangle(
				imageX + clippingArea.x + clippingArea.width,
				imageY + clippingArea.y - 1,
				previewImageData.width - clippingArea.x - clippingArea.width,
				clippingArea.height + 2);
		*/

		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(
				imageX -1 + clippingArea.x,
				imageY -1 + clippingArea.y,
				clippingArea.width + 1,
				clippingArea.height + 1);
	}

	private static void partialDrawImage(PaintEvent e, Image image, int srcX, int srcY, int destX, int destY, int width, int height)
	{
		// nothing to do checks:
		if(e.x + e.width < destX)
			// target is left of image
			return;
		if(e.y + e.height < destY)
			// target is top of image
			return;
		if(e.x > destX + width)
			// target is right of image
			return;
		if(e.y > destY + height)
			// target is bottom of image
			return;

		if(e.x > destX) {
			int diff = e.x - destX;
			destX += diff;
			srcX += diff;
			width -= diff;
		}
		if(e.y > destY) {
			int diff = e.y - destY;
			destY += diff;
			srcY += diff;
			height -= diff;
		}
		if(width > e.width) {
			width = e.width;
		}
		if(height > e.height) {
			height = e.height;
		}

		//System.out.println("draw: "+srcX+"/"+srcY+" -> "+destX+"/"+destY+" ("+width+"/"+height+")");
		e.gc.drawImage(image, srcX, srcY, width, height, destX, destY, width, height);
	}

	private Point getDimensions()
	{
		return new Point(getClientArea().width - getClientArea().x, getClientArea().height - getClientArea().y);
	}

	private void createPreviewImage()
	{
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				Point imageBounds = new Point(sourceImageData.width, sourceImageData.height);
				Point myBounds = getDimensions();
				float mx = (float)imageBounds.x / (float)myBounds.x;
				float my = (float)imageBounds.y / (float)myBounds.y;
				float m = Math.max(mx, my);
				previewImageData = sourceImageData.scaledTo(Math.round(imageBounds.x / m) - 2, Math.round(imageBounds.y / m) - 2);
				previewImage = new Image(getDisplay(), previewImageData);

				previewImageDisabled = new Image(getDisplay(), previewImageData);
				GC gc = new GC(previewImageDisabled);
				gc.setBackgroundPattern(bgEven);
				gc.fillRectangle(0, 0, previewImageData.width, previewImageData.width);
				gc.dispose();

				if(clippingArea == null) {
					clippingArea = new Rectangle(0, 0, previewImageData.width, previewImageData.height);
				}
			}
		});
	}

	private void init()
	{
		try {
			bgEvenImage = new Image(getDisplay(), Activator.getDefault().getBundle().getResource("/icons/bg-even.gif").openStream()); //$NON-NLS-1$
			bgEven = new Pattern(getDisplay(), bgEvenImage);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		cursorDefault = getShell().getCursor();
		cursorSizeAll = new Cursor(getDisplay(), SWT.CURSOR_SIZEALL);
		cursorSizeNW = new Cursor(getDisplay(), SWT.CURSOR_SIZENW);
		cursorSizeNE = new Cursor(getDisplay(), SWT.CURSOR_SIZENE);
		cursorSizeSW = new Cursor(getDisplay(), SWT.CURSOR_SIZESW);
		cursorSizeSE = new Cursor(getDisplay(), SWT.CURSOR_SIZESE);

		cursorSizeNS = new Cursor(getDisplay(), SWT.CURSOR_SIZENS);
		cursorSizeWE = new Cursor(getDisplay(), SWT.CURSOR_SIZEWE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
	 */
	@Override
	public void widgetDisposed(DisposeEvent e)
	{
		super.dispose();
		if(previewImage != null) {
			previewImage.dispose();
			previewImage = null;
			previewImageData = null;
		}
		sourceImageData = null;
		bgEvenImage.dispose();
		bgEven.dispose();
		cursorSizeAll.dispose();
		cursorSizeNW.dispose();
		cursorSizeNS.dispose();
		cursorSizeSE.dispose();
		cursorSizeWE.dispose();
		//System.out.println("DISPOSE");
	}

	/**
	 * Get the source image data.
	 * @return the sourceImageData
	 */
	public ImageData getSourceImageData()
	{
		return sourceImageData;
	}

	/**
	 * Set the source image data.
	 * @param sourceImage the sourceImageData to set
	 */
	public void setSourceImage(ImageData sourceImageData)
	{
		this.sourceImageData = sourceImageData;
		if(previewImage != null) {
			previewImage.dispose();
			previewImage = null;
		}
	}

	/**
	 * Set the sourceImage.
	 * @param sourceImage the sourceImage to set
	 */
	public void setSourceImage(Image sourceImage)
	{
		setSourceImage(sourceImage.getImageData());
	}

	public Rectangle getClippingArea()
	{
		return clippingArea;
	}

	public Rectangle getClippingAreaForSource()
	{
		if(sourceImageData == null)
			return null;
		if(previewImage == null)
			// should never be null here...
			createPreviewImage();
		float mx = (float)previewImageData.width / (float)sourceImageData.width;
		float my = (float)previewImageData.height / (float)sourceImageData.height;
		//System.out.println("m: "+mx+" "+my);
		return new Rectangle(
				Math.round(clippingArea.x / mx),
				Math.round(clippingArea.y / my),
				Math.round(clippingArea.width / mx),
				Math.round(clippingArea.height / my));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
	 */
	@Override
	public void controlMoved(ControlEvent e)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
	 */
	@Override
	public void controlResized(ControlEvent e)
	{
		if(previewImage == null)
			return;
		int oldWidth = previewImageData.width;
		int oldHeight = previewImageData.height;
		previewImage.dispose();
		previewImage = null;
		createPreviewImage();
		int newWidth = previewImageData.width;
		int newHeight = previewImageData.height;
		float mx = (float)newWidth / (float)oldWidth;
		float my = (float)newHeight / (float)oldHeight;
		//System.out.println("mx: "+mx+" my: "+my);
		clippingArea.x = Math.round(clippingArea.x * mx);
		clippingArea.width = Math.round(clippingArea.width * mx);
		clippingArea.y = Math.round(clippingArea.y * my);
		clippingArea.height = Math.round(clippingArea.height * my);
	}

	private enum Anchor {
		NONE,
		INNER,
		NW,
		N,
		NE,
		E,
		SE,
		S,
		SW,
		W;
	}

	private void calculateAnchor(int mouseX, int mouseY)
	{
		//System.out.println(e.x+"/"+e.y);
		int clippingAreaAbsX = imageX + clippingArea.x;
		int clippingAreaAbsY = imageY + clippingArea.y;

		if(mouseX > clippingAreaAbsX - anchorSnapTolerance &&
				mouseY > clippingAreaAbsY - anchorSnapTolerance &&
				mouseX - anchorSnapTolerance < clippingAreaAbsX + clippingArea.width &&
				mouseY - anchorSnapTolerance < clippingAreaAbsY + clippingArea.height) {
			if(Math.abs(mouseX - clippingAreaAbsX) < anchorSnapTolerance &&
				Math.abs(mouseY - clippingAreaAbsY) < anchorSnapTolerance) {
				// nw
				anchor = Anchor.NW;
			} else if(Math.abs(mouseX - (clippingAreaAbsX + clippingArea.width)) < anchorSnapTolerance &&
					Math.abs(mouseY - clippingAreaAbsY) < anchorSnapTolerance) {
				// ne
				anchor = Anchor.NE;
			} else if(Math.abs(mouseX - clippingAreaAbsX) < anchorSnapTolerance &&
					Math.abs(mouseY - (clippingAreaAbsY + clippingArea.height)) < anchorSnapTolerance) {
				// sw
				anchor = Anchor.SW;
			} else if(Math.abs(mouseX - (clippingAreaAbsX + clippingArea.width)) < anchorSnapTolerance &&
					Math.abs(mouseY - (clippingAreaAbsY + clippingArea.height)) < anchorSnapTolerance) {
				// se
				anchor = Anchor.SE;
			} else if(Math.abs(mouseY - clippingAreaAbsY) < anchorSnapTolerance) {
				// n
				anchor = Anchor.N;
			} else if(Math.abs(mouseY - (clippingAreaAbsY + clippingArea.height)) < anchorSnapTolerance) {
				// s
				anchor = Anchor.S;
			} else if(Math.abs(mouseX - clippingAreaAbsX) < anchorSnapTolerance) {
				// w
				anchor = Anchor.W;
			} else if(Math.abs(mouseX - (clippingAreaAbsX + clippingArea.width)) < anchorSnapTolerance) {
				// e
				anchor = Anchor.E;
			} else {
				// inside
				anchor = Anchor.INNER;
			}
		} else {
			anchor = Anchor.NONE;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseMove(MouseEvent e)
	{
		if(sourceImageData == null)
			return;

		if(dragging)
			applyDragging(e.x, e.y);
		else
			applyCursor(e.x, e.y);
	}

	/**
	 * Change the clipping area depending on the mouse moving.
	 * @param mouseX mouse x coordinate
	 * @param mouseY mouse y coordinate
	 */
	private void applyDragging(int mouseX, int mouseY)
	{
		Rectangle oldClippingArea = new Rectangle(clippingArea.x, clippingArea.y, clippingArea.width, clippingArea.height);
		int dx = mouseX - dragStart.x;
		int dy = mouseY - dragStart.y;
//			System.out.println("diff: "+dx+"/"+dy);
		int maxX = previewImageData.width - clippingArea.width;
		int maxY = previewImageData.height - clippingArea.height;
		int oldX = clippingArea.x;
		int oldY = clippingArea.y;
		int actualdx;
		int actualdy;

		if(anchor == Anchor.INNER) {
			clippingArea.x = Math.max(0, Math.min(dragClippingArea.x + dx, maxX));
			clippingArea.y = Math.max(0, Math.min(dragClippingArea.y + dy, maxY));
		} else {
			if(anchor == Anchor.N || anchor == Anchor.NW || anchor == Anchor.NE) {
				clippingArea.y = Math.max(0, dragClippingArea.y + dy);
				clippingArea.y = Math.min(clippingArea.y, dragClippingArea.y + dragClippingArea.height - clippingAreaMinimumSize);
				actualdy = clippingArea.y - oldY;
				clippingArea.height -= actualdy;
			}
			if(anchor == Anchor.W || anchor == Anchor.NW || anchor == Anchor.SW) {
				clippingArea.x = Math.max(0, dragClippingArea.x + dx);
				clippingArea.x = Math.min(clippingArea.x, dragClippingArea.x + dragClippingArea.width - clippingAreaMinimumSize);
				//System.out.println("w "+clippingArea.x);
				actualdx = clippingArea.x - oldX;
				clippingArea.width -= actualdx;
			}
			if(anchor == Anchor.E || anchor == Anchor.SE || anchor == Anchor.NE) {
				clippingArea.width = Math.min(dragClippingArea.width + dx, previewImageData.width - clippingArea.x);
				clippingArea.width = Math.max(clippingArea.width, clippingAreaMinimumSize);
			}
			if(anchor == Anchor.S || anchor == Anchor.SE || anchor == Anchor.SW) {
				clippingArea.height = Math.min(dragClippingArea.height + dy, previewImageData.height - clippingArea.y);
				clippingArea.height = Math.max(clippingArea.height, clippingAreaMinimumSize);
			}
		}

		Rectangle redrawRect = oldClippingArea.union(clippingArea);
		redraw(redrawRect.x + imageX - 1, redrawRect.y + imageY - 1, redrawRect.width + 2, redrawRect.height + 2, false);
		fireClippingAreaChanged();
	}

	/**
	 * Apply the cursor for the current mouse position.
	 * @param mouseX mouse x coordinate
	 * @param mouseY mouse y coordinate
	 */
	private void applyCursor(int mouseX, int mouseY)
	{
		calculateAnchor(mouseX, mouseY);
		Cursor cursorToUse;
		switch (anchor) {
			case INNER: cursorToUse = cursorSizeAll; break;
			case NW: cursorToUse = cursorSizeNW; break;
			case N: cursorToUse = cursorSizeNS; break;
			case NE: cursorToUse = cursorSizeNE; break;
			case E: cursorToUse = cursorSizeWE; break;
			case SE: cursorToUse = cursorSizeSE; break;
			case S: cursorToUse = cursorSizeNS; break;
			case SW: cursorToUse = cursorSizeSW; break;
			case W: cursorToUse = cursorSizeWE; break;
			default: cursorToUse = cursorDefault; break;
		}
		if(getShell().getCursor() != cursorToUse)
			getShell().setCursor(cursorToUse);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(MouseEvent e)
	{
		if(sourceImageData == null)
			return;

		clippingArea.x = 0;
		clippingArea.y = 0;
		clippingArea.width = previewImageData.width;
		clippingArea.height = previewImageData.height;
		fireClippingAreaChanged();
		redraw();

		// TEST:
//		redraw(Math.max(0, e.x-10), Math.max(0, e.y-10), 20, 20, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDown(MouseEvent e)
	{
		if(sourceImageData == null)
			return;

		dragging = true;
		dragStart = new Point(e.x, e.y);
		dragClippingArea = new Rectangle(clippingArea.x, clippingArea.y, clippingArea.width, clippingArea.height);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseUp(MouseEvent e)
	{
		if(sourceImageData == null)
			return;

		dragging = false;
		dragStart = null;
		dragClippingArea = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseEnter(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseEnter(MouseEvent e)
	{
		applyCursor(e.x, e.y);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseExit(MouseEvent e)
	{
		getShell().setCursor(cursorDefault);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseHover(MouseEvent e)
	{
	}
}
