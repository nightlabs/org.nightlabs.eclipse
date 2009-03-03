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

	public void loadTreeState(Tree tree) {
		StatableTree statableTree = statableTreeMap.get(tree);

		IPreferencesService preferencesService = Platform.getPreferencesService();
		IEclipsePreferences rootNode = preferencesService.getRootNode();
		for (TreeItem treeItem : statableTree.getTree().getItems()) {
			try {
				if (rootNode.nodeExists(treeItem.getText())) {
					Preferences node = rootNode.node(treeItem.getText());
					boolean isExpanded = node.getInt(treeItem.getText(), 0) == 1?true:false;
					treeItem.setExpanded(isExpanded);

					loadSubTreeState(treeItem, node);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void loadSubTreeState(TreeItem treeItem, Preferences parentNode) {
		for (TreeItem subTreeItem : treeItem.getItems()) {
			Preferences node = parentNode.node(treeItem.getText());
			boolean isExpanded = node.getInt(treeItem.getText(), 0) == 1?true:false;
			subTreeItem.setExpanded(isExpanded);

			loadSubTreeState(subTreeItem, node);
		}
	}
}