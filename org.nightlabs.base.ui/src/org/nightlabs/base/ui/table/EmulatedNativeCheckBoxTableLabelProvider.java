package org.nightlabs.base.ui.table;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;
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
  private static final String CHECKED_KEY = "CHECKED-CHECKBOX"; //$NON-NLS-1$
  private static final String UNCHECK_KEY = "UNCHECKED-CHECKBOX"; //$NON-NLS-1$

  public EmulatedNativeCheckBoxTableLabelProvider(TableViewer viewer) {
    if (JFaceResources.getImageRegistry().getDescriptor(UNCHECK_KEY) == null) {
      JFaceResources.getImageRegistry().put(UNCHECK_KEY, JFaceUtil.getCheckBoxImage(viewer, false));
      JFaceResources.getImageRegistry().put(CHECKED_KEY, JFaceUtil.getCheckBoxImage(viewer, true));
    }
  }

	public Image getCheckBoxImage(boolean checkState) {
    if(checkState) {
      return JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY).createImage();
    } else {
      return JFaceResources.getImageRegistry().getDescriptor(UNCHECK_KEY).createImage();
    }
  }
}