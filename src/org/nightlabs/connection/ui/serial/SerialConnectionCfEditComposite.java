package org.nightlabs.connection.ui.serial;

import java.util.ArrayList;
import java.util.List;

import gnu.io.CommPortIdentifier;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.composite.ComboComposite;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.connection.resource.Messages;
import org.nightlabs.connection.rxtx.config.SerialConnectionCf;

public class SerialConnectionCfEditComposite
		extends XComposite
{
	private SerialConnectionCfEdit serialConnectionCfEdit;

	private ComboComposite<String> addressCombo;
	private ComboComposite<Integer> baudRateCombo;
	private ComboComposite<Integer> dataBitsCombo;
	private ComboComposite<Character> parityCombo;
	private ComboComposite<Integer> stopBitsCombo;

	public SerialConnectionCfEditComposite(Composite parent, SerialConnectionCfEdit serialConnectionCfEdit)
	{
		super(parent, SWT.NONE);
		this.serialConnectionCfEdit = serialConnectionCfEdit;

		getGridLayout().numColumns = 2;

		new Label(this, SWT.NONE).setText(Messages.getString("ui.serial.SerialConnectionCfEditComposite.portLabel.text")); //$NON-NLS-1$
		addressCombo = new ComboComposite<String>(this, SWT.READ_ONLY);

		new Label(this, SWT.NONE).setText(Messages.getString("ui.serial.SerialConnectionCfEditComposite.baudRateLabel.text")); //$NON-NLS-1$
		baudRateCombo = new ComboComposite<Integer>(this, SWT.READ_ONLY);
		for (int baudRate : SerialConnectionCf.BAUD_RATE)
			baudRateCombo.addElement(baudRate);

		new Label(this, SWT.NONE).setText(Messages.getString("ui.serial.SerialConnectionCfEditComposite.dataBitsLabel.text")); //$NON-NLS-1$
		dataBitsCombo = new ComboComposite<Integer>(this, SWT.READ_ONLY);
		for (int dataBits : SerialConnectionCf.DATA_BITS)
			dataBitsCombo.addElement(dataBits);

		new Label(this, SWT.NONE).setText(Messages.getString("ui.serial.SerialConnectionCfEditComposite.parityLabel.text")); //$NON-NLS-1$
		parityCombo = new ComboComposite<Character>(this, SWT.READ_ONLY);
		for (char parity : SerialConnectionCf.PARITY)
			parityCombo.addElement(parity);

		new Label(this, SWT.NONE).setText(Messages.getString("ui.serial.SerialConnectionCfEditComposite.stopBitsLabel.text")); //$NON-NLS-1$
		stopBitsCombo = new ComboComposite<Integer>(this, SWT.READ_ONLY);
		for (int stopBits : SerialConnectionCf.STOP_BITS)
			stopBitsCombo.addElement(stopBits);

		addressCombo.addSelectionChangedListener(selectionChangedListener);
		baudRateCombo.addSelectionChangedListener(selectionChangedListener);
		dataBitsCombo.addSelectionChangedListener(selectionChangedListener);
		parityCombo.addSelectionChangedListener(selectionChangedListener);
		stopBitsCombo.addSelectionChangedListener(selectionChangedListener);

		load();
	}

	public SerialConnectionCfEdit getSerialConnectionCfEdit()
	{
		return serialConnectionCfEdit;
	}

	private ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {
		public void selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent event) {
			save();
		}
	};

	private void load()
	{
		addressCombo.removeAll();
		List<CommPortIdentifier> cpis;
		try {
			cpis = SerialConnectionCf.getSerialPortIdentifiers();
		} catch (Throwable t) {
			cpis = new ArrayList<CommPortIdentifier>();
			ExceptionHandlerRegistry.asyncHandleException(t);
		}
		for (CommPortIdentifier cpi : cpis)
			addressCombo.addElement(cpi.getName());

		SerialConnectionCf connectionCf = (SerialConnectionCf) serialConnectionCfEdit.getConnectionCf();
		addressCombo.selectElement(connectionCf.getAddress());
		baudRateCombo.selectElement(connectionCf.getBaudRate());
		dataBitsCombo.selectElement(connectionCf.getDataBits());
		parityCombo.selectElement(connectionCf.getParity());
		stopBitsCombo.selectElement(connectionCf.getStopBits());
	}

	private void save()
	{
		SerialConnectionCf connectionCf = (SerialConnectionCf) serialConnectionCfEdit.getConnectionCf();

		connectionCf.setAddress(addressCombo.getSelectedElement());

		Integer baudRate = baudRateCombo.getSelectedElement();
		connectionCf.setBaudRate(baudRate == null ? SerialConnectionCf.BAUD_RATE[0] : baudRate.intValue());

		Integer dataBits = dataBitsCombo.getSelectedElement();
		connectionCf.setDataBits(dataBits == null ? SerialConnectionCf.DATA_BITS[SerialConnectionCf.DATA_BITS.length - 1] : dataBits.intValue());

		Character parity = parityCombo.getSelectedElement();
		connectionCf.setParity(parity == null ? SerialConnectionCf.PARITY_NONE : parity.charValue());

		Integer stopBits = stopBitsCombo.getSelectedElement();
		connectionCf.setStopBits(stopBits == null ? SerialConnectionCf.STOP_BITS[0] : stopBits.intValue());
	}
}
