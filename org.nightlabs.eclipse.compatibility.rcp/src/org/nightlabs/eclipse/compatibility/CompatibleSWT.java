package org.nightlabs.eclipse.compatibility;

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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;

public class CompatibleSWT {
	public static final int RIGHT_TO_LEFT = SWT.RIGHT_TO_LEFT;
	public static final int HIDE_SELECTION = SWT.HIDE_SELECTION;
	public static final int HIGH = SWT.HIGH;
	public static final int IMAGE_DISABLE= SWT.IMAGE_DISABLE;
	public static final int LINE_SOLID= SWT.LINE_SOLID;
	public static final int ICON = SWT.ICON;
	
	public static final int Paint = SWT.Paint;
	public static final int MouseMove = SWT.MouseMove;
	
	public static final int  TRAVERSE_ARROW_PREVIOUS = SWT.TRAVERSE_ARROW_PREVIOUS;
	public static final int  TRAVERSE_ARROW_NEXT = SWT.TRAVERSE_ARROW_NEXT;
	
	public static int getModifyEventTime(ModifyEvent event) {
		return event.time;
	}
	
	public static int getFocusEventTime(FocusEvent event) {
		return event.time;
	}
	
	public static Image newImage(Device device, int w, int h) {
		return new Image(device, w, h);
	}

	public static Image newImage(Device device, Rectangle rectangle) {
		return new Image(device, rectangle);
	}

	public static void removePaintListener(Control  control, PaintListener listener) {
		control.removePaintListener(listener);
	}
	
	public static void addPaintListener(Control  control, PaintListener listener) {
		control.addPaintListener(listener);
	}
	
	public static void addMouseTrackListener(Control control, MouseTrackListener listener) {
		control.addMouseTrackListener(listener);
	}
	
	public static void removeMouseTrackListener(Control control, MouseTrackListener listener) {
		control.removeMouseTrackListener(listener);
	}
	
	
	public static int getVerticalScrollBarWidth(Composite composite) {
  		ScrollBar sb = composite.getVerticalBar();
		if(sb.isEnabled() && sb.isVisible())
			return sb.getSize().x;
		else
			return 0;
	}
	
	public static int getVerticalScrollBarHeight(Composite composite) {
		return composite.getVerticalBar().getSize().y;
	}
	
	public static void dispose(Image image) {
		image.dispose();
	}
	
	public static void addMouseMoveListener(Control control, MouseMoveListener listener) {
		control.addMouseMoveListener(listener);
	}
	
	public static void removeMouseMoveListener(Control control, MouseMoveListener listener) {
		control.removeMouseMoveListener(listener);
	}
	
	public static void addMouseWheelListener(Control control, MouseWheelListener listener) {
		control.addMouseWheelListener(listener);
	}
	
	public static void removeMouseWheelListener(Control control, MouseWheelListener listener) {
		control.removeMouseWheelListener(listener);
	}
	
	public static int getMouseEventCount(MouseEvent event) {
		return event.count;
	}
	
	
}
