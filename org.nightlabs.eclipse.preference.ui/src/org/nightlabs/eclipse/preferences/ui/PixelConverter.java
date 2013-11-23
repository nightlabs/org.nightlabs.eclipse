/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.nightlabs.eclipse.preferences.ui;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.dialogs.Dialog;

/**
 * A Pixel size converter.
 * <p>
 * This class was originally taken from the Eclipse JDT project. 
 * </p>
 * @author unascribed
 * @version $Revision: 1734 $ - $Date: 2008-01-08 17:02:20 +0100 (Di, 08 Jan 2008) $
 */
public class PixelConverter {
	
	private final FontMetrics fFontMetrics;
	
	/**
	 * Create a new PixelConverter.
	 * @param control The control to get the font from
	 */
	public PixelConverter(Control control) {
		this(control.getFont());
	}
	
	/**
	 * Create a new PixelConverter.
	 * @param font The font to use for ceonversions
	 */
	public PixelConverter(Font font) {
		GC gc = new GC(font.getDevice());
		gc.setFont(font);
		fFontMetrics= gc.getFontMetrics();
		gc.dispose();
	}
	
  /**
   * Returns the number of pixels corresponding to the height of the given
   * number of characters.
   * <p>
   * This method may only be called after <code>initializeDialogUnits</code>
   * has been called.
   * </p>
   * <p>
   * Clients may call this framework method, but should not override it.
   * </p>
   * 
   * @param chars
   *            the number of characters
   * @return the number of pixels
   */
	public int convertHeightInCharsToPixels(int chars) {
		return Dialog.convertHeightInCharsToPixels(fFontMetrics, chars);
	}

  /**
   * Returns the number of pixels corresponding to the given number of
   * horizontal dialog units.
   * <p>
   * This method may only be called after <code>initializeDialogUnits</code>
   * has been called.
   * </p>
   * <p>
   * Clients may call this framework method, but should not override it.
   * </p>
   * 
   * @param dlus
   *            the number of horizontal dialog units
   * @return the number of pixels
   */
	public int convertHorizontalDLUsToPixels(int dlus) {
		return Dialog.convertHorizontalDLUsToPixels(fFontMetrics, dlus);
	}

  /**
   * Returns the number of pixels corresponding to the given number of
   * vertical dialog units.
   * <p>
   * This method may only be called after <code>initializeDialogUnits</code>
   * has been called.
   * </p>
   * <p>
   * Clients may call this framework method, but should not override it.
   * </p>
   * 
   * @param dlus
   *            the number of vertical dialog units
   * @return the number of pixels
   */
	public int convertVerticalDLUsToPixels(int dlus) {
		return Dialog.convertVerticalDLUsToPixels(fFontMetrics, dlus);
	}
	
  /**
   * Returns the number of pixels corresponding to the width of the given
   * number of characters.
   * <p>
   * This method may only be called after <code>initializeDialogUnits</code>
   * has been called.
   * </p>
   * <p>
   * Clients may call this framework method, but should not override it.
   * </p>
   * 
   * @param chars
   *            the number of characters
   * @return the number of pixels
   */
	public int convertWidthInCharsToPixels(int chars) {
		return Dialog.convertWidthInCharsToPixels(fFontMetrics, chars);
	}	
}
