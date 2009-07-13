package org.nightlabs.history;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public abstract class AbstractEditorHistoryContributionItem
extends ContributionItem
{
	/**
	 *
	 */
	public AbstractEditorHistoryContributionItem(String id) {
		super(id);
	}

	private Button button;
	private CoolItem coolItem;
	private MenuItem menuItem;
	private ToolItem toolItem;
	private Image image = null;

	protected abstract String getText();

	protected abstract String getToolTip();

	protected abstract void run();

	protected abstract Image createImage();

	@Override
	public abstract boolean isEnabled();

	protected Image getImage()
	{
		if (image == null){
			image = createImage();
		}
		return image;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void fill(Composite parent) {
		button = new Button(parent, SWT.FLAT);
		button.setText(getText());
		button.setToolTipText(getToolTip());
		button.setImage(getImage());
		button.addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				run();
			}
		});
		update();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.CoolBar, int)
	 */
	@Override
	public void fill(CoolBar parent, int index) {
		coolItem = new CoolItem(parent, SWT.NONE);
		coolItem.setText(getText());
		coolItem.setImage(getImage());
		coolItem.addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				run();
			}
		});
		update();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Menu, int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText(getText());
		menuItem.setImage(getImage());
		menuItem.addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				run();
			}
		});
		update();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.ToolBar, int)
	 */
	@Override
	public void fill(ToolBar parent, int index) {
		toolItem = new ToolItem(parent, SWT.PUSH);
		toolItem.setText(getText());
		toolItem.setToolTipText(getToolTip());
		toolItem.setImage(getImage());
		toolItem.addSelectionListener(new SelectionAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				run();
			}
		});
		update();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.ContributionItem#update()
	 */
	@Override
	public void update() {
		if (button != null) {
			button.setEnabled(isEnabled());
		}
		if (menuItem != null) {
			menuItem.setEnabled(isEnabled());
		}
		if (toolItem != null) {
			toolItem.setEnabled(isEnabled());
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (image != null) {
			image.dispose();
		}
		if (button != null) {
			button.dispose();
		}
		if (menuItem != null) {
			menuItem.dispose();
		}
		if (toolItem != null) {
			toolItem.dispose();
		}
	}
}
