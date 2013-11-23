package org.nightlabs.base.ui.timepattern.input;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.timepattern.InputTimePattern;

/**
 * Composite to edit an {@link InputTimePattern} using an {@link InputTimePatternDialog}. It will
 * show a Label with the definition-string of the current time pattern and let the user open an
 * {@link InputTimePatternDialog} to create a new pattern.
 * <p>
 * This Composite is capable of optionally displaying a caption label or a caption checkbox. To
 * create the checkbox use the style {@link #STYLE_SHOW_ACTIVE_CHECKBOX} when constructing the
 * Composite. Note, that when the checkbox-style is applied, the 'active'-behaviour is switched on
 * and {@link #isActive()} and {@link #setActive(boolean)} can be used.
 * </p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class InputTimePatternEditComposite extends XComposite {

	/**
	 * This style can be used in addition to the SWT style to have the caption rendered as checkbox
	 * and enabling the 'active'-behaviour.
	 */
	public static final int STYLE_SHOW_ACTIVE_CHECKBOX = 0x10000000;
	
	/** The {@link InputTimePattern} of this composite */
	private InputTimePattern inputTimePattern;
	/** The {@link Label} displaying the definition-string */
	private Text displayText;
	
	/** Optional caption, set in the constructor. If set a label will be created */
	private final String caption;
	
	/** Listeners for modification */
	private ListenerList modifyListeners = new ListenerList();
	
	/** Used to suppress unnecessary Modify notifications, set in {@link #setInputTimePattern(InputTimePattern)} */
	private boolean refreshing = false;
	/** ModifyListener on the displayLabel that delegates to all {@link ModifyListener}s added to this Composite */
	private ModifyListener displayTextModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent event) {
			if (!refreshing) {
				notifyModifyListener(event);
			}
		}
	};
	
	/** The style this was constructed with */
	private int constructorStyle;

	/** The Checkbox created when {@link #STYLE_SHOW_ACTIVE_CHECKBOX} was used */
	private Button activeCheckbox;

	/** The Button opening the dialog to define the pattern */
	private Button dialogButton;
	
	/**
	 * Create a new {@link InputTimePatternEditComposite} that fills horizontal.
	 * 
	 * @param parent The parent to use.
	 * @param style The style to use.
	 */
	public InputTimePatternEditComposite(Composite parent, int style) {
		this(parent, style, LayoutDataMode.GRID_DATA_HORIZONTAL);
	}

	/**
	 * Create a new {@link InputTimePatternEditComposite} that fills horizontal.
	 * 
	 * @param parent The parent to use.
	 * @param style The style to use.
	 * @param caption An optional caption for the Composite 
	 */
	public InputTimePatternEditComposite(Composite parent, int style, String caption) {
		this(parent, style, LayoutDataMode.GRID_DATA_HORIZONTAL, caption);
	}
	
	/**
	 * Create a new {@link InputTimePatternEditComposite} with the given {@link LayoutDataMode}.
	 * 
	 * @param parent The parent to use.
	 * @param style The style to use.
	 * @param layoutDataMode The {@link LayoutDataMode} to use.
	 */
	public InputTimePatternEditComposite(Composite parent, int style, LayoutDataMode layoutDataMode) {
		this(parent, style, layoutDataMode, null);
	}
	
	/**
	 * Create a new {@link InputTimePatternEditComposite} with the given {@link LayoutDataMode}.
	 * 
	 * @param parent The parent to use.
	 * @param style The style to use.
	 * @param layoutDataMode The {@link LayoutDataMode} to use.
	 * @param caption An optional caption for the Composite 
	 */
	public InputTimePatternEditComposite(Composite parent, int style, LayoutDataMode layoutDataMode, String caption) {
		super(parent, getClearStyle(style), LayoutMode.TIGHT_WRAPPER, layoutDataMode);
		this.constructorStyle = style;
		this.caption = caption;
		init();
	}
	
	/**
	 * Used internally to get the .
	 * 
	 * @param style The input style.
	 * @return The SWT style stripped from the custom style.
	 */
	private static int getClearStyle(int style) {
		return style & ~STYLE_SHOW_ACTIVE_CHECKBOX;
	}

	/**
	 * Used internally to check for the {@link #STYLE_SHOW_ACTIVE_CHECKBOX} flag in the consturctorStyle.
	 * 
	 * @return <code>true</code> if the {@link #STYLE_SHOW_ACTIVE_CHECKBOX} was used.
	 */
	private boolean isShowActiveCheckbox() {
		return (constructorStyle & STYLE_SHOW_ACTIVE_CHECKBOX) > 0;
	}
	
	/**
	 * Creates the widgets and listeners.
	 */
	private void init() {
		getGridLayout().numColumns = 2;
		getGridLayout().makeColumnsEqualWidth = false;
		if (caption != null && !caption.isEmpty()) {
			
			Control control;
			if (isShowActiveCheckbox()) {
				activeCheckbox = new Button(this, SWT.CHECK);
				activeCheckbox.setText(caption);
				activeCheckbox.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						activeSelected();
					}
				});				
				control = activeCheckbox;
			}
			else {
				Label l = new Label(this, SWT.WRAP);
				l.setText(caption);
				control = l;
			}
			GridData lgd = new GridData(GridData.FILL_HORIZONTAL);
			lgd.horizontalSpan = 2;
			control.setLayoutData(lgd);
		}
		displayText = new Text(this, getBorderStyle());
		displayText.setEditable(false);
		displayText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		displayText.addModifyListener(displayTextModifyListener);
		updateLabel();
		dialogButton = new Button(this, SWT.PUSH);
		dialogButton.setText("...");
		dialogButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputTimePattern dlgResult = InputTimePatternDialog.open(dialogButton.getShell(), inputTimePattern);
				if (dlgResult != null) {
					inputTimePattern = dlgResult;
					updateLabel();
				}
			}
		});
		activeSelected();
	}

	/**
	 * Called to update the displayText with the current {@link InputTimePattern}.
	 */
	private void updateLabel() {
		if (inputTimePattern == null) {
			displayText.setText("No pattern defined");
		} else {
			displayText.setText(String.format("Pattern: %s", inputTimePattern.getDefinitionString()));
		}
	}

	/**
	 * Set the {@link InputTimePattern} this Composite should display and edit.
	 * 
	 * @param inputTimePattern The {@link InputTimePattern} to set.
	 */
	public void setInputTimePattern(InputTimePattern inputTimePattern) {
		refreshing = true;
		try {
			this.inputTimePattern = inputTimePattern;
			getDisplay().syncExec(new Runnable() {
				public void run() {
					updateLabel();
				}
			});
		} finally {
			refreshing = false;
		}
	}

	/**
	 * Returns the current {@link InputTimePattern} as set or defined by the user. Note that if this
	 * widget is not 'active', i.e. if {@link #isActive()} returns <code>false</code>, this method
	 * will return <code>null</code>.
	 * 
	 * @return the current {@link InputTimePattern}, might be <code>null</code>.
	 */
	public InputTimePattern getInputTimePattern() {
		if (isActive())
			return inputTimePattern;
		return null;
	};

	/**
	 * Add a ModifyListener to changes of the {@link InputTimePattern} this composite will return.
	 * Note that the event the {@link ModifyListener} receive might be <code>null</code>, and so it
	 * should not be accessed, but rather the {@link InputTimePatternEditComposite} itself.
	 * 
	 * TODO alex think about this and maybe an own listener type is better.
	 * 
	 * @param modifyListener The ModifyListener to add.
	 */
	public void addModifyListener(ModifyListener modifyListener) {
		modifyListeners.add(modifyListener);
	}
	
	/**
	 * Remove the given {@link ModifyListener} from the list of listeners.
	 * 
	 * @param modifyListener The event to remove.
	 */
	public void removeModifyListener(ModifyListener modifyListener) {
		modifyListeners.remove(modifyListener);
	}
	
	/**
	 * Notify all {@link ModifyListener}s of this object of a change.
	 * @param event The event, might be <code>null</code>
	 */
	protected void notifyModifyListener(ModifyEvent event) {
		Object[] listeners = modifyListeners.getListeners();
		for (Object listener : listeners) {
			if (listener instanceof ModifyListener) {
				((ModifyListener) listener).modifyText(event);
			}
		}
	}

	/**
	 * Checks the activation of the widget. Returns <code>true</code> when
	 * {@link #STYLE_SHOW_ACTIVE_CHECKBOX} was not used, otherwise it will check the selection-state
	 * of the checkbox controlling the enablement and return its value. Note that, if
	 * {@link #isActive()} returns <code>false</code> {@link #getInputTimePattern()} will be
	 * <code>null</code>
	 * 
	 * @return Whether this widget is active an the user can define an InputTimePattern.
	 */
	public boolean isActive() {
		return activeCheckbox == null ? true : activeCheckbox.getSelection();
	}

	/**
	 * If the style {@link #STYLE_SHOW_ACTIVE_CHECKBOX} was used, this method can be used to control
	 * the widgets activation-state. If a Composite is de-activated it will return <code>null</code>
	 * (=not defined) when asked for {@link #getInputTimePattern()}.
	 * 
	 * @param active Whether the widget should be active or not.
	 */
	public void setActive(boolean active) {
		if (this.activeCheckbox== null)
			return;

		this.activeCheckbox.setSelection(active);
		activeSelected();
	}
	
	/**
	 * Used internally to update enablement.
	 */
	private void activeSelected() {
		displayText.setEnabled(isActive());
		dialogButton.setEnabled(isActive());
	}

}
