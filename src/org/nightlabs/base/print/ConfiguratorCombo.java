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

package org.nightlabs.base.print;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.XComboComposite;
import org.nightlabs.base.print.PrinterConfigurationRegistry.ConfiguratorFactoryEntry;
import org.nightlabs.base.table.TableLabelProvider;

/**
 * Combo with a list of all Registered {@link PrinterConfiguratorFactory}.
 * As registrations are held as {@link ConfiguratorFactoryEntry}s this is
 * the actual type of entries in this combo.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ConfiguratorCombo extends XComboComposite<ConfiguratorFactoryEntry> {

	private static class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object arg0, int arg1) {
			return ((ConfiguratorFactoryEntry)arg0).getName();
		}
	}
	
	/**
	 * @param types
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public ConfiguratorCombo(Composite parent, int style) {
		super(parent,	style, (String) null, new LabelProvider());
		setInput(	PrinterConfigurationRegistry.sharedInstance().getPrinterConfiguratorEntries() );
	}

}
