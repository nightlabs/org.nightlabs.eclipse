package org.nightlabs.base.ui.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.DoubleSpinnerComposite;

public class DoubleSpinnerCellEditor extends CellEditor {

	private double min;
	private double max;
	private double step;
	private int precision;

	public DoubleSpinnerCellEditor(Composite parent, double min, double max, double step, int precision) {
		setStyle(SWT.NONE);
		this.min = min;
		this.max = max;
		this.step = step;
		this.precision = precision;
		create(parent);
	}

	@Override
	protected Control createControl(Composite parent) {
		DoubleSpinnerComposite spinner = new DoubleSpinnerComposite(parent, SWT.NONE, SWT.NONE, precision, min, max, step);
		spinner.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					fireCancelEditor();

				} else if (e.detail == SWT.TRAVERSE_RETURN) {
					fireApplyEditorValue();
				}
				e.doit = false;
			}
		});
		return spinner;
	}

	@Override
	protected Object doGetValue() {
		return getSpinner().getValue();
	}

	@Override
	protected void doSetValue(Object value) {
		if (value instanceof Integer)
			getSpinner().setValue((Integer) value);
		if (value instanceof Float)
			getSpinner().setValue((Float) value);
		if (value instanceof Double)
			getSpinner().setValue((Double) value);
		if (value instanceof String) {
			try {
				Double value2 = new Double((String) value);
				getSpinner().setValue(value2);
			} catch (Exception e) {
				getSpinner().setValue(Double.NaN);
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doSetFocus() {
		getControl().setFocus();
	}

	public DoubleSpinnerComposite getSpinner() {
		return (DoubleSpinnerComposite) getControl();
	}
}
