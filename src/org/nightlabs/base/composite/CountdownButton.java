package org.nightlabs.base.composite;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.resource.Messages;

public class CountdownButton
		extends XComposite
{
	private Button button;

	private String text = Messages.getString("org.nightlabs.base.composite.CountdownButton.defaultText"); //$NON-NLS-1$
	private int countdownLengthSec;
	private int countdownRest;

	private boolean countdownFinishedCalled = false;

	private Timer timer;
	private TimerTask timerTask = new TimerTask() {
		@Implement
		public void run()
		{
			button.getDisplay().asyncExec(new Runnable()
			{
				public void run()
				{
					countdownRest -= 1;

					if (countdownRest <= 0) {
						timer.cancel();
						timerTask.cancel();
						countdownRest = 0;
						if (!countdownFinishedCalled) {
							countdownFinishedCalled = true;
							countdownFinished();
						}
					}

					if (button.isDisposed()) {
						timer.cancel();
						timerTask.cancel();
						return;
					}

					setText(getText());
				}
			});
		}
	};

	/**
	 * @param parent The parent composite.
	 * @param style This style will be directly passed to the internal {@link Button}. Hence it can take any value that is supported
	 *		by {@link Button} ({@link SWT#PUSH}).
	 * @param countdownLengthSec The length of the timeout in seconds.
	 */
	public CountdownButton(Composite parent, int style, int countdownLengthSec)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		setCountdownLengthSec(countdownLengthSec);
		button = createButton(style);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fireSelectionEvent();
			}
		});
		setText(Messages.getString("org.nightlabs.base.composite.CountdownButton.defaultText")); //$NON-NLS-1$
		timer = new Timer("buttonTimer"); //$NON-NLS-1$
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				timer.cancel();
			}
		});
		timer.schedule(timerTask, 0, 1000);
	}

	public void setCountdownLengthSec(int countdownLengthSec)
	{
		this.countdownLengthSec = countdownLengthSec;
		this.countdownRest = countdownLengthSec;
	}

	public int getCountdownLengthSec()
	{
		return countdownLengthSec;
	}

	public int getCountdownRest()
	{
		return countdownRest;
	}
	public void setCountdownRest(int countdownRest)
	{
		this.countdownRest = countdownRest;
		setText(getText());
	}

	protected Button createButton(int style)
	{
		return new Button(this, style);
	}

	private String textFormat = Messages.getString("org.nightlabs.base.composite.CountdownButton.defaultTextFormat"); //$NON-NLS-1$

	public void setText(String text)
	{
		if (text == null)
			throw new IllegalArgumentException("text must not be null!"); //$NON-NLS-1$

		this.text = text;
		button.setText(String.format(textFormat, new Object[] {text, countdownRest}));
	}

	public String getText()
	{
		return text;
	}

	public void setTextFormat(String textFormat)
	{
		this.textFormat = textFormat;
	}
	public String getTextFormat()
	{
		return textFormat;
	}

	protected void countdownFinished()
	{
		fireSelectionEvent();
	}

	private ListenerList selectionListeners = new ListenerList();

	protected void fireSelectionEvent()
	{
		Event e = new Event();
		e.button = 1;
		e.display = button.getDisplay();
		e.item = this;
		e.widget = this;
		SelectionEvent se = new SelectionEvent(e);

		Object[] listeners = selectionListeners.getListeners();
		for (Object l : listeners)
			((SelectionListener)l).widgetSelected(se);
	}

	public void addSelectionListener(SelectionListener listener) {
		selectionListeners.add(listener);
	}
	public void removeSelectionListener(SelectionListener listener) {
		selectionListeners.remove(listener);
	}
}
