/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.tool;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.editor2d.viewer.ui.ViewerPlugin;
import org.nightlabs.editor2d.viewer.ui.resource.Messages;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class MarqueeToolEntry
extends AbstractToolEntry
{
	public MarqueeToolEntry()
	{
		super();
		setName(Messages.getString("org.nightlabs.editor2d.viewer.ui.tool.MarqueeToolEntry.name")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.viewer.ui.tool.MarqueeToolEntry.tooltip"));		 //$NON-NLS-1$
		setImage(SharedImages.getSharedImage(ViewerPlugin.getDefault(), MarqueeToolEntry.class));
		setTool(new MarqueeTool());
	}
}
