package org.bio5.irods.iplugin.views;

import java.io.File;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class FileTreeModel implements TreeModel {

	protected File root;

	public FileTreeModel(File root) {
		this.root = root;
	}

	public Object getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getChild(Object parent, int index) {
		String[] children = ((File) parent).list();
		if ((children == null) || (index >= children.length))
			return null;
		return new File((File) parent, children[index]);
	}

	public int getChildCount(Object parent) {
		String[] children = ((File) parent).list();
		if (children == null)
			return 0;
		return children.length;
	}

	public boolean isLeaf(Object node) {
		return ((File) node).isFile();
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub

	}

	public int getIndexOfChild(Object parent, Object child) {
		String[] children = ((File) parent).list();
		if (children == null)
			return -1;
		String childname = ((File) child).getName();
		for (int i = 0; i < children.length; i++) {
			if (childname.equals(children[i]))
				return i;
		}
		return -1;
	}

	public void addTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub

	}

	public void removeTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub

	}

}
