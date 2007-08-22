package org.nightlabs.keyreader.preference;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.composite.ComboComposite;
import org.nightlabs.base.composite.ListComposite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.config.Config;
import org.nightlabs.connection.Connection;
import org.nightlabs.connection.ConnectionImplementation;
import org.nightlabs.connection.ConnectionImplementationRegistry;
import org.nightlabs.connection.config.ConnectionCf;
import org.nightlabs.connection.ui.ConnectionCfEdit;
import org.nightlabs.connection.ui.ConnectionCfEditFactory;
import org.nightlabs.connection.ui.ConnectionCfEditRegistry;
import org.nightlabs.keyreader.KeyReaderImplementation;
import org.nightlabs.keyreader.KeyReaderImplementationRegistry;
import org.nightlabs.keyreader.KeyReaderSharingDevice;
import org.nightlabs.keyreader.KeyReaderUseCase;
import org.nightlabs.keyreader.KeyReaderUseCaseRegistry;
import org.nightlabs.keyreader.config.KeyReaderCf;
import org.nightlabs.keyreader.config.KeyReaderConfigModule;
import org.nightlabs.util.Utils;

public class KeyReaderPreferenceComposite
extends XComposite
{
	private ListComposite<KeyReaderUseCase> keyReaderUseCaseList;

	private XComposite detailComposite;
	private ComboComposite<KeyReaderImplementation> keyReaderImplementationCombo;
	private Text keyReaderImplementationClassName;
	private ComboComposite<ConnectionImplementation> connectionImplementationCombo;
	private Text slot;
	private Label shareDeviceWithKeyReaderLabel;
	private ComboComposite<KeyReaderUseCase> shareDeviceWithKeyReaderCombo;

	private LabelProvider keyReaderUseCaseLabelProvider = new LabelProvider() {
		@Override
		public String getText(Object element)
		{
			KeyReaderUseCase keyReaderUseCase = (KeyReaderUseCase) element;
			return keyReaderUseCase.getName() + " (" + keyReaderUseCase.getKeyReaderID() + ')';
		}
	};

	public KeyReaderPreferenceComposite(Composite parent, int style, LayoutDataMode layoutDataMode)
	{
		super(parent, style, layoutDataMode);
		getGridLayout().numColumns = 2;

		SashForm sashForm = new SashForm(this, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		keyReaderUseCaseList = new ListComposite<KeyReaderUseCase>(sashForm, SWT.SINGLE, (String) null, 
				keyReaderUseCaseLabelProvider);
		keyReaderUseCaseList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				keyReaderUseCaseSelected();
			}
		});


		detailComposite = new XComposite(sashForm, SWT.BORDER);
		detailComposite.getGridLayout().numColumns = 2;

		new Label(detailComposite, SWT.NONE).setText("Driver:");
		keyReaderImplementationCombo = new ComboComposite<KeyReaderImplementation>(detailComposite, SWT.READ_ONLY | SWT.DROP_DOWN, new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				KeyReaderImplementation keyReaderImplementation = (KeyReaderImplementation) element;
				return keyReaderImplementation.getName(Locale.getDefault());
			}
		});
		keyReaderImplementationCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				keyReaderImplementationSelected();
			}
		});

		new Label(detailComposite, SWT.NONE).setText("Class:");
		keyReaderImplementationClassName = new Text(detailComposite, SWT.BORDER | SWT.READ_ONLY);
		keyReaderImplementationClassName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(detailComposite, SWT.NONE).setText("Slot:");
		slot = new Text(detailComposite, SWT.BORDER);
		slot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		slot.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				keyReaderCf.setSlot(slot.getText());
			}
		});

		load();
		keyReaderUseCaseSelected();
	}

	private void keyReaderImplementationSelected()
	{
		KeyReaderImplementation keyReaderImplementation = keyReaderImplementationCombo.getSelectedElement();
		if (keyReaderImplementation == null)
			keyReaderImplementationClassName.setText("");
		else {
			keyReaderImplementationClassName.setText(
					keyReaderImplementation.getKeyReaderClassName());

			keyReaderCf.setKeyReaderClass(keyReaderImplementation.getKeyReaderClassName());
		}

		if (KeyReaderSharingDevice.class.equals(keyReaderImplementation.getKeyReaderClass())) {
			connectionImplementationCombo_dispose();
			shareDeviceWithKeyReaderCombo_create();
		}
		else {
			keyReaderCf.setShareDeviceWith("");
			shareDeviceWithKeyReaderCombo_dispose();
			connectionImplementationCombo_create();
		}
		detailComposite.layout(true, true);
	}

	private void shareDeviceWithKeyReaderCombo_create()
	{
		shareDeviceWithKeyReaderCombo_dispose();

		shareDeviceWithKeyReaderLabel = new Label(detailComposite, SWT.NONE);
		shareDeviceWithKeyReaderLabel.setText("Share:");
		shareDeviceWithKeyReaderCombo = new ComboComposite<KeyReaderUseCase>(detailComposite, SWT.DROP_DOWN | SWT.READ_ONLY, keyReaderUseCaseLabelProvider);

		KeyReaderUseCase selected = null;
		for (KeyReaderUseCase useCase : KeyReaderUseCaseRegistry.sharedInstance().getKeyReaderUseCases()) {
			if (!keyReaderUseCase.getKeyReaderID().equals(useCase.getKeyReaderID()))
				shareDeviceWithKeyReaderCombo.addElement(useCase);
			if (keyReaderCf.getShareDeviceWith() != null && keyReaderCf.getShareDeviceWith().equals(useCase.getKeyReaderID()))
				selected = useCase;
		}
		shareDeviceWithKeyReaderCombo.selectElement(selected);

		shareDeviceWithKeyReaderCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				KeyReaderUseCase selected = shareDeviceWithKeyReaderCombo.getSelectedElement();
				if (selected == null)
					keyReaderCf.setShareDeviceWith("");
				else
					keyReaderCf.setShareDeviceWith(selected.getKeyReaderID());
			}
		});
	}
	private void shareDeviceWithKeyReaderCombo_dispose()
	{
		if (shareDeviceWithKeyReaderLabel != null) {
			shareDeviceWithKeyReaderLabel.dispose();
			shareDeviceWithKeyReaderLabel = null;
		}
		if (shareDeviceWithKeyReaderCombo != null) {
			shareDeviceWithKeyReaderCombo.dispose();
			shareDeviceWithKeyReaderCombo = null;
		}
	}

	private Label connectionLabel;
	private void connectionImplementationCombo_create()
	{
		connectionImplementationCombo_dispose();

		connectionLabel = new Label(detailComposite, SWT.NONE);
		connectionLabel.setText("Connection:");
		connectionImplementationCombo = new ComboComposite<ConnectionImplementation>(detailComposite, SWT.READ_ONLY | SWT.DROP_DOWN, new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				ConnectionImplementation connectionImplementation = (ConnectionImplementation) element;
				return connectionImplementation.getName(Locale.getDefault());
			}
		});
		connectionImplementationCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				try {
					ConnectionImplementation connectionImplementation = connectionImplementationCombo.getSelectedElement();
					connectionCfEdit_dispose();
					if (connectionImplementation != null) {
						Class connectionCfClass = ((Connection)connectionImplementation.getConnectionClass().newInstance()).getConnectionCfClass();
						ConnectionCf connectionCf = (ConnectionCf) connectionCfClass.newInstance();
						connectionCf.init();
						keyReaderCf.setConnectionCf(connectionCf);
						connectionCfEdit_create();
					}
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
			}
		});
		connectionImplementationCombo.removeAll();
		connectionImplementationCombo.addElements(ConnectionImplementationRegistry.sharedInstance().getConnectionImplementations());

		ConnectionCf connectionCf = keyReaderCf.getConnectionCf();
		if (connectionCf == null)
			connectionImplementationCombo.selectElement(null);
		else
			connectionCfEdit_create();
	}

	private void connectionImplementationCombo_dispose()
	{
		if (connectionLabel != null) {
			connectionLabel.dispose();
			connectionLabel = null;
		}
		if (connectionImplementationCombo != null) {
			connectionImplementationCombo.dispose();
			connectionImplementationCombo = null;
		}
		connectionCfEdit_dispose();
	}

	private KeyReaderUseCase keyReaderUseCase;
	private KeyReaderConfigModule keyReaderConfigModule;
	private KeyReaderCf keyReaderCf;
	private Map<String, KeyReaderCf> keyReaderID2keyReaderCfClone = new HashMap<String, KeyReaderCf>();
	private ConnectionCfEdit connectionCfEdit = null;
	private Composite connectionCfEditCompositeParent = null;

	private void keyReaderUseCaseSelected()
	{
		keyReaderUseCase = keyReaderUseCaseList.getSelectedElement();
		if (keyReaderUseCase != null) {
			detailComposite.setEnabled(true);
			keyReaderCf = keyReaderID2keyReaderCfClone.get(keyReaderUseCase.getKeyReaderID());
			if (keyReaderCf == null) {
				keyReaderCf = keyReaderConfigModule._getKeyReaderCf(keyReaderUseCase.getKeyReaderID()); // guaranteed to return a non-null result
				keyReaderCf = Utils.cloneSerializable(keyReaderCf);
				keyReaderID2keyReaderCfClone.put(keyReaderUseCase.getKeyReaderID(), keyReaderCf);
			}

			connectionCfEdit_dispose();

			String keyReaderClass = keyReaderCf.getKeyReaderClass();
			KeyReaderImplementation keyReaderImplementation = className2KeyReaderImplementation.get(keyReaderClass);
			keyReaderImplementationCombo.selectElement(keyReaderImplementation);
			keyReaderImplementationClassName.setText(keyReaderClass);

			String s = keyReaderCf.getSlot();
			slot.setText(s == null ? "" : s);

			keyReaderImplementationSelected();
		}
		else {
			keyReaderCf = null;
			detailComposite.setEnabled(false);
			clearUI();
		}
	}

	private void connectionCfEdit_create()
	{
		connectionCfEdit_dispose();

		ConnectionCf connectionCf = keyReaderCf.getConnectionCf();
		ConnectionCfEditFactory factory = ConnectionCfEditRegistry.sharedInstance().getConnectionCfEditFactory(connectionCf.getConnectionClass().getName(), true);
		connectionImplementationCombo.selectElement(factory.getConnectionImplementation());
		connectionCfEdit = factory.createConnectionCfEdit(connectionCf);

		connectionCfEditCompositeParent = new XComposite(detailComposite, SWT.BORDER, LayoutMode.TIGHT_WRAPPER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		connectionCfEditCompositeParent.setLayoutData(gd);

		connectionCfEdit.createConnectionCfEditComposite(connectionCfEditCompositeParent);

		detailComposite.layout(true, true);
	}

	private void connectionCfEdit_dispose()
	{
		if (connectionCfEdit != null) {
			connectionCfEdit.getConnectionCfEditComposite().dispose();
			connectionCfEdit = null;
		}

		if (connectionCfEditCompositeParent != null) {
			connectionCfEditCompositeParent.dispose();
			connectionCfEditCompositeParent = null;
		}
	}

	private void clearUI()
	{
		keyReaderImplementationCombo.selectElement(null);
		connectionCfEdit_dispose();
	}

	private Map<String, KeyReaderImplementation> className2KeyReaderImplementation = new HashMap<String, KeyReaderImplementation>();

	public void load()
	{
		keyReaderConfigModule = (KeyReaderConfigModule) Config.sharedInstance().createConfigModule(KeyReaderConfigModule.class);

		for (KeyReaderImplementation keyReaderImplementation : KeyReaderImplementationRegistry.sharedInstance().getKeyReaderImplementations())
			className2KeyReaderImplementation.put(keyReaderImplementation.getKeyReaderClassName(), keyReaderImplementation);

		keyReaderUseCaseList.removeAll();
		keyReaderUseCaseList.addElements(KeyReaderUseCaseRegistry.sharedInstance().getKeyReaderUseCases());

		keyReaderImplementationCombo.removeAll();
		keyReaderImplementationCombo.addElements(KeyReaderImplementationRegistry.sharedInstance().getKeyReaderImplementations());
	}

	public void save()
	{
		for (KeyReaderCf clone : keyReaderID2keyReaderCfClone.values())
			keyReaderConfigModule._addKeyReaderCf(Utils.cloneSerializable(clone));
	}

}
