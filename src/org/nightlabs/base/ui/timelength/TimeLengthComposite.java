package org.nightlabs.base.ui.timelength;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.nightlabs.util.CollectionUtil;

public class TimeLengthComposite extends XComposite
{
	private Text text;

	private List<TimeUnit> timeUnits = new ArrayList<TimeUnit>(TimeUnit.values().length);
	{
		timeUnits.add(TimeUnit.day);
		timeUnits.add(TimeUnit.hour);
		timeUnits.add(TimeUnit.minute);
		timeUnits.add(TimeUnit.second);
		timeUnits.add(TimeUnit.msec);
	}

	/**
	 * Get the time units displayed by this {@link TimeLengthComposite} instance.
	 * @return the time units.
	 */
	public List<TimeUnit> getTimeUnits() {
		assertSwtThread();
		return Collections.unmodifiableList(timeUnits);
	}

	protected void assertSwtThread()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread!");
	}

	public void setTimeUnits(TimeUnit[] newTimeUnits) {
		setTimeUnits(CollectionUtil.array2ArrayList(newTimeUnits));
	}

	public void setTimeUnits(Collection<TimeUnit> newTimeUnits) {
		assertSwtThread();

		if (newTimeUnits == null)
			throw new IllegalArgumentException("newTimeUnits must not be null!");

		if (newTimeUnits.isEmpty())
			throw new IllegalArgumentException("newTimeUnits must not be empty!");

		Set<TimeUnit> s = new HashSet<TimeUnit>(newTimeUnits);
		this.timeUnits.clear();
		for (TimeUnit timeUnit : TimeUnit.values()) {
			if (s.contains(timeUnit))
				this.timeUnits.add(timeUnit);
		}
	}

	// we might later l10n it from properties - in this case we should convert it to an object-field (rather than a class field - i.e. non-static)
	private static final Map<TimeUnit, String> timeUnitSymbolMap;
	static {
		Map<TimeUnit, String> m = new HashMap<TimeUnit, String>();
		for (TimeUnit timeUnit : TimeUnit.values()) {
			if (timeUnit == TimeUnit.msec)
				m.put(timeUnit, timeUnit.name());
			else if (timeUnit == TimeUnit.month)
				m.put(timeUnit, "M");
			else
				m.put(timeUnit, timeUnit.name().substring(0, 1));
		}
		timeUnitSymbolMap = Collections.unmodifiableMap(m);
	}

	private static final Map<TimeUnit, Long> timeUnit2lengthMSec;
	static {
		Map<TimeUnit, Long> m = new HashMap<TimeUnit, Long>();
		m.put(TimeUnit.year,   1000L * 60L * 60L * 24L * 365);
		m.put(TimeUnit.month,  1000L * 60L * 60L * 24L * 30);
		m.put(TimeUnit.day,    1000L * 60L * 60L * 24L);
		m.put(TimeUnit.hour,   1000L * 60L * 60L);
		m.put(TimeUnit.minute, 1000L * 60L);
		m.put(TimeUnit.second, 1000L);
		m.put(TimeUnit.msec,   1L);
		timeUnit2lengthMSec = Collections.unmodifiableMap(m);
	}

	public TimeLengthComposite(Composite parent) {
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.getGridData().grabExcessHorizontalSpace = false;
		this.getGridData().grabExcessVerticalSpace = false;
		text = new Text(this, XComposite.getBorderStyle(parent));
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private long timeLength;

	private boolean displayZeroValues = true;

	public boolean isDisplayZeroValues() {
		return displayZeroValues;
	}

	public void setDisplayZeroValues(boolean displayZeroValues) {
		this.displayZeroValues = displayZeroValues;
	}

	/**
	 * Set the time length in milliseconds.
	 *
	 * @param timeLength the length in milliseconds.
	 */
	public void setTimeLength(long timeLength)
	{
		this.timeLength = timeLength;

		long rest = timeLength;
		Map<TimeUnit, Long> lengthMap = new HashMap<TimeUnit, Long>();
		for (TimeUnit timeUnit : timeUnits) {
			Long lengthOfOneTimeUnit = timeUnit2lengthMSec.get(timeUnit);
			long value = rest / lengthOfOneTimeUnit;
			lengthMap.put(timeUnit, value);
			rest = rest - value * lengthOfOneTimeUnit;
		}

		StringBuilder sb = new StringBuilder();
		for (TimeUnit timeUnit : timeUnits) {
			String symbol = timeUnitSymbolMap.get(timeUnit);
			Long length = lengthMap.get(timeUnit);
			if (displayZeroValues || length.longValue() != 0) {
				if (sb.length() > 0)
					sb.append(' ');

				sb.append(length.longValue());
				sb.append(symbol);
			}
		}

//		if (sb.length() == 0) {
//			sb.append(0);
//			sb.append(timeUnitSymbolMap.get(TimeUnit.msec));
//		}

		text.setText(sb.toString());
	}

	private TimeUnit findTimeUnitInString(String field)
	{
		for (Map.Entry<TimeUnit, String> me : timeUnitSymbolMap.entrySet()) {
			if (field.endsWith(me.getValue()))
				return me.getKey();
		}
		return null;
	}
	private TimeUnit parseTimeUnit(String field)
	{
		for (Map.Entry<TimeUnit, String> me : timeUnitSymbolMap.entrySet()) {
			if (field.equals(me.getValue()))
				return me.getKey();
		}
		return null;
	}

	/**
	 * Get the length in milliseconds.
	 *
	 * @return the time length in milliseconds.
	 */
	public long getTimeLength()
	{
		// parse the string
		String[] _fields = text.getText().split("\\s"); //$NON-NLS-1$
		List<String> fieldList = new ArrayList<String>(_fields.length);
		for (int i = 0; i < _fields.length; i++) {
			if (!"".equals(_fields[i])) //$NON-NLS-1$
				fieldList.add(_fields[i]);
		}
		String[] fields = CollectionUtil.collection2TypedArray(fieldList, String.class);

		Map<TimeUnit, Long> lengthMap = new HashMap<TimeUnit, Long>();

		for (int i = 0; i < fields.length; i++) {
			// usually, the field should be value + symbol without space
			String field = fields[i];
			TimeUnit timeUnit = findTimeUnitInString(field);
			String symbol;
			String valueStr;
			if (timeUnit != null) {
				symbol = timeUnitSymbolMap.get(timeUnit);
				valueStr = field.substring(0, field.length() - symbol.length());
			}
			else {
				// TimeUnit not found in this field - try it in the next.
				if (++i < fields.length) {
					symbol = fields[i];
					timeUnit = parseTimeUnit(symbol);
				}
				else {
					timeUnit = TimeUnit.msec;
					symbol = timeUnitSymbolMap.get(timeUnit);
				}
				valueStr = field;
				field = field + ' ' + symbol;
				if (timeUnit == null) {
					MessageDialog.openError(getShell(), Messages.getString("org.nightlabs.base.ui.timelength.TimeLengthComposite.errorDialog.invalidFormat.title"), String.format(Messages.getString("org.nightlabs.base.ui.timelength.TimeLengthComposite.errorDialog.invalidFormat.message"), valueStr, symbol, field)); //$NON-NLS-1$ //$NON-NLS-2$
					return timeLength;
				}
			}

			long value;
			try {
				value = Long.parseLong(valueStr);
			} catch (NumberFormatException x) {
				MessageDialog.openError(getShell(), Messages.getString("org.nightlabs.base.ui.timelength.TimeLengthComposite.errorDialog.invalidValue.title"), String.format(Messages.getString("org.nightlabs.base.ui.timelength.TimeLengthComposite.errorDialog.invalidValue.message"), valueStr, symbol, field)); //$NON-NLS-1$ //$NON-NLS-2$
				return timeLength;
			}
			lengthMap.put(timeUnit, value);
		}

		long newTimeLength = 0;
		for (TimeUnit timeUnit : TimeUnit.values()) {
			Long length = lengthMap.get(timeUnit);
			if (length == null)
				continue;

			Long lengthOfOneTimeUnit = timeUnit2lengthMSec.get(timeUnit);
			newTimeLength = newTimeLength + length * lengthOfOneTimeUnit;
		}
		timeLength = newTimeLength;

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
