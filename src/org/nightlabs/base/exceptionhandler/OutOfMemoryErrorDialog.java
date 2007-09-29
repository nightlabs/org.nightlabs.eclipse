/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.exceptionhandler;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.base.resource.Messages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class OutOfMemoryErrorDialog extends DefaultErrorDialog {
	
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(OutOfMemoryErrorDialog.class);
	
	protected static final int RESTART_WORKBENCH_ID = DefaultErrorDialog.SEND_ERROR_REPORT_ID + 10;
	
	private Button restartWorkbenchButton;
	
	protected void createCustomButtons(Composite parent) {
		restartWorkbenchButton = createButton(parent, RESTART_WORKBENCH_ID, Messages.getString("org.nightlabs.base.exceptionhandler.OutOfMemoryErrorDialog.restartWorkbenchButton.text"), false); //$NON-NLS-1$
		// TODO:
//		super.createCustomButtons(parent);
	}
	
	protected void buttonPressed(int id) {
		if (id == RESTART_WORKBENCH_ID) {
			restartWorkbenchButton.getVisible(); // to avoid warning
			try {
				logger.info("Trying to restart the workbench due to OutOfMemoryError"); //$NON-NLS-1$
				if (!PlatformUI.getWorkbench().restart()) {
					System.exit(1);
				}
				logger.info("Close successful, restarting"); //$NON-NLS-1$
			} catch (Throwable t) {
				System.exit(1);
			}
		}
		else			
			super.buttonPressed(id);
	}
	
}
