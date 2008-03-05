/**
 * 
 */
package org.nightlabs.base.ui.entity.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.util.RCPUtil;

/**
 * Each {@link EntityEditor} has a {@link EntityEditorStaleHandler}
 * that accepts {@link IEntityEditorPageStaleHandler}s to be registered
 * to. If a handler receives such stale handlers it will either
 * display them in a dialog at once (when the editor has the focus) or
 * the display of the dialog will be scheduled for the next
 * time the editor gets the focus.
 * <p>
 * The dialog will display each {@link IEntityEditorPageStaleHandler}
 * in a tree as top-level node. The {@link IEntityEditorPageStaleHandler}s
 * LabelProvider will be used to display the top-level-row. As children
 * of each handler all its pages will be displayed as well.
 * </p>
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class EntityEditorStaleHandler {
	/**
	 * The class used to display registered {@link IEntityEditorPageStaleHandler}s.
	 */
	class StaleHandlerDialog extends CenteredDialog {

		private EntityEditorStaleHandlerTree handlerTree;
		
		public StaleHandlerDialog(Shell parentShell) {
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.RESIZE);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA);
			Label label = new Label(wrapper, SWT.BOLD | SWT.WRAP);
			label.setText(
					String.format(Messages.getString("org.nightlabs.base.ui.entity.editor.EntityEditorStaleHandler.label.text"), //$NON-NLS-1$
							getEntityEditor().getPartName()
					)
			);
			GridData gd = new GridData();
			label.setLayoutData(gd);
			
			handlerTree = new EntityEditorStaleHandlerTree(wrapper);
			handlerTree.setInput(staleHandlers);
			handlerTree.getTreeViewer().expandAll();
			
			return wrapper;
		}
		
		public void refreshTree() {
			if (!handlerTree.isDisposed()) {
				handlerTree.refresh(true);
				handlerTree.getTreeViewer().expandAll();
			}
		}
		
		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText(Messages.getString("org.nightlabs.base.ui.entity.editor.EntityEditorStaleHandler.dialog.title")); //$NON-NLS-1$
			setToCenteredLocationPreferredSize(newShell, 400, 300);
		}
	}

	/**
	 * The entity editor this handler is asssociated to.
	 */
	private EntityEditor entityEditor;
	/**
	 * The registered stale handlers.
	 */
	private List<IEntityEditorPageStaleHandler> staleHandlers = new ArrayList<IEntityEditorPageStaleHandler>();
	/**
	 * Tracks whether the shell the associated editor is displayed in was deactivated.
	 */
	private boolean shellDeactivated = false;
	/**
	 * Used to synchronize access to {@link #staleHandlers}.
	 */
	private Object mutex = new Object();
	/**
	 * There is only one dialog shown per editor. The visible instance is registered here.
	 */
	private StaleHandlerDialog staleHandlerDialog = null;
	
	/**
	 * Create a new {@link EntityEditorStaleHandler} for the given editor.
	 * <p>
	 * It will register listeners to track the focus of the editor and
	 * whether the shell is active at all.
	 * </p>
	 * @param entityEditor The editor the new handler is associated to.
	 */
	public EntityEditorStaleHandler(EntityEditor entityEditor) {
		this.entityEditor = entityEditor;
		
		if (getEntityEditor() != null) {
			final ShellListener shellListener = new ShellListener() {
				@Override
				public void shellActivated(ShellEvent e) {
					shellDeactivated = false;
					if (RCPUtil.getActiveWorkbenchPage() != null) {
						IEditorPart part = RCPUtil.getActiveWorkbenchPage().getActiveEditor();
						if (getEntityEditor().equals(part)) {
							handleEditorActivated();
						}
					}
				}
				@Override
				public void shellClosed(ShellEvent e) {
				}
				@Override
				public void shellDeactivated(ShellEvent e) {
					shellDeactivated = true;
				}
				@Override
				public void shellDeiconified(ShellEvent e) {
				}
				@Override
				public void shellIconified(ShellEvent e) {
				}
			};
			
			// add a part listener that will react on the activation of the
			// associated editor and notify the user of a change if necessary.
			RCPUtil.getActiveWorkbenchPage().addPartListener(new IPartListener() {
				@Override
				public void partActivated(final IWorkbenchPart part) {
					if (part.equals(getEntityEditor())) {
						// part was activated, handle notify the user if necessary
						handleEditorActivated();
					}
				}
				@Override
				public void partBroughtToTop(final IWorkbenchPart part) {
				}
				@Override
				public void partClosed(final IWorkbenchPart part) {
					if (part.equals(getEntityEditor())) {
						// part was closed, remove the part listener
						IWorkbenchPage page = RCPUtil.getActiveWorkbenchPage();
						if (page != null)
							page.removePartListener(this);
						Shell workbenchShell = RCPUtil.getActiveWorkbenchShell();
						if (workbenchShell != null)
							workbenchShell.removeShellListener(shellListener);
					}
				}
				@Override
				public void partDeactivated(final IWorkbenchPart part) {
				}
				@Override
				public void partOpened(final IWorkbenchPart part) {
				}
			});
			// also add a shell listener so that when the
			// notification happens when the shell does not
			// have the focus, the handler is invoked
			// when it gets the focus again
			RCPUtil.getActiveWorkbenchShell().addShellListener(shellListener);
		}
	}
	
	/**
	 * Get the {@link EntityEditor} this handler is associated with.
	 * @return The {@link EntityEditor} this handler is associated with.
	 */
	public EntityEditor getEntityEditor() {
		return entityEditor;
	}
	
	/**
	 * This method is called when the associated Editor
	 * is activated and will run the {@link #handleEntityChangeRunnable} if set.
	 */
	protected void handleEditorActivated() {
		boolean openDialog = false;
		synchronized (mutex) {
			if (staleHandlers != null && staleHandlers.size() > 0 && staleHandlerDialog == null) {
				staleHandlerDialog = new StaleHandlerDialog(RCPUtil.getActiveShell());
				openDialog = true;
			}
		}
		if (openDialog) {
			if (staleHandlerDialog.open() == Window.OK) {
				staleHandlerDialog = null;
				handleDialogClosed();
			}
		} else if (staleHandlerDialog != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (staleHandlerDialog != null)
					staleHandlerDialog.refreshTree();
				}
			});
		}
	}

	/**
	 * Handles when the dialog is closed (with OK).
	 * It will run all registered {@link IEntityEditorPageStaleHandler}s.
	 */
	protected void handleDialogClosed() {
		synchronized (mutex) {
			if (staleHandlers == null)
				return;
			for (Iterator<IEntityEditorPageStaleHandler> it = staleHandlers.iterator(); it.hasNext();) {
				IEntityEditorPageStaleHandler handler = it.next();
				handler.run();
				it.remove();
			}
		}
	}
	
	/**
	 * Adds a {@link IEntityEditorPageStaleHandler} to this handler.
	 * If the associated editor is currently active the dialog
	 * will be shown right away, if not it will be scheduled to be shown
	 * when the editor gets active again.
	 * <p>
	 * If a handler for the page controller of the given handler
	 * was already registered, it will be replaced with the given one.
	 * </p>
	 * @param staleHandler The handler to add.
	 */
	public void addEntityEdiorStaleHandler(IEntityEditorPageStaleHandler staleHandler) {
		final IEditorPart[] activeEditor = new IEditorPart[1];
		synchronized (mutex) {
			for (int i = 0; i < staleHandlers.size(); i++) {
				IEntityEditorPageStaleHandler handler = staleHandlers.get(i);
				if (handler.getPageController().equals(staleHandler.getPageController())) {
					staleHandlers.set(i, staleHandler);
					return;
				}
			}
			staleHandlers.add(staleHandler);
			// check if the editor has focus and the
			// handlers have to be presented to the user right away.
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					Shell shell = RCPUtil.getActiveShell();
					if (shell != null) {
						if (shellDeactivated)
							return;
					}
					IWorkbenchPage workbenchPage = RCPUtil.getActiveWorkbenchPage();
					if (workbenchPage != null)
						activeEditor[0] = workbenchPage.getActiveEditor();
				}
			});
		}
		if (activeEditor[0] != null && activeEditor[0] == getEntityEditor()) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					handleEditorActivated();
				}
			});
		}
	}
}
