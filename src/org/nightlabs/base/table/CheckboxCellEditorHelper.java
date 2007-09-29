package org.nightlabs.base.table;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.resource.SharedImages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class CheckboxCellEditorHelper {

	public CheckboxCellEditorHelper() {
		super();
	}

	/**
	 * To be used by LabelProviders that whant to display a checked/unchecked icon for
	 * the CheckboxCellEditor that does not have a Control.
	 * 
	 * @param cellModifier The ICellModifier for the CellEditor to provide the value
	 * @param element The current element
	 * @param property The property the cellModifier should return the value from 
	 */
	public static Image getCellEditorImage(ICellModifier cellModifier, Object element, String property) {
		Boolean value = (Boolean)cellModifier.getValue(element, property);
		return getCellEditorImage(value, false);
	}

	/**
	 * returns an checked checkbox image if value if true and an unchecked checkbox image if false
	 * 
	 * @param value the value to get the cooresponding image for
	 * @param disabled determines if the image should be disabled or not
	 * @return an checked checkbox image if value if true and an unchecked checkbox image if false
	 * 
	 */
	public static Image getCellEditorImage(boolean value, boolean disabled) 
	{
		Image image = null;
		if (value)
			image = SharedImages.getSharedImage(NLBasePlugin.getDefault(), CheckboxCellEditorHelper.class, "checked"); //$NON-NLS-1$
		else
			image = SharedImages.getSharedImage(NLBasePlugin.getDefault(), CheckboxCellEditorHelper.class, "unchecked"); //$NON-NLS-1$
		
		if (disabled)
			image = new Image(Display.getDefault(), image, SWT.IMAGE_DISABLE);
		
		return image;
	}
}
