package org.bio5.irods.imagej.listeners;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class MyTreeModelListener implements TreeModelListener {

	 public void treeNodesChanged(TreeModelEvent e) {/*
         DefaultMutableTreeNode node;
         node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());
             int index = e.getChildIndices()[0];
             node = (DefaultMutableTreeNode)(node.getChildAt(index));
             JOptionPane.showMessageDialog(null, "treeNodesChanged !");
         System.out.println("The user has finished editing the node.");
         System.out.println("New value: " + node.getUserObject());
     */}

	public void treeNodesInserted(TreeModelEvent e) {
		// TODO Auto-generated method stub
		//JOptionPane.showMessageDialog(null, "Added !");
	}

	public void treeNodesRemoved(TreeModelEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void treeStructureChanged(TreeModelEvent e) {
		// TODO Auto-generated method stub

		//JOptionPane.showMessageDialog(null, " treeStructureChanged!");
	}

}
