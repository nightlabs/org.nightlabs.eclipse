/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.nightlabs.eclipse.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Layout utility methods.
 * <p>
 * This class was originally taken from the Eclipse JDT project. 
 * </p>
 * @author unascribed
 * @version $Revision: 1734 $ - $Date: 2008-01-08 17:02:20 +0100 (Di, 08 Jan 2008) $
 */
public class LayoutUtil {
	
	/**
	 * Calculates the number of columns needed by field editors.
	 * @param editors the field editors
	 * @return The number of columns needed
	 */
	public static int getNumberOfColumns(DialogField[] editors) {
		int nCulumns= 0;
		for (int i= 0; i < editors.length; i++) {
			nCulumns= Math.max(editors[i].getNumberOfControls(), nCulumns);
		}
		return nCulumns;
	}
	
	/**
	 * Creates a composite and fills in the given editors.
	 * @param parent the parent composite
	 * @param editors the field editors
	 * @param labelOnTop Defines if the label of all fields should be on top of the fields
	 */	
	public static void doDefaultLayout(Composite parent, DialogField[] editors, boolean labelOnTop) {
		doDefaultLayout(parent, editors, labelOnTop, 0, 0);
	}

	/**
	 * Creates a composite and fills in the given editors.
	 * @param parent the parent composite
	 * @param editors the field editors
	 * @param labelOnTop Defines if the label of all fields should be on top of the fields
	 * @param marginWidth The margin width to be used by the composite
	 * @param marginHeight The margin height to be used by the composite
	 */	
	public static void doDefaultLayout(Composite parent, DialogField[] editors, boolean labelOnTop, int marginWidth, int marginHeight) {
		int nCulumns= getNumberOfColumns(editors);
		Control[][] controls= new Control[editors.length][];
		for (int i= 0; i < editors.length; i++) {
			controls[i]= editors[i].doFillIntoGrid(parent, nCulumns);
		}
		if (labelOnTop) {
			nCulumns--;
			modifyLabelSpans(controls, nCulumns);
		}
		GridLayout layout= null;
		if (parent.getLayout() instanceof GridLayout) {
			layout= (GridLayout) parent.getLayout();
		} else {
			layout= new GridLayout();
		}
		if (marginWidth != SWT.DEFAULT) {
			layout.marginWidth= marginWidth;
		}
		if (marginHeight != SWT.DEFAULT) {
			layout.marginHeight= marginHeight;
		}
		layout.numColumns= nCulumns;		
		parent.setLayout(layout);
	}
	
	private static void modifyLabelSpans(Control[][] controls, int nCulumns) {
		for (int i= 0; i < controls.length; i++) {
			setHorizontalSpan(controls[i][0], nCulumns);
		}
	}
	
	/**
	 * Sets the span of a control. Assumes that GridData is used.
	 * @param control the control
	 * @param span the horizontal span
	 */
	public static void setHorizontalSpan(Control control, int span) {
		Object ld= control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData)ld).horizontalSpan= span;
		} else if (span != 1) {
			GridData gd= new GridData();
			gd.horizontalSpan= span;
			control.setLayoutData(gd);
		}
	}	

	/**
	 * Sets the width hint of a control. Assumes that GridData is used.
	 * @param control the control
	 * @param widthHint the width hint
	 */
	public static void setWidthHint(Control control, int widthHint) {
		Object ld= control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData)ld).widthHint= widthHint;
		}
	}
	
	/**
	 * Sets the heightHint hint of a control. Assumes that GridData is used.
	 * @param control the control
	 * @param heightHint the height hint
	 */
	public static void setHeightHint(Control control, int heightHint) {
		Object ld= control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData)ld).heightHint= heightHint;
		}
	}	
	
	/**
	 * Sets the horizontal indent of a control. Assumes that GridData is used.
	 * @param control the control
	 * @param horizontalIndent the horizontal indent
	 */
	public static void setHorizontalIndent(Control control, int horizontalIndent) {
		Object ld= control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData)ld).horizontalIndent= horizontalIndent;
		}
	}
	
	/**
	 * Sets the horizontal grabbing of a control to true. Assumes that GridData is used.
	 * @param control the control
	 */
	public static void setHorizontalGrabbing(Control control) {
		Object ld= control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData)ld).grabExcessHorizontalSpace= true;
		}
	}		

}
