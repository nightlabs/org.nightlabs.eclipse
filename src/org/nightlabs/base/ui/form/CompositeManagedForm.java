/**
 * 
 */
package org.nightlabs.base.ui.form;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * This class accompanies {@link CompositeFormPage} and provides its {@link IManagedForm} implementation. 
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
class CompositeManagedForm implements IManagedForm {

	private List<IFormPart> parts = new LinkedList<IFormPart>();
	private Object container;
	private Object input;
	private Composite composite;
	private FormToolkit toolkit;
	private boolean initialized = false;
	
	public CompositeManagedForm(Composite composite, FormToolkit toolkit) {
		this.composite = composite;
		this.toolkit = toolkit;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#addPart(org.eclipse.ui.forms.IFormPart)
	 */
	@Override
	public void addPart(IFormPart part) {
		parts.add(part);
	}

	/**
	 * Iterates all parts added to this {@link ManagedForm}
	 * and calls their commit method.
	 * 
	 * @see org.eclipse.ui.forms.IManagedForm#commit(boolean)
	 */
	@Override
	public void commit(boolean onSave) {
		for (IFormPart part : parts) {
			if (part.isDirty())
				part.commit(onSave);
		}
	}

	/**
	 * Noop!
	 * @see org.eclipse.ui.forms.IManagedForm#dirtyStateChanged()
	 */
	@Override
	public void dirtyStateChanged() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#fireSelectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void fireSelectionChanged(IFormPart part, ISelection selection) {
		for (IFormPart cpart : parts) {
			if (part.equals(cpart))
				continue;
			if (cpart instanceof IPartSelectionListener) {
				((IPartSelectionListener) cpart).selectionChanged(part,
						selection);
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#getContainer()
	 */
	@Override
	public Object getContainer() {
		return container;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#getForm()
	 */
	@Override
	public ScrolledForm getForm() {
		throw new UnsupportedOperationException("This implementation of ManagedForm (" + this.getClass().getSimpleName() + ") does not support this method");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#getInput()
	 */
	@Override
	public Object getInput() {
		return input;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#getMessageManager()
	 */
	@Override
	public IMessageManager getMessageManager() {
		throw new UnsupportedOperationException("This implementation of ManagedForm (" + this.getClass().getSimpleName() + ") does not support this method");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#getParts()
	 */
	@Override
	public IFormPart[] getParts() {
		return parts.toArray(new IFormPart[parts.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#getToolkit()
	 */
	@Override
	public FormToolkit getToolkit() {
		return toolkit;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#initialize()
	 */
	@Override
	public void initialize() {
		if (initialized)
			return;
		for (IFormPart part : parts) {
			part.initialize(this);
		}
		initialized = true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#isDirty()
	 */
	@Override
	public boolean isDirty() {
		for (IFormPart part : parts) {
			if (part.isDirty())
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#isStale()
	 */
	@Override
	public boolean isStale() {
		for (IFormPart part : parts) {
			if (part.isStale())
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#reflow(boolean)
	 */
	@Override
	public void reflow(boolean changed) {
		composite.layout(changed);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#refresh()
	 */
	@Override
	public void refresh() {
		Thread t = Thread.currentThread();
		Display display = composite.getDisplay();
		Thread dt = display.getThread();
		if (t.equals(dt))
			doRefresh();
		else {
			display.asyncExec(new Runnable() {
				public void run() {
					doRefresh();
				}
			});
		}
	}

	private void doRefresh() {
		boolean needsLayout = false;
		for (IFormPart part : parts) {
			if (part.isStale()) {
				part.refresh();
				needsLayout = true;
			}
		}
		if (needsLayout)
			reflow(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#removePart(org.eclipse.ui.forms.IFormPart)
	 */
	@Override
	public void removePart(IFormPart part) {
		parts.remove(part);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#setContainer(java.lang.Object)
	 */
	@Override
	public void setContainer(Object container) {
		this.container = container;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IManagedForm#setInput(java.lang.Object)
	 */
	@Override
	public boolean setInput(Object input) {
		boolean pageResult = false;
		this.input = input;
		for (IFormPart part : parts) {
			boolean result = part.setFormInput(input);
			if (result)
				pageResult = true;
		}
		return pageResult;
	}

	/**
	 * Noop!
	 * @see org.eclipse.ui.forms.IManagedForm#staleStateChanged()
	 */
	@Override
	public void staleStateChanged() {
	}

}
