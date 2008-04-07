package org.nightlabs.base.ui.util;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

/**
 * Adapted from http://tom-eclipse-dev.blogspot.com/2007/01/tableviewers-and-nativelooking.html
 * 
 * @author unascribed
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public final class JFaceUtil
{
	private static final String CHECKED_KEY = "CHECKED";
	private static final String UNCHECK_KEY = "UNCHECKED";

	private static Image makeShot(Shell shell, boolean type, Color backround)
	{
		Shell s = new Shell(shell, SWT.NO_TRIM);
		Button b = new Button(s, SWT.CHECK);
		b.setBackground(backround);
		b.setSelection(type);
		Point bsize = b.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		b.setSize(bsize);
		b.setLocation(0, 0);
		s.setSize(bsize);
		s.open();

		GC gc = new GC(b);
		Image image = new Image(shell.getDisplay(), bsize.x, bsize.y);
		gc.copyArea(image, 0, 0);
		gc.dispose();

		s.close();

		return image;
	}

	/**
	 * Returns an Image of the native checkbox in the <code>checkState</code>. It first checks
	 * whether there is already an Image registered in the JFace image registry under the
	 * {@link #CHECKED_KEY}. If not takes images of the native checkbox and registeres them in the
	 * JFace image registry.
	 * 
	 * @param viewer
	 *          The viewer via which the shell for taking the pictures of the native checkbox are
	 *          taken.
	 * @param checkState
	 *          The state in which the checkbox should be.
	 * @return An Image of the native checkbox in the <code>checkState</code>.
	 */
	public static Image getCheckBoxImage(StructuredViewer viewer, boolean checkState)
	{
		if (JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY) == null)
		{
			JFaceResources.getImageRegistry().put(UNCHECK_KEY,
				makeShot(viewer.getControl().getShell(), false, viewer.getControl().getBackground()));
			JFaceResources.getImageRegistry().put(CHECKED_KEY,
				makeShot(viewer.getControl().getShell(), true, viewer.getControl().getBackground()));
		}

		if (checkState)
		{
			return JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY).createImage();
		}
		else
		{
			return JFaceResources.getImageRegistry().getDescriptor(UNCHECK_KEY).createImage();
		}
	}
}
