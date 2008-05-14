package org.nightlabs.eclipse.ui.fckeditor.file;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
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
 */
public class ImageClippingArea extends Canvas implements PaintListener, ControlListener, MouseListener, MouseMoveListener, DisposeListener
{
	private Image sourceImage;
	private Image previewImage;
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

	private static final int outerBorder = 0;

	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		gc.setAdvanced(true);

		Point myBounds = getDimensions();

		if(sourceImage == null) {
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
		ImageData previewImageData = previewImage.getImageData();
		int offsetX = Math.round((myBounds.x - previewImageData.width - outerBorder) / 2f);
		int offsetY = Math.round((myBounds.y - previewImageData.height - outerBorder) / 2f);
		imageX = getClientArea().x + offsetX + outerBorder;
		imageY = getClientArea().y + offsetY + outerBorder;
		gc.drawImage(previewImage, imageX, imageY);

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

		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(
				imageX -1 + clippingArea.x,
				imageY -1 + clippingArea.y,
				clippingArea.width + 1,
				clippingArea.height + 1);
	}

	private Point getDimensions()
	{
		return new Point(getClientArea().width - getClientArea().x, getClientArea().height - getClientArea().y);
	}

	private void createPreviewImage()
	{
		ImageData sourceImageData = sourceImage.getImageData();
		Point imageBounds = new Point(sourceImageData.width, sourceImageData.height);
		Point myBounds = getDimensions();
		float mx = (float)imageBounds.x / (float)myBounds.x;
		float my = (float)imageBounds.y / (float)myBounds.y;
		float m = Math.max(mx, my);
		previewImage = new Image(getDisplay(), sourceImage.getImageData().scaledTo(Math.round(imageBounds.x / m) - 2, Math.round(imageBounds.y / m) - 2));
		if(clippingArea == null) {
			ImageData previewImageData = previewImage.getImageData();
			clippingArea = new Rectangle(0, 0, previewImageData.width, previewImageData.height);
		}
	}

	private void init()
	{
		try {
			bgEvenImage = new Image(getDisplay(), Activator.getDefault().getBundle().getResource("/icons/bg-even.gif").openStream());
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
		}
		bgEvenImage.dispose();
		bgEven.dispose();
		cursorSizeAll.dispose();
		cursorSizeNW.dispose();
		cursorSizeNS.dispose();
		cursorSizeSE.dispose();
		cursorSizeWE.dispose();
		System.out.println("DISPOSE");
	}

	/**
	 * Get the sourceImage.
	 * @return the sourceImage
	 */
	public Image getSourceImage()
	{
		return sourceImage;
	}

	/**
	 * Set the sourceImage.
	 * @param sourceImage the sourceImage to set
	 */
	public void setSourceImage(Image sourceImage)
	{
		this.sourceImage = sourceImage;
		if(previewImage != null) {
			previewImage.dispose();
			previewImage = null;
		}
	}

	public Rectangle getClippingArea()
	{
		return clippingArea;
	}

	public Rectangle getClippingAreaForSource()
	{
		if(sourceImage == null)
			return null;
		if(previewImage == null)
			// should never be null here...
			createPreviewImage();
		float mx = (float)previewImage.getImageData().width / (float)sourceImage.getImageData().width;
		float my = (float)previewImage.getImageData().height / (float)sourceImage.getImageData().height;
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
		int oldWidth = previewImage.getImageData().width;
		int oldHeight = previewImage.getImageData().height;
		previewImage.dispose();
		previewImage = null;
		createPreviewImage();
		int newWidth = previewImage.getImageData().width;
		int newHeight = previewImage.getImageData().height;
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

	private Anchor anchor = Anchor.NONE;
	private boolean dragging = false;
	private Point dragStart;
	private Rectangle dragClippingArea;

	private void calculateAnchor(int mouseX, int mouseY)
	{
		int tolerance = 5;
		//System.out.println(e.x+"/"+e.y);
		int clippingAreaAbsX = imageX + clippingArea.x;
		int clippingAreaAbsY = imageY + clippingArea.y;

		if(mouseX > clippingAreaAbsX - tolerance &&
				mouseY > clippingAreaAbsY - tolerance &&
				mouseX - tolerance < clippingAreaAbsX + clippingArea.width &&
				mouseY - tolerance < clippingAreaAbsY + clippingArea.height) {
			if(Math.abs(mouseX - clippingAreaAbsX) < tolerance &&
				Math.abs(mouseY - clippingAreaAbsY) < tolerance) {
				// nw
				anchor = Anchor.NW;
			} else if(Math.abs(mouseX - (clippingAreaAbsX + clippingArea.width)) < tolerance &&
					Math.abs(mouseY - clippingAreaAbsY) < tolerance) {
				// ne
				anchor = Anchor.NE;
			} else if(Math.abs(mouseX - clippingAreaAbsX) < tolerance &&
					Math.abs(mouseY - (clippingAreaAbsY + clippingArea.height)) < tolerance) {
				// sw
				anchor = Anchor.SW;
			} else if(Math.abs(mouseX - (clippingAreaAbsX + clippingArea.width)) < tolerance &&
					Math.abs(mouseY - (clippingAreaAbsY + clippingArea.height)) < tolerance) {
				// se
				anchor = Anchor.SE;
			} else if(Math.abs(mouseY - clippingAreaAbsY) < tolerance) {
				// n
				anchor = Anchor.N;
			} else if(Math.abs(mouseY - (clippingAreaAbsY + clippingArea.height)) < tolerance) {
				// s
				anchor = Anchor.S;
			} else if(Math.abs(mouseX - clippingAreaAbsX) < tolerance) {
				// w
				anchor = Anchor.W;
			} else if(Math.abs(mouseX - (clippingAreaAbsX + clippingArea.width)) < tolerance) {
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
		if(sourceImage == null)
			return;

		if(dragging) {
			int dx = e.x - dragStart.x;
			int dy = e.y - dragStart.y;
//			System.out.println("diff: "+dx+"/"+dy);
			int maxX = previewImage.getImageData().width - clippingArea.width;
			int maxY = previewImage.getImageData().height - clippingArea.height;
			int oldX = clippingArea.x;
			int oldY = clippingArea.y;
			switch (anchor) {
			case INNER:
				clippingArea.x = Math.max(0, Math.min(dragClippingArea.x + dx, maxX));
				clippingArea.y = Math.max(0, Math.min(dragClippingArea.y + dy, maxY));
				redraw();
				fireClippingAreaChanged();
				break;
			case NW:
				clippingArea.x = Math.max(0, dragClippingArea.x + dx);
				clippingArea.y = Math.max(0, dragClippingArea.y + dy);
				int actualdx = clippingArea.x - oldX;
				int actualdy = clippingArea.y - oldY;
				clippingArea.width -= actualdx;
				clippingArea.height -= actualdy;
				redraw();
				fireClippingAreaChanged();
				break;
			case N:
				clippingArea.y = Math.max(0, dragClippingArea.y + dy);
				actualdy = clippingArea.y - oldY;
				clippingArea.height -= actualdy;
				redraw();
				fireClippingAreaChanged();
				break;
			case W:
				clippingArea.x = Math.max(0, dragClippingArea.x + dx);
				System.out.println("w "+clippingArea.x);
				actualdx = clippingArea.x - oldX;
				clippingArea.width -= actualdx;
				redraw();
				fireClippingAreaChanged();
				break;
			case NE:
				clippingArea.y = Math.max(0, dragClippingArea.y + dy);
				actualdy = clippingArea.y - oldY;
				clippingArea.height -= actualdy;
				clippingArea.width = Math.min(dragClippingArea.width + dx, previewImage.getImageData().width - clippingArea.x);
				redraw();
				fireClippingAreaChanged();
				break;
			case E:
				clippingArea.width = Math.min(dragClippingArea.width + dx, previewImage.getImageData().width - clippingArea.x);
				redraw();
				break;
			case S:
				clippingArea.height = Math.min(dragClippingArea.height + dy, previewImage.getImageData().height - clippingArea.y);
				redraw();
				fireClippingAreaChanged();
				break;
			case SE:
				clippingArea.width = Math.min(dragClippingArea.width + dx, previewImage.getImageData().width - clippingArea.x);
				clippingArea.height = Math.min(dragClippingArea.height + dy, previewImage.getImageData().height - clippingArea.y);
				redraw();
				fireClippingAreaChanged();
				break;
			case SW:
				clippingArea.height = Math.min(dragClippingArea.height + dy, previewImage.getImageData().height - clippingArea.y);
				clippingArea.x = Math.max(0, dragClippingArea.x + dx);
				actualdx = clippingArea.x - oldX;
				clippingArea.width -= actualdx;
				redraw();
				fireClippingAreaChanged();
				break;
			default:
				break;
			}

		} else {
			calculateAnchor(e.x, e.y);
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
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(MouseEvent e)
	{
		if(sourceImage == null)
			return;

		clippingArea.x = 0;
		clippingArea.y = 0;
		clippingArea.width = previewImage.getImageData().width;
		clippingArea.height = previewImage.getImageData().height;
		fireClippingAreaChanged();
		redraw();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDown(MouseEvent e)
	{
		if(sourceImage == null)
			return;

		dragging = true;
		dragStart = new Point(e.x, e.y);
		dragClippingArea = new Rectangle(clippingArea.x, clippingArea.y, clippingArea.width, clippingArea.height);
		System.out.println("drag start");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseUp(MouseEvent e)
	{
		if(sourceImage == null)
			return;

		dragging = false;
		dragStart = null;
		dragClippingArea = null;
		System.out.println("drag end");
	}
}
