package org.nightlabs.eclipse.ui.treestate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
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
	}

	private void saveTreeState(Tree tree) {
		StatableTree statableTree = statableTreeMap.get(tree);

		IScopeContext context = new ConfigurationScope();
		IEclipsePreferences rootNode = context.getNode(statableTree.getID());
		if (rootNode != null) {
			for (TreeItem treeItem : tree.getItems()) {
				rootNode.put(treeItem.getText(), treeItem.getExpanded()?"1":"0");

				try {
					rootNode.flush();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				createSubTreeState(treeItem, rootNode);
			}
		}
	}

	private void createSubTreeState(TreeItem treeItem, Preferences parentNode) {
		if (treeItem.getExpanded()) {
			for (TreeItem subTreeItem : treeItem.getItems()) {
				Preferences node = parentNode.node(treeItem.getText());
				node.put(subTreeItem.getText(), subTreeItem.getExpanded()?"1":"0");

				try {
					node.flush();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				createSubTreeState(subTreeItem, node);
			}
		}
		else {
			try {
				parentNode.node(treeItem.getText()).removeNode();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private long currentTime;
	private TimerTask timerTask;

	/**
	 * Restores the tree's expansion states. This method uses values from {@link PreferenceStore}
	 * and {@link org.nightlabs.eclipse.ui.treestate.preferences.Preferences} for specifying the time 
	 * for trying to get the tree items and the total time to try getting them because the tree loads its
	 * items lazily.
	 * 
	 * @param tree
	 */
	public void loadTreeState(final Tree tree) {
		IPreferenceStore preferenceStore = org.nightlabs.eclipse.ui.treestate.preferences.Preferences.getPreferenceStore();
		boolean isEnable = preferenceStore.getBoolean(org.nightlabs.eclipse.ui.treestate.preferences.Preferences.PREFERENCE_ENABLE_STATE);

		if (isEnable) {
			final long relativeTime = 
				preferenceStore.getLong(org.nightlabs.eclipse.ui.treestate.preferences.Preferences.PREFERENCE_RELATIVE_TIME) * 1000;
			final long absoluteTime = 
				preferenceStore.getLong(org.nightlabs.eclipse.ui.treestate.preferences.Preferences.PREFERENCE_ABSOLUTE_TIME) * 1000;
			
			final StatableTree statableTree = statableTreeMap.get(tree);

			final IPreferencesService preferencesService = Platform.getPreferencesService();
			final Preferences startNode = preferencesService.getRootNode().node(ConfigurationScope.SCOPE).node(statableTree.getID());

			final Timer timer = new Timer();
			timerTask = new TimerTask() {
				@Override
				public void run() {
					if (currentTime < absoluteTime) {
						if (tree != null && !tree.isDisposed()) {
							tree.getDisplay().asyncExec(new Runnable() {
								@Override
								public void run() {
									for (TreeItem treeItem : tree.getItems()) {
										boolean isExpanded = startNode.getInt(treeItem.getText(), 0) == 1;
										if (isExpanded) {
											sendEventExpandTreeItem(treeItem);
											treeItem.setExpanded(true);

											Preferences node = startNode.node(treeItem.getText());
											loadSubTreeState(treeItem, node);
										}
									}

									currentTime += relativeTime;
									loadTreeState(tree);
								}
							});
						}
					}
					else {
						timer.cancel();
						currentTime = 0;
					}
				}
			};

			timer.schedule(timerTask, relativeTime);
		}
	}

	private void loadSubTreeState(TreeItem parentItem, Preferences parentNode) {
		for (TreeItem subTreeItem : parentItem.getItems()) {
			Preferences subNode = parentNode.node(subTreeItem.getText());
			boolean isExpanded = parentNode.getInt(subTreeItem.getText(), 0) == 1;
			if (isExpanded) {
				sendEventExpandTreeItem(subTreeItem);
				subTreeItem.setExpanded(true);
				loadSubTreeState(subTreeItem, subNode);
			}
		}
	}

	private void sendEventExpandTreeItem(TreeItem item) {
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
	}
}