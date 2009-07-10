package org.nightlabs.history;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.nightlabs.base.ui.part.PartAdapter;
import org.nightlabs.base.ui.util.RCPUtil;

/**
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

	private IWorkbench workbench;
	private List<IEditorHistoryItem> historyItems;
	private int currentIndex = 0;
	private IPartListener2 partListener = new PartAdapter() {
		/* (non-Javadoc)
		 * @see org.nightlabs.base.ui.part.PartAdapter#partActivated(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partActivated(IWorkbenchPartReference partRef) {
			IWorkbenchPart part = partRef.getPart(false);
			if (part instanceof IEditorPart) {
				IEditorPart editorPart = (IEditorPart) part;
				IEditorHistoryItem item = getHistoryItem(editorPart);
				if (item == null) {
					item = new EditorHistoryItem(partRef.getId(), editorPart.getEditorInput(), getCurrentPerspectiveID());
					historyItems.add(item);
					currentIndex++;
				}
			}
		}
	};

	private EditorHistory() {
		historyItems = new ArrayList<IEditorHistoryItem>();
	}

	private boolean listenerAdded = false;
	private boolean check()
	{
		if (workbench == null) {
			throw new IllegalStateException("Workbencj is notset, call setWorkbench() first!");
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
//		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId();
		return RCPUtil.getActivePerspectiveID();
	}

	private IEditorHistoryItem getHistoryItem(IEditorPart editorPart) {
		for (IEditorHistoryItem item : historyItems) {
			if (item.getEditorInput().equals(editorPart.getEditorInput())) {
				return item;
			}
		}
		return null;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public int getHistorySize() {
		return historyItems.size();
	}

	public void historyForward() {
		if (canForward()) {
			currentIndex++;
			IEditorHistoryItem item = historyItems.get(currentIndex);
			showHistoryItem(item);
		}
	}

	public void historyBack() {
		if (canBackward()) {
			currentIndex--;
			IEditorHistoryItem item = historyItems.get(currentIndex);
			showHistoryItem(item);
		}
	}

	public boolean canForward() {
		boolean canForward = historyItems.size() > currentIndex + 1 && !historyItems.isEmpty();
		if (logger.isDebugEnabled()) {
			logger.debug("currentIndex = "+currentIndex);
			logger.debug("historyItems.size() = "+historyItems.size());
			logger.debug("canForward = "+canForward);
		}
		return canForward;
	}

	public boolean canBackward() {
		boolean canBackward = currentIndex - 1 >= 0 && !historyItems.isEmpty();
		if (logger.isDebugEnabled()) {
			logger.debug("currentIndex = "+currentIndex);
			logger.debug("historyItems.size() = "+historyItems.size());
			logger.debug("canBackward = "+canBackward);
		}
		return canBackward;
	}

	protected void showHistoryItem(IEditorHistoryItem item) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("showHistoryItem()");
				logger.debug("item.getEditorId() = "+item.getEditorId());
				logger.debug("item.getEditorInput() = "+item.getEditorInput());
				logger.debug("item.getPerspectiveId() = "+item.getPerspectiveId());
			}
			workbench.showPerspective(item.getPerspectiveId(), workbench.getActiveWorkbenchWindow());
			for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
				for (IWorkbenchPage page : window.getPages()) {
					// Does not work
					IEditorPart editorPart = page.findEditor(item.getEditorInput());
					if (editorPart != null) {
//						workbench.showPerspective(item.getPerspectiveId(), window);
						page.activate(editorPart);
						break;
					}

//					// Does not work
//					IEditorReference[] editorReferences = page.getEditorReferences();
//					for (IEditorReference editorReference : editorReferences) {
//						if (logger.isDebugEnabled()) {
//							logger.debug("editorReference.getId() = "+editorReference.getId());
//						}
//						if (editorReference.getId().equals(item.getEditorId())) {
//							if (logger.isDebugEnabled()) {
//								logger.debug("editorReference.getId().equals(item.getEditorId()");
//							}
//							if (editorReference.getEditorInput().equals(item.getEditorInput())) {
//								workbench.showPerspective(item.getPerspectiveId(), window);
//								page.activate(editorReference.getPart(false));
//								if (logger.isDebugEnabled()) {
//									logger.debug("perspectiveID = "+item.getPerspectiveId());
//									logger.debug("editorID = "+item.getEditorId());
//									logger.debug("editorReference = "+editorReference);
//								}
//								break;
//							}
//						}
//					}

				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
