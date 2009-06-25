/**
 * 
 */
package org.nightlabs.base.ui.composite;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

/**
 * A Composite that will present a list of elements (input) as Radio-Buttons
 * and enables the user to choose one element out of the input.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class RadioGroupComposite<ElementType> extends XComposite implements ISelectionProvider {

	private LabelProvider labelProvider;
	private Group group;
	private Collection<ElementType> input;
	private ElementType selectedElement;
	private String title;
	
	private ListenerList listeners = new ListenerList();
	
	private SelectionListener selectionListener = new SelectionAdapter() {
		@SuppressWarnings("unchecked")
		@Override
		public void widgetSelected(SelectionEvent evt) {
			if (evt.widget instanceof Button) {
				if (((Button) evt.widget).getSelection()) {
					try {
						selectedElement = (ElementType) evt.widget.getData();
						fireSelectionChanged();
					} catch (ClassCastException e) {
						selectedElement = null;
					}
				}
			}
		}
	};
	
	/**
	 * Create a new {@link RadioGroupComposite}.
	 * 
	 * @param parent The parent to use.
	 * @param style The style of the Composite.
	 */
	public RadioGroupComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		init();
	}

	/**
	 * Create a new {@link RadioGroupComposite}.
	 * 
	 * @param parent The parent to use.
	 * @param style The style of the Composite.
	 * @param layoutDataMode The {@link LayoutDataMode} to use.
	 */
	public RadioGroupComposite(Composite parent, int style,
			LayoutDataMode layoutDataMode) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER, layoutDataMode);
		init();
	}
	
	/**
	 * Creates the group that will be parent of all created radio-buttons.
	 */
	protected void init() {
		group = new Group(this, SWT.NONE);
		group.setLayout(XComposite.getLayout(LayoutMode.ORDINARY_WRAPPER));
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		if (title != null)
			group.setText(title);
	}
	
	/**
	 * Disposes off all children of the group.
	 */
	protected void clearGroup() {
		Control[] children = group.getChildren();
		for (Control child : children) {
			child.dispose();
		}
	}
	
	public void setTitle(String title) {
		if (title != null) {
			if (group != null)
				group.setText(title);
		} else {
			if (group != null) 
				group.setText(title);
		}
		this.title = title;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation prevents that a Layout can be set from outside.
	 * </p>
	 */
	@Override
	public void setLayout(Layout layout) {
		super.setLayout(layout);
	}

	/**
	 * Set the {@link LabelProvider} that will be invoked for ever element
	 * set in {@link #setInput(Collection)}.
	 * 
	 * @param labelProvider The label provider to use.
	 */
	public void setLabelProvider(LabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}
	
	/**
	 * @return The label provider.
	 */
	public LabelProvider getLabelProvider() {
		return labelProvider;
	}
	
	/**
	 * Set the elements this composite should create radio-buttons for.
	 * The user will be able to select one out of the given elements.
	 * 
	 * @param input The input to display.
	 */
	public void setInput(Collection<ElementType> input) {
		if (labelProvider == null)
			throw new IllegalStateException("The label provider has to be set when input is set.");
		this.input = input;
		clearGroup();
		for (ElementType element : input) {
			Button option = new Button(group, SWT.RADIO);
			option.setLayoutData(new GridData());
			option.setText(labelProvider.getText(element));
//			option.setText("Test");
			option.setData(element);
			option.addSelectionListener(selectionListener);
		}
		this.layout(true, true);
	}
	
	/**
	 * @return The current input of this Composite.
	 */
	public Collection<ElementType> getInput() {
		return input;
	}
	
	/**
	 * @return The currently selected element.
	 */
	public ElementType getSelectedElement() {
		return selectedElement;
	}

	/**
	 * Set the currently selected element. 
	 * @param elementType The element to select.
	 */
	public void setSelectedElement(ElementType elementType) {
		Control[] children = group.getChildren();
		for (Control child : children) {
			if (child instanceof Button) {
				if (child.getData() != null) {
					((Button) child).setSelection(child.getData().equals(elementType));
				}
			}
		}
		fireSelectionChanged();
	}

	/**
	 * Fires a selection changed event.
	 */
	private void fireSelectionChanged() {
		if (selectedElement == null)
			return;
		Object[] l = listeners.getListeners();
		for (Object listener : l) {
			if (listener instanceof ISelectionChangedListener) {
				((ISelectionChangedListener) listener).selectionChanged(new SelectionChangedEvent(this, new StructuredSelection(selectedElement)));
			}
		}
	}

	/**
	 * Add a listener that will be invoked when the selection changes.
	 * @param listener The listener to add.
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove the listener from the list of listeners.
	 * @param listener The listener to remove.
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);
	}
	
	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.open();
		shell.setLayout(new GridLayout());
		RadioGroupComposite<String> test = new RadioGroupComposite<String>(shell, SWT.NONE);
		test.setLabelProvider(new LabelProvider() {
		});
		test.setInput(Arrays.asList(new String[] {"Test1", "Test2", "Test3"}));
		test.setTitle("My Group");
		shell.layout(true, true);
		while (!shell.isDisposed()) {
			d.readAndDispatch();
		}
	}

	@Override
	public ISelection getSelection() {
		if (selectedElement != null)
			return new StructuredSelection(selectedElement);
		return new StructuredSelection();
	}

	@Override
	public void setSelection(ISelection selection) {
		if (selection instanceof StructuredSelection) {
			try {
				ElementType selectElement = (ElementType) ((StructuredSelection) selection).getFirstElement();
				this.selectedElement = selectElement;
			} catch (ClassCastException e) {
			}
		}
	}
}
