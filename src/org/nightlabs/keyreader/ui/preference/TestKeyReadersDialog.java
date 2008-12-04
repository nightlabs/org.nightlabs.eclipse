package org.nightlabs.keyreader.ui.preference;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.keyreader.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.util.Util;

public class TestKeyReadersDialog extends ResizableTrayDialog {

	public TestKeyReadersDialog(Shell shell)
	{
		super(shell, Messages.RESOURCE_BUNDLE);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	private SashForm sashForm;
	private TestKeyReadersTable testKeyReadersTable;
	private Text keyReaderErrorText;

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new FillLayout());

		sashForm = new SashForm(area, SWT.VERTICAL);

		testKeyReadersTable = new TestKeyReadersTable(sashForm, SWT.NONE);
		testKeyReadersTable.getGridData().grabExcessHorizontalSpace = true;
		testKeyReadersTable.getGridData().grabExcessVerticalSpace = true;

		testKeyReadersTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				showCurrentError();
			}
		});

		keyReaderErrorText = new Text(sashForm, SWT.MULTI | SWT.READ_ONLY);

		testKeyReadersTable.addPropertyChangeListener(TestKeyReadersTable.PROPERTY_REFRESH, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				showCurrentError();
			}
		});

		sashForm.setWeights(new int[] {50, 50});

		return area;
	}

	private void showCurrentError() {
		if (testKeyReadersTable.isDisposed())
			return;

		Collection<TestKeyReaderItem> sel = testKeyReadersTable.getSelectedElements();
		if (sel.isEmpty()) {
			keyReaderErrorText.setText("");
			return;
		}

		TestKeyReaderItem testKeyReaderItem = sel.iterator().next();
		if (testKeyReaderItem.getLastKeyReaderErrorEvent() == null) {
			keyReaderErrorText.setText("");
			return;
		}

		keyReaderErrorText.setText(
				DateFormatter.formatDateShortTimeHMS(
						testKeyReaderItem.getLastKeyReaderErrorEvent().getTimestamp(),
						false
				) + '\n' +
				Util.getStackTraceAsString(testKeyReaderItem.getLastKeyReaderErrorEvent().getError())
		);
	}
	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		if (id == OK)
			return super.createButton(parent, id, label, defaultButton);
		else
			return null;
	}
}
