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

package org.nightlabs.editor2d.ui.rulers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.rulers.RulerChangeListener;
import org.eclipse.gef.rulers.RulerProvider;
import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.EditorRuler;
import org.nightlabs.editor2d.ui.command.CreateGuideCommand;
import org.nightlabs.editor2d.ui.command.DeleteGuideCommand;
import org.nightlabs.editor2d.ui.command.MoveGuideCommand;


public class EditorRulerProvider 
extends RulerProvider 
{
  private EditorRuler ruler;
  private PropertyChangeListener rulerListener = new PropertyChangeListener() {
  	public void propertyChange(PropertyChangeEvent evt) {
  		if (evt.getPropertyName().equals(EditorRuler.PROPERTY_CHILDREN)) {
  			EditorGuide guide = (EditorGuide)evt.getNewValue();
  			if (getGuides().contains(guide)) {
  				guide.addPropertyChangeListener(guideListener);
  			} else {
  				guide.removePropertyChangeListener(guideListener);
  			}
  			for (int i = 0; i < listeners.size(); i++) {
  				((RulerChangeListener)listeners.get(i))
  						.notifyGuideReparented(guide);
  			}
  		} else {
  			for (int i = 0; i < listeners.size(); i++) {
  				((RulerChangeListener)listeners.get(i))
  						.notifyUnitsChanged(ruler.getUnit());
  			}
  		}
  	}
  };
  private PropertyChangeListener guideListener = new PropertyChangeListener() {
  	public void propertyChange(PropertyChangeEvent evt) {
  		if (evt.getPropertyName().equals(EditorGuide.PROPERTY_CHILDREN)) {
  			for (int i = 0; i < listeners.size(); i++) {
  				((RulerChangeListener)listeners.get(i))
  						.notifyPartAttachmentChanged(evt.getNewValue(), evt.getSource());
  			}
  		} else {
  			for (int i = 0; i < listeners.size(); i++) {
  				((RulerChangeListener)listeners.get(i))
  						.notifyGuideMoved(evt.getSource());
  			}
  		}
  	}
  };

  public EditorRulerProvider(EditorRuler ruler) {
  	this.ruler = ruler;
  	this.ruler.addPropertyChangeListener(rulerListener);
  	List guides = getGuides();
  	for (int i = 0; i < guides.size(); i++) {
  		((EditorGuide)guides.get(i)).addPropertyChangeListener(guideListener);
  	}
  }

  @Override
	public List getAttachedModelObjects(Object guide) {
  	return new ArrayList(((EditorGuide)guide).getParts());
  }

  @Override
	public Command getCreateGuideCommand(int position) {
  	return new CreateGuideCommand(ruler, position);
  }

  @Override
	public Command getDeleteGuideCommand(Object guide) {
  	return new DeleteGuideCommand((EditorGuide)guide, ruler);
  }

  @Override
	public Command getMoveGuideCommand(Object guide, int pDelta) {
  	return new MoveGuideCommand((EditorGuide)guide, pDelta);
  }

  @Override
	public int[] getGuidePositions() {
  	List guides = getGuides();
  	int[] result = new int[guides.size()];
  	for (int i = 0; i < guides.size(); i++) {
  		result[i] = ((EditorGuide)guides.get(i)).getPosition();
  	}
  	return result;
  }

  @Override
	public Object getRuler() {
  	return ruler;
  }

  @Override
	public int getUnit() {
  	return ruler.getUnit();
  }

  @Override
	public void setUnit(int newUnit) {
  	ruler.setUnit(newUnit);
  }

  @Override
	public int getGuidePosition(Object guide) {
  	return ((EditorGuide)guide).getPosition();
  }

  @Override
	public List getGuides() {
  	return ruler.getGuides();
  }
  
}
