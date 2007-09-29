/**
 * 
 */
package org.nightlabs.base.print;

import javax.print.PrintService;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.XComboComposite;
import org.nightlabs.base.table.TableLabelProvider;
import org.nightlabs.print.PrintUtil;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class PrintServiceCombo extends XComboComposite<PrintService> {

	private static class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object arg0, int arg1) {
			return ((PrintService)arg0).getName();
		}
	}
	
	/**
	 * @param types
	 * @param parent
	 * @param style
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public PrintServiceCombo(Composite parent, int comboStyle) {
		super(parent, comboStyle, (String) null, new LabelProvider());
		setInput( PrintUtil.lookupPrintServices() );
	}	
	
}
