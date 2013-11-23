package org.nightlabs.base.ui.util;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.eclipse.compatibility.RAPCompatibilityPlugin;

/**
 * Adapted from http://tom-eclipse-dev.blogspot.com/2007/01/tableviewers-and-nativelooking.html
 *
 * @author Tom Schindl & Alexander Ljungberg (siehe link)
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public final class JFaceUtil
{
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
	    String resource = 
	    	enabled ?
	    		(checkState ? "icons/checkbox_checked.png" : "icons/checkbox_unchecked.png") : 
	    		(checkState ? "icons/checkbox_checked_disabled.png" : "icons/checkbox_unchecked_disabled.png");
		
		return RAPCompatibilityPlugin.getDefault().getImage(resource);
	}
}
