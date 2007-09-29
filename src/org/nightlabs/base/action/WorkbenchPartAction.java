/**
 * 
 */
package org.nightlabs.base.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class WorkbenchPartAction 
extends Action 
implements IWorkbenchPartAction 
{
	private IWorkbenchPart activePart = null;
	
	public WorkbenchPartAction() {
	}

	/**
	 * @param activePart The current active part.
	 */
	public WorkbenchPartAction(IWorkbenchPart activePart) {
		setActivePart(activePart);
	}
	
	/**
	 * @param text
	 */
	public WorkbenchPartAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public WorkbenchPartAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public WorkbenchPartAction(String text, int style) {
		super(text, style);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.action.IWorkbenchPartAction#setActivePart(org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IWorkbenchPart part) {
		this.activePart = part;
	}
	
	public IWorkbenchPart getActivePart() {
		return activePart;
	}

}
