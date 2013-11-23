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

import org.eclipse.core.runtime.IStatus;

/**
 * Status change listener.
 * <p>
 * This class was originally taken from the Eclipse JDT project. 
 * </p>
 * @author unascribed
 * @version $Revision: 1734 $ - $Date: 2008-01-08 17:02:20 +0100 (Di, 08 Jan 2008) $
 */
public interface IStatusChangeListener {
	
	/**
	 * Notifies this listener that the given status has changed.
	 * 
	 * @param	status	the new status
	 */
	void statusChanged(IStatus status);
}
