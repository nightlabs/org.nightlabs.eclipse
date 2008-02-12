package org.nightlabs.editor2d.viewer.ui.j2dswt;

/*
 * Copyright (c) 2005, Holongate.org
 *
 * All rights reserved.
 * Contributors:
 *               Christophe Avare - initial API and implementation
 *               Grant Slender - additional corrections and rewrite
 */

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

/**
 * The SWTGraphics2D class wraps the Graphics2D around a given SWT GC and mimics as much
 * of the original Graphics2D behavior as possible, only using GC methods.
 * <p>
 * This class can be used wherever a standard Graphics2D object is legal. The main purpose
 * of this class is then to reuse Java2D code without changing it, while drawing directly
 * in an SWT Drawable.
 * </p>
 * 
 * @author Christophe Avare
 * @author Grant Slender
 * @version $Revision: 1.2.2.2.2.1.2.15.2.8 $
 * @since 1.0
 */
public class SWTGraphics2D extends Graphics2D {
	/**
	 * A best effort approximation of the SWT.LINE_DOT style on Windows
	 */
	public final static float[] LINE_DOT = {3f, 3f};

	/**
	 * A best effort approximation of the SWT.LINE_DASH style on Windows
	 */
	public final static float[] LINE_DASH = {20f, 5f};

	/**
	 * A best effort approximation of the SWT.LINE_DASHDOT style on Windows
	 */
	public final static float[] LINE_DASHDOT = {10f, 5f, 3f, 5f};

	/**
	 * A best effort approximation of the SWT.LINE_DASHDOTDOT style on Windows
	 */
	public final static float[] LINE_DASHDOTDOT = {10f, 3f, 3f, 3f, 3f, 3f};

	public final static AffineTransform IDENTITY = new AffineTransform();

	public final static PaletteData RGB_PALETTE = new PaletteData(0x000000FF, 0x0000FF00,
		0x00FF0000);

	/**
	 * The current Stroke object (to avoid unecessary conversions)
	 */
	protected Stroke currentStroke;

	/**
	 * The current Font (to avoid unecessary conversions)
	 */
	protected Font currentFont;

	private org.eclipse.swt.graphics.Font _theFont;

	/**
	 * The current affine transform.
	 */
	protected AffineTransform currentTransform = new AffineTransform();

	/**
	 * The current user clip, or null if none has been set.
	 */
	protected Shape currentClip;

	/**
	 * The current user paint, or null if none has been set.
	 */
	protected Paint currentPaint;

	/**
	 * If true, this flag indicates that the currentPaint object have no SWT equivalent
	 * and thus must be simulated.
	 */
	protected boolean complexPaint = false;

	/**
	 * All current rendering hints. Only renderings hints meaningful to the current
	 * implementation are stored in this map, others are silently ignored.
	 */
	protected RenderingHints hints = new RenderingHints(null);

	/**
	 * The last value of GC.getBackground().
	 * 
	 * @see swapColors()
	 */
	private org.eclipse.swt.graphics.Color oldBg;

	/**
	 * The last value of GC.getForeground().
	 * 
	 * @see swapColors()
	 */
	private org.eclipse.swt.graphics.Color oldFg;

	/**
	 * If true, this flag means the fg / bg colors must be swapped before rendering. This
	 * flag is normally set to true to prevent the fillXXX methods to render using the
	 * background color (the SWT behavior) instead of the foreground color (the AWT
	 * behavior).
	 */
	protected boolean needSwap = true;

	/**
	 * Remembers the current line width (in pixels) to allow for line width compensation.
	 * <p>
	 * Each time the current line width is changed, a new line width of currentLineWidth *
	 * currentScale is stored in targetLineWidth to compensate the lack of line width
	 * calculation in SWT.
	 * </p>
	 */
	protected double currentLineWidth = 1;

	/**
	 * The original GC this SWTGaphics2D will use.
	 */
	protected GC _gc;

	/**
	 * The Device we will use to get device-related resources.
	 * <p>
	 * The constructors will ensure this value is never null.
	 * </p>
	 */
	protected Device _dev;

	/**
	 * For internal use only! *
	 */
	private SWTGraphics2D() {
	}

	/**
	 * Constructs a new <code>SWTGraphics2D</code> object. If <code>dev</code> is null
	 * a device will be obtained from <code>Display.getDefault()</code>
	 * 
	 * @see java.awt.Graphics2D
	 * 
	 * 
	 * @param gc
	 *            The SWT graphics context
	 * @param dev
	 *            The SWT display device
	 */
	public SWTGraphics2D(GC gc, Device dev) {
		super();
		_gc = gc;
		_dev = (dev == null) ? Display.getDefault() : dev;

		_gc.setAdvanced(true);
		// Retrieves and cache the current transform
		Transform t = new Transform(_dev);
		_gc.getTransform(t);
		currentTransform = toAWTTransform(t);
		t.dispose();

		// Retrieves the line width in user space units
		currentLineWidth = _gc.getLineWidth();

		// Retrieves and cache the current font
		currentFont = getAwtFont();

		// Retrieves and cache an approximation of the current Stroke object
		currentStroke = getAwtStroke();

		// Retrieves the current clip rectangle
		org.eclipse.swt.graphics.Rectangle clip = _gc.getClipping();
		currentClip = new Rectangle(clip.x, clip.y, clip.width, clip.height);

		// Set initial ering hints
		switch (_gc.getAntialias()) {
			case SWT.DEFAULT :
			case SWT.OFF :
				hints.put(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
				_gc.setAntialias(SWT.OFF);
				break;
			case SWT.ON :
				hints.put(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
				break;
		}
		switch (_gc.getTextAntialias()) {
			case SWT.DEFAULT :
			case SWT.OFF :
				hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
				_gc.setTextAntialias(SWT.OFF);
				break;
			case SWT.ON :
				hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				break;
		}
		switch (_gc.getInterpolation()) {
			case SWT.DEFAULT :
			case SWT.LOW :
				hints.put(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				_gc.setInterpolation(SWT.LOW);
				break;
			case SWT.HIGH :
				hints.put(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				break;
		}
	}

	public SWTGraphics2D(GC gc) {
		this(gc, null);
	}

	protected BufferedImage createCachedImage(int w, int h) {
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.getGraphics();
		g.setColor(new Color(255, 255, 255, 0));
		g.fillRect(0, 0, w, h);
		return img;
	}

	/**
	 * Font size in Java2D is expressed in pixels??? The javadoc says that font sizes are
	 * in points!
	 * <p>
	 * Font size is converted in pixels by multiplying SWT font size by screen DPI / 72.
	 * </p>
	 */
	private Font getAwtFont() {
		FontData f = _gc.getFont().getFontData()[0];
		int style = Font.PLAIN;
		if ((f.getStyle() & SWT.BOLD) != 0) {
			style = Font.BOLD;
		}
		if ((f.getStyle() & SWT.ITALIC) != 0) {
			style |= Font.ITALIC;
		}
		// The font size is in pixel size as per Java2D
		int pixels = (int) (f.getHeight() * _dev.getDPI().x / 72.0);
		return new Font(f.getName(), style, pixels);
	}

	private Stroke getAwtStroke() {
		int cap = BasicStroke.CAP_BUTT;
		switch (_gc.getLineCap()) {
			case SWT.CAP_SQUARE :
				cap = BasicStroke.CAP_SQUARE;
				break;
			case SWT.CAP_ROUND :
				cap = BasicStroke.CAP_ROUND;
				break;
		}
		int join = BasicStroke.JOIN_MITER;
		switch (_gc.getLineJoin()) {
			case SWT.JOIN_ROUND :
				join = BasicStroke.JOIN_ROUND;
				break;
			case SWT.JOIN_BEVEL :
				join = BasicStroke.JOIN_BEVEL;
				break;
		}

		float[] dashes = null;
		switch (_gc.getLineStyle()) {
			case SWT.LINE_DOT :
				dashes = LINE_DOT;
				break;
			case SWT.LINE_DASH :
				dashes = LINE_DASH;
				break;
			case SWT.LINE_DASHDOT :
				dashes = LINE_DASHDOT;
				break;
			case SWT.LINE_DASHDOTDOT :
				dashes = LINE_DASHDOTDOT;
				break;
			case SWT.LINE_CUSTOM :
				int[] dash = _gc.getLineDash();
				dashes = new float[dash.length];
				for (int i = 0; i < dash.length; i++) {
					dashes[i] = dash[i];
				}
				dash = null;
				break;
		}
		// Use default miter limit of 10 and phase of 0
		return new BasicStroke(_gc.getLineWidth(), cap, join, 10f, dashes, 0);
	}

	/**
	 * Calls setTransform on the GC using the currentTransform
	 */
	protected void updateTransform() {
		Transform t = toSWTTransform(currentTransform);
		_gc.setTransform(t);
		t.dispose();
	}

	/**
	 * Converts two int[] points into a transformed single int[] points: where x is
	 * followed by y
	 */
	public int[] projectPoints(int[] xPoints, int[] yPoints, int nPoints) {
		float[] buf = new float[nPoints * 2];
		int j = 0;
		for (int i = 0; i < nPoints; i++) {
			buf[j++] = xPoints[i];
			buf[j++] = yPoints[i];
		}
		currentTransform.transform(buf, 0, buf, 0, nPoints);
		int[] coords = new int[buf.length];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = (int) buf[i];
		}
		buf = null;
		return coords;
	}

	/**
	 * Converts x, y, w, h into a transformed Rectangle
	 */
	public Rectangle projectRectangle(int x, int y, int w, int h) {
		Rectangle r = new Rectangle(x, y, w, h);
		return currentTransform.createTransformedShape(r).getBounds();
	}

	/**
	 * Swap the GC foreground and background colors.
	 * <p>
	 * It is expected that methods like fillXXX have a call to this method before and
	 * after the actual call (calling swapColors in pairs restores the initial state).
	 * </p>
	 */
	protected void swapColors() {
		if (needSwap) {
			oldBg = _gc.getBackground();
			oldFg = _gc.getForeground();
			_gc.setBackground(oldFg);
			_gc.setForeground(oldBg);
		}
	}

	/**
	 * Sets the line width to currentLineWidth * currentScale.
	 * <p>
	 * The GC transform is set to identity: it is expected that only methods that restore
	 * the GC transform will call adjustLineWidth().
	 * </p>
	 * <p>
	 * Because the GC line width is an integer, this correction will lead to line width of
	 * 0 or 1 in most cases.
	 * </p>
	 */
	protected void adjustLineWidth() {
		double currentScale = (Math.abs(currentTransform.getScaleX()) + Math
			.abs(currentTransform.getScaleY())) * 0.5;
		_gc.setTransform(null);
		_gc.setLineWidth((int) Math.round(currentLineWidth * currentScale));
	}

	/**
	 * Converts an AWT Image into a BufferedImage of type TYPE_4BYTE_ABGR of the same
	 * size.
	 * <p>
	 * The image background is first cleared with the supplied bg color if not null. Only
	 * fully constructed images or null are returned.
	 * </p>
	 * 
	 * @param img
	 *            The source Image
	 * @param bg
	 *            The BufferedImage background, or null if no background should be
	 *            painted.
	 * @param observer
	 *            The image observer that monitor the loading process
	 * @return A new BufferedImage that is a copy of the source Image, or null if the
	 *         Image is not fully loaded.
	 */
	public BufferedImage toBufferedImage(Image img, Color bg, ImageObserver observer) {
		if (img != null) {
			int h = img.getHeight(observer);
			int w = img.getWidth(observer);
			if (w > 0 && h > 0) {
				BufferedImage buf = createCachedImage(w, h);
				Graphics2D g2d = buf.createGraphics();
				g2d.addRenderingHints(hints);
				if (g2d.drawImage(img, 0, 0, bg, observer)) {
					return buf;
				}
			}
		}
		return null;
	}

	/**
	 * Converts a Java2D AffineTransform into an SWT Transform.
	 * <p>
	 * The caller must call dispose() on the returned instance.
	 * </p>
	 * <p>
	 * This method is not static because the returned transform is bound to the same
	 * Device as the orginal GC this instance represents.
	 * </p>
	 * 
	 * @param t
	 *            The Java2D affine transform
	 * @return An SWT Transform initialized with the affine transform values
	 */
	public Transform toSWTTransform(AffineTransform t) {
		double[] m = new double[6];
		t.getMatrix(m);
		return new Transform(_dev, (float) m[0], (float) m[1], (float) m[2],
			(float) m[3], (float) m[4], (float) m[5]);
	}

	/**
	 * Converts an SWT Transform into a Java2D AffineTransform.
	 * <p>
	 * Hopefully, the matrix elements have the same meaning in both libraries!
	 * </p>
	 * 
	 * @param t
	 *            The SWT Transform
	 * @return An AWT AffineTransform initialized with the SWT transform values
	 */
	public static AffineTransform toAWTTransform(Transform t) {
		float[] m = new float[6];
		t.getElements(m);
		AffineTransform a = new AffineTransform(m[0], m[1], m[2], m[3], m[4], m[5]);
		m = null;
		return a;
	}

	/**
	 * Suboptimal image conversion! The image must be of type BYTE_ABGR or everything is
	 * broken!
	 * 
	 * @param bi the {@link BufferedImage} to convert to an SWT Image
	 * @return converted image
	 */
	public org.eclipse.swt.graphics.Image toSWTImage(BufferedImage bi) {
		if (bi == null) {
			return null;
		}
		int w = bi.getWidth();
		int h = bi.getHeight();
		Raster r = null;
		if (bi.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
			// Can't use createCachedImage here, as the bi variable may
			// be the cached image itself!
			// BufferedImage off = new BufferedImage(w, h,
			// BufferedImage.TYPE_4BYTE_ABGR);
			BufferedImage off = createCachedImage(w, h);
			off.createGraphics().drawRenderedImage(bi, IDENTITY);
			r = off.getRaster();
		} else {
			r = bi.getRaster();
		}
		byte[] data = ((DataBufferByte) r.getDataBuffer()).getData();
		ImageData imData = new ImageData(w, h, 32, RGB_PALETTE, 4, data);
		byte[] alpha = new byte[imData.width * imData.height];
		for (int y = 0; y < imData.height; y++) {
			for (int x = 0; x < imData.width; x++) {
				alpha[x + y * imData.width] = (byte) r.getSample(x, y, 3);
			}
		}
		imData.alphaData = alpha;
		org.eclipse.swt.graphics.Image im = new org.eclipse.swt.graphics.Image(_dev,
			imData);
		data = null;
		alpha = null;
		r = null;
		return im;
	}

	/**
	 * Converts an SWT Color into an AWT Color.
	 * <p>
	 * The returned Color have its alpha channel set to the current alpha value of the GC.
	 * As a consequence, colors cannot be cached or resused.
	 * </p>
	 * 
	 * @param rgb
	 *            The SWT color
	 * @return The AWT color
	 */
	public Color toAWTColor(org.eclipse.swt.graphics.Color rgb) {
		return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), _gc.getAlpha());
	}

	/**
	 * Converts an AWT Color into an SWT Color.
	 * <p>
	 * The alpha channel of the AWT color is lost in the process.
	 * </p>
	 * <p>
	 * The caller must call dispose() on the returned instance.
	 * </p>
	 * <p>
	 * This method is not static because the returned color is bound to the same Device as
	 * the orginal GC this instance represents.
	 * </p>
	 * 
	 * @param rgba the awt color to convert to a swt color
	 * @return the swt color
	 */
	public org.eclipse.swt.graphics.Color toSWTColor(Color rgba) {
		return new org.eclipse.swt.graphics.Color(_dev, rgba.getRed(), rgba.getGreen(),
			rgba.getBlue());
	}

	/**
	 * Converts a Java2D Shape into an SWT Path object.
	 * <p>
	 * Coordinates are supposed to be expressed in the original (untransformed) user space
	 * following the Java2D convention for coordinates.
	 * </p>
	 * <p>
	 * This method uses the PathIterator mechanism to iterate over the shape.
	 * </p>
	 * <p>
	 * The caller must call dispose() on the returned instance.
	 * </p>
	 * <p>
	 * This method is not static because the returned path is bound to the same Device as
	 * the orginal GC this instance represents.
	 * </p>
	 * 
	 * @param s
	 *            The Shape to convert
	 * @return The equivalent Path. <b>As a side effect, the GC fill rule is set to the
	 *         path winding rule</b>.
	 */
	public Path toPath(Shape s, AffineTransform t) {
		float[] coords = new float[6];
		Path parent = new Path(_dev);
		Path p = parent;
		PathIterator it = s.getPathIterator(t);
		// In SWT, the winding rule is set in the GC, not in the Path object!
		// Changing the winding rule here means the converted path must be drawn
		// as soon as possible
		if (it.getWindingRule() == PathIterator.WIND_EVEN_ODD) {
			_gc.setFillRule(SWT.FILL_EVEN_ODD);
		} else {
			_gc.setFillRule(SWT.FILL_WINDING);
		}
		while (!it.isDone()) {
			int type = it.currentSegment(coords);
			switch (type) {
				case PathIterator.SEG_MOVETO :
					p.moveTo(coords[0], coords[1]);
					break;
				case PathIterator.SEG_LINETO :
					p.lineTo(coords[0], coords[1]);
					break;
				case PathIterator.SEG_QUADTO :
					p.quadTo(coords[0], coords[1], coords[2], coords[3]);
					break;
				case PathIterator.SEG_CUBICTO :
					p.cubicTo(coords[0], coords[1], coords[2], coords[3], coords[4],
						coords[5]);
					break;
				case PathIterator.SEG_CLOSE :
					p.close();
					if (p != parent) {
						parent.addPath(p);
					}
					p = new Path(_dev);
					break;
			}
			it.next();
		}
		if (p != parent) {
			parent.addPath(p);
		}
		return parent;
	}

	@Override
	public void addRenderingHints(Map map) {
		setRenderingHints(map);
	}

	@Override
	public void clip(Shape s) {
		Area clip = new Area(currentClip);
		clip.intersect(new Area(currentTransform.createTransformedShape(s)));
		setClip(clip);
	}

	@Override
	public void draw(Shape s) {
		if (!complexPaint) {
			adjustLineWidth();
			Path p = toPath(s, currentTransform);
			_gc.drawPath(p);
			p.dispose();
			updateTransform();			
		} else {
			fill(currentStroke.createStrokedShape(s));
		}		
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		fill(g.getOutline(x, y));
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		if (img == null) {
			return;
		}
		org.eclipse.swt.graphics.Image swtImg = null;
		if (op != null) {
			swtImg = toSWTImage(op.filter(img, null));
		} else {
			swtImg = toSWTImage(img);
		}
		_gc.drawImage(swtImg, x, y);
		swtImg.dispose();
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		BufferedImage buf = toBufferedImage(img, null, obs);
		if (buf == null) {
			return false;
		}
		drawImage(buf, new AffineTransformOp(xform, hints), 0, 0);
		return true;
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		drawRenderedImage(img.createDefaultRendering(), xform);
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		Rectangle target = new Rectangle(0, 0, img.getWidth(), img.getHeight());
		target = xform.createTransformedShape(target).getBounds();
		BufferedImage buf = createCachedImage(target.width, target.height);
		Graphics2D g2d = buf.createGraphics();
		g2d.addRenderingHints(hints);
		g2d.drawRenderedImage(img, xform);
		drawImage(buf, new AffineTransformOp(IDENTITY, hints), 0, 0);
		buf = null;
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		// FIXME: no attribute is currently used!
		char[] buf = new char[iterator.getEndIndex() - iterator.getBeginIndex()];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = iterator.next();
		}
		drawString(new String(buf, 0, buf.length), x, y);
		buf = null;
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		drawString(iterator, (float) x, (float) y);
	}

	/**
	 * The text drawing origin must be shift by (ascent + descent) to compensate the
	 * difference of drawString() in AWT and SWT.
	 */

	@Override
	public void drawString(String str, float x, float y) {
		int fh = _gc.getFontMetrics().getAscent() + _gc.getFontMetrics().getDescent();
		_gc.drawString(str, (int) x, (int) (y - fh), true);
	}

	@Override
	public void drawString(String str, int x, int y) {
		drawString(str, (float) x, (float) y);
	}

	@Override
	public void fill(Shape s) {
		if (!complexPaint) {
			// toPath may destroy the current fill rule
			int oldrule = _gc.getFillRule();
			swapColors();
			Path p = toPath(s, IDENTITY);
			_gc.fillPath(p);
			p.dispose();
			_gc.setFillRule(oldrule);
			swapColors();
		} else {
			Rectangle device = s.getBounds();
			Rectangle user = currentTransform.createTransformedShape(device).getBounds();
			PaintContext ctx = currentPaint.createContext(ColorModel
				.getRGBdefault(), device, user, currentTransform, hints);
			Raster ra = ctx.getRaster(user.x, user.y, user.width, user.height);
			WritableRaster wr = ra.createCompatibleWritableRaster();
			wr.setDataElements(0, 0, ra);
			BufferedImage bi = new BufferedImage(ctx.getColorModel(), wr, true, null);
			Shape oldClip = currentClip;
			clip(s);
			_gc.setTransform(null);
			drawImage(bi, null, user.x, user.y);
			updateTransform();
			setClip(oldClip);
			bi = null;
			wr = null;
			ra = null;
		}
	}

	@Override
	public Color getBackground() {
		return toAWTColor(_gc.getBackground());
	}

	/**
	 * Only SRC rule is meaningful in SWT!
	 */

	@Override
	public Composite getComposite() {
		return AlphaComposite.Src;
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		// FIXME
		return null;
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return new FontRenderContext(currentTransform, true, true);
	}

	@Override
	public Paint getPaint() {
		return currentPaint;
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		return hints.get(hintKey);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return hints;
	}

	@Override
	public Stroke getStroke() {
		return currentStroke;
	}

	@Override
	public AffineTransform getTransform() {
		return new AffineTransform(currentTransform);
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		// FIXME
		return false;
	}

	@Override
	public void rotate(double theta, double x, double y) {
		currentTransform.rotate(theta, x, y);
		updateTransform();
	}

	@Override
	public void rotate(double theta) {
		currentTransform.rotate(theta);
		updateTransform();
	}

	@Override
	public void scale(double sx, double sy) {
		currentTransform.scale(sx, sy);
		updateTransform();
	}

	@Override
	public void setBackground(Color color) {
		_gc.setBackground(toSWTColor(color));
		_gc.setAlpha(color.getAlpha());
	}

	/**
	 * Composite ops are ignored for SWT compatibility.
	 */

	@Override
	public void setComposite(Composite comp) {
	}

	/**
	 * Approximate a Paint object with an SWT equivalent.
	 * 
	 * @see java.awt.Graphics2D#setPaint(java.awt.Paint)
	 */

	@Override
	public void setPaint(Paint _paint) {
		currentPaint = _paint;
		complexPaint = false;
		needSwap = true;
		if (currentPaint == null)
			return;
		if (currentPaint instanceof Color) {
			setColor((Color) currentPaint);
		} else if (currentPaint instanceof GradientPaint) {
			GradientPaint gp = (GradientPaint) currentPaint;
			Point2D p1 = gp.getPoint1();
			Point2D p2 = gp.getPoint2();
			// in SWT if p1 == p2 then nothing is painted.
			// in AWT the colour is still painted ???
			// so, use the c1 color
			if (p1.distance(p2) == 0) {
				setColor(gp.getColor1());
				return;
			}
			org.eclipse.swt.graphics.Color c1 = toSWTColor(gp.getColor1());
			org.eclipse.swt.graphics.Color c2 = toSWTColor((gp.getColor2()));
			Pattern p = new Pattern(_dev, (float) p1.getX(), (float) p1.getY(),
				(float) p2.getX(), (float) p2.getY(), c1, c2);
			_gc.setBackgroundPattern(p);
			c1.dispose();
			c2.dispose();
			needSwap = false;
		} else if (currentPaint instanceof TexturePaint) {
			TexturePaint tp = (TexturePaint) currentPaint;
			BufferedImage awtImg = tp.getImage();
			org.eclipse.swt.graphics.Image swtImg = toSWTImage(awtImg);
			Pattern p = new Pattern(_dev, swtImg);
			_gc.setBackgroundPattern(p);
			swtImg.dispose();
			needSwap = false;
		} else {
			complexPaint = true;
		}
	}

	/**
	 * Only honor KEY_ANTIALIASING, KEY_TEXT_ANTIALIASING and KEY_INTERPOLATION.
	 */

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		hints.put(hintKey, hintValue);
		if (hintKey == RenderingHints.KEY_ANTIALIASING) {
			if (hintValue == RenderingHints.VALUE_ANTIALIAS_OFF)
				_gc.setAntialias(SWT.OFF);
			else
				_gc.setAntialias(SWT.ON);
		}
		if (hintKey == RenderingHints.KEY_TEXT_ANTIALIASING) {
			if (hintValue == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
				_gc.setTextAntialias(SWT.OFF);
			else
				_gc.setTextAntialias(SWT.ON);
		}
		if (hintKey == RenderingHints.KEY_INTERPOLATION) {
			if (hintValue == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR) {
				_gc.setInterpolation(SWT.NONE);
			} else if (hintValue == RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
				_gc.setInterpolation(SWT.LOW);
			} else if (hintValue == RenderingHints.VALUE_INTERPOLATION_BICUBIC) {
				_gc.setInterpolation(SWT.HIGH);
			}
		}
	}

	@Override
	public void setRenderingHints(Map hints) {
		Iterator it = hints.keySet().iterator();
		while (it.hasNext()) {
			Key key = (Key) it.next();
			setRenderingHint(key, hints.get(key));
		}
	}

	/**
	 * Convert a Stroke into updates in the GC attributes.
	 * <p>
	 * The line width is a silly integer in SWT, which means it cannot be accurately used
	 * with non-identity transforms!
	 * </p>
	 * <p>
	 * The mitter limit value cannot be specified in SWT and thus is ignored.
	 * </p>
	 * <p>
	 * The dashes for custom line styles are, like the line width, expressed in pixels!
	 * </p>
	 */

	@Override
	public void setStroke(Stroke s) {
		currentStroke = s;
		// We can only do useful things with BasicStroke objects!
		if (s instanceof BasicStroke) {
			BasicStroke bs = (BasicStroke) s;

			// Set the line width
			currentLineWidth = bs.getLineWidth();

			// Set the line join
			switch (bs.getLineJoin()) {
				case BasicStroke.JOIN_BEVEL :
					_gc.setLineJoin(SWT.JOIN_BEVEL);
					break;
				case BasicStroke.JOIN_MITER :
					_gc.setLineJoin(SWT.JOIN_MITER);
					break;
				case BasicStroke.JOIN_ROUND :
					_gc.setLineJoin(SWT.JOIN_ROUND);
					break;
			}

			// Set the line cap
			switch (bs.getEndCap()) {
				case BasicStroke.CAP_BUTT :
					_gc.setLineCap(SWT.CAP_FLAT);
					break;
				case BasicStroke.CAP_ROUND :
					_gc.setLineCap(SWT.CAP_ROUND);
					break;
				case BasicStroke.CAP_SQUARE :
					_gc.setLineCap(SWT.CAP_SQUARE);
					break;
			}

			// Set the line style to solid by default
			_gc.setLineStyle(SWT.LINE_SOLID);

			// Look for any line style
			float[] dashes = bs.getDashArray();
			if (dashes != null) {
				// FIXME: dumb approximation here!
				// FIXME: should look closer at units for line dashes
				int[] a = new int[dashes.length];
				for (int i = 0; i < a.length; i++) {
					a[i] = (int) dashes[i];
				}
				// this also sets the line style to LINE_CUSTOM
				_gc.setLineDash(a);
				a = null;
				dashes = null;
			}
		}
	}

	@Override
	public void setTransform(AffineTransform t) {
		currentTransform.setTransform(t);
		updateTransform();
	}

	/**
	 * The shear transform is manually computed, no SWT equivalent.
	 */

	@Override
	public void shear(double shx, double shy) {
		currentTransform.shear(shx, shy);
		updateTransform();
	}

	@Override
	public void transform(AffineTransform t) {
		currentTransform.concatenate(t);
		updateTransform();
	}

	@Override
	public void translate(double tx, double ty) {
		currentTransform.translate(tx, ty);
		updateTransform();
	}

	@Override
	public void translate(int x, int y) {
		translate((double) x, (double) y);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		int alpha = _gc.getAlpha();
		_gc.setAlpha(0);
		_gc.fillRectangle(x, y, width, height);
		_gc.setAlpha(alpha);
	}

	/**
	 * clipRect() performs a cumulative intersection of the new rectangle and the current
	 * clip rectangle.
	 */

	@Override
	public void clipRect(int x, int y, int width, int height) {
		clip(new Rectangle(x, y, width, height));
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		_gc.copyArea(x, y, width, height, dx, dy);
	}

	/**
	 * Creates a copy of this instance.
	 * <p>
	 * Note that the GC are shared (don't know how to copy a GC rom a GC without a
	 * platform dependent code), which means this method returns and almost useless
	 * object!
	 * </p>
	 */

	@Override
	public Graphics create() {
		SWTGraphics2D g2d = new SWTGraphics2D();
		g2d._gc = _gc;
		g2d._dev = _dev;
		g2d.currentTransform = new AffineTransform(currentTransform);
		g2d.currentLineWidth = currentLineWidth;
		g2d.currentClip = currentClip;
		g2d.hints.add(hints);
		g2d.needSwap = needSwap;
		g2d.oldBg = oldBg;
		g2d.oldFg = oldFg;
		return g2d;
	}

	@Override
	public void dispose() {
		_gc = null;
		_dev = null;
		hints.clear();
		hints = null;
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		if (!complexPaint) {
			Rectangle r = projectRectangle(x, y, width, height);
			adjustLineWidth();
			// FIXME: start and end angles must be recomputed if transform
			// contains some
			// shear?
			_gc.drawArc(r.x, r.y, r.width, r.height, startAngle, arcAngle);
			updateTransform();
		} else {
			fill(currentStroke.createStrokedShape(new Arc2D.Float(x, y, width, height,
				startAngle, arcAngle, Arc2D.OPEN)));
		}
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgColor,
		ImageObserver observer) {
		return drawImage(img, x, y, -1, -1, bgColor, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		return drawImage(img, x, y, null, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
		Color bgcolor, ImageObserver observer) {
		BufferedImage buf = toBufferedImage(img, bgcolor, observer);
		if (buf != null) {
			org.eclipse.swt.graphics.Image swtImg = toSWTImage(buf);
			org.eclipse.swt.graphics.Rectangle b = swtImg.getBounds();
			if (width < 0) {
				width = b.width;
			}
			if (height < 0) {
				height = b.height;
			}
			_gc.drawImage(swtImg, 0, 0, b.width, b.height, x, y, width, height);
			swtImg.dispose();
			buf.flush();
			buf = null;
			return true;
		}
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
		ImageObserver observer) {
		return drawImage(img, x, y, width, height, null, observer);
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1,
		int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		BufferedImage buf = toBufferedImage(img, bgcolor, observer);
		if (buf != null) {
			org.eclipse.swt.graphics.Image swtImg = toSWTImage(buf);
			_gc.drawImage(swtImg, sx1, sy1, (sx2 - sx1), (sy2 - sy1), dx1, dy1,
				(dx2 - dx1), (dy2 - dy1));
			swtImg.dispose();
			buf.flush();
			buf = null;
			return true;
		}
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1,
		int sy1, int sx2, int sy2, ImageObserver observer) {
		return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, observer);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		if (!complexPaint) {
			float[] coords = new float[]{x1, y1, x2, y2};
			currentTransform.transform(coords, 0, coords, 0, 2);
			adjustLineWidth();
			_gc.drawLine((int) coords[0], (int) coords[1], (int) coords[2],
				(int) coords[3]);
			updateTransform();
			coords = null;
		} else {
			fill(currentStroke.createStrokedShape(new Line2D.Float(x1, y1, x2, y2)));
		}
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		if (!complexPaint) {
			Rectangle r = projectRectangle(x, y, width, height);
			adjustLineWidth();
			_gc.drawOval(r.x, r.y, r.width, r.height);
			updateTransform();
		} else {
			fill(currentStroke
				.createStrokedShape(new Ellipse2D.Float(x, y, width, height)));
		}
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		if (!complexPaint) {
			int[] coords = projectPoints(xPoints, yPoints, nPoints);
			adjustLineWidth();
			_gc.drawPolygon(coords);
			updateTransform();
			coords = null;
		} else {
			fill(currentStroke.createStrokedShape(new Polygon(xPoints, yPoints, nPoints)));
		}
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		if (!complexPaint) {
			int[] coords = projectPoints(xPoints, yPoints, nPoints);
			adjustLineWidth();
			_gc.drawPolyline(coords);
			updateTransform();
			coords = null;
		} else {
			GeneralPath p = new GeneralPath(Path2D.WIND_EVEN_ODD, nPoints);
			p.moveTo(xPoints[0], yPoints[0]);
			for (int i = 1; i < nPoints; i++) {
				p.lineTo(xPoints[i], yPoints[i]);
			}
			fill(currentStroke.createStrokedShape(p));
		}
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth,
		int arcHeight) {
		if (!complexPaint) {
			Rectangle r = projectRectangle(x, y, width, height);
			adjustLineWidth();
			_gc.drawRoundRectangle(r.x, r.y, r.width, r.height,
				(int) (arcWidth * currentTransform.getScaleX()),
				(int) (arcHeight * currentTransform.getScaleY()));
			updateTransform();
		} else {
			fill(currentStroke.createStrokedShape(new RoundRectangle2D.Float(x, y, width,
				height, arcWidth, arcHeight)));
		}
	}

	/**
	 * This method must swap colors if required.
	 * 
	 */

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		if (!complexPaint) {
			swapColors();
			_gc.fillArc(x, y, width, height, startAngle, arcAngle);
			swapColors();
		} else {
			fill(new Arc2D.Float(x, y, width, height, startAngle, arcAngle, Arc2D.PIE));
		}
	}

	/**
	 * This method must swap colors if required.
	 * 
	 */

	@Override
	public void fillOval(int x, int y, int width, int height) {
		if (!complexPaint) {
			swapColors();
			_gc.fillOval(x, y, width, height);
			swapColors();
		} else {
			fill(new Ellipse2D.Float(x, y, width, height));
		}
	}

	/**
	 * This method must swap colors if required.
	 * 
	 */

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		if (!complexPaint) {
			int[] coords = projectPoints(xPoints, yPoints, nPoints);
			swapColors();
			_gc.fillPolygon(coords);
			swapColors();
			coords = null;
		} else {
			fill(new Polygon(xPoints, yPoints, nPoints));
		}
	}

	/**
	 * This method must swap colors if required.
	 * 
	 */

	@Override
	public void fillRect(int x, int y, int width, int height) {
		if (!complexPaint) {
			swapColors();
			_gc.fillRectangle(x, y, width, height);
			swapColors();
		} else {
			fill(new Rectangle(x, y, width, height));
		}
	}

	/**
	 * This method must swap colors if required.
	 * 
	 */

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth,
		int arcHeight) {
		if (!complexPaint) {
			swapColors();
			_gc.fillRoundRectangle(x, y, width, height, arcWidth, arcHeight);
			swapColors();
		} else {
			fill(new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight));
		}
	}

	@Override
	public Shape getClip() {
		return currentClip;
	}

	@Override
	public Rectangle getClipBounds() {
		return (currentClip != null) ? currentClip.getBounds() : null;
	}

	@Override
	public Color getColor() {
		return toAWTColor(_gc.getForeground());
	}

	@Override
	public Font getFont() {
		return currentFont;
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return Toolkit.getDefaultToolkit().getFontMetrics(f);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		setClip(new Rectangle(x, y, width, height));
	}

	@Override
	public void setClip(Shape clip) {
		// If clip is not null, sets the GC clip to the transformed
		// shape
		currentClip = clip;
		if (clip != null) {
			Path p = toPath(clip, IDENTITY);
			_gc.setTransform(null);
			_gc.setClipping(p);
			updateTransform();
			p.dispose();
		} else {
			// Reset the clip region
			_gc.setClipping((org.eclipse.swt.graphics.Rectangle) null);
		}
	}

	@Override
	public void setColor(Color c) {
		_gc.setForeground(toSWTColor(c));
		_gc.setAlpha(c.getAlpha());
		needSwap = true;
	}

	/**
	 * Font size in Java2D is expressed in pixels??? The javadoc says that font sizes are
	 * in points!
	 * <p>
	 * Font size is converted in points by multiplying AWT font size by 72/ screen DPI.
	 * </p>
	 */

	@Override
	public void setFont(Font font) {
		currentFont = font;
		if (_theFont != null) {
			_theFont.dispose();
			_theFont = null;
		}
		int style = SWT.NORMAL;
		if (font.isBold()) {
			style |= SWT.BOLD;
		}
		if (font.isItalic()) {
			style |= SWT.ITALIC;
		}
		// The font used is in pixel size as per Java2D
		int points = (int) (font.getSize2D() * 72.0 / _dev.getDPI().x);

		//System.err.println("Testing "+font.getName());
		FontData[] fd = _dev.getFontList(null, true);
		for (int i = 0; i < fd.length; i++) {
			//System.err.print("\t"+fd[i].getName()+": ");
			if (fd[i].getName().toUpperCase().matches(font.getName().toUpperCase())) {
				fd[i].setHeight(points);
				fd[i].setStyle(style);
				_theFont = new org.eclipse.swt.graphics.Font(_dev, fd[i]);
				_gc.setFont(_theFont);
				//System.err.println("match!");
				break;
			}
			//System.err.println("failed!");
		}
		if (_theFont == null) {
			//System.err.println(font + ": Unknown SWT font!");
		}
		_gc.setFont(_theFont);
	}

	@Override
	public void setPaintMode() {
		_gc.setXORMode(false);
	}

	@Override
	public void setXORMode(Color c1) {
		// FIXME: alternate color should be set to c1?
		_gc.setXORMode(true);
	}
}
