package org.nightlabs.base.ui.util;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Adapted from http://tom-eclipse-dev.blogspot.com/2007/01/tableviewers-and-nativelooking.html
 *
 * @author Tom Schindl & Alexander Ljungberg (siehe link)
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public final class JFaceUtil
{
	private static final String CHECKED_KEY = "CHECKED"; //$NON-NLS-1$
	private static final String UNCHECK_KEY = "UNCHECKED"; //$NON-NLS-1$
	private static final String DISABLED_UNCHECKED_KEY = "DISABLED_UNCHECKED"; //$NON-NLS-1$
	private static final String DISABLED_CHECKED_KEY = "DISABLED_CHECKED"; //$NON-NLS-1$

	private static Image makeShot(Control control, boolean type, boolean enabled)
	{
		// Hopefully no platform uses exactly this color because we'll make
		// it transparent in the image.
		Color greenScreen = new Color(control.getDisplay(), 222, 223, 224);

		Shell shell = new Shell(control.getShell(), SWT.NO_TRIM);

		// otherwise we have a default gray color
		shell.setBackground(greenScreen);

		Button button = new Button(shell, SWT.CHECK);
		button.setEnabled(enabled);
		button.setBackground(greenScreen);
		button.setSelection(type);

		// otherwise an image is located in a corner
		button.setLocation(1, 1);
		Point bsize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		// otherwise an image is stretched by width
		bsize.x = Math.max(bsize.x - 1, bsize.y - 1);
		bsize.y = Math.max(bsize.x - 1, bsize.y - 1);
		button.setSize(bsize);
		shell.setSize(bsize);

		shell.open();
		GC gc = new GC(shell);
		Image image = new Image(control.getDisplay(), bsize.x, bsize.y);
		gc.copyArea(image, 0, 0);
		gc.dispose();
		shell.close();

		ImageData imageData = image.getImageData();
		imageData.transparentPixel = imageData.palette.getPixel(greenScreen
			.getRGB());

		return new Image(control.getDisplay(), imageData);
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
		return getCheckBoxImage(viewer, checkState, true);
	}

	/**
	 * Returns an Image of the native <code>enabled</code> checkbox in the <code>checkState</code>.
	 * It first checks whether there is already an Image registered in the JFace image registry under
	 * the {@link #CHECKED_KEY}. If not takes images of the native checkbox and registeres them in the
	 * JFace image registry.
	 *
	 * @param viewer
	 *          The viewer via which the shell for taking the pictures of the native checkbox are
	 *          taken.
	 * @param checkState
	 *          The state in which the checkbox should be.
	 * @param enabled
	 * 					The enable state of the checkbox image to return.
	 * @return An Image of the native checkbox in the <code>checkState</code>.
	 */
	public static Image getCheckBoxImage(StructuredViewer viewer, boolean checkState, boolean enabled)
	{
		if (JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY) == null) {
			JFaceResources.getImageRegistry().put(CHECKED_KEY,
					makeShot(viewer.getControl(), true, true));
		}
		
		if (JFaceResources.getImageRegistry().getDescriptor(DISABLED_UNCHECKED_KEY) == null) {
			JFaceResources.getImageRegistry().put(DISABLED_UNCHECKED_KEY,
					makeShot(viewer.getControl(), false, false));
		}

		if (JFaceResources.getImageRegistry().getDescriptor(DISABLED_CHECKED_KEY) == null) {
			JFaceResources.getImageRegistry().put(DISABLED_CHECKED_KEY,
					makeShot(viewer.getControl(), true, false));
		}

		if (JFaceResources.getImageRegistry().getDescriptor(UNCHECK_KEY) == null) {
			JFaceResources.getImageRegistry().put(UNCHECK_KEY,
					makeShot(viewer.getControl(), false, true));
		}
		
		if (checkState)
		{
			if (enabled)
				return JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY).createImage();
			else
				return JFaceResources.getImageRegistry().getDescriptor(DISABLED_CHECKED_KEY).createImage();
		}

		if (enabled)
			return JFaceResources.getImageRegistry().getDescriptor(UNCHECK_KEY).createImage();
		else
			return JFaceResources.getImageRegistry().getDescriptor(DISABLED_UNCHECKED_KEY).createImage();
	}
}
