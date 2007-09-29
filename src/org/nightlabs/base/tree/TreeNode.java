package org.nightlabs.base.tree;

import java.util.List;

import org.eclipse.swt.graphics.Image;

public interface TreeNode <R> {

	public TreeNode getParent();
	
	public boolean hasChildren();
	
	public List<R> getChildren();
	
	public String getColumnText(int columnIndex);
	
	public Image getColumnImage(int columnIndex);
}
