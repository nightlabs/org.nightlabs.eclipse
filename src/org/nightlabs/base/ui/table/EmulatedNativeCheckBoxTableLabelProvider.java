package org.nightlabs.base.ui.table;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.util.JFaceUtil;

/**
 * Adapted from http://tom-eclipse-dev.blogspot.com/2007/01/tableviewers-and-nativelooking.html
 * 
 * You can also use the Util method of {@link JFaceUtil#getCheckBoxImage(org.eclipse.jface.viewers.StructuredViewer, boolean)}.
 */
public abstract class EmulatedNativeCheckBoxTableLabelProvider
extends TableLabelProvider
implements ITableLabelProvider
{
  private static final String CHECKED_KEY = "CHECKED"; //$NON-NLS-1$
  private static final String UNCHECK_KEY = "UNCHECKED"; //$NON-NLS-1$

  public EmulatedNativeCheckBoxTableLabelProvider(TableViewer viewer) {
    if (JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY) == null) {
      JFaceResources.getImageRegistry().put(UNCHECK_KEY, makeShot(viewer.getControl().getShell(), false));
      JFaceResources.getImageRegistry().put(CHECKED_KEY, makeShot(viewer.getControl().getShell(), true));
    }
  }

  private Image makeShot(Shell _shell, boolean type) {
  	// Hopefully no platform uses exactly this color because we'll make
		// it transparent in the image.
		Color greenScreen = new Color(_shell.getDisplay(), 222, 223, 224);

		Shell shell = new Shell(_shell, SWT.NO_TRIM);

		// otherwise we have a default gray color
		shell.setBackground(greenScreen);

		Button button = new Button(shell, SWT.CHECK);
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
		Image image = new Image(shell.getDisplay(), bsize.x, bsize.y);
		gc.copyArea(image, 0, 0);
		gc.dispose();
		shell.close();

		ImageData imageData = image.getImageData();
		imageData.transparentPixel = imageData.palette.getPixel(greenScreen
			.getRGB());

		return new Image(_shell.getDisplay(), imageData);
		
//    Shell s = new Shell(shell,SWT.NO_TRIM);
//    Button b = new Button(s,SWT.CHECK);
//    b.setSelection(type);
//    Point bsize = b.computeSize(SWT.DEFAULT, SWT.DEFAULT);
//    b.setSize(bsize);
//    b.setLocation(0, 0);
//    s.setSize(bsize);
//    s.open();
//
//    GC gc = new GC(b);
//    Image image = new Image(shell.getDisplay(), bsize.x, bsize.y);
//    gc.copyArea(image, 0, 0);
//    gc.dispose();
//
//    s.close();
//
//    return image;
  }

	public Image getCheckBoxImage(boolean checkState) {
    if(checkState) {
      return JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY).createImage();
    } else {
      return JFaceResources.getImageRegistry().getDescriptor(UNCHECK_KEY).createImage();
    }
  }
}