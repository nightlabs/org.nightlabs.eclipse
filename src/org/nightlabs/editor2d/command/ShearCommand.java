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

import java.awt.geom.AffineTransform;

import org.eclipse.draw2d.PositionConstants;


public class ShearCommand 
extends AbstractTransformCommand
{

  public ShearCommand() 
  {
    super();
  }

  protected double shear;  
  public double getShear() {
    return shear;
  }
  public void setShear(double shear) {
    this.shear = shear;
  }
  
  protected int direction;  
  public int getDirection() {
    return direction;
  }
  public void setDirection(int direction) {
    this.direction = direction;
  }
    
  protected AffineTransform calcAffineTransform() 
  {
    AffineTransform at = new AffineTransform();
    switch (direction) 
    {    	
    	case(PositionConstants.EAST):
    	case(PositionConstants.WEST):    	  
    		at.shear(shear, 1.0d);
    		break;
    	case(PositionConstants.NORTH):
    	case(PositionConstants.SOUTH):
    		at.shear(1.0d, shear);
    		setAffineTransform(at);
    		break;    		
    }
		return at;
  }
  
  public void execute() 
  {
    setAffineTransform(calcAffineTransform());
    super.execute();
  }
  
}
