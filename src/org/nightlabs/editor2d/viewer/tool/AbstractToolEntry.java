/**
 * 
 */
package org.nightlabs.editor2d.viewer.tool;

import org.eclipse.swt.graphics.Image;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public abstract class AbstractToolEntry 
implements IToolEntry 
{

	public AbstractToolEntry() {
		super();
	}

	protected ITool tool;
	
	/**
	 * @see org.nightlabs.editor2d.viewer.tool.IToolEntry#getTool()
	 */
	public ITool getTool() {
		return tool;
	}

	/**
	 * @see org.nightlabs.editor2d.viewer.tool.IToolEntry#setTool(org.nightlabs.editor2d.viewer.tool.ITool)
	 */
	public void setTool(ITool tool) {
		this.tool = tool;
	}

	protected String name = null;
	
	/**
	 * @see org.nightlabs.editor2d.viewer.tool.IToolEntry#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see org.nightlabs.editor2d.viewer.tool.IToolEntry#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	protected Image image = null;
	
	/**
	 * @see org.nightlabs.editor2d.viewer.tool.IToolEntry#getImage()
	 */
	public Image getImage() {
		return image;
	}
	/**
	 * @see org.nightlabs.editor2d.viewer.tool.IToolEntry#setImage(Image)
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	protected String toolTipText = null;
	
	/**
	 * @see org.nightlabs.editor2d.viewer.tool.IToolEntry#getToolTipText()
	 */	
	public String getToolTipText() {
		return toolTipText;
	}
	
	/**
	 * @see org.nightlabs.editor2d.viewer.tool.IToolEntry#setToolTipText(String)
	 */	
	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
	}
	
//	protected BufferedImage image = null;
//	/**
//	 * @see org.nightlabs.editor2d.viewer.tool.IToolEntry#getImage()
//	 */
//	public BufferedImage getImage() {
//		return image;
//	}
//
//	/**
//	 * @see org.nightlabs.editor2d.viewer.tool.IToolEntry#setImage(java.awt.image.BufferedImage)
//	 */
//	public void setImage(BufferedImage image) {
//		this.image = image;
//	}

}
