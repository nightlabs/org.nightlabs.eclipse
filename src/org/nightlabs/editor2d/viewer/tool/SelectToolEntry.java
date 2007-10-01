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
public class SelectToolEntry 
extends AbstractToolEntry 
{
	public SelectToolEntry() 
	{
		super();
		setName(Messages.getString("org.nightlabs.editor2d.viewer.tool.SelectToolEntry.name.select")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.viewer.tool.SelectToolEntry.tooltip.select"));		 //$NON-NLS-1$
		setImage(SharedImages.getSharedImage(ViewerPlugin.getDefault(), SelectToolEntry.class));
		setTool(new SelectTool());
	}
}
