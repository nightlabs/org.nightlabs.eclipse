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
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.osgi.service.prefs.Preferences;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class TreeStateController
{
	private static TreeStateController sharedInstance;

	private Map<Tree, StatableTree> statableTreeMap;

	private TreeStateController() {}

	/**
	 *
	 * @return a shared instance of the controller
	 */
	public static TreeStateController sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new TreeStateController();
		}
		return sharedInstance;
	}

	/**
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
				Tree sourceTree = (Tree)e.getSource();
				saveTreeState(sourceTree);
			}
		});
	}

	/**
	 *
	 * @param tree
	 */
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

	private static final long ABSOLUTE_TIMEOUT = 20 * 1000;
	private static final long RELATIVE_TIMEOUT = 2 * 1000;
	private long currentTime;
	private TimerTask timerTask;

	public void loadTreeState(final Tree tree) {
		final StatableTree statableTree = statableTreeMap.get(tree);

		final IPreferencesService preferencesService = Platform.getPreferencesService();
		final Preferences startNode = preferencesService.getRootNode().node(ConfigurationScope.SCOPE).node(statableTree.getID());

		final Timer timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (currentTime < ABSOLUTE_TIMEOUT) {
					if (tree != null && !tree.isDisposed()) {
						tree.getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								for (TreeItem treeItem : statableTree.getTree().getItems()) {
									try {
										if (startNode.getInt(treeItem.getText(), 0) == 1) {
											Event e = new Event();
											e.type = SWT.Expand;
											e.item = treeItem;
											e.widget = tree;
											Method m = Widget.class.getDeclaredMethod("sendEvent", new Class[] {int.class, Event.class});
											m.setAccessible(true);
											m.invoke(tree, e.type, e);

											treeItem.setExpanded(true);
											loadSubTreeState(treeItem, startNode);
										}
									} catch (Exception e) {
										throw new RuntimeException(e);
									}
								}

								currentTime += RELATIVE_TIMEOUT;
								loadTreeState(tree);
							}
						});
					}
				}
				else
					timer.cancel();
			}
		};

		timer.schedule(timerTask, RELATIVE_TIMEOUT);
	}

	private void loadSubTreeState(TreeItem parentItem, Preferences parentNode) {
		for (TreeItem subTreeItem : parentItem.getItems()) {
			Preferences subNode = parentNode.node(subTreeItem.getText());
			boolean isExpanded = subNode.getInt(subTreeItem.getText(), 0) == 1?true:false;
			if (isExpanded) {
				Event event = new Event();
				event.type = SWT.Expand;
				event.item = subTreeItem;
				event.widget = subTreeItem.getParent();
				Method method;
				try {
					method = Widget.class.getDeclaredMethod("sendEvent", new Class[] {int.class, Event.class});

					method.setAccessible(true);
					method.invoke(subTreeItem.getParent(), event.type, event);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}

				subTreeItem.setExpanded(true);
				loadSubTreeState(subTreeItem, subNode);
			}
		}
	}
}