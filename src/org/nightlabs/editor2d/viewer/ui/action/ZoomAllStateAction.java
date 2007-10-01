package org.nightlabs.editor2d.viewer.ui.action;

import org.eclipse.swt.SWT;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.editor2d.viewer.ui.IZoomSupport;
import org.nightlabs.editor2d.viewer.ui.ViewerPlugin;
import org.nightlabs.editor2d.viewer.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ZoomAllStateAction 
extends ZoomAction 
{
	public static final String ID = ZoomAllStateAction.class.getName();
	
	/**
	 * @param zoomSupport
	 */
	public ZoomAllStateAction(IZoomSupport zoomSupport) {
		super(zoomSupport, Messages.getString("org.nightlabs.editor2d.viewer.ui.action.ZoomAllStateAction.name"), SWT.TOGGLE); //$NON-NLS-1$
		setChecked(zoomSupport.isZoomAll());
	}

	@Override
	public void init() 
	{
		setId(ID);
		setText(Messages.getString("org.nightlabs.editor2d.viewer.ui.action.ZoomAllStateAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.viewer.ui.action.ZoomAllStateAction.tooltip")); //$NON-NLS-1$
		setImageDescriptor(SharedImages.getSharedImageDescriptor(ViewerPlugin.getDefault(), ZoomAllAction.class));				
	}

	public void zoomChanged(double zoom) {
		
	}

	public void run() {
		getZoomSupport().setZoomAll(!getZoomSupport().isZoomAll());
	}
}
