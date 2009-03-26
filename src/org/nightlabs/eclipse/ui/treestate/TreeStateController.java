package org.nightlabs.eclipse.ui.treestate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.osgi.service.prefs.Preferences;

/**
 * This class is used for storing and restoring any trees' expansion states.
 * <p>
 * To use this controller, the tree has to implement the {@link StatableTree} interface
 * ,so that the controller will be able to identify the tree by its ID.
 * </p>
 *
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class TreeStateController
{
	private static TreeStateController sharedInstance;

	private Map<Tree, StatableTree> statableTreeMap;

	private Set<TreeItem> collapsedItems = new HashSet<TreeItem>();

	private TreeStateController() {}

	/**
	 * @return a shared instance of the controller
	 */
	public static TreeStateController sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new TreeStateController();
		}
		return sharedInstance;
	}

	/**
	 * Adds a {@link StatableTree} for storing and restoring its expansion states.
	 * 
	 * This method uses the {@link ConfigurationScope} to get the workspaces' shared preferences.
	 * 
	 * @param statableTree
	 */
	public void registerTree(StatableTree statableTree) {
		if (statableTreeMap == null) {
			statableTreeMap = new HashMap<Tree, StatableTree>();
		}
		statableTreeMap.put(statableTree.getTree(), statableTree);

		statableTree.getTree().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				IPreferenceStore preferenceStore = org.nightlabs.eclipse.ui.treestate.preferences.Preferences.getPreferenceStore();
				boolean isEnable = preferenceStore.getBoolean(org.nightlabs.eclipse.ui.treestate.preferences.Preferences.PREFERENCE_ENABLE_STATE);

				if (isEnable) {
					Tree sourceTree = (Tree)e.getSource();
					saveTreeState(sourceTree);
				}
			}
		});

		//Adds a tree listener for recording the collapsed tree items and these items will not be auto-expanded
		statableTree.getTree().addTreeListener(new TreeAdapter() {
			@Override
			public void treeCollapsed(TreeEvent e) {
				Tree tree = (Tree)e.getSource();
				if (tree.getSelection().length > 0) {
					TreeItem collapsedTreeItem = tree.getSelection()[0];
					collapsedItems.add(collapsedTreeItem);					
				}
			}
		});
	}

	private void saveTreeState(Tree tree) {
		StatableTree statableTree = statableTreeMap.get(tree);

		IScopeContext context = new ConfigurationScope();
		IEclipsePreferences rootNode = context.getNode(statableTree.getID());
		if (rootNode != null) {
			try {
				rootNode.clear();
				
				for (TreeItem treeItem : tree.getItems()) {
					saveTreeItemState(treeItem, rootNode);
				}
				
				rootNode.flush();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void saveTreeItemState(TreeItem treeItem, Preferences parentNode) {
		if (!treeItem.getExpanded())
			return;

		parentNode.put(treeItem.getText(), "1"); // treeItem.getExpanded()?"1":"0");

		for (TreeItem subTreeItem : treeItem.getItems()) {
			Preferences node = parentNode.node(treeItem.getText());

			saveTreeItemState(subTreeItem, node);
		}
	}

	private long totalTryingTime;
	private TimerTask expandTreeItemTimerTask;

	/**
	 * Restores the tree's expansion states. 
	 * 
	 * This method uses values from {@link PreferenceStore} and 
	 * {@link org.nightlabs.eclipse.ui.treestate.preferences.Preferences} for specifying the time 
	 * for trying to get the tree items and the total time to try getting them because 
	 * the tree loads its items lazily.
	 * 
	 * Calling this method after setting input to the tree is a suggestion.
	 * 
	 * @param tree
	 */
	public void loadTreeState(final Tree tree) {
		IPreferenceStore preferenceStore = org.nightlabs.eclipse.ui.treestate.preferences.Preferences.getPreferenceStore();
		boolean isEnable = preferenceStore.getBoolean(org.nightlabs.eclipse.ui.treestate.preferences.Preferences.PREFERENCE_ENABLE_STATE);

		if (isEnable) {
			final long nextTryingToGetTreeItemTime = 
				preferenceStore.getLong(org.nightlabs.eclipse.ui.treestate.preferences.Preferences.PREFERENCE_RELATIVE_TIME);
			final long totalTryingToGetTreeItemTime = 
				preferenceStore.getLong(org.nightlabs.eclipse.ui.treestate.preferences.Preferences.PREFERENCE_ABSOLUTE_TIME);

			final StatableTree statableTree = statableTreeMap.get(tree);

			final IPreferencesService preferencesService = Platform.getPreferencesService();
			final Preferences startNode = preferencesService.getRootNode().node(ConfigurationScope.SCOPE).node(statableTree.getID());

			final Timer expandTreeItemTimer = new Timer();
			expandTreeItemTimerTask = new TimerTask() {
				@Override
				public void run() {
					if (totalTryingTime < totalTryingToGetTreeItemTime) {
						if (tree != null && !tree.isDisposed()) {
							tree.getDisplay().asyncExec(new Runnable() {
								@Override
								public void run() {
									if (tree != null && !tree.isDisposed()) {
										for (TreeItem treeItem : tree.getItems()) {
											boolean isExpanded = startNode.getInt(treeItem.getText(), 0) == 1;
											if (isExpanded) {
												sendEventExpandTreeItem(treeItem);

												Preferences node = startNode.node(treeItem.getText());
												loadSubTreeState(treeItem, node);
											}
										}

										totalTryingTime += nextTryingToGetTreeItemTime;
										loadTreeState(tree);
									}
								}
							});
						}
					}
					else {
						expandTreeItemTimer.cancel();
						totalTryingTime = 0;
					}
				}
			};

			expandTreeItemTimer.schedule(expandTreeItemTimerTask, nextTryingToGetTreeItemTime);
		}
	}

	private void loadSubTreeState(TreeItem parentItem, Preferences parentNode) {
		if (!parentItem.isDisposed()) {
			for (TreeItem subTreeItem : parentItem.getItems()) {
				Preferences subNode = parentNode.node(subTreeItem.getText());
				boolean isExpanded = parentNode.getInt(subTreeItem.getText(), 0) == 1;
				if (isExpanded) {
					sendEventExpandTreeItem(subTreeItem);
					loadSubTreeState(subTreeItem, subNode);
				}
			}
		}
	}

	private void sendEventExpandTreeItem(TreeItem item) {
		//Checks whether the item's already collapsed
		if (!collapsedItems.contains(item)) {
			Event event = new Event();
			event.type = SWT.Expand;
			event.item = item;
			event.widget = item.getParent();
			Method method;
			try {
				method = Widget.class.getDeclaredMethod("sendEvent", new Class[] {int.class, Event.class});
				method.setAccessible(true);
				method.invoke(item.getParent(), event.type, event);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

			item.setExpanded(true);
		}
	}
}