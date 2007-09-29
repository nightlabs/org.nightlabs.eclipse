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

package org.nightlabs.base.print.pref;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.XComboComposite;
import org.nightlabs.base.print.PrinterConfigurationRegistry;
import org.nightlabs.base.print.PrinterUseCase;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrinterUseCaseCombo extends XComboComposite<PrinterUseCase> {

	/**
	 * @param types
	 * @param parent
	 * @param comboStyle
	 */
	public PrinterUseCaseCombo(Composite parent, int comboStyle) {
		super(parent, comboStyle, (String) null,
				new LabelProvider() {
					/* (non-Javadoc)
					 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
					 */
					@Override
					public String getText(Object element) {
						if (element instanceof PrinterUseCase) {
							PrinterUseCase useCase = (PrinterUseCase)element;
							return useCase.getName()+": "+useCase.getDescription(); //$NON-NLS-1$
						}
						else
							return super.getText(element);
					}
				}
			);

		setInput( PrinterConfigurationRegistry.sharedInstance().getPrinterUseCases() );
	}

}
