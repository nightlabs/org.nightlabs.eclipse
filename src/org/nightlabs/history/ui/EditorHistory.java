package org.nightlabs.history.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Editor-History which tracks all activated editors.,
 * In contrast to the default org.eclipse.ui.internal.NavigationHistory
 * it also tracks the corresponding perspective where the editor was opened and
 * opens it again, when the corresponding history item will be displayed.
 * The history also provides a forward and backward action.
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditorHistory
{
	private static final Logger logger = Logger.getLogger(EditorHistory.class);

	private static EditorHistory sharedInstance;

	public static EditorHistory sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new EditorHistory();
		}
		return sharedInstance;
	}

//	private IContributionItem forwardAction;
//	private IContributionItem backAction;
	private IAction forwardAction;
	private IAction backAction;

	private IWorkbench workbench;
	private List<IEditorHistoryItem> historyItems;
	private int currentIndex = 0;
	private boolean forwardPressed;
	private boolean backPressed;

	private ListenerList listeners;

	private IPartListener2 partListener = new IPartListener2() {
		/* (non-Javadoc)
		 * @see org.nightlabs.base.ui.part.PartAdapter#partActivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partActivated(IWorkbenchPartReference partRef) {
			IWorkbenchPart part = partRef.getPart(false);
			if (part instanceof IEditorPart) {
				IEditorPart editorPart = (IEditorPart) part;
				// user clicked manually on the editor tab
				if (!forwardPressed  && !backPressed) {
					IEditorHistoryItem item = new EditorHistoryItem(partRef.getId(), editorPart.getEditorInput(), getCurrentPerspectiveID());
					historyItems.add(item);
					currentIndex = historyItems.indexOf(item);
				}
				updateActions();
				fireEventHistoryEvent();
			}
		}
		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partClosed(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partHidden(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partInputChanged(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partOpened(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		}

	};

	private EditorHistory() {
		historyItems = new ArrayList<IEditorHistoryItem>();
//		forwardAction = new EditorHistoryForwardContributionItem();
//		backAction = new EditorHistoryBackContributionItem();
		forwardAction = new EditorHistoryForwardAction();
		backAction = new EditorHistoryBackAction();
		listeners = new ListenerList();
		updateActions();
	}

	private boolean listenerAdded = false;
	private boolean check()
	{
		if (workbench == null) {
			throw new IllegalStateException("Workbench is not set, call setWorkbench() first!"); //$NON-NLS-1$
		}
		if (!listenerAdded) {
			for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
				for (IWorkbenchPage page : window.getPages()) {
					page.addPartListener(partListener);
				}
	    	 }
			listenerAdded = true;
		}
		return listenerAdded;
	}

	/**
	 * Sets the workbench.
	 * @param workbench the workbench to set
	 */
	public void setWorkbench(IWorkbench workbench) {
		this.workbench = workbench;
		check();
	}

	private String getCurrentPerspectiveID() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId();
	}

//	private IEditorHistoryItem getHistoryItem(IEditorPart editorPart) {
//		for (IEditorHistoryItem item : historyItems) {
//			if (item.getEditorInput().equals(editorPart.getEditorInput())) {
//				return item;
//			}
//		}
//		return null;
//	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public int getHistorySize() {
		return historyItems.size();
	}

	public void historyForward() {
		if (canForward()) {
			forwardPressed = true;
			currentIndex++;
			IEditorHistoryItem item = historyItems.get(currentIndex);
			showHistoryItem(item);
			forwardPressed = false;
		}
	}

	public void historyBack() {
		if (canBackward()) {
			backPressed = true;
			currentIndex--;
			IEditorHistoryItem item = historyItems.get(currentIndex);
			showHistoryItem(item);
			backPressed = false;
		}
	}

	public boolean canForward() {
		boolean canForward = historyItems.size() > currentIndex + 1;
		if (logger.isDebugEnabled()) {
			logger.debug("currentIndex = "+currentIndex); //$NON-NLS-1$
			logger.debug("historyItems.size() = "+historyItems.size()); //$NON-NLS-1$
			logger.debug("canForward = "+canForward); //$NON-NLS-1$
		}
		return canForward;
	}

	public boolean canBackward() {
		boolean canBackward = currentIndex - 1 >= 0;
		if (logger.isDebugEnabled()) {
			logger.debug("currentIndex = "+currentIndex); //$NON-NLS-1$
			logger.debug("historyItems.size() = "+historyItems.size()); //$NON-NLS-1$
			logger.debug("canBackward = "+canBackward); //$NON-NLS-1$
		}
		return canBackward;
	}

	private void showHistoryItem(IEditorHistoryItem item) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("showHistoryItem()"); //$NON-NLS-1$
				logger.debug("item.getEditorId() = "+item.getEditorId()); //$NON-NLS-1$
				logger.debug("item.getEditorInput() = "+item.getEditorInput()); //$NON-NLS-1$
				logger.debug("item.getPerspectiveId() = "+item.getPerspectiveId()); //$NON-NLS-1$
			}
			workbench.showPerspective(item.getPerspectiveId(), workbench.getActiveWorkbenchWindow());
			for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
				for (IWorkbenchPage page : window.getPages()) {
					IEditorPart editorPart = page.findEditor(item.getEditorInput());
					if (editorPart != null) {
						page.activate(editorPart);
						break;
					}
					// editor could not be found (possibly closed)
					if (editorPart == null) {
						editorPart = page.openEditor(item.getEditorInput(), item.getEditorId());
						break;
					}
				}
			}
			updateActions();
			fireEventHistoryEvent();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

//	private void updateActions() {
//		if (forwardAction != null) {
//			forwardAction.update();
//		}
//		if (backAction != null) {
//			backAction.update();
//		}
//	}
//
//	/**
//	 * Returns the forwardAction.
//	 * @return the forwardAction
//	 */
//	public IContributionItem getForwardAction() {
//		return forwardAction;
//	}
//
//	/**
//	 * Returns the backAction.
//	 * @return the backAction
//	 */
//	public IContributionItem getBackAction() {
//		return backAction;
//	}

	private void updateActions() {
		if (forwardAction != null) {
			forwardAction.setEnabled(canForward());
		}
		if (backAction != null) {
			backAction.setEnabled(canBackward());
		}
	}

	/**
	 * Returns the forwardAction.
	 * @return the forwardAction
	 */
	public IAction getForwardAction() {
		return forwardAction;
	}

	/**
	 * Returns the backAction.
	 * @return the backAction
	 */
	public IAction getBackAction() {
		return backAction;
	}

	public List<IEditorHistoryItem> getBackItems() {
		 List<IEditorHistoryItem> backItems = new ArrayList<IEditorHistoryItem>(currentIndex);
		 for (int i = 0; i < currentIndex; i++) {
			 backItems.add(historyItems.get(i));
		 }
		 return backItems;
	}

	public List<IEditorHistoryItem> getForwardItems() {
		 List<IEditorHistoryItem> forwardItems = new ArrayList<IEditorHistoryItem>();
		 for (int i = currentIndex+1; i < historyItems.size(); i++) {
			 forwardItems.add(historyItems.get(i));
		 }
		 return forwardItems;
	}

	public void addEventHistoryListener(IEditorHistoryChangedListener listener) {
		listeners.add(listener);
	}

	public void removeEventHistoryListener(IEditorHistoryChangedListener listener) {
		listeners.remove(listener);
	}

	private void fireEventHistoryEvent() {
		EditorHistoryChangedEvent event = new EditorHistoryChangedEvent(this);
		for (Object o : listeners.getListeners()) {
			IEditorHistoryChangedListener listener = (IEditorHistoryChangedListener) o;
			listener.editorHistoryChanged(event);
		}
	}

}
