/**
 *
 */
package org.nightlabs.base.ui.print;

import javax.print.PrintService;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.print.PrintUtil;

/**
 * {@link Combo} showing all {@link PrintService}s that can be found in the system.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class PrintServiceCombo extends XComboComposite<PrintService> {

	private static class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object arg0, int arg1) {
			return ((PrintService)arg0).getName();
		}
	}

	/**
	 * Create a new {@link PrintServiceCombo}.
	 * @param parent The parent for the new combo.
	 * @param comboStyle The to use for the combo.
	 */
	public PrintServiceCombo(Composite parent, int comboStyle) {
		super(parent, comboStyle, (String) null, new LabelProvider());
		setInput( PrintUtil.lookupPrintServices() );
	}

}
