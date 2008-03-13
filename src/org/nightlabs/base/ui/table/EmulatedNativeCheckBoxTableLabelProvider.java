package org.nightlabs.base.ui.table;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

/**
 * Adapted from http://tom-eclipse-dev.blogspot.com/2007/01/tableviewers-and-nativelooking.html
 */
public abstract class EmulatedNativeCheckBoxTableLabelProvider
extends TableLabelProvider
implements ITableLabelProvider
{
  private static final String CHECKED_KEY = "CHECKED";
  private static final String UNCHECK_KEY = "UNCHECKED";

  public EmulatedNativeCheckBoxTableLabelProvider(TableViewer viewer) {
    if (JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY) == null) {
      JFaceResources.getImageRegistry().put(UNCHECK_KEY, makeShot(viewer.getControl().getShell(), false));
      JFaceResources.getImageRegistry().put(CHECKED_KEY, makeShot(viewer.getControl().getShell(), true));
    }
  }

  private Image makeShot(Shell shell, boolean type) {
    Shell s = new Shell(shell,SWT.NO_TRIM);
    Button b = new Button(s,SWT.CHECK);
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

	public Image getCheckBoxImage(boolean checkState) {
    if(checkState) {
      return JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY).createImage();
    } else {
      return JFaceResources.getImageRegistry().getDescriptor(UNCHECK_KEY).createImage();
    }
  }
}