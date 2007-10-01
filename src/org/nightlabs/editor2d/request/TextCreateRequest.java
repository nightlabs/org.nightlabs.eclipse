/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.request;


public class TextCreateRequest 
extends EditorCreateShapeRequest 
{  
  public TextCreateRequest() {
    super();
  }
  
  protected String fontName;
  public String getFontName() {
    return fontName;
  }  
  public void setFontName(String fontName) {
    this.fontName = fontName;
  }
  
  protected String text;  
  public String getText() {
    return text;
  }  
  public void setText(String text) {
    this.text = text;
  }
  
  protected int fontSize;  
  public int getFontSize() {
    return fontSize;
  }  
  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }
  
  protected int fontStyle;  
  public int getFontStyle() {
    return fontStyle;
  }  
  public void setFontStyle(int fontStyle) {
    this.fontStyle = fontStyle;
  }
    
}
