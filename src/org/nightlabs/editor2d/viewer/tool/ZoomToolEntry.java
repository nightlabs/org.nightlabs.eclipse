/**
 * 
 */
package org.nightlabs.editor2d.viewer.tool;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.editor2d.viewer.ViewerPlugin;
import org.nightlabs.editor2d.viewer.resource.Messages;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class ZoomToolEntry 
extends AbstractToolEntry 
{
	public ZoomToolEntry() 
	{
		super();
		setName(Messages.getString("org.nightlabs.editor2d.viewer.tool.ZoomToolEntry.name")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.viewer.tool.ZoomToolEntry.tooltip")); //$NON-NLS-1$
		setImage(SharedImages.getSharedImage(ViewerPlugin.getDefault(), ZoomToolEntry.class));
		setTool(new ZoomTool());		
	}
}
