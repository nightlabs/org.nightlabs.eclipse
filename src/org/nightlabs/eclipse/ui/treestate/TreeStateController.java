package org.nightlabs.eclipse.ui.treestate;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
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

		IScopeContext context = new InstanceScope();
		IEclipsePreferences rootNode = context.getNode("org.nightlabs.eclipse.ui.treestate");
		if (rootNode != null) {
			Preferences node = rootNode.node(statableTree.getID());
			for (TreeItem treeItem : tree.getItems()) {
				node.put(treeItem.getText(), treeItem.getExpanded()?"1":"0");

				try {
					node.flush();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				createSubTreeState(treeItem, node);
			}
		}
	}

	private void createSubTreeState(TreeItem treeItem, Preferences parentNode) {
		for (TreeItem subTreeItem : treeItem.getItems()) {
			Preferences node = parentNode.node(parentNode.absolutePath());
			node.put(subTreeItem.getText(), subTreeItem.getExpanded()?"1":"0");

			try {
				node.flush();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			createSubTreeState(subTreeItem, node);
		}
	}

	public void loadTreeState(Tree tree) {
		StatableTree statableTree = statableTreeMap.get(tree);

		IPreferencesService service = Platform.getPreferencesService();
		for (TreeItem treeItem : statableTree.getTree().getItems()) {
			boolean isExpanded = service.getInt(TreeStatePlugin.PLUGIN_ID, treeItem.getText(), 0, null) == 1?true:false;
			treeItem.setExpanded(isExpanded);
		}
	}
}