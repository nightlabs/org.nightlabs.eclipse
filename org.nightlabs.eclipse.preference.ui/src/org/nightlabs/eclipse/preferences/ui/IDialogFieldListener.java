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

/**
 * Change listener used by <code>DialogField</code>.
 * <p>
 * This class was originally taken from the Eclipse JDT project. 
 * </p>
 * @author unascribed
 * @version $Revision: 1734 $ - $Date: 2008-01-08 17:02:20 +0100 (Di, 08 Jan 2008) $
 */
public interface IDialogFieldListener {
	
	/**
	 * The dialog field has changed.
	 * @param field the dialog field that has changed
	 */
	void dialogFieldChanged(DialogField field);

}
