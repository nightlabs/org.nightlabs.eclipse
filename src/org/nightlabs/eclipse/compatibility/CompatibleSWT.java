package org.nightlabs.eclipse.compatibility;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

public class CompatibleSWT {
	public static final int RIGHT_TO_LEFT = 0;
	public static final int HIDE_SELECTION = 0;
	public static final int HIGH = 0;
	public static final int IMAGE_DISABLE = 0;
	public static final int LINE_SOLID = 0;
	public static final int ICON = 0;

	public static final int Paint = 0;
	public static final int MouseMove = 0;

	public static final int TRAVERSE_ARROW_PREVIOUS = 1 << 5;
	public static final int TRAVERSE_ARROW_NEXT = 1 << 6;

	public static void removeListener(Widget widget, Listener listener) {

	}

	public static int getModifyEventTime(ModifyEvent event) {
		return 0;
	}

	public static int getFocusEventTime(FocusEvent event) {
		return 0;
	}

	public static int getMouseEventCount(MouseEvent event) {
		return 0;
	}

	public static Image newImage(Device device, int w, int h) {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			if (ImageIO.write(image, "png", bos)) {
				ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
				return new Image(device, new ImageData(bis));
			}else
				throw new IllegalStateException("cannot find writer for PNG format");
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

	public static Image newImage(Device device, Rectangle rectangle) {
		return newImage(device, rectangle.width, rectangle.height);
	}

	public static void removePaintListener(Control control, PaintListener listener) {
		throw new NotAvailableInRAPException();
	}

	public static void addPaintListener(Control control, PaintListener listener) {
		throw new NotAvailableInRAPException();
	}

	public static void addMouseTrackListener(Control control, MouseTrackListener listener) {

	}

	public static void addMouseMoveListener(Control control, MouseMoveListener listener) {

	}

	public static void removeMouseTrackListener(Control control, MouseTrackListener listener) {

	}

	public static void addMouseWheelListener(Control control, MouseWheelListener listener) {

	}

	public static void removeMouseWheelListener(Control control, MouseWheelListener listener) {

	}

	public static int getVerticalScrollBarWidth(Composite composite) {
		// was:
		/*
		 * ScrollBar sb = c.getVerticalBar(); if(sb.isEnabled() && sb.isVisible()) width -= sb.getSize().x;
		 */

		return 10;
	}

	public static int getVerticalScrollBarHeight(Composite composite) {

		// was: c.getVerticalBar().getSize().y;

		return 10;
	}

	public static void dispose(final Image errorImage) {
		final Display display = Display.getDefault();
		if(display == null)
			SWT.error( SWT.ERROR_THREAD_INVALID_ACCESS );
		
		Thread other = new Thread() {
			public void run() {
				if(errorImage.getDevice() != null) {  // call getDevice on NON UI thread. it will be null, if this 
					                                  // is a factory image
					display.syncExec(new Runnable() { // otherwise call dispose, but this time from the UI thread 
						@Override
						public void run() {
							errorImage.dispose();
						}
					});
				}
			}
		};
		
		other.start();
		
		try {
			other.join();
		} catch (InterruptedException e) {
		}
	}

}
