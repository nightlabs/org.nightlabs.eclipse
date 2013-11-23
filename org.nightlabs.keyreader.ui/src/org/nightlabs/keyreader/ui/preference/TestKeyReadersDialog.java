package org.nightlabs.keyreader.ui.preference;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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

	@Override
	public void create() {
		super.create();
		getShell().setText("Test key readers");
	}

	private SashForm sashForm;
	private TestKeyReadersTable testKeyReadersTable;
	private Text keyReaderErrorText;

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new GridLayout());

		Label msg = new Label(area, SWT.WRAP);
		msg.setText(
				"Use your key readers to scan some codes. The last key that has been read is shown for each reader as well as the last error.\n" +
				"\n" +
				"You can select a key reader to see details: In case of an error, the complete error message (with stack trace) is shown in the detail area below."
		);
		msg.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		sashForm = new SashForm(area, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		testKeyReadersTable = new TestKeyReadersTable(sashForm, SWT.NONE);
		testKeyReadersTable.getGridData().grabExcessHorizontalSpace = true;
		testKeyReadersTable.getGridData().grabExcessVerticalSpace = true;

		testKeyReadersTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				showCurrentDetails();
			}
		});

		keyReaderErrorText = new Text(sashForm, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

		testKeyReadersTable.addPropertyChangeListener(TestKeyReadersTable.PROPERTY_REFRESH, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				showCurrentDetails();
			}
		});

		sashForm.setWeights(new int[] {50, 50});

		return area;
	}

	private void showCurrentDetails() {
		if (testKeyReadersTable.isDisposed())
			return;

		Collection<TestKeyReaderItem> sel = testKeyReadersTable.getSelectedElements();
		if (sel.isEmpty()) {
			keyReaderErrorText.setText("");
			return;
		}

		StringBuilder sb = new StringBuilder();

		TestKeyReaderItem testKeyReaderItem = sel.iterator().next();

		sb.append("*** Use case ***");
		sb.append("\nUse case name: ");
		sb.append(testKeyReaderItem.getKeyReaderUseCase().getName());
		sb.append("\nKey reader ID: ");
		sb.append(testKeyReaderItem.getKeyReaderUseCase().getKeyReaderID());

		if (testKeyReaderItem.getLastKeyReadEvent() != null) {
			sb.append("\n*** Last key ***\n");
			sb.append(
					DateFormatter.formatDateShortTimeHMS(
							testKeyReaderItem.getLastKeyReadEvent().getTimestamp(),
							false
					)
			);
			sb.append('\n');
			sb.append(testKeyReaderItem.getLastKeyReadEvent().getKey());
		}

		if (testKeyReaderItem.getLastKeyReaderErrorEvent() != null) {
			sb.append("\n*** Error ***\n");
			sb.append(
					DateFormatter.formatDateShortTimeHMS(
							testKeyReaderItem.getLastKeyReaderErrorEvent().getTimestamp(),
							false
					)
			);
			sb.append('\n');
			sb.append(
					Util.getStackTraceAsString(testKeyReaderItem.getLastKeyReaderErrorEvent().getError())
			);
		}

		keyReaderErrorText.setText(sb.toString());
	}
	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		if (id == OK)
			return super.createButton(parent, id, label, defaultButton);
		else
			return null;
	}
}
