package org.nightlabs.keyreader.ui.preference;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.keyreader.KeyReadEvent;
import org.nightlabs.keyreader.KeyReadListener;
import org.nightlabs.keyreader.KeyReader;
import org.nightlabs.keyreader.KeyReaderErrorEvent;
import org.nightlabs.keyreader.KeyReaderErrorListener;
import org.nightlabs.keyreader.KeyReaderMan;
import org.nightlabs.keyreader.ui.KeyReaderUseCase;
import org.nightlabs.keyreader.ui.KeyReaderUseCaseRegistry;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.ProgressMonitor;

public class TestKeyReadersTable extends AbstractTableComposite<TestKeyReaderItem>
{
	private static class LabelProvider extends TableLabelProvider
	{
		@Override
		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
				case 0:
					return ((TestKeyReaderItem)element).getKeyReaderUseCase().getName();
				case 1:
					return ((TestKeyReaderItem)element).getKeyReaderUseCase().getKeyReaderID();

				case 2: {
					TestKeyReaderItem testKeyReaderItem = (TestKeyReaderItem)element;
					if (testKeyReaderItem.getLastKeyReadEvent() == null)
						return "";
					return testKeyReaderItem.getLastKeyReadEvent().getKey();
				}
				case 3: {
					TestKeyReaderItem testKeyReaderItem = (TestKeyReaderItem)element;
					if (testKeyReaderItem.getLastKeyReadEvent() == null)
						return "";
					return DateFormatter.formatDateShortTimeHMS(
							testKeyReaderItem.getLastKeyReadEvent().getTimestamp(),
							false
					);
				}

				case 4: {
					TestKeyReaderItem testKeyReaderItem = (TestKeyReaderItem)element;
					if (testKeyReaderItem.getLastKeyReaderErrorEvent() == null)
						return "";
					return String.valueOf(testKeyReaderItem.getLastKeyReaderErrorEvent().getError());
				}
				case 5: {
					TestKeyReaderItem testKeyReaderItem = (TestKeyReaderItem)element;
					if (testKeyReaderItem.getLastKeyReaderErrorEvent() == null)
						return "";
					return DateFormatter.formatDateShortTimeHMS(
							testKeyReaderItem.getLastKeyReaderErrorEvent().getTimestamp(),
							false
					);
				}
				default:
					return ""; //$NON-NLS-1$
			}
		}
	}

	private Map<KeyReader, TestKeyReaderItem> keyReader2TestKeyReaderItem = new HashMap<KeyReader, TestKeyReaderItem>();
	private boolean disposed;

	private KeyReadListener keyReadListener = new KeyReadListener() {
		@Override
		public void keyRead(final KeyReadEvent e) {
			KeyReader keyReader = (KeyReader) e.getSource();
			TestKeyReaderItem testKeyReaderItem;
			synchronized (keyReader2TestKeyReaderItem) {
				testKeyReaderItem = keyReader2TestKeyReaderItem.get(keyReader);
			}
			testKeyReaderItem.setLastKeyReadEvent(e);

			if (isDisposed())
				return;

			getDisplay().asyncExec(new Runnable() {
				public void run() {
					refresh(true);
					propertyChangeSupport.firePropertyChange(PROPERTY_REFRESH, null, e);
				}
			});
		}
	};

	private KeyReaderErrorListener keyReaderErrorListener = new KeyReaderErrorListener() {
		@Override
		public void errorOccured(final KeyReaderErrorEvent e) {
			KeyReader keyReader = (KeyReader) e.getSource();
			TestKeyReaderItem testKeyReaderItem;
			synchronized (keyReader2TestKeyReaderItem) {
				testKeyReaderItem = keyReader2TestKeyReaderItem.get(keyReader);
			}
			testKeyReaderItem.setLastKeyReaderErrorEvent(e);

			if (isDisposed())
				return;

			getDisplay().asyncExec(new Runnable() {
				public void run() {
					refresh(true);
					propertyChangeSupport.firePropertyChange(PROPERTY_REFRESH, null, e);
				}
			});
		}
	};

	public TestKeyReadersTable(Composite parent, int style) {
		super(parent, style);

		setLoadingMessage("Loading...");

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				Set<KeyReader> keyReaders;
				synchronized (keyReader2TestKeyReaderItem) {
					keyReaders = new HashSet<KeyReader>(keyReader2TestKeyReaderItem.keySet());
					disposed = true;
				}
				for (KeyReader keyReader : keyReaders) {
					keyReader.removeKeyReadListener(keyReadListener);
					keyReader.removeKeyReaderErrorListener(keyReaderErrorListener);
					keyReader.close(false);
				}
			}
		});

		final Display display = getDisplay();
		Job job = new Job("Start key readers") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				final List<TestKeyReaderItem> testKeyReaderItems = new ArrayList<TestKeyReaderItem>();
				for (KeyReaderUseCase keyReaderUseCase : KeyReaderUseCaseRegistry.sharedInstance().getKeyReaderUseCases()) {
					KeyReader keyReader;
					TestKeyReaderItem testKeyReaderItem;
					synchronized (keyReader2TestKeyReaderItem) {
						if (disposed)
							return Status.CANCEL_STATUS;

						keyReader = KeyReaderMan.sharedInstance().createKeyReader(keyReaderUseCase.getKeyReaderID());
						testKeyReaderItem = new TestKeyReaderItem(
								keyReaderUseCase,
								keyReader
						);
						keyReader2TestKeyReaderItem.put(keyReader, testKeyReaderItem);
						keyReader.addKeyReadListener(keyReadListener);
						keyReader.addKeyReaderErrorListener(keyReaderErrorListener);
						keyReader.openPort();
					}
					testKeyReaderItems.add(testKeyReaderItem);
				}

				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						setInput(testKeyReaderItems);
						select(0);
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tc;

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Use case");

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Key reader identifier");

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Last read key");

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Last read timestamp");

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Last error");

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Last error timestamp");

		table.setLayout(new WeightedTableLayout(new int[] { 20, 20, 20, 20, 20, 20 }));
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}


	public static final String PROPERTY_REFRESH = "refresh";

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}


}
