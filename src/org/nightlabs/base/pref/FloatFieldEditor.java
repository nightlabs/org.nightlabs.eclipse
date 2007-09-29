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

package org.nightlabs.base.pref;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.resource.Messages;

public class FloatFieldEditor 
extends StringFieldEditor 
{
  private float minValidValue = 0;
  private float maxValidValue = Float.MAX_VALUE;
  private static final int DEFAULT_TEXT_LIMIT = 10;
  
  /**
   * Creates a new float field editor 
   */
  protected FloatFieldEditor() {
  }

  /**
   * Creates a float field editor.
   * 
   * @param name the name of the preference this field editor works on
   * @param labelText the label text of the field editor
   * @param parent the parent of the field editor's control
   */
  public FloatFieldEditor(String name, String labelText, Composite parent) {
      this(name, labelText, parent, DEFAULT_TEXT_LIMIT);
  }

  /**
   * Creates a float field editor.
   * 
   * @param name the name of the preference this field editor works on
   * @param labelText the label text of the field editor
   * @param parent the parent of the field editor's control
   * @param textLimit the maximum number of characters in the text.
   */
  public FloatFieldEditor(String name, String labelText, Composite parent,
          int textLimit) {
      init(name, labelText);
      setTextLimit(textLimit);
      setEmptyStringAllowed(false);
      setErrorMessage(Messages.getString("org.nightlabs.base.pref.FloatFieldEditor.errorMessage")); //$NON-NLS-1$
      createControl(parent);
  }

  /**
   * Sets the range of valid values for this field.
   * 
   * @param min the minimum allowed value (inclusive)
   * @param max the maximum allowed value (inclusive)
   */
  public void setValidRange(float min, float max) {
      minValidValue = min;
      maxValidValue = max;
  }

  /* (non-Javadoc)
   * Method declared on StringFieldEditor.
   * Checks whether the entered String is a valid integer or not.
   */
  protected boolean checkState() {

      Text text = getTextControl();

      if (text == null)
          return false;

      String numberString = text.getText();
      try {
          float number = Float.valueOf(numberString).intValue();
          if (number >= minValidValue && number <= maxValidValue) {
			clearErrorMessage();
			return true;
		}
          
		showErrorMessage();
		return false;
		
      } catch (NumberFormatException e1) {
          showErrorMessage();
      }

      return false;
  }

  /* (non-Javadoc)
   * Method declared on FieldEditor.
   */
  protected void doLoad() {
      Text text = getTextControl();
      if (text != null) {
          float value = getPreferenceStore().getFloat(getPreferenceName());
          text.setText("" + value);//$NON-NLS-1$
      }

  }

  /* (non-Javadoc)
   * Method declared on FieldEditor.
   */
  protected void doLoadDefault() {
      Text text = getTextControl();
      if (text != null) {
          float value = getPreferenceStore().getDefaultFloat(getPreferenceName());
          text.setText("" + value);//$NON-NLS-1$
      }
      valueChanged();
  }

  /* (non-Javadoc)
   * Method declared on FieldEditor.
   */
  protected void doStore() {
      Text text = getTextControl();
      if (text != null) {
          Float f = new Float(text.getText());
          getPreferenceStore().setValue(getPreferenceName(), f.floatValue());
      }
  }

  /**
   * Returns this field editor's current value as float.
   *
   * @return the value
   * @exception NumberFormatException if the <code>String</code> does not
   *   contain a parsable float
   */
  public double getFloatValue() throws NumberFormatException {
      return new Float(getStringValue()).floatValue();
  }  
}
