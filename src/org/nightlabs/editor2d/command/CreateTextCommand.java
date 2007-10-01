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

package org.nightlabs.editor2d.command;

import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.request.TextCreateRequest;
import org.nightlabs.editor2d.resource.Messages;

public class CreateTextCommand  
extends AbstractCreateTextCommand
{  
  protected TextCreateRequest request;  
  public CreateTextCommand(TextCreateRequest request) 
  {
    super(request);  
    setLabel(Messages.getString("org.nightlabs.editor2d.command.CreateTextCommand.label")); //$NON-NLS-1$
    this.request = request; 
  }
  
  protected TextDrawComponent getTextDrawComponent() {
  	return (TextDrawComponent) getChild();
  }

	@Override
	public TextDrawComponent createTextDrawComponent(TextCreateRequest request, int x, int y) 
	{
		return getFactory().createTextDrawComponent(
				request.getText(), request.getFontName(), request.getFontSize(), 
				request.getFontStyle(), x, y, parent);		 
	}
}
