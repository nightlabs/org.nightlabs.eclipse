/* *****************************************************************************
 *  NightLabs JDO Eclipse Extensions                                           *
 *  Copyright (c) 2006 NightLabs, Germany                                      *
 *                                                                             *
 *  All rights reserved. This program and the accompanying materials           *
 *  are made available under the terms of the Eclipse Public License v1.0      *
 *  which accompanies this distribution, and is available at                   *
 *  http://www.eclipse.org/legal/epl-v10.html                                  *
 *                                                                             *
 *  Contributors:                                                              *
 *   Alexander Bieber, NightLabs - initial API and implementation              *
 ******************************************************************************/

package org.nightlabs.jseditor.ui.editor;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

/**
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class JSEditorWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return isWhitespaceChar(c);
	}
	
	public static boolean isWhitespaceChar(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
