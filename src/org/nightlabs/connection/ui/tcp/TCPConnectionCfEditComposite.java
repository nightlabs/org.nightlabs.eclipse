package org.nightlabs.connection.ui.tcp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.connection.resource.Messages;
import org.nightlabs.connection.tcp.config.TCPConnectionCf;

public class TCPConnectionCfEditComposite
		extends XComposite
{
	private TCPConnectionCfEdit tcpConnectionCfEdit;

	private Text host;
	private Spinner port;
	private Button useSSL;
	private Spinner soTimeout;

	public TCPConnectionCfEditComposite(Composite parent, TCPConnectionCfEdit tcpConnectionCfEdit)
	{
		super(parent, SWT.NONE);
		this.getGridLayout().numColumns = 2;
		this.tcpConnectionCfEdit = tcpConnectionCfEdit;

		new Label(this, SWT.NONE).setText(Messages.getString("ui.tcp.TCPConnectionCfEditComposite.hostLabel.text")); //$NON-NLS-1$
		host = new Text(this, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		host.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				save();
			}
		});

		new Label(this, SWT.NONE).setText(Messages.getString("ui.tcp.TCPConnectionCfEditComposite.portLabel.text")); //$NON-NLS-1$
		port = new Spinner(this, SWT.BORDER);
		port.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		port.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				save();
			}
		});
		port.setMinimum(1);
		port.setMaximum(65535);

		useSSL = new Button(this, SWT.CHECK);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		useSSL.setLayoutData(gd);
		useSSL.setText(Messages.getString("ui.tcp.TCPConnectionCfEditComposite.useSSL.text")); //$NON-NLS-1$
		useSSL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				save();
			}
		});

		new Label(this, SWT.NONE).setText(Messages.getString("ui.tcp.TCPConnectionCfEditComposite.soTimeoutLabel.text")); //$NON-NLS-1$
		soTimeout = new Spinner(this, SWT.BORDER);
		soTimeout.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		soTimeout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				save();
			}
		});
		soTimeout.setMinimum(0);
		soTimeout.setMaximum(Integer.MAX_VALUE);

		load();
	}

	private boolean loading = false;

	private void load()
	{
		loading = true;
		try {
			TCPConnectionCf connectionCf = (TCPConnectionCf) tcpConnectionCfEdit.getConnectionCf();
			host.setText(connectionCf.getHost());
			port.setSelection(connectionCf.getPort());
			useSSL.setSelection(connectionCf.isUseSSL());
			soTimeout.setSelection(connectionCf.getSoTimeout());
		} finally {
			loading = false;
		}
	}

	private void save()
	{
		if (loading)
			return;

		TCPConnectionCf connectionCf = (TCPConnectionCf) tcpConnectionCfEdit.getConnectionCf();
		connectionCf.setAddress(host.getText() + ':' + port.getSelection());
		connectionCf.setUseSSL(useSSL.getSelection());
		connectionCf.setSoTimeout(soTimeout.getSelection());
	}
}
