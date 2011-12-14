package org.nightlabs.eclipse.compatibility.treestate;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

public class TreeStateUtil {

	public static void sendEvent(Widget widget, Event event) {
		Method method;
		try {
			method = Widget.class.getDeclaredMethod("sendEvent", new Class[] {int.class, Event.class});
			method.setAccessible(true);
			method.invoke(widget, event.type, event);
		} catch (Exception ex) {
			// FIXME
			System.err.println(ex);
//			throw new RuntimeException(ex);
		}
	}
}
