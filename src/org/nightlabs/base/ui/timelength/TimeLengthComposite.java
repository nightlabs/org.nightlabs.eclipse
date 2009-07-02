package org.nightlabs.base.ui.timelength;

import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.timelength.TimeLengthGeneratorException.Mode;

public class TimeLengthComposite extends XComposite
{
	private Text text;
	private long timeLength;
	private TimeLengthGenerator timeLengthGenerator;

	protected void assertSwtThread()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread!");
	}

	public void setTimeUnits(TimeUnit[] newTimeUnits) {
		assertSwtThread();
		timeLengthGenerator.setTimeUnits(newTimeUnits);
	}

	public void setTimeUnits(Collection<TimeUnit> newTimeUnits) {
		assertSwtThread();
		timeLengthGenerator.setTimeUnits(newTimeUnits);
	}

	public TimeLengthComposite(Composite parent) {
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.getGridData().grabExcessHorizontalSpace = false;
		this.getGridData().grabExcessVerticalSpace = false;
		text = new Text(this, XComposite.getBorderStyle(parent));
		text.setLayoutData(new GridData(GridData.FILL_BOTH));

		timeLengthGenerator = new TimeLengthGenerator();
	}

	public TimeLengthGenerator getTimeLengthGenerator() {
		return timeLengthGenerator;
	}

	public boolean isDisplayZeroValues() {
		return timeLengthGenerator.isDisplayZeroValues();
	}

	public void setDisplayZeroValues(boolean displayZeroValues) {
		timeLengthGenerator.setDisplayZeroValues(displayZeroValues);
	}

	/**
	 * Set the time length in milliseconds.
	 *
	 * @param timeLength the length in milliseconds.
	 */
	public void setTimeLength(long timeLength)
	{
		this.timeLength = timeLength;
		String time = timeLengthGenerator.getTimeLength(timeLength);
		text.setText(time);
	}

	/**
	 * Get the length in milliseconds.
	 *
	 * @return the time length in milliseconds.
	 */
	public long getTimeLength()
	{
		long time;
		try {
			time = timeLengthGenerator.getTimeLength(text.getText());
		}
		catch (TimeLengthGeneratorException e) {
			Mode mode = e.getMode();
			String valueStr = e.getValueStr();
			String symbol = e.getSymbol();
			String field = e.getField();
			if (mode == Mode.INVALID_FORMAT) {
				MessageDialog.openError(getShell(),
						Messages.getString("org.nightlabs.base.ui.timelength.TimeLengthComposite.errorDialog.invalidFormat.title"), //$NON-NLS-1$
						String.format(Messages.getString("org.nightlabs.base.ui.timelength.TimeLengthComposite.errorDialog.invalidFormat.message"), valueStr, symbol, field)); //$NON-NLS-1$
			}
			else if (mode == Mode.INVALID_NUMBER_FORMAT) {
				MessageDialog.openError(getShell(),
						Messages.getString("org.nightlabs.base.ui.timelength.TimeLengthComposite.errorDialog.invalidValue.title"), //$NON-NLS-1$
						String.format(Messages.getString("org.nightlabs.base.ui.timelength.TimeLengthComposite.errorDialog.invalidValue.message"), valueStr, symbol, field)); //$NON-NLS-1$
			}
			return timeLength;
		}
		timeLength = time;
		return timeLength;
	}

	// TODO this should be changed to use some kind of proxy in order to pass the correct widget (this) instead of the Text used internally!
	public void addModifyListener(ModifyListener listener) {
		text.addModifyListener(listener);
	}

	public void removeModifyListener(ModifyListener listener) {
		text.removeModifyListener(listener);
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		text.addFocusListener(listener);
	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		text.removeFocusListener(listener);
	}
}
