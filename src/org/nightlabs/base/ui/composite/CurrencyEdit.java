/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.ui.composite;

import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.l10n.Currency;
import org.nightlabs.l10n.NumberFormatter;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CurrencyEdit extends XComposite
{
	private Currency currency;
	private final Text numberText;
	//	private Label currencySymbol;
	private long value;
	private final long flags;
	private Button active;

	public static final long FLAGS_NONE = 0;
	public static final long FLAGS_SHOW_ACTIVE_CHECK_BOX = 0x100000000L;

	/**
	 * @param parent
	 */
	public CurrencyEdit(final Composite parent, final Currency _currency)
	{
		this(parent, _currency, 0, null);
	}

	public CurrencyEdit(final Composite parent, final Currency _currency, final long _flags, final String caption)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.currency = _currency;
		this.flags = _flags;

		getGridLayout().numColumns = 2;

		if (caption != null) {
			Control control;
			if ((FLAGS_SHOW_ACTIVE_CHECK_BOX & flags) == FLAGS_SHOW_ACTIVE_CHECK_BOX) {
				active = new Button(this, SWT.CHECK);
				active.setText(caption);
				control = active;
			}
			else {
				final Label l = new Label(this, SWT.WRAP);
				l.setText(caption);
				control = l;
			}
			final GridData gd = new GridData();
			gd.horizontalSpan = getGridLayout().numColumns;
			control.setLayoutData(gd);
		}

		if ((FLAGS_SHOW_ACTIVE_CHECK_BOX & flags) == FLAGS_SHOW_ACTIVE_CHECK_BOX) {
			if (active == null) {
				++getGridLayout().numColumns;
				active = new Button(this, SWT.CHECK);
			}

			active.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e)
				{
					activeSelected();
				}
			});
		}

		numberText = new Text(this, SWT.BORDER);
		numberText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//		currencySymbol = new Label(this, SWT.NONE);
		setCurrency(currency);

		numberText.addModifyListener(new ModifyListener(){
			public void modifyText(final ModifyEvent e)
			{
				if (!modifyListenerEnabled)
					return;

				final String s = numberText.getText();
				try {
					value = NumberFormatter.parseCurrency(s, currency, false);
					parseException = null;
				} catch (final ParseException x) {
					parseException = x;

					if (errorDialogEnabled)
						MessageDialog.openError(getShell(), Messages.getString("org.nightlabs.base.ui.composite.CurrencyEdit.errorNotANumber.title"), Messages.getString("org.nightlabs.base.ui.composite.CurrencyEdit.errorNotANumber.message") + ' ' + x.getLocalizedMessage()); //$NON-NLS-1$ //$NON-NLS-2$
				}

				if (modifyListeners == null)
					return;

				final Event event = new Event();
				event.widget = CurrencyEdit.this;
				event.display = e.display;
				event.time = e.time;
				event.data = e.data;
				final ModifyEvent me = new ModifyEvent(event);
				for (final Iterator<ModifyListener> it = modifyListeners.iterator(); it.hasNext(); ) {
					final ModifyListener l = it.next();
					l.modifyText(me);
				}
			}
		});

		activeSelected();
	}

	private boolean modifyListenerEnabled = true;

	private void activeSelected()
	{
		numberText.setEnabled(isActive());
		//		currencySymbol.setEnabled(isActive());
	}

	private boolean errorDialogEnabled = true;

	/**
	 * @return Returns the errorDialogEnabled.
	 */
	public boolean isErrorDialogEnabled()
	{
		return errorDialogEnabled;
	}
	/**
	 * If <tt>errorDialogEnabled</tt> is <tt>true</tt> and the user enters
	 * invalid data, a {@link MessageDialog} will be opened. This is the
	 * default behaviour, but might be unwanted - e.g. in a wizard.
	 * <p>
	 * If you want to handle erroneous input externally, set this <tt>false</tt>
	 * and register a {@link ModifyListener}. You can then find out, whether
	 * there was an parser error by calling {@link #getParseException()}.
	 *
	 * @param errorDialogEnabled The errorDialogEnabled to set.
	 */
	public void setErrorDialogEnabled(final boolean errorDialogEnabled)
	{
		this.errorDialogEnabled = errorDialogEnabled;
	}

	private ParseException parseException = null;

	/**
	 * @return Returns the last <tt>ParseException</tt> or <tt>null</tt> if the
	 *		user-input is OK.
	 */
	public ParseException getParseException()
	{
		return parseException;
	}

	private LinkedList<ModifyListener> modifyListeners = null;

	public void addModifyListener(final ModifyListener modifyListener)
	{
		if (modifyListeners == null)
			modifyListeners = new LinkedList<ModifyListener>();

		modifyListeners.add(modifyListener);
	}

	public boolean removeModifyListener(final ModifyListener modifyListener)
	{
		if (modifyListeners == null)
			return false;

		return modifyListeners.remove(modifyListener);
	}

	// same method exists in org.nightlabs.l10n.AbstractNumberFormatter
	private static long power(long base, int exponent) { // exponent >= 0
		if (exponent < 0)
			throw new IllegalArgumentException("exponent < 0!");

		long result = 1;
		while (exponent != 0) {
			if (exponent % 2 != 0)
				result = result * base;
			exponent = exponent / 2;
			base = base * base;
		}
		return result;
	}

	public void setCurrency(final Currency currency)
	{
		final int oldDecimalDigitCount = this.currency.getDecimalDigitCount();
		final int newDecimalDigitCount = currency.getDecimalDigitCount();

		this.currency = currency;
		//		currencySymbol.setText(currency.getCurrencySymbol());
		if (oldDecimalDigitCount == newDecimalDigitCount)
			setValue(getValue());
		else
			setValue(getValue() / power(10, oldDecimalDigitCount) * power(10, newDecimalDigitCount));
	}

	public void setValue(final long currencyValue)
	{
		modifyListenerEnabled = false;
		try {
			this.value = currencyValue;
			numberText.setText(
					NumberFormatter.formatCurrency(currencyValue, currency, true));
		} finally {
			modifyListenerEnabled = true;
		}
	}

	public long getValue()
	{
		return value;
	}

	/**
	 * @return Returns the currency.
	 */
	public Currency getCurrency()
	{
		return currency;
	}

	public boolean isActive()
	{
		return active == null ? true : active.getSelection();
	}

	public void setActive(final boolean active)
	{
		if (this.active == null)
			return;

		this.active.setSelection(active);
		activeSelected();
	}

	public void addVerifyListener(final VerifyListener listener){
		numberText.addVerifyListener(listener);
	}
}
